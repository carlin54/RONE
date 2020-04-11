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
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import rone.backend.garudahandler.GarudaHandler;
import rone.filemanager.FileManager;
import rone.filemanager.Table;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import java.awt.Toolkit;


public class MainWindow implements ActionListener {
	
	private MainWindow mMainWindow;
	private JFrame mFrmToxicologyGadget;
	private GarudaHandler mGarudaHandler;
	private final Action mFileImportAction = new FileImportAction();
	private final Action mFileExportTableAction = new FileExportTableAction();
	private final Action mFileClearTableAction = new FileClearTableAction();
	private final Action mReactomeImportAction =  new ReactomeImportAction();
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
		
		JMenuItem mntmPercellomeMenuItem = new JMenuItem("Import Percellome");
		mntmPercellomeMenuItem.setAction(mReactomeImportAction);
		mnOtherTools.add(mntmPercellomeMenuItem);
		
		JMenuItem mntmTargetMineMenuItem = new JMenuItem("Import TargetMine");
		mntmTargetMineMenuItem.setAction(mTargetMineImportAction);
		mnOtherTools.add(mntmTargetMineMenuItem);
	}
		
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
			case "Genelist":
				loadList(file, "Gene");
				break;
				
			case "Ensemble":
				loadList(file, "Ensemble");
				break;
				
			case "AGCT Scenario":
				loadAGCTScenario(file);
				break;
				
			case "CSV": 
				loadDataFile(file, ",");
				break;
				
			case "Tab":
				loadDataFile(file, "\t");
				break;
			
			case "bioCompendium (HTML)":
				loadBioCompendium(file);
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
				
				Object[] possibilities = {"AGCT Scenario", "Genelist", "Ensemble", "CSV", "Tab Delimited Text", "bioCompendium (HTML)"};
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
