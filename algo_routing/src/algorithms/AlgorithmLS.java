package algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class AlgorithmLS extends Algorithm
{
    Graph g = null;
    NodeRouter start;
    
    /**
     * Constructeur
     * @param graph
     * @param start
     */
    public AlgorithmLS(Graph graph, NodeRouter start){
        this.g = graph;
        this.start = start;
    }
    
 
    @Override
    public void calculate(Map<String, String> table)
    {
         calculateShortestPathFromSource(g, start); 
         
         for (NodeRouter r : g.getNodes())
         {
            if(r.getShortestPath().size() == 1){
                table.put(r.getName(),r.getName());
            }
            else if(r.getShortestPath().size() > 1){
                table.put(r.getName(),r.getShortestPath().get(1).getName());
            }
         }
         
    }

 
    /**
     * Cette fonction calcule le plus court chemin dans un grpah a partir d'une source donné
     * @param graph
     * @param source
     * @return
     */
    public static Graph calculateShortestPathFromSource(Graph graph, NodeRouter source) {
        source.setDistance(0);
     
        Set<NodeRouter> settledNodes = new HashSet<>();
        Set<NodeRouter> unsettledNodes = new HashSet<>();
     
        unsettledNodes.add(source);
     
        while (unsettledNodes.size() != 0) {
        	NodeRouter currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Entry < NodeRouter, Integer> adjacencyPair: 
              currentNode.getAdjacentNodes().entrySet()) {
            	NodeRouter adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }
    
    /**
     * Function qui retourne la plus petite distance
     * @param unsettledNodes
     * @return
     */
    private static NodeRouter getLowestDistanceNode(Set < NodeRouter > unsettledNodes) {
        NodeRouter lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (NodeRouter node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
    
    /**
     * Calcule la distance minimum
     * @param evaluationNode
     * @param edgeWeigh
     * @param sourceNode
     */
    private static void CalculateMinimumDistance(NodeRouter evaluationNode,Integer edgeWeigh, NodeRouter sourceNode) {
	    Integer sourceDistance = sourceNode.getDistance();
	    if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
	        evaluationNode.setDistance(sourceDistance + edgeWeigh);
	        LinkedList<NodeRouter> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
	        shortestPath.add(sourceNode);
	        evaluationNode.setShortestPath(shortestPath);
	    }
     }
   
}
