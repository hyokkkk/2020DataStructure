import java.io.*;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("q") == 0)
					break;

				command(input);
			}
			catch (Exception e)
			{
				System.out.println("ERROR");
			}
		}
	}

	private static void command( String input )
	{
		boolean check = true;

		//크게 총 3단계.
		String result1 = First_is_valid_infix.isValidInfix(input);
		String result2 = Second_convert_to_postfix.convert_to_Postfix(input);
		String result3 = Third_calculate_postfix.calculate_postfix(result2);

		if( result1.equals("0") ) {
			System.out.println("ERROR");
			check = false; }

		// 1단계 통과해서 infix인거 확실하면 처음 input된 문장을 postfix로 바꾸는 작업 수행(2단계)
		// 3단계로 postfix 계산을 해서 오류가 없으면 2단계와 3단계를 같이 print하게 해야 한다.
		else {
			if(result3.equals("ERROR")) {
				System.out.println("ERROR");
				check = false;
			}
			if ( check ) {
				System.out.print(result2+"\n");
				System.out.print(result3+"\n");
			}
		}

	}

}




class First_is_valid_infix {

	//계산에 들어가기 전, 형식만으로도 잡아낼 수 있는 오류는 다 잡아낸다.
	public static String isValidInfix( String input ) {
		String result = isValid_removeBlank(input);

		//공백 삭제하면 안 되는 경우(삭제하고 다음 절차 진행할 시 문제 생김)
		if ( result.equals("ERROR")) {
			return "0";
		}else{
			//공백 삭제해도 되면, 괄호에 관한 오류인지 확인하고 오류 없으면 그 다음 검증한다.
			if( !isValid_first( result ) ) {
				return "0";
			}else{
				if ( !isValid_second( result )) {
					//괄호 갯수는 맞지만 괄호 주변에서 infix 아님이 밝혀지는 경우
					return "0";
				}else{
					//2단계도 통과하면 괄호를 삭제하고 진짜 infix 검증법 돌린다.
					result = result.replace("(", "");
					result = result.replace(")", "");
					if (! isValid_third( result )) { return "0";}
					else return result; }
			}
		}
	}


	public static String isValid_removeBlank( String input ) {
		//공백들을 없애기 전에, '숫자 ( 여러 공백 ) 숫자' <-이 형태를 잡아서 에러메시지 띄워라
		String error = "ERROR";
		Pattern p  = Pattern.compile(".*\\d\\s+\\d.*");
		Matcher m = p.matcher(input);

		if(m.matches()) {
			return error;
		}else{
			//공백삭제
			String result =  input;
			result = result.replaceAll("\\s", "");

			return result; }
	}




	public static boolean isValid_first( String input ) {
		//1. 괄호 '(' 와 ')' 의 갯수 안 맞는 식은 에러처리
		String compare_bracket = input;
		int count = 0;
		for( int i = 0; i < compare_bracket.length(); i++ ) {
			if( compare_bracket.charAt(i) == '(' ) {
				count +=1;
			}else if(compare_bracket.charAt(i) == ')' ) {
				count -= 1; }
		}
		if( count != 0) return false;
		else return true;
	}




	public static boolean isValid_second( String input ) {
		Pattern p;
		Matcher m;
		boolean validity = true;
	//2. valid infix 검사를 위해 식 안의 괄호를 다 삭제할 것임. 괄호 삭제해도 무관한지 확인해야.
		//1) '(' 앞에 부호 없는 경우. ex. a(b) -> 괄호 삭제해버리면 23(45) 가 2345로 돼서 뜯어낼 수 없음.
		p  = Pattern.compile(".*\\d\\(.*");
		m = p.matcher(input);
		if( m.matches() ) { validity = false;}

		//2) ')' 뒤에 바로 숫자가 오는 경우. ex. (b)a -> 위와 동일.
		p  = Pattern.compile(".*\\)\\d.*");
		m = p.matcher(input);
		if( m.matches() ) { validity = false;}

		//3) ')' 앞에 부호가 오는 경우. ex. +b+)c -> 괄호를 없애면
		//		 valid한 식으로 인식될 것임. 따라서 괄호 삭제 전에 오류라고 확인해줘야함.
		p  = Pattern.compile(".*([+]|-|\\^|[*]|/|%)\\).*");
		m = p.matcher(input);
		if( m.matches() ) { validity = false;}

		//4) '(' 뒤에 +- 이외의 부호 오는 경우. ex. 7-(*9+9)
		p  = Pattern.compile(".*\\((\\^|[*]|/|%).*");
		m = p.matcher(input);
		if( m.matches() ) { validity = false;}

		//5) 괄호 관련은 아니지만 식 성립 안 되는 경우. x / 0 , x % 0 일 때.
		p  = Pattern.compile(".*\\d(/|%)0.*");
		m = p.matcher(input);
		if( m.matches() ) { validity = false;}

		if(validity == false) return false;
		else { return true; }
	}





	public static boolean isValid_third( String input ) {
		//3. unary 부호는 없애서 일단 숫자와 binary부호만 남겨놓고 infix 검증 알고리즘 돌린다.
		String noUnary = isValid_removeBlank( input );
		noUnary = noUnary.replaceAll( "-+", "-" );
		noUnary = noUnary.replaceAll( "\\+-", "-" );
		noUnary = noUnary.replaceAll( "--", "+" );
		noUnary = noUnary.replaceAll( "\\++", "+" );
		noUnary = noUnary.replaceAll( "\\*+", "*" );
		noUnary = noUnary.replaceAll( "\\*-", "*" );
		noUnary = noUnary.replaceAll( "\\/+", "/" );
		noUnary = noUnary.replaceAll( "\\/-", "/" );
		noUnary = noUnary.replaceAll( "\\%+", "%" );
		noUnary = noUnary.replaceAll( "\\%-", "%" );
		noUnary = noUnary.replaceAll( "\\^+", "\\^" );

		//^ 뒤에 오는 - unary 없애기 전에, 0 ^ -3 과 같은 경우가 있으면 잡아내서 false 리턴해야 한다.
		// -가 궁극적으로 하나만 남아있을 때만 문제가 생기므로 겹치는 부호들은 위에서처럼 처리를 해 준 상태여야 함.
		if(Pattern.matches(".*0\\^-.*", noUnary)) return false;
		else{
			noUnary = noUnary.replaceAll( "\\^-", "\\^" );

			//맨 앞에 unary면 일단 없앰. infix인지를 확인하려는 거니까.
			if(noUnary.startsWith( "+" ) || noUnary.startsWith( "-" )) {
				noUnary = noUnary.substring( 1 ); }

			//4. <infix> = <identifier> | <infix><operator><infix> 이용해서 infix 인지 확인.
			//여기선 주로 +- 제외한 부호들 연속으로 쓴 거 잡을 듯?
			return isInfix(noUnary);
		}
	}




	public static boolean isInfix(String input) {
		boolean flag = recurInfix(input);
		if (flag) return true;
		else return false;
	}




	//recursion을 통해 infix 형태가 맞는지 확인한다.
	public static boolean recurInfix(String input) {
		String identifier = "\\d+";

		Pattern p = Pattern.compile("^(\\d+?)([+]|-|\\^|/|%|[*]){1}(.*)$"); //input에 (숫자)(부호)(random) 형태가 있냐
		Matcher m = p.matcher(input);

		if( Pattern.matches(identifier, input)) { return true;}
		else if( m.find()) {
			// (숫자)(부호)(random) -> (random)부분만 남겨놓고 계속 재귀 돌린다.
			input = input.replaceAll("^(\\d+?)([+]|-|\\^|/|%|[*]){1}(.*)$", "$3");
			return recurInfix(input);
		}else return false;
   }

}






class Second_convert_to_postfix {
	public static String convert_to_Postfix(String input) {
		String number = "\\d+";
		Stack<Character> operatorStack = new Stack<Character>();
		StringBuilder postfix = new StringBuilder();

		String converting = input_w_oneBlank(input);
		String[] converting_arr = converting.split(" ");


		for (int i = 0; i < converting_arr.length; i ++) {
			String token = converting_arr[i];
			char token_to_char = converting_arr[i].charAt(0);

			//숫자면 postfix stringbuilder로 보낸다.
			if( Pattern.matches(number, token_to_char+"") ) {
				postfix.append( token +" ");
			}
			//부호인 경우이다.
			// ')'가 추출이 되면 stack에서 '('가 pop 될 때까지 그 사이에 있는 것들을 pop해서 postfix에 append한다.
			else if( token.equals(")") ) {
				while(operatorStack.peek() != '(') {
					postfix.append(operatorStack.pop()+" "); }
				// '('도 제거한다.
				operatorStack.pop();

			}else{ // 추출된 것이 )가 아닌 다른 부호인 경우.
					recursion_to_Postfix(operatorStack, token_to_char, postfix); }

		}
		//for문이 끝났는데도 stack이 not empty면, empty될 때까지 다 pop해서 postfix에 append한다.
		if ( !operatorStack.isEmpty() ) {
			while( !operatorStack.isEmpty() ) {
				postfix.append(operatorStack.pop()+ " "); }
		}

		//맨 마지막 공백 삭제하고 string으로 리턴.
		return postfix.substring(0, postfix.length()-1);

	}




	private static void recursion_to_Postfix(Stack<Character> stack, char ch, StringBuilder sb) {
		//stack이 비어있거나, peek이 '('면 무조건 operator stack에 push한다.
		if( stack.isEmpty() || stack.peek() == '('	) {
			stack.push(ch);
		}else {//그렇지 않다면, peek과 우선순위를 비교한다.
			//들어오려는 부호가 우선순위가 높다면 operator stack에 넣는다.
			if(compare_operator(stack.peek(), ch)) {
				stack.push(ch);
			}else { //들어오려는 게 우선순위가 같거나 낮은 경우
					//stack의 꼭대기에 ^가 있는 경우
					if(stack.peek() == '^') {
						//들어오려는 것도 ^인 경우엔 그냥 넣어준다
						if( stack.peek() == ch) { stack.push(ch); }
						else {//들어오려는 게 다른 것인 경우엔 맨 꼭대기가 ^인 동안 계속 출력문으로 보내고
							while( !stack.isEmpty() && stack.peek() == '^') {
								sb.append(stack.pop()+" ");	}
							recursion_to_Postfix(stack, ch, sb); }
						// ~도 ^와 마찬가지 과정
					}else if ( stack.peek() == '~') {
						if( stack.peek() == ch) { stack.push(ch); }
						else {
							while( !stack.isEmpty() && stack.peek() == '~') {
								sb.append(stack.pop()+" ");	}
							recursion_to_Postfix(stack, ch, sb); }
					}else { // ~나 ^가 아닌 부호는 이 과정을 다시 처음부터 돌림.
						sb.append(stack.pop()+" ");
						recursion_to_Postfix(stack, ch, sb); }
			}
		}
	}



	//부호 우선순위를 비교하기 위해 부호에 가중치를 준다.
	private static int rank_operator(char operator) {
		int priority_grade = 0;
		switch (operator){
			case '+': case '-':
				priority_grade = 0;
				break;
			case '*' : case '/' : case '%' :
				priority_grade = 1;
				break;
			case '~' :
				priority_grade = 2;
				break;
			case '^' :
				priority_grade = 3;
				break;
			case '(' :
				priority_grade = 4;
				break;
		}
		return priority_grade;
	}





	//부호 우선순위 비교. 기존에 스택에 있는 부호보다 새로 들어오는 부호가 우선순위가 더 높으면 true 리턴
	private static boolean compare_operator(char inStack, char toStackOrNot ) {
		if( rank_operator( inStack ) < rank_operator( toStackOrNot ) ) {
			return true;
		}else return false;
	}






	public static String input_w_oneBlank(String input) {
		String realOperator = "[+]|[*]|-|/|%|\\^|\\(";
		String operator = "\\D";
		String number = "\\d";

		String postfix = "";
		StringBuilder build = new StringBuilder();

		postfix = input.replaceAll("\\s", "");

		//for문 돌려서 문자 사이에 공백을 집어넣음.
		//문자 사이에 공백 넣기 전에, unary변환부터 한다.
		//unary 1. 식 맨 앞에서 -인 경우. ex. -9
		if( postfix.charAt(0) == '-') {
			build.append("~ ");
		}else if(Pattern.matches(number, postfix.charAt(0)+"")) {
			//맨 앞에 숫자인 경우
			//이 다음이 숫자면 그냥 build에 넣고,
			if ( Pattern.matches(number, postfix.charAt(1)+"")) {
				build.append(postfix.charAt(0));
			}else {//이 다음이 부호면 공백도 같이 넣어준다
				build.append(postfix.charAt(0)+ " ");
			}
		}else {//맨 앞이 괄호인 경우 무조건 공백도 같이 넣어줌
			build.append(postfix.charAt(0)+ " ");
		}

		for ( int i = 1; i < postfix.length()-1 ; i++ ) {

			//만약 부호면,
			if( Pattern.matches(operator, postfix.charAt(i)+"") ) {
				//unary 2. 앞에 부호가 바로 오거나. ex. 5 + - 6
				//unary 3. 앞에 여는 괄호가 바로 오면 unary. ex. (-9 )
				if ( postfix.charAt(i) == '-' && Pattern.matches(realOperator, postfix.charAt(i-1)+"")) {
					build.append("~ ");
				}else{
					build.append(postfix.charAt(i) + " "); }
			}
			//만약 숫자면,
			else {
				//숫자긴 숫잔데, 그 뒤에 부호가 오면 숫자만 넣고 공백 추가.
				if( Pattern.matches(number, postfix.charAt(i)+"") && Pattern.matches(operator, postfix.charAt(i+1)+"")) {
					build.append(postfix.charAt(i) + " ");
				}

				//숫자 뒤에 또 숫자가 오면, 뒤에 공백 넣지 말고 숫자만 추가
				else if (Pattern.matches(number, postfix.charAt(i)+"") && Pattern.matches(number, postfix.charAt(i+1)+"") ) {
					build.append(postfix.charAt(i));
				}
			}
		}
		//맨 마지막 자리는 루프가 안 돈다. 괄호나 숫자일 것.
		build.append(postfix.charAt(postfix.length()-1));

		return build.toString();
	}

}






class Third_calculate_postfix {
	public static String calculate_postfix(String input) {
		//먼저, unary 정리를 해서 array 생성.
		String postfix = input.replace("~ ~ ", "");
		String[] postfix_arr = postfix.split(" ");

		String number = "\\d+";
		Stack<Long> stack = new Stack<Long>();

		for( int i = 0; i < postfix_arr.length ; i ++ ) {
			String token = postfix_arr[i];

			// 숫자인 경우, stack에 그냥 숫자만 넣는다.
			if( Pattern.matches( number, token+"" ) ) {
				long num = Long.parseLong( token );
				stack.push(num);
			}
			// unary가 나오면 stack에 있는 숫자 하나를 꺼내서 *-1 해서 다시 스택에 넣는다
			else if ( Pattern.matches( "~", token+"")) {
				long a = stack.pop();
				a = a * -1 ;
				stack.push(a);
			}


			// unary가 아닌 부호가 나오면 stack에 있는 숫자 두 개 꺼내서 계산하고 계산한 값을 stack에 push 한다.
			else {
				long b = stack.pop();
				long a = stack.pop();
				String operator = token;

				long result = 0;
				if (operator.equals( "+" )) { result = a + b; }
				else if (operator.equals( "-" )) { result = a - b; }
				else if (operator.equals( "*" )) { result = a * b; }
				else if (operator.equals( "/" )) {
					if ( b == 0) { return "ERROR";
					}else { result = a / b; }}
				else if (operator.equals( "%" )) {
					if ( b == 0) { return "ERROR";
					}else { result = a % b; }}
				else if (operator.equals( "^" )) {
					if ( b < 0) { return "ERROR";
					}else { result = (long) Math.pow(a, b); }}
				stack.push(result);
			}
		}
		return stack.pop().toString();
	}
}


