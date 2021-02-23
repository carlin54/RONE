package rone.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class PercellomeSearchPlugin extends Plugin {

	public PercellomeSearchPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Extension(ordinal=1)
	public static class PercellomeSearchExtension extends SearchExtension {
		
		private PercellomeSearch.Species getSpeciesSelection(){
			Object[] possibilities = PercellomeSearch.Species.values();
			String[] stringPossibilities = new String[possibilities.length];
			for(int i = 0; i < possibilities.length; i++) {
				stringPossibilities[i] = possibilities[i].toString();
			}
			
			Object s = (String)JOptionPane.showInputDialog(
                    null,
                    "Species select: ",
                    "Customized Dialog",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    stringPossibilities,
                    PercellomeSearch.Species.Mouse);
			
			return (s != null) ? PercellomeSearch.Species.valueOf(s.toString()) : null;
		}
		
		@Override
		public JMenu getMenu() {
			JMenu menuIndex = new JMenu("Percellome");
			JMenuItem geneSymbols = new JMenuItem("with Probe (Affy IDs)");
			geneSymbols.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PercellomeSearch.SearchMode searchMode = PercellomeSearch.SearchMode.WITH_PROBE_IDS;
					PercellomeSearch.Species species = getSpeciesSelection();
					if(species != null) {
						Object[] selectionArray = getCallback().getSelection().toObjectArray();
						Search search = new PercellomeSearch(searchMode, species);
						search.setThreadPoolSize(1);
						search.setSearchSize(1);
						search.setUniqueSearchRequests(selectionArray);
			
						getCallback().startSearch(search);
					}
				}
				
			});
			menuIndex.add(geneSymbols);
			
			JMenuItem affySymbols = new JMenuItem("with Gene Symbols");
			affySymbols.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(getCallback().getSelection().isEmpty())
						return;
					
					PercellomeSearch.SearchMode searchMode = PercellomeSearch.SearchMode.WITH_GENE_SYMBOLS;
					PercellomeSearch.Species species = getSpeciesSelection();
					
					if(species != null) {
						
						Object[] selectionArray = getCallback().getSelection().toObjectArray();
						Search search = new PercellomeSearch(searchMode, species);
						search.setThreadPoolSize(1);
						search.setSearchSize(1);
						search.setUniqueSearchRequests(selectionArray);
			
						getCallback().startSearch(search);
					}
				}
				
			});
			menuIndex.add(affySymbols);
			
			return menuIndex; 
		}

	}
	
	public static class PercellomeSearch extends Search {
		
		public final static String TITLE = new String("Percellome");
		
		public static String[] COLUMNS_WITH_PROBE_IDS = new String[]
		{
				"Probe (Affy ID)",
	            "Gene Symbol",
	            "Biological Function",
	            "Cellular Function",
	            "Molecular Function"   
		};
		
		public static String[] COLUMNS_WITH_GENE_SYMBOLS = new String[]
		{
				"Gene Symbol",
	            "Probe (Affy ID)",
	            "Found Gene Symbol"
		};
		
		private SearchMode mSearchMode;
		
		private Species mSpecies; 
		
		public enum SearchMode {
			WITH_GENE_SYMBOLS,
			WITH_PROBE_IDS
		}
		
		public enum Species {
			Mouse,
			Rat,
			Human
		}
		
		public final static String[] GET_COLUMNS(SearchMode searchMode) {
			return (searchMode.equals(SearchMode.WITH_PROBE_IDS)) ? COLUMNS_WITH_PROBE_IDS : COLUMNS_WITH_GENE_SYMBOLS;
		}
		
		public PercellomeSearch(SearchMode searchMode, Species species) {
			super(TITLE, GET_COLUMNS(searchMode));
			setSearchSize(1);
			setThreadPoolSize(5);
			mSearchMode = searchMode;
			mSpecies = species;
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
		
		public ArrayList<Object[]> queryWithProbeIDs(String[] searchData) {

			String urlString = null; 
			String jsonString = null;
			JSONObject jsonObj = null;
			ArrayList<Object[]> searchResults = new ArrayList<Object[]>();
			int requestAttempts = 0;
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
	    
		public ArrayList<Object[]> getSearchResults(Object[] searchRequests) {
			SearchMode searchMode = getSearchMode();
			
			String[] searchData = new String[searchRequests.length];
	        System.arraycopy(searchRequests, 0, searchData, 0, searchRequests.length);
	        
			switch(searchMode) {
			case WITH_GENE_SYMBOLS:
				return queryWithGeneSymbols(searchData);
			case WITH_PROBE_IDS:
				return queryWithProbeIDs(searchData);
			}
			return null; 
		}
		
	}
	
}
