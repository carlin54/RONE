package rone.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JPanel;
import java.awt.Panel;
import java.awt.TextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.LineBorder;
import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ErrorDialog {
	private JDialog dialog;  
	
	public static String stackTraceToString(StackTraceElement[] stackTraceElements) {
		String stackTraceAsString = new String();
		for(int i = 0; i < stackTraceElements.length; i++) {
			StackTraceElement stackTraceElement = stackTraceElements[i];
			stackTraceAsString += stackTraceElement.toString() + "\r\n";
		}
		return stackTraceAsString;
	}
	
	ErrorDialog(Exception e){
		
		String stackTraceToString = stackTraceToString(e.getStackTrace()); 
		 
		JFrame f = new JFrame();
		Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		f.getContentPane().setLayout(gridBagLayout);
		
		JPanel panelTop = new JPanel();
		GridBagConstraints gbc_panelTop = new GridBagConstraints();
		gbc_panelTop.fill = GridBagConstraints.BOTH;
		gbc_panelTop.gridx = 0;
		gbc_panelTop.gridy = 0;
		f.getContentPane().add(panelTop, gbc_panelTop);
		panelTop.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panelUpper = new JPanel();
		panelUpper.setBorder(UIManager.getBorder("EditorPane.border"));
		panelTop.add(panelUpper);
		GridBagLayout gbl_panelUpper = new GridBagLayout();
		gbl_panelUpper.columnWidths = new int[]{320, 0};
		gbl_panelUpper.rowHeights = new int[]{25, 0, 25, 0};
		gbl_panelUpper.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelUpper.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		panelUpper.setLayout(gbl_panelUpper);
		
		JPanel panelUpperLabels = new JPanel();
		panelUpperLabels.setBorder(UIManager.getBorder("EditorPane.border"));
		GridBagConstraints gbc_panelUpperLabels = new GridBagConstraints();
		gbc_panelUpperLabels.insets = new Insets(0, 0, 5, 0);
		gbc_panelUpperLabels.fill = GridBagConstraints.BOTH;
		gbc_panelUpperLabels.gridx = 0;
		gbc_panelUpperLabels.gridy = 0;
		panelUpper.add(panelUpperLabels, gbc_panelUpperLabels);
		GridBagLayout gbl_panelUpperLabels = new GridBagLayout();
		gbl_panelUpperLabels.columnWidths = new int[]{32, 0, 0, 0};
		gbl_panelUpperLabels.rowHeights = new int[]{32, 0};
		gbl_panelUpperLabels.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelUpperLabels.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelUpperLabels.setLayout(gbl_panelUpperLabels);
		
		JLabel lblErrorIcon = new JLabel(errorIcon);
		lblErrorIcon.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblErrorIcon = new GridBagConstraints();
		gbc_lblErrorIcon.insets = new Insets(0, 0, 0, 5);
		gbc_lblErrorIcon.gridx = 0;
		gbc_lblErrorIcon.gridy = 0;
		panelUpperLabels.add(lblErrorIcon, gbc_lblErrorIcon);
		
		JLabel lblExceptionType = new JLabel("Exception Type:");
		lblExceptionType.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblExceptionType = new GridBagConstraints();
		gbc_lblExceptionType.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblExceptionType.insets = new Insets(0, 0, 0, 5);
		gbc_lblExceptionType.gridx = 1;
		gbc_lblExceptionType.gridy = 0;
		panelUpperLabels.add(lblExceptionType, gbc_lblExceptionType);
		
		JLabel lblExceptionTypeDescription = new JLabel("");
		lblExceptionTypeDescription.setHorizontalAlignment(SwingConstants.LEFT);
		lblExceptionTypeDescription.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lblExceptionTypeDescription = new GridBagConstraints();
		gbc_lblExceptionTypeDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblExceptionTypeDescription.gridx = 2;
		gbc_lblExceptionTypeDescription.gridy = 0;
		panelUpperLabels.add(lblExceptionTypeDescription, gbc_lblExceptionTypeDescription);
		
		JPanel panelMiddleTextArea = new JPanel();
		panelMiddleTextArea.setBorder(UIManager.getBorder("EditorPane.border"));
		GridBagConstraints gbc_panelMiddleTextArea = new GridBagConstraints();
		gbc_panelMiddleTextArea.insets = new Insets(0, 0, 5, 0);
		gbc_panelMiddleTextArea.fill = GridBagConstraints.BOTH;
		gbc_panelMiddleTextArea.gridx = 0;
		gbc_panelMiddleTextArea.gridy = 1;
		panelUpper.add(panelMiddleTextArea, gbc_panelMiddleTextArea);
		GridBagLayout gbl_panelMiddleTextArea = new GridBagLayout();
		gbl_panelMiddleTextArea.columnWidths = new int[]{0, 0};
		gbl_panelMiddleTextArea.rowHeights = new int[]{0, 0};
		gbl_panelMiddleTextArea.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelMiddleTextArea.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panelMiddleTextArea.setLayout(gbl_panelMiddleTextArea);
		
		JScrollPane scrollPaneExceptionTextArea = new JScrollPane();
		GridBagConstraints gbc_scrollPaneExceptionTextArea = new GridBagConstraints();
		gbc_scrollPaneExceptionTextArea.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneExceptionTextArea.gridx = 0;
		gbc_scrollPaneExceptionTextArea.gridy = 0;
		panelMiddleTextArea.add(scrollPaneExceptionTextArea, gbc_scrollPaneExceptionTextArea);
		
		JTextArea exceptionTextArea = new JTextArea();
		exceptionTextArea.setFont(new Font("Tahoma", Font.PLAIN, 12));
		scrollPaneExceptionTextArea.setViewportView(exceptionTextArea);
		
		String title = e.getClass().toString();
		int index = title.lastIndexOf('.');
		if(index != -1 && index+1 < title.length()) {
			title = title.substring(index+1);
			f.setTitle("Exception Thrown: " + title);
		}
		

		lblExceptionType.setForeground(Color.RED);
		
		lblExceptionTypeDescription.setText(e.getMessage().toString());
		lblExceptionTypeDescription.setForeground(Color.RED);
		lblExceptionTypeDescription.setHorizontalAlignment(SwingConstants.LEFT);
		
		String errorMessage = stackTraceToString;
		exceptionTextArea.setForeground(Color.RED);
		exceptionTextArea.setText(errorMessage);
		
		JPanel panelDetailsPanel = new JPanel();
		GridBagConstraints gbc_panelDetailsPanel = new GridBagConstraints();
		gbc_panelDetailsPanel.fill = GridBagConstraints.BOTH;
		gbc_panelDetailsPanel.gridx = 0;
		gbc_panelDetailsPanel.gridy = 2;
		panelUpper.add(panelDetailsPanel, gbc_panelDetailsPanel);
		GridBagLayout gbl_panelDetailsPanel = new GridBagLayout();
		gbl_panelDetailsPanel.columnWidths = new int[]{120, 0, 120, 0};
		gbl_panelDetailsPanel.rowHeights = new int[]{25, 0};
		gbl_panelDetailsPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelDetailsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelDetailsPanel.setLayout(gbl_panelDetailsPanel);
		
		JButton btnShowDetails = new JButton("Show Detail (↓)");
		btnShowDetails.setHorizontalAlignment(SwingConstants.RIGHT);
		btnShowDetails.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnShowDetails = new GridBagConstraints();
		gbc_btnShowDetails.fill = GridBagConstraints.BOTH;
		gbc_btnShowDetails.insets = new Insets(0, 0, 0, 5);
		gbc_btnShowDetails.gridx = 0;
		gbc_btnShowDetails.gridy = 0;
		panelDetailsPanel.add(btnShowDetails, gbc_btnShowDetails);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				f.dispose();
			}
		});
		btnClose.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnClose = new GridBagConstraints();
		gbc_btnClose.fill = GridBagConstraints.BOTH;
		gbc_btnClose.gridx = 2;
		gbc_btnClose.gridy = 0;
		panelDetailsPanel.add(btnClose, gbc_btnClose);
		
		f.setDefaultCloseOperation(f.DISPOSE_ON_CLOSE);

		f.setVisible(true);
		f.setResizable(false);
		f.setPreferredSize(new Dimension(120*5, 100));
		f.pack();
		f.repaint();
		
		gbl_panelUpper.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelUpper.remove(panelMiddleTextArea);
		
		btnShowDetails.addActionListener(new ActionListener(){
			boolean mShowDetails = false;
			final int SHOW_DETAILS_HEIGHT = 400;
			final int HIDE_DETAILS_HEIGHT = 100;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mShowDetails = !mShowDetails;
				
				panelMiddleTextArea.setVisible(mShowDetails);
				Dimension size = new Dimension(f.getWidth(), SHOW_DETAILS_HEIGHT);
				if(mShowDetails) {
					size = new Dimension(f.getWidth(), SHOW_DETAILS_HEIGHT);
					btnShowDetails.setText("Show Details (↑)");
					
					gbl_panelUpper.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
					panelUpper.add(panelMiddleTextArea, gbc_panelMiddleTextArea);
					exceptionTextArea.setCaretPosition(0);
					panelUpper.repaint();
				} else {
					size = new Dimension(f.getWidth(), HIDE_DETAILS_HEIGHT);
					btnShowDetails.setText("Show Details (↓)");
					
					gbl_panelUpper.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
					panelUpper.remove(panelMiddleTextArea);
				}

				
				f.setPreferredSize(size);
				f.setMinimumSize(size);
				f.setMaximumSize(size);
				f.pack();
				
				
			}
			
		});
		
		
		
	}
}
