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

public class MainWindow implements ActionListener {

	private JFrame frmToxicologyGadget;
	private GarudaHandler garudaHandler;
	private final Action fileOpenAction = new FileOpenAction();
	private final GarudaDiscoverActionGeneList garudaDiscoverAction = new GarudaDiscoverActionGeneList();
	
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
		mntmNewMenuItem.setAction(garudaDiscoverAction);
		mnGaruda.add(mntmNewMenuItem);
		
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
			
			JTabbedPane fileTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("File", null, fileTabbedPane, null);
			
			JSplitPane splitPane_6 = new JSplitPane();
			splitPane_6.setOrientation(JSplitPane.VERTICAL_SPLIT);
			fileTabbedPane.addTab("Table", null, splitPane_6, null);
			
			JSplitPane splitPane_7 = new JSplitPane();
			splitPane_7.setOrientation(JSplitPane.VERTICAL_SPLIT);
			fileTabbedPane.addTab("Load / Save", null, splitPane_7, null);
			
			JButton btnNewButton = new JButton("New button");
			splitPane_7.setLeftComponent(btnNewButton);
			
			JTabbedPane garudaTabPanel = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("Garuda", null, garudaTabPanel, null);
			
			JSplitPane splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			garudaTabPanel.addTab("Percellome", null, splitPane, null);
			
			JComboBox comboBox = new JComboBox();
			splitPane.setLeftComponent(comboBox);
			
			JSplitPane splitPane_1 = new JSplitPane();
			splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setRightComponent(splitPane_1);
			
			JComboBox comboBox_1 = new JComboBox();
			splitPane_1.setLeftComponent(comboBox_1);
			
			JSplitPane splitPane_2 = new JSplitPane();
			splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane_1.setRightComponent(splitPane_2);
			
			JComboBox comboBox_2 = new JComboBox();
			splitPane_2.setLeftComponent(comboBox_2);
			
			JSplitPane splitPane_3 = new JSplitPane();
			splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane_2.setRightComponent(splitPane_3);
			
			JButton button = new JButton("Search Selected");
			splitPane_3.setLeftComponent(button);
			
			JPanel panel = new JPanel();
			splitPane_3.setRightComponent(panel);
			
			JSplitPane splitPane_8 = new JSplitPane();
			splitPane_8.setOrientation(JSplitPane.VERTICAL_SPLIT);
			garudaTabPanel.addTab("New tab", null, splitPane_8, null);
			
			JTabbedPane otherToolsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("Other Tools", null, otherToolsTabbedPane, null);
			
			JSplitPane splitPane_4 = new JSplitPane();
			splitPane_4.setOrientation(JSplitPane.VERTICAL_SPLIT);
			otherToolsTabbedPane.addTab("TargetMine", null, splitPane_4, null);
			
			JSplitPane splitPane_5 = new JSplitPane();
			splitPane_5.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane_4.setRightComponent(splitPane_5);
			
			JLabel lblNewLabel_1 = new JLabel("Complete (0) of (100)...");
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel_1.setAlignmentX(Component.CENTER_ALIGNMENT);
			splitPane_5.setLeftComponent(lblNewLabel_1);
			
			JButton btnTargetMineSearchSelected = new JButton("Search Selected");
			btnTargetMineSearchSelected.setActionCommand("Search Selected");
			splitPane_4.setLeftComponent(btnTargetMineSearchSelected);
			
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
	
	private class GarudaDiscoverActionGeneList extends AbstractAction {
		public GarudaDiscoverActionGeneList() {
			putValue(NAME, "Discover Genelist");
			putValue(SHORT_DESCRIPTION, "Some short description");
			
	
		}
		public void actionPerformed(ActionEvent ae) {
			
			System.out.println(ae.getActionCommand());
			
			if(hasGenelist()) {
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
				
				garudaHandler.garudaDiscover(file, "ensemble");
				
			}else {
				JOptionPane.showMessageDialog(frmToxicologyGadget, "No genelist avaliable!");
			}
		}
	}
}
