package toxicologygadget.filemanager;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.*;
import toxicologygadget.ui.MainWindow;  

public class FileManager {

	private static int getNumberOfLines(File file) {
		BufferedReader bufferReader = null;
		
		try {
			bufferReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		int i = 0;
		String line;
		try {
			for(i = 0; (line = bufferReader.readLine()) != null; i++);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	  
	}
	
	public static String[] loadCSVIdentifiers(File file) throws IOException {
		
		ArrayList<String> identifiers = new ArrayList<String>();
		
		BufferedReader bufferReader = new BufferedReader(new FileReader(file));
		String line = bufferReader.readLine();
		String[] parsedLine = line.split(",");
		identifiers.addAll(Arrays.asList(parsedLine));
		
		int len = identifiers.size();
		return identifiers.toArray(new String[len]);
	}
	
	public static Table loadCSV(File file) throws IOException{
		
		if (!file.exists()) return null;
		
		//TODO: file exists exception
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
		
		
		
		return new Table(data, identifiers);
			
	}
	
	public static Table loadListFile(File listFile, String header) throws IOException {
		
		if (!listFile.exists()) return null;
		
		//TODO: file exists exception
				
		BufferedReader bufferReader = new BufferedReader(new FileReader(listFile));
		ArrayList<ArrayList<Object>> tb = new ArrayList<ArrayList<Object>>();
		String line; 
		
		while((line = bufferReader.readLine()) != null) {
			ArrayList<Object> row = new ArrayList<Object>();
			row.add(line);
			tb.add(row);
		}
		
		ArrayList<String> id = new ArrayList<String>(); 
		id.add(header);
		Table dt = new Table(tb, id);
		
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

			}else{
				//@ TODO: add exception handling
				System.out.println("Error Processing AGCT file - command not found!");
			}
		}
		
		return referenceString;
	}
	
	public static int[] loadAGCTClusterResults(File clusterFile)  {
	
		int numberOfGenes = getNumberOfClusters(clusterFile);
		int[] clusterResults = new int[numberOfGenes];
		int clustIndex = 0;
		String line;
		
		BufferedReader bufferReader = null;
		try {
			bufferReader = new BufferedReader(new FileReader(clusterFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		
		
		try {
			line = bufferReader.readLine();
			line = bufferReader.readLine();
			while ((line = bufferReader.readLine()) != null){
				int i = 0;
				boolean foundCluster = false;
				for (i = 0; i < line.length(); i++) {
					if(line.charAt(i) == '|') {
						String clustString = line.substring(i+1);
						int cluster = Integer.parseInt(clustString);
						clusterResults[clustIndex] = cluster;
						clustIndex = clustIndex + 1;
						foundCluster = true;
						System.out.println(cluster); 
						break;
					}				
				}
				if (!foundCluster) return null;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return clusterResults;
		
	}
	
	private static int getNumberOfClusters(File clusterFile) {
		return (int) (clusterFile.length() - 2);
	}
	
	public static File writeOutTable(Table writeOut) {
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
	
	private static ArrayList<Object> loadAGCTScenarioColumns(){
		return null;
		
	}
	
	public static Table loadAGCTScenario(File file) throws IOException {
		
	
		final String ORDERED_LIST_HANDLE = "@Ordered_List_Names_Begin";
		String referenceString = null;
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
		
		ArrayList<Object> clusters = new ArrayList<Object>();
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
			
		}else {
			// TODO: throw
		}
		
		return null;
	}
	
}

