package toxicologygadget.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import toxicologygadget.filemanager.Table;

public class ReactomeQueryThread extends Thread {

    private String[] mGenelist;
    private QueryThreadCallback mCallback;
    private boolean mProcessRunning;
    private boolean mStopProcess;
	
	public ReactomeQueryThread(QueryThreadCallback callback) {
		mCallback = callback;
	}
	
	public void setGenelist(String genelist[]) {
		this.mGenelist = genelist;
	}
	
	
	public void stopRunning() {
		mStopProcess = true;
	}
	
	public boolean isRunning() {
		return mProcessRunning;
	};
	
	public String[] getCommand(String gene, int resultsPerPage, int pageNumber) {
		String url= "\"https://reactome.org/AnalysisService/identifier/" 
					+ gene 
					+ "?interactors=false&pageSize=" 
					+ resultsPerPage 
					+ "&page=" 
					+ pageNumber 
					+ "&sortBy=ENTITIES_PVALUE&order=ASC&resource=TOTAL&pValue=1&includeDisease=true\"";
		
		return new String[] {"curl", "-X", "GET", url, "-H", "\"accept: application/json\""}; 
	}
	
	public ArrayList<ArrayList<Object>> query(String gene) {
		
		String jsonQuery = null;
		ArrayList<ArrayList<Object>> pathways = new ArrayList<ArrayList<Object>>();
		try
		{
			int resultsPerPage = 1000; 
			int pathwaysSearched = 0;
			int numberOfPathways = 0;
			int pageNumber = 1;
			do {
				String[] command = getCommand(gene, resultsPerPage, pageNumber);
				ProcessBuilder process = new ProcessBuilder(command); 
				Process p;
				
				p = process.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ( (line = reader.readLine()) != null) {
					builder.append(line);
					builder.append(System.getProperty("line.separator"));
				}
				jsonQuery = builder.toString();
				System.out.print(jsonQuery);
				
				numberOfPathways = getNumberOfPathways(jsonQuery);
				ArrayList<ArrayList<Object>> searchResults = parseQuery(jsonQuery);
				for(ArrayList<Object> o : searchResults) 
					o.add(0, gene);
				pathways.addAll(searchResults);
				
				pathwaysSearched = pathwaysSearched + resultsPerPage;
				pageNumber++;
			} while(pathwaysSearched < numberOfPathways);
			
		} catch (IOException e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
			return null; // <-- 
		}
		
		return pathways;
	}
	
	private ArrayList<ArrayList<Object>> parseQuery(String jsonQuery) {
		JSONObject jo;
		JSONArray pathways;
		
		try {
			jo = new JSONObject(jsonQuery);		
			pathways= jo.getJSONArray("pathways");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
		
		for(int i = 0; i < pathways.length(); i++) {
			
			JSONObject pathway;
			JSONObject species;
			JSONObject entities;
			try {
				pathway = pathways.getJSONObject(i);
				species = pathway.getJSONObject("species");
				entities = pathway.getJSONObject("entities");
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}

			
			ArrayList<Object> row = new ArrayList<Object>();
			try {
				// "(R) Pathway" - "name":"Citric acid cycle (TCA cycle)"
				row.add(pathway.getString("name"));
				
				// "(R) Species" - "name":"Homo sapiens"
				row.add(species.getString("name"));
							
				// "(R) stId" - "stId":"R-HSA-71403"
				row.add(pathway.getString("stId"));
				
				// "(R) Coverage" - "ratio":0.0013613068545803972
				row.add(entities.getString("ratio"));
				
				// "(R) P-Value"- "pValue":0.030749978135627742
				row.add(entities.getString("pValue"));
				
				// "(R) FDR" - "fdr":0.10114749050397542
				row.add(entities.getString("fdr"));
				
				// System.out.println(pathway.getString("name")  + " : " + species.getString("name") + " : " + pathway.getString("stId") + " : " + entities.getString("ratio"));
				
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			
			rows.add(row);	
		}
		
		return rows;
		
		
	}
	
	private int getNumberOfPathways(String jsonQuery) {
		try {
			JSONObject jo = new JSONObject(jsonQuery);
			return jo.getInt("pathwaysFound");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	private String[] fixParsedQuery(String[] arrayEntries) {
		int len = arrayEntries.length;
		if(len > 1) {
			arrayEntries[0] = arrayEntries[0] + "}";
			for(int j = 1; j < arrayEntries.length-1; j++) {
				arrayEntries[j] = "{" + arrayEntries[j] + "}";
			}
			arrayEntries[len-1] = "{" + arrayEntries[len-1];
		}
		return arrayEntries;
	}
	
	
	/*private String[] parseQuery(String jsonQuery) {
				
		JSONObject jsonResults;
		String arrayEntries[] = null;
		try {
			JSONObject jo = new JSONObject(jsonQuery);
			
			// Reactome Structure
			// (...[], results [ entries [annotation 0,... annotation N] ])
			// get the results 
			String strResults = jo.getString("results");
			String strClippedResults = (String) strResults.substring(1, strResults.length()-1);
			jsonResults = new JSONObject(strClippedResults);
	
			// get the entries 
			String strEntries = jsonResults.getString("entries");
			String strClippedEntries = (String) strEntries.substring(1, strEntries.length()-1);
			
			// split into an array
			arrayEntries = strClippedEntries.split("\\},\\{");
		} catch (JSONException je) {
			// System.out.println(je.getMessage());
			return null; // <-- issue return null communicate it
			// terminate parsing of this gene
		}	
		

		arrayEntries = fixParsedQuery(arrayEntries);
		
		return arrayEntries;
	}*/
	
	private ArrayList<Object> extractEntryData(String entry){

		JSONObject jsonEntry = null;
		try {
			jsonEntry = new JSONObject(entry);
		}catch(JSONException je) {
			// System.out.println(je.getMessage());
			// je.printStackTrace();
			return null;
		}
		
		
		String[] labels = {"name", "species", "stId"};
		int lblLen = labels.length;
		ArrayList<Object> row = new ArrayList<Object>();
		for(int k = 0; k < lblLen; k++) {
			String entryData = null; 
			try {
				entryData = jsonEntry.getString(labels[k]);
				System.out.println(entryData);
			}catch(JSONException je){
				// System.out.println(je.getMessage());
				// je.printStackTrace();
			}
			row.add(entryData);
		}
		
		return row;
	}
	
	public boolean unsuccessful(ArrayList<ArrayList<Object>> ptr) {
		return ptr == null || ptr.size() == 0;
	}
	
	private boolean stopProcess() {
		return mStopProcess;
	}
	
	private String cleanStr(String str) {
		
		if(str != null) {
			String s = str.replace("]", "");
			s = s.replace("[", "");
			s = s.replace("\"", "");
			return s;
		}
		
		return null;
	}
	
	public ArrayList<Object> cleanRow(ArrayList<Object> row){
		final int iSpecies = 2;
		
		String cleanedSpecies = cleanStr((String)row.get(iSpecies));
		row.set(iSpecies, cleanedSpecies);
		
		return row;
		
	}
	
	
	public void run() {
		
		mProcessRunning = true;
		mStopProcess = false;
		int numGene = mGenelist.length;
		mCallback.startSearch(numGene);
		ArrayList<String> identifiers = 
				new ArrayList<String>(
						Arrays.asList("Gene", "(R) Pathway", "(R) Species", "(R) stId", "(R) Coverage", "(R) P-Value", "(R) FDR")); // what else do you want
		Table reactomeTable = new Table(identifiers);
		int foundCounter = 0;
		
		for(int i = 0; i < numGene; i++) {
			
			mCallback.statusUpdate(i, numGene, foundCounter);
			if(stopProcess()) {
				mCallback.completeSearch(reactomeTable, QueryThreadCallback.statusCodeFinishUnsuccess);
				return;
			}
			
			String gene = mGenelist[i];
			ArrayList<ArrayList<Object>> queryResults = query(gene);
			if(unsuccessful(queryResults)) continue;
			
			for(ArrayList<Object> row : queryResults) 
				reactomeTable.addRow(row);
			
			
		}
		mCallback.statusUpdate(numGene, numGene, foundCounter);
		mCallback.completeSearch(reactomeTable, QueryThreadCallback.statusCodeFinishSuccess);
		mProcessRunning = false;
		
	}
	
}
