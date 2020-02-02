package toxicologygadget.filemanager;

import java.io.*; 
import java.util.regex.*;
import toxicologygadget.ui.MainWindow;  

public class FileManager {
		

	
	public static String[] loadEnsembleGenelistTxt(File ensembleGenelistFile) {
		
		
		
		//TODO: file exists exception
		String ensembleGenelist;
		
		BufferedReader bufferReader = null;
		
		// find the command
		// @Reference_String|AP -Point Manifold3D -P -2 -B 2 -I 0
		try {
			bufferReader = new BufferedReader(new FileReader(ensembleGenelistFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	  
		String line; 
		
		return null;
		
	}
	
	public static int[] loadAGCTVisibleClusterFile(File clusterResultsFile) throws IOException {
		
		//TODO: file exists exception
		String command;
		
		int numberOfGenes = (int) (clusterResultsFile.length() - 2);
		int[] clusterResults = new int[numberOfGenes];
		
		BufferedReader bufferReader = null;
		
		// find the command
		// @Reference_String|AP -Point Manifold3D -P -2 -B 2 -I 0
		try {
			bufferReader = new BufferedReader(new FileReader(clusterResultsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	  
		String line; 
		
		line = bufferReader.readLine();
		
		if(line != null) {
			
			final String STRING_COMMAND_REFERENCE = "@Reference_String\\|";
			
			if(line.length() >= STRING_COMMAND_REFERENCE.length()) {
				command = line.substring(STRING_COMMAND_REFERENCE.length()-1);
				//@ TODO: remove output
				System.out.println(command);
			}else{
				//@ TODO: add exception handling
				System.out.println("Error Processing AGCT file - command not found!");
			}
			
		}else {
			//@ TODO: add exception handling
			System.out.println("Error Processing AGCT file - command not found null!"); 
		}
		
		// find the clustering results
		try {
			bufferReader = new BufferedReader(new FileReader(clusterResultsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		line = bufferReader.readLine();
		line = bufferReader.readLine();
		
		int clustIndex = 0;
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
			
			if(!foundCluster) {
				System.out.println("Error Processing AGCT file - cluster not found!"); 
				return null;
				//@ TODO: add exception handling 
			}
			
		}
		
		return clusterResults;
		
  	}
		
}

