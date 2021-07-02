
public class Hashtable< V>
{
	int capacity = 100; // hashtable 용량
	@ SuppressWarnings ("unchecked")
	AVLtree<String, V>[ ] slot = new AVLtree[capacity]; // hashtable의 slot은 avl tree의 배열로 이루어져있음.

	// Hashtable이 생성되는 동시에 avl tree배열이 생성되도록 함
	public Hashtable () {
		for ( int i = 0 ; i < capacity; ++ i) {
			slot[i] = new AVLtree<>();
		}
	}

	// hashcode algorithm
	public int getHashCode ( String substring ) {
		int hashcode = 0;

		// 받은 substring을 char배열로 만든 후, 하나씩 돌면서 아스키코드값 더한다.
		for ( char c : substring.toCharArray() ) { hashcode += c; }
		hashcode = hashcode % capacity; // 그 값에 mod.
		return hashcode;
	}

	public void insert ( String key, V value ) {
		int hashcode = getHashCode( key ); // hashcode 받는다.
		slot[ hashcode ].insert( key, value ); // 해당 AVL tree에서 insert하게 넘긴다
	}
}

