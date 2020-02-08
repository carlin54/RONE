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

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import toxicologygadget.backend.garudahandler.GarudaHandler;
import toxicologygadget.filemanager.DataTable;
import toxicologygadget.filemanager.FileManager;
import toxicologygadget.targetmine.TargetMineQueryThread;
import toxicologygadget.targetmine.TargetMineCallback;

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

public class MainWindow implements ActionListener {

	private JFrame frame;
	private GarudaHandler garudaHandler;
	private final Action fileOpenAction = new FileOpenAction();
	//private GeneTable geneTable;
	private GeneTable geneTable;
	private TargetMineQueryThread targetMineQueryThread;
	private MainWindowTargetMineCallback targetMineCallback;
	
	private class MainWindowTargetMineCallback implements TargetMineCallback {

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
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {	
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
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
			this.garudaHandler = new GarudaHandler(this.frame, this.geneTable);
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[]{0, 0, 225, 0};
			gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
			gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
			gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
			frame.getContentPane().setLayout(gridBagLayout);
			
			JScrollPane scrollPane = new JScrollPane();
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			frame.getContentPane().add(scrollPane, gbc_scrollPane);
			
			geneTable.setFillsViewportHeight(true);
			scrollPane.setViewportView(geneTable);
			
			Component horizontalGlue = Box.createHorizontalGlue();
			GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
			gbc_horizontalGlue.insets = new Insets(0, 0, 5, 5);
			gbc_horizontalGlue.gridx = 1;
			gbc_horizontalGlue.gridy = 0;
			frame.getContentPane().add(horizontalGlue, gbc_horizontalGlue);
			
			JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
			GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
			gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxNewCheckBox.gridx = 2;
			gbc_chckbxNewCheckBox.gridy = 0;
			frame.getContentPane().add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
			
			Component verticalGlue = Box.createVerticalGlue();
			GridBagConstraints gbc_verticalGlue = new GridBagConstraints();
			gbc_verticalGlue.insets = new Insets(0, 0, 5, 5);
			gbc_verticalGlue.gridx = 0;
			gbc_verticalGlue.gridy = 1;
			frame.getContentPane().add(verticalGlue, gbc_verticalGlue);
			
			JLabel lblNewLabel = new JLabel("New label");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 2;
			frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
			//header.setBackground(Color.yellow);
			
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		int[] clusterResults;
		File clusterResFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_VisibleClustering.txt");
		File ensembleGenelistFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\EnsembleGenelist2.txt");
		
		
		
		try {
			clusterResults = FileManager.loadAGCTClusterResults(clusterResFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] genelist = FileManager.loadEnsembleGenelistTxt(ensembleGenelistFile);
		
		geneTable.loadGenelist(genelist);
		
		targetMineQueryThread.setGenelist(genelist);
		targetMineQueryThread.start();
		
		
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 812, 555);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAction(fileOpenAction);
		mnFile.add(mntmOpen);
		
		mntmOpen.addActionListener(this);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);

		geneTable = new GeneTable();
		
		targetMineCallback = new MainWindowTargetMineCallback();
		targetMineQueryThread = new TargetMineQueryThread(targetMineCallback);
		
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
			int returnVal = fc.showOpenDialog(frame);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				Object[] possibilities = {"Genelist", "Cluster", "Ensemble"};
				String content = (String)JOptionPane.showInputDialog(
				                    frame,
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
