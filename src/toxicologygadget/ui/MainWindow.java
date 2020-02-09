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

public class MainWindow implements ActionListener {

	private JFrame frmToxicologyGadget;
	private GarudaHandler garudaHandler;
	private final Action fileOpenAction = new FileOpenAction();
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

		this.geneTable = new GeneTable();
		
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
			this.garudaHandler = new GarudaHandler(this.frmToxicologyGadget, this.geneTable);
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[]{0, 0, 225, 0};
			gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
			gridBagLayout.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
			gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
			frmToxicologyGadget.getContentPane().setLayout(gridBagLayout);
			
			JScrollPane scrollPane = new JScrollPane();
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			frmToxicologyGadget.getContentPane().add(scrollPane, gbc_scrollPane);
			
			geneTable.setFillsViewportHeight(true);
			scrollPane.setViewportView(geneTable);
			
			Component horizontalGlue = Box.createHorizontalGlue();
			GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
			gbc_horizontalGlue.insets = new Insets(0, 0, 5, 5);
			gbc_horizontalGlue.gridx = 1;
			gbc_horizontalGlue.gridy = 0;
			frmToxicologyGadget.getContentPane().add(horizontalGlue, gbc_horizontalGlue);
			
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
			gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
			gbc_tabbedPane.fill = GridBagConstraints.BOTH;
			gbc_tabbedPane.gridx = 2;
			gbc_tabbedPane.gridy = 0;
			frmToxicologyGadget.getContentPane().add(tabbedPane, gbc_tabbedPane);
			
			JSplitPane splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			tabbedPane.addTab("New tab", null, splitPane, null);
			
			JSplitPane splitPane_1 = new JSplitPane();
			splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setRightComponent(splitPane_1);
			
			JSplitPane splitPane_2 = new JSplitPane();
			splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane_1.setRightComponent(splitPane_2);
			
			JComboBox comboBox_2 = new JComboBox();
			splitPane_2.setLeftComponent(comboBox_2);
			
			JComboBox comboBox_1 = new JComboBox();
			splitPane_1.setLeftComponent(comboBox_1);
			
			JComboBox comboBox = new JComboBox();
			splitPane.setLeftComponent(comboBox);
			
			Component verticalGlue = Box.createVerticalGlue();
			GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
			gbc_verticalGlue.insets = new Insets(0, 0, 5, 5);
			gbc_verticalGlue.gridx = 0;
			gbc_verticalGlue.gridy = 1;
			frmToxicologyGadget.getContentPane().add(verticalGlue, gbc_verticalGlue);
			
			JLabel lblNewLabel = new JLabel("New label");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 2;
			frmToxicologyGadget.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
			//header.setBackground(Color.yellow);
			
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		// JSONObject res = new JSONObject(a);
		
		
		File clusterResFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_VisibleClustering.txt");
		File ensembleGenelistFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\PercellomeTestDataSmall.txt");
		
		int[] clusterResults = FileManager.loadAGCTClusterResults(clusterResFile);
		String[] genelist = FileManager.loadEnsembleGenelistTxt(ensembleGenelistFile);
		
		geneTable.loadGenelist(genelist);
		
		// targetMineQueryThread.setGenelist(genelist);
		// targetMineQueryThread.start();
		
		percellomeQueryThread.setGenelist(genelist);
		percellomeQueryThread.setProjectId(87);
		percellomeQueryThread.start();
		
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
}
