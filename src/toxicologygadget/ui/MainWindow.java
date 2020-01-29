package toxicologygadget.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.sbi.garuda.backend.net.exception.GarudaConnectionNotInitializedException;
import jp.sbi.garuda.backend.net.exception.NetworkConnectionException;
import toxicologygadget.backend.garudahandler.GarudaHandler;

public class MainWindow {

	private JFrame frame;
	private JTable tbl_DataTable;
	private GarudaHandler garudaHandler;
	
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
		
		tbl_DataTable = new JTable();
		tbl_DataTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
				{null},
			},
			new String[] {
				"New column"
			}
		));
		tbl_DataTable.setBounds(141, 32, 575, 356);
		frame.getContentPane().add(tbl_DataTable);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
	}
}
