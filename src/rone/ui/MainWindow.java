package rone.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import rone.backend.SearchDialog;
import rone.backend.SearchInterface;
import rone.backend.TargetMineSearchInterface;
import rone.backend.garudahandler.GarudaHandler;
import rone.filemanager.Database;
import rone.filemanager.FileManager;
import rone.filemanager.Table;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import java.awt.Toolkit;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.net.*;
import java.io.*;

import org.json.*;
import java.sql.*; 
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.derby.jdbc.EmbeddedDriver;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import java.awt.ScrollPane;

public class MainWindow implements ActionListener {
	
	private JScrollPane scrollPane;
	private JMenuBar menuBar;
	
	private final ActionFileImportFromFile actionImportActionFromFile = new ActionFileImportFromFile();
	private final ActionFileExportToFile actionExportActionToFile = new ActionFileExportToFile();
	private final ActionFileExportToGarudaAsGenelist actionExportGarudaToGenelist = new ActionFileExportToGarudaAsGenelist();
	private final ActionFileExportToGarudaAsEnsemble actionExportGarudaToEnsemble = new ActionFileExportToGarudaAsEnsemble();
	private final ActionFileExportToTable actionFileExportToTable = new ActionFileExportToTable();
	private final ActionSearchTargetMineWithGeneSymbols actionTargetMineWithGeneSymbols = new ActionSearchTargetMineWithGeneSymbols();
	private final ActionSearchPercellomeWithProbeID actionPercellomeProbeID = new ActionSearchPercellomeWithProbeID();
	private final ActionSearchReactomeWithGeneSymbols actionReactomeWithGeneSymbols = new ActionSearchReactomeWithGeneSymbols();
	private final ActionSearchBioCompendiumWithSelect actionBioCompendiumWithSelect = new ActionSearchBioCompendiumWithSelect();
	private final ActionTableJoin actionTableJoin = new ActionTableJoin();
	private final ActionTableClear actionTableClear = new ActionTableClear();
	
	private MainWindow mMainWindow;
	private JFrame mMainWindowJFrame;
	private GarudaHandler mGarudaHandler;
	private final Action mFileImportAction = new FileImportAction();
	private final Action mFileExportTableAction = new FileExportTableAction();
	private final Action mFileClearTableAction = new FileClearTableAction();
	private final Action mReactomeImportAction =  new ReactomeImportAction();
	private final Action mPercellomeImportAction =  new PercellomeImportAction();
	private final Action mTargetMineImportAction = new TargetMineImportAction();
	private final GarudaDiscoverActionGenelist mGarudaDiscoverActionGenelist = new GarudaDiscoverActionGenelist();
	private final GarudaDiscoverActionEnsemble mGarudaDiscoverActionEnsemble = new GarudaDiscoverActionEnsemble();
	
	private DatabaseTabbedPane mDatabaseTabbedPane;
	private JMenu mnFile;
	private JMenu mnImport;
	private JMenuItem mntmFromFile;
	private JMenu mnExport;
	private JMenuItem mntmToFile;
	private JMenuItem mntmToTable;
	private JMenu mnToGaruda;
	private JMenuItem mntmGenelist;
	private JMenuItem mntmEnsemble;
	private JMenuItem mntmClose;
	private JMenu mnSearch;
	private JMenu mnPercellome;
	private JMenuItem mntmPercellomeProbeIDs;
	private JMenu mnTargetMine;
	private JMenuItem mntmTargetMineGeneSymbols;
	private JMenu mnReactome;
	private JMenuItem mntmReactomeGeneSymbols;
	private JMenu mnBioCompendium;
	private JMenuItem mntmWithSelect;
	private JMenu mnJoin;
	private JMenuItem mntmJoinTable;
	private JMenuItem mntmClear;
	private JTable table;
	
	static final String TABBED_PANE_NAME = "TABBED_PANE";
	private DatabaseTabbedPane getTabbedPane() {
		Component[] components = this.mMainWindowJFrame.getContentPane().getComponents();
		for(Component component : components) {
			System.out.println(component.getClass().getName());
			if(component.getName() == TABBED_PANE_NAME) {
				return (DatabaseTabbedPane) component;
			}
		}
		return null;
	}
	
	
	private void initialize() {
		mMainWindow = this;
		mMainWindowJFrame = new JFrame();
		mDatabaseTabbedPane = new DatabaseTabbedPane(JTabbedPane.TOP);
		mDatabaseTabbedPane.setName("mDatabaseTabbedPane");
		
		Path currentRelativePath = Paths.get("");
		String iconLocation = currentRelativePath.toAbsolutePath().toString() + "\\rone_logo.png";
		mMainWindowJFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconLocation));mMainWindowJFrame.setTitle("RONE");
		mMainWindowJFrame.setBounds(100, 100, 820, 540);
		mMainWindowJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		JMenuBar menuBar = new JMenuBar();
		mMainWindowJFrame.setJMenuBar(menuBar);
		{
			mnFile = new JMenu("File");
			menuBar.add(mnFile);
			{
				mnImport = new JMenu("Import");
				mnFile.add(mnImport);
				{
					mntmFromFile = new JMenuItem("from File");
					mntmFromFile.setAction(actionImportActionFromFile);
					mnImport.add(mntmFromFile);
				}
			}
			{
				mnExport = new JMenu("Export");
				mnFile.add(mnExport);
				{
					mntmToFile = new JMenuItem("to File");
					mnExport.add(mntmToFile);
				}
				{
					mntmToTable = new JMenuItem("to Table");
					mnExport.add(mntmToTable);
				}
				{
					mnToGaruda = new JMenu("to Garuda");
					mnExport.add(mnToGaruda);
					{
						mntmGenelist = new JMenuItem("Genelist");
						mnToGaruda.add(mntmGenelist);
					}
					{
						mntmEnsemble = new JMenuItem("Ensemble");
						mnToGaruda.add(mntmEnsemble);
					}
				}
			}
			{
				mntmClose = new JMenuItem("Close");
				mnFile.add(mntmClose);
			}
		}
		{
			mnSearch = new JMenu("Search");
			menuBar.add(mnSearch);
			{
				mnPercellome = new JMenu("Percellome");
				mnSearch.add(mnPercellome);
				{
					mntmPercellomeProbeIDs = new JMenuItem("with Probe IDs (Affy IDs)");
					mnPercellome.add(mntmPercellomeProbeIDs);
				}
			}
			{
				mnTargetMine = new JMenu("TargetMine");
				mnSearch.add(mnTargetMine);
				{
					mntmTargetMineGeneSymbols = new JMenuItem("with Gene Symbols");
					mnTargetMine.add(mntmTargetMineGeneSymbols);
				}
			}
			{
				mnReactome = new JMenu("Reactome");
				mnSearch.add(mnReactome);
				{
					mntmReactomeGeneSymbols = new JMenuItem("with Gene Symbols");
					mnReactome.add(mntmReactomeGeneSymbols);
				}
			}
			{
				mnBioCompendium = new JMenu("bioCompendium (unavaliable)");
				mnSearch.add(mnBioCompendium);
				{
					mntmWithSelect = new JMenuItem("with Select");
					mnBioCompendium.add(mntmWithSelect);
				}
			}
		}
		{
			mnJoin = new JMenu("Table");
			menuBar.add(mnJoin);
			{
				mntmJoinTable = new JMenuItem("Join Table");
				mnJoin.add(mntmJoinTable);
			}
			{
				mntmClear = new JMenuItem("Clear");
				mnJoin.add(mntmClear);
			}
		}
		JMenuItem mntmNewMenuItem = new JMenuItem("Discover");
		JPopupMenu popupMenu = new JPopupMenu("Discover");
		popupMenu.add(mntmNewMenuItem);
	
	}
	

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }
	
    public static String[] parseDescription(String description) {
		String[] find = {"<<<BiologicalProcess>>>.*<<<CellularComponent>>>", 
                		 "<<<CellularComponent>>>.*<<<MolecularFunction>>>", 
                		 "<<<MolecularFunction>>>.*"};

		String[] found = {"", "", ""};

		for (int i = 0; i < find.length; i++) { 
			Pattern pattern = Pattern.compile(find[i], Pattern.DOTALL | Pattern.MULTILINE);
			Matcher m = pattern.matcher(description);
			System.out.println("----------");
			if (m.find()) {
				System.out.println(m.group(0));
				found[i] = m.group(0);
			} else {
				System.out.println("None");
				found[i] = null;
			}
		}
		
		if(found[0] != null) found[0] = found[0].replace("\n", "").substring(23, found[0].length()-23);
		else found[0] = "";
		
		if(found[1] != null) found[1] = found[1].replace("\n", "").substring(23, found[1].length()-23);
		else found[1] = "";
		
		if(found[2] != null) found[2] = found[2].replace("\n", "").substring(23);
		else found[2] = "";
		
		return found;
    }
    
	
    public static void main(String[] args) {
		
			
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {	
					MainWindow window = new MainWindow();
					window.mMainWindowJFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	
    public MainWindow() {
		initialize();
		
		mMainWindowJFrame.getContentPane().setLayout(new BoxLayout(mMainWindowJFrame.getContentPane(), BoxLayout.X_AXIS));
		
		try {
			//mDatabaseTabbedPane.setFillsViewportHeight(true);
			this.mGarudaHandler = new GarudaHandler(this);
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			JOptionPane.showMessageDialog(mMainWindowJFrame, "Was unable to connect to Garuda Platform. Please restart to connect.");
			e.printStackTrace();
		}
		
		mDatabaseTabbedPane = new DatabaseTabbedPane(JTabbedPane.TOP);
		mDatabaseTabbedPane.setName(TABBED_PANE_NAME);
		mMainWindowJFrame.getContentPane().add(mDatabaseTabbedPane);
		
	}
    
    
		
	@Override
	public void actionPerformed(ActionEvent e) {


	}
	
	
	public void loadTable(String tableName, String[] columnIdentifiers, ArrayList<Object[]> tableToLoad) {
		
		mMainWindowJFrame.getContentPane().add(mDatabaseTabbedPane);
		Database.Table importTable;
		try {
			importTable = Database.getInstance().createTable(tableName, columnIdentifiers, new int[]{});
			importTable.insertRows(tableToLoad);
			mDatabaseTabbedPane.importDatabaseTable(tableName, columnIdentifiers, importTable, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(mDatabaseTabbedPane.isEmpty()) {
			/*Database.Table importTable = Database.getInstance().createTable(tableName, columnIdentifiers, new int[]{});
			importTable.insertRows(tableToLoad);
			mDatabaseTabbedPane.importDatabaseTable(importTable);*/
		} else {
			/*ImportDataDialog importSelection = new ImportDataDialog(mMainWindowJFrame, fromWhere, tox_id, inc_id) ;
			importSelection.setVisible(true);	
			
			String[] data = importSelection.getData();
			if(data[0] != null) {
				String keyTox = data[0];
				String keyInc = data[1];
				mDatabaseTabbedPane.importTable(keyTox, keyInc, incomingTable);*/
		}
	}
		
	
	
	private void loadList(File file, String header) {
		Table listTable = null;
		try {
			listTable = FileManager.loadListFile(file, header);
			mDatabaseTabbedPane.setTable(listTable);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	
	private void loadAGCTScenario(File file) {
		Table scenario;
		try {
			scenario = FileManager.loadAGCTScenario(file);
			loadTable(scenario, "Import AGCT Scenario");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	
	private void loadDataFile(File file, String seperator) {
		ArrayList<Object[]> data = new ArrayList<Object[]>();
				
		try {
			data = FileManager.loadDataFile(file, seperator);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		String[] columnIdentifiers = (String[])data.get(0);
		data.remove(0);
		loadTable(file.getName(), columnIdentifiers, data);
	}
	
	private void loadBioCompendium(File file) {
		Table data;
		try {
			data = FileManager.loadBioCompendiumFile(file);
			loadTable(data, "Import File Data");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private void loadFile(File file, String contents) {
		
		if(!file.exists()) return;
		
		System.out.println(contents);
		
		switch (contents) {
				
			case "CSV": 
				loadDataFile(file, ",");
				break;
				
			case "Tab":
				loadDataFile(file, "\t");
				break;
		}
		
	}
	
	public void windowClosing(WindowEvent e) {
		System.out.println("window closing");
	}

	
	private class FileImportAction extends AbstractAction {
		
		final JFileChooser fc = new JFileChooser();
		
		public FileImportAction() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Import a file");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			//In response to a button click:
			int returnVal = fc.showOpenDialog(mMainWindowJFrame);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"CSV", "Tab Delimited Text"};
				String content = (String)JOptionPane.showInputDialog(
				                    mMainWindowJFrame,
				                    "Complete the sentence:\n",
				                    "File Type",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "Genelist");
				
				loadFile(file, content);
			}
			
		}
	}
	
	private void startDiscovery(String contence, String extension) {
		String[] data = mDatabaseTabbedPane.getUniqueSelected();
		
		String list = new String("");
		for(int i = 0; i < data.length; i++) {
			list += data[i] + "\n";
		}
		
		int time = (int) new Date().getTime();
		
		String fileName = contence + "_" + time + "." + extension;
		File file = null;
		
		try {
			file = FileManager.writeOutString(list, fileName);
			file.deleteOnExit();
			mGarudaHandler.garudaDiscover(file, contence);
		} catch (IOException e) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(
			        null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			
		}
		
		
	}
	
	boolean hasValidSelection() {
		
		if(mDatabaseTabbedPane.isEmpty()) {
			JOptionPane.showMessageDialog(mMainWindowJFrame, "There is no data in the table.");
			return false;
		}
		
		if(!mDatabaseTabbedPane.hasSelection()) {
			JOptionPane.showMessageDialog(mMainWindowJFrame, "Select data from the table to use.");
			return false;
		}
		
		return true;
	}
		
	private class GarudaDiscoverActionGenelist extends AbstractAction {
		public GarudaDiscoverActionGenelist() {
			putValue(NAME, "Genelist");
			putValue(SHORT_DESCRIPTION, "Discover Garuda Genelist");
		}
		public void actionPerformed(ActionEvent ae) {
			
			if(!hasValidSelection()) 
				return;

			startDiscovery("genelist", "txt");
			
		}
	}
	
	private class GarudaDiscoverActionEnsemble extends AbstractAction {
		
		public GarudaDiscoverActionEnsemble() {
			putValue(NAME, "Ensemble");
			putValue(SHORT_DESCRIPTION, "Discover Garuda Ensemble");
			
	
		}
		
		public void actionPerformed(ActionEvent ae) {
			
			if(!hasValidSelection()) 
				return;
			
			startDiscovery("ensemble", "txt");
			
		}
	}

	private class TargetMineImportAction extends AbstractAction {
		
		public TargetMineImportAction() {
			putValue(NAME, "Import TargetMine");
			putValue(SHORT_DESCRIPTION, "Import selected from TargetMine");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = mDatabaseTabbedPane.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}

			
		}
	}
	
	private class ReactomeImportAction extends AbstractAction {
		public ReactomeImportAction() {
			putValue(NAME, "Import Reactome");
			putValue(SHORT_DESCRIPTION, "Import selected from Reactome");
		}
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = mDatabaseTabbedPane.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			

			
		}
	}
	
	private class PercellomeImportAction extends AbstractAction {
		public PercellomeImportAction() {
			putValue(NAME, "Import Percellome");
			putValue(SHORT_DESCRIPTION, "Import selected from Percellome");
		}
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = mDatabaseTabbedPane.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			

			
		}
	}
	
	private class FileExportTableAction extends AbstractAction {
		
		public FileExportTableAction() {
			putValue(NAME, "Export");
			putValue(SHORT_DESCRIPTION, "Export selected to CSV");
		}
		
		public void actionPerformed(ActionEvent e) {
			/*if(mToxicologyTable.isEmpty()) {
				JOptionPane.showMessageDialog(mMainWindowJFrame, "There is no data in the table.");
				return;
			}*/
			
			JFrame parentFrame = new JFrame();
			 
			File fileToSave;
			boolean hasFile = false;
			do {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter() {

					   public String getDescription() {
					       return "CSV (*.csv)";
					   }

					   public boolean accept(File f) {
					       if (f.isDirectory()) {
					           return true;
					       } else {
					           String filename = f.getName().toLowerCase();
					           return filename.endsWith(".csv");
					       }
					   }
					});
				
				fileChooser.setDialogTitle("Specify a file to save");   
				int userSelection = fileChooser.showSaveDialog(parentFrame);
				if (userSelection != JFileChooser.APPROVE_OPTION)
				    return;
				
				fileToSave = fileChooser.getSelectedFile();
				
				if(fileToSave.exists()) {
					System.out.println("File exists!");
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to overwrite this file?","Warning", dialogButton);
					if(dialogResult == JOptionPane.YES_OPTION) {
						if(!fileToSave.delete()) {
							continue;
						}
					}
				}else {
					System.out.println("File does not exists!");
				}
				
				try {
					fileToSave.createNewFile();
					hasFile = true;
				} catch (IOException e1) {
					e1.printStackTrace();
					continue;
				}
				
			} while(!hasFile);
			
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
		    
		    if(mDatabaseTabbedPane.hasSelection()) {
				
			    BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(fileToSave));
					int cols[] = mDatabaseTabbedPane.getSelectedColumns();
					
					String[] id = mDatabaseTabbedPane.getIdentifiers();
					
					String header = "";
					for(int i = 0; i < cols.length-1; i++) {
						int index = cols[i];
						header = header + id[index] + ",";
					}
					int index = cols[cols.length-1];
					header = header + id[index] + "\n"; 
					writer.write(header);
					
					System.out.println(header);
					
				    int rows[] = mDatabaseTabbedPane.getSelectedRows();
				    for(int r = 0; r < rows.length; r++) {
				    	String line = "";
				    	for(int c = 0; c < cols.length-1; c++) {
				    		line = line + "\"" + mDatabaseTabbedPane.getCell(r, c) + "\"" + ",";
				    	}
				    	line = line + mDatabaseTabbedPane.getCell(r, cols.length-1) + "\n";
				    	writer.write(line);
				    }
					writer.close();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			    
			    
			} else {
				
			}
			
		}
		
	}
	
	private class FileClearTableAction extends AbstractAction {
		
		public FileClearTableAction() {
			putValue(NAME, "Clear");
			putValue(SHORT_DESCRIPTION, "Clear the contence of the table");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			if(!mDatabaseTabbedPane.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(null,
						"Are you sure you would like to discard the current table?", "Clear Confirm", JOptionPane.YES_NO_OPTION);
				
				if(result != JOptionPane.YES_OPTION)
					return;
				
			}
			
			try {
				mDatabaseTabbedPane.clearTable();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	
	}
	
	private class ActionFileImportFromFile extends AbstractAction {
		public ActionFileImportFromFile() {
			putValue(NAME, "from File");
		}
		public void actionPerformed(ActionEvent e) {
			//In response to a button click:
			final JFileChooser fc = new JFileChooser();
			
			int returnVal = fc.showOpenDialog(mMainWindowJFrame);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"CSV", "Tab Delimited Text"};
				String content = (String)JOptionPane.showInputDialog(
				                    mMainWindowJFrame,
				                    "Complete the sentence:\n",
				                    "File Type",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "Genelist");
				
				loadFile(file, content);
			}
		}
	}
	
	private class ActionFileExportToFile extends AbstractAction {
		public ActionFileExportToFile() {
			putValue(NAME, "to File");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionFileExportToGarudaAsGenelist extends AbstractAction {
		public ActionFileExportToGarudaAsGenelist() {
			putValue(NAME, "as Genelist");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionFileExportToGarudaAsEnsemble extends AbstractAction {
		public ActionFileExportToGarudaAsEnsemble() {
			putValue(NAME, "as Ensemble");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionFileExportToTable extends AbstractAction {
		public ActionFileExportToTable() {
			putValue(NAME, "to Table");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchTargetMineWithGeneSymbols extends AbstractAction {
		public ActionSearchTargetMineWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchPercellomeWithProbeID extends AbstractAction {
		public ActionSearchPercellomeWithProbeID() {
			putValue(NAME, "with Probe IDs (Affy ID)");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchReactomeWithGeneSymbols extends AbstractAction {
		public ActionSearchReactomeWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionSearchBioCompendiumWithSelect extends AbstractAction {
		public ActionSearchBioCompendiumWithSelect() {
			putValue(NAME, "with Select");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionTableJoin extends AbstractAction {
		public ActionTableJoin() {
			putValue(NAME, "Join");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class ActionTableClear extends AbstractAction {
		public ActionTableClear() {
			putValue(NAME, "Clear");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}


}
