/**
 * @author ZhangJie
 */
// package edu.princeton.cs.algs4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;   


public class Outcast {
    private final WordNet wordnet;
    
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new NullPointerException("The argument is required");
        this.wordnet = wordnet;
    }
    
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) throw new NullPointerException("null argument");
        if (nouns.length < 2) throw new IllegalArgumentException("Number of nouns must be great than or equal to 2");
        int dist = 0;
        int index = 0;        
        for (int i = 0; i < nouns.length; i++) {
            int d = 0;
            for (int j = 0; j < nouns.length; j++) {
                d += wordnet.distance(nouns[i], nouns[j]);
            }
            if (d > dist) {
                dist = d;
                index = i;
            }
        }
        return nouns[index];    
    }
    
    // see test client below
    public static void main(String[] args) { 
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}