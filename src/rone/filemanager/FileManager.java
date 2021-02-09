package rone.filemanager;

import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

public class FileManager {
	
	
	private static String parsePathwayID(String pathwayIdLine) {
		int beginIndex = pathwayIdLine.length() - 20;
		int endIndex = pathwayIdLine.length() - 12;
		return pathwayIdLine.substring(beginIndex, endIndex);
	}
	
	private static String parsePathwayName(String pathwayNameLine) {
		int beginIndex = 0;
		int endIndex = pathwayNameLine.length() - 12;
		String clipped = pathwayNameLine.substring(beginIndex, endIndex);
		String[] split = clipped.split(">");
		return split[split.length-1];
	}
	
	private static String parseAdjustedPvalue(String adjustedPvalueLine) {
		int beginIndex = 0;
		int endIndex = adjustedPvalueLine.length() - 39;
		String clipped = adjustedPvalueLine.substring(beginIndex, endIndex);
		String[] split = clipped.split(">");
		return split[split.length-1];
	}
	
	private static String parseGeneName(String geneNameLine) {
		int beginIndex = 0;
		int endIndex = geneNameLine.length() - 12;
		String clipped = geneNameLine.substring(beginIndex, endIndex);
		String[] split = clipped.split(">");
		return split[split.length-1];
	}
	
	private static String parseKEGGGene(String KEGGGeneLine) {
		int beginIndex = 0;
		int endIndex = KEGGGeneLine.length() - 16;
		String clipped = KEGGGeneLine.substring(beginIndex, endIndex);
		String[] split = clipped.split(">");
		return split[split.length-1];
	}
	
	private static String parseEnsemble(String ensembleLine) {
		int beginIndex = 0;
		int endIndex = ensembleLine.length() - 16;
		String clipped = ensembleLine.substring(beginIndex, endIndex);
		String[] split = clipped.split(">");
		return split[split.length-1];
	}
	
	public static Table loadBioCompendiumFile(File file) throws IOException {
		if (!file.exists()) return null;
		
		BufferedReader bufferReader = null;
		try {
			bufferReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String[] columns = {"KEGG Pathway ID","KEGG Pathway Name","Adjusted P-Value","Gene Name","KEGG Gene","Ensembl Gene"};
		
		ArrayList<String> identifiers = new ArrayList<String> ();
		Collections.addAll(identifiers, columns);
		
		Table bioCompendiumResults = new Table(identifiers);
	
		String patternPathwayID = "<td rowspan=\\\"\\d+\"><font face=\\\"Arial\\\" size=\\\"2\\\" color=\\\"#000000\\\"><a href=\\\".*";
		Pattern pPathwayID = Pattern.compile(patternPathwayID);
		
		String patternGeneName = "<td><font face=\\\"Arial\\\" size=\\\"2\\\" color=\\\"#000000\\\">[A-Z0-9]*<\\/font><\\/td>";
		Pattern pGeneName = Pattern.compile(patternGeneName);
			
		String patternKEGGGene = "<td><font face=\\\"Arial\\\" size=\\\"2\\\" color=\\\"#000000\\\"><a class=biocompendium href=\\\".+?\\\">[0-9]*<\\/a><\\/font><\\/td>";
		Pattern pKEGGGene = Pattern.compile(patternKEGGGene);
		
		String patternEnsemble = "<td><font face=\\\"Arial\\\" size=\\\"2\\\" color=\\\"#000000\\\"><a class=\\\"biocompendium\\\" href=\\\"http:\\/\\/www.ens.*?\">[A-Za-z0-9]*";
		Pattern pEnsemble = Pattern.compile(patternEnsemble);
		
		String patternEnd = "</tr>";
		
		String line;
		int pathways = 0;
		int genes = 0;
		while((line = bufferReader.readLine()) != null) {
		
			Matcher matcherPathwayID = pPathwayID.matcher(line);
			
			if(matcherPathwayID.find()) {
				
				pathways++;
				
				String pathwayId = parsePathwayID(line);
				System.out.println("Pathway: " + pathwayId);
				
				line = bufferReader.readLine();
				String pathwayName = parsePathwayName(line);
				System.out.println("Pathway Name: " + pathwayName);
				
				line = bufferReader.readLine();
				String adjustedPvalue = parseAdjustedPvalue(line);
				System.out.println("Pathway Name: " + adjustedPvalue);
				
				boolean endPathwayFound = false;
				
				while(!endPathwayFound) {
					while((line = bufferReader.readLine()) != null){
						Matcher matcherGeneName = pGeneName.matcher(line);
						
						//System.out.println(line);
						if(matcherGeneName.find()) {
							genes++;
							// System.out.println("Found Gene Name! " + genes);
							String geneName = parseGeneName(line);
							System.out.println("\t Gene Name -> " + geneName);
							
							line = bufferReader.readLine();
							String KEGGGene = parseKEGGGene(line);
							System.out.println("\t KEGG Gene Found -> " + KEGGGene);
							
							line = bufferReader.readLine();
							String ensembl = parseEnsemble(line);
							System.out.println("\t Ensemble -> " + ensembl);
							
							Object[] a = {pathwayId, pathwayName, adjustedPvalue, geneName, KEGGGene, ensembl};
							
							ArrayList<Object> row = new ArrayList<Object>(Arrays.asList(a));
							
							bioCompendiumResults.addRow(row);
							
						}else if(line.contentEquals("</tr>")) {
							System.out.println("EndPathway -> " + line.length() + " - " + line);
							endPathwayFound = true;
							break;
						}
						
					}
					
				}
				
				System.out.println("Genes: " + genes);
				System.out.println("Pathways: " + pathways);
				
			}
			
		}
		return bioCompendiumResults;
		
	}
	
	public static ArrayList<Object[]> loadStructuredFile(File file, String seperator, boolean skipHeader) throws IOException {
		
		if (!file.exists()) 
			return null;
		
		ArrayList<Object[]> data = new ArrayList<Object[]>();
		
		BufferedReader bufferReader = new BufferedReader(new FileReader(file));
		
		String line = null;
		
		if(skipHeader)
			line = bufferReader.readLine();
		
		while((line = bufferReader.readLine()) != null) {
			String[] parsedLine = line.split(seperator);
			data.add(parsedLine);
		}
		bufferReader.close();
		
		return data;
			
	}
	
	public static String[] loadCSVColumnHeaders(File file, String seperator) throws IOException {
		ArrayList<String> identifiers = new ArrayList<String>();
		BufferedReader bufferReader = new BufferedReader(new FileReader(file));
		String line = bufferReader.readLine();
		String[] parsedLine = line.split(seperator);
		identifiers.addAll(Arrays.asList(parsedLine));
		bufferReader.close();
		int len = identifiers.size();
		return identifiers.toArray(new String[len]);	
	}

	public static String getLocationTemporary() {
		Path currentRelativePath = Paths.get("");
		String location = currentRelativePath.toAbsolutePath().toString() + "\\temp";
		return location;
	}
	private static final char CHAR_QUOTATION = '\"';
	private static boolean isEncasedInQuotations(String str) {
		int len = str.length();
		return len > 2 ? str.charAt(0) == CHAR_QUOTATION && str.charAt(len-1) == CHAR_QUOTATION : false;
	}
	
	private static boolean isLineEncasedInQuotations(String[] parsedLine) {
		boolean allAreEncasedInQuotations = true;

		return allAreEncasedInQuotations;
	}
	
	private static String removeEncasing(String str){
		int len = str.length();
		return len > 2 ? str.substring(1,len-1) : "";
	}
	
	private static String[] parseCSVLine(String line){
		String[] splitLine = line.split(CHAR_COMMA);
		for(int i = 0; i < splitLine.length; i++) {
			String cell = splitLine[i];
			if(isEncasedInQuotations(cell)) {
				splitLine[i] = removeEncasing(cell);
			}
		}
		return splitLine;
	}
	
	private static final String CHAR_COMMA = ",";
	public static ArrayList<Object[]> loadCSV(File file, boolean skipHeader) throws IOException {
		
		if (!file.exists()) 
			return null;
		
		ArrayList<Object[]> data = new ArrayList<Object[]>();
		
		BufferedReader bufferReader = new BufferedReader(new FileReader(file));
		String line = null;
		
		if(skipHeader)
			line = bufferReader.readLine();
		
		while((line = bufferReader.readLine()) != null) {
			String[] parsedLine = parseCSVLine(line);
			System.out.println(Arrays.toString(parsedLine));
			data.add(parsedLine);
		}
		
		bufferReader.close();
		
		
		return data;
			
	}
	
	public static ArrayList<Object[]> loadTextFile(File file, boolean skipHeader) throws IOException {
		if (!file.exists()) 
			return null;
		
		ArrayList<Object[]> data = new ArrayList<Object[]>();
		
		BufferedReader bufferReader = new BufferedReader(new FileReader(file));
		String line = null;
		
		if(!skipHeader)
			line = bufferReader.readLine();
		
		while((line = bufferReader.readLine()) != null) {
			Object[] row = new Object[1];
			row[0] = line;
			data.add(row);
		}
		bufferReader.close();
		
		return data;
	}
	
	public static ArrayList<Object[]> loadGarudaLoadDataRequest(File file) throws IOException
	{
		ArrayList<Object[]> loadedFile = new ArrayList<Object[]>();

		return loadedFile; 
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
	
	public static File writeOutTable(Table writeOut, String location) throws IOException {
		
		File file = new File(location);
		
		if(file.exists())
			file.delete();
		
		
		FileWriter fr = new FileWriter(file, true);
		
		ArrayList<String> identifiers = writeOut.getIdentifiers();
		
		// write header
		for(int c = 0; c < identifiers.size(); c++) {
			String cell = "\"" + identifiers.get(c) + "\"";
			if(c < identifiers.size()-1)
				cell += ",";
			fr.write(cell);
		}
		fr.write("\n");
		
		// write data
		for(int r = 0; r < writeOut.rowCount(); r++) {
			ArrayList<Object> row = writeOut.getRow(r);
			for(int c = 0; c < writeOut.columnCount(); c++) {
				String cell = "\"" + writeOut.getRow(r).get(c) + "\"";
				if(c < identifiers.size()-1)
					cell += ",";
				fr.write(cell);
			}
			fr.write("\n");
		}
		fr.close();

		
		return file;
	}
	
	private static String getJarPath() {
		File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		return jarDir.getAbsolutePath();
	}
	
	public static File writeOutString(String data, String fileName) throws IOException {
		
		String path = fileName;
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

