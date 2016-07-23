/**
 * @author ZhangJie
 */
// package edu.princeton.cs.algs4;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
// import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
// import java.util.NoSuchElementException;

public class WordNet { 
//    private final int INFINITY = Integer.MAX_VALUE;
    private final int N;
    private final Digraph wndg;
    private final ArrayList<String> strlist;
    private final RedBlackBST<String, Bag<Integer>> rbbst;
    private final SAP wnsap;
   
    
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new NullPointerException("the argument(s) is required.");
        this.rbbst = new RedBlackBST<String, Bag<Integer>>();        
        this.strlist = new ArrayList<String>();
        int count = 0;
        In synIn = new In(synsets);
        while (synIn.hasNextLine()) {
            String[] linestr = synIn.readLine().split(",");            
            int index = Integer.parseInt(linestr[0]);
            strlist.add(linestr[1]);
            String[] str = linestr[1].split(" ");
            for (int i = 0; i < str.length; i++) {
                if (rbbst.get(str[i]) == null) {
                    Bag<Integer> val = new Bag<Integer>();
                    val.add(index);
                    rbbst.put(str[i], val);
                } else {
                   Bag<Integer> val = rbbst.get(str[i]);
                   val.add(index);
                   rbbst.put(str[i], val);
                }
            }
            count++;
        }    
        
        this.N = count;        
        this.wndg = new Digraph(N);   
        
        In hypIn = new In(hypernyms);
        while (hypIn.hasNextLine()) {
            String[] linestr = hypIn.readLine().split(",");
            for (int i = 0; i < linestr.length - 1; i++) {
                wndg.addEdge(Integer.parseInt(linestr[0]), Integer.parseInt(linestr[i+1]));
            }
        }  
        // check whether the Digraph is a rooted DAG
        count = 0;
        for (int i = 0; i < wndg.V(); i++) {
            if (wndg.outdegree(i) == 0) count++;
        }
        if (count > 1) throw new IllegalArgumentException("the Graph should be rooted");
        Topological sapTopo = new Topological(wndg);
        if (!sapTopo.hasOrder()) throw new IllegalArgumentException("the Graph should be a DAG.");
    
        this.wnsap = new SAP(wndg);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        // if structure is tree, how to return a iterable nouns?
        return rbbst.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new NullPointerException("the argument is required.");
        return rbbst.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new NullPointerException("the argument(s) is required.");
        if (!(isNoun(nounA) && isNoun(nounB))) throw new IllegalArgumentException("noun(s) not in WordNet.");       
        Bag<Integer> valueA = rbbst.get(nounA);
        Bag<Integer> valueB = rbbst.get(nounB);      
        
//        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(wndg, valueA);
//        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(wndg, valueB);
//        for (int x = 0; x < wndg.V(); x++) {           
//            if (bfs_v.hasPathTo(x) && bfs_w.hasPathTo(x)) {
//                int d = bfs_v.distTo(x) + bfs_w.distTo(x);
//                if (d < len) {
//                    len = d;
//                }
//            }
//        }       
        return wnsap.length(valueA, valueB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new NullPointerException("the argument(s) is required.");
        if (!(isNoun(nounA) && isNoun(nounB))) throw new IllegalArgumentException("noun(s) not in WordNet.");       
        Bag<Integer> valueA = rbbst.get(nounA);
        Bag<Integer> valueB = rbbst.get(nounB);  
        int ca = -1;
        ca = wnsap.ancestor(valueA, valueB);
        if (ca == -1) {
            return null;
        } else {
            return strlist.get(ca);
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // WordNet wordnet = new WordNet(args[0], args[1]);
        // todo
    }
}