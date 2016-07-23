/**
 * @author ZhangJie
 */
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.FlowEdge;

public class BaseballElimination {
    private static final double INFINITY = Double.POSITIVE_INFINITY;
    private static final double FLOATING_POINT_EPSILON = 1E-11;
    private final int N;
    private final RedBlackBST<String, Integer> teamBST;
    private final String[] teamName;
    private final int[] win;
    private final int[] lose;
    private final int[] remain;
    private final int[] g;

     /** 
       * Create a baseball division from given filename in format specified below
       * 
       * @param filename to read in
       */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        this.N = Integer.parseInt(in.readLine());
        teamName = new String[N];
        teamBST = new RedBlackBST<String, Integer>();
        win = new int[N];
        lose = new int[N];
        remain = new int[N];
        g = new int[N * (N - 1) / 2];
        
        int i = 0;
        while (in.hasNextLine()) {
            String lineIn = in.readLine();
            String[] line = lineIn.trim().split("\\s+");
            teamBST.put(line[0], i);
            teamName[i] = line[0];
            win[i] = Integer.parseInt(line[1]);
            lose[i] = Integer.parseInt(line[2]);
            remain[i] = Integer.parseInt(line[3]);
            for (int j = i + 1; j < N; j++) {
                // g[i][j] (i < j) -> i*N - i(i + 1)/2 + j - (i + 1)
                g[i * N - (i + 1) * (i + 2) / 2 + j] = Integer.parseInt(line[4 + j]);
            }
            i++;
        }      
    }
    
    /** 
      * Returns the number of teams
      * 
      * @return the number of teams in this division
      */
    public int numberOfTeams() {
        return N;
    }
    
    /** 
      * Returns all teams
      * 
      * @return an Iterable Sting 
      */
    public Iterable<String> teams() {
        return teamBST.keys();
    }
    
    /**
     * Return the number of wins for given team
     * 
     * @param team the name of a team
     * @return the number of wins for the team
     */
    public int wins(String team) {
        validateTeam(team);
        return win[teamBST.get(team)];
    }
    
    /**
     * Return the number of losses for given team
     * 
     * @param team the name of a team
     * @return the number of losses for the team
     */
    public int losses(String team) {
        validateTeam(team);
        return lose[teamBST.get(team)];
    }
    
    /**
     * Return the number of remaining games for given team
     * 
     * @param team the name of a team
     * @return the number of remaining games for the team
     */
    public int remaining(String team) {
        validateTeam(team);
        return remain[teamBST.get(team)];
    }
    
    /** 
     * number of remaining games between team1 and team2
     * 
     * @param team1 the name of a team
     * @param team2 the name of a team
     * @return the number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        if (team1.equals(team2)) return 0;
        int i = teamBST.get(team1);
        int j = teamBST.get(team2);
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        return g[i * N - (i + 1) * (i + 2) / 2 + j];
    } 
     
    /*
     * is given team eliminated?
     * 
     * @param team the name of a team
     * @return <tt>true</tt> if the team is elinimated else <tt>false</tt>
     */
    public boolean isEliminated(String team) {    
        validateTeam(team);        
        // trivial elimination
        int wr = wins(team) + remaining(team);
        for (String x : teams())
            if (!x.equals(team) && wr < wins(x)) return true;     
        // nontrivial elimination
        FlowNetwork fnw = flowNetwork(team);
        FordFulkerson ff = new FordFulkerson(fnw, 0, N * (N - 1) / 2 + 1);      
        double capacity = 0;
        for (FlowEdge e : fnw.adj(0))
            capacity += e.capacity();
        return Math.abs(capacity - ff.value()) > FLOATING_POINT_EPSILON;
    }
    
    /*
     * subset R of teams that eliminates given team; null if not eliminated
     * 
     * @param team the name of a team
     * @return subset R of teams that eliminates givea team; <tt>null</tt> if not eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        Queue<String> queue = new Queue<String>();
        // trivial elimination 
        int wr = wins(team) + remaining(team);
        for (String x : teams()) {
            if (!x.equals(team) && wr < wins(x)) {
                queue.enqueue(x);
                return queue;
            }
        }
        // nontrivial elimination  
        // construct flow network
        FlowNetwork fnw = flowNetwork(team);
        FordFulkerson ff = new FordFulkerson(fnw, 0, N * (N - 1) / 2 + 1);
        for (int i  = 0; i < N - 1; i++) {
            if (ff.inCut(i + (N - 1) * (N - 2) / 2 + 1)) {
                if (i >= teamBST.get(team)) queue.enqueue(teamName[i + 1]);
                else queue.enqueue(teamName[i]);
            }
        }
        if (queue.isEmpty()) return null;
        else return queue;
    }

    // validate the team name
    private void validateTeam(String team) {
        if (team == null) 
            throw new NullPointerException("a team name is required.");
        if (!teamBST.contains(team)) 
            throw new IllegalArgumentException("team not in this division.");       
    }
    
    // construct a flow network
    private FlowNetwork flowNetwork(String team) {
        FlowNetwork fnw = new FlowNetwork(N * (N - 1) / 2 + 2);
        // add edge from s to match
        int x = teamBST.get(team);
        String [] match = new String[(N - 1) * (N - 2) / 2];
        int count = 0;
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (i != x && j != x) {
                    match[count] = i + "-" + j;
                    count++;
                    // add flowedge
                    FlowEdge e = new FlowEdge(0, count, (double) g[i * N - (i + 1) * (i + 2) / 2 + j]);
                    fnw.addEdge(e);
                }
            }
        }
        // add edge from match to team
        for (int i = 0; i < match.length; i++) {
            String[] t = match[i].split("-");
            int t1 = Integer.parseInt(t[0]);
            int t2 = Integer.parseInt(t[1]); 
            if (t1 >= x) t1 = t1 - 1;
            if (t2 >= x) t2 = t2 - 1;
            FlowEdge e1 = new FlowEdge(i + 1, t1 + (N - 1) * (N - 2) / 2 + 1, INFINITY);
            FlowEdge e2 = new FlowEdge(i + 1, t2 + (N - 1) * (N - 2) / 2 + 1, INFINITY);
            fnw.addEdge(e1);
            fnw.addEdge(e2);
        }
        // add edge from team to t
        for (int i = 0; i < N - 1; i++) {
            int v, t, ti;
            int flow;
            v = i + (N - 1) * (N - 2) / 2 + 1;
            t = N * (N - 1) / 2 + 1;
            if (i < x) ti = i;
            else ti = i + 1;
            flow = win[x] + remain[x] - win[ti]; 
            FlowEdge e = new FlowEdge(v, t, (double) flow);
            fnw.addEdge(e);
        }
        return fnw;
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}