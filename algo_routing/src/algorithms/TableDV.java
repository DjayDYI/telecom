package algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class TableDV implements Serializable
{

    private static final long serialVersionUID = 1L;

	private final static int INFINI = 1000000;

    private int [][] _tempTable;
    private ArrayList<String> via = new ArrayList<String>();
    private ArrayList<String> destinations = new ArrayList<String>();
	private String monIp;
    
	
    public TableDV(Map<String, Integer> voisin,String monIp) {      
    	this.monIp=monIp;
        ArrayList<Integer> value= new ArrayList<>();
        for (Integer routeurCout : voisin.values())
        {
            value.add(routeurCout);
        }
        
        for ( String key : voisin.keySet() ) {
            via.add(key);
            destinations.add(key);
        }
        
        int sizeTable= voisin.size();
        _tempTable= new int [sizeTable][sizeTable];
        for (int i = 0; i < _tempTable.length; i++) 
        {   
            for (int j = 0; j < _tempTable.length; j++)
            {
                if(i==j)
                {
                    _tempTable[i][j]=value.get(i);
                }
                else
                {
                    _tempTable[i][j]=INFINI;
                }
            }
        
        }

    }
    
    /**
     * Affiche la table de routage
     */
    public void print() {
        String a ="";
        for (int[] is : _tempTable) {
            for (int i : is) {
                a+=""+i+" ";
            }
            a+="\n";
        }
        System.out.println(a);      
    }
    
    
    public int[][] getTable(){
        return _tempTable;
    }
    
    public ArrayList<String> getVia(){
        return via;
    }
    
    public ArrayList<String> getDestination(){
        return destinations;
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
    public static TableDV getObject(byte[] byteArr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArr);
        ObjectInput in = new ObjectInputStream(bis);
        return (TableDV) in.readObject();
    }


	public String getIp() {
		return monIp ;
	}

	public void addDestination(String destination, String voisin, int i) {
		this.destinations.add(destination);
		int positionVia=via.indexOf(voisin);
		int [][]_tempTable2= new int[destinations.size()][via.size()];
		for (int j = 0; j < _tempTable.length; j++)
        {
            for (int k = 0; k < via.size(); k++)
            {
                _tempTable2[j][k]=_tempTable[j][k];
            }
        }
		
		

		for (int viaColonne = 0; viaColonne < via.size(); viaColonne++)
            _tempTable2[destinations.size()-1][viaColonne]=INFINI;
        
		_tempTable=_tempTable2;
		_tempTable[destinations.size() - 1][positionVia]=i;

	}
	

    /**
	 * retourne le plus petit de la ligne
	 * @param ligne
	 * @return
	 */
	private int getMinimum(int ligne) {
		int minimum= INFINI; 
		for (int colonne=0;colonne<via.size();colonne++)
		{
			if (_tempTable[ligne][colonne]<minimum)
				minimum=_tempTable[ligne][colonne];			
		}
		return minimum;
	}
	
	public int getMinimum (String nomDestination) {
		int ligne= destinations.indexOf(nomDestination);
		return getMinimum(ligne);
	}
	
	/**
	 * Compare la table courante a une autre
	 * On compare les destinations et on s�lectionne
	 * la plus petite 
	 * @param table
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public boolean compareTable (TableDV tableVoisin, boolean alreadyChanged) {			
		//Selectionner les destinations identiques
		ArrayList<String> identiques= new ArrayList<String>();
		
		for (int i = 0; i < tableVoisin.getDestination().size(); i++){
		    //if(tableVoisin.getDestination().get(i) != monIp)
    			if(this.destinations.contains(tableVoisin.getDestination().get(i))){
    				identiques.add(tableVoisin.getDestination().get(i));
    			}
		}	
		
		//Compare les destinations identiques
		if(!identiques.isEmpty()) {
			//Pour comparer ancienne et nouvelle valeur
			TableDV enAttendant=this;
			
			//Compare leur valeur et on prend la plus petite
			for (String destination : identiques) {
			    if(destination != monIp && !tableVoisin.getIp().equals(monIp))
    				if(tableVoisin.getMinimum(destination)+getMinimum(tableVoisin.getIp())<getMinimum(destination)) {
    					changeValue(tableVoisin.getIp(),destination,tableVoisin.getMinimum(destination)+tableVoisin.getMinimum(destination));
    				}
			}
			
			//Comparaison des anciensValeurs et pseudoNouvelleValeur
			for (String destination : identiques) {
			    if(destination != monIp && !tableVoisin.getIp().equals(monIp))
    				if(enAttendant.getMinimum(destination)!=getMinimum(destination)) {
    				    alreadyChanged=true;
    				}
			}
	       
	    }
		
        return alreadyChanged;		

	}

	/**
	 * 
	 * @param nomVia
	 * @param destination
	 * @param value
	 */
	public void changeValue(String nomVia, String destination, int value) {
		int positionVia= via.indexOf(nomVia);
		int positionDestination= destinations.indexOf(destination);
		_tempTable[positionDestination][positionVia]=value;		
	}   
}
