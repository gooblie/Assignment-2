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
    private Map<Node, Integer> counts;
    private Map<Node, Integer> reachback;
    private Map<Node, Collection<Node>> children;

    public Searcher(Graph graph) {
        this.graph = graph;
    }

    private List<Segment> findPathFromPrev(SearchNode node){
        List<Segment> edges = new ArrayList<>();
        SearchNode currentNode = node;
        while(currentNode.getPrev()!=null){
            edges.add(currentNode.getEdge());
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

    private Node getRandomNode(Collection<Node> from) {
        Random rnd = new Random();
        int i = rnd.nextInt(from.size());
        return (Node)from.toArray()[i];
    }

    public List<Node> findArtPnts(){
        List<Node> artPnts = new ArrayList<>();
        for (Node n: graph.nodes.values()) {
            counts.put(n, Integer.MAX_VALUE);
        }
        int numSubTrees = 0;
        Node root = getRandomNode(graph.nodes.values());
        counts.replace(root, 0);
        for (Node n: root.getNeighbors().values()) {
            artPnts.addAll(iterArtPts(n, 1, root));
            numSubTrees++;
        }
        if(numSubTrees>1){artPnts.add(root);}
        return artPnts;
    }

    public List<Node> iterArtPts(Node firstNode, int count, Node root){
        List<Node> artPnts = new ArrayList<>();
        Stack<APNode> stack = new Stack<>();
        stack.push(new APNode(firstNode, count, root));
        while(!stack.isEmpty()){
            APNode current = stack.peek();
            if(counts.get(current.getNode()) == Integer.MAX_VALUE){
                counts.replace(current.getNode(), current.getCount());
                reachback.put(current.getNode(), current.getCount());
                children.put(current.getNode(), current.getNode().getNeighbors().values());
            }else if(!children.get(current.getNode()).isEmpty()){
                Node child = getRandomNode(children.get(current.getNode()));
                children.get(current.getNode()).remove(child);
                if(counts.get(child)<Integer.MAX_VALUE){
                    reachback.replace(current.getNode(), Math.min(reachback.get(current.getNode()), counts.get(child)));
                }else{
                    stack.push(new APNode(child, count+1, current.getNode()));
                }
            }else{
                if(!current.getNode().equals(firstNode)){
                    reachback.replace(current.getParent(), Math.min(reachback.get(current.getNode()), reachback.get(current.getParent())));
                    if(reachback.get(current.getNode())>= counts.get(current.getParent())){
                        artPnts.add(current.getParent());
                    }
                    stack.remove(current);
                }
            }
        }
        return artPnts;
    }

}
