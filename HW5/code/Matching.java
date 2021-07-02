import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Matching
{
	public static void main ( String args[] ) {
		BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );

		while ( true ) {
			try {
				String input = br.readLine(); // 입력을 받는다.
				if ( input.compareTo( "QUIT" ) == 0 )
					break;
				if ( input == null )
					break;

				// 명령어에 따라 다른 함수로 보낸다.
				// 입력 규칙 : 명령문자 -> 공백 -> 내용. 내용만 남겨놓게 만들어서 parameter로 전달
				// substring(x) --> x전까지 자른다.
				String param = input.substring( 2 );
				switch ( input.charAt( 0 ) ) {
				case '<' :
					getFile( param );
					break;
				case '@' :
					printKeys( param );
					break;
				case '?' :
					matching( param );
					break;
				}
			} catch ( IOException e ) {
				System.out.println( "입력이 잘못되었습니다. 오류 : " + e.toString() );
			}
		}
	}
	/*---------------------------------------------------------------------------------------------------------------*/

	static Hashtable<Pair<Integer, Integer>> hashtable;
	final static int len_of_substring = 6;

	// 여기서는 substring들 hashtable에 다 담는다.
	public static void getFile ( String filepath ) throws IOException {
		// 파일 경로 받아와서 읽는다. 라인별로 원소에 저장. 즉, 파일 하나가 배열 하나임.
		List<String> lines = Files.readAllLines( Paths.get( filepath ));

		hashtable = new Hashtable<>();


		// 총 줄 수를 센다.
		int line_count = 0;

		for ( String string : lines )
		{
			line_count ++ ;
			for ( int j = 0 ; j <= string.length() - len_of_substring ; ++ j )
			{ 	// 문장을 길이 6으로 자른다. 이것이 key.
				String key = string.substring( j, j + len_of_substring );

				// 몇 번쨔 줄의 몇 번째 문자인지가 value.
				Pair<Integer, Integer> value = new Pair<Integer, Integer>( line_count, j + 1 );
				hashtable.insert( key, value );
			}
		}
	}

	public static void printKeys ( String hashcode ) { // 일단 String으로 받아놓고 parstInt는 여기서 하자.
		int hash = Integer.parseInt( hashcode );
		AVLtree<String, Pair<Integer, Integer>> tree = hashtable.slot[ hash ];
		System.out.println( tree.root.getKey() == null ? "EMPTY" : tree.root );
	}



	/*
	 1. first substring ( k = 6 ) 의 value를 (n, m)라 하자 --> 걍 linkedlist에 복사
	 2. 첫번째 sub 이후 i번째 substring을 구하고, 그것이 가지고 있는 valuelist도 구한다.
	    i번째 substring이라 할 때, 그 valuelist 안에 (n, m + i -1)가 존재하면,  first substring의 value(n, m)를 살려두고, 없으면 null로 바꾼다.
	    ex. pattern의 길이가 7이라면, 1~6번째 문자로 이루어진 sub의 시작점이 (n,m)이고, 2~7번째 문자sub의 시작점은 (n, m+1)일 것이다.
	    즉, pattern으로 만들 수 있는 substring의 시작점이 연속적인지를 확인하는 것이다.
	 3. 모든 for문 다 돌았을 때 null이 아닌 경우에만 인쇄를 한다.
	 */

	public static void matching ( String pattern ) {
		try {
			// key를 길이 6인 substrings로 만든다. 일단, 기준이 되는 첫 번째 substring은 따로 구함.
			String first_sub = pattern.substring( 0, len_of_substring );
			LinkedList<Pair<Integer, Integer>> temp = findValue( first_sub );


			// 이제 나머지 substring 구한다.
			// 나머지 substring의 valuelist가 하나하나 나올 때마다 아래의 작업을 반복한다.

			for ( int i = 1 ; i <= pattern.length() - len_of_substring ; ++ i ) {

				String key = pattern.substring( i, i + len_of_substring ); // pattern의 i+1번째 substring을 구함
				LinkedList<Pair<Integer, Integer>> valuelist = findValue( key ); //그 substring이 가지고 있는 valuelist를 받음

				// (첫 번째 sub의 value 리스트)temp에서 원소 firstsub을 하나씩 꺼낸다
				// 지금 얻은 valuelist의 원소에 자신 다음으로 올 원소가 있는지 확인함

				for( Pair<Integer, Integer> firstsub : temp ) {
					boolean thereis = false;

					for ( Pair<Integer, Integer > othersub : valuelist) {

						//othersub의 j가 firstsub의 j보다 i 크면 firstsub 놔두고, 그런게 없으면 삭제
						if ( firstsub.getJ() == null || firstsub.getN() == null ) continue;

						if ( firstsub.getN().equals(othersub.getN())
							&& ( Integer.valueOf(firstsub.getJ() + i).equals(othersub.getJ()) ) )
							{ thereis = true; break; }
					}
					if( !thereis ) { firstsub.setNull(); }
				}
			}

			//마지막까지 temp에 남아있는 원소는 pattern의 시작점이다. 그걸 인쇄하면 된다.
			String pair = "";
			for ( Pair<Integer, Integer> p : temp ) { if( p.getN() != null && p.getJ() != null ) pair += p.toString() + ' '; }
			pair = pair.trim();
			System.out.println( pair.equals( "" ) ? "(0, 0)" : pair );

		} catch ( Exception e ) {
			System.out.println( "(0, 0)" ); //hashcode에 해당하는 tree가 null인 경우
		}
	}

	public static LinkedList<Pair<Integer, Integer>> findValue( String key ){
		int hash = hashtable.getHashCode( key ); //해쉬코드 받는다
		AVLtree<String, Pair<Integer, Integer>> tree = hashtable.slot[ hash ]; // 그 슬롯에 간다.
		AVLtree<String, Pair<Integer, Integer>>.Node node= tree.search4matching ( key, tree.root ); // key갖고 있는 노드 찾는다.
		return node.headToValue;
	}
}



