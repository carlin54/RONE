package rone.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.JSONException;
import org.json.JSONObject;

public class PercellomeSearchInterface implements SearchInterface  {
	
	public enum SearchMode {
		WITH_GENE_SYMBOLS,
		WITH_PROBE_IDS
	}
	
	public enum Species {
		Mouse,
		Rat,
		Human
	}
	
	SearchMode mSearchMode;
	Species mSpecies; 
	
	public PercellomeSearchInterface(Species species, SearchMode searchMode){
		this.mSpecies = species; 
		this.mSearchMode = searchMode;
	}
	
	@Override
	public String getTitle() {
		return "Percellome";
	}
	
	public static ImageIcon getIcon() {
		Path currentRelativePath = Paths.get("");
		String location = currentRelativePath.toAbsolutePath().toString() + "\\icons\\percellome_icon.png";
		ImageIcon img = new ImageIcon(location);
		return img;
	}
	
	@Override
	public String getIconLocation() {
		Path currentRelativePath = Paths.get("");
		return currentRelativePath.toAbsolutePath().toString() + "\\icons\\percellome_icon.png";
	}

	public String[] columnIdentifiersWithProbeIDs() {
		return new String[] {
				"Gene Symbols", "Common", "Probe IDs", "Cellular Component", "Molecular Function"
		};
	}
	
	public String[] columnIdentifiersWithGeneSymbols() {
		return new String[] {
				"Gene Symbol", "Common", "Probe ID"
		};
	}
	
	@Override
	public String[] getColumnIdentifers() {
		SearchMode searchMode = getSearchMode();
		
		switch(searchMode) {
		case WITH_GENE_SYMBOLS:
			return columnIdentifiersWithGeneSymbols();
		case WITH_PROBE_IDS:
			return columnIdentifiersWithProbeIDs();
		}
		return null;
	}

	@Override
	public int[] getPrimaryKeys() {
		return new int[] {};
	}

	@Override
	public int getWorkSize() {
		return 100;
	}

	@Override
	public int getThreadPoolSize() {
		// TODO Auto-generated method stub
		return 3;
	}
	
	
	static final int MAX_REQUEST_ATTEMPTS = 7;
	
	static boolean hasResult(String jsonString) {
		return !jsonString.equals("[null]");
	}
	
	public ArrayList<Object[]> queryWithGeneSymbols(String[] searchData) {

		String urlString = null; 
		String jsonString = null;
		JSONObject jsonObj = null;
		ArrayList<Object[]> searchResults = new ArrayList<Object[]>();
		String species = this.mSpecies.toString().toLowerCase();
		int requestAttempts = 0;
		for(int i = 0; i < searchData.length;) {
			
			try {
				Object[] row = new Object[this.getColumnIdentifers().length];
			
				String gene = searchData[i];
				
				urlString = "http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/psid/" + species + "/" + gene;
				
				//System.out.println("Fetching: " + urlString);
				jsonString = fetchPecellomeURL(urlString);
				
				if(hasResult(jsonString)) {
					
					jsonString = jsonString.substring(2, jsonString.length()-2);
					
					// Probe ID
					jsonObj = new JSONObject(jsonString);
					
					row[0] = gene;
					
					row[1] = jsonObj.get("id");
					
					row[2] = (String)jsonObj.get("Common");
					
					searchResults.add(row);
				
				}
				
			} catch (JSONException e1) {
				e1.printStackTrace();
				continue;
			}catch (IOException e2) {
				if(requestAttempts < MAX_REQUEST_ATTEMPTS) {
					requestAttempts++;
					//System.out.println("Failed: " + i);
					continue;
				}else {
					
					e2.printStackTrace();
				}
			}
			i++;
			requestAttempts = 0; 
			
		}
		return searchResults;
	}
	
    
	public SearchMode getSearchMode() {
		return this.mSearchMode;
	}
	
	public ArrayList<Object[]> query(String[] searchData) {
		SearchMode searchMode = getSearchMode();
		
		switch(searchMode) {
		case WITH_GENE_SYMBOLS:
			return queryWithGeneSymbols(searchData);
		case WITH_PROBE_IDS:
			return queryWithProbeIDs(searchData);
		}
		return null; 
	}
	
	public ArrayList<Object[]> queryWithProbeIDs(String[] searchData) {

		String urlString = null; 
		String jsonString = null;
		JSONObject jsonObj = null;
		ArrayList<Object[]> searchResults = new ArrayList<Object[]>();
		int requestAttempts = 0;
		boolean resetRequestAttempts = false;
		for(int i = 0; i < searchData.length;) {
			
			try {
				Object[] row = new Object[this.getColumnIdentifers().length];
			
				String probe = searchData[i];
				urlString = "http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/geneinfo/mouse/" + probe;
				
				//System.out.println("Fetching: " + urlString);
				jsonString = fetchPecellomeURL(urlString);
				
				if(hasResult(jsonString)) {
					
					jsonString = jsonString.substring(2, jsonString.length()-2);
					
					// Probe ID
					jsonObj = new JSONObject(jsonString);
					
					
					////System.out.println("AffyID: " + jsonObj.get("AffyID"));
					row[0] = probe;
					
					// Gene Symbol
					////System.out.println("Common: " + jsonObj.get("Common"));
					row[1] = jsonObj.get("Common");
					
					String description = (String)jsonObj.get("Descruption");
					String[] found = parseDescription(description);
					
					// Biological Function
					row[2] = found[0];
					
					// Cellular Function
					row[3] = found[1];
					
					// Molecular Function
					row[4] = found[2];
					
					searchResults.add(row);
				
				}
				
			} catch (JSONException e1) {
				e1.printStackTrace();
				continue;
			}catch (IOException e2) {
				if(requestAttempts < MAX_REQUEST_ATTEMPTS) {
					requestAttempts++;
					//System.out.println("Failed: " + i);
					continue;
				}else {
					
					e2.printStackTrace();
				}
			}
			i++;
			requestAttempts = 0; 
			
		}
		return searchResults;
	}
	
    public static String fetchPecellomeURL(String urlString) throws IOException {
    	StringBuilder stringToBuild = new StringBuilder();
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	    connection.setConnectTimeout(1000);
	    connection.setReadTimeout(1000);
	    connection.connect();
	    //http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/geneinfo/mouse/1422217_a_at
	    InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
	    BufferedReader reader = new BufferedReader(inputStreamReader);
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	    	stringToBuild.append(line);
	    	//System.out.println(line);
	    }
	    reader.close();
	    String output = stringToBuild.toString();
	    return output;
    }
    
    public static String[] parseDescription(String description) {
		String[] find = {"<<<BiologicalProcess>>>.*<<<CellularComponent>>>", 
                		 "<<<CellularComponent>>>.*<<<MolecularFunction>>>", 
                		 "<<<MolecularFunction>>>.*"};

		String[] found = {"", "", ""};

		for (int i = 0; i < find.length; i++) { 
			Pattern pattern = Pattern.compile(find[i], Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = pattern.matcher(description);
			//System.out.println("----------");
			if (m.find()) {
				//System.out.println(m.group(0));
				found[i] = m.group(0);
			} else {
				//System.out.println("None");
				found[i] = null;
			}
		}
		
		if(found[0] != null) found[0] = found[0].replace("\n", "").substring(23, found[0].length()-23);
		else found[0] = "";
		
		if(found[1] != null) found[1] = found[1].replace("\n", "").substring(23, found[1].length()-23);
		else found[1] = "";
		
		if(found[2] != null) found[2] = found[2].replace("\n", "").substring(23);
		else found[2] = "";
		
		return found;
    }
    

}
