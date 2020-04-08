package rone.ui;


import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class SearchStatusWindow extends JInternalFrame {
	
	private JPanel mContentPane;
	private JLabel mLblComplete;
	private JLabel mLblGenes;
	private JLabel mLblNumResults;
	
	
	
	/**
	 * Create the frame.
	 */
	public SearchStatusWindow(String searchName, int numSearch) {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setResizable(false);
		setTitle(searchName + " Search");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 70);
		mContentPane = new JPanel();
		mContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mContentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{100, 100, 100, 0};
		gbl_contentPane.rowHeights = new int[]{1, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		mContentPane.setLayout(gbl_contentPane);
		
		mLblGenes = new JLabel("Total: " + numSearch);
		GridBagConstraints gbc_lblGenes = new GridBagConstraints();
		gbc_lblGenes.insets = new Insets(0, 0, 0, 5);
		gbc_lblGenes.anchor = GridBagConstraints.NORTH;
		gbc_lblGenes.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblGenes.gridx = 0;
		gbc_lblGenes.gridy = 0;
		mContentPane.add(mLblGenes, gbc_lblGenes);
		
		mLblComplete = new JLabel("Searched: 0");
		GridBagConstraints gbc_lblComplete = new GridBagConstraints();
		gbc_lblComplete.insets = new Insets(0, 0, 0, 5);
		gbc_lblComplete.anchor = GridBagConstraints.NORTH;
		gbc_lblComplete.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblComplete.gridx = 1;
		gbc_lblComplete.gridy = 0;
		mContentPane.add(mLblComplete, gbc_lblComplete);
		
		mLblNumResults = new JLabel("Results Found: 0");
		GridBagConstraints gbc_lblNumResults = new GridBagConstraints();
		gbc_lblNumResults.anchor = GridBagConstraints.NORTH;
		gbc_lblNumResults.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNumResults.gridx = 2;
		gbc_lblNumResults.gridy = 0;
		mContentPane.add(mLblNumResults, gbc_lblNumResults);
	}
	
	public void updateSearch(int searched, int total, int totalFound) {
		this.mLblGenes.setText("Genes: " + total);
		this.mLblComplete.setText("Searched: " + searched);
		this.mLblNumResults.setText("Results Found: " + totalFound);
	}
	
	
	
	
}
