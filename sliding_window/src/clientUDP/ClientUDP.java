package clientUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Window.UdpWindowReceiver;
import Window.UdpWindowSender;
import Window.UdpConst;


public class ClientUDP{
    
    private static final int    SERVER_SOCKET = 65500;
    private DatagramSocket      clientSocket;
    private InetAddress         serverIP;
    private int                 port;
    private byte[]              adr;
    private String              file;
    
    
    /**
     * Constructeur
     */
    public ClientUDP(byte[] _ipServer) {
        System.out.println("Initialize client");

        // Adresse ip du serveur
        adr = _ipServer;
               
        //Get IP
        try
        {
            serverIP = InetAddress.getByAddress(adr);
        }
        catch (UnknownHostException e)
        {
            System.out.println("Error: Can't get ip or ip adress is invalid");
        }
    }
    
    /**
     * Upload un fichier au server
     * @param _file
     */
    public void upload(String _file) {
        try
        {

            // 3 Way handshake
            this.HandShake(UdpConst.UPLOAD, _file);
            
            //Crée la fenetre d'envoie
            new UdpWindowSender(serverIP,port,file);
            
            
        }
        catch (IOException e)
        {
            System.out.println("Erreur lors de l'upload");
        }
    }
    
    
    /**
     * Donwload un fichier du server
     */
    public void download(String _file) {
        try
        {
            // 3 Way handshake
            this.HandShake(UdpConst.DOWNLAOD, _file);
            
            //Crée la fenetre de réception
            new UdpWindowReceiver( _file, clientSocket);
            
        }
        catch (IOException e)
        {
            System.out.println("Erreur lors de l'upload");
        }
    }
    
    
    /**
     * 3 Way-Handshake
     * @throws IOException 
     * @parma : 0 -> demande de upload
     *          1 -> demande de download
     */
    private void HandShake(byte _updw, String _filename) throws IOException {
       
        port = SERVER_SOCKET;
        clientSocket = new DatagramSocket();
        file = _filename;
        
        System.out.println("Demande de connection au server...");
        byte[] sendData = new byte[1000];
        sendData[0] = _updw;
        
        // Send request of upload / downlaod
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverIP, port);
        clientSocket.send(packet);
        
        // Receive ACK
        clientSocket.receive(packet);
        packet = new DatagramPacket(sendData, sendData.length);
        
        // Send fileName 
        sendData = file.getBytes();
        packet = new DatagramPacket(sendData, sendData.length, serverIP, port);
        clientSocket.send(packet);
        
        // Reiceive Ack from thread and recover port for transmisson
        clientSocket.receive(packet);
        port = packet.getPort();


        System.out.println("Connection au server établie...");
    }
    
}
