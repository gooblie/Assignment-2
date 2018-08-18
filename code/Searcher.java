import java.util.*;
import java.util.function.Function;

/**
 * The searcher class allows a graph to be searched for the lowest cost path
 * between two nodes. It also allows for articulation point nodes to be found
 *
 * @author tony
 */
public class Searcher {
    private Graph graph;

    public Searcher(Graph graph) {
        this.graph = graph;
    }

    private List<Segment> findPathFromPrev(SearchNode node){
        List<Segment> edges = new ArrayList<>();
        SearchNode currentNode = node;
        while(currentNode.getPrev()!=null){
            edges.add(node.getEdge());
            currentNode = currentNode.getPrev();
        }
        Collections.reverse(edges);
        return edges;
    }

    public List<Segment> findShortestPath(Node start, Node goal, Function<Node, Double> heuristic) {
        PriorityQueue<SearchNode> open = new PriorityQueue<>(11, new Comparator<SearchNode>() {
            @Override
            public int compare(SearchNode o1, SearchNode o2) {
                return Double.compare(o1.getF(), o2.getF());
            }
        });
        open.add(new SearchNode(start, null, null, 0, heuristic.apply(start)));
        Set<Node> closed = new HashSet<>();
        while(!open.isEmpty()){
            SearchNode node = open.poll();
            if(node.getNode().equals(goal)){
                return findPathFromPrev(node);
            }
            closed.add(node.getNode());
            Map<Segment, Node> neighbors= node.getNode().getNeighbors();
            for(Segment edge: neighbors.keySet()){
                Node neighbor = neighbors.get(edge);
                if (!closed.contains(neighbor)){
                    double cost = edge.length;
                    double g = node.getG() + cost;
                    double f = g + heuristic.apply(neighbor);
                    open.add(new SearchNode(neighbor, node, edge, g, f));
                }
            }
        }
        return null;
    }

}
