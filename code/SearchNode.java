/**
 * The SearchNode class represents a node used in the path finding algorithm.
 * It contains references to previous nodes as well as information about the current
 * cost from the starting node(g) and estimated total cost(f)
 *
 * @author Michael Henrys
 */
public class SearchNode {

    private Node node;
    private Node prev;
    private double g;
    private double f;

    public SearchNode(Node node, Node prev, double g, double f) {
        this.node = node;
        this.prev = prev;
        this.g = g;
        this.f = f;
    }

    public Node getNode() {
        return node;
    }

    public Node getPrev() {
        return prev;
    }

    public double getG() {
        return g;
    }

    public double getF() {
        return f;
    }

}
