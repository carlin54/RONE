package rone.filemanager;

import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;

import rone.filemanager.Database.Table;

import org.pf4j.Extension;
import org.pf4j.Plugin;

import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

public class FileManager {
	
	public static void exportCSV(File file, ArrayList<Object[]> data) throws IOException  {
		if(file.exists()) {
			boolean success = file.delete();
			if(!success)
				throw new IOException("Could not delete existing file.");
		}
		
		if(!file.createNewFile())
			throw new IOException("Create file required to write out data.");
		
		
		if(!file.canWrite())
			throw new IOException("Cannot write to newly created file.");
		
		
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < data.size(); i++) {
			
			Object[] row = data.get(i);
			for(int j = 1; j < row.length-1; j++) {
				sb.append("\"" + row[j-1].toString() + "\",");
			}
			sb.append("\"" + row[row.length-1].toString() + "\"\n");
		}
		PrintWriter writer = new PrintWriter(file);
		writer.write(sb.toString());
		writer.close();
	}
	
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
	
	public static String getSearchPluginDirectory() {
		Path currentRelativePath = Paths.get("");
		String location = currentRelativePath.toAbsolutePath().toString() + "\\plugins";
		return location;
	}
	
	
	public class ExtensionLoader<C> {

		  public C LoadClass(String directory, String classpath, Class<C> parentClass) throws ClassNotFoundException {
		    File pluginsDir = new File(System.getProperty("user.dir") + directory);
		    for (File jar : pluginsDir.listFiles()) {
		      try {
		        ClassLoader loader = URLClassLoader.newInstance(
		            new URL[] { jar.toURL() },
		            getClass().getClassLoader()
		        );
		        Class<?> clazz = Class.forName(classpath, true, loader);
		        Class<? extends C> newClass = clazz.asSubclass(parentClass);
		        // Apparently its bad to use Class.newInstance, so we use 
		        // newClass.getConstructor() instead
		        Constructor<? extends C> constructor = newClass.getConstructor();
		        return constructor.newInstance();
		        
		      } catch (ClassNotFoundException e) {
		        // There might be multiple JARs in the directory,
		        // so keep looking
		    	  System.out.println(jar.getAbsoluteFile());
		        continue;
		      } catch (MalformedURLException e) {
		        e.printStackTrace();
		      } catch (NoSuchMethodException e) {
		        e.printStackTrace();
		      } catch (InvocationTargetException e) {
		        e.printStackTrace();
		      } catch (IllegalAccessException e) {
		        e.printStackTrace();
		      } catch (InstantiationException e) {
		        e.printStackTrace();
		      }
		    }
		    throw new ClassNotFoundException("Class " + classpath
		        + " wasn't found in directory " + System.getProperty("user.dir") + directory);
		  }
		}
	
	public static boolean clearTemp() {
		File tempDir = new File(getTemporaryDirectory());
		if(tempDir.exists()) {
			File[] files = tempDir.listFiles();
			
			for(File file : files) {
				file.delete();
			}
			
			return true;
		}else {
			return tempDir.mkdir();
		}
		
	}
	
	public static String getTemporaryDirectory() {
		Path currentRelativePath = Paths.get("");
		String location = currentRelativePath.toAbsolutePath().toString() + "\\temp";
		return location;
	}
	
	public static String getIconDirectory() {
		Path currentRelativePath = Paths.get("");
		String location = currentRelativePath.toAbsolutePath().toString() + "\\icons";
		return location;
	}
	
	private static final char CHAR_QUOTATION = '\"';
	private static boolean isEncasedInQuotations(String str) {
		int len = str.length();
		return len > 2 ? str.charAt(0) == CHAR_QUOTATION && str.charAt(len-1) == CHAR_QUOTATION : false;
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

