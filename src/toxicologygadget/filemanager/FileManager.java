package toxicologygadget.filemanager;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	public static String[][] loadCSV(File file, int numColumns){
		
		if (!file.exists()) return null;
		
		//TODO: file exists exception
		int numLines = getNumberOfLines(file);
		String[][] rows = new String[(int) numLines][numColumns];
		
		BufferedReader bufferReader = null;
		
		try {
			bufferReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	  
		String line; 
		
		try {
			for(int i = 0; (line = bufferReader.readLine()) != null; i++) {
				String[] parsedLine = line.split(",");
				rows[i] = parsedLine;
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		System.out.print("\n");
		for(int i = 0; i < rows.length; i++) {
			for(int j = 0; j < rows[i].length; j++) {
				System.out.print(rows[i][j] + ", ");
			}
			System.out.print("\n");
		}
		
		return rows;
			
	}
	
	public static DataTable loadSHOE(File shoeFile){
		return new DataTable(loadCSV(shoeFile, 14));
	}
	
	private static String[][] addHeader(String[] header, String[][] arrayData){
		
		String[][] dataWithHeader = new String[arrayData.length+1][5];
		dataWithHeader[0] = header;
		
		for(int i = 0; i < arrayData.length; i++) {
			dataWithHeader[i+1] = arrayData[i];
		}
		
		return dataWithHeader;
		
	}
	
	public static String[][] loadReactome(File reactomeFile){
		
		if (!reactomeFile.exists()) return null;

		final String[] COLUMN_NAMES = {"Pathway", "Species", "% Coverage", "Pval", "FDR"};
		
		String[][] arrayData = loadCSV(reactomeFile, 5);
		
		String[][] reactomeData = addHeader(COLUMN_NAMES, arrayData);
		
		return reactomeData;
		
	}
	
	public static String[] loadEnsembleGenelistTxt(File ensembleGenelistFile) {
		
		if (!ensembleGenelistFile.exists()) return null;
		
		//TODO: file exists exception
		int numGenes = getNumberOfLines(ensembleGenelistFile);
		String[] ensembleGenelist = new String[(int) numGenes];
		
		BufferedReader bufferReader = null;
		
		try {
			bufferReader = new BufferedReader(new FileReader(ensembleGenelistFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	  
		String gene; 
		
		try {
			for(int i = 0; (gene = bufferReader.readLine()) != null; i++) {
				ensembleGenelist[i] = gene;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ensembleGenelist;
		
		
	}
	
	public static String loadAGCTReferenceString(File clusterResultsFile) throws IOException {
		// find the command
		// @Reference_String|AP -Point Manifold3D -P -2 -B 2 -I 0
		final String STRING_COMMAND_REFERENCE = "@Reference_String\\|";
		String referenceString = null;
		BufferedReader bufferReader = null;
		
		try {
			bufferReader = new BufferedReader(new FileReader(clusterResultsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		String line; 
		line = bufferReader.readLine();
		
		if(line != null) {
			if(line.length() >= STRING_COMMAND_REFERENCE.length()) {
				referenceString = line.substring(STRING_COMMAND_REFERENCE.length()-1);
				//@ TODO: remove output
				System.out.println(referenceString);
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
	
	public static File writeOutTable(DataTable writeOut) {
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
	
}

