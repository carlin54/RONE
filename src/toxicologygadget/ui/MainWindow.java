package toxicologygadget.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.Button;
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
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import toxicologygadget.backend.garudahandler.GarudaHandler;
import toxicologygadget.filemanager.DataTable;
import toxicologygadget.filemanager.FileManager;
import toxicologygadget.targetmine.TargetMineQueryClient;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

public class MainWindow implements ActionListener {

	private JFrame frame;
	private GeneTable tbl_GeneTable;
	private GarudaHandler garudaHandler;
	private final Action fileOpenAction = new FileOpenAction();

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				File clusterResFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\AGCT_VisibleClustering.txt");
				File ensembleGenelistFile = new File("C:\\Users\\Richard\\eclipse-workspace\\ToxicologyGadget\\data\\EnsembleGenelist2.txt");
				
				int[] clusterResults;
				String[] genelist;
				
				String[][] dataA = {
						{"Letter", "Number"},
						{"A", "1"},
						{"B", "2"},
						{"C", "3"},
						{"D", "4"},
						{"E", "5"}
				};
				DataTable a = new DataTable(dataA);
				
				String[][] dataB = {
						{"Letter", "TF"},
						{"A", "ACAC"},
						{"B", "GCGC"},
						{"B", "HCHC"},
						{"B", "YYYY"},
						{"C", "BCBC"}
				};
				DataTable b = new DataTable(dataB);
				DataTable c = DataTable.leftJoin(a, b, "Letter");
				
				try {
					
					clusterResults = FileManager.loadAGCTClusterResults(clusterResFile);
					genelist = FileManager.loadEnsembleGenelistTxt(ensembleGenelistFile);
									
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					
					window.tbl_GeneTable.loadGenelist(genelist);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		
		try {
			this.garudaHandler = new GarudaHandler(this.frame);
			
			
			
		} catch (GarudaConnectionNotInitializedException | NetworkConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 746, 474);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		Button btn_TargetMineQuery = new Button("Query TargetMine");
		btn_TargetMineQuery.setBounds(10, 30, 125, 25);
		frame.getContentPane().add(btn_TargetMineQuery);
		
		Button btn_SendToPercellome = new Button("Send to Percellome");
		btn_SendToPercellome.setBounds(10, 61, 125, 25);
		frame.getContentPane().add(btn_SendToPercellome);
		
		Button btn_SendToBioCompendium = new Button("Send to Biocompendium");
		btn_SendToBioCompendium.setBounds(10, 92, 125, 25);
		frame.getContentPane().add(btn_SendToBioCompendium);
		
		Button btn_SendToReactome = new Button("Send to Reactome");
		btn_SendToReactome.setBounds(10, 123, 125, 25);
		frame.getContentPane().add(btn_SendToReactome);
		
		tbl_GeneTable = new GeneTable();
		

		
		tbl_GeneTable.setBounds(141, 32, 575, 356);
		frame.getContentPane().add(tbl_GeneTable);
		
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
						tbl_GeneTable.loadGenelist(ensembleGenelist);
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
