package App;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Threads.Receive;

public class Host implements HostUtil{

	private static Message message;
	private InetAddress ip;
	
	/**
	 * This constructor initialize a host to receive message
	 * @param _adr
	 */
	public Host(String _adr) {
	    this.init(_adr);
		try
        {
		    Receive t = new Receive(65001, this);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
	}
	
	/**
	 * This constructor initialize a host that send a message
	 * @param _adr
	 * @param _dest
	 * @param r
	 */
	public Host(String _adr, String _dest, Routeur r) {
	    this.init(_adr);   
	    message = new Message("Hello World!", _dest);
	    this.send(r);
	}
	
	/**
	 * Initialize host with IP
	 * @param _adr
	 */
	public void init(String _adr) {
       try
        {
            ip = InetAddress.getByName(_adr);
        }
        catch (UnknownHostException e)
        {
            System.out.println("Ip bad format!");
        }
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

	/**
	 * Fucntion that print message receive by Host
	 * @param msg
	 */
    public void receive(Message msg)
    {
        System.out.println("Le host a recu le message");
        System.out.println("Message : " + msg.getMessage());
    }
	
}
