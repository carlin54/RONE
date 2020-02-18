package toxicologygadget.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toxicologygadget.filemanager.Table;

public class PercellomeQueryThread extends Thread {
	
	
	static final String ROOT = "http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/";
	static final String TEST_CONNECTIVITY = "http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/test";
	
	QueryThreadCallback callback;
	int projectId;
	String[] genelist;
	
	public PercellomeQueryThread(QueryThreadCallback callback){
    	this.callback = callback;
    	this.genelist = null;
    	this.projectId = 0;
    }
	
	public void setGenelist(String[] genelist) {
		this.genelist = genelist;
	}
	
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	
	private static URL individualDataURL(String gene, int projectId) throws MalformedURLException {
		String s = "http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/IdvData/" + projectId + "/" + gene;
		return new URL("http://percellome.nihs.go.jp/PDBR/v1.dll/ds/rest/tools/IdvData/" + projectId + "/" + gene);
	}
	
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static String[] parseColumnIdentifiers(String individualDataJSON) {
		ArrayList<String> identifiers = new ArrayList<String>();
		
		String findResult = "\"[0-9]* mg\\/kg, [0-9]* hr_#1\"";
		Pattern p = Pattern.compile(findResult);
		Matcher m = p.matcher(individualDataJSON);
		
		while (m.find()) {    
			String id = m.group() + " (P)";
			identifiers.add(id);
        }  
		
		return identifiers.toArray(new String[identifiers.size()]);
		
	}
	
	public static ArrayList<Object> parseIndividualData(String gene, String individualDataJSON) {
		ArrayList<Object> row = new ArrayList<Object>();
		row.add(gene);
		
		String findResult = "\"[0-9]*([.]*[0-9]*)?\"";
		Pattern p = Pattern.compile(findResult);
		Matcher m = p.matcher(individualDataJSON);
		
		while (m.find()) {    
			row.add(m.group());
        }   
		
		return row;
		
	}
	
	private boolean hasResults(String individualDataJSON) {
		return !individualDataJSON.contentEquals("[null]");
	}
	
	public void run() {
		
		ArrayList<ArrayList<Object>> individualData = new ArrayList<ArrayList<Object>>();
		String individualDataJSON = new String("");
		
		for(int i = 0; i < genelist.length; i++)
		{
			String gene = genelist[i];
			URL url;
			InputStream inputStream;
			
			ArrayList<Object> individualDataRow;
			
			try {
				url = individualDataURL(gene, this.projectId);
				inputStream = url.openStream();
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
				individualDataJSON = readAll(bufferReader);
				
				if(hasResults(individualDataJSON)) {
					individualDataRow = parseIndividualData(gene, individualDataJSON);
					individualData.add(individualDataRow);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		}
		
		String[] columnIdentifiers = parseColumnIdentifiers(individualDataJSON);
		
		Table individualDataTable = new Table(individualData, columnIdentifiers);
		
		callback.completeSearch(individualDataTable);
		
	}
	
}
