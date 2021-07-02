import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를 유지하는 데이터베이스이다.
 */
public class MovieDB {
	//이 프로그램의 중심이 되는 링크드리스트
	MyLinkedList<Genre> genreList;

	public MovieDB() {
		genreList = new MyLinkedList<>();
	}

	public void insert(MovieDBItem item) {
		//제목에 따라 정렬되는 영화리스트
		//장르와 제목이 동시에 생성됨
		MovieList movie = new MovieList(item);

		// 장르대로 정렬되는 리스트가 아예 비어있으면 위처럼 movie를 만든 후
		// genreList에 그 Genre inst가 위치하게 한다.
		// movielist의 헤드가 가리키는 것은 Genre instance임.
		if (genreList.isEmpty()) {
			genreList.add(movie.head);
		}

		// genreList에 무언가가 있으면 지금 입력받은 장르와 동일한지 검사
		else {
			Iterator<Genre> it = genreList.iterator();
			boolean flg = false;
			Node<Genre> cur = null;

			while (it.hasNext()) {
				Genre genre = it.next();
				if (genre.equals((Object) item)) {
					// 동일한 장르가 있는지 판단하고, 있으면 그 위치 받는다.
					flg = true;
					cur = ((MyLinkedListIterator<Genre>) it).getCurr();
					break;
				}
			}
			//이미 동일장르 존재 시
			if (flg == true) {
				// 동일한 장르 그 하위에 제목 비교해서 넣는다
				movie.head = cur.getItem();
				Iterator<String> itr = movie.iterator();

				// title을 어디에 넣을지 compare해서 결정하기
				boolean flag = false;
				Node<String> curr = null;

				while (itr.hasNext()) {
					String ttl = itr.next();
					// title이 중간에 끼어들어야 하는지, 맨 뒤로 가면 되는지 판단.
					// 끼어들어야 할 시에는 그 위치를 기억한다.
					if (ttl.compareTo(item.getTitle()) > 0) {
						curr = ((MovieListIterator) itr).getPrev();
						flag = true;
						break;
					}
					// 장르, 제목 다 같으면 아무것도 안 하고 RETURN.
					else if (ttl.compareTo(item.getTitle()) == 0) {
						return;
					}
				}
				// title이 그냥 맨 뒤에 붙으면 되는 경우
				if (flag == false) {
					movie.add(item.getTitle());
				}


				// title이 중간에 끼어들어야 하는 경우
				else if (flag == true) {
					curr.insertNext(item.getTitle());
				}
			}

			//동일한 장르가 없으니 genreList에 장르를 넣어주어야 함.
			else if (flg == false) {
				Iterator<Genre> itr = genreList.iterator();

				// 만든 장르클래스 어디에 넣을지 compare해서 결정하기
				boolean flag = false;
				Node<Genre> curr = null;

				while (itr.hasNext()) {
					Genre gen = itr.next();

					// 중간에 끼어들어야 하는지 판단하고, 그렇다면 그 위치를 기억한다.
					if (gen.compareTo(movie.head) > 0) {
						curr = ((MyLinkedListIterator<Genre>) itr).getPrev();
						flag = true;
						break;
					}
				}
				// 그냥 맨 뒤에 붙으면 되는 경우
				if (flag == false) {
					genreList.add(movie.head);
				}
				// 중간에 끼어들어야 하는 경우
				else if (flag == true) {
					curr.insertNext(movie.head);
					genreList.numItems += 1;
				}
			}
		}
	}

	
	
	public void delete(MovieDBItem item) {
		String title = item.getTitle();
		Iterator<Genre> it = genreList.iterator();
		Node<Genre> curr = null;
		Node<Genre> prev = null;
		
		while (it.hasNext()) {
			Genre genreInst = it.next();
			if (genreInst.equals((Object) item)) {
				// 동일한 장르가 있는지 판단하고, 있으면 장르 자체를 삭제할 수도 있으므로 그 위치 받는다.
				curr = ((MyLinkedListIterator<Genre>) it).getCurr();
				prev = ((MyLinkedListIterator<Genre>) it).getPrev();
				
				//장르에 영화 하나만 있으면 그 장르 삭제
				if(curr.getItem().getNext().getNext() == null) {
					prev.removeNext();
				}
				
				//영화가 여러개 있으면 그 영화만 삭제
				else {
					Node<String> cur = curr.getItem();
					String movieTitle = null;
					
					//일치하지 않는 동안에만 돌아라. 일치하면 값 뱉을거야
					while (cur.getNext() != null) {
						movieTitle = cur.getNext().getItem();
						if(movieTitle.equals(title)) {
							cur.removeNext();
							break;
						}
						cur = cur.getNext();
					}
				}
				break;
			}
		}
	}

	public MyLinkedList<MovieDBItem> search(String term) {
		Iterator<Genre> it = genreList.iterator();
		String genre = null;
		String title = null;
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		// 한 바퀴 돌 때마다 해당하는 genre랑 title 받아서 MovieDBItem에 넣고,
		// 그걸 MyLinkedList의 안에 insert해야함.

		while (it.hasNext()) {
			Genre gen = it.next();
			genre = gen.getItem();
			
			//해당 장르 inst가 있는 노드의 위치를 받는다.
			Node<Genre> curr = ((MyLinkedListIterator<Genre>) it).getCurr();
			
			//그 노드 안에 있는 Genre inst를 curr에 저장한다.
			Node<String> cur = curr.getItem();

			//term이 들어있는 genre, title 받아서 results에 저장
			while (cur.getNext() != null) {
				title = cur.getNext().getItem();
				if(title.contains(term)) {
					MovieDBItem result = new MovieDBItem(genre, title);
					results.add(result);
				}
				cur = cur.getNext();
			}
		}
		return results;
	}

		

	public MyLinkedList<MovieDBItem> items() {
		Iterator<Genre> it = genreList.iterator();
		String genre = null;
		String title = null;
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		// 한 바퀴 돌 때마다 genre랑 title 받아서 MovieDBItem에 넣고,
		// 그걸 MyLinkedList의 안에 insert해야함.

		while (it.hasNext()) {
			Genre gen = it.next();
			genre = gen.getItem();
			
			//장르 inst 있는 위치 받아놓음
			Node<Genre> curr = ((MyLinkedListIterator<Genre>) it).getCurr();
			
			//그 노드 안에 있는 Genre inst를 curr에 저장한다.
			Node<String> cur = curr.getItem();

			while (cur.getNext() != null) {
				title = cur.getNext().getItem();
				MovieDBItem result = new MovieDBItem(genre, title);
				results.add(result);
				cur = cur.getNext();
			}
		}
		return results;
	}
}



class Genre extends Node<String> implements Comparable<Genre> {

	public Genre(String name) {
		super(name);
	}

	@Override
	public int compareTo(Genre o) {
		return (this.getItem()).compareTo(o.getItem());
	}

	@Override
	public boolean equals(Object obj) {
		return (this.getItem()).equals(((MovieDBItem) obj).getGenre());
	}
}



class MovieList implements ListInterface<String> {

	Genre head;
	public int numItems;

	public MovieList(MovieDBItem item) {
		head = new Genre(item.getGenre());
		Node<String> toMovie = new Node<String>(item.getTitle());
		head.setNext(toMovie);
		numItems = 1;
	}

	@Override
	public final Iterator<String> iterator() {
		return new MovieListIterator(this);
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
	public void add(String item) {
		Node<String> last = head.getNext();
		while (last.getNext() != null) {
			last = last.getNext();
		}
		last.insertNext(item);
		numItems += 1;
	}

	@Override
	public String first() {
		return head.getNext().getItem();
	}

	@Override
	public void removeAll() {
		head = null;
	}
}

class MovieListIterator implements Iterator<String> {
	private MovieList list;
	private Node<String> curr;
	private Node<String> prev;

	public MovieListIterator(MovieList list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != null;
	}

	@Override
	public String next() {
		if (!hasNext())
			throw new NoSuchElementException();
		prev = curr;
		curr = curr.getNext();
		return curr.getItem();
	}

	public Node<String> getCurr() {
		return curr;
	}

	public Node<String> getPrev() {
		return prev;
	}

	@Override
	public void remove() {
		if (prev == null)
			throw new IllegalStateException("next() should be called first");
		if (curr == null)
			throw new NoSuchElementException();
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}
}

