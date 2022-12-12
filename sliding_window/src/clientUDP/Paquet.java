package clientUDP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Paquet implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -4094566215273473054L;
    private int sequence;
    private byte end=0;
    private byte[] data;

    public Paquet (int sequence, byte [] data) {
        this.sequence=sequence;
        this.data=data;  
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
    public static Object getObject(byte[] byteArr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
        ObjectInput in = new ObjectInputStream(bis);
        return in.readObject();
    }


    public int getSequence()
    {
        return sequence;
    }


    public byte getEnd()
    {
        return end;
    }

    
    public byte[] getData()
    {
        return data;
    }
    
    
    public void setToLast()
    {
        end = 1;
    }

    /**
     * Verifie si on est sur le dernier paquet
     * @return
     */
    public boolean isLastPaquet()  {    
        return end==1;
    }
    
}
