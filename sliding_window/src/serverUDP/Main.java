package serverUDP;
import java.net.SocketException;

public class Main
{
    public static void main(String[] args)
    {               
        try
        {
            ServeurUDP server = new ServeurUDP();        
            server.run();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }      
    }
}
