package App;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Threads.Receive;
import algorithms.Algorithm;
import algorithms.AlgorithmDV;
import algorithms.AlgorithmLS;
import algorithms.Start;

public class Routeur
{
    private static final int RECEPTION_DEFAUlT_PORT_ROUTER = 65000;
    private static final int RECEPTION_DEFAUlT_PORT_HOST = 65001;
    private static final int LINK_STATE=1;
    private static final int DISTANCE_VECTOR=2;
    
    private Map<String, InetAddress> voisin = new HashMap<>();
    private  Map<String, Integer> cost = new HashMap<>();
    private Map<String, String> table = new HashMap<>();
    private InetAddress ip;
    private Algorithm algoRouteur;
	private Receive threadReceive;
	private String name;
 
    public Routeur(String config, int typeAlgo) {
        this.init(config); 
        if(typeAlgo==LINK_STATE) {
            algoRouteur= Start.initLS();
            algoRouteur.calculate(table);
            printTable();
        }
        else if(typeAlgo==DISTANCE_VECTOR)
        {
 
            algoRouteur =  new AlgorithmDV(cost,name);
            algoRouteur.calculate(table); 
            ((AlgorithmDV) algoRouteur).print();
        }
  
        try
        {
            threadReceive = new Receive(RECEPTION_DEFAUlT_PORT_ROUTER,this);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }

    }
    
    /**
     * Affiche la table de routage
     */
    public void printTable() {
        System.out.println("----------------------------------------------------");
        System.out.println("  \tDestination\t   |      Next Router\t            ");
        System.out.println("----------------------------------------------------");
        for (Map.Entry<String, String> it : table.entrySet())
        {
            System.out.println("\t" + it.getKey() + "       |      " + it.getValue());
        }
        System.out.println("----------------------------------------------------");
    }
    
    /**
     * Init le routeur avec un fichier de config
     * @param config
     * @param algoRouteur2 
     */
    private void init(String config)
    {
        File fs = new File(config);
        try
        {
            Scanner s = new Scanner(fs);
            
            // this router configuration
            String router = s.nextLine();
            String conf[] = router.split(" ");
            ip = InetAddress.getByName(conf[0]);
            name = conf[0];

            // Neiighbor configuration
            while(s.hasNext()) {
                String line = s.nextLine();
                String res[] = line.split(" ");
                voisin.put(res[0], InetAddress.getByName(res[0]));
                cost.put(res[0], Integer.valueOf(res[1]));
            }
            
            s.close();
              
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * The thread listener parse us the receive Data
     * 
     * * @param msg
     */
    public void receive(Message msg) {

        System.out.println( "This routeur Has received a message (" + this.ip.getHostAddress() + ")" );
        System.out.println( "Message: " + msg.getMessage() + "\n" );
        System.out.println( "Destination : " + msg.getDestination() );

        if(msg.getDestination().equals(name)) {
            try
            {
                send(msg, InetAddress.getByName(msg.getDestination()),RECEPTION_DEFAUlT_PORT_HOST);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if(algoRouteur instanceof AlgorithmLS) {
                String v = table.get(msg.getDestination());
                send(msg, voisin.get(v), RECEPTION_DEFAUlT_PORT_ROUTER);
            }
            
            if(algoRouteur instanceof AlgorithmDV) {
                send(msg, ((AlgorithmDV) algoRouteur).getShortest(msg.getDestination()), RECEPTION_DEFAUlT_PORT_ROUTER);
            }
        }
    }
    
    /**
     * Send message to another rooter
     * @param msg
     * @param adr
     */
    public void send(Message msg, InetAddress adr, int port){
        byte [] buf = new byte[15000];
        try
        {
            DatagramSocket socket = new DatagramSocket();
            buf = msg.convertToByte();
            DatagramPacket packet  = new DatagramPacket(buf, buf.length, adr, port);
            socket.send(packet);
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    
    
    /**
     * Getter and setter
     * @return
     */
    public InetAddress getAddress() {
        return ip;
    }
    
    
    
}
