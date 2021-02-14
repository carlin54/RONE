package rone.ui;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import rone.backend.PercellomeSearchInterface;
import rone.backend.PercellomeSearchInterface.SearchMode;
import rone.backend.PercellomeSearchInterface.Species;
import rone.backend.ReactomeSearchInterface;
import rone.backend.TargetMineSearchInterface;
import rone.backend.garudahandler.GarudaHandler;
import rone.backend.Search;
import rone.filemanager.FileManager;


import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import java.awt.Toolkit;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.sql.SQLException;

import javax.swing.JTabbedPane;


public class MainWindow {
	
	private final ActionFileImportFromFile actionImportActionFromFile = new ActionFileImportFromFile();
	private final ActionFileExportToFile actionExportToFile = new ActionFileExportToFile();
	private final ActionFileExportToGarudaStartDiscovery actionExportGarudaToGenelistTXT = new ActionFileExportToGarudaStartDiscovery("as Genelist (txt)", FileContence.GENELIST, FileExtension.TXT);
	private final ActionFileExportToGarudaStartDiscovery actionExportGarudaToEnsembleTXT = new ActionFileExportToGarudaStartDiscovery("as Genelist (csv)", FileContence.GENELIST, FileExtension.CSV);
	private final ActionFileExportToGarudaStartDiscovery actionExportGarudaToGenelistCSV = new ActionFileExportToGarudaStartDiscovery("as Ensemble (txt)", FileContence.ENSEMBLE, FileExtension.TXT);
	private final ActionFileExportToGarudaStartDiscovery actionExportGarudaToEnsembleCSV = new ActionFileExportToGarudaStartDiscovery("as Ensemble (csv)", FileContence.ENSEMBLE, FileExtension.CSV);
	private final ActionFileExportToTable actionFileExportToTable = new ActionFileExportToTable();
	private final ActionSearchTargetMineWithGeneSymbols actionTargetMineWithGeneSymbols = new ActionSearchTargetMineWithGeneSymbols();
	private final ActionSearchPercellomeWithProbeIds actionPercellomeWithProbeID = new ActionSearchPercellomeWithProbeIds();
	private final ActionSearchPercellomeWithGeneSymbols actionPercellomeWithGeneSymbols = new ActionSearchPercellomeWithGeneSymbols();
	private final ActionSearchReactomeWithGeneSymbols actionReactomeWithGeneSymbols = new ActionSearchReactomeWithGeneSymbols();

	private final ActionTableJoin actionTableJoin = new ActionTableJoin();
	
	private JFrame mMainWindowJFrame;
	private GarudaHandler mGarudaHandler;
	
	private DatabaseTabbedPane mDatabaseTabbedPane;
	private JMenu mnFile;
	private JMenu mnImport;
	private JMenuItem mntmFromFile;
	private JMenu mnExport;
	private JMenuItem mntmToFile;
	private JMenuItem mntmToTable;
	private JMenu mnToGaruda;
	private JMenuItem mntmGenelistTXT;
	private JMenuItem mntmGenelistCSV;
	private JMenuItem mntmClose;
	private JMenu mnSearch;
	private JMenu mnPercellome;
	private JMenuItem mntmPercellomeProbeIDs;
	private JMenuItem mntmPercellomeGeneSymbols;
	private JMenu mnTargetMine;
	private JMenuItem mntmTargetMineGeneSymbols;
	private JMenu mnReactome;
	private JMenuItem mntmReactomeGeneSymbols;
	private JMenu mnJoin;
	private JMenuItem mntmJoinTable;
	private JMenuItem mntmClear;
	
	static final String TABBED_PANE_NAME = "TABBED_PANE";
	private JMenuItem mntmEnsembleTXT;
	private JMenuItem mntmEnsembleCSV;
	
	enum FileExtension {
		TXT,
		CSV
	}
	
	enum FileContence {
		GENELIST,
		ENSEMBLE,
		CSV,
		TXT
	}
	
	
	public DatabaseTabbedPane getTabbedPane() {
		Component[] components = this.mMainWindowJFrame.getContentPane().getComponents();
		for(Component component : components) {
			//System.out.println(component.getClass().getName());
			if(component.getName() == TABBED_PANE_NAME) {
				return (DatabaseTabbedPane) component;
			}
		}
		return null;
	}
	
	public JFrame getFrame() {
		return this.mMainWindowJFrame;
	}
	

	private void initialize() {
		mMainWindowJFrame = new JFrame();
		mDatabaseTabbedPane = new DatabaseTabbedPane(JTabbedPane.TOP);
		mDatabaseTabbedPane.setName("mDatabaseTabbedPane");
		
		Path currentRelativePath = Paths.get("");
		String iconLocation = currentRelativePath.toAbsolutePath().toString() + "\\icons\\rone_icon.png";
		mMainWindowJFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconLocation));
		mMainWindowJFrame.setTitle("RONE");
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
					mntmToFile.setAction(actionExportToFile);
					mnExport.add(mntmToFile);
				}
				{
					mntmToTable = new JMenuItem("to Table");
					mntmToTable.setAction(actionFileExportToTable);
					mnExport.add(mntmToTable);
				}
				{
					mnToGaruda = new JMenu("to Garuda");
					mnExport.add(mnToGaruda);
					{
						mntmGenelistTXT = new JMenuItem();
						mntmGenelistTXT.setAction(actionExportGarudaToGenelistTXT);
						mnToGaruda.add(mntmGenelistTXT);
					}
					{
						mntmGenelistCSV = new JMenuItem();
						mntmGenelistCSV.setAction(actionExportGarudaToGenelistCSV);
						mnToGaruda.add(mntmGenelistCSV);
					}
					{
						mntmEnsembleTXT = new JMenuItem();
						mntmEnsembleTXT.setAction(actionExportGarudaToEnsembleTXT);
						mnToGaruda.add(mntmEnsembleTXT);
					}
					{
						mntmEnsembleCSV = new JMenuItem();
						mntmEnsembleCSV.setAction(actionExportGarudaToEnsembleCSV);
						mnToGaruda.add(mntmEnsembleCSV);
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
					mntmPercellomeProbeIDs.setAction(actionPercellomeWithProbeID);
					mnPercellome.add(mntmPercellomeProbeIDs);
				}
				{
					mntmPercellomeGeneSymbols = new JMenuItem("with Probe IDs (Affy IDs)");
					mntmPercellomeGeneSymbols.setAction(actionPercellomeWithGeneSymbols);
					mnPercellome.add(mntmPercellomeGeneSymbols);
				}
			}
			{
				mnTargetMine = new JMenu("TargetMine");
				mnSearch.add(mnTargetMine);
				{
					mntmTargetMineGeneSymbols = new JMenuItem("with Gene Symbols");
					mntmTargetMineGeneSymbols.setAction(actionTargetMineWithGeneSymbols);
					mnTargetMine.add(mntmTargetMineGeneSymbols);
				}
			}
			{
				mnReactome = new JMenu("Reactome");
				mnSearch.add(mnReactome);
				{
					mntmReactomeGeneSymbols = new JMenuItem("with Gene Symbols");
					mntmReactomeGeneSymbols.setAction(actionReactomeWithGeneSymbols);
					mnReactome.add(mntmReactomeGeneSymbols);
				}
			}
		}
		{
			mnJoin = new JMenu("Table");
			menuBar.add(mnJoin);
			{
				mntmJoinTable = new JMenuItem("Join Table");
				mntmJoinTable.setAction(actionTableJoin);
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
		
		
		connectToGaruda();
		
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
			//System.out.println("----------");
			if (m.find()) {
				//System.out.println(m.group(0));
				found[i] = m.group(0);
			} else {
				//System.out.println("None");
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
    
    public static void main(String args[]) {
    	
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {	
					MainWindow window = new MainWindow();
					window.mMainWindowJFrame.setVisible(true);
				} catch (Exception exception) {
					exception.printStackTrace();
					showError(exception);
				}
				
			}
		});
		
	}
    	
    public MainWindow() {
    	FileManager.clearTemp();
    	
		initialize();
		
		mMainWindowJFrame.getContentPane().setLayout(new BoxLayout(mMainWindowJFrame.getContentPane(), BoxLayout.X_AXIS));
		
		mDatabaseTabbedPane = new DatabaseTabbedPane(JTabbedPane.TOP);
		mDatabaseTabbedPane.setName(TABBED_PANE_NAME);
		mMainWindowJFrame.getContentPane().add(mDatabaseTabbedPane);
		
	}
	
	private boolean hasFile(File file) {
		return file != null && file.exists();
	}
	
	private void loadTable(File file, ArrayList<Object[]> tableToLoad) {
		String[] columnIdentifiers = (String[])tableToLoad.get(0);
		tableToLoad.remove(0);
		
		try {
			mDatabaseTabbedPane.addTab(file.getName(), columnIdentifiers, new int[0], tableToLoad);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			showError(sqlException);
		}
	}
	
	private void loadFile(File file, String contents) {
		
		if(!hasFile(file)) 
			return;
		
		//System.out.println(contents);
		ArrayList<Object[]> data = null;
		try {
			switch (contents) {
				case "CSV": 
					data = FileManager.loadCSV(file, false);
					loadTable(file, data);
					break;
					
				case "Tab":
					data = FileManager.loadStructuredFile(file, "\t", false);
					loadTable(file, data);
					break;
			}
		} catch (IOException e) {
			showError(e);
		}
		
	}
	
	private boolean isConnectedToGaruda() {
		return mGarudaHandler != null ? mGarudaHandler.isConnected() : false;
	}
	
	private boolean connectToGaruda() {
		try {
			this.mGarudaHandler = new GarudaHandler(this);
			return true;
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			this.mGarudaHandler = null;
			return false;
		}
	}
	
	private void startDiscovery(String contence, String extension) {
		
		if(!isConnectedToGaruda()) {
			boolean success = connectToGaruda();
			if(!success) {
				JOptionPane.showMessageDialog(mMainWindowJFrame,
					    "Unable to connect to the Garuda Platform. \r\nOpen the Garuda Platform to connect.",
					    "Connection Error",
					    JOptionPane.ERROR_MESSAGE);
				return; 
			}
		}
		
		String[] data = mDatabaseTabbedPane.getSelection();
		
		String list = new String("");
		for(int i = 0; i < data.length; i++) {
			list += data[i] + "\n";
		}
		
		int time = (int) new Date().getTime();
		String tempLocation = FileManager.getTemporaryDirectory();
		String fileName = tempLocation + "\\" + contence + "_" + time + "." + extension;
		File file = null;
		
		try {
			file = FileManager.writeOutString(list, fileName);
			file.deleteOnExit();
			mGarudaHandler.garudaDiscover(file, contence);
		} catch (IOException e) {
			e.printStackTrace();
			showError(e);
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
	
	private class ActionFileImportFromFile extends AbstractAction {

		private static final long serialVersionUID = 1L;
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

		private static final long serialVersionUID = 1L;
		public ActionFileExportToFile() {
			putValue(NAME, "to File");
		}
		public void actionPerformed(ActionEvent e) {
	        
	        if(!mDatabaseTabbedPane.hasTab()) {
	        	JOptionPane.showMessageDialog(mMainWindowJFrame,
	        		    "No table to export data from.",
	        		    "Export error",
	        		    JOptionPane.ERROR_MESSAGE);
	        	return;
	        }

	        JFileChooser chooser = new JFileChooser();
	        FileNameExtensionFilter filter = new FileNameExtensionFilter("Comma-Seperated Value Files", "csv", "csv");
	        chooser.setFileFilter(filter);
	        
	        int result = chooser.showSaveDialog(null);
	        File file = null;
	        if (result == JFileChooser.APPROVE_OPTION) {
	            file = chooser.getSelectedFile();
	        }
	        
	        DatabaseTabbedPane.Tab tab = mDatabaseTabbedPane.getActiveTab();
	        Object[] columnIdentifers = null;
	        ArrayList<Object[]> rows = null;
	        if(tab.hasSelection()) {
	        	columnIdentifers = tab.getSelectedColumnIdentifers();
		        rows = tab.getSelectedRows();
	        } else {
	        	columnIdentifers = tab.getColumnIdentifers();
		        rows = tab.getRows();
	        }
	        rows.add(0, columnIdentifers);
	        try {
				FileManager.exportCSV(file, rows);
			} catch (IOException e1) {
				showError(e1);
			}
		}
	}
	
	private class ActionFileExportToGarudaStartDiscovery extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		String mLabelText; 
		String mContence;
		String mExtension; 
		
		public ActionFileExportToGarudaStartDiscovery(String labelText, FileContence contence, FileExtension extension) {
			
			mLabelText = labelText; 
			mContence = contence.toString().toLowerCase(); 
			mExtension = extension.toString().toLowerCase(); 
			
			putValue(NAME, mLabelText);
		}
		public void actionPerformed(ActionEvent e) {
			
			if(!hasValidSelection()) 
				return;

			startDiscovery(mContence, mExtension.toString());
		}
	}
	
	private class ActionFileExportToTable extends AbstractAction {



		private static final long serialVersionUID = 1L;
		public ActionFileExportToTable() {
			putValue(NAME, "to Table");
		}
		public void actionPerformed(ActionEvent e) {
			
			if(!mDatabaseTabbedPane.hasTab()) {
	        	JOptionPane.showMessageDialog(mMainWindowJFrame,
	        		    "No table to export data from.",
	        		    "Export error",
	        		    JOptionPane.ERROR_MESSAGE);
	        	return;
	        }
			
			if(!mDatabaseTabbedPane.hasSelection()) {
	        	JOptionPane.showMessageDialog(mMainWindowJFrame,
	        		    "No selection from table. Select values from table to export to another tab.",
	        		    "Export error",
	        		    JOptionPane.ERROR_MESSAGE);
	        	return;
	        }
	        
			
			String newTableName = (String)JOptionPane.showInputDialog(mMainWindowJFrame, "New table name:");
			if(newTableName == null || newTableName.equals(""))
				return;
			
			ArrayList<Object[]> selectedRows = mDatabaseTabbedPane.getActiveTab().getSelectedRows();
			Object[] columnIdentifers = mDatabaseTabbedPane.getActiveTab().getSelectedColumnIdentifers();

	        String[] stringColumnIdentifers = Arrays.copyOf(columnIdentifers, columnIdentifers.length, String[].class);
	        		
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						mDatabaseTabbedPane.addTab(newTableName, stringColumnIdentifers, null, selectedRows);
					} catch (SQLException e) {
						showError(e);
					}
				}
			});
		}
	}
	
	private class ActionSearchTargetMineWithGeneSymbols extends AbstractAction {



		private static final long serialVersionUID = 1L;
		public ActionSearchTargetMineWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");

		}
		public void actionPerformed(ActionEvent e) {
			String[] selection = mDatabaseTabbedPane.getSelection();
			TargetMineSearchInterface targetMineSearchInterface = new TargetMineSearchInterface();
			
			Search search = new Search(targetMineSearchInterface, selection);
			
			try {
				mDatabaseTabbedPane.addSearch(search);
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				showError(sqlException);
			}
			
		}
	}
	
	private class ActionSearchReactomeWithGeneSymbols extends AbstractAction {



		private static final long serialVersionUID = 1L;
		public ActionSearchReactomeWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");

		}
		public void actionPerformed(ActionEvent e) {
			String[] selection = mDatabaseTabbedPane.getSelection();
			ReactomeSearchInterface reactomeSearchInterface = new ReactomeSearchInterface();
			
			Search search = new Search(reactomeSearchInterface, selection);
			
			try {
				mDatabaseTabbedPane.addSearch(search);
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				showError(sqlException);
			}
		}
	}
	
	private class ActionTableJoin extends AbstractAction {



		private static final long serialVersionUID = 1L;
		public ActionTableJoin() {
			putValue(NAME, "Join Table");
		}
		public void actionPerformed(ActionEvent e) {
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {

					try {	
						TableJoinDialog tableJoinDialog = new TableJoinDialog(mDatabaseTabbedPane);
						tableJoinDialog.setVisible(true);
					} catch (Exception exception) {
						exception.printStackTrace();
						showError(exception);
					}
					
				}
			});
			
		}
	}
	
	public static void showError(Exception exceptionError) {
    	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				exceptionError.printStackTrace();
				new ErrorDialog(exceptionError);  
			}
		});
		

	}
	
	private Species getPercellomeSpecies() {
		Object[] possibilities = PercellomeSearchInterface.Species.values();
		Species s = (Species)JOptionPane.showInputDialog(
		                    null,
		                    "Select Species:\n",
		                    "Percellome",
		                    JOptionPane.PLAIN_MESSAGE,
		                    PercellomeSearchInterface.getIcon(),
		                    possibilities,
		                    PercellomeSearchInterface.Species.values()[0]);
		return s;
	}
	
	private class ActionSearchPercellomeWithGeneSymbols extends AbstractAction {


		private static final long serialVersionUID = 1L;
		public ActionSearchPercellomeWithGeneSymbols() {
			putValue(NAME, "with Gene Symbols");
		}
		public void actionPerformed(ActionEvent e) {
			String[] selection = mDatabaseTabbedPane.getSelection();
			Species speciesSelect = getPercellomeSpecies();
			SearchMode searchMode = SearchMode.WITH_GENE_SYMBOLS;
			
			if(speciesSelect == null) return; 
			
			PercellomeSearchInterface percellomeSearchInterface = new PercellomeSearchInterface(speciesSelect, searchMode);
			
			Search search = new Search(percellomeSearchInterface, selection);
			
			try {
				mDatabaseTabbedPane.addSearch(search);
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				showError(sqlException);
			}
		}
	}
	
	private class ActionSearchPercellomeWithProbeIds extends AbstractAction {

		private static final long serialVersionUID = 1L;
		public ActionSearchPercellomeWithProbeIds() {
			putValue(NAME, "with Probe IDs (Affy ID)");
		}
		public void actionPerformed(ActionEvent e) {
			String[] selection = mDatabaseTabbedPane.getSelection();
			Species speciesSelect = getPercellomeSpecies();
			SearchMode searchMode = SearchMode.WITH_PROBE_IDS;
			
			if(speciesSelect == null) return; 
			
			PercellomeSearchInterface percellomeSearchInterface = new PercellomeSearchInterface(speciesSelect, searchMode);
			
			Search search = new Search(percellomeSearchInterface, selection);
			
			try {
				mDatabaseTabbedPane.addSearch(search);
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				showError(sqlException);
			}
		}
	}

}
