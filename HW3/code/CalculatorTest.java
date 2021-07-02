// 1. 올바른 input인지 check
//      1) wrong input -> print "ERROR"
// 2. correct input -> convert infix to postfix
// 3. calculate postfix


import java.util.regex.*;
import java.util.Stack;
import java.io.*;

public class CalculatorTest{
    public static void main(String args[]){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            try{
                String input = br.readLine();
                if (input.compareTo("q") == 0) break;

                command(input);
            }
            catch (Exception e){
                System.out.println("ERROR");
            }
        }
    }
    private static void command(String input){
        //1. 가시적으로 보이는 formation error를 check.
        First res1 = new First(input);
        if (res1.result.equals("ERROR")) {
            System.out.println(res1.result);
            return ;
        }
        //2. infix -> postfix로 convert.
        Second res2 = new Second(res1);

        //3. postfix를 calculate. x/0, x%0, 0^-y처럼 연산이 성립->postfix를 인쇄하지 않고 ERROR를 인쇄.
        Third res3 = new Third(res2);
        if (res3.error){
           System.out.println("ERROR");
           return ;
        }
        System.out.println(res2.result);
        System.out.println(res3.result);

    }
}


class First{
    String input;
    String result;

    First(String input){
        this.input = input;
        this.result = check_validity(input);
    }

    private boolean operator(char ch){
        return ch =='+'|ch =='-'|ch =='*'|ch =='/'|ch =='%'|ch =='^';
    }

    // formation error check method.
    public String check_validity (String input){

    // 0. 공백 없애기 전에 12 34 와 같은 수인지 확인해야 함.
        if(Pattern.compile("\\d+\\s+\\d+").matcher(input).find()){
            return "ERROR";
        }
    // 1. 공백을 없앤다.
        String noBlank = input.replaceAll("\\s", "");

    // 2. 올바른 수식인지 체크
        // (1) check bracket pair.
        int cnt = 0;
        for (int i = 0; i < noBlank.length(); i++){
            char ch = noBlank.charAt(i);
            if (ch =='('){
                cnt ++;
            }
            else if (ch == ')'){
                cnt --;
            }
        }
        if (cnt != 0) {
            return "ERROR";
        }
        // (2) "()", "(부호(*/%^)", "모든부호)", "숫자(", ")숫자", "x/0", "x%0" 형태가 존재하면 error
        Matcher m1, m2, m3, m4, m5, m6;
        m1 = Pattern.compile("\\(\\)").matcher(noBlank);
        m2 = Pattern.compile("\\((\\*|/|^|%)").matcher(noBlank);
        m3 = Pattern.compile("(\\+|-|/|\\*|^|%)\\)").matcher(noBlank);
        m4 = Pattern.compile("\\d\\(").matcher(noBlank);
        m5 = Pattern.compile("\\)(\\d)").matcher(noBlank);
        m6 = Pattern.compile("(/|%)0").matcher(noBlank);

        if (m1.find() | m2.find() | m3.find() | m4.find() | m5.find() | m6.find()){
            return "ERROR";
        }

    // 3. 연산이 성립하지 않는 경우 (not infix)
        // (1) infix 판별하기 위해 임시로 괄호와 unary를 다 없앤다. (postfix로 변환할 때 ~unary는 살려야 함).
        String noUnary = noBlank;
        noUnary = noUnary.replaceAll("\\(", "");
        noUnary = noUnary.replaceAll("\\)", "");

        String temp = "" ;
        while(!noUnary.equals(temp)){
            // 수많은 unary가 존재하는 경우에 단 하나의 unary만 남도록 하기 위함.
            // + 는 무조건 bin으로 본다.
            // 수정되는 것이 없을 때까지 돌려라.
            temp = noUnary;
            noUnary = noUnary.replaceAll("-\\+", "-");
            noUnary = noUnary.replaceAll("\\+-", "-");
            noUnary = noUnary.replaceAll("--", "");
        }
        // 0^-y 형태 error find.
        if (Pattern.compile("0\\^-\\d").matcher(noUnary).find()){
            return "ERROR";
        }
        noUnary = noUnary.replaceAll("\\*-", "*");
        noUnary = noUnary.replaceAll("/-", "/");
        noUnary = noUnary.replaceAll("%-", "%");

        // (2) check infix
        // 간단한 check를 위해 숫자들을 전부 1로 바꿈 & 식이 +, -로 시작하면 부호 삭제
        String temp1= noUnary.replaceAll("\\d+", "1");
        temp1 = temp1.startsWith("+") | temp1.startsWith("-") ? temp1.substring(1) : temp1;
        return isInfix(temp1) ? noBlank : "ERROR";
    }

    private boolean isInfix(String input){
        // <infix><operator><infix> || <identifier>
        if (input.equals("")) return false;
        else if (input.charAt(0) != '1') return false;
        else if (input.equals("1")) return true;
        else if (input.charAt(0)=='1' & operator(input.charAt(1))) return isInfix(input.substring(2));
        return false;
    }
}



class Second{
    // identifier(숫자)로 시작하는지 판단하기 위함.
    public final Pattern IDEN = Pattern.compile("^(\\d+)(.*)");
    public Matcher m;
    private StringBuilder postfix;
    private Stack<Character> stk_op;
    String result;

    Second(First input){
        result = convert_to_postfix(input.result);
    }

    // - unary를 ~로 바꾼다.
    private String unary_handling(String input){
        char ch = input.charAt(0);
        String temp = "";

        while(!input.equals(temp)){
            temp = input;
            input = input.replaceAll("\\+-", "+~");
            input = input.replaceAll("--", "-~");
            input = input.replaceAll("~-", "~~");
            input = input.replaceAll("\\(-", "(~");
            input = input.replaceAll("\\*-", "*~");
            input = input.replaceAll("/-", "/~");
            input = input.replaceAll("%-", "%~");
        }
        if (ch == '+'){
            input = input.substring(1);
        }else if (ch == '-'){
            input = new StringBuilder().append("~").append(input.substring(1)).toString();
        }
        return input;
    }

    // infix -> postfix. 입력이 들어올 때마다 postfix와 stack을 reset해야 해서 recur를 여기서 호출.
    public String convert_to_postfix(String input){
        postfix = new StringBuilder();
        stk_op = new Stack<>();
        return postfix_recur(unary_handling(input)).toString().trim();
    }

    // postfix 만드는 재귀함수.
    private StringBuilder postfix_recur(String input){
        m = IDEN.matcher(input);
        char op, popped_op;

        // 1. 탈출조건.
        if (input.equals("")) {
            while(!stk_op.isEmpty()){
                postfix.append(stk_op.pop()).append(" ");
            }
            return postfix;
        }
        // 2. input의 시작이 숫자인 경우. 숫자를 postfix에 저장하고 input에서 없앰.
        if (m.matches()) {
            String identifier = m.group(1);
            input = m.group(2);
            postfix.append(identifier).append(" ");
        }
        // 3. input의 시작이 op인 경우.
        else {
            op = input.charAt(0);
            input = input.substring(1);

            // 1) )면 짝이 맞는 (가 나올 때까지 다 pop해서 postfix에 저장.
            if (op == ')') {
                while('(' != (popped_op = stk_op.pop())){
                    postfix.append(popped_op).append(" ");
                }
            // 2) (, ^, ~ 아닌 다른 부호: rank 비교해서 pop 여부 결정.
            }else if(!(op == '(' | op == '^' | op == '~') & !stk_op.isEmpty()){
                while (operator_rank(stk_op.peek()) >= operator_rank(op)){
                    postfix.append(stk_op.pop()).append(" ");
                    if (stk_op.isEmpty()) break;
                }
                stk_op.push(op);
            // 3) ( 는 pop을 언제까지 할 지 알려줌.
            //    ^, ~는 right associative라 본인이 본인을 pop시킬 수 없음.
            //    stk empty에는 무조건 push
            }else {
                stk_op.push(op);
            }
        }
        return postfix_recur(input);
    }

    private char operator_rank(char ch) {
        switch(ch){
            // (는 무조건 push, )는 무조건 pop이라서 rank 따질 필요가 없다.
            // ( 는 rank 비교 시, 무조건 pop되지 않고 저지를 해야 하므로 rank 점수 낮게 줌.
            case '^':
                return 4;
            case '~':
                return 3;
            case '*': case '/': case'%':
                return 2;
            case '+': case '-':
                return 1;
            default : return 0;
        }

    }

}



class Third{
    long result;
    boolean error = false;
    Stack<Long> num_stk = new Stack<>();

    Third(Second input){
        this.result = calculate(input.result);
    }

    private long calculate(String input){
        String[] post = input.split("\\s");
        String operators = "\\+|-|\\*|/|%|~|\\^";
        String element;

        for (int i = 0; i < post.length; i++){
            element = post[i];
            // 숫자면 stack에 넣는다.
            if (!Pattern.compile(operators).matcher(element).matches()){
                num_stk.push(Long.parseLong(element));
            // 부호 : ~는 하나, 나머지는 두 개 pop 후, 계산해서 push. x/0, x%0, 0^-y는 error처리.
            }else {
                long cal = 0;
                long num1 = num_stk.pop();
                switch(element){
                    case "~":
                        cal = num1 * -1; break;
                    case "+":
                        cal = num_stk.pop() + num1; break;
                    case "-":
                        cal = num_stk.pop() - num1; break;
                    case "*":
                        cal = num_stk.pop() * num1; break;
                    case "/":
                        if (num1 == 0){
                            error = true;
                            break;
                        }
                        cal = num_stk.pop() / num1;
                        break;
                    case "%":
                        if (num1 == 0){
                            error = true;
                            break;
                        }
                        cal = num_stk.pop() % num1;
                        break;
                    case "^":
                        if (num1 < 0){
                            error = true;
                            break;
                        }
                        cal = (long)Math.pow(num_stk.pop(), num1); break;
                }
                num_stk.push(cal);
            }
        }
        if (error) return -1;
        return num_stk.pop();
    }
}

