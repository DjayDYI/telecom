package clientUDP;
public class Main
{

    public static void main(String[] args)
    {
        
        byte[] a = {10,44,88,123};
        ClientUDP c  = new ClientUDP(a);
        ClientUDP c2  = new ClientUDP(a);
        
        c2.upload(".//a.zip");
        c2.upload(".//send.jpg");
        //c2.upload(".//c.bin");
        c2.upload(".//S3-Le-métier-de-gestionnaire.pptx");
        
        
        c.download(".//c.bin");

    }

}
