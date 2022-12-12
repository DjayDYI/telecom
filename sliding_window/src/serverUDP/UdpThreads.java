package serverUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Window.UdpWindowReceiver;
import Window.UdpWindowSender;

public class UdpThreads implements Runnable{
	
    private Thread 							t ;
    private final int						DOWNLOAD=0;
    private final int 						UPLOAD=1;
    private int								typeTransfert;
    private InetAddress						ipClient;
    private int 							portClient;
    private String                          filename;
    
    /**
     * Constructeur
     * @param portAvaible
     */
    public UdpThreads(String filename, int typeTransfert, DatagramPacket packet) {       
        t= new Thread(this);
        this.typeTransfert= typeTransfert;     
        this.filename=filename;
        ipClient=packet.getAddress();
        portClient=packet.getPort();

        t.start();
    }
    
    
    /**
     * 
     */
    @Override
    public void run()
    {            	
        DatagramSocket socket;
        try
        {
            // Fin du Handshake
            socket = new DatagramSocket();
            byte[] ack= {1};
            socket.send(new DatagramPacket(ack,ack.length,ipClient, portClient));
            
            //Selon le type de transfert...
            switch(typeTransfert)
            {
                case DOWNLOAD:              
                    new UdpWindowReceiver(filename,socket);
                    break;
                    
                case UPLOAD:            
                    new UdpWindowSender(ipClient,portClient,filename);
                    break;
                    
                default:                
                    System.out.println("Choix non pris en compte");         
            }                       
        }
        catch (SocketException e2)
        {
            e2.printStackTrace();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    	
    }
    
   
}
