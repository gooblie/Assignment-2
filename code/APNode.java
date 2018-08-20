/**
 * Created by Michael on 19/08/18.
 */
public class APNode {
    private Node node;
    private int count;
    private Node parent;

    public APNode(Node node, int count, Node parent) {
        this.node = node;
        this.count = count;
        this.parent = parent;
    }

    public Node getNode() {
        return node;
    }

    public int getCount() {
        return count;
    }

    public Node getParent() {
        return parent;
    }
}
