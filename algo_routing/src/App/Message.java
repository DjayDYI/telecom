package App;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import algorithms.TableDV;


public class Message implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String message;
    private String destination;

    public Message(String s, String routeur)
    {
        message = s;
        destination = routeur;
    }


    //Convertir en byte array
    public byte[] convertToByte() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }
    
    //COnversion en Object
    public static Message getObject(byte[] byteArr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
        ObjectInput in = new ObjectInputStream(bis);
        return (Message) in.readObject();
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getDestination() {
        return destination;
    }
    

}
