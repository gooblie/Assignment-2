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

    public List<Segment> findShortestPath(Node start, Node goal, Function<Segment, Double> costFunction, Function<Node, Double> heuristic) {
        PriorityQueue<SearchNode> open = new PriorityQueue<>(11, (o1, o2) -> Double.compare(o1.getF(), o2.getF()));
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
                    double cost = costFunction.apply(edge);
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

    public Set<Node> findArtPnts(){
        Set<Node> artPnts = new HashSet<>();
        counts = new HashMap<>();
        reachback = new HashMap<>();
        children = new HashMap<>();
        for (Node n: graph.nodes.values()) {
            counts.put(n, Integer.MAX_VALUE);
        }
        Collection<Node> unvisited = new HashSet<Node>(graph.nodes.values());
        while(!unvisited.isEmpty()){
            int numSubTrees = 0;
            Node root = getRandomNode(unvisited);
            unvisited.remove(root);
            counts.put(root, 0);
            for (Node n: root.getChildren()) {
                if (counts.get(n) == Integer.MAX_VALUE){
                    artPnts.addAll(iterArtPts(n, 1, root, unvisited));
                    numSubTrees++;
                }
            }
            if(numSubTrees>1){artPnts.add(root);}
        }
        return artPnts;


    }

    public Set<Node> iterArtPts(Node firstNode, int count, Node root, Collection<Node> unvisted){
        Set<Node> artPnts = new HashSet<>();
        Stack<APNode> stack = new Stack<>();
        stack.push(new APNode(firstNode, count, root));
        while(!stack.isEmpty()){
            APNode current = stack.peek();
            if(counts.get(current.getNode()) == Integer.MAX_VALUE){
                counts.replace(current.getNode(), current.getCount());
                reachback.put(current.getNode(), current.getCount());
                children.put(current.getNode(), current.getNode().getChildren());
            }else if(!children.get(current.getNode()).isEmpty()){
                Node child = (Node)children.get(current.getNode()).toArray()[0];
                children.get(current.getNode()).remove(child);
                if(counts.get(child)<Integer.MAX_VALUE){
                    reachback.replace(current.getNode(), Math.min(reachback.get(current.getNode()), counts.get(child)));
                }else{
                    stack.push(new APNode(child, current.getCount()+1, current.getNode()));
                }
            }else{
                if(!current.getNode().equals(firstNode)){
                    reachback.replace(current.getParent(), Math.min(reachback.get(current.getNode()), reachback.get(current.getParent())));
                    if(reachback.get(current.getNode()) >= counts.get(current.getParent())){
                        artPnts.add(current.getParent());
                    }

                }
                stack.remove(current);
                unvisted.remove(current.getNode());
            }
        }
        return artPnts;
    }


    public int recArtPts(Node node, int count, Node parent, Collection<Node> artPnts){
        counts.replace(node, count);
        int reachBack = count;
        for (Node neighbor: node.getChildren()) {
            if(!neighbor.equals(parent)){
                if(counts.get(neighbor)<Integer.MAX_VALUE){
                    reachBack = Math.min(counts.get(neighbor), reachBack);
                }
                else{
                    int childReach = recArtPts(neighbor, count+1, node, artPnts);
                    reachBack = Math.min(childReach, reachBack);
                    if(childReach >= count) {
                        artPnts.add(node);
                    }

                }
            }
        }
        return reachBack;
    }

}
