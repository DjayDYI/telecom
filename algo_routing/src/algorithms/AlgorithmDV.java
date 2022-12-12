package algorithms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Threads.ReceiveDV;

public class AlgorithmDV extends Algorithm
{
    private static final int RECEPTION_DEFAUlT_PORT_DV = 65002;

    private TableDV table;
    private Map<String, Integer> voisin = new HashMap<>();
    private ReceiveDV receiveThread;
    private ArrayList<String> unsynched = new ArrayList<String>();


	/*
	 * Constructeur 
	 */
    public AlgorithmDV(Map<String, Integer> _voisin,String monIp)
    {
        this.voisin = _voisin;
        try
        {
            this.receiveThread = new ReceiveDV(RECEPTION_DEFAUlT_PORT_DV, this);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        
        table = new TableDV(voisin,monIp);
        
        NotifyNeighbor();
        
    }

    /**
     * Envoie table au voisin
     */
    private void NotifyNeighbor()
    {
        for ( String key : voisin.keySet() ) {
            try
            {
                InetAddress adr = InetAddress.getByName(key);
                sendTable(adr);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void synch(Map<String, Integer> table2)
    {
        for ( String key : table2.keySet() ) {
            try
            {
                unsynched.add(key);
                sendTable(InetAddress.getByName(key));
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
         
    }
    
    
    
    /**
     * 1- Mettre a jour notre table
     * 2- Verifier si on a modifie notre table
     */
    public boolean updateTable( TableDV tableVoisin ){
        
        /*
    	boolean haveChanged=false;
    	//Pour les nouvelles destinations
    	for (String destinationVoisin : tableVoisin.getDestination()) {
			if(!table.getDestination().contains(destinationVoisin) && !destinationVoisin.equals(table.getIp())) {
				int positionVia=table.getVia().indexOf(tableVoisin.getIp());
				int dest=table.getDestination().indexOf(tableVoisin.getIp());
				
				System.out.println(table.getIp() + "   " + destinationVoisin);
				
				table.addDestination(destinationVoisin,tableVoisin.getIp(),tableVoisin.getMinimum(destinationVoisin)+table.getTable()[positionVia][dest]);
				haveChanged=true;
				unsynched.add(destinationVoisin);
			}
		}   
    	print();
    
        haveChanged=table.compareTable(tableVoisin,haveChanged);
        return haveChanged ;
        */
        
        boolean b=false;
        for (String destinationVoisin : tableVoisin.getDestination()) 
        {
            int positionVia=table.getVia().indexOf(tableVoisin.getIp()); 
            int positionDest;
            if(!destinationVoisin.equals(table.getIp())) {
                positionDest=table.getDestination().indexOf(tableVoisin.getIp());
                if(!table.getDestination().contains(destinationVoisin))
                {
                    table.addDestination(destinationVoisin,tableVoisin.getIp(), tableVoisin.getMinimum(destinationVoisin) + table.getTable()[positionDest][positionVia]);
                    b=true;
                }
                else
                {
                    positionDest=table.getDestination().indexOf(destinationVoisin);
                    int val = tableVoisin.getMinimum(destinationVoisin) + table.getTable()[positionVia][positionVia] ;
                    if( val < table.getTable()[positionDest][positionVia] ) {
                        table.changeValue( tableVoisin.getIp(),destinationVoisin, val);
                        b=true;
                    }
                }
            }
        }
       
        
        return b;
    }

    /**
     * Affiche notre table de routage
     */
    public void print()
    {
        String strHeader = "              ";
        for(int i=0; i < table.getVia().size(); i++) {
            strHeader += "|  " + table.getVia().get(i) + "  ";
        }
        System.out.println(strHeader);
        
        String strline = "";
        for(int i=0; i < table.getDestination().size(); i++) {
            strline += table.getDestination().get(i) + "  |";
            for(int j=0; j < table.getVia().size(); j++) {
                strline += String.valueOf(table.getTable()[i][j]) + "  \t\t";
            }
            System.out.println(strline);
            strline = "";
        }
    }
	
    
	/**
	 * Send table to neighbor
	 * @param r
	 */
	public void sendTable(InetAddress adr) {
        try
        {
            byte[] buf = new byte[15000];           
            DatagramSocket socket = new DatagramSocket();
            buf = table.convertToByte();
            DatagramPacket packet  = new DatagramPacket(buf, buf.length, adr, RECEPTION_DEFAUlT_PORT_DV);
            socket.send(packet);
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	/**
	 * Lorsqu'on recoit une table de routage
	 * @param rec
	 */
    public void recieve(TableDV rec)
    {
        
        print();
        
        updateTable(rec);
            NotifyNeighbor();
      
    }

    /**
     * Prendre le routeur le plus court
     * @param voisin
     * @return
     */
    public InetAddress getShortest(String voisin)
    {
        
        InetAddress ret = null;
        int min=1000000;
        String via="";
        
        for(int i=0; i<table.getDestination().size(); i++) {
            if(table.getDestination().get(i).equals(voisin)) {
                for(int j=0; j<table.getVia().size(); j++) {
                    if(table.getTable()[i][j] < min) {
                        min = table.getTable()[i][j];
                        via = table.getVia().get(j);
                    }
                }
            }
        }
        
        try
        {
            ret =  InetAddress.getByName(via);
            if(ret.getHostAddress().equals("127.0.0.1"))
            {
                return InetAddress.getByName(voisin);
            }
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        
        return ret;
    }
    

    
    @Override
    public void calculate(Map<String, String> table)
    {
        synch(voisin);
    }


	public ReceiveDV getReceiveThread() {
		return receiveThread;
	}


	public void setReceiveThread(ReceiveDV receiveThread) {
		this.receiveThread = receiveThread;
	}
	
	public TableDV getTable() {
		return table;
	}

	public  ArrayList<String> getUnsych(){
	    return unsynched;
	}


}
