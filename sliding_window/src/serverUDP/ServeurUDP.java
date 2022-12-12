package serverUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Window.UdpConst;


public class ServeurUDP extends Thread{
	
   private DatagramSocket 			socket;
   private boolean 					running = true;
   private byte [] 					buf = new byte[1500];
   private  int 					port = 65500;

   
   /**
    * Constructeur
    */
   public ServeurUDP() throws SocketException{
       socket= new DatagramSocket(port);
   }  
   
   
   /**
    * Server en écoute continuelle
    */
   public void run() {
       System.out.println("Demarrage du serveur");
       while (running) 
       {
           DatagramPacket packet= new DatagramPacket(buf,buf.length);         
		   try
		   {
			   //Listen for request
			   socket.receive(packet);

			   int downloadOrUpload= packet.getData()[0];

			   
			   //Send Ack to Client
               byte[] response = {1};
               socket.send(new DatagramPacket(response,response.length,packet.getAddress(), packet.getPort()));
               
               // send or receive filename
               socket.receive(packet);
               
               String filename= new String (packet.getData(), 0,packet.getLength());
               System.out.println(filename);

               
			   //Download
			   if( downloadOrUpload==UdpConst.UPLOAD) 
			   {                    				   
				   new UdpThreads(filename,UdpConst.UPLOAD,packet);
				}
			   //Upload
			   else if(downloadOrUpload==UdpConst.DOWNLAOD)
			   {				       
				   new UdpThreads(filename,UdpConst.DOWNLAOD,packet);	
			   }
		   }
		   catch (IOException e)
		   {
			        e.printStackTrace();
		   }  
       }
   }
}
