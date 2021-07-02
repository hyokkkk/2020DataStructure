
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<T> implements ListInterface<T> {
    // dummy head
    Node<T> head;
    private int numItems;

    public MyLinkedList() {
            head = new Node<T>(null);
            numItems = 0;
    }

    /**
     * {@code Iterable<T>}를 구현하여 iterator() 메소드를 제공하는 클래스의 인스턴스는
     * 다음과 같은 자바 for-each 문법의 혜택을 볼 수 있다.
     *
     * <pre>
     *  for (T item: iterable) {
     *  	item.someMethod();
     *  }
     * </pre>
     *
     * @see PrintCmd#apply(MovieDB)
     * @see SearchCmd#apply(MovieDB)
     * @see java.lang.Iterable#iterator()
     */

    // parent(Iterator<>)가 child(MyLinkedListIterator<>를 참조하는 형태
    // 였는데 바꿨다.
    public final MyLinkedListIterator<T> iterator() {
        // 지팡이를 내놓는다.
    	return new MyLinkedListIterator<T>(this);
    }

    public int sizeUp(){
        return ++numItems;
    }

    public int sizeDown() {
        return --numItems;
    }


    @Override
    public boolean isEmpty() {
            return head.getNext() == null;
    }

    @Override
    public int size() {
            return numItems;
    }

    @Override
    public T first() {
            return head.getNext().getItem();
    }

    @Override
    // 맨 뒤에 더하는 거
    public void add(T item) {
            Node<T> last = head;
            while (last.getNext() != null) {
                    last = last.getNext();
            }
            last.insertNext(item);
            numItems += 1;
    }

    @Override
    public void removeAll() {
            head.setNext(null);
    }
}



// 지팡이 class
// doubly-linked list ver.
class MyLinkedListIterator<T> implements Iterator<T> {
    /* Iterator<T> itr = new MyLinkedListIterator<T>(); 형태라서
    *  itr을 통해서는 본인 or overriding 된 값에만 접근 가능함.
    *  새로운 method 추가해도 itr.method() 통해서 접근 불가하다.
    *  --> 그래서 iterator() 의 return형을 Iterator-> MyLinkedListIterator로 수정함. (자바의 목적에 맞는지는 모르겠지만...;)
    */

    // You have to maintain the current position of the iterator.
    private MyLinkedList<T> list;
    private Node<T> curr;

    public MyLinkedListIterator(MyLinkedList<T> list) {
            this.list = list;
            this.curr = list.head;
    }

    public void insertPrev(T item){
        curr.insertPrev(item);
        list.sizeUp();
    }

    @Override
    public boolean hasNext() {
            return curr.getNext() != null;
    }

    //insert할 때 다음꺼 가져와서 insert해야 할 거랑 같으면 curr에서 insertNext하면 됨.
    @Override
    public T next() {
            if (!hasNext())
                    throw new NoSuchElementException();

            // curr이 먼저 다음거로 옮겨지고, 옮겨진 노드의 item을 return함.
            curr = curr.getNext();

            return curr.getItem();
    }

    // curr을 삭제하고, curr의 위치를 전의 것으로 옮김
    @Override
    public void remove() {
            if (curr == null)
                    throw new NoSuchElementException();
            curr.remove();
            list.sizeDown();
            curr = curr.getPrev();
    }
}

