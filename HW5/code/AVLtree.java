
public class AVLtree<K extends Comparable<K>, V> // <String, Pair>
{
	Node root;

	@Override
	public String toString() { return root == null ? "" : root.toString(); }

	public AVLtree () { root = new Node(); }

	// node for AVL Tree.
	public class Node
	{
		K key;
		LinkedList<V> headToValue;
		Node left , right , parent;
		int balance;

		public Node () {
			this.key = null;
			this.left = null;
			this.right = null;
			this.parent = null;
			this.headToValue = new LinkedList<V>(); // node생성시 일단 빈 linked list도 만들어놓기
		}

		public Node (K key, V value) {
			this.key = key;
			this.left = null;
			this.right = null;
			this.parent = null;
			this.headToValue = new LinkedList<V>();
		}

		public void setKey ( K key ) {
			this.key = key;
		}

		public K getKey ( ) {
			return this.key;
		}

		public void addValue ( V value ) {
			headToValue.add( value );
		}

		public int height ( Node node ) { //재귀돌면서 높이 구한다.
			if( node == null ) return -1;
			return Math.max( height( node.left ), height ( node.right ) )  + 1;
		}

		public int checkBalance( Node node ) {
			return height ( node.left ) - height( node.right );
		}


		public void rebalance ( ) {

			balance = checkBalance( this );
			Node rebalanced;

			if ( balance == - 2 ) {
				// ubalanced node가 right heavy면, right child가 rotate.
				// right child가 right heavy : L, left heavy : RL.
				rebalanced = height( this.right.right ) > height( this.right.left ) ?
						rotateLeft( this.right ) : rotateRightLeft( this.right );

			} else if ( balance == 2 ) {
				// unbalanced node: left heavy면, left child가 rotate.
				// left child가 right heavy : LR, left heavy :R .
				rebalanced = height( this.left.right ) > height( this.left.left ) ?
						rotateLeftRight( this.left ) : rotateRight( this.left );

			} else { rebalanced = this; }

			//parent 따라가면서 다시 rebal.
			if ( rebalanced.parent != null ) { rebalanced.parent.rebalance(); }
		}



		public Node rotateLeft(Node rotate) {

			//1. rotate의 parent의 parent가 rotate 가리키게 한다. parent가 root라서 p.p가 null인경우는 root가 rotate 가리킨다.
			if (rotate.parent.parent == null) { root = rotate;}
			else {
				if( rotate.parent.parent.right == rotate.parent ) { rotate.parent.parent.right = rotate; }
				else if( rotate.parent.parent.left == rotate.parent )	{ rotate.parent.parent.left = rotate; }
			}

			//2. rotate의 left에 뭐가 있으면 그걸 rotate.parent.right으로 보낸다. 없으면 rotate가리키는 right값을 없앤다.
			if ( rotate.left != null ) { rotate.parent.right = rotate.left; }
			else { rotate.parent.right = null; }

			//3. rotate.parent가 rotate.left로 온다
			rotate.left = rotate.parent;

			//4. parent var 변경
			if( root == rotate ) { rotate.parent = null; }
			else { rotate.parent = rotate.parent.parent ;}

			if( rotate.left.right != null ) { rotate.left.right.parent = rotate.left; }

			rotate.left.parent = rotate;
			return rotate;
		}



		public Node rotateRight(Node rotate) { // 위 함수의 left와 right를 맞교환

			//1. rotate의 parent의 parent가 rotate 가리키게 한다. parent가 root라서 p.p가 null인경우는 root가 rotate 가리킨다.
			if ( rotate.parent.parent == null ) { root = rotate; }
			else {
				if ( rotate.parent.parent.left == rotate.parent ) { rotate.parent.parent.left = rotate; }
				else if ( rotate.parent.parent.right == rotate.parent ) { rotate.parent.parent.right = rotate; }
			}

			//2. rotate의 right에 뭐가 있으면 그걸 rotate.parent.left으로 보낸다. 없으면 rotate가리키는 left값을 없앤다.
			if ( rotate.right != null ) { rotate.parent.left = rotate.right; }
			else { rotate.parent.left= null; }

			//3. rotate.parent가 rotate.right로 온다
			rotate.right = rotate.parent;

			//4. parent var 변경
			if ( root == rotate ) {	rotate.parent = null; }
			else { rotate.parent = rotate.parent.parent; }

			if ( rotate.right.left != null ) {rotate.right.left.parent = rotate.right;}

			rotate.right.parent = rotate;
			return rotate;
		}


		public Node rotateRightLeft(Node rotate) {
			return rotateLeft( rotateRight( rotate.left ) );
		}

		public Node rotateLeftRight ( Node rotate ) {
			return rotateRight( rotateLeft( rotate.right ) );
		}



		@ Override
		// @hashcode값 입력되면 preorder traversal로 key 출력함. parent --> left --> right 재귀돌아서.
		public String toString ( ) {
			// node안의 함수이다. toString하면 일단 node의 key를 s에 저장한다.
			// key는 node가 아니라 걍 string이라서 이 함수가 오버라이딩되는 게 아니다.

			String s ="";
			if ( key != null ) s = key.toString();
			if ( left != null)  s += " " + left.toString(); // left 노드로 가서 재귀 돌다가 빠져나오면
			if ( right != null) s += " " + right.toString(); //right가서도 또 재귀돈다.
			return s;
		}

	}







	public void insert ( K key, V  value ) {
		Node searchResult = search( key, root );

		// return받은 노드에 이미 key가 존재하면 value만 넣으면 된다.
		if ( searchResult.getKey() != null ) {
			searchResult.addValue( value );
		}
		// 만약 노드에 키가 비어있으면, key를 넣고 value도 넣는다.
		else {
			searchResult.setKey( key );
			searchResult.addValue( value );
		}
		searchResult.rebalance(); //insert된 노드부터 rebalance한다.
	}



	// tree안에 동일 key가 있는지 확인
	// key가 같은게 있으나 없으나 본인이 삽입되어야 하는 node를 return한다.
	// 1. 동일한 게 있는 경우: key가 존재하는 노드 리턴--> value만 add하면 됨
	// 2. 동일한 게 없는 경우: key가 null인 노드 --> key, value 다 add.
	// return 받은 노드의 key가 null인지 아닌지 확인하면 동일 key 존재여부를 알 수 있음.

	public Node search ( K key, Node searchingNode ) {

		// root가 빈 경우라 key가 null임. root node는 parent== null임.
		if ( searchingNode.getKey() == null ) {
			return searchingNode;

		} else {
			int result = searchingNode.getKey().compareTo( key );
			if ( result == 0 ) { //동일한 게 있다.
				return searchingNode;

			} else if ( result > 0 ) { // 기존 키보다 더 먼저온다-->왼쪽으로 가라
				if ( searchingNode.left == null ) { // 근데 왼쪽이 없다? 그럼 왼쪽에 insert
					searchingNode.left = new Node();
					searchingNode.left.parent = searchingNode;	//parent 설정.
					return searchingNode.left;

				} else { // 왼쪽 있으면 거기서 또 search
					return search( key, searchingNode.left );
				}

			} else { // 기존 키보다 나중에 오면 오른쪽으로.
				if ( searchingNode.right == null ) {
					searchingNode.right = new Node();
					searchingNode.right.parent = searchingNode;
					return searchingNode.right;

				} else {
					return search( key, searchingNode.right );
				}
			}
		}
	}



	public Node search4matching ( K key, Node searchingNode ) {

		// root가 빈 경우라 key가 null임. root node는 parent== null임.
		if ( searchingNode.getKey() == null ) {
			return searchingNode;
		} else {
			int result = searchingNode.getKey().compareTo( key );
			if ( result == 0 ) { //동일한 게 있다.
				return searchingNode;

			} else if ( result > 0 ) { // 기존 키보다 더 먼저온다-->왼쪽으로 가라
				if ( searchingNode.left == null ) { // 근데 왼쪽이 없다? 그럼 왼쪽에 insert
					return null;

				} else { // 왼쪽 있으면 거기서 또 search
					return search4matching( key, searchingNode.left );
				}

			} else { // 기존 키보다 나중에 오면 오른쪽으로.
				if ( searchingNode.right == null ) {
					return null;

				} else {
					return search4matching( key, searchingNode.right );
				}
			}
		}
	}
}
