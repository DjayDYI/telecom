package DnsProtocol;

public class Answer
{
    private byte[] answer;
    private int len;
    private byte[] ip = null;
    
    public Answer(byte[] message, int len) 
    {
        this.answer = message;
        this.len = len;
    }
    
    private boolean isNull() 
    {
        for(int i=0;i<ip.length;i++)
            if(ip[i] != 0)
                return false;
        
        return true;
    }
    
    /**
     * Translate adress to byte only one time 
     * if adress is [0.0.0.0] then return null;
     * @return
     */
    public byte[] getIp()
    {
        if(ip == null)
        {
            int offset = len - 16;              //nb byte
            int ipLen  = answer[offset + 11];   //nb byte to read for ip
         
            this.ip = new byte[4];
            for(int i=0; i<ipLen; i++)
                ip[i] =  answer[len - ipLen + i] ;
        }
        
        if(isNull())
            return null;
        
        return ip;
    }
    
    
}


