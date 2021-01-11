package rone.ui;

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

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
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

public class MainWindow implements ActionListener {
	
	private MainWindow mMainWindow;
	private JFrame mFrmToxicologyGadget;
	private GarudaHandler mGarudaHandler;
	private final Action mFileImportAction = new FileImportAction();
	private final Action mFileExportTableAction = new FileExportTableAction();
	private final Action mFileClearTableAction = new FileClearTableAction();
	private final Action mReactomeImportAction =  new ReactomeImportAction();
	private final Action mPercellomeImportAction =  new PercellomeImportAction();
	private final Action mTargetMineImportAction = new TargetMineImportAction();
	private final GarudaDiscoverActionGenelist mGarudaDiscoverActionGenelist = new GarudaDiscoverActionGenelist();
	private final GarudaDiscoverActionEnsemble mGarudaDiscoverActionEnsemble = new GarudaDiscoverActionEnsemble();
	
	private ToxicologyTable mToxicologyTable;
		
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mMainWindow = this;
		mFrmToxicologyGadget = new JFrame();
		mFrmToxicologyGadget.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Richard\\eclipse-workspace\\RONE\\icons\\roneicon.png"));
		mFrmToxicologyGadget.setTitle("RONE");
		mFrmToxicologyGadget.setBounds(100, 100, 812, 555);
		mFrmToxicologyGadget.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		JMenuBar menuBar = new JMenuBar();
		mFrmToxicologyGadget.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Import");
		mntmOpen.setAction(mFileImportAction);
		mnFile.add(mntmOpen);
		
		mntmOpen.addActionListener(this);
		
		JMenuItem mntmSave = new JMenuItem("Export");
		mntmSave.setAction(mFileExportTableAction);
		mnFile.add(mntmSave);
		
		JMenuItem menuItem = new JMenuItem("Clear Table");
		menuItem.setAction(mFileClearTableAction);
		mnFile.add(menuItem);
		
		JMenu mnGaruda = new JMenu("Garuda");
		menuBar.add(mnGaruda);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Discover");
		
		JMenu mnDiscover = new JMenu("Discover");
		mnGaruda.add(mnDiscover);
		
		JMenuItem mntmDiscoverGenelist = new JMenuItem("New menu item");
		mntmDiscoverGenelist.setAction(mGarudaDiscoverActionGenelist);
		mnDiscover.add(mntmDiscoverGenelist);
		
		JMenuItem mntmDiscoverEnsemble = new JMenuItem("New menu item");
		mntmDiscoverEnsemble.setAction(mGarudaDiscoverActionEnsemble);
		mnDiscover.add(mntmDiscoverEnsemble);
		
		JPopupMenu popupMenu = new JPopupMenu("Discover");
		popupMenu.add(mntmNewMenuItem);
		
		JMenu mnOtherTools = new JMenu("Tools");
		menuBar.add(mnOtherTools);
		
		JMenuItem mntmReactomeMenuItem = new JMenuItem("Import Reactome");
		mntmReactomeMenuItem.setAction(mReactomeImportAction);
		mnOtherTools.add(mntmReactomeMenuItem);
		
		JMenuItem mntmTargetMineMenuItem = new JMenuItem("Import TargetMine");
		mntmTargetMineMenuItem.setAction(mTargetMineImportAction);
		mnOtherTools.add(mntmTargetMineMenuItem);
		
		JMenuItem mntmPercellomeMenuItem = new JMenuItem("Import Percellome");
		mntmTargetMineMenuItem.setAction(mPercellomeImportAction);
		mnOtherTools.add(mntmPercellomeMenuItem);
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
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
        
		try {
			Database db = Database.getInstance();
			
			String tableName = "Percellome";
			String[] columnIdentifiers = {
					"AffyID",
					"GeneSymbol",
					"Biology_Process",
					"Cellular_Component",
					"Molecular_Function"
			};
			
			int[] primaryKeys = {
					1
			};
			
			//
			Database.Table percellomeTable = db.createTable("Percellome", columnIdentifiers, primaryKeys);
			ArrayList<Object[]> percellomeData = new ArrayList<Object[]>();
			percellomeData.add(new Object[]{"1451544_at","Tapbpl","A","B"});
			percellomeData.add(new Object[]{"1452676_a_at","Pnpt1","C","D"});
			percellomeData.add(new Object[]{"1415713_a_at","Ddx24","E","F"});
			percellomeData.add(new Object[]{"1427076_at","Mpeg1","G","H"});
			percellomeTable.insertRows(percellomeData);
			percellomeTable.getTabel();
			
			//
			tableName = new String("SHOE");
			primaryKeys = new int[]{
					1
			};
			columnIdentifiers = new String[]{"Gene","NM","TF","Region","Strand","MA_Score","PSSM_Score","ID","MOTIF","CONSENSUS","Similarity","Pareto"};
			Database.Table shoeTable = db.createTable(tableName, columnIdentifiers, primaryKeys);
			ArrayList<Object[]> shoeData = new ArrayList<Object[]>();
			shoeData.add(new Object[]{"Tapbpl","NM_001777","Nrf-1","922-931","+","5.782955","7.831982","1","CGCGTGCGCG","CGCATGCGCR","0.85","0"});
			shoeData.add(new Object[]{"Pnpt1","NM_001565","IRF-2","47-59","+","5.625442","7.353745","1","GGAAAGTGAAACC","GAAAAGYGAAASY","0.807692308","1"});
			shoeData.add(new Object[]{"Ddx24","NM_001350","AML-1a","317-322","+","1.397158","9.581742","1","TGTGGT","TGTGGT","1","0"});
			shoeTable.insertRows(shoeData);
			shoeTable.getTabel();
			
			//
			java.sql.ResultSet rs = db.join(percellomeTable, 1, shoeTable, 0, Database.JOIN.LEFT);
			int size = rs.getFetchSize();
			System.out.println("Result size: " + size);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			System.out.println("Num Columns: " + rsmd.getColumnCount());
			columnIdentifiers = new String[columnCount];
			for (int i = 1; i <= columnCount; i++ ) {
			  columnIdentifiers[i-1] = rsmd.getColumnName(i);
			}
			System.out.println(java.util.Arrays.toString(columnIdentifiers));
			
			while (rs.next()) {
			    for (int i = 1; i <= columnCount; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = rs.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {	
					MainWindow window = new MainWindow();
					window.mFrmToxicologyGadget.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	/**
	 * Create the application.;
	 * @throws IOException 
	 */
	public MainWindow() throws SocketTimeoutException {
		initialize();
		
			mFrmToxicologyGadget.getContentPane().setLayout(new BoxLayout(mFrmToxicologyGadget.getContentPane(), BoxLayout.X_AXIS));
	
			this.mToxicologyTable = new ToxicologyTable();
			mToxicologyTable.setCellSelectionEnabled(true);
			mToxicologyTable.setColumnSelectionAllowed(true);
			
			try {
				this.mGarudaHandler = new GarudaHandler(this.mFrmToxicologyGadget, this.mToxicologyTable);
			} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
				JOptionPane.showMessageDialog(mFrmToxicologyGadget, "Was unable to connect to Garuda Platform. Please restart to connect.");
				e.printStackTrace();
			}
			
			
			JScrollPane scrollPane = new JScrollPane();
			mFrmToxicologyGadget.getContentPane().add(scrollPane);
			
			mToxicologyTable.setFillsViewportHeight(true);
			scrollPane.setViewportView(mToxicologyTable);
			
			
		
		/* JSONObject res = new JSONObject(a);
		
		// File ensembleGenelistFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\PercellomeTestDataSmall.txt");
		
		// File clusterResFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_Scenario.txt");
		// File geneSymbolsFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\EnsembleGenelist2.txt");
		
		// try {
			// Table agctScenario = FileManager.loadAGCTScenario(clusterResFile);
			// Table geneSymbols = FileManager.loadListFile(geneSymbolsFile, "Gene");
			
			// toxicologyTable.importTable("Gene", "Gene", geneSymbols);
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }
		
		
		// geneTable.loadGenelist(genelist);
		
		
		
		// targetMineQueryThread.setGenelist(genelist);
		// targetMineQueryThread.start();
		
		// percellomeQueryThread.setGenelist(genelist);
		// percellomeQueryThread.setProjectId(87);
		// percellomeQueryThread.start();
		
		*/
		
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {


	}
	
	public void loadTable(Table incomingTable, String fromWhere) {
		
		if(mToxicologyTable.isEmpty()) {
			mToxicologyTable.setTable(incomingTable);
			
		}else {
			int inc_len = incomingTable.getIdentifiers().size();
			String[] inc_id = incomingTable.getIdentifiers().toArray(new String[inc_len]);
			
			int tox_len = mToxicologyTable.getIdentifiers().size();
			String[] tox_id = mToxicologyTable.getIdentifiers().toArray(new String[tox_len]);
			
			ImportDataDialog importSelection = new ImportDataDialog(mFrmToxicologyGadget, fromWhere, tox_id, inc_id) ;
			importSelection.setVisible(true);	
			
			String[] data = importSelection.getData();
			if(data[0] != null) {
				String keyTox = data[0];
				String keyInc = data[1];
				mToxicologyTable.importTable(keyTox, keyInc, incomingTable);
			}
		}
		
	}
	
	private void loadList(File file, String header) {
		Table listTable = null;
		try {
			listTable = FileManager.loadListFile(file, header);
			mToxicologyTable.setTable(listTable);
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
		Table data;
		try {
			data = FileManager.loadDataFile(file, seperator);
			loadTable(data, "Import File Data");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
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
			int returnVal = fc.showOpenDialog(mFrmToxicologyGadget);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"CSV", "Tab Delimited Text"};
				String content = (String)JOptionPane.showInputDialog(
				                    mFrmToxicologyGadget,
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
		String[] data = mToxicologyTable.getUniqueSelected();
		
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
		
		if(mToxicologyTable.isEmpty()) {
			JOptionPane.showMessageDialog(mFrmToxicologyGadget, "There is no data in the table.");
			return false;
		}
		
		if(!mToxicologyTable.hasSelection()) {
			JOptionPane.showMessageDialog(mFrmToxicologyGadget, "Select data from the table to use.");
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
			
			String[] genelist = mToxicologyTable.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			TargetMineSearchDialog searchDialog = new TargetMineSearchDialog(mMainWindow, genelist); 
			searchDialog.start();
			
		}
	}
	
	private class ReactomeImportAction extends AbstractAction {
		public ReactomeImportAction() {
			putValue(NAME, "Import Reactome");
			putValue(SHORT_DESCRIPTION, "Import selected from Reactome");
		}
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = mToxicologyTable.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			
			ReactomeSearchDialog searchDialog = new ReactomeSearchDialog(mMainWindow, genelist); 
			searchDialog.start();
			
		}
	}
	
	private class PercellomeImportAction extends AbstractAction {
		public PercellomeImportAction() {
			putValue(NAME, "Import Percellome");
			putValue(SHORT_DESCRIPTION, "Import selected from Percellome");
		}
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = mToxicologyTable.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			
			PercellomeSearchDialog searchDialog = new PercellomeSearchDialog(mMainWindow, genelist); 
			searchDialog.start();
			
		}
	}
	
	private class FileExportTableAction extends AbstractAction {
		
		public FileExportTableAction() {
			putValue(NAME, "Export");
			putValue(SHORT_DESCRIPTION, "Export selected to CSV");
		}
		
		public void actionPerformed(ActionEvent e) {
			if(mToxicologyTable.isEmpty()) {
				JOptionPane.showMessageDialog(mFrmToxicologyGadget, "There is no data in the table.");
				return;
			}
			
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
		    
		    if(mToxicologyTable.hasSelection()) {
				
			    BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(fileToSave));
					int cols[] = mToxicologyTable.getSelectedColumns();
					
					ArrayList<String> id = mToxicologyTable.getIdentifiers();
					
					String header = "";
					for(int i = 0; i < cols.length-1; i++) {
						int index = cols[i];
						header = header + id.get(index) + ",";
					}
					int index = cols[cols.length-1];
					header = header + id.get(index) + "\n"; 
					writer.write(header);
					
					System.out.println(header);
					
				    int rows[] = mToxicologyTable.getSelectedRows();
				    for(int r = 0; r < rows.length; r++) {
				    	String line = "";
				    	for(int c = 0; c < cols.length-1; c++) {
				    		line = line + "\"" + mToxicologyTable.getCell(r, c) + "\"" + ",";
				    	}
				    	line = line + mToxicologyTable.getCell(r, cols.length-1) + "\n";
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
			
			if(!mToxicologyTable.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(null,
						"Are you sure you would like to discard the current table?", "Clear Confirm", JOptionPane.YES_NO_OPTION);
				
				if(result != JOptionPane.YES_OPTION)
					return;
				
			}
			
			mToxicologyTable.clearTable();
			
		}
		
	}


	
}
