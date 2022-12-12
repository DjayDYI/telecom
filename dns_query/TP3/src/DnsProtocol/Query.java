package DnsProtocol;

import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

public class Query
{
    private String url_to_decode;
    
    public Query(String url)
    {
        url_to_decode = url;
    }
    
    /**
     * Generate hexadecimal string representing the dns message header
     * @param id
     * @return
     */
    private String Header(int id) 
    {
        String hexheader =  String.format("%04X",id);       // Id
               hexheader += HeaderQueryParameter(0, 1);     // Flag part
               hexheader += String.format("%04X",0x0001);   // Nb Questions
               hexheader += String.format("%04X",0x0000);   // Nb Answers
               hexheader += String.format("%04X",0x0000);   // Number of authority records
               hexheader += String.format("%04X",0x0000);   // Number of additional records
               
        return hexheader;
    }
    
    /**
     * 
     * TODO : Parametrize Query Type and Query Class
     * 
     * Generate hexadecimal string representing question section of dns message
     * @return
     */
    private String Question()
    {
        //Question
        String[] res = url_to_decode.split("\\.");
        String hexquestion = "";;
        
        //Sub-Domain
        for(int i=0; i<res.length; i++)
        {
            hexquestion += String.format("%02X", res[i].length());
            for(int j=0; j<res[i].length(); j++)
            {
                hexquestion += String.format("%02X", (byte)res[i].charAt(j) );
            }
        }
        
        hexquestion += "00";    // Padding for data end
        hexquestion += "0001";  // Query Type A:1
        hexquestion += "0001";  // Query Class IN : internet = 1
        
        return hexquestion;    
    }
    
    /**
     * 
     * @param opCode
     * @param recursive  [0,1] 1 if we want to recursively lookup 
     * @return Integer de 32 bit correspondant au header 
     */
    private String HeaderQueryParameter( int opCode, int recursive ) 
    { 
        // 1 er bit + significatif à 0 pour Query
        short headerMeta = 0;
        
        // 4 bits subséquent Opcode 1 - 15
        //
        //    0   QUERY, Standard query.  RFC 1035
        //    1   IQUERY, Inverse query.  RFC 1035, RFC 3425
        //    2   STATUS, Server status request.  RFC 1035  
        //    4   Notify. RFC 1996
        //    5   Update.
        //
        headerMeta |= (opCode << 11);

        // 1 bit Authoritative Answer   0 => Not else 1
        // headerMeta |= 0x0400;
       
        // 1 bit Truncated              0 => Not else 1
        //  Indicates that only the first 512 bytes of the reply was returned
        
        // 1 bit Recursion Available    0 => Not else 1
        headerMeta |= (recursive << 8);
        
        // 1 bit RA, Recursion Available. 1 bit.
        //headerMeta |= (recursive << 7);

        // Z. 3 bit.
        //headerMeta |= 0x00;

        // AD, Authenticated data. 1 bit.
        // Indicates in a response that all data included in the answer and authority sections of 
        // the response have been authenticated by the server according to the policies of that server. 
        // It should be set only if all data in the response has been cryptographically verified or otherwise
        // meets the server's local security policy.
        //       0 : query

        // CD, Checking Disabled. 1 bit.
        // headerMeta |= 0x10;
        
        // 4 bit RCODE
        // Use for return code leave it to 0
        
        return String.format("%04X",headerMeta);
    }
    
    /**
     * Byte Array containing the dns query message
     * @return
     */
    public byte[] generateQuery() {
       return DatatypeConverter.parseHexBinary( Header(170) + Question() );
    }
      
}
