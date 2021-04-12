/*
 * RONE
 * Copyright (C) [2021] [Carlin. R. Connell]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rone.filemanager;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import rone.ui.MainWindow;

public class FileManager {
	
	private static final char CHAR_QUOTATION = '\"';
	private static final String CHAR_COMMA = ",";
	
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
	
	public static String getConfigFileLocation() {
		Path currentRelativePath = Paths.get("");
		return currentRelativePath.toAbsolutePath().toString() + "\\config.txt";
	}
	
	private static Properties SINGLETON_PROPERTIES = null;
	
	public final static Properties getProperties(){
		if(SINGLETON_PROPERTIES == null) {
			SINGLETON_PROPERTIES = loadProperties();
		}
		return SINGLETON_PROPERTIES;
	}
	
	private static Properties loadProperties() {
		String location = getConfigFileLocation();
		File configFile = new File(location);

		Properties props = new Properties();
		try {
			configFile.createNewFile();
		    FileReader reader = new FileReader(configFile);
		    props.load(reader);
		    reader.close();
		} catch (FileNotFoundException ex) {
			MainWindow.showError(ex);
		} catch (IOException ex) {
			MainWindow.showError(ex);
		}
		return props;
		
	}
	
	public static void storeProperties(Properties properties) {
		File configFile = new File(getConfigFileLocation());
		FileWriter writer;
		try {
			writer = new FileWriter(configFile);
			properties.store(writer, "RONE Application Properties");
			writer.close();
		} catch (IOException e) {
			MainWindow.showError(e);
		}
		
	}
	
}

