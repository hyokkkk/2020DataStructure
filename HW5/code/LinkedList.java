import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T>
{
	PairNode<T> head;

	public LinkedList () {
		head = new PairNode<T>( null );
	}

	@ Override
	public Iterator<T> iterator ( ) {
		return new LinkedListIterator<T>( this );
	}

	public boolean isEmpty ( ) {
		return head.getNext() == null;
	}

	public T first ( ) {
		return head.getNext().getItem();
	}

	public void add ( T item ) {
		PairNode<T> last = head;
		while ( last.getNext() != null ) {
			last = last.getNext();
		}
		last.insertNext( item );
	}

	public void removeAll ( ) {
		head.setNext( null );
	}
}


class PairNode<T>
{
	private T item;
	private PairNode<T> next;

	PairNode (T item) {
		this.item = item;
		this.next = null;
	}

	PairNode (T item, PairNode<T> next) {
		this.item = item;
		this.next = next;
	}

	public final T getItem ( ) {
		return item;
	}

	public final void setItem ( T item ) {
		this.item = item;
	}

	public final void setNext ( PairNode<T> next ) {
		this.next = next;
	}

	public PairNode<T> getNext ( ) {
		return this.next;
	}

	public final void insertNext ( T obj ) {
		PairNode<T> temp = new PairNode<T>( obj );
		temp.setNext( this.getNext() );
		this.setNext( temp );
	}

	public final void removeNext ( ) {
		this.setNext( this.getNext().getNext() );
	}

}


class LinkedListIterator<T> implements Iterator<T>
{
	private LinkedList<T> list;
	private PairNode<T> curr;
	private PairNode<T> prev;

	public LinkedListIterator (LinkedList<T> list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@ Override
	public boolean hasNext ( ) {
		return curr.getNext() != null;
	}

	@ Override
	public T next ( ) {
		if ( !hasNext() ) throw new NoSuchElementException();

		prev = curr;
		curr = curr.getNext();

		return curr.getItem();
	}

	public PairNode<T> getCurr ( ) {
		return curr;
	}

	public PairNode<T> getPrev ( ) {
		return prev;
	}

	@ Override
	public void remove ( ) {
		if ( prev == null ) throw new IllegalStateException( "next() should be called first" );
		if ( curr == null ) throw new NoSuchElementException();
		prev.removeNext();
		curr = prev;
		prev = null;
	}
}
