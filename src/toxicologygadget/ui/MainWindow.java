package toxicologygadget.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import toxicologygadget.filemanager.DataTable;
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
	private GeneTable geneTable;
	private TargetMineQueryThread targetMineQueryThread;
	private MainWindowTargetMineCallback targetMineCallback;
	private PercellomeQueryThread percellomeQueryThread;
	private MainWindowPercellomeCallback percellomeCallback;
	
	
	private class MainWindowTargetMineCallback implements QueryThreadCallback {

		@Override
		public void completeSearch(DataTable results) {
			geneTable.importTable(results);
			
		}

		@Override
		public void unsuccessfulSearch(String error) {
			System.out.println("unsuccessfulSearch()");
		}
		
		
	}
	
	private class MainWindowPercellomeCallback implements QueryThreadCallback {

		@Override
		public void completeSearch(DataTable results) {
			geneTable.importTable(results);
			
		}

		@Override
		public void unsuccessfulSearch(String error) {
			System.out.println("unsuccessfulSearch()");
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
		mntmNewMenuItem.setAction(garudaDiscoverActionGenelist);
		mnGaruda.add(mntmNewMenuItem);
		
		JMenuItem menuItem = new JMenuItem("Discover");
		menuItem.setAction(garudaDiscoverActionEnsemble);
		mnGaruda.add(menuItem);
		
		JMenu mnOtherTools = new JMenu("Tools");
		menuBar.add(mnOtherTools);


		this.geneTable = new GeneTable();
		geneTable.setCellSelectionEnabled(true);
		geneTable.setColumnSelectionAllowed(true);
		
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
			frmToxicologyGadget.getContentPane().setLayout(new MigLayout("", "[590px]", "[417px]"));
			this.garudaHandler = new GarudaHandler(this.frmToxicologyGadget, this.geneTable);
			
			JScrollPane scrollPane = new JScrollPane();
			frmToxicologyGadget.getContentPane().add(scrollPane, "cell 0 0,grow");
			
			geneTable.setFillsViewportHeight(true);
			scrollPane.setViewportView(geneTable);
			//header.setBackground(Color.yellow);
			
			
			
			
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		// JSONObject res = new JSONObject(a);
		
		
		File clusterResFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_VisibleClustering.txt");
		File ensembleGenelistFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\PercellomeTestDataSmall.txt");
				
		File file = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_Scenario.txt");
		
		int[] clusterResults = FileManager.loadAGCTClusterResults(clusterResFile);
		String[] genelist = FileManager.loadEnsembleGenelistTxt(ensembleGenelistFile);
		
		try {
			DataTable agctScenario = FileManager.loadAGCTScenario(file);
			geneTable.importTable(agctScenario);
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
					String[] ensembleGenelist = FileManager.loadEnsembleGenelistTxt(file);
					if(ensembleGenelist != null) {
						geneTable.loadGenelist(ensembleGenelist);
					}
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
		return this.geneTable.hasColumn("Gene");
	}
	
	private void discover(String contence) {
		String data = geneTable.getGenelistStringTxt();
		String fileName = "genelist.txt";
		File file = null;
		try {
			file = FileManager.writeOutString(data, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GarudaGlassPanel g = garudaHandler.getGarudaGlassPanel();
		g.showPanel();
		
		garudaHandler.garudaDiscover(file, contence);
	}
	
	private class GarudaDiscoverActionGenelist extends AbstractAction {
		public GarudaDiscoverActionGenelist() {
			putValue(NAME, "Discover Genelist");
			putValue(SHORT_DESCRIPTION, "Some short description");
			
	
		}
		public void actionPerformed(ActionEvent ae) {
			
			System.out.println(ae.getActionCommand());
			
			if(hasGenelist()) {
				discover("genelist");
			}else {
				JOptionPane.showMessageDialog(frmToxicologyGadget, "No genelist avaliable!");
			}
		}
	}
	
	private class GarudaDiscoverActionEnsemble extends AbstractAction {
		public GarudaDiscoverActionEnsemble() {
			putValue(NAME, "Discover Ensemble");
			putValue(SHORT_DESCRIPTION, "Some short description");
			
	
		}
		public void actionPerformed(ActionEvent ae) {
			
			System.out.println(ae.getActionCommand());
			
			if(hasGenelist()) {
				discover("ensemble");
			}else {
				JOptionPane.showMessageDialog(frmToxicologyGadget, "No genelist avaliable!");
			}
		}
	}
}
