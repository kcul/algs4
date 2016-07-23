/**
 * @author ZhangJie
 */
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Bag;

import java.util.Iterator;

public class BoggleSolver {
    private final MyTrie<Boolean> dictst;
    
    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     * (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
     * @param dictionary as an array of String
     */
    public BoggleSolver(String[] dictionary) {
        this.dictst = new MyTrie<Boolean>();
        for (int i = 0; i < dictionary.length; i++) {
            dictst.put(dictionary[i], true);
        }
    }
    
    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     * @param board a BoggleBoard
     * @return iterable String
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // board: m X n.
        int m = board.rows();
        int n = board.cols();
        // precompute board
        char[] cboard = new char[m * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                cboard[i * n + j] = board.getLetter(i, j);
            }
        }        
        // adjacent dice 
        Bag<Integer>[] adjbag = (Bag<Integer>[]) new Bag[m * n];
        for (int i = 0; i < m * n; i++) {
            adjbag[i] = new Bag<Integer>();
        }
        adjacentBag(adjbag, m, n);
        
        Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[m * n];
        for (int v = 0; v < m * n; v++) {
            adj[v] = adjbag[v].iterator();
        }        
        // depth-first search using an explicit stack
        boolean[] marked = new boolean[m * n];
        SET<String> set = new SET<String>();
        Stack<Integer> stack = new Stack<Integer>();
        StringBuilder prefix = new StringBuilder();
        
        // enumerate every letter as fisrt letter from the board
        for (int s = 0; s < m * n; s++) {
            stack.push(s);
            marked[s] = true;
            char ch = cboard[s];
            if (ch == 'Q') prefix.append("QU");
            else prefix.append(ch);
            
            // nonrecursive DFS
            while (!stack.isEmpty()) {
                int v = stack.peek();
                if (adj[v].hasNext()) {
                    int w = adj[v].next();
                    // backtrace 
                    ch = cboard[w];
                    if (!marked[w] && isPre(prefix, ch)) {
                        marked[w] = true;
                        stack.push(w);
                        if (ch == 'Q') prefix.append("QU");
                        else prefix.append(ch);
                        String str = new String(prefix);
                        if (str.length() > 2 && dictst.contains(str)) set.add(str);
                    }
                } else {
                    stack.pop();
                    // fade if popped out
                    marked[v] = false;
                    int len = prefix.length();
                    if (cboard[v] == 'Q') prefix.delete(len - 2, len);
                    else prefix.deleteCharAt(len - 1);
                    adj[v] = adjbag[v].iterator();
                }
            }
        }
        return set;
    }
    
    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * (You can assume the word contains only the uppercase letters A through Z.)
     * @param word a word string
     * @return the score
     */
    public int scoreOf(String word) {
        if (word == null) throw new NullPointerException("a string is required.");  
        if (!dictst.contains(word)) return 0;
        int len = word.length();
        if (len < 3) return 0;
        else if (len < 5) return 1;
        else if (len < 6) return 2;
        else if (len < 7) return 3;
        else if (len < 8) return 5;
        else return 11;
    }
    
    // return true if string prefix + c is prefix in dictionary
    private boolean isPre(StringBuilder prefix, char c) {
        if (c == 'Q') prefix.append("QU");
        else prefix.append(c);        
        String s = new String(prefix);
        boolean ispre = dictst.isPrefixExist(s);
        int len = prefix.length();
        if (c == 'Q') prefix.delete(len - 2, len);
        else prefix.deleteCharAt(len - 1);
        
        return ispre;
    }
    
    // generate adjacent bag
    private void adjacentBag(Bag<Integer>[] bag, int m, int n) {
        if (m == 1 && n == 1) return;
        else if (m == 1) {
            for (int i = 0; i < n; i++) {
                if (i == 0) bag[0].add(1);
                else if (i == n - 1) bag[i].add(n - 2);
                else {
                    bag[i].add(i - 1);
                    bag[i].add(i + 1);
                }
            }
        } else if (n == 1) {
            for (int i = 0; i < m; i++) {
                if (i == 0) bag[0].add(1);
                else if (i == m - 1) bag[i].add(m - 2);
                else {
                    bag[i].add(i - 1);
                    bag[i].add(i + 1);
                }
            }
        } else {
            for (int i = 1; i < m - 1; i++) {
                for (int j = 1; j < n - 1; j++) {
                    bag[i * n + j].add((i - 1) * n + j - 1);
                    bag[i * n + j].add((i - 1) * n + j);
                    bag[i * n + j].add((i - 1) * n + j + 1);
                    bag[i * n + j].add(i * n + j - 1);
                    bag[i * n + j].add(i * n + j + 1);
                    bag[i * n + j].add((i + 1) * n + j - 1);
                    bag[i * n + j].add((i + 1) * n + j);
                    bag[i * n + j].add((i + 1) * n + j + 1);
                }
            }
            // location (0, 0)
            bag[0].add(1);
            bag[0].add(n);
            bag[0].add(n + 1);
            // location (0, n - 1)
            bag[n - 1].add(n - 2);
            bag[n - 1].add(2 * n - 1);
            bag[n - 1].add(2 * n - 2);
            // location (m - 1, 0)
            bag[(m - 1) * n].add((m - 1) * n + 1);
            bag[(m - 1) * n].add((m - 2) * n);
            bag[(m - 1) * n].add((m - 2) * n + 1);
            // location (m - 1, n - 1)
            bag[m * n - 1].add(m * n - 2);
            bag[m * n - 1].add((m - 1) * n - 1);
            bag[m * n - 1].add((m - 1) * n - 2);
            // top
            for (int i = 1; i <= n - 2; i++) {
                bag[i].add(i - 1);
                bag[i].add(i + 1);
                bag[i].add(i + n - 1);
                bag[i].add(i + n);
                bag[i].add(i + n + 1);
            }
            // bottom
            for (int j = 1; j <= n - 2; j++) {
                bag[(m - 1) * n + j].add((m - 1) * n + j - 1);
                bag[(m - 1) * n + j].add((m - 1) * n + j + 1);
                bag[(m - 1) * n + j].add((m - 2) * n + j - 1);
                bag[(m - 1) * n + j].add((m - 2) * n + j);
                bag[(m - 1) * n + j].add((m - 2) * n + j + 1);
            }
            // left
            for (int i = 1; i <= m - 2; i++) {
                bag[i * n].add((i - 1) * n);
                bag[i * n].add((i - 1) * n + 1);
                bag[i * n].add(i * n + 1);
                bag[i * n].add((i + 1) * n);
                bag[i * n].add((i + 1) * n + 1);
            }
            // right
            for (int i = 1; i <= m - 2; i++) {
                bag[(i + 1) * n - 1].add(i * n - 1);
                bag[(i + 1) * n - 1].add(i * n - 2);
                bag[(i + 1) * n - 1].add((i + 1) * n - 2);
                bag[(i + 1) * n - 1].add((i + 2) * n - 1);
                bag[(i + 1) * n - 1].add((i + 2) * n - 2);
            }            
        }
    }
    
    // unit test
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int count = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
            count++;
        }
        StdOut.println("Score = " + score);
        StdOut.println("Count=" + count);
    }
}
