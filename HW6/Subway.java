import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;



/*===================================================================================================================*/
// *** overall structure : HashMap은 총 2개가 필요하다.
// 1. key: stationID, value: station --> stationID받아서 해당 station의 edge에 걸리는 시간 넣어준다.
// 2. key: name, value: ArrayList<Station>. --> 각 이름을 갖고있는 역들을 리스트에 추가함. 환승역들은 한 이름에 여러 역이 추가될 것.
// 1의 val에 들어가는 station과 2의 ArrayList에 들어가는 station은 same instance다.
/*===================================================================================================================*/

class Station
{
	final String name;
	final ArrayList<Edge> adjacency;

	Station(String name){
		this.name = name;
		this.adjacency = new ArrayList<>(); // station생성될 때 adjacency list는 처음이자 마지막으로 생성.
	}

	@Override
	public String toString ( ) {
		return name;
	}
}

/*==================================================================================*/

class Edge
{
	final Station nextStation;
	final long weight;	// 소요시간

	Edge(Station nextStation, long weight){
		this.nextStation = nextStation;
		this.weight = weight;
	}

	@ Override
	public String toString ( ) {
		return "-"+"(" + weight + "min)"+ "->" + nextStation ;
	}
}

/*==================================================================================*/

public class Subway
{
	public static void main (String[] args) throws IOException { //file이름이 String[] args로 들어옴.

		if (args.length == 0) { System.err.println("읽을 파일경로를 넣어주셈"); System.exit( 1 );
		}else if (args.length >1) { System.err.println("wrong filepath"); System.exit( 1 );}



		// 0. file읽어서 지도data받기
		Path path = Paths.get( args[0] );

		try { readFile(path);
		} catch( IOException e ) { System.err.println("no such file exception"); System.exit( 1 );}



		// 1. 입력받기(출발역, 도착역, QUIT) + 출력하기
		BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );

		while ( true ) {
			try {
				String input = br.readLine(); // break될 때까지 입력을 받는다.
				if (input.compareTo( "QUIT" ) == 0) break;
				if (input.equals("")) { System.out.println( "아무것도 입력되지 않았습니다." ); continue; }

				String[] inputArr = input.split( " " );
				String departureStation = inputArr[0];
				String arrivalStation = inputArr[1];

				printResult(departureStation, arrivalStation);

			} catch ( IOException e ) {
				System.out.println( "입력이 잘못되었습니다. 오류 : " + e.toString() );
			}
		}
	}

//
//----------------------------------------- 0. file읽어서 지도data받기---------------------------------------------------------//
//
	static final HashMap<String, Station> map_by_StationID = new HashMap<>( ); // String = stationID , Station = station
	static final HashMap<String, ArrayList<Station>> database = new HashMap<>( ); // String = station name, ArrayList<Statino> = station name에 해당하는 station.

	public static HashMap<String, ArrayList<Station>> readFile(Path path) throws IOException {

		// 파일 경로 받아와서 읽는다. 라인별로 원소에 저장. 즉, 파일 하나가 배열 하나임.
		List<String> filelines = Files.readAllLines( path );
		Iterator<String> iter = filelines.iterator(); //generic 안 쓰면 iter사용할 때마다 casting해줘야함.


		// 1) stationID: 810 / name: 암사 / linenum: 8 를 hashmap들에 넣기
		while (iter.hasNext()) {
			String fileline = iter.next();

			if (fileline.equals( "" )) break;	//정보가 나뉘는 곳 전까지만 받는다. iterator는 중단점 기억해서 중단한 후에 그 지점부터 재시작 가능

			String[] stationINFO = fileline.split( " " );
			String id = stationINFO[0];
			String name = stationINFO[1];
//			String linenum = stationINFO[2]; // linenum 사옹하는 곳이 없어서 안 받음.



			// i) insert stationID & station name to map_by_statoinID

			Station station = new Station( name );

			// connect same named stations(transfer station)
			int mapsize = map_by_StationID.size();

			if (mapsize !=0) { // 기존에 역이 하나라도 있어야 동일한 이름의 역인지 비교를 하지.

				for ( Station stationInMap : map_by_StationID.values()) {
					if (stationInMap.name.equals( name )) {
						stationInMap.adjacency.add( new Edge( station, 5 ) );
						station.adjacency.add( new Edge( stationInMap, 5 ) );
					}
				}
			}
			// insert station into id map
			map_by_StationID.put( id, station );


			// ii) insert into database

			if (!database.containsKey( name )) { // 해당하는 이름이 key에 없으면 처음 들어오는 역이란 소리.
				ArrayList<Station> stationsWithThatName = new ArrayList<>();
				stationsWithThatName.add( station );
				database.put( name, stationsWithThatName );
			}else database.get( name ).add( station ); // 이미 동일 이름의 key가 존재하면 그 value(arraylist)받아서 거기에 추가
		}



		// 2) from : 810 / to: 811 / weight: 3를 hashmap에 넣기
		while (iter.hasNext()) {
			String fileline = iter.next();

			String[] stationINFO = fileline.split( " " );

			// map_by_stationid 에서 key: ID로 station찾는다. 어짜피 database에 들어있는 station이랑 동일함.
			// 그 station의 edge에 다음역이랑 weight 넣는다.

			Station from = map_by_StationID.get( stationINFO[0] );
			Station to = map_by_StationID.get( stationINFO[1] );
			int weight = Integer.valueOf( stationINFO[2] );

			from.adjacency.add( new Edge( to, weight ) );
		}


	//************* TODO : run시 주석화하기. 구조 확인용 code ******************//
	/*
		Set keys = database.keySet();
		Iterator<String> iterator = keys.iterator();

		//get(key)는 key에 대한 value값만 출력. values()는 저장된 모든 value 출력
		System.out.println( "[debugging]" );

		while (iterator.hasNext()) {
			String key = iterator.next();
			System.out.println( "<"+"역명: "+key+">");

			ArrayList<Station> stationlist = database.get( key );
			int listsize = stationlist.size();

			for (int i = 0; i < listsize; i ++) {
				Station station = stationlist.get( i );
				int stationsize = station.adjacency.size();

				for(int j = 0; j < stationsize; j++)
				System.out.println( i+1+"th "+ station + "" + station.adjacency.get( j ) ); //이미 해당 class에 toString() override해놨음
			}
			System.out.println( "" );
		}
	*/
	//*******************************************************************//
		return database;
	}

//
//---------------------1. input 받고 경로 계산해서 출력하기-------------------------------------------------------------------------//
//
	static PriorityQueue<Node> priorityQueue = new PriorityQueue<>();	//가장 적은 시간 걸리는 노드를 리턴할거임.
	static ArrayList<String> pathlist = new ArrayList<>();
	static HashSet<Station> visited = new HashSet<Station>();
	static long minimumTime;



	public static Node dijkstra (String departure, String arrival) {

		//출발역이 있는지 확인. 있다면 그 역 받아서 dijkmap에 넣는다.
		boolean departureExist = database.containsKey( departure ) ;
		boolean arrivalExist = database.containsKey( arrival) ;

		if (!departureExist && ! arrivalExist) {
			System.out.println( "Both stations" + departure+" and " +arrival+" don't exist." );
			System.exit( -1 );
		}else if(!departureExist) {
			System.out.println( "Departure stations" + departure+" doesn't exist." );
			System.exit( -1 );
		}else if(!arrivalExist) {
			System.out.println( "Arrival stations" + arrival +" doesn't exist." );
			System.exit( -1 );
		}


		for ( Station station : database.get( departure ) ) {
			Node node = new Node(station, 0, departure);
			priorityQueue.add( node );
			// 출발점이니까 거리 0, visited = T; prepaths만들고 자기 자신 넣음.
		}

		Node result = null;

		// loop
		boolean found = false;
		while(true) {

			Node polledNode = priorityQueue.poll(); //최소값을 queue에서 꺼낸다.
			boolean inSet = true;
			while( inSet) {
				if ( visited.size() != 0 && visited.contains( polledNode.station )) { //이미 그 노드에 해당하는 역이 set에 있다면
					polledNode = priorityQueue.poll();	// 다시 선택이 될 수 없기에 새로운 거 꺼냄.
				} else { // set에 포함되지 않았던 역이라면, 바로 set에 넣어준다.
					visited.add( polledNode.station );
					inSet = false;
					if (polledNode.station.name.equals(arrival)) { // 꺼낸 역이 종료역과 같은지 판단해서 종료
						result = polledNode;
						minimumTime = polledNode.distance;
						found = true;
					}
				}
			}
			if (found) break;

			ArrayList<Edge> adjStations = polledNode.station.adjacency;
			for (int i = 0; i < adjStations.size(); i++) {
				Station adjStation = adjStations.get(i).nextStation; // 방금 최단경로로 선택된 노드의 인접역을 받는다.

				if (visited.contains( adjStation )) continue; // 이 인접역이 set에 있으면 걍 넘어간다.

				Node node = new Node( adjStation ); // set에 없는 인접역이면 해당하는 node 만든다.
				long newDist= polledNode.distance + adjStations.get( i ).weight;

				if (newDist < node.distance) {
					node.updateRelaxation( newDist ); // relaxation했다.
					node.previous_paths.addAll( polledNode.previous_paths);// path update한다.
					node.previous_paths.add( node.station.name ); // 자기 자신도 path에 넣음
					priorityQueue.add( node );// queue에 보낸다
				}
			}
		}
		return result;
	}


/*--------------------------------------------------------------------------------------------------*/

	public static void printResult(String departure, String arrival) {

		Node destNode= dijkstra(departure, arrival); // 목적지를 담은 node가 return.
		pathlist = destNode.previous_paths;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < pathlist.size()-1; i ++) {
			String p = pathlist.get( i );
			if (p.equals("")) continue; //환승역 중 하나 없애고 이거 안 해주면 공백 두 칸 들어갈거임.
			if (p.equals(pathlist.get( i+1 ))) { // 환승한 곳은 현재역과 이전역의 이름이 같을 것임.
				pathlist.set( i, "["+p+"]" );
				pathlist.set( i+1, "" ); // 같은 것 중 하나는 없애줌.
			}
			sb.append( pathlist.get( i ) ).append( " " ); //이거까지 p로 하면 set되기 전의 p라서 괄호가 안 붙어서 나옴.
		}
		sb.append( pathlist.get( pathlist.size()-1 ) ); //맨 마지막꺼도 더해줌.
		System.out.println( sb );
		System.out.println( minimumTime ); //최소시간

			priorityQueue = new PriorityQueue<>();
			pathlist = new ArrayList<>();
			visited = new HashSet<Station>();
			minimumTime = 0;
	}
}


class Node implements Comparable<Node>{
	final long INF = 9223372036854775807L; //두 간선 사이의 소요시간 최대가 1억이니까 1억인 정류장 몇 십개 거치면 int범위에서 처리가 안 될수도 있음.
	Station station;
	long distance;
	ArrayList<String> previous_paths;

	Node(Station station){
		this.distance = INF;
		this.station = station;
		this.previous_paths = new ArrayList<String>();
	}

	Node(Station station, long distance, String itself){ // 맨 처음 시작점에서만 사용됨
		this.station = station;
		this.distance = distance;
		this.previous_paths = new ArrayList<>();
		previous_paths.add( itself );
	}

	public void updateRelaxation(long distance){
		this.distance = distance;
	}

	@Override
	public int compareTo(Node target) {
		return this.distance > target.distance ? 1 : -1 ;
	}

	@Override
	public String toString() {
		return "(" + this.station.name + ", 거리:" + this.distance + ")";
	}
}

/*==================================================================================*/
