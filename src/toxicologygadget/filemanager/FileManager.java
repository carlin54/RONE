package toxicologygadget.filemanager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class FileManager {


	
	public static String[] loadCSVIdentifiers(File file) throws IOException {
		
		ArrayList<String> identifiers = new ArrayList<String>();
		
		BufferedReader bufferReader = new BufferedReader(new FileReader(file));
		String line = bufferReader.readLine();
		String[] parsedLine = line.split(",");
		identifiers.addAll(Arrays.asList(parsedLine));
		bufferReader.close();
		int len = identifiers.size();
		return identifiers.toArray(new String[len]);
	}
	
	public static Table loadCSV(File file) throws IOException{
		
		if (!file.exists()) return null;
		
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		ArrayList<String> identifiers = new ArrayList<String>();
		
		BufferedReader bufferReader = null;
	
		bufferReader = new BufferedReader(new FileReader(file));
	  
		String line; 
	
		line = bufferReader.readLine();
		String[] parsedLine = line.split(",");
		identifiers.addAll(Arrays.asList(parsedLine));
		while((line = bufferReader.readLine()) != null) {
			parsedLine = line.split(",");
			ArrayList<Object> row = new ArrayList<Object>();
			row.addAll(Arrays.asList(parsedLine));
			data.add(row);
		}
		
		bufferReader.close();
		
		return new Table(data, identifiers);
			
	}
	
	public static Table loadListFile(File listFile, String header) throws IOException {
		
		if (!listFile.exists()) return null;
				
		BufferedReader bufferReader = new BufferedReader(new FileReader(listFile));
		ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();
		String line; 
		
		if(header == null) {
			header = bufferReader.readLine();
		}
		
		while((line = bufferReader.readLine()) != null) {
			ArrayList<Object> row = new ArrayList<Object>();
			row.add(line);
			table.add(row);
		}
		
		ArrayList<String> identifier = new ArrayList<String>(); 
		identifier.add(header);
		Table dt = new Table(table, identifier);
		
		bufferReader.close();
		return dt;
	}
	
	public static String loadAGCTReferenceString(File clusterResultsFile) throws IOException {
		// find the command
		// @Reference_String|AP -Point Manifold3D -P -2 -B 2 -I 0
		final String STRING_COMMAND_REFERENCE = "@Reference_String\\|";
		String referenceString = null;
		BufferedReader bufferReader = null;
		
		bufferReader = new BufferedReader(new FileReader(clusterResultsFile));
		
		
		String line; 
		line = bufferReader.readLine();
		
		if(line != null) {
			if(line.length() >= STRING_COMMAND_REFERENCE.length()) {
				referenceString = line.substring(STRING_COMMAND_REFERENCE.length()-1);
			}
		}
		
		bufferReader.close();
		
		return referenceString;
	}
	
	public static File writeOutTable(Table writeOut, String location) {
		return null;
	}
	
	private static String getJarPath() {
		File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		return jarDir.getAbsolutePath();
	}
	
	public static File writeOutString(String data, String fileName) throws IOException {
		
		String path = getJarPath() + "\\" + fileName;
		File file = new File(path);
		
		if(file.exists())
			file.delete();
		
		FileWriter fr = new FileWriter(file, true);
		fr.write(data);
		fr.close();

		
		return file;
	}
	
	
	
	public static Table loadAGCTScenario(File file) throws IOException {
		
	
		final String ORDERED_LIST_HANDLE = "@Ordered_List_Names_Begin";
		BufferedReader bufferReader = null;
		
		bufferReader = new BufferedReader(new FileReader(file));
		
		ArrayList<Object> genes = new ArrayList<Object>();
		
		String line; 
		line = bufferReader.readLine();
		while((line = bufferReader.readLine()) != null) {
			if(line.equals(ORDERED_LIST_HANDLE)) {
				line = bufferReader.readLine();
				String[] str = line.split("\\|");
				for(String s : str) {
					genes.add(s);
				}
			}
		}
		bufferReader.close();
		
		
		ArrayList<Object> clusters = new ArrayList<Object>();
		
		// Finds data handle
		final String DATA_HANDLE = "@DATA|";
		bufferReader = new BufferedReader(new FileReader(file));
		while((line = bufferReader.readLine()) != null) {
			if(line.equals(DATA_HANDLE)) 
				break;
		}
		
		while((line = bufferReader.readLine()) != null) {
			String[] parse = line.split("\\|");
			clusters.add(parse[1]);
		}
		bufferReader.close();
		
		if(clusters.size() == genes.size()) {
			
			ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();
			for(int i = 0; i < clusters.size(); i++) {
				ArrayList<Object> row = new ArrayList<Object>();
				row.add(genes.get(i));
				row.add(clusters.get(i));
				table.add(row);
			}
			
			ArrayList<String> identifiers = new ArrayList<String>();
			identifiers.add("Probe ID");
			identifiers.add("Cluster");
			
			
			return new Table(table, identifiers);
			
		} else {
			JOptionPane.showMessageDialog(null, "Error: Processing AGCT Scenario.", "Failure", JOptionPane.ERROR_MESSAGE);
		}
		
		return null;
	}
	
}

