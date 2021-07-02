import java.io.*;
import java.util.*;

public class SortingTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r')
			{
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			else
			{
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true)
			{
				int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

				String command = br.readLine();

				long t = System.currentTimeMillis();
				switch (command.charAt(0))
				{
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom)
				{
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				}
				else
				{
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
					for (int i = 0; i < newvalue.length; i++)
					{
						System.out.println(newvalue[i]);
					}
				}

			}
		}
		catch (IOException e)
		{
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}

	///////////////////////////////////////////////////
	private static void swap(int[] value, int index1, int index2) {
		int temp = value[ index1 ];
		value[ index1 ] = value[ index2 ];
		value[ index2 ] = temp;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value)
	{
		for ( int i = 0 ; i < value.length-1 ; i ++ ) {
			for ( int j = 0 ; j < value.length -1 -i ; j ++) {
				if ( value[ j ] > value[ j+1 ]) swap(value, j, j+1 );
			}
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value)
	{
		for ( int i = 0 ; i < value.length -1 ; i ++) {
			for ( int j = i + 1 ; j > 0 ; j --) {
				if ( value[ j ] < value[ j - 1 ] ) swap( value, j-1, j );
			}
		}
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value)
	{
		if( value.length != 0 ) {
			//1. building heap
			for ( int i = getParent( value.length -1 ); i >=0 ; -- i ) { percolate_down( value, i, value.length -1 ); }

			//2. sorting
			for ( int lastIndex = value.length -1; lastIndex > 0 ; -- lastIndex ) {
				swap( value, 0, lastIndex );
				percolate_down( value, 0, lastIndex -1 );
			}
		}
		return (value);
	}

	private static void percolate_down( int[] value, int parent, int lastIndex )
	{
		if( parent == lastIndex ) return ; //sort마지막에 root하나만 남은 경우엔 percolate할 필요 없음.

		int child = compareChild( value, parent, lastIndex ); //child중 큰 값 인덱스 찾기
		if( value[parent] < value[child] ) {
			swap( value, parent, child );

			// 현재 child의 leftchild가 배열 안에 존재할 때에만 재귀 돈다. rightchild로 기준을 잡으면, right은 범위 밖이지만 left는 범위 안인데도 배제될 수가 있음.
			if ( getLeftchild( child ) <= lastIndex ) percolate_down( value, child, lastIndex );
		}
	}

	private static int getParent( int child ) { return (child +1)/2 -1; }
	private static int getLeftchild( int parent ) { return 2 * parent + 1; }
	private static int getRightchild( int parent ) { return 2 * parent + 2; }

	private static int compareChild( int[] value, int parent, int lastIndex )
	{
		int leftChild = getLeftchild( parent ),
			rightChild = getRightchild( parent );

		//범위 내에 leftchild만 있는 경우엔 leftchild 리턴
		if ( leftChild == lastIndex ) return leftChild;
		else { //둘 다 있으면 큰거 리턴
			if( value[leftChild] > value[rightChild] ) return leftChild;
			else return rightChild;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoMergeSort(int[] value)
	{
		mergeSort( value, 0, value.length -1);
		return (value);
	}

	private static void mergeSort( int[] value, int first, int last )
	{
		//길이가 1일 때에는 못 들어오게 조건 설정
		if( first < last ){
			int mid = ( first + last ) / 2;
			mergeSort( value, first, mid );
			mergeSort( value, mid + 1, last );
			merge( value, first, mid, last );
		}
	}

	private static void merge( int[] value, int first, int mid, int last )
	{
		//임시저장할 배열 만들기.
		int[] temp = new int[last - first + 1];
		int left = first,
			right = mid + 1,
			i = 0;

		while( left <= mid && right <= last ){
			if( value[left] <= value[right] ) temp[i++] = value[left++];
			else temp[i++] = value[right++];
		}
		while( left <= mid ) temp[i++] = value[left++];
		while( right <= last ) temp[i++] = value[right++];

		// temp에 있는 것을 original array로 옮기기.
		for ( i = 0; i < temp.length ; i ++ ) {
				value[first++] = temp[i];
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoQuickSort(int[] value)
	{
		quickSort( value, 0, value.length -1 );
		return (value);
	}


	private static void quickSort( int[] value, int first, int last )
	{
		//element가 1개일 때는 그냥 return
		if( first >= last ) return;

		// first와 last는 입력값이고, left와 right는 재귀 돌면서 변화하는 값임.
		// pivot은 각 배열의 가장 왼쪽 인덱스로 정함
		int left = first,
			right = last,
			pivot = value[left];


		while(true) {
			//pointer를 양쪽 끝에서 중간 방향으로 이동시킴.
			//왼쪽에 존재하는 원소가 pivot보다 작으면 swap필요 없으니 그냥 넘어간다. -3, 1, 0, 2 같은 경우에는 left는 [0]에 멈춰있을 것임
			while( pivot > value[left] ) left++;
			while( pivot < value[right] ) right--;
			if( left >= right ) break;
			swap( value, left, right ); //두 개의 while문이 끝났다는 것은 swap해야 할 것들이 생겼다는 의미.
			left++; right --;
		}
		//여기서 left를 사용하면 재귀 도는 이유가 없어진다. 위와 같은 상황에서 left는 계속 0일 것이기에.
		quickSort( value, first, right );
		quickSort( value, right + 1, last );

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoRadixSort(int[] value)
	{
		radixSort(value, 10,10);
		return (value);
	}

	private static void radixSort(int[] value, int base, int digits)
	{	//base = 진수, digits = 자릿수의 갯수 (숫자가 max 몇 자리수인지)
		//들어오는 숫자 중 가장 큰 숫자가 몇 자리수인지 구하지 않고 최대 자릿수까지 다 비교한다.

		//음수와 양수를 다루어야 하니 진수*2만큼의 counting sort용 배열을 만들어준다.
		//mod 연산을 거친 값이 들어간다. index 0은 사용되지 않을 것이다.
		int[] count = new int[base*2];
		int[] temp = new int[value.length];

		//0. counting sort
		//digit의 갯수만큼 for문을 돌려 정렬을 해야한다.
		//for문에서느 i++보다 ++i가 더 빠르다고 해서 써보았따.
		for( int k = 0; k < digits; ++k) {
			Arrays.fill(count, 0);

			int power = (int)Math.pow(base, k);
			//1.
			//숫자가 [n:0]일 때 LSB k번째 자리 숫자를 구하려면, 해당 숫자를 base^k로 나눈 몫을 다시 base로 % 해야 함.
			//ex. 10진법 1345 의 10의 자리([1]) 숫자는, 1345 / 10^1 = 134, 134 % 10 = 4 로 나옴.
			//음수까지 다루어야 하기에 kth_digit에 base를 더했다.
			//ex. 10진법 45의 1의 자리 수는 5이고, base=10이므로, 이 숫자는 index 15의 count에 해당한다.
			//ex. 반면, -45는 mod10을 하면 -5가 나오기때문에 base를 더하면 index 5의 count에 해당하게 된다.
			for ( int i = 0; i < value.length; ++i) {
				int kth_digit = value[i] / power % base;
				++count[kth_digit + base];
			}
			//2.
			//그 후엔 count의 각 element를 앞에서부터 누적시킨다.
			for ( int i = 0; i < count.length -1 ; ++i) { count[i+1] += count[i]; }

			//3.
			//value의 오른쪽 원소부터 counting sort를 실행해야 stable하게 sort가 이루어진다.
			//2.에서 value[i]가 증가시켰던 count의 인덱스를 찾는다--> count배열 그 인덱스의 원소 값에서 -1 한다--> temp[-1한 그 값]에 value[i]를 넣는다.
			for ( int i = value.length -1; i >=0; --i) {
				int index = value[i] / power % base + base;
				temp[--count[index]] = value[i];
			}
			//4.
			//temp-->value로 복사
			for ( int i = 0; i < temp.length ; ++i ) { value[i] = temp[i]; }
		}
	}
}

