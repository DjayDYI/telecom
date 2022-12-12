package App;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Devrait ajouter un load, relaod , etc mais bon un moment donné la .....!
 * Faire des interface pour les different http request mais bon encore la ca me le tente plus ou moins
 * Ajouter la posssibilite d'utiliser different protocole... etc et bla bla bla bla
 * 
 * 
 * 
 * @author bouj1951
 *
 */
public class HttpSession
{
    // TODO : Automatisation , dynamic map filler -> #refact1
    private static final String REGEX_GIF = "[=][^\\s.]+.gif"; //  i.e [=][^\\s.]+.gif
    private static final String REGEX_JPG = "[=][^\\s.]+.jpg";
    private static final String REGEX_TIF = "[=][^\\s.]+.tif";
    private static final Pattern rGIF = Pattern.compile(REGEX_GIF);
    private static final Pattern rJPG = Pattern.compile(REGEX_JPG);
    private static final Pattern rTIF = Pattern.compile(REGEX_TIF);
    private  int a=1;
    
    // Attributes
    private Socket webSocket;
    private String[] urlDict;
    
    private String[] lastStatus;
    private String body = null;
    private ArrayList<String> imgs = new ArrayList<String>();
    private BufferedWriter wr;
    private BufferedReader rd;
    
    public HttpSession(byte[] _ip, int port, String[] _urlDict) 
    {
        try
        {
            urlDict = _urlDict;
            webSocket = new Socket(InetAddress.getByAddress(_ip), port);
            wr = new BufferedWriter(new OutputStreamWriter(webSocket.getOutputStream(), "UTF8"));
            rd = new BufferedReader(new InputStreamReader(webSocket.getInputStream()));
        }
        catch (UnknownHostException e)
        {
            System.out.println("DNS_PROBE_FINISHED_NXDOMAIN"
            		+ "\nPage inaccessible"
            		+ "Impossible de trouver l'adresse Ip du serveur de ce site");
            e.printStackTrace();     
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void close()
    {
        try
        {
            webSocket.close();
            wr.close();
            rd.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String PrintStatus() 
    {
        if(body == null)
            getPage();   //make sure there is content in body
        
        String status = "";
        for(int i=0; i<lastStatus.length; i++)
            status += lastStatus[i] + " ";
            
        return status;
    }
    
    public String getPage()
    {   
        try
        {
            return HttpGetRequest("/");
        }
        catch (IOException e)
        {
            return "";
        }
    }
    
    public void GetMedia()
    {
        if(body != null)
        {
        	for (String imgName : imgs) 
        	{
        		try
                {
                    HttpGetImgRequest(imgName);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }  
			}       	                
        }
              
    }
    
    private void HttpGetImgRequest(String urlPath) throws IOException
    {
    	System.out.println("TELECHARGEMENT IMAGES");
        wr.write("GET "+urlPath+" HTTP/1.1 \r\n");
        wr.write("Host: "+urlDict[0]+" \r\n");
        wr.write("\r\n\r\n");
        wr.flush();
        String [] filename= urlPath.split("\\/");

        InputStream inputStream = webSocket.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        System.out.println("img : " + filename[filename.length-1]);
         //Get response
         BufferedReader rd = new BufferedReader(reader);
             
            // Status de la request
         lastStatus = rd.readLine().split(" ");
         System.out.println( PrintStatus() );
            
        String img = "";
        int endHeader = 0;
        boolean flag=false;
        
        while (!flag && rd.ready()) {
            img = rd.readLine();
            
            if(endHeader > 11)
            {
                System.out.print(img);
                endHeader++;
                if(endHeader == 14)
                    flag=true;
            }
            else 
            {
                endHeader++;
            }
        }
        
        System.out.println("\n");
    }


	private String HttpGetRequest(String u) throws UnknownHostException, IOException 
    {
        // Send headers
        wr.write("GET "+u+" HTTP/1.1 \r\n");
        wr.write("Host: "+ urlDict[0] +" \r\n");
        wr.write("\r\n\r\n");
        wr.flush();

        
     // Get response
        BufferedReader rd = new BufferedReader(new InputStreamReader(webSocket.getInputStream()));
        //Afficher(rd);
         
        // Status de la request
        lastStatus = rd.readLine().split(" ");
        
        this.body = "";
        int endHeader = 0;
        
        while (rd.ready()) {
            String line = rd.readLine();
            
            if(endHeader > 12) 
                body += line + "\n";
            else
                endHeader++;
                
                
            //#refact1
            //extract img url
            if(line.contains(".gif")){
                Matcher match = rGIF.matcher(line);
                if (match.find()&& ! imgs.contains(match.group(0).substring(2)))
                   imgs.add( match.group(0).substring(2) );
            }
            
            if(line.contains(".jpg")){
                Matcher match = rJPG.matcher(line);
                if (match.find()&& ! imgs.contains(match.group(0).substring(2)))
                   imgs.add( match.group(0).substring(2) );
            }
            
            if(line.contains(".tif")){
                Matcher match = rTIF.matcher(line);
                if (match.find()&& ! imgs.contains(match.group(0).substring(2)))
                   imgs.add( match.group(0).substring(2) );
            }          
        }
        return body;
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
            if(line.contains(".jpg") || line.contains(".tiff")|| line.contains(".gif"))
                images.append(line.trim()+"\n");
            System.out.println(line);
        }
        System.out.println("PARTIE IMAGE YFYFYYFYFYFYFYYF ");
        System.out.println(images);
        
    }
}
