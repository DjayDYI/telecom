package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import App.Host;
import App.Message;
import App.Routeur;

public class Receive extends Thread{
    
    private DatagramSocket           socket;
    private boolean                  running = true;
    private byte []                  buf = new byte[15000];
    private Routeur                  r = null;
    private Host                     h = null;

    
    /**
     * Constructeur
     * @param routeur 
     */
    public Receive(int port, Routeur routeur) throws SocketException{
        socket= new DatagramSocket(port);
        this.r = routeur;
        start();
    }
    
    /**
     * 
     * @param port
     * @param host
     * @throws SocketException
     */
    public Receive(int port, Host host) throws SocketException{
        socket= new DatagramSocket(port);
        this.h = host;
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
                //Listen for request
                socket.receive(packet);
                byte[] msgByte= packet.getData();
                try 
                {       
                    Message msg=  Message.getObject(msgByte);
                    
                    if(r != null)
                        r.receive(msg);
                    
                    if(h != null)
                        h.receive(msg);
                    
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
