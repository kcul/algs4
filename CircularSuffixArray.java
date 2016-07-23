/**
 * implement 3-way string quick sort
 * @author ZhangJie
 */
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private final int[] index;
    private final int N;
    
    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new NullPointerException("a string argument is required.");
        this.N = s.length();
        index = new int[N];
        for (int i = 0; i < N; i++) {
            index[i] = i;
        }
        sort(s, 0, N - 1, 0);
    }
    
    // length of s
    public int length() {
        return N;
    }
    
    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > N - 1) throw new IndexOutOfBoundsException("input should between 0 and N-1");
        return index[i];
    }    
    
    // implement 3-way string quick sort
    private void sort(String s, int lo, int hi, int d) {
        //cutoff to insertion sort for small subarrays 
        if (hi <= lo + 15) {
            insertion(s, lo, hi, d);
            return;
        }
        // compute frequency counts
        int[] count = new int[258]; // R = 256
        for (int i = lo; i <= hi; i++) {
            int c;
            if (d == N) c = -1;
            else c = s.charAt((index[i] + d) % N);
            count[c + 2]++;
        }
        // transform counts to indices
        for (int r = 0; r < 257; r++) {
            count[r + 1] += count[r];
        }
        // distribute
        int[] aux = new int[hi - lo + 1];
        for (int i = lo; i <= hi; i++) {
            int c;
            if (d == N) c = -1;
            else c = s.charAt((index[i] + d) % N);
            aux[count[c + 1]++] = index[i];
        }
        // copy back
        for (int i = lo; i <= hi; i++) {
            index[i] = aux[i - lo];
        }
        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < 256; r++) {
            sort(s, lo + count[r], lo + count[r + 1] - 1, d + 1);
        }            
    }
    // insertion sort when sort scale is small
    private void insertion(String s, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++) {
            for (int j = i; j > lo && less(s, index[j], index[j - 1], d); j--) {
                exch(j, j - 1);
            }
        }
    }
    
    private boolean less(String s, int i, int j, int d) {
        for (int k = d; k < N; k++) {
            if (s.charAt((i + k) % N) < s.charAt((j + k) % N)) return true;
            if (s.charAt((i + k) % N) > s.charAt((j + k) % N)) return false;
        }
        return false; // reach here only when the s consists only one character
    }
    
    private void exch(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }
    
    // unit testing of the methods (optional)
    public static void main(String[] args) {
        In in = new In(args[0]);
        String str = in.readString();
        str = str.trim();
        CircularSuffixArray csa = new CircularSuffixArray(str);
        int N = csa.length();
        StdOut.printf("N: %d\n", N);
        for (int i = 0; i < N; i++) {
            StdOut.printf("%d\n", csa.index(i));
        }        
    }
}