import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

    // (부호 0~1개)(숫자)(+/-/*)(부호 0~1개)(숫자)
    // group(0): whole, 그 다음부터 group(1), group(2)...로 정해짐.
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("^(\\D?)(\\d*)(\\+|-|\\*)(\\D?)(\\d*)$");

    static String bin_oper;
    static String una_oper1;
    static String una_oper2;
    static boolean negative = false;

    // instance vars
    private byte[] data;
    private int arrLen;
    private String str;

    // add/sum/mul로 보낼 때 사용
    public BigInteger(String s){
        arrLen = s.length();
        data = new byte[arrLen];
        str = s;
        //init the byte array. reversed digit.
        for (int i = 0; i < arrLen; i++){
            data[arrLen-1-i] = (byte)(str.charAt(i)-'0');
        }
    }

    // result 담을 때 사용
    public BigInteger(int arrLen){
        this.arrLen = arrLen;
        data = new byte[arrLen];
        str = "";
        // init to 0
        for (int i = 0; i < arrLen; i++){
            data[i] = 0;
        }
    }



    // for less branch instruction, no Ternery operations.
    public BigInteger add(BigInteger num2){

        int len1 = str.length();
        int len2 = num2.str.length();
        BigInteger big, small;
        BigInteger result = new BigInteger(Math.max(len1, len2) + 1);
        negative = una_oper1.equals("-") ? true : false;    // 애초에 una_oper1 == bin_oper 인 경우만 add로 들어온다.

        if (len1 > len2)    { big = this; small = num2; }
        else                { big = num2; small = this; }

        boolean cout = false; // carry out

        // small과 겹치는 부분
        for (int i = 0; i < small.arrLen; i++){
            big.data[i] = cout ? (byte)(big.data[i]+1) : big.data[i];
            cout = false;

            if (big.data[i] + small.data[i] > 9){
                cout = true;
                result.data[i] = (byte)(big.data[i] + small.data[i] -10);
            } else{
                result.data[i] = (byte)(big.data[i] + small.data[i]);
            }
        }
        // 나머지 big part
        for (int i = small.arrLen; i < big.arrLen; i++){
            big.data[i] = cout ? (byte)(big.data[i]+1) : big.data[i];
            cout = false;

            if (big.data[i] > 9) {
                cout = true;
                result.data[i] = (byte)(big.data[i] - 10);
            } else{
                result.data[i] = big.data[i];
            }
        }
        result.data[big.arrLen] = cout ? (byte)1 : 0;       // carry out to msb

        return result;
    }



    // num1 - num2
    public BigInteger subtract(BigInteger num2){

        int len1 = str.length();
        int len2 = num2.str.length();
        BigInteger big, small;
        BigInteger result = new BigInteger(Math.max(len1, len2));

        // |num1| > |num2|
        if ((len1 > len2) || (len1 == len2 && str.compareTo(num2.str) > 0)){
            negative = una_oper1.equals("-") ? true : false;
            big = this;
            small = num2;
        } else if(str.compareTo(num2.str) == 0){ // num1 == num2
            negative = false;
            return result;
        }
        else { // |num1| < |num2|
            negative = bin_oper.equals("-") ? true : false;
            big = num2;
            small = this;
        }

        boolean cin = false; //carry in

        // small num과 겹치는 자릿수 계산
        for (int i = 0; i < small.arrLen; i++){
            big.data[i] = cin ? (byte)(big.data[i]-1) : big.data[i];
            cin = false;

            if (big.data[i] < small.data[i]){
                cin = true;
                result.data[i] = (byte)(big.data[i] + 10 - small.data[i]);
            } else{
                result.data[i] = (byte)(big.data[i] - small.data[i]);
            }
        }
        // 남은 big num 부분
        if (big.arrLen > small.arrLen){
            for (int i = small.arrLen; i < big.arrLen; i++){
                big.data[i] = cin ? (byte)(big.data[i]-1) : big.data[i];
                cin = false;

                if (big.data[i] < 0) {
                    cin = true;
                    result.data[i] = (byte)(big.data[i] + 10);
                } else{
                    result.data[i] = big.data[i];
                }
            }
        }
        return result;
    }



    public BigInteger multiply(BigInteger num2){

        BigInteger result = new BigInteger(str.length() + num2.str.length());
        negative = false;

        if (str.equals("0") || num2.str.equals("0")) return result;
        negative = una_oper1.equals(una_oper2) ? false : true;

        // considering locality, sequential memory access should be avoided.
        int carry = 0;
        for (int ridx = 0; ridx < result.arrLen-1; ridx++){
            int sum = carry;
            for (int idx = 0; idx < arrLen; idx++){
                if (ridx-idx >= 0 && ridx-idx < num2.arrLen){
                    sum += data[idx] * num2.data[ridx-idx];
                }
            }
            carry = sum / 10;
            result.data[ridx] = (byte)(sum % 10);
        }
        result.data[result.arrLen-1] = (byte)carry;

        return result;
    }



    @Override
    public String toString(){
        StringBuilder result = new StringBuilder("");

        for (int i = arrLen -1; i >=0; i--){
            result.append(data[i]);
        }

        // 0없애기
        while(result.charAt(0)=='0' && arrLen != 1) {
            result.deleteCharAt(0);
            arrLen --;
        }

        return negative ? "-" + result.toString() : result.toString();
    }



    static BigInteger evaluate(String input) throws IllegalArgumentException{
        //1. remove all blanks.
        input = input.replace(" ", "");

        //2. parsing.
        Matcher m = EXPRESSION_PATTERN.matcher(input);
        if(!m.matches()){//find는 일치하는 게 포함되면 T, matches()는 입력 전체가 일치해야 T.
           new IllegalArgumentException();
        }

        //3. extract unary, binary operators.
        bin_oper = m.group(3);
        una_oper1 = m.group(1).equals("") ? "+" : m.group(1); //unary default = "+"
        una_oper2 = m.group(4).equals("") ? "+" : m.group(4);

        //4. make operands instances.
        BigInteger num1 = new BigInteger(m.group(2));
        BigInteger num2 = new BigInteger(m.group(5));

        //5. binary oper == *, go to mul.
        if (bin_oper.equals("*")) return num1.multiply(num2);

        //6. add/sub operator handle
        //   By comparing binary and unary2, convert unary2 to "+", in order to simplify later calculation.
        if (una_oper2.equals("-")){
            una_oper2 = "+";
            bin_oper = bin_oper.equals("+") ? "-" : "+";
        }

        // if right input is not guaranteed, should do exception handling.
        return (una_oper1.equals(bin_oper)) ? num1.add(num2) : num1.subtract(num2);
    }



    //Entry point
    public static void main(String[] args) throws Exception{
        try (InputStreamReader isr = new InputStreamReader(System.in)){
            try (BufferedReader reader = new BufferedReader(isr)){
                boolean done = false;
                while (!done){
                    String input = reader.readLine();
                    try{
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e){
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }

    static boolean processInput(String input) throws IllegalArgumentException{
        boolean quit = isQuitCmd(input);
        if (quit){
            return true;
        }
        else{
            BigInteger result = evaluate(input);
            System.out.println(result.toString());
            return false;
        }
    }

    static boolean isQuitCmd(String input){
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
