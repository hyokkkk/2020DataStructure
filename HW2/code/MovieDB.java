import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 *
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를
 * 유지하는 데이터베이스이다.
 */
public class MovieDB {
    MyLinkedList<Genre> genList;

    public MovieDB() {
        genList = new MyLinkedList<>();
    }

    public void insert(MovieDBItem item) {
        Genre newGen = new Genre(item.getGenre());
        String newTitle = item.getTitle();

        Genre oldGen;
        MyLinkedListIterator<Genre> itr = genList.iterator();

        while(itr.hasNext()){
            oldGen = itr.next();
            int gencmp = oldGen.compareTo(newGen);

            //iterator를 통해서는 값을 읽어올 수만 있음. curr멈추고 거기에 insert못하지만 MyLinkedListIterator를 수정해서 가능하게 함..
            if (gencmp==0){
                MyLinkedListIterator<String> titleItr = oldGen.movielist.iterator();

                /* title insert */
                while(titleItr.hasNext()){
                    String oldTitle = titleItr.next();
                    int ttlcmp = oldTitle.compareTo(newTitle);
                    if (ttlcmp ==0 ) { return; }
                    else if (ttlcmp > 0) {
                        titleItr.insertPrev(newTitle);
                        return;
                    }
                }
                oldGen.movielist.add(newTitle);//끝났는데도 compareTo < 0이면 맨 마지막에 insert한다.
                return;
            }else if (gencmp > 0){
                itr.insertPrev(newGen);
                newGen.movielist.add(newTitle);
                return;
            }
        }
        // 동일 장르 없음 -> iteration 끝나고 맨 뒤에 genre, title 동시 추가
        genList.add(newGen);
        newGen.movielist.add(newTitle);
    }



    public void delete(MovieDBItem item) {

        String targetGen = item.getGenre();
        MyLinkedListIterator<Genre> genItr = genList.iterator();

        while(genItr.hasNext()){
            Genre oldGen = genItr.next();

            if (oldGen.getGenre().equals(targetGen)){
                MyLinkedListIterator<String> titleItr = oldGen.movielist.iterator();
                String targetTtl = item.getTitle();

                while(titleItr.hasNext()){
                    String oldTitle = titleItr.next();
                    if (oldTitle.equals(targetTtl)){
                        titleItr.remove();
                    }
                }
            }
        }
    }



    public MyLinkedList<MovieDBItem> search(String term) {

        MyLinkedList<MovieDBItem> result = new MyLinkedList<MovieDBItem>();
        MyLinkedListIterator<Genre> genItr = genList.iterator();
        while(genItr.hasNext()){
            Genre nowGen = genItr.next();
            String nowGenStr = nowGen.getGenre();

            MyLinkedListIterator<String> titleItr = nowGen.movielist.iterator();
            while(titleItr.hasNext()){
                String nowTitle = titleItr.next();
                if (nowTitle.contains(term)){
                    result.add(new MovieDBItem(nowGenStr, nowTitle));
                }
            }
        }
    	return result;
    }



    public MyLinkedList<MovieDBItem> items() {

        MyLinkedList<MovieDBItem> result = new MyLinkedList<MovieDBItem>();
        MyLinkedListIterator<Genre> genItr = genList.iterator();
        while(genItr.hasNext()){
            Genre nowGen = genItr.next();
            String nowGenStr = nowGen.getGenre();

            MyLinkedListIterator<String> titleItr = nowGen.movielist.iterator();
            while(titleItr.hasNext()){
                result.add(new MovieDBItem(nowGenStr, titleItr.next()));
            }
        }
    	return result;
    }
}

class Genre implements Comparable<Genre> {

    private String genre;
    MyLinkedList<String> movielist;

    public Genre(String name) {
        genre = name;
        movielist = new MyLinkedList<>();
    }

    public String getGenre(){
        return genre;
    }

    @Override
    public int compareTo(Genre o) {
        return genre.compareTo(o.getGenre());
    }

    @Override
    public int hashCode() {
            throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public boolean equals(Object obj) {
        return genre.equals((String)obj);
    }
}
