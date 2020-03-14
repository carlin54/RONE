package toxicologygadget.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import toxicologygadget.backend.garudahandler.GarudaHandler;
import toxicologygadget.filemanager.Table;
import toxicologygadget.filemanager.FileManager;
import toxicologygadget.query.QueryThreadCallback;
import toxicologygadget.query.ReactomeQueryThread;
import toxicologygadget.query.TargetMineQueryThread;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;


public class MainWindow implements ActionListener {
	private MainWindow mMainWindow;
	private JFrame mFrmToxicologyGadget;
	private GarudaHandler mGarudaHandler;
	private final Action mFileImportAction = new FileImportAction();
	private final Action mFileClearTableAction = new FileClearTableAction();
	private final Action mReactomeImportAction =  new ReactomeImportAction();
	private final Action mTargetMineImportAction = new TargetMineImportAction();
	
	
	private final GarudaDiscoverActionGenelist mGarudaDiscoverActionGenelist = new GarudaDiscoverActionGenelist();
	private final GarudaDiscoverActionEnsemble mGarudaDiscoverActionEnsemble = new GarudaDiscoverActionEnsemble();
	
	
	private ToxicologyTable mToxicologyTable;
	private TargetMineSearchDialog mTargetMineSearchDialog;
	private MainWindowTargetMineCallback mTargetMineCallback;
	private ReactomeQueryThread mReactomeQueryThread;
	private MainWindowReactomeCallback mReactomeCallback;
	
	private class MainWindowTargetMineCallback implements QueryThreadCallback {
		
		SearchStatusWindow mTargetMineStatusWindow;
		
		@Override
		public void startSearch(int number) {
			mTargetMineStatusWindow = new SearchStatusWindow("TargetMine", number);
			mTargetMineStatusWindow.setVisible(true);
		}
		
		@Override
		public void completeSearch(Table results, int status) {
			
			if(status == QueryThreadCallback.statusCodeFinishSuccess) {
				
				int res_len = results.getIdentifiers().size();
				String[] res_id = results.getIdentifiers().toArray(new String[res_len]);
				
				int tox_len = mToxicologyTable.getIdentifiers().size();
				String[] tox_id = mToxicologyTable.getIdentifiers().toArray(new String[tox_len]);
				String fromWhere = "TargetMine";
				ImportDataDialog importSelection = new ImportDataDialog(mFrmToxicologyGadget, fromWhere, tox_id, res_id) ;
				importSelection.setVisible(true);	
				
				String[] data = importSelection.getData();
				
				if(data[0] != null) {
					String keyTox = data[0];
					String keyRes = data[1];
					mToxicologyTable.importTable(keyTox, keyRes, results);
				}
			
			}
			
			mTargetMineStatusWindow.setVisible(false);
			mTargetMineStatusWindow.dispose();
			mTargetMineStatusWindow = null;
			
		}

		@Override
		public void statusUpdate(int complete, int total, int totalFound) {
			mTargetMineStatusWindow.updateSearch(complete, total, totalFound);
			System.out.println("Complete: " + complete + "\t Total: " + total + "\t Results: " + totalFound);
		}
		
		
	}

	private class MainWindowReactomeCallback implements QueryThreadCallback {
		
		SearchStatusWindow mReactomeStatusWindow;
				
		@Override
		public void completeSearch(Table results, int status) {
			
			if(status == QueryThreadCallback.statusCodeFinishSuccess) {
				
				int res_len = results.getIdentifiers().size();
				String[] res_id = results.getIdentifiers().toArray(new String[res_len]);
				
				int tox_len = mToxicologyTable.getIdentifiers().size();
				String[] tox_id = mToxicologyTable.getIdentifiers().toArray(new String[tox_len]);
				String fromWhere = "Reactome";
				ImportDataDialog importSelection = new ImportDataDialog(mFrmToxicologyGadget, fromWhere, tox_id, res_id) ;
				importSelection.setVisible(true);	
				
				String[] data = importSelection.getData();
				
				if(data[0] != null) {
					String keyTox = data[0];
					String keyRes = data[1];
					mToxicologyTable.importTable(keyTox, keyRes, results);
				}
			
			}
			
			mReactomeStatusWindow.setVisible(false);
			mReactomeStatusWindow.dispose();
			mReactomeStatusWindow = null;
		}

		@Override
		public void statusUpdate(int complete, int total, int totalFound) {
			mReactomeStatusWindow.updateSearch(complete, total, totalFound);
			System.out.println("Complete: " + complete + "\t Total: " + total + "\t Results: " + totalFound);
		}

		@Override
		public void startSearch(int number) {
			mReactomeStatusWindow = new SearchStatusWindow("Reactome", number);
			mReactomeStatusWindow.setVisible(true);
		}
		
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mMainWindow = this;
		mFrmToxicologyGadget = new JFrame();
		mFrmToxicologyGadget.setTitle("Toxicology Gadget");
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
		
		
		this.mReactomeCallback = new MainWindowReactomeCallback();
		this.mReactomeQueryThread = new ReactomeQueryThread(mReactomeCallback);
		
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
	public MainWindow() {
		initialize();
		try {
			
				mFrmToxicologyGadget.getContentPane().setLayout(new BoxLayout(mFrmToxicologyGadget.getContentPane(), BoxLayout.X_AXIS));
		
				this.mToxicologyTable = new ToxicologyTable();
				mToxicologyTable.setCellSelectionEnabled(true);
				mToxicologyTable.setColumnSelectionAllowed(true);
				this.mGarudaHandler = new GarudaHandler(this.mFrmToxicologyGadget, this.mToxicologyTable);
				
				JScrollPane scrollPane = new JScrollPane();
				mFrmToxicologyGadget.getContentPane().add(scrollPane);
				
				mToxicologyTable.setFillsViewportHeight(true);
				scrollPane.setViewportView(mToxicologyTable);
			
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
			int returnVal = fc.showOpenDialog(mFrmToxicologyGadget);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"AGCT Scenario", "Genelist", "Ensemble", "CSV", "Tab Delimited Text"};
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
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			
			String[] genelist = mToxicologyTable.getUniqueSelected();
			
			if(!hasValidSelection()) {
				return;
			}
			
			if(mReactomeQueryThread.isRunning())
				mReactomeQueryThread.stopRunning();
			
			try {
				//TODO: add thread stop dialog
				mReactomeQueryThread.join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			
			mReactomeQueryThread = new ReactomeQueryThread(mReactomeCallback);
			mReactomeQueryThread.setGenelist(genelist);
			mReactomeQueryThread.start();
			
		}
	}
	
	private class FileClearTableAction extends AbstractAction {
		
		public FileClearTableAction() {
			putValue(NAME, "Clear");
			putValue(SHORT_DESCRIPTION, "Some short description");
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
