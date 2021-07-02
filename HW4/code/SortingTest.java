import java.io.*;
import java.util.*;

public class SortingTest{
    public static void main(String args[]){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	try{
            boolean isRandom = false;	        // 입력받은 배열이 난수인가 아닌가?
            int[] value;	                // 입력 받을 숫자들의 배열
            String nums = br.readLine();	// 첫 줄을 입력 받음
            if (nums.charAt(0) == 'r'){
                // 난수일 경우
                isRandom = true;	        // 난수임을 표시

                String[] nums_arg = nums.split(" ");
                int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
                int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
                int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

                Random rand = new Random();	                // 난수 인스턴스를 생성한다.

                value = new int[numsize];	                // 배열을 생성한다.
                for (int i = 0; i < value.length; i++)	        // 각각의 배열에 난수를 생성하여 대입
                    value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
            }else{
                // 난수가 아닐 경우
                int numsize = Integer.parseInt(nums);

                value = new int[numsize];	                // 배열을 생성한다.
                for (int i = 0; i < value.length; i++)	        // 한줄씩 입력받아 배열원소로 대입
                value[i] = Integer.parseInt(br.readLine());
            }

            // 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
            while (true){
                int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

                String command = br.readLine();

                long t = System.currentTimeMillis();
                switch (command.charAt(0)){
                    case 'B':	// Bubble Sort
                        newvalue = DoBubbleSort(newvalue);
                        break;
                    case 'b':	// Bubble Sort Recursive
                        newvalue = BubbleRecur(newvalue, newvalue.length-1);
                        break;
                    case 'I':	// Insertion Sort
                        newvalue = DoInsertionSort(newvalue);
                        break;
                    case 'i':	// Insertion Sort Recursive
                        newvalue = InsertionRecur(newvalue, newvalue.length-1);
                        break;
                    case 'H':	// Heap Sort
                        newvalue = DoHeapSort(newvalue);
                        break;
                    case 'M':	// Merge Sort
                        newvalue = DoMergeSort(newvalue, 0, newvalue.length-1);
                        break;
                    case 'Q':	// Quick Sort
                        newvalue = DoQuickSort(newvalue, 0, newvalue.length-1);
                        break;
                    case 'R':	// Radix Sort
                        newvalue = DoRadixSort(newvalue);
                        break;
                    case 'X':
                        return;	// 프로그램을 종료한다.
                    default:
                        throw new IOException("잘못된 정렬 방법을 입력했습니다.");
                }
                if (isRandom){
                    // 난수일 경우 수행시간을 출력한다.
                    System.out.println((System.currentTimeMillis() - t) + " ms");
                }else{
                    // 난수가 아닐 경우 정렬된 결과값을 출력한다.
                    for (int i = 0; i < newvalue.length; i++){
                        System.out.println(newvalue[i]);
                    }
                }
            }
        }
        catch (IOException e){
            System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] swap(int[] arr, int idx1, int idx2){
        int temp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = temp;

        return arr;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoBubbleSort(int[] value){
        // 1. general
        for (int last = value.length -1; last > 0; last--){
            for (int i = 0; i < last; i ++){
                if (value[i] > value[i+1]) swap(value, i, i+1);
            }
        }
        return value;
    }

    private static int[] BubbleRecur(int[] value, int lastIdx){
        //2. recursive. -> stackoverflow error 생긴다. java stack 작고 소즁해..
        if (lastIdx == 0) return value;
        for (int i = 0; i < lastIdx; i++){
            if (value[i] > value[i+1]) swap(value, i, i+1);
        }
        BubbleRecur(value, lastIdx -1);
        return value; //의미없음
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoInsertionSort(int[] value){
        // 1. general
        for (int i = 1; i < value.length; i++){
            int loc = i-1;
            int item = value[i]; // 편한 덮어쓰기 위해서.
            while (loc >= 0 && value[loc] > item){
                value[loc+1] = value[loc--]; // 한 칸씩 뒤로 미는거니까 덮어쓰는 위치도 변해야 함. i말고 loc+1써야함.
            }
            value[loc+1] = item;
        }
        return value;
    }

    private static int[] InsertionRecur(int[] value, int lastIdx){
        //2. recursive. -> stackoverflow error 생긴다. java stack 작고 소즁해..
        if (lastIdx == 0) return value;
        // recur은 자신보다 몸집 작은 애들에게 동일한 절차를 실행하는 것.
        // unsorted가 n-1되는 걸 기준으로 해야하므로 일단 원소 1개일 때까지 분할.
        InsertionRecur(value, lastIdx-1);

        int item = value[lastIdx];
        int loc = lastIdx-1;
        while (loc >= 0 && item < value[loc]){
            value[loc+1] = value[loc--];
        }
        value[loc+1] = item;
        return value;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoHeapSort(int[] value){
        //1. build heap
        buildheap(value);
        //2. sorting
        int i = value.length-1;
        while (i > 0){
            swap(value, 0, i);
            heapify(value, 0, i-1);
            i--;
        }
        return (value);
    }
    private static void buildheap(int[] value){
        //1. leaf있는 node중 가장 나중 node부터 시작
        for (int i = (value.length-2)/2; i >= 0; i--){
            heapify(value, i, value.length-1);
        }
    }
    private static void heapify(int[] value, int node, int last){ // last 잘 넣어줘야 함.
        int left = 2 * node + 1;
        int right = 2 * node + 2;
        int bigger;

        if (right <= last){ // left, right 둘 다 있는 경우
            bigger = value[left] > value[right] ? left : right;
        }else if (left <= last){ // left만 있는 경우
            bigger = left;
        }else { return; }

        if (value[node] < value[bigger]){
            swap(value, node, bigger);
            heapify(value, bigger, last);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoMergeSort(int[] value, int begin, int end){
        if (begin >= end) return value;

        int mid = (begin + end)/2;
        // 실질적으로는 index만 옮기는 작업임.
        DoMergeSort(value, begin, mid);
        DoMergeSort(value, mid+1, end);
        return Merge(value, begin, mid+1, end);
    }
    private static int[] Merge(int[] value, int arr1, int arr2, int end2){
        int[] result = new int[end2 - arr1 + 1]; // merge에는 결과를 담을 배열이 따로 필요함.
        int rIdx = 0;
        int valIdx = arr1;
        int end1 = arr2-1;

        // 1. 두 배열 다 끝에 도달하지 않았을 경우
        while (arr1 <= end1 && arr2 <= end2){ // arr2는 유동적이라 기준으로 삼으면 안 됨.
            if (value[arr1] > value[arr2]){ result[rIdx++] = value[arr2++]; }
            else{ result[rIdx++] = value[arr1++]; }
        }
        // 2. 왼쪽 배열만 남았을 경우. arr2는 postfix여서 end+1인 상태임.
        while (arr1 <= end1){ result[rIdx++] = value[arr1++]; }
        // 3. 오른쪽 배열만 남았을 경우
        while (arr2 <= end2){ result[rIdx++] = value[arr2++]; }
        // 4. merge한 부분 배열을 본 배열에 담는다.
        for (int i = 0; i < result.length; i++){ value[valIdx++] = result[i]; }
        return value;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoQuickSort(int[] value, int begin, int end){
        /*
        // ver1. temp에 정렬 후 value에 다시 옮기는 방법. stackoverflow error.

        // 1. 기준 잡고, 작은 건 왼쪽, 큰 건 오른쪽으로 정렬한다.
        if (begin >= end) return value;
        int[] result = new int[end - begin + 1];
        int pivotIdx = (begin+end)/2;
        int l = 0, r = result.length-1;

        for (int i = begin; i < result.length; i ++){
            if (i == pivotIdx) continue;
            if (value[i] <= value[pivotIdx]){ result[l++] = value[i]; }
            else { result[r--] = value[i]; }
        }
        result[r] = value[pivotIdx];

        int j = 0;
        for (int i = begin; i <= end; i++){ value[i] = result[j++]; }

        // 2. 양쪽 각각 quicksort한다.
        DoQuickSort(value, begin, r-1);
        DoQuickSort(value, r+1, end);
        return value;
        */

        // ver2. swap통해서 정렬한다.
        //------------------------------------------------------
        //   small    | [b]  big     | [u] unsorted   | pivot |
        // -----------------------------------------------------
        if (begin >= end) return value;
        int u = begin;      // unsorted의 시작 idx
        int b = begin;     // pivot보다 big인 원소 시작 idx. small 갯수에 영향받음.
        int pivot = value[end];

        //1. bigger, smaller 나눈다.
            //1. [u] > pivot -> b stay, u++
            //2. [u] < pivot -> [u] <-> [b-1], b++, u++
            //3. pivot은 어느 idx를 잡든 맨 마지막으로 보낸다. 편의상 마지막 idx를 pivot으로 함.
        while (u < end){
            if (value[u] < pivot){
                swap(value, u, b);
                b++;
            }
            u++;
        }
        swap(value, u, b); // u == end 되면 멈추고 pivot<->[b]

        DoQuickSort(value, begin, b-1);
        DoQuickSort(value, b+1, end);
        return value;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int[] DoRadixSort(int[] value){
        //1. 10진수를 기준으로 함. int의 범위는 10진수 10자리가 최대.
        //-- 가장 큰 입력이 몇자리까지 있는지 찾는 것보다 10자리까지 그냥 도는 게 낫다.

        // 음수, 양수 다 다루어야 함. 각 자리수 loop 돌 때 modulo 한 값이 cnt에 들어감.
        // ex. -35 % 10 = -5, 25 % 10 = 5, 나오면 각 값에 10을 더함.
        //          => [5]  ,  [15] 에 저장이 된다. [0]은 사용되지 않음.
        int[] cnt = new int[20];
        int[] temp = new int[value.length];
        int[] dgt_cntIdx = new int[value.length];

        //2. 일의자리부터 radix sorting한다. 
        //-- Big-Theta(n)을 보장하기 위해 counting sort를 사용함.
        for (int p = 0; p < 10; p++){
            Arrays.fill(cnt, 0); // loop 돌 때마다 초기화해줘야 함. 
            for (int i = 0; i < value.length; i++){
                dgt_cntIdx[i] = value[i] / (int)Math.pow(10, p) % 10 + 10;
                // digit + 10(진수)을 cnt의 index로 삼아서 cnt를 증가시킨다.
                cnt[dgt_cntIdx[i]]++;
            }
            //3. counting sort에서 각 원소를 누적시킨다.
            for (int i = 0; i < cnt.length-1; i++){
                cnt[i+1] += cnt[i];
            }

            //4. **stable sorting**을 위해 value의 뒷index부터 temp에 옮겨담는다.
            for (int i = value.length-1; i >= 0; i--){
                temp[--cnt[dgt_cntIdx[i]]] = value[i];
            }
            //5. temp->value. 한 자리수라면 4에서 바로 value에 넣어도 됐는데 value의 original 값을 살려야하므로 일단 temp에 옮긴다.
            for (int i = 0; i < value.length; i++){
                value[i] = temp[i];
            }
        }

        return (value);
    }
}

