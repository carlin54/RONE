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
import java.io.File;
import java.io.IOException;
import java.net.URL;
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

import org.json.JSONException;
import org.json.JSONObject;

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import jp.sbi.garuda.backend.ui.GarudaGlassPanel;
import toxicologygadget.backend.garudahandler.GarudaHandler;
import toxicologygadget.filemanager.Table;
import toxicologygadget.filemanager.FileManager;
import toxicologygadget.filemanager.JsonReader;
import toxicologygadget.query.PercellomeQueryThread;
import toxicologygadget.query.QueryThreadCallback;
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
	private final Action fileOpenAction = new FileOpenAction();
	private final GarudaDiscoverActionGenelist garudaDiscoverActionGenelist = new GarudaDiscoverActionGenelist();
	private final GarudaDiscoverActionEnsemble garudaDiscoverActionEnsemble = new GarudaDiscoverActionEnsemble();
	
	//private GeneTable geneTable;
	private ToxicologyTable toxicologyTable;
	private TargetMineQueryThread targetMineQueryThread;
	private MainWindowTargetMineCallback targetMineCallback;
	private PercellomeQueryThread percellomeQueryThread;
	private MainWindowPercellomeCallback percellomeCallback;
	private final Action action_1 = new TargetMineImportAction();
	private final Action action_2 = new PercellomeImportAction();
	
	private class MainWindowTargetMineCallback implements QueryThreadCallback {

		@Override
		public void completeSearch(Table results) {
			toxicologyTable.importTable(results);
			
		}

		@Override
		public void unsuccessfulSearch(String error) {
			System.out.println("unsuccessfulSearch()");
		}

		@Override
		public void status(int complete, int unsuccessful, int total) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	private class MainWindowPercellomeCallback implements QueryThreadCallback {

		@Override
		public void completeSearch(Table results) {
			toxicologyTable.importTable(results);
			
		}

		@Override
		public void unsuccessfulSearch(String error) {
			System.out.println("unsuccessfulSearch()");
		}

		@Override
		public void status(int complete, int total, int unsuccessful) {
			// TODO Auto-generated method stub
			
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
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAction(fileOpenAction);
		mnFile.add(mntmOpen);
		
		mntmOpen.addActionListener(this);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
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
		mntmPercellomeMenuItem.setAction(action_2);
		mnOtherTools.add(mntmPercellomeMenuItem);
		
		JMenuItem mntmTargetMineMenuItem = new JMenuItem("Import TargetMine");
		mntmTargetMineMenuItem.setAction(action_1);
		mnOtherTools.add(mntmTargetMineMenuItem);
		
		this.targetMineCallback = new MainWindowTargetMineCallback();
		this.targetMineQueryThread = new TargetMineQueryThread(targetMineCallback);
		
		this.percellomeCallback = new MainWindowPercellomeCallback();
		this.percellomeQueryThread = new PercellomeQueryThread(percellomeCallback);
		
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
		

		
		// JSONObject res = new JSONObject(a);
		
		
		File clusterResFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_VisibleClustering.txt");
		File ensembleGenelistFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\PercellomeTestDataSmall.txt");
				
		File file = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\GeneSymbols.txt");
		
		// int[] clusterResults = FileManager.loadAGCTClusterResults(clusterResFile);
		// String[] genelist = FileManager.loadListFile(ensembleGenelistFile);
		
		try {
			Table agctScenario = FileManager.loadListFile(file, "Gene");
			toxicologyTable.importTable(agctScenario);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		// geneTable.loadGenelist(genelist);
		
		
		
		// targetMineQueryThread.setGenelist(genelist);
		// targetMineQueryThread.start();
		
		// percellomeQueryThread.setGenelist(genelist);
		// percellomeQueryThread.setProjectId(87);
		// percellomeQueryThread.start();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {


	}
	
	private boolean isTxtExtention(File file) {
		String path = file.getPath();
		int len = path.length();
		System.out.println(path.substring(len-4, len-1));
		return path.substring(len-3, len).contentEquals(".txt");
	}
	
	private void loadFile(File file, String contents) {
		
		if(!file.exists()) return;
		
		
		switch (contents) {
		case "Genelist":
				// TODO: Add ensemble genelist
				if(isTxtExtention(file)) {
					
					Table ensembleGenelist = null;
					
					try {
						ensembleGenelist = FileManager.loadListFile(file, "Gene");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					toxicologyTable.importTable(ensembleGenelist);
				
				}
				
			break;
		}
		
	}
	
	private class FileOpenAction extends AbstractAction {
		
		final JFileChooser fc = new JFileChooser();
		
		public FileOpenAction() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			//In response to a button click:
			int returnVal = fc.showOpenDialog(frmToxicologyGadget);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"Genelist", "Cluster", "Ensemble"};
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
	
	private boolean hasGenelist() {
		return this.toxicologyTable.hasColumn("Gene");
	}
	
	
	private void startDiscovery(String contence, String extension) {
		String[] data = toxicologyTable.getUniqueSelected();
		
		String list = new String("");
		for(int i = 0; i < data.length; i++) {
			list += data[i] + "\n";
		}
		
		String fileName = contence + "." + extension;
		File file = null;
		try {
			file = FileManager.writeOutString(list, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		garudaHandler.garudaDiscover(file, contence);
	}
	
	boolean hasValidSelection() {
		
		if(toxicologyTable.isEmpty()) {
			JOptionPane.showMessageDialog(frmToxicologyGadget, "The table is empty, there is nothing to export.");
			return false;
		}
		
		if(!toxicologyTable.hasSelection()) {
			JOptionPane.showMessageDialog(frmToxicologyGadget, "Please make a selection.");
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
			
			System.out.println(ae.getActionCommand());
			
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
			
			if(genelist.length < 1) {
				// TODO: dialog box
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
	
	
	private class PercellomeImportAction extends AbstractAction {
		public PercellomeImportAction() {
			putValue(NAME, "Import Percellome");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}
