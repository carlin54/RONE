package toxicologygadget.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.GridBagLayout;
import java.awt.CardLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.Sizes;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class TargetMineStatusWindow extends JFrame {
	
	private JPanel contentPane;
	private JLabel lblComplete;
	private JLabel lblGenes;
	private JLabel lblNumResults;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TargetMineStatusWindow frame = new TargetMineStatusWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TargetMineStatusWindow(int total) {
		setResizable(false);
		setTitle("TargetMine Search");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 70);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{100, 100, 100, 0};
		gbl_contentPane.rowHeights = new int[]{1, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		lblGenes = new JLabel("Total: " + total);
		GridBagConstraints gbc_lblGenes = new GridBagConstraints();
		gbc_lblGenes.insets = new Insets(0, 0, 0, 5);
		gbc_lblGenes.anchor = GridBagConstraints.NORTH;
		gbc_lblGenes.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblGenes.gridx = 0;
		gbc_lblGenes.gridy = 0;
		contentPane.add(lblGenes, gbc_lblGenes);
		
		lblComplete = new JLabel("Searched: 0");
		GridBagConstraints gbc_lblComplete = new GridBagConstraints();
		gbc_lblComplete.insets = new Insets(0, 0, 0, 5);
		gbc_lblComplete.anchor = GridBagConstraints.NORTH;
		gbc_lblComplete.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblComplete.gridx = 1;
		gbc_lblComplete.gridy = 0;
		contentPane.add(lblComplete, gbc_lblComplete);
		
		lblNumResults = new JLabel("Results Found: 0");
		GridBagConstraints gbc_lblNumResults = new GridBagConstraints();
		gbc_lblNumResults.anchor = GridBagConstraints.NORTH;
		gbc_lblNumResults.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNumResults.gridx = 2;
		gbc_lblNumResults.gridy = 0;
		contentPane.add(lblNumResults, gbc_lblNumResults);
	}
	
	public void updateSearch(int searched, int total, int totalFound) {
		this.lblGenes.setText("Genes: " + total);
		this.lblComplete.setText("Searched: " + searched);
		this.lblNumResults.setText("Results Found: " + totalFound);
	}
	
}
