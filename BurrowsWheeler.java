/**
 * @author ZhangJie
 */
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
//import edu.princeton.cs.algs4.StdOut;

// Burrows-Wheeler transform
public class BurrowsWheeler {
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int N = csa.length();
        int[] index = new int[N];
        char[] c = new char[N];
        for (int i = 0; i < N; i++) {
            c[i] = s.charAt(i);
            index[i] = csa.index(i);
            if (index[i] == 0)
                BinaryStdOut.write(i);
        }
        
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(c[(index[i] + N - 1) % N], 8);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        int N = s.length();
        char[] t = s.toCharArray();
        char[] c = new char[N];
        int r = 256;
        int[] count = new int[r + 1];
        for (int i = 0; i < N; i++) {
            count[((int) t[i]) + 1]++;
        }        
        for (int i = 0; i < r; i++) {
            count[i + 1] += count[i];
        }  
         int[] next = new int[N];
        for (int i = 0; i < N; i++) {
            c[count[(int) t[i]]] = t[i];
            next[count[(int) t[i]]++] = i;
        }
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(c[first]);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");  
    }
}