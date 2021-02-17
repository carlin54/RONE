package plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReactomeSearchPlugin extends SearchPlugin {

	@Override
	public boolean onPluginLoad() {
		return true;
	}

	@Override
	public String[] getPluginMenuLocation() {
		return new String[] {"Reactome","with Genes"};
	}

	@Override
	public String getPluginTitle() {
		return "Reactome";
	}

	@Override
	public String[] getPluginColumnIdentifers() {
		return new String[]{
				"Gene Symbol", 
				"Species Name", 
				"Pathway Name", 
				"Pathway stId", 
				"Pathway Disease Association", 
				"Pathway lll",
				"Entities Ratio",
				"Entities pValue",
				"Entities FDR"
				};
	}

	@Override
	public void onPluginBeginSearch() {
		return;
	}

	@Override
	public int getPluginSearchTimeout() {
		return 60*1000;
	}

	@Override
	public int getPluginSearchSize() {
		return 1;
	}

	@Override
	public int getPluginSearchThreadPoolSize() {
		return 1;
	}
	
	public static String[] getCommand(String gene, int resultsPerPage, int pageNumber) {
		String url= "\"https://reactome.org/AnalysisService/identifier/" 
					+ gene 
					+ "?interactors=false&pageSize=" 
					+ resultsPerPage 
					+ "&page=" 
					+ pageNumber 
					+ "&sortBy=ENTITIES_PVALUE&order=ASC&resource=TOTAL&pValue=1&includeDisease=true\"";
		
		return new String[] {"curl", "-X", "GET", url, "-H", "\"accept: application/json\""}; 
	}
	
	private static int getNumberOfPathways(String jsonQuery) {
		try {
			JSONObject jo = new JSONObject(jsonQuery);
			
			return jo.getInt("pathwaysFound");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	private String getString(JSONObject jsonObject, String identifier) {
		try {
			String object = jsonObject.getString(identifier);
			return object;
		} catch (JSONException e) {
			return null;
		}
	}
	
	private Object[] cleanRow(Object[] row) {
		Object[] cleanedRow = row.clone();
		for(int i = 0; i < row.length; i++) {
			if(row[i] == null) {
				cleanedRow[i] = new String("");
			}
		}
		return cleanedRow;
	}	
	
	private ArrayList<Object[]> parseQuery(String gene, String jsonQuery) {
		JSONObject jo;
		JSONArray pathways;
		
		try {
			jo = new JSONObject(jsonQuery);		
			pathways = jo.getJSONArray("pathways");
		} catch (JSONException e) {
			return null;
		}
		
		ArrayList<Object[]> rows = new ArrayList<Object[]>();
		////System.out.println(pathways.length());
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

			
			Object[] row = new Object[getPluginColumnIdentifers().length];
			
			// "(R) Pathway" - "name":"Citric acid cycle (TCA cycle)"
			row[0] = new String(gene);
			
			// "(R) Species" - "name":"Homo sapiens"
			row[1] = getString(species, "name");
			
			// "(R) Pathway" - "name":"Citric acid cycle (TCA cycle)"
			row[2] = getString(pathway, "name");
						
			// "(R) stId" - "stId":"R-HSA-71403"
			row[3] = getString(pathway, "stId");
			
			// "Disease Association"
			row[4] = getString(pathway, "inDisease");
			
			// "Disease Association"
			row[5] = getString(pathway, "llp");
			
			// "(R) Coverage" - "ratio":0.0013613068545803972
			row[6] = getString(entities, "ratio");
			
			// "(R) P-Value"- "pValue":0.030749978135627742
			row[7] = getString(entities, "pValue");
			
			// "(R) FDR" - "fdr":0.10114749050397542
			row[8] = getString(entities, "fdr");
			
			
			for(int j = 0; j < row.length; j++) {
				Object obj = row[j];
				if(obj != null) {
					row = cleanRow(row);
					rows.add(row);	
					////System.out.println(Arrays.toString(row));
					break;
				}
			}
			
			
		}
		
		return rows;
	}
	
	
	private static final int MAX_REQUEST_ATTEMPTS = 1;
	
	private String[] selectionToStringArray(ArrayList<Object[]> selection) {
		ArrayList<String> stringSelection = new ArrayList<String>();
		for(int i = 0; i < selection.size(); i++) {
			Object[] row = selection.get(i);
			for(int j = 0; j < row.length; j++) {
				stringSelection.add(row[j].toString());
			}
		}
		String[] selectionAsStringArray = (String[])stringSelection.toArray();
		return selectionAsStringArray;
	}
	
	@Override
	public ArrayList<Object[]> onPluginSearch(Object[] columns, ArrayList<Object[]> selection) {
		String[] genes = selectionToStringArray(selection);
		String jsonQuery = null;
		ArrayList<Object[]> pathways = new ArrayList<Object[]>();
		int requestAttempts = 0;
		for(int i = 0; i < genes.length; i++) {
			String gene = genes[i];
			
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
					//System.out.print(jsonQuery);
					
					numberOfPathways = getNumberOfPathways(jsonQuery);
					//System.out.println(jsonQuery);
					ArrayList<Object[]> searchResults = parseQuery(gene, jsonQuery);
					
					pathways.addAll(searchResults);
					
					pathwaysSearched = pathwaysSearched + resultsPerPage;
					pageNumber++;
				} while(pathwaysSearched < numberOfPathways);
				
			} catch (IOException e) {
				// maybe add attempts
				if(requestAttempts < MAX_REQUEST_ATTEMPTS) {
					i--;	
					requestAttempts++;
				}else {
					requestAttempts = 0;
				}
			}
		
		}
		return pathways;
	}
	
	@Override
	public boolean onPluginEndSearch() {
		return true;
	}

	@Override
	public boolean onPluginClose() {
		return false;
	}

	@Override
	public void pluginTimedOut(Object[] columns, ArrayList<Object[]> selection) {
		// TODO Auto-generated method stub
		
	}

}
