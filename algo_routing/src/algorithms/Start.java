package algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import App.Host;
import App.Routeur;

public class Start {
	public static void main(String[] args) {
	    
	    final String numeroIp= "10.44.88.124";
	    final String nameFile= "a.txt";
	 
	    
		System.out.println("Choose your algorithm between \n"
				            + "1. Link-State \n"
				            + "2. Distance Vector\n");
		Scanner clavier = new Scanner(System.in);
		int valueAlgo= clavier.nextInt();		
		clavier.close();
		
		Routeur  r=null;
		switch (valueAlgo) {
		case 1:
			 r = new Routeur(nameFile,1);		
			break;
		case 2:
			r= new Routeur(nameFile,2);
			break;
		default:
			System.out.println("Choix non pris en compte!"+" "+r);
			break;
		};

		Host h = new Host(numeroIp);	
		
	}
	
	/**
	 * Utilitaire d'initilaisation de LS
	 */
	public static AlgorithmLS initLS(){
        NodeRouter nodeA = new NodeRouter("10.44.88.119");
        NodeRouter nodeB = new NodeRouter("10.44.88.120");
        NodeRouter nodeC = new NodeRouter("10.44.88.121");
        NodeRouter nodeD = new NodeRouter("10.44.88.122"); 
        NodeRouter nodeE = new NodeRouter("10.44.88.123");
        NodeRouter nodeF = new NodeRouter("10.44.88.124");
         
        nodeA.addDestination(nodeB, 5);
        nodeA.addDestination(nodeD, 45);
        
        nodeB.addDestination(nodeA, 5);
        nodeB.addDestination(nodeC, 70);
        nodeB.addDestination(nodeE, 3);
         
        nodeC.addDestination(nodeD, 50);
        nodeC.addDestination(nodeF, 78);
        nodeC.addDestination(nodeB, 70);
         
        nodeD.addDestination(nodeE, 8);
        nodeD.addDestination(nodeA, 45);
        nodeD.addDestination(nodeC, 50);
        
        nodeE.addDestination(nodeB, 3);
        nodeE.addDestination(nodeD, 8);
        nodeE.addDestination(nodeF, 7);
        
        nodeF.addDestination(nodeE, 7);
        nodeF.addDestination(nodeC, 78);
        
        Graph graph = new Graph();
         
        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);
        
        return new AlgorithmLS(graph, nodeA);
	}
	
}
