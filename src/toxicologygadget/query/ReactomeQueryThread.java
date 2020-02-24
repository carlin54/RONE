package toxicologygadget.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

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
	
	public String[] getCommand(String gene) {
		String url="https://reactome.org/ContentService/search/query?query=" + gene + "&types=Pathway&cluster=true";
		return new String[] {"curl", "-X", "GET", url, "-H", "\"accept: application/json\""}; 
	}
	
	public String query(String gene) {
		
		String jsonQuery = null;
		try
		{
			String[] command = getCommand(gene);
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
		
		} catch (IOException e) {
			// System.out.println(e.getMessage());
			// e.printStackTrace();
			return null; // <-- 
		}
		
		return jsonQuery;
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
	
	private String[] parseQuery(String jsonQuery) {
				
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
	}
	
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
	
	public boolean unsuccessful(Object ptr) {
		return ptr == null;
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
		ArrayList<String> identifiers = 
				new ArrayList<String>(
						Arrays.asList("Gene", "Name", "Species", "stId")); // what else do you want
		Table reactomeTable = new Table(identifiers);
		int foundCounter = 0;
		int numGene = mGenelist.length;
		for(int i = 0; i < numGene; i++) {
			mCallback.statusUpdate(i, numGene, foundCounter);
			if(stopProcess()) {
				mCallback.completeSearch(reactomeTable, QueryThreadCallback.statusCodeFinishUnsuccess);
				return;
			}
			
			String gene = mGenelist[i];
			String jsonQuery = query(gene);
			if(unsuccessful(jsonQuery)) continue;
			
			String entries[] = parseQuery(jsonQuery);
			if(unsuccessful(entries)) continue;
			
			
			for(int j = 0; j < entries.length; j++) {
				ArrayList<Object> row = extractEntryData(entries[j]);
				if(unsuccessful(row)) continue;
				
				row.add(0, gene);
				row = cleanRow(row);
				reactomeTable.addRow(row);
				foundCounter = foundCounter + 1;
			}
			
		}
		
		mCallback.completeSearch(reactomeTable, QueryThreadCallback.statusCodeFinishSuccess);
		mProcessRunning = false;
		
	}
	
}
