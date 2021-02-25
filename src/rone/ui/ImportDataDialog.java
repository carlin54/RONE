package rone.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ImportDataDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 5567669448342892681L;
	private String[] mData;
	private JComboBox<String> mColumnSelectA;
	private JComboBox<String> mColumnSelectB;

	private JButton mBtnOk;
	private JButton mBtnCancel;

	public ImportDataDialog(Frame parent, String fromWhere, String[] columnA, String[] columnB) {
		super(parent, "Pick Constraint", true);
		
		this.setTitle("Import " + fromWhere);
		
		Point loc = parent.getLocation();
		setLocation(loc.x+80,loc.y+80);
		mData = new String[2]; // set to amount of data items
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,2,2,2);
		JLabel descLabel = new JLabel("Table Columns:");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(descLabel,gbc);
		
		JLabel colorLabel = new JLabel("Import Columns:");
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(colorLabel,gbc);
		
		mColumnSelectA = new JComboBox<String>(columnA);
		gbc.gridwidth = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(mColumnSelectA, gbc);
		
		mColumnSelectB = new JComboBox<String>(columnB);
		gbc.gridwidth = 1;
		gbc.gridx = 1;
		gbc.gridy = 1;
		panel.add(mColumnSelectB, gbc);
	
		// try to select columns with same identifier
		for(int i = 0; i < columnA.length; i++) {
			for(int j = i; j < columnB.length; j++) {
				if(columnA[i].equals(columnB[j])) {
					mColumnSelectA.setSelectedItem(columnA[i]);
					mColumnSelectB.setSelectedItem(columnB[j]);
				}
			} 	  
		}
	
		JLabel spacer = new JLabel(" ");
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(spacer,gbc);
		mBtnOk = new JButton("Ok");
		mBtnOk.addActionListener(this);
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(mBtnOk,gbc);
		mBtnCancel = new JButton("Cancel");
		mBtnCancel.addActionListener(this);
		gbc.gridx = 1;
		gbc.gridy = 3;
		panel.add(mBtnCancel,gbc);
		getContentPane().add(panel);
		pack();
		
		mData = new String[2];
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == mBtnOk) {
			mData[0] = (String)mColumnSelectA.getSelectedItem();
			mData[1] = (String)mColumnSelectB.getSelectedItem();
		} else {
			mData[0] = null;
			mData[1] = null;
		}
		dispose();
	}

	public String[] run() {
		this.setVisible(true);
		return mData;
	}

	public String[] getData() {
		return this.mData;
	}
}