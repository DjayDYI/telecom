package Window;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;



import clientUDP.Paquet;

public class UdpWindowSender
{
    private ArrayList<Paquet>   packets = new ArrayList<Paquet>();
    private DatagramSocket      windowSocket;
    private InetAddress         ip;
    private int                 port;
    
    
    /**
     * Constructeur
     * @param _ip
     * @param _port
     */
    public UdpWindowSender(InetAddress _ip, int _port, String _file) 
    {
       this.ip = _ip;
       this.port = _port;
       
       //Create socket
       try
       {
            windowSocket = new DatagramSocket();
            start(_file);
       }
       catch (IOException e)
       {
           System.out.println("Error: Can't create socket!");
       }
       
    }
    
    /**
     * Démarre l'envoie de packet
     * @param _file
     * @throws IOException
     */
    private void start(String _file) throws IOException
    {
        
        //Split file in packet
        this.readFile(_file);
  
        // Send Content GoBackN(4)
        this.send(4);
    }
    
    /**
     * Lit le fichier et le sépare en paquet
     * @param fileName nom du fichier à envoyer
     */
    private void readFile(String fileName) {
       
        System.out.println("Reading file...");
        
        try
        {
            byte[] sendData = Files.readAllBytes(Paths.get(fileName));
            
            int seq = 0;
            byte[] tmp;
            
            // Split File in Packet
            for(int i=0; i<sendData.length; i = i + 1024)
            {  
                tmp = new byte[1024];
                for(int j=0; j<1024; j++)
                {
                    if(i+j < sendData.length)
                        tmp[j] = sendData[i+j];
                }
                
                packets.add(new Paquet(seq, tmp));
                
                if(seq == 3)
                    seq = 0;
                else
                    seq++;  
            }
            
            // Set le flag du dernier paquet
            packets.get(packets.size() - 1).setToLast();
            
            System.out.println("File succesfully read");
            
        }
        catch (IOException e)
        {
            System.out.println("Can't read file or file not existing");
        }
         
    }
    

    /**
     * Send Packet
     * @param _indexPacket
     * @return
     */
    private boolean sendPacket(int _indexPacket){

        if(_indexPacket >= packets.size())
            return false;
        
        try
        {
            System.out.println("Envoie du packet : " + _indexPacket);
            byte[] sendData = packets.get(_indexPacket).convertToByte();
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, ip, port);
            windowSocket.send(packet);
            return true;
        }
        catch (IOException e)
        {
            System.out.println("Erreur lors de l'envoie du paquet avec la sequence : " + packets.get(_indexPacket).getSequence());
            return false;
        } 
        
    }
    
    /**
     * 
     * @return la séquence qui correspond au packet Acknowledge
     * @throws SocketException 
     * @reutrn -1 s'il y a une erreur
     */
    private int receivePacket() {
        
        byte[] buffer = new byte[1];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        
        try
        {
            windowSocket.receive(packet);
            packet = new DatagramPacket(buffer, buffer.length);
            return packet.getData()[0];
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return -1;
    }
    
    /**
     * Envoie les données en packet selon l'algorithme GoBackN
     * @param _n Window size de l'algorithme GoBackN
     * @throws IOException
     */
    private void send(int _n) throws SocketTimeoutException {         
        
        System.out.println("Début de l'envoie...");
        
        int indexWindow = 0;
        boolean done = false;
        

        while(!done){
            
            int ackExpected = 0;

            try
            {
                // Envoie de packet en série
                for(int i = 0; i<_n; i++) {
                   sendPacket(indexWindow + i);
                }
                
                windowSocket.setSoTimeout(5000);
                
                  // recieve data until timeout
                    try {
                        
                        byte[] buffer = new byte[1];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        windowSocket.receive(packet);
                        packet = new DatagramPacket(buffer, buffer.length);
                        int ack = packet.getData()[0];
                        
                        if(indexWindow == packets.size()) {
                            done = true;
                            
                        } else if(ack == ackExpected && done != true) {
                            indexWindow++;
                            ackExpected++;
                        }
                        
                        buffer = new byte[1];
                        packet = new DatagramPacket(buffer, buffer.length);
                        windowSocket.receive(packet);
                        packet = new DatagramPacket(buffer, buffer.length);
                        ack = packet.getData()[0];
                        
                        if(indexWindow == packets.size()) {
                            done = true;
                            
                        } else if(ack == ackExpected && done != true) {
                            indexWindow++;
                            ackExpected++;
                        }
                        
                        buffer = new byte[1];
                        packet = new DatagramPacket(buffer, buffer.length);
                        windowSocket.receive(packet);
                        packet = new DatagramPacket(buffer, buffer.length);
                        ack = packet.getData()[0];
                        
                        if(indexWindow == packets.size()) {
                            done = true;
                            
                        } else if(ack == ackExpected && done != true ) {
                            indexWindow++;
                            ackExpected++;
                        }
                        
                        buffer = new byte[1];
                        packet = new DatagramPacket(buffer, buffer.length);
                        windowSocket.receive(packet);
                        packet = new DatagramPacket(buffer, buffer.length);
                        ack = packet.getData()[0];
                        
                        if(indexWindow == packets.size()) {
                            done = true;
                            
                        } else if(ack == ackExpected  && done != true) {
                            indexWindow++;
                            ackExpected++;
                        }
                        
                        
                    }
                    catch (SocketTimeoutException e) {
                        System.out.println("Timeout reached!!! " + e);
                    }
                    catch (IOException e){
                        System.out.println("Bullshit " + e);
                    }
                }
                
            
            catch (SocketException e1)
            {
                e1.printStackTrace();
            }  

            // Valid si c'est la fin de l'envoie
            if(indexWindow == packets.size()) {
                done = true; 
            }
            

            /*
                int ackExpected = 0;
                
                // Envoie de packet en série
                for(int i = 0; i<_n; i++) {
                    sendPacket(indexWindow + i);
                 }
            
            
                 // Reception et ajustement de la fenetre
                for(int i = 0; i < _n; i++){
                    if(indexWindow == packets.size()) {
                        done = true;
                        
                    } else if(receivePacket() == ackExpected) {
                        indexWindow++;
                        ackExpected++;
                        
                    }
                }
           */


        }
         
        
        System.out.println("Fin de l'envoie");
    }
    
    
}


