/**
 * @author ZhangJie
 */
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] c = new char[256];
        int[] idx = new int[256];
        for (int i = 0; i < 256; i++) {
            c[i] = (char) i;
            idx[i] = i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            BinaryStdOut.write((char) idx[(int) ch]);
            for (int i = idx[(int) ch]; i > 0; i--) {
                c[i] = c[i - 1];
                idx[(int) c[i]]++;
            }
            c[0] = ch;
            idx[(int) ch] = 0;
        }        
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] c = new char[256];
        for (int i = 0; i < 256; i++) {
            c[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
             char ch = BinaryStdIn.readChar();
             char ctemp = c[(int) ch];
             BinaryStdOut.write(ctemp);             
             for (int i = (int) ch; i > 0; i--) {
                 c[i] = c[i - 1];
             }
             c[0] = ctemp;
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");    
    }
}