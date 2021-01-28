package rone.ui;

import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSplitPane;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.sun.org.apache.xml.internal.utils.ObjectPool;
import com.sun.tools.javac.util.Pair;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.List;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.border.BevelBorder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Panel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;

import java.awt.Choice;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.SwingConstants;
import java.awt.TextField;

public class TableJoinDialog extends JFrame  {
	private MainWindow mMainWindow; 
	private DatabaseTabbedPane mDatabaseTabbedPane; 
	
	private JPanel panel;
	private GridBagLayout gbl_panel;
	private JPanel panelTableSelect;
	private JLabel lblTableSelectTableA;
	private JLabel lblTableSelectTableB;
	private JComboBox choiceTableSelectA;
	private JComboBox choiceTableSelectB;
	private JPanel panelTableA;
	private JList listTableAExcludeColumns;
	private JList listTableAIncludeColumns;
	private JButton btnTableAInclude;
	private JButton btnTableAExclude;
	private JButton btnTableAIncludeAll;
	private JButton btnTableAExcludeAll;
	private JPanel panelTableB;
	private JList listTableBExcludeColumns;
	private JList listTableBIncludeColumns;
	private JButton btnTableBInclude;
	private JButton btnTableBExclude;
	private JButton btnTableBIncludeAll;
	private JButton btnTableBExcludeAll;
	private JPanel panelJoinOperation;
	private JButton choiceJoinAddConstraint;
	private JTable tableJoinConstraints;
	private JButton btnJoinRemoveAllConstraints;
	private JLabel lblJoinJoinType;
	private JComboBox choiceJoinJoinType;
	private JPanel panelNameTable;
	private JLabel lblNameTableNameTable;
	private JTextField txtFieldNameTableNewTableName;
	private JButton btnJoinTable;
	private JComboBox comboBoxJoinOperationTableA;
	private JComboBox comboBoxJoinOperationTableB;
	
	
	public TableJoinDialog(DatabaseTabbedPane pane) {
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Join Table");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		
		panel = new JPanel();
		getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {240, 0};
		gbl_panel.rowHeights = new int[] {0, 60, 60, 60, 40, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		panelTableSelect = new JPanel();
		panelTableSelect.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "1. Table Select", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelTableSelect = new GridBagConstraints();
		gbc_panelTableSelect.insets = new Insets(0, 0, 5, 0);
		gbc_panelTableSelect.fill = GridBagConstraints.BOTH;
		gbc_panelTableSelect.gridx = 0;
		gbc_panelTableSelect.gridy = 0;
		panel.add(panelTableSelect, gbc_panelTableSelect);
		GridBagLayout gbl_panelTableSelect = new GridBagLayout();
		gbl_panelTableSelect.columnWidths = new int[]{0, 0, 0};
		gbl_panelTableSelect.rowHeights = new int[]{0, 20, 0};
		gbl_panelTableSelect.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panelTableSelect.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelTableSelect.setLayout(gbl_panelTableSelect);
		
		lblTableSelectTableA = new JLabel("Table A:");
		lblTableSelectTableA.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblTableSelectTableA = new GridBagConstraints();
		gbc_lblTableSelectTableA.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTableSelectTableA.insets = new Insets(0, 0, 5, 5);
		gbc_lblTableSelectTableA.gridx = 0;
		gbc_lblTableSelectTableA.gridy = 0;
		panelTableSelect.add(lblTableSelectTableA, gbc_lblTableSelectTableA);
		
		lblTableSelectTableB = new JLabel("Table B:");
		lblTableSelectTableB.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblTableSelectTableB = new GridBagConstraints();
		gbc_lblTableSelectTableB.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTableSelectTableB.insets = new Insets(0, 0, 5, 0);
		gbc_lblTableSelectTableB.gridx = 1;
		gbc_lblTableSelectTableB.gridy = 0;
		panelTableSelect.add(lblTableSelectTableB, gbc_lblTableSelectTableB);
		
		choiceTableSelectA = new JComboBox();
		choiceTableSelectA.setMaximumRowCount(999);
		GridBagConstraints gbc_choiceTableSelectA = new GridBagConstraints();
		gbc_choiceTableSelectA.fill = GridBagConstraints.BOTH;
		gbc_choiceTableSelectA.insets = new Insets(0, 0, 0, 5);
		gbc_choiceTableSelectA.gridx = 0;
		gbc_choiceTableSelectA.gridy = 1;
		panelTableSelect.add(choiceTableSelectA, gbc_choiceTableSelectA);
		
		choiceTableSelectB = new JComboBox();
		choiceTableSelectB.setMaximumRowCount(999);
		GridBagConstraints gbc_choiceTableSelectB = new GridBagConstraints();
		gbc_choiceTableSelectB.fill = GridBagConstraints.BOTH;
		gbc_choiceTableSelectB.gridx = 1;
		gbc_choiceTableSelectB.gridy = 1;
		panelTableSelect.add(choiceTableSelectB, gbc_choiceTableSelectB);
		
		panelTableA = new JPanel();
		panelTableA.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "2. Table A", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelTableA = new GridBagConstraints();
		gbc_panelTableA.fill = GridBagConstraints.BOTH;
		gbc_panelTableA.insets = new Insets(0, 0, 5, 0);
		gbc_panelTableA.gridx = 0;
		gbc_panelTableA.gridy = 1;
		panel.add(panelTableA, gbc_panelTableA);
		GridBagLayout gbl_panelTableA = new GridBagLayout();
		gbl_panelTableA.columnWidths = new int[] {30, 30, 30};
		gbl_panelTableA.rowHeights = new int[]{0, 20, 20, 0, 20, 0};
		gbl_panelTableA.columnWeights = new double[]{1.0, 0.0, 1.0};
		gbl_panelTableA.rowWeights = new double[]{1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panelTableA.setLayout(gbl_panelTableA);
		
		listTableAExcludeColumns = new JList();
		listTableAExcludeColumns.setBorder(new LineBorder(Color.DARK_GRAY));
		GridBagConstraints gbc_listTableAExcludeColumns = new GridBagConstraints();
		gbc_listTableAExcludeColumns.gridheight = 4;
		gbc_listTableAExcludeColumns.insets = new Insets(0, 0, 5, 5);
		gbc_listTableAExcludeColumns.fill = GridBagConstraints.BOTH;
		gbc_listTableAExcludeColumns.gridx = 0;
		gbc_listTableAExcludeColumns.gridy = 0;
		panelTableA.add(listTableAExcludeColumns, gbc_listTableAExcludeColumns);
		
		listTableAIncludeColumns = new JList();
		listTableAIncludeColumns.setBorder(new LineBorder(Color.DARK_GRAY));
		GridBagConstraints gbc_listTableAIncludeColumns = new GridBagConstraints();
		gbc_listTableAIncludeColumns.gridheight = 4;
		gbc_listTableAIncludeColumns.insets = new Insets(0, 0, 5, 0);
		gbc_listTableAIncludeColumns.fill = GridBagConstraints.BOTH;
		gbc_listTableAIncludeColumns.gridx = 2;
		gbc_listTableAIncludeColumns.gridy = 0;
		panelTableA.add(listTableAIncludeColumns, gbc_listTableAIncludeColumns);
		
		btnTableAInclude = new JButton(">>");
		GridBagConstraints gbc_btnTableAInclude = new GridBagConstraints();
		gbc_btnTableAInclude.fill = GridBagConstraints.BOTH;
		gbc_btnTableAInclude.insets = new Insets(0, 0, 5, 5);
		gbc_btnTableAInclude.gridx = 1;
		gbc_btnTableAInclude.gridy = 1;
		panelTableA.add(btnTableAInclude, gbc_btnTableAInclude);
		
		btnTableAExclude = new JButton("<<");
		GridBagConstraints gbc_btnTableAExclude = new GridBagConstraints();
		gbc_btnTableAExclude.fill = GridBagConstraints.BOTH;
		gbc_btnTableAExclude.insets = new Insets(0, 0, 5, 5);
		gbc_btnTableAExclude.gridx = 1;
		gbc_btnTableAExclude.gridy = 2;
		panelTableA.add(btnTableAExclude, gbc_btnTableAExclude);
		
		btnTableAIncludeAll = new JButton("Add All");
		btnTableAIncludeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		GridBagConstraints gbc_btnTableAIncludeAll = new GridBagConstraints();
		gbc_btnTableAIncludeAll.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTableAIncludeAll.insets = new Insets(0, 0, 0, 5);
		gbc_btnTableAIncludeAll.gridx = 0;
		gbc_btnTableAIncludeAll.gridy = 4;
		panelTableA.add(btnTableAIncludeAll, gbc_btnTableAIncludeAll);
		
		btnTableAExcludeAll = new JButton("Remove All");
		GridBagConstraints gbc_btnTableAExcludeAll = new GridBagConstraints();
		gbc_btnTableAExcludeAll.fill = GridBagConstraints.BOTH;
		gbc_btnTableAExcludeAll.gridx = 2;
		gbc_btnTableAExcludeAll.gridy = 4;
		panelTableA.add(btnTableAExcludeAll, gbc_btnTableAExcludeAll);
		
		panelTableB = new JPanel();
		panelTableB.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "3. Table B", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panelTableB = new GridBagConstraints();
		gbc_panelTableB.insets = new Insets(0, 0, 5, 0);
		gbc_panelTableB.fill = GridBagConstraints.BOTH;
		gbc_panelTableB.gridx = 0;
		gbc_panelTableB.gridy = 2;
		panel.add(panelTableB, gbc_panelTableB);
		GridBagLayout gbl_panelTableB = new GridBagLayout();
		gbl_panelTableB.columnWidths = new int[] {30, 30, 30};
		gbl_panelTableB.rowHeights = new int[] {0, 20, 20, 0, 20};
		gbl_panelTableB.columnWeights = new double[]{1.0, 0.0, 1.0};
		gbl_panelTableB.rowWeights = new double[]{1.0, 0.0, 0.0, 1.0, 0.0};
		panelTableB.setLayout(gbl_panelTableB);
		
		listTableBExcludeColumns = new JList();
		listTableBExcludeColumns.setBorder(new LineBorder(Color.DARK_GRAY));
		GridBagConstraints gbc_listTableBExcludeColumns = new GridBagConstraints();
		gbc_listTableBExcludeColumns.gridheight = 4;
		gbc_listTableBExcludeColumns.insets = new Insets(0, 0, 5, 5);
		gbc_listTableBExcludeColumns.fill = GridBagConstraints.BOTH;
		gbc_listTableBExcludeColumns.gridx = 0;
		gbc_listTableBExcludeColumns.gridy = 0;
		panelTableB.add(listTableBExcludeColumns, gbc_listTableBExcludeColumns);
		
		listTableBIncludeColumns = new JList();
		listTableBIncludeColumns.setBorder(new LineBorder(Color.DARK_GRAY));
		GridBagConstraints gbc_listTableBIncludeColumns = new GridBagConstraints();
		gbc_listTableBIncludeColumns.gridheight = 4;
		gbc_listTableBIncludeColumns.insets = new Insets(0, 0, 5, 0);
		gbc_listTableBIncludeColumns.fill = GridBagConstraints.BOTH;
		gbc_listTableBIncludeColumns.gridx = 2;
		gbc_listTableBIncludeColumns.gridy = 0;
		panelTableB.add(listTableBIncludeColumns, gbc_listTableBIncludeColumns);
		
		btnTableBInclude = new JButton(">>");
		GridBagConstraints gbc_btnTableBInclude = new GridBagConstraints();
		gbc_btnTableBInclude.fill = GridBagConstraints.VERTICAL;
		gbc_btnTableBInclude.insets = new Insets(0, 0, 5, 5);
		gbc_btnTableBInclude.gridx = 1;
		gbc_btnTableBInclude.gridy = 1;
		panelTableB.add(btnTableBInclude, gbc_btnTableBInclude);
		
		btnTableBExclude = new JButton("<<");
		GridBagConstraints gbc_btnTableBExclude = new GridBagConstraints();
		gbc_btnTableBExclude.fill = GridBagConstraints.VERTICAL;
		gbc_btnTableBExclude.insets = new Insets(0, 0, 5, 5);
		gbc_btnTableBExclude.gridx = 1;
		gbc_btnTableBExclude.gridy = 2;
		panelTableB.add(btnTableBExclude, gbc_btnTableBExclude);
		
		btnTableBIncludeAll = new JButton("Add All");
		btnTableBIncludeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		GridBagConstraints gbc_btnTableBIncludeAll = new GridBagConstraints();
		gbc_btnTableBIncludeAll.fill = GridBagConstraints.BOTH;
		gbc_btnTableBIncludeAll.insets = new Insets(0, 0, 0, 5);
		gbc_btnTableBIncludeAll.gridx = 0;
		gbc_btnTableBIncludeAll.gridy = 4;
		panelTableB.add(btnTableBIncludeAll, gbc_btnTableBIncludeAll);
		
		btnTableBExcludeAll = new JButton("Remove All");
		GridBagConstraints gbc_btnTableBExcludeAll = new GridBagConstraints();
		gbc_btnTableBExcludeAll.fill = GridBagConstraints.BOTH;
		gbc_btnTableBExcludeAll.gridx = 2;
		gbc_btnTableBExcludeAll.gridy = 4;
		panelTableB.add(btnTableBExcludeAll, gbc_btnTableBExcludeAll);
		
		panelJoinOperation = new JPanel();
		GridBagConstraints gbc_panelJoinOperation = new GridBagConstraints();
		gbc_panelJoinOperation.insets = new Insets(0, 0, 5, 0);
		gbc_panelJoinOperation.fill = GridBagConstraints.BOTH;
		gbc_panelJoinOperation.gridx = 0;
		gbc_panelJoinOperation.gridy = 3;
		panel.add(panelJoinOperation, gbc_panelJoinOperation);
		panelJoinOperation.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "4. Join Operation", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		GridBagLayout gbl_panelJoinOperation = new GridBagLayout();
		gbl_panelJoinOperation.columnWidths = new int[] {30};
		gbl_panelJoinOperation.rowHeights = new int[] {20, 20, 0, 20, 30};
		gbl_panelJoinOperation.columnWeights = new double[]{1.0, 1.0, 0.0};
		gbl_panelJoinOperation.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0};
		panelJoinOperation.setLayout(gbl_panelJoinOperation);
		
		choiceJoinAddConstraint = new JButton("Add Constraint (=)");
		choiceJoinAddConstraint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		comboBoxJoinOperationTableA = new JComboBox();
		GridBagConstraints gbc_comboBoxJoinOperationTableA = new GridBagConstraints();
		gbc_comboBoxJoinOperationTableA.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxJoinOperationTableA.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxJoinOperationTableA.gridx = 0;
		gbc_comboBoxJoinOperationTableA.gridy = 0;
		panelJoinOperation.add(comboBoxJoinOperationTableA, gbc_comboBoxJoinOperationTableA);
		
		comboBoxJoinOperationTableB = new JComboBox();
		GridBagConstraints gbc_comboBoxJoinOperationTableB = new GridBagConstraints();
		gbc_comboBoxJoinOperationTableB.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxJoinOperationTableB.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxJoinOperationTableB.gridx = 1;
		gbc_comboBoxJoinOperationTableB.gridy = 0;
		panelJoinOperation.add(comboBoxJoinOperationTableB, gbc_comboBoxJoinOperationTableB);
		GridBagConstraints gbc_choiceJoinAddConstraint = new GridBagConstraints();
		gbc_choiceJoinAddConstraint.fill = GridBagConstraints.VERTICAL;
		gbc_choiceJoinAddConstraint.insets = new Insets(0, 0, 5, 0);
		gbc_choiceJoinAddConstraint.gridx = 2;
		gbc_choiceJoinAddConstraint.gridy = 0;
		panelJoinOperation.add(choiceJoinAddConstraint, gbc_choiceJoinAddConstraint);
		
		tableJoinConstraints = new JTable();
		tableJoinConstraints.setBorder(new LineBorder(Color.DARK_GRAY));
		GridBagConstraints gbc_tableJoinConstraints = new GridBagConstraints();
		gbc_tableJoinConstraints.gridheight = 4;
		gbc_tableJoinConstraints.gridwidth = 2;
		gbc_tableJoinConstraints.insets = new Insets(0, 0, 0, 5);
		gbc_tableJoinConstraints.fill = GridBagConstraints.BOTH;
		gbc_tableJoinConstraints.gridx = 0;
		gbc_tableJoinConstraints.gridy = 1;
		panelJoinOperation.add(tableJoinConstraints, gbc_tableJoinConstraints);
		
		btnJoinRemoveAllConstraints = new JButton("Remove Selected");
		GridBagConstraints gbc_btnJoinRemoveAllConstraints = new GridBagConstraints();
		gbc_btnJoinRemoveAllConstraints.insets = new Insets(0, 0, 5, 0);
		gbc_btnJoinRemoveAllConstraints.fill = GridBagConstraints.BOTH;
		gbc_btnJoinRemoveAllConstraints.gridx = 2;
		gbc_btnJoinRemoveAllConstraints.gridy = 1;
		panelJoinOperation.add(btnJoinRemoveAllConstraints, gbc_btnJoinRemoveAllConstraints);
		
		lblJoinJoinType = new JLabel("Join Type:");
		lblJoinJoinType.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblJoinJoinType = new GridBagConstraints();
		gbc_lblJoinJoinType.fill = GridBagConstraints.BOTH;
		gbc_lblJoinJoinType.insets = new Insets(0, 0, 5, 0);
		gbc_lblJoinJoinType.gridx = 2;
		gbc_lblJoinJoinType.gridy = 3;
		panelJoinOperation.add(lblJoinJoinType, gbc_lblJoinJoinType);
		
		choiceJoinJoinType = new JComboBox();
		choiceJoinJoinType.setEnabled(false);
		GridBagConstraints gbc_choiceJoinJoinType = new GridBagConstraints();
		gbc_choiceJoinJoinType.fill = GridBagConstraints.HORIZONTAL;
		gbc_choiceJoinJoinType.gridx = 2;
		gbc_choiceJoinJoinType.gridy = 4;
		panelJoinOperation.add(choiceJoinJoinType, gbc_choiceJoinJoinType);
		
		panelNameTable = new JPanel();
		panelNameTable.setBorder(new TitledBorder(null, "5. Name Table", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelNameTable = new GridBagConstraints();
		gbc_panelNameTable.fill = GridBagConstraints.BOTH;
		gbc_panelNameTable.gridx = 0;
		gbc_panelNameTable.gridy = 4;
		panel.add(panelNameTable, gbc_panelNameTable);
		GridBagLayout gbl_panelNameTable = new GridBagLayout();
		gbl_panelNameTable.columnWidths = new int[]{50, 0, 50, 0};
		gbl_panelNameTable.rowHeights = new int[]{0, 0};
		gbl_panelNameTable.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelNameTable.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelNameTable.setLayout(gbl_panelNameTable);
		
		lblNameTableNameTable = new JLabel("New Table Name:");
		lblNameTableNameTable.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNameTableNameTable = new GridBagConstraints();
		gbc_lblNameTableNameTable.insets = new Insets(0, 0, 0, 5);
		gbc_lblNameTableNameTable.gridx = 0;
		gbc_lblNameTableNameTable.gridy = 0;
		panelNameTable.add(lblNameTableNameTable, gbc_lblNameTableNameTable);
		
		txtFieldNameTableNewTableName = new JTextField();
		GridBagConstraints gbc_txtFieldNameTableNewTableName = new GridBagConstraints();
		gbc_txtFieldNameTableNewTableName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldNameTableNewTableName.insets = new Insets(0, 0, 0, 5);
		gbc_txtFieldNameTableNewTableName.gridx = 1;
		gbc_txtFieldNameTableNewTableName.gridy = 0;
		panelNameTable.add(txtFieldNameTableNewTableName, gbc_txtFieldNameTableNewTableName);
		
		btnJoinTable = new JButton("Join Tables");
		GridBagConstraints gbc_btnJoinTable = new GridBagConstraints();
		gbc_btnJoinTable.fill = GridBagConstraints.BOTH;
		gbc_btnJoinTable.gridx = 2;
		gbc_btnJoinTable.gridy = 0;
		panelNameTable.add(btnJoinTable, gbc_btnJoinTable);
		btnJoinTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		this.mDatabaseTabbedPane = pane;
		
		this.setSize(480, 720);
		
		setIcon();
		
		init();
		
	}
	
	private void setIcon() {
		Path currentRelativePath = Paths.get("");
		String iconLocation = currentRelativePath.toAbsolutePath().toString() + "\\rone_logo.png";
		ImageIcon imgicon = new ImageIcon(iconLocation);
		this.setIconImage(imgicon.getImage());
	}
	
	private ArrayList<Pair<String, DataTable>> mTableNames;
	
	private DataTable getDataTable(String select) {
		mTableNames = mDatabaseTabbedPane.getTabNames();
		
		for(Pair<String, DataTable> pair : mTableNames) {
			if(select.equals(pair.fst)) {
				return pair.snd;
			}
		}			
		
		return null;
	}
	
	private class ChoiceTableSelectListener implements ItemListener {
		
		public JComboBox mComboBoxToListenTo;
		public JComboBox mComboBoxToListenToModify;
		public JList mIncludeColumn;
		public JList mExcludeColumn;
		
		public JButton mIncludeButton;
		public JButton mExcludeButton;
		public JButton mIncludeAllButton;
		public JButton mExcludeAllButton;
		
		
		
		public ChoiceTableSelectListener(JComboBox comboBoxToListenTo, 
										 JComboBox comboBoxToListenToModify,
										 JList includeColumn, 
										 JList excludeColumn,
										 JButton includeAllButton,
										 JButton excludeAllButton)
		{
			
			mComboBoxToListenTo = comboBoxToListenTo;
			mComboBoxToListenToModify = comboBoxToListenToModify;
			mIncludeColumn = includeColumn;
			mExcludeColumn = excludeColumn;
			mIncludeAllButton = includeAllButton;
			mExcludeAllButton = excludeAllButton;
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {
        	choiceTableSelectA.getSelectedItem();
            if(e.getStateChange() == ItemEvent.SELECTED){
                String selected = (String) e.getItem();
                DataTable dataTable = getDataTable(selected);
                assert(dataTable != null);
                String[] columns = dataTable.getIdentifiers();
                
                DefaultListModel newExclude = new DefaultListModel(); 
                for(String column : columns) {
                	newExclude.addElement(column);
                }
                mExcludeColumn.setModel(newExclude);
                mIncludeColumn.setModel(new DefaultListModel());

            	mComboBoxToListenToModify.removeItem(selected);
            	
        		
        		mIncludeAllButton.setEnabled(true);
        		mExcludeAllButton.setEnabled(false);
            	
            }
            
            if(e.getStateChange() == ItemEvent.DESELECTED){
            	String deselected = (String) e.getItem(); 
            	ComboBoxModel newModifyModel = mComboBoxToListenToModify.getModel();
            	mComboBoxToListenToModify.addItem(deselected);
            }
            
		}
		
	}

	private class ListTableButtonControlActionListener implements MouseListener {
		
		JList mList;
		JButton mButton; 
		
		public ListTableButtonControlActionListener(JList list, JButton btn){
			mList = list;
			mButton = btn;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			System.out.println("mouseClicked!");
			boolean enableBtn = !mList.isSelectionEmpty();
			mButton.setEnabled(enableBtn);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			System.out.println("mouseClicked!");
			boolean enableBtn = !mList.isSelectionEmpty();
			mButton.setEnabled(enableBtn);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			System.out.println("mouseClicked!");
			boolean enableBtn = !mList.isSelectionEmpty();
			mButton.setEnabled(enableBtn);
			
		}
		
		
		
	}
	


	private class ButtonMoveControlListTableActionListener implements ActionListener {
		
		private JList mTo;
		private JButton mToButton;
		private JButton mToButtonAll;
		private JComboBox mToComboBox;
		
		private JList mFrom;
		private JButton mFromButton;
		private JButton mFromButtonAll;	
		private JComboBox mFromComboBox;
		
		boolean mMoveSelectedMode;

		private boolean canSelectJoinType() {
			return 		listTableAIncludeColumns.getModel().getSize() > 0
					&&  listTableBIncludeColumns.getModel().getSize() > 0;
		}
		
		public void updateComboBoxJoinType() {
			boolean enableComboBox = canSelectJoinType();
			choiceJoinJoinType.setEnabled(enableComboBox);
		}
		
		private ButtonMoveControlListTableActionListener(
				JList from, 
				JButton fromBtn, 
				JButton fromBtnAll, 
				JComboBox fromComboBox, 
				JList to, 
				JButton toBtn, 
				JButton toBtnAll, 
				JComboBox toComboBox, 
				boolean moveSelectedMode){

			mFrom = from;
			mFromButton = fromBtn;
			mFromButtonAll = fromBtnAll;
			mFromComboBox = fromComboBox; 
			

			mTo = to;
			mToButton = toBtn;
			mToButtonAll = toBtnAll;
			mToComboBox = toComboBox;
			
			mMoveSelectedMode = moveSelectedMode;
		}
		
		private boolean moveSelectedMode() { return mMoveSelectedMode; }
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(moveSelectedMode()) {
				moveSelected();	
			} else {
				moveAll(); 
			}
			
			updateButtons();
			
			updateComboBoxes();
			
		}
		
		private boolean hasComboBox(JComboBox comboBox) {
			return comboBox != null;
		}
		
		private void updateComboBoxes() {
			if(hasComboBox(this.mFromComboBox)) {
				insertListIntoComboBoxModel(this.mFrom, this.mFromComboBox);
			}
			
			if(hasComboBox(this.mToComboBox)) {
				insertListIntoComboBoxModel(this.mTo, this.mToComboBox);
			}
			
			updateComboBoxJoinType();
		}
		
		private void updateButtons() {
			boolean toBtnEnabled = !isListEmpty(mTo);
			mToButton.setEnabled(false);
			mToButtonAll.setEnabled(toBtnEnabled);
			
			boolean fromBtnEnabled = !isListEmpty(mFrom);
			mFromButton.setEnabled(false);
			mFromButtonAll.setEnabled(fromBtnEnabled);
		}
		
		private void moveSelected() {
			
			DefaultListModel newFromModel = new DefaultListModel(); 
			DefaultListModel newToModel = new DefaultListModel(); 
			
			int[] selectedIdx = mFrom.getSelectedIndices();
			
			ListModel oldFromModel = mFrom.getModel();
			
			// add all existing cells to the to new to model
			ListModel oldToModel = mTo.getModel();
			for(int i = 0; i < oldToModel.getSize(); i++) {
				Object element = oldToModel.getElementAt(i);
				newToModel.addElement(element);
			}
			
			// add all selected cells to the new to model
			// add all non-selected cells to the new from model
			int j = 0;
			for(int i = 0; i < oldFromModel.getSize(); i++) {
				Object element = oldFromModel.getElementAt(i);
				
				if(j < selectedIdx.length) {
					if(i == selectedIdx[j]) {
						newToModel.addElement(element);
						j++;
						continue;
					}
				}
				
				newFromModel.addElement(element);
				
			}
			
			// set the new to models
			mFrom.setModel(newFromModel);
			mTo.setModel(newToModel);
			
		}
		
		private void moveAll() {
			DefaultListModel newFromModel = new DefaultListModel(); 
			DefaultListModel newToModel = new DefaultListModel(); 
			
			ListModel oldFromModel = mFrom.getModel();
			ListModel oldToModel = mTo.getModel();
			
			// all all elements from old to model to new to model
			for(int i = 0; i < oldToModel.getSize(); i++) {
				Object element = oldToModel.getElementAt(i);
				newToModel.addElement(element);
			}
			
			
			// all all elements from old from model to new model 
			for(int i = 0; i < oldFromModel.getSize(); i++) {
				Object element = oldFromModel.getElementAt(i);
				newToModel.addElement(element);
			}
			
			// set the new to models
			mFrom.setModel(newFromModel);
			mTo.setModel(newToModel);
		}
		
		private boolean hasComboBox() {
			return mToComboBox != null;
		}
		
		private void insertListIntoComboBoxModel(JList list, JComboBox comboBox) {
			
			if(isListEmpty(list)) {
				comboBox.setEnabled(false);
				comboBox.setModel(new DefaultComboBoxModel());
			} else {
				comboBox.setEnabled(true);
				ListModel oldListModel = list.getModel();
				DefaultComboBoxModel newComboBoxModel = new DefaultComboBoxModel();
				for(int i = 0; i < oldListModel.getSize(); i++) {
					Object element = oldListModel.getElementAt(i);
					newComboBoxModel.addElement(element);
				}
				comboBox.setModel(newComboBoxModel);
				comboBox.setSelectedIndex(-1);
			}
			
			
			
		}
		
		private boolean isListEmpty(JList list) {
			return list.getModel().getSize() < 1; 
		}
		
	};
	
	private class ChoiceJoinOperationItemListener implements ItemListener {
		
		JComboBox mComboBoxA;
		JComboBox mComboBoxB;
		JComboBox mComboBoxJoinType;
		JButton mBtnAddConstraint;
		
		ChoiceJoinOperationItemListener(JComboBox comboBoxA, JComboBox comboBoxB, JButton btnAddConstraint, JComboBox comboBoxJoinType){
			mComboBoxA = comboBoxA;
			mComboBoxB = comboBoxB;
			mBtnAddConstraint = btnAddConstraint;
			mComboBoxJoinType = comboBoxJoinType;
		}
		
		private boolean hasBothSelected() {
			return mComboBoxA.getSelectedIndex() != -1 && mComboBoxB.getSelectedIndex() != -1;
		}
		
		private boolean hasSelectedJoinType() {
			return  mComboBoxJoinType.getSelectedIndex() != -1;
		}
		
		private boolean canAddConstraint() {
			return hasBothSelected() && hasSelectedJoinType();
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(canAddConstraint()) {
				mBtnAddConstraint.setEnabled(true);
			} else {
				mBtnAddConstraint.setEnabled(false);
			}
		}
		
	}
	
	private class ChoiceListJoinType implements ActionListener {
		
		private JList mListIncludeColumnsA;
		private JList mListIncludeColumnsB;
		private JComboBox mComboBoxJoinType; 
		
		public ChoiceListJoinType(JList includeA, JList includeB, JComboBox joinType){
			mListIncludeColumnsA = includeA;
			mListIncludeColumnsB = includeB;
			mComboBoxJoinType = joinType; 
		}
		
		private boolean canSelectJoinType() {
			return 		mListIncludeColumnsA.getModel().getSize() > 0
					&&  mListIncludeColumnsB.getModel().getSize() > 0;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean enableComboBox = canSelectJoinType();
			mComboBoxJoinType.setEnabled(enableComboBox);
		}
		
	}
	
	private ChoiceTableSelectListener mChoiceSelectTableA;
	private ChoiceTableSelectListener mChoiceSelectTableB;
	
	private ListTableButtonControlActionListener mListTableAIncludeColumnButtonControlActionListener;
	private ListTableButtonControlActionListener mListTableAExcludeColumnButtonControlActionListener;
	
	private ListTableButtonControlActionListener mListTableBIncludeColumnButtonControlActionListener;
	private ListTableButtonControlActionListener mListTableBExcludeColumnButtonControlActionListener;

	private ButtonMoveControlListTableActionListener mButtonTableAIncludeControlListActionListener;
	private ButtonMoveControlListTableActionListener mButtonTableAExcludeControlListActionListener;
	private ButtonMoveControlListTableActionListener mButtonTableBIncludeControlListActionListener;
	private ButtonMoveControlListTableActionListener mButtonTableBExcludeControlListActionListener;
	
	private ButtonMoveControlListTableActionListener mButtonTableAIncludeAllControlListActionListener;
	private ButtonMoveControlListTableActionListener mButtonTableAExcludeAllControlListActionListener; 
	private ButtonMoveControlListTableActionListener mButtonTableBIncludeAllControlListActionListener;
	private ButtonMoveControlListTableActionListener mButtonTableBExcludeAllControlListActionListener; 
	
	private JComboBox mComboBoxJoinTypeItemListener;
	private JComboBox mComboBoxJoinOperationTableAItemListener;
	private JComboBox mComboBoxJoinOperationTableBActionListner;
	
	private ChoiceJoinOperationItemListener mComboBoxJoinOperationTableItemListener;

	private ChoiceListJoinType mListIncludeExcludeJoinTypeAddConstraintActionListener;
	
	void init() {
		
		// Pre Stage 1 
		mTableNames = mDatabaseTabbedPane.getTabNames();
		
		for(Pair<String, DataTable> pair : mTableNames) {
			String name = pair.fst;
			choiceTableSelectA.addItem(name);
			choiceTableSelectB.addItem(name);
			
			DataTable table = pair.snd;
		}
		
		choiceTableSelectA.setSelectedIndex(-1);
		choiceTableSelectB.setSelectedIndex(-1);
		
		// Stage 1 
		mChoiceSelectTableA = new ChoiceTableSelectListener(
				choiceTableSelectA, 
				choiceTableSelectB, 
				listTableAIncludeColumns, 
				listTableAExcludeColumns,
				btnTableAIncludeAll,
				btnTableAExcludeAll);
		choiceTableSelectA.addItemListener(mChoiceSelectTableA);
		
		mChoiceSelectTableB = new ChoiceTableSelectListener(
				choiceTableSelectB, 
				choiceTableSelectA, 
				listTableBIncludeColumns, 
				listTableBExcludeColumns,
				btnTableBIncludeAll,
				btnTableBExcludeAll);
		choiceTableSelectB.addItemListener(mChoiceSelectTableB);
		

		// Stage 2	
		// Mouse Listeners for the selection columns
		mListTableAExcludeColumnButtonControlActionListener = 
				new ListTableButtonControlActionListener(listTableAExcludeColumns, btnTableAInclude);
		listTableAExcludeColumns.addMouseListener(mListTableAExcludeColumnButtonControlActionListener);
		
		mListTableAIncludeColumnButtonControlActionListener = 
				new ListTableButtonControlActionListener(listTableAIncludeColumns, btnTableAExclude);
		listTableAIncludeColumns.addMouseListener(mListTableAIncludeColumnButtonControlActionListener);
		
		mListTableBExcludeColumnButtonControlActionListener = 
				new ListTableButtonControlActionListener(listTableBExcludeColumns, btnTableBInclude);
		listTableBExcludeColumns.addMouseListener(mListTableBExcludeColumnButtonControlActionListener);
		
		mListTableBIncludeColumnButtonControlActionListener = 
				new ListTableButtonControlActionListener(listTableBIncludeColumns, btnTableBExclude);
		listTableBIncludeColumns.addMouseListener(mListTableBIncludeColumnButtonControlActionListener);
		
		// Select Include/Exclude "<<" and ">>", as well as Include/Exclude all "Add All" and "Remove All"
		mButtonTableAIncludeControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableAExcludeColumns,
						btnTableAInclude,
						btnTableAIncludeAll,
						null,
						listTableAIncludeColumns,
						btnTableAExclude,
						btnTableAExcludeAll,
						comboBoxJoinOperationTableA,
						true);
		btnTableAInclude.addActionListener(mButtonTableAIncludeControlListActionListener);
		
		mButtonTableAExcludeControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableAIncludeColumns,
						btnTableAExclude,
						btnTableAExcludeAll,
						comboBoxJoinOperationTableA,
						listTableAExcludeColumns,
						btnTableAInclude,
						btnTableAIncludeAll,
						null,
						true);
		btnTableAExclude.addActionListener(mButtonTableAExcludeControlListActionListener);
		
		mButtonTableBIncludeControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableBExcludeColumns,
						btnTableBInclude,
						btnTableBIncludeAll,
						null,
						listTableBIncludeColumns,
						btnTableBExclude,
						btnTableBExcludeAll,
						comboBoxJoinOperationTableB,
						true);
		btnTableBInclude.addActionListener(mButtonTableBIncludeControlListActionListener);
		
		mButtonTableBExcludeControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableBIncludeColumns,
						btnTableBExclude,
						btnTableBExcludeAll,
						comboBoxJoinOperationTableB,
						listTableBExcludeColumns,
						btnTableBInclude,
						btnTableBIncludeAll,
						null,
						true);
		btnTableBExclude.addActionListener(mButtonTableBExcludeControlListActionListener);
		
		mButtonTableAIncludeAllControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableAExcludeColumns,
						btnTableAInclude,
						btnTableAIncludeAll,
						null,
						listTableAIncludeColumns,
						btnTableAExclude,
						btnTableAExcludeAll,
						comboBoxJoinOperationTableA,
						false);
		btnTableAIncludeAll.addActionListener(mButtonTableAIncludeAllControlListActionListener);
		
		mButtonTableAExcludeAllControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableAIncludeColumns,
						btnTableAExclude,
						btnTableAExcludeAll,
						comboBoxJoinOperationTableA,
						listTableAExcludeColumns,
						btnTableAInclude,
						btnTableAIncludeAll,
						null,
						false);
		btnTableAExcludeAll.addActionListener(mButtonTableAExcludeAllControlListActionListener);
		
		mButtonTableBIncludeAllControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableBExcludeColumns,
						btnTableBInclude,
						btnTableBIncludeAll,
						null,
						listTableBIncludeColumns,
						btnTableBExclude,
						btnTableBExcludeAll,
						comboBoxJoinOperationTableB,
						false);
		btnTableBIncludeAll.addActionListener(mButtonTableBIncludeAllControlListActionListener);
		
		mButtonTableBExcludeAllControlListActionListener = 
				new ButtonMoveControlListTableActionListener(
						listTableBIncludeColumns,
						btnTableBExclude,
						btnTableBExcludeAll,
						comboBoxJoinOperationTableB,
						listTableBExcludeColumns,
						btnTableBInclude,
						btnTableBIncludeAll,
						null,
						false);
		btnTableBExcludeAll.addActionListener(mButtonTableBExcludeAllControlListActionListener);
		
		btnTableAInclude.setEnabled(false);
		btnTableAExclude.setEnabled(false);
		btnTableAIncludeAll.setEnabled(false);
		btnTableAExcludeAll.setEnabled(false);
		btnTableBInclude.setEnabled(false);
		btnTableBExclude.setEnabled(false);
		btnTableBIncludeAll.setEnabled(false);
		btnTableBExcludeAll.setEnabled(false);
		// Stage 3
			
		// Pre-Stage 4 
		mListIncludeExcludeJoinTypeAddConstraintActionListener = 
				new ChoiceListJoinType(
						listTableAIncludeColumns, 
						listTableBIncludeColumns, 
						choiceJoinJoinType);
		
		btnTableAInclude.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableAExclude.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableAIncludeAll.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableAExcludeAll.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableBInclude.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableBExclude.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableBIncludeAll.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		btnTableBExcludeAll.addActionListener(mListIncludeExcludeJoinTypeAddConstraintActionListener);
		
		// Stage 4
		choiceJoinJoinType.addItem("LEFT");
		choiceJoinJoinType.addItem("INNER");
		choiceJoinJoinType.addItem("RIGHT");
		choiceJoinJoinType.addItem("SELF");
		choiceJoinJoinType.addItem("FULL");
		choiceJoinJoinType.addItem("RIGHT");
		choiceJoinJoinType.addItem("CARTESIAN");
		choiceJoinJoinType.setSelectedIndex(-1);
		choiceJoinJoinType.setEnabled(false);
		
		choiceJoinAddConstraint.setEnabled(false);
		btnJoinRemoveAllConstraints.setEnabled(false);
		tableJoinConstraints.setEnabled(false);
		
		mComboBoxJoinOperationTableItemListener = 
				new ChoiceJoinOperationItemListener(comboBoxJoinOperationTableA, comboBoxJoinOperationTableB, choiceJoinAddConstraint, choiceJoinJoinType);
		
		//comboBoxJoinOperationTableA.addItemListener(mComboBoxJoinOperationTableItemListener);
		//comboBoxJoinOperationTableB.addItemListener(mComboBoxJoinOperationTableItemListener);
		//choiceJoinJoinType.addItemListener(mComboBoxJoinOperationTableItemListener);
		
		comboBoxJoinOperationTableA.setEnabled(false);
		comboBoxJoinOperationTableB.setEnabled(false);
		
		// Stage 5
		this.txtFieldNameTableNewTableName.setEnabled(false);
		this.btnJoinTable.setEnabled(false);
		
	}

}
