package Window;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import clientUDP.Paquet;

public class UdpWindowReceiver {

    private DatagramSocket socket;
    private boolean                         running = true;
    private byte []                         buf = new byte[1500];
    private int []                          numSequence= {0,1,2,3};
    private int                             numWaiting = 0;
    private Paquet                          paquet = null;
    private ArrayList<Paquet>               paquets= new ArrayList<Paquet>();

    
    public UdpWindowReceiver(String filename, DatagramSocket so) {
        this.socket = so;
  
        download(filename);     
    }
    
    /**
     * Recoit les donnees du client
     */
    private void download(String filename) {

        byte[] reponseByte= new byte[1];
        boolean estOrdonne = true;
        
        
        while (running) 
        {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);    
            
            try
            {
                 socket.receive(packet); 

                 System.out.println(packet.getLength());
                 System.out.println(packet.getData()[0]);
                 
                 paquet=(Paquet) Paquet.getObject(packet.getData());                  
                 reponseByte[0]=  numWaiting==0? (byte)0:(byte)(numWaiting-1); 
                 
                 if(estOrdonne)
                 {
                     if(isWaiting(paquet))
                     {                
                         reponseByte[0]=(byte) paquet.getSequence();  
                     }
                     else 
                     {       
                         estOrdonne=false;
                     }
                 }
                 socket.send(new DatagramPacket(reponseByte,reponseByte.length,packet.getAddress(),packet.getPort()));   
                 if(paquet.isLastPaquet())
                 {
                    numWaiting=0;
                    break;
                 }
             }
             catch (IOException e)
             {               
                 e.printStackTrace();
             }   
             catch (ClassNotFoundException e)
             {            
                 e.printStackTrace();
             }
        }
        socket.close(); 
        writeFile(filename);
    }

    /**
     * Verifie si le numero de sequence attendu est identique au numero de sequence re�u
     * @return boolean
     */
    private boolean isWaiting(Paquet paquet)
    {   
        if(paquet.getSequence()==numWaiting)
        {
            numWaiting= numSequence[(++numWaiting)%numSequence.length];            
            paquets.add(paquet);
            return true;
        }  
            return false;        
    }
    
    /**
     * Ecrit le contenu du fichier re�u
     * @param fileName nom du fichier re�u
     */
    private void writeFile(String filename) {
        System.out.println("écriture du fichier...");
        FileOutputStream fs;
        
        try
        {
            fs = new FileOutputStream(filename);
            
            for (Paquet paquet : paquets)
            {    
                fs.write(paquet.getData(),0,paquet.getData().length);
                fs.flush();      
            }
                fs.close();
        }
        catch (IOException e)
        {
            System.err.println("Can't write file into specified location");
            e.printStackTrace();
        }
        
    }
       
    
}

