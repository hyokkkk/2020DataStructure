public class Node<T> {
    private T item;
    private Node<T> prev;
    private Node<T> next;

    public Node(){
        this.item = null;
        this.prev = null;
        this.next = null;
    }

    public Node(T obj) {
        this.item = obj;
        this.prev = null;
        this.next = null;
    }

    public Node(T obj, Node<T> prev, Node<T> next) {
    	this.item = obj;
        this.prev = prev;
    	this.next = next;
    }

    public final T getItem() {
    	return item;
    }

    public final void setItem(T item) {
    	this.item = item;
    }

    public final void setPrev(Node<T> prev) {
    	this.prev = prev;
    }

    public Node<T> getPrev() {
    	return this.prev;
    }
    public final void setNext(Node<T> next) {
    	this.next = next;
    }

    public Node<T> getNext() {
    	return this.next;
    }

    public final void insertPrev(T obj){
        Node<T> n = new Node<T>(obj, this.getPrev(), this);
        setPrev(n);
        n.getPrev().setNext(n);
    }

    public final void insertNext(T obj) {
        // 내 노드 다음에 obj를 element로 하는 node를 insert
        Node<T> n = new Node<T>(obj, this, this.getNext());
        setNext(n);
        if (n.getNext()!= null){
            getNext().setPrev(n);
        }
    }

    // doubly-linked list라서 removeNext 대신 자기 자신을 remove하게 만듦.
    public final void remove() {
        getPrev().setNext(getNext());
        if (getNext() != null){
            getNext().setPrev(getPrev());
        }
    }
}

