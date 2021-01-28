package rone.backend;

import java.io.BufferedReader;
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

import org.json.JSONException;
import org.json.JSONObject;

public class PercellomeSearchInterface implements SearchInterface  {

	@Override
	public String getTitle() {
		return "Percellome";
	}

	@Override
	public String getIconLocation() {
		Path currentRelativePath = Paths.get("");
		return currentRelativePath.toAbsolutePath().toString() + "\\percellome_logo.png";
	}

	@Override
	public String[] getColumnIdentifers() {
		return new String[] {
				"AffyID", "Common", "Biological Process", "Cellular Component", "Molecular Function"
		};
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
	
	@Override
	public ArrayList<Object[]> query(String[] searchData) {

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
				
				System.out.println("Fetching: " + urlString);
				jsonString = fetchPecellomeURL(urlString);
				
				if(hasResult(jsonString)) {
					
					jsonString = jsonString.substring(2, jsonString.length()-2);
					
					// Probe ID
					jsonObj = new JSONObject(jsonString);
					
					
					//System.out.println("AffyID: " + jsonObj.get("AffyID"));
					row[0] = probe;
					
					// Gene Symbol
					//System.out.println("Common: " + jsonObj.get("Common"));
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
					System.out.println("Failed: " + i);
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
	    	System.out.println(line);
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
			System.out.println("----------");
			if (m.find()) {
				System.out.println(m.group(0));
				found[i] = m.group(0);
			} else {
				System.out.println("None");
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
