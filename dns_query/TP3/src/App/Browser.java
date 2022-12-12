package App;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;



import DnsProtocol.Answer;
import DnsProtocol.Query;

//https://github.com/callumdmay/java-dns-client/blob/master/src/DnsClient.java


public class Browser
{
    private static final byte[] DNS_SERVER = {8,8,8,8};
    private static final int DNS_PORT = 53;
    private static final int HTTP_PORT = 80;
    private static final int HTTPS_PORT = 403;
    
    
    public Browser() 
    {
        try
        {
            run();
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Browser App execution
     * @throws UnknownHostException
     * @throws IOException
     */
    private void run() throws UnknownHostException, IOException
    {
        System.out.print("Please enter a URL : ");
        
        // read url
        @SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
        String[] urlDict = input.nextLine().split("/");
        
        
        // LooupDns and extract IP
        byte[] ip = LookupDNS(urlDict[0]);
        HttpSession session = new HttpSession(ip, HTTP_PORT, urlDict);
        
        
        // on se casse pas trop le bicycle....
        System.out.println(session.PrintStatus());
        System.out.println(session.getPage());
        session.GetMedia();
        session.close();
        
    }
    
    private void Afficher(BufferedReader rd) throws IOException
    {
        //Information sur le statut
        String line = rd.readLine();
        String [] lines= line.split(" ");
        System.out.println();
        System.out.println(" ------------------------------------\n"
                         +"|LIGNE DE STATUT                    |\n"
                         + "|-----------------------------------|\n"
                         + "| VERSION |"+"      "+ lines[0]+"           |\n"
                         + "|-----------------------------------|\n"
                         + "| STATUT  |"+"      "+ lines[1]+"                |\n"                       
                         + " -----------------------------------| \n"
                         + "| MESSAGE |"+"      "+ lines[2]+"                 |\n"
                         + " ----------------------------------- \n");
        
        //Affichage de l'entete et du contenu 
        StringBuilder images= new StringBuilder(); 
        System.out.println("LIGNE D'ENTETE\n");
        while (rd.ready()) {
            line=rd.readLine();
            if(line.equalsIgnoreCase("<HTML>"))
                System.out.println("CONTENU \n "); 
            if(line.contains(".jpg") || line.contains(".tiff")|| line.contains("gif"))
                images.append(line.trim()+"\n");
            System.out.println(line);
        }
        System.out.println("PARTIE IMAGE YFYFYYFYFYFYFYYF ");
        System.out.println(images);
        
    }


    /**
     * Recupere l'adresse Ip  a partir du DNS 
     * Hexadecimal String that represent dns message answer
     * for a given url
     * @param url
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    private byte[] LookupDNS(String url) throws UnknownHostException, IOException
    {
        // Query Message
        Query dnsMessage = new Query(url);
        byte[] queryMessage = dnsMessage.generateQuery();
        byte[] responseMessage = new byte[1024];
        
        
        DatagramSocket socketdns = new DatagramSocket();
        DatagramPacket requestPacket = new DatagramPacket(queryMessage, queryMessage.length, InetAddress.getByAddress(DNS_SERVER), DNS_PORT);
        DatagramPacket responsePacket = new DatagramPacket(responseMessage, responseMessage.length);
       
        
        socketdns.send(requestPacket);
        socketdns.receive(responsePacket);
        socketdns.close();
        
        // Answer Message
        Answer ans = new Answer(responsePacket.getData(), responsePacket.getLength());
        return ans.getIp();
    }
    

}
