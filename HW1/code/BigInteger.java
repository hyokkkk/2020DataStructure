import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger
{
                public static final String QUIT_COMMAND = "quit";
                public static final String MSG_INVALID_INPUT = "WRONG INPUT";

                public static final Pattern EXPRESSION_PATTERN = Pattern.compile("^(\\D?)(\\d*)(\\+|-|\\*)(\\D?)(\\d*)$");

                //to use operators when decide which method should we use
                static String first_operator;
                static String last_operator;
                static String real_operator;

                int len;
                int[] arr;
                String operator;


                public BigInteger() {
                }

		public BigInteger(int[] arr) {
			this.arr = arr;
			len = arr.length;
			operator = "";
		}

		public BigInteger(String operator, int[] arr) {
			this.arr = arr;
			len = arr.length;
			this.operator = operator;
		}


		@Override
	    public String toString() {
	    	String result = "";
	    	boolean flag =  false;

	    	//to remove useless 0 in the in front of the array. ex) 0000123->123
			for(int i=0; i<len; i++) {
				if(arr[i] != 0) {
					flag = true;
					if(flag) {
						for(int j = i; j<len; j++) {
							result += arr[j];
						}
						break;
					}
				}
			}

			//flag turns 'true' when it comes to meet 'not 0 number'.
			//if 'flag' is still false, it means there is no 'not 0 number' and so the answer is 0
			if(!flag) {
				result = "0";
				return result;
			}
	    	return operator+result;
		}



	    public BigInteger add(BigInteger big){
			final int COUT = 1;
			int lenR = len+1; // the length of result should be one more longer than the length of the bigger number
			int[] add_result = new int [lenR];
			String minus_oper = "";

			//start adding
			for(int i = 1; i <= len; i++) {
				add_result[lenR-i] = arr[len-i] + big.arr[len-i];

				//Carry-out
				if(add_result[lenR-i] >=10) {
					add_result[lenR-i] -=10;
					if(len-i-1 >= 0) arr[len-i-1] += COUT;
					else add_result[0]=1;
				}
			}
			// the case that we need - operator, like -A -B. logical expression is got by drawing a truth table.
			if(((real_operator.equals("+")) ^ !(last_operator.equals("-"))) &(first_operator.equals("-"))) {
				minus_oper = "-";
			}

			//pass just "" when we do not need any operators.
			return new BigInteger(minus_oper, add_result);
	    }



	    public BigInteger subtract(Boolean exchange, BigInteger big){
	    	int lenR = len; //length of a result is enough with same length of the longer one
	    	int[] sub_result = new int[lenR];
			String minus_oper = "";

			//subtract from the back
			for(int i=0; i < len; i++) {
				if(arr[len-1-i] >= big.arr[len-1-i]) {
					sub_result[len-1-i] = arr[len-1-i] - big.arr[len-1-i];
				}

				//bada naerim
				else {sub_result[len-1-i] = arr[len-1-i] +10 - big.arr[len-1-i];
				arr[len-2-i] -=1;
				}
			}


			//the cases that we need '-' operator.
			if(exchange == true) {
				if(!(first_operator.equals("-")) & ((real_operator.equals("-")) ^ last_operator.equals("-"))) {
					minus_oper = "-";
				}
			}

			else {
				if((first_operator.equals("-")) & (!((real_operator.equals("-")) ^ last_operator.equals("-")))){
					minus_oper = "-";
				}
			}

			return new BigInteger(minus_oper, sub_result);
	    }



	    public BigInteger multiply(BigInteger big){
	    	int len1 = len;
			int len2 = big.len;
			int lenR = len1 + len2; //result length
			int[] mult_result = new int[lenR];
			String minus_oper = "";

			//instead of using some temporary array, directly add result numbers to the result array.
			for(int i=0; i < len2; i++) {
				for(int j=0; j< len1; j++) {
					mult_result[lenR -1 -(i+j)] += (big.arr[len2 -1 -i] * arr[len1 -1 -j]) % 10;
					mult_result[lenR -2 -(i+j)] += (big.arr[len2 -1 -i] * arr[len1 -1 -j]) / 10;

					if (lenR -2 -(i+j) >= 0) {
						if(mult_result[lenR -1 -(i+j)] >= 10) {
							mult_result[lenR -2 -(i+j)] += mult_result[lenR-1-(i+j)]/10;
							mult_result[lenR-1-(i+j)] = mult_result[lenR-1-(i+j)] %10;
						}
						if(mult_result[lenR -2 -(i+j)] >= 10) {
							mult_result[lenR -3 -(i+j)] += mult_result[lenR-2-(i+j)]/10;
							mult_result[lenR-2-(i+j)] = mult_result[lenR-2-(i+j)] %10;
						}
					}
					else{break;}
				}
			}

				//the cases that we need '-' operator
				if(real_operator.equals("*") & (first_operator.equals("-") ^ last_operator.equals("-"))) {
					minus_oper = "-";
				}
				return new BigInteger(minus_oper, mult_result);
	    	}



	    static BigInteger evaluate(String input) throws IllegalArgumentException{
	       String numbers = input;
	       numbers = numbers.replaceAll("\\p{Z}", ""); //erase all the blank spaces
	       Matcher m = EXPRESSION_PATTERN.matcher(numbers);

	       BigInteger result = new BigInteger();


	       if(m.find()) {
				//extract operators from the given input for more useful using
				first_operator = m.group(1);
				real_operator = m.group(3);
				last_operator = m.group(4);

				//delete all operators except for the middle(real) operator, which is the criteria of split working
				numbers = numbers.replaceAll("^(\\D?)(\\d*)(\\+|\\-|\\*)(\\D?)(\\d*)$", "$2$3$5");
				String[] numbers_split = numbers.split("\\"+real_operator);


				//go to add() when the operators are these combinations.
				if ((!(first_operator.equals("-")) ^ (real_operator.equals("+")) ^ !(last_operator.equals("-"))) & !(real_operator.equals("*"))) {

					//for more convenient calculation, lengthen the shorter array's length.
					// ex. 123 + 4567 -> 123 becomes 0123
					int len = Math.max(numbers_split[0].length(), numbers_split[1].length());
					int[] arr_num1 = new int[len];
					int[] arr_num2 = new int[len];

					//put the first number's digits one by one to the first array.
					for(int i = 0; i< numbers_split[0].length(); i++) {
						arr_num1[len - numbers_split[0].length() + i] = numbers_split[0].charAt(i) - '0';
					}

					//put the second number's digits to the second array.
					for(int i = 0; i< numbers_split[1].length(); i++) {
						arr_num2[len - numbers_split[1].length() + i] = numbers_split[1].charAt(i) - '0';
					}

					BigInteger bignum1 = new BigInteger(arr_num1);
					BigInteger bignum2 = new BigInteger(arr_num2);
					BigInteger addresult = bignum1.add(bignum2);
//
					result = addresult;
				}


				//go to subtract method when the combinations are these.
				else if (!(!(first_operator.equals("-"))  ^ (real_operator.equals("+")) ^ !(last_operator.equals("-"))) & !(real_operator.equals("*"))) {

					//the length of the result array should be long as the longer number's array
					int len = Math.max(numbers_split[0].length(), numbers_split[1].length());
					int[] arr_num1 = new int[len];
					int[] arr_num2 = new int[len];

					//put the first number's digits one by one to the first array.
					for(int i = 0; i< numbers_split[0].length(); i++) {
						arr_num1[len - numbers_split[0].length() + i] = numbers_split[0].charAt(i) - '0';
					}

					//same working for the second number.
					for(int i = 0; i< numbers_split[1].length(); i++) {
						arr_num2[len - numbers_split[1].length() + i] = numbers_split[1].charAt(i) - '0';
					}

					BigInteger bignum1 = new BigInteger(arr_num1);
					BigInteger bignum2 = new BigInteger(arr_num2);

					Boolean exchange = false;

					//we should subtract smaller absolute value number from the bigger absolute value one.
					//compare the digits one by one from index 0 to decide which one is bigger.
					for (int i = 0; i < len; i++) {
						if(arr_num1[i] > arr_num2[i]) {
							break;
						}

						//if the latter one is bigger, swap the num1 and num2 so that we can get num1 for the bigger one.
						else if (arr_num1[i] < arr_num2[i]) {
							BigInteger temp = bignum1;
							bignum1 = bignum2;
							bignum2 = temp;
							exchange = true;
							break;
						}
					}
					BigInteger subresult = bignum1.subtract(exchange, bignum2);
					result = subresult;
				}


				//combinations of operators for multiply
				else if (real_operator.equals("*")) {

					//dont need to change the length of each original numbers.
					int[] arr_num1 = new int[numbers_split[0].length()];
					int[] arr_num2 = new int[numbers_split[1].length()];

					//put the first number's digits one by one to the first array.
					for(int i=0; i<arr_num1.length; i++) {
						arr_num1[i] = numbers_split[0].charAt(i)-'0';
					}

					//same working for the second number
					for(int i=0; i<arr_num2.length; i++) {
						arr_num2[i] = numbers_split[1].charAt(i)-'0';
					}

					BigInteger bignum1 = new BigInteger(arr_num1);
					BigInteger bignum2 = new BigInteger(arr_num2);
					BigInteger multresult = bignum1.multiply(bignum2);

					result = multresult;
				}
	       }
	       return result;
	    }



    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();

                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }

    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);

        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());

            return false;
        }
    }

    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}

