package algorithms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import App.Message;
import App.Routeur;

public class NodeRouter {
	
	private String name;
    
    private List<NodeRouter> shortestPath = new LinkedList<>();
     
    private Integer distance = Integer.MAX_VALUE;
    //NodeRouteur pour le routeur et Integer pour la distance entre ce routeur
    //et le routeur courant
    private Map<NodeRouter, Integer> adjacentNodes = new HashMap<>();
 
    public void addDestination(NodeRouter destination, int distance) {
        adjacentNodes.put(destination, distance);
    }
  
    public NodeRouter(String name) {
        this.name = name;
    }

    //Getters Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NodeRouter> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<NodeRouter> shortestPath) {
		this.shortestPath = shortestPath;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Map<NodeRouter, Integer> getAdjacentNodes() {
		return adjacentNodes;
	}

	public void setAdjacentNodes(Map<NodeRouter, Integer> adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
	}
	
	public String toString() {
		String list= "";
		for (NodeRouter nodeRouter : shortestPath) {
			list+=nodeRouter.getName()+ "->";
		}
		list+=""+name;
		return list;
	}
	
	/**
	 * Send message to a our routeur
	 * @param r
	 */
	public void send(Routeur r) {
        try
        {
            byte[] buf = new byte[15000];           
            DatagramSocket socket = new DatagramSocket();
            Message message= new Message("","");
            buf = message.convertToByte();
            DatagramPacket packet  = new DatagramPacket(buf, buf.length, r.getAddress(), 65000);
            System.out.println("Le host envoie le message : " + message.getMessage());
            socket.send(packet);
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
    
    
}
