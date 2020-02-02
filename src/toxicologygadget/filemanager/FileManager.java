package toxicologygadget.filemanager;

import java.io.*; 
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
	
	public static String[] loadEnsembleGenelistTxt(File ensembleGenelistFile) throws IOException {
		
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
		
		for(int i = 0; (gene = bufferReader.readLine()) != null; i++) {
			ensembleGenelist[i] = gene;
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
	
	public static int[] loadAGCTClusterResults(File clusterFile) throws IOException {
		
	
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
		
		
		
		return clusterResults;
		
	}
	
	private static int getNumberOfClusters(File clusterFile) {
		return (int) (clusterFile.length() - 2);
	}
	
	
}

