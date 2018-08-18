/**
 * The SearchNode class represents a node used in the path finding algorithm.
 * It contains references to previous nodes and segments connecting  as well as information about the current
 * cost from the starting node(g) and estimated total cost(f)
 *
 * @author Michael Henrys
 */
public class SearchNode {

    private Node node;
    private SearchNode prev;
    private Segment edge;
    private double g;
    private double f;

    public SearchNode(Node node, SearchNode prev, Segment edge, double g, double f) {
        this.node = node;
        this.prev = prev;
        this.edge = edge;
        this.g = g;
        this.f = f;
    }

    public Node getNode() {
        return node;
    }

    public SearchNode getPrev() {
        return prev;
    }


    public Segment getEdge() {
        return edge;
    }

    public double getG() {
        return g;
    }

    public double getF() {
        return f;
    }

}
