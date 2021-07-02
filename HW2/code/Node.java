public class Node<T> {
    private T item;
    private Node<T> next;

    public Node(T obj) {
        this.item = obj;
        this.next = null;
    }

    public Node(T obj, Node<T> next) {
    	this.item = obj;
    	this.next = next;
    }

    public final T getItem() {
    	return item;
    }

    public final void setItem(T item) {
    	this.item = item;
    }

    public final void setNext(Node<T> next) {
    	this.next = next;
    }

    public Node<T> getNext() {
    	return this.next;
    }

    public final void insertNext(T obj) {
    	//implemented
    	   Node<T> temp = new Node<T>(obj);
    	   temp.setNext(this.getNext());
    	   this.setNext(temp);
    }

    public final void removeNext() {
    	//implemented
    	this.setNext(this.getNext().getNext());
    }
}

