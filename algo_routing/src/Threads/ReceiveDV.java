package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import algorithms.AlgorithmDV;
import algorithms.TableDV;

public class ReceiveDV extends Thread{
    
    private DatagramSocket           socket;
    private boolean                  running = true;
    private byte []                  buf = new byte[15000];
    private AlgorithmDV              dv;

    
    /**
     * Constructeur
     * @param routeur 
     */
    public ReceiveDV(int port, AlgorithmDV routerDV) throws SocketException{
        socket= new DatagramSocket(port);
        this.dv = routerDV;
        start();
    }
    
    
    /**
     * Server en ecoute continuelle
     */
    public void run() {
        while (running) 
        {
            DatagramPacket packet= new DatagramPacket(buf,buf.length);         
            try
            {
                
                try
                {
                    
                    do 
                    {
                
                        socket.receive(packet);
                        byte[] msgByte= packet.getData();
                        
                        if(this.dv.getUnsych().contains(packet.getAddress()))
                            this.dv.getUnsych().remove(this.dv.getUnsych().indexOf(packet.getAddress()));
                        
                        TableDV rcv =  TableDV.getObject(msgByte);
                        this.dv.recieve(rcv);
                        
                    }while(this.dv.getUnsych().size() > 0);
                    

                       
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }  
        }
    }
 }
