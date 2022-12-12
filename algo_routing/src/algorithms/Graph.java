package algorithms;

import java.util.HashSet;
import java.util.Set;

public class Graph {

	private Set<NodeRouter> nodes = new HashSet<>();
    
    public void addNode(NodeRouter nodeA) {
        nodes.add(nodeA);
    }

    //Getters Setters
	public Set<NodeRouter> getNodes() {
		return nodes;
	}

	public void setNodes(Set<NodeRouter> nodes) {
		this.nodes = nodes;
	}
	
	
	public String toString() {
		String list= "";
		for (NodeRouter nodeRouter : nodes) {
			list+=(nodeRouter.getName())+"->";
		}
		return list;
	}
 
}
