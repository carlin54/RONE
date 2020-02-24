package toxicologygadget.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;
import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import jp.sbi.garuda.backend.ui.GarudaGlassPanel;
import toxicologygadget.backend.garudahandler.GarudaHandler;
import toxicologygadget.filemanager.Table;
import toxicologygadget.filemanager.FileManager;
import toxicologygadget.filemanager.JsonReader;
import toxicologygadget.query.PercellomeQueryThread;
import toxicologygadget.query.QueryThreadCallback;
import toxicologygadget.query.ReactomeQueryThread;
import toxicologygadget.query.TargetMineQueryThread;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

public class MainWindow implements ActionListener {

	private JFrame frmToxicologyGadget;
	private GarudaHandler garudaHandler;
	private final Action fileImportAction = new FileImportAction();
	private final Action fileClearTableAction = new FileClearTableAction();
	private final Action reactomeImportAction =  new ReactomeImportAction();
	private final Action targetMineImportAction = new TargetMineImportAction();
	
	
	private final GarudaDiscoverActionGenelist garudaDiscoverActionGenelist = new GarudaDiscoverActionGenelist();
	private final GarudaDiscoverActionEnsemble garudaDiscoverActionEnsemble = new GarudaDiscoverActionEnsemble();
	
	
	//private GeneTable geneTable;
	private ToxicologyTable toxicologyTable;
	private TargetMineQueryThread targetMineQueryThread;
	private MainWindowTargetMineCallback targetMineCallback;
	private ReactomeQueryThread reactomeQueryThread;
	private MainWindowReactomeCallback reactomeCallback;
	
	private class MainWindowTargetMineCallback implements QueryThreadCallback {
		
		TargetMineStatusWindow targetMineStatusWindow;
		
		@Override
		public void startSearch(int number) {
			targetMineStatusWindow = new TargetMineStatusWindow(number);
			targetMineStatusWindow.setVisible(true);
		}
		
		@Override
		public void completeSearch(Table results, int status) {
			
			if(status == QueryThreadCallback.statusCodeFinishSuccess) {
				
				int res_len = results.getIdentifiers().size();
				String[] res_id = results.getIdentifiers().toArray(new String[res_len]);
				
				int tox_len = toxicologyTable.getIdentifiers().size();
				String[] tox_id = toxicologyTable.getIdentifiers().toArray(new String[tox_len]);
				String fromWhere = "TargetMine";
				ImportDataDialog importSelection = new ImportDataDialog(frmToxicologyGadget, fromWhere, tox_id, res_id) ;
				importSelection.setVisible(true);	
				
				String[] data = importSelection.getData();
				
				if(data[0] != null) {
					String keyTox = data[0];
					String keyRes = data[1];
					toxicologyTable.importTable(keyTox, keyRes, results);
				}
			
			}
			
			targetMineStatusWindow.setVisible(false);
			targetMineStatusWindow.dispose();
			targetMineStatusWindow = null;
			
		}

		@Override
		public void statusUpdate(int complete, int total, int totalFound) {
			
			targetMineStatusWindow.updateSearch(complete, total, totalFound);
			System.out.println("Complete: " + complete + "\t Total: " + total + "\t Results: " + totalFound);
		}
		
		
	}
	
	private class MainWindowPercellomeCallback implements QueryThreadCallback {
		
		@Override
		public void completeSearch(Table results, int status) {
			
			int res_len = results.getIdentifiers().size();
			String[] res_id = results.getIdentifiers().toArray(new String[res_len]);
			
			int tox_len = toxicologyTable.getIdentifiers().size();
			String[] tox_id = toxicologyTable.getIdentifiers().toArray(new String[tox_len]);
			
			ImportDataDialog importSelection = new ImportDataDialog(frmToxicologyGadget, "Percellome", tox_id, res_id) ;
			importSelection.setVisible(true);	
			
			String[] data = importSelection.getData();
			
			if(data[0] != null) {
				String keyTox = data[0];
				String keyRes = data[1];
				toxicologyTable.importTable(keyTox, keyRes, results);
			}
			
			
		}

		@Override
		public void statusUpdate(int complete, int total, int totalFound) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startSearch(int total) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	}
	
	private class MainWindowReactomeCallback implements QueryThreadCallback {
		
		@Override
		public void completeSearch(Table results, int status) {
			if(status == QueryThreadCallback.statusCodeFinishSuccess) {
				
				int res_len = results.getIdentifiers().size();
				String[] res_id = results.getIdentifiers().toArray(new String[res_len]);
				
				int tox_len = toxicologyTable.getIdentifiers().size();
				String[] tox_id = toxicologyTable.getIdentifiers().toArray(new String[tox_len]);
				String fromWhere = "TargetMine";
				ImportDataDialog importSelection = new ImportDataDialog(frmToxicologyGadget, fromWhere, tox_id, res_id) ;
				importSelection.setVisible(true);	
				
				String[] data = importSelection.getData();
				
				if(data[0] != null) {
					String keyTox = data[0];
					String keyRes = data[1];
					toxicologyTable.importTable(keyTox, keyRes, results);
				}
			
			}
		}

		@Override
		public void statusUpdate(int complete, int total, int totalFound) {
			System.out.println("Complete: " + complete + "\t Total: " + "\t Found: " + totalFound);
		}

		@Override
		public void startSearch(int total) {
			
		}
		
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmToxicologyGadget = new JFrame();
		frmToxicologyGadget.setTitle("Toxicology Gadget");
		frmToxicologyGadget.setBounds(100, 100, 812, 555);
		frmToxicologyGadget.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmToxicologyGadget.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Import");
		mntmOpen.setAction(fileImportAction);
		mnFile.add(mntmOpen);
		
		mntmOpen.addActionListener(this);
		
		JMenuItem mntmSave = new JMenuItem("Export");
		mnFile.add(mntmSave);
		
		JMenuItem menuItem = new JMenuItem("Clear Table");
		menuItem.setAction(fileClearTableAction);
		mnFile.add(menuItem);
		
		JMenu mnGaruda = new JMenu("Garuda");
		menuBar.add(mnGaruda);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Discover");
		
		JMenu mnDiscover = new JMenu("Discover");
		mnGaruda.add(mnDiscover);
		
		JMenuItem mntmDiscoverGenelist = new JMenuItem("New menu item");
		mntmDiscoverGenelist.setAction(garudaDiscoverActionGenelist);
		mnDiscover.add(mntmDiscoverGenelist);
		
		JMenuItem mntmDiscoverEnsemble = new JMenuItem("New menu item");
		mntmDiscoverEnsemble.setAction(garudaDiscoverActionEnsemble);
		mnDiscover.add(mntmDiscoverEnsemble);
		
		JPopupMenu popupMenu = new JPopupMenu("Discover");
		popupMenu.add(mntmNewMenuItem);
		
		JMenu mnOtherTools = new JMenu("Tools");
		menuBar.add(mnOtherTools);
		
		JMenuItem mntmPercellomeMenuItem = new JMenuItem("Import Percellome");
		mntmPercellomeMenuItem.setAction(reactomeImportAction);
		mnOtherTools.add(mntmPercellomeMenuItem);
		
		JMenuItem mntmTargetMineMenuItem = new JMenuItem("Import TargetMine");
		mntmTargetMineMenuItem.setAction(targetMineImportAction);
		mnOtherTools.add(mntmTargetMineMenuItem);
		
		this.targetMineCallback = new MainWindowTargetMineCallback();
		this.targetMineQueryThread = new TargetMineQueryThread(targetMineCallback);
		
		this.reactomeCallback = new MainWindowReactomeCallback();
		this.reactomeQueryThread = new ReactomeQueryThread(reactomeCallback);
		
	}
		
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {	
					MainWindow window = new MainWindow();
					window.frmToxicologyGadget.setVisible(true);
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
	public MainWindow() {
		initialize();
		try {
			
				frmToxicologyGadget.getContentPane().setLayout(new BoxLayout(frmToxicologyGadget.getContentPane(), BoxLayout.X_AXIS));
		
				this.toxicologyTable = new ToxicologyTable();
				toxicologyTable.setCellSelectionEnabled(true);
				toxicologyTable.setColumnSelectionAllowed(true);
				this.garudaHandler = new GarudaHandler(this.frmToxicologyGadget, this.toxicologyTable);
				
				JScrollPane scrollPane = new JScrollPane();
				frmToxicologyGadget.getContentPane().add(scrollPane);
				
				toxicologyTable.setFillsViewportHeight(true);
				scrollPane.setViewportView(toxicologyTable);
			
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	private boolean isTxtExtention(File file) {
		String path = file.getPath();
		int len = path.length();
		System.out.println(path.substring(len-4, len));
		return path.substring(len-4, len).contentEquals(".txt");
	}
	
	private void loadTable(Table incomingTable, String fromWhere) {
		
		if(toxicologyTable.isEmpty()) {
			toxicologyTable.setTable(incomingTable);
			
		}else {
			int inc_len = incomingTable.getIdentifiers().size();
			String[] inc_id = incomingTable.getIdentifiers().toArray(new String[inc_len]);
			
			int tox_len = toxicologyTable.getIdentifiers().size();
			String[] tox_id = toxicologyTable.getIdentifiers().toArray(new String[tox_len]);
			
			ImportDataDialog importSelection = new ImportDataDialog(frmToxicologyGadget, fromWhere, tox_id, inc_id) ;
			importSelection.setVisible(true);	
			
			String[] data = importSelection.getData();
			if(data[0] != null) {
				String keyTox = data[0];
				String keyInc = data[1];
				toxicologyTable.importTable(keyTox, keyInc, incomingTable);
			}
		}
		
	}
	
	private void loadList(File file, String header) {
		Table listTable = null;
		try {
			listTable = FileManager.loadListFile(file, header);
			toxicologyTable.setTable(listTable);
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
	
	private void loadCSV(File file) {
		
	}
	
	private void loadTxt(File file) {
		
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
				loadCSV(file);
				break;
				
			case "Tab":
				break;
		}
		
	}
	
	private class FileImportAction extends AbstractAction {
		
		final JFileChooser fc = new JFileChooser();
		
		public FileImportAction() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			//In response to a button click:
			int returnVal = fc.showOpenDialog(frmToxicologyGadget);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"AGCT Scenario", "Genelist", "Ensemble", "CSV", "Tab Delimited Text"};
				String content = (String)JOptionPane.showInputDialog(
				                    frmToxicologyGadget,
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
		String[] data = toxicologyTable.getUniqueSelected();
		
		String list = new String("");
		for(int i = 0; i < data.length; i++) {
			list += data[i] + "\n";
		}
		
		int time = (int) new Date().getTime();
		
		String fileName = contence + "_" + time + "." + extension;
		File file = null;
		
		try {
			file = FileManager.writeOutString(list, fileName);
			garudaHandler.garudaDiscover(file, contence);
		} catch (IOException e) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(
			        null, e.getMessage(), "Failure", JOptionPane.ERROR_MESSAGE);
			
		}
		
		
	}
	
	boolean hasValidSelection() {
		
		if(toxicologyTable.isEmpty()) {
			JOptionPane.showMessageDialog(frmToxicologyGadget, "There is no data in the table.");
			return false;
		}
		
		if(!toxicologyTable.hasSelection()) {
			JOptionPane.showMessageDialog(frmToxicologyGadget, "Select data from the table to use.");
			return false;
		}
		
		return true;
	}
	
	
	private class GarudaDiscoverActionGenelist extends AbstractAction {
		public GarudaDiscoverActionGenelist() {
			putValue(NAME, "Genelist");
			putValue(SHORT_DESCRIPTION, "Some short description");
			
			
			
			
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
			putValue(SHORT_DESCRIPTION, "Some short description");
			
	
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
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = toxicologyTable.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			
			if(targetMineQueryThread.isRunning())
				targetMineQueryThread.stopRunning();
			
			try {
				//TODO: add thread stop dialog
				targetMineQueryThread.join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			
			targetMineQueryThread = new TargetMineQueryThread(targetMineCallback);
			targetMineQueryThread.setGenelist(genelist);
			targetMineQueryThread.start();
			
		}
	}
	
	private class ReactomeImportAction extends AbstractAction {
		public ReactomeImportAction() {
			putValue(NAME, "Import Reactome");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = toxicologyTable.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			
			if(reactomeQueryThread.isRunning())
				reactomeQueryThread.stopRunning();
			
			try {
				//TODO: add thread stop dialog
				reactomeQueryThread.join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			
			reactomeQueryThread = new ReactomeQueryThread(reactomeCallback);
			reactomeQueryThread.setGenelist(genelist);
			reactomeQueryThread.start();
			
		}
	}
	
	private class FileClearTableAction extends AbstractAction {
		
		public FileClearTableAction() {
			putValue(NAME, "Clear");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			if(!toxicologyTable.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(null,
						"Are you sure you would like to discard the current table?", "Clear Confirm", JOptionPane.YES_NO_OPTION);
				
				if(result != JOptionPane.YES_OPTION)
					return;
				
			}
			
			toxicologyTable.clearTable();
			
		}
		
	}
}
