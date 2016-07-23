// package edu.princeton.cs.algs4;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
// import edu.princeton.cs.algs4.Topological;

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final Digraph dg;
//    private int distV;
//    private int distW;
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new NullPointerException("The argument is required.");
        this.dg = new Digraph(G);
    } 
    
    // length of shortest ancetral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
        if (w < 0 || w > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
        int x = ancestor(v, w);
        if (x == -1) 
            return -1;
        else {
             BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(dg, v);
             BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(dg, w); 
             return bfs_v.distTo(x) + bfs_w.distTo(x);
        }
    }
    
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
        if (w < 0 || w > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
        int ca = -1;
        int len = INFINITY;
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(dg, w);
        for (int x = 0; x < dg.V(); x++) {
            if (bfs_v.hasPathTo(x) && bfs_w.hasPathTo(x)) {
                int d = bfs_v.distTo(x) + bfs_w.distTo(x);
                if (d < len) {
                ca = x;
                len = d;
//                distV = bfs_v.distTo(x);
//                distW = bfs_w.distTo(x);
                }
            }
        }
        return ca;            
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        for (int x : v) {
            for (int y : w) {
                if (x < 0 || x > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
                if (y < 0 || y > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
            }
        }

        int x = ancestor(v, w);
        if (x == -1) 
            return -1;
        else {            
            BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(dg, v);
            BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(dg, w);
            return bfs_v.distTo(x) + bfs_w.distTo(x);
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        for (int x : v) {
            for (int y : w) {
                if (x < 0 || x > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
                if (y < 0 || y > dg.V() -1) throw new IndexOutOfBoundsException("Index out of bound");
            }
        }
        int ca = -1;
        int len = INFINITY;
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(dg, w);
        for (int x = 0; x < dg.V(); x++) {            
            if (bfs_v.hasPathTo(x) && bfs_w.hasPathTo(x)) {               
                int d = bfs_v.distTo(x) + bfs_w.distTo(x);
                if (d < len) {
                ca = x;
                len = d;
//                distV = bfs_v.distTo(x);
//                distW = bfs_w.distTo(x);
                }
            }
        }
        return ca;           
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
