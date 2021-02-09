package rone.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.javatuples.Triplet;

import com.sun.tools.javac.util.ArrayUtils;
import com.sun.tools.javac.util.Pair;

import rone.backend.SearchInterface;
import rone.filemanager.Database;
import rone.filemanager.Table;
import rone.backend.Search;
import rone.filemanager.Database.Join;



public class DatabaseTabbedPane extends JTabbedPane {
	
	ArrayList<Tab> mDatabaseTabs; 
	SearchThreadManager mSearchThreadManager; 
	JoinOperationThreadManager mJoinOperationThreadManager;
	
	public DatabaseTabbedPane(int position){
		super(position);
		
		mDatabaseTabs = new ArrayList<Tab>();
		
		mSearchThreadManager = new SearchThreadManager();
		mSearchThreadManager.start();
		
		mJoinOperationThreadManager = new JoinOperationThreadManager();
		mJoinOperationThreadManager.start();
		
	}
	
	protected void finalize ()
	{  
		mSearchThreadManager.end();
		mJoinOperationThreadManager.end();

		
		try {
			mSearchThreadManager.join();
		} catch (InterruptedException e) {
			mSearchThreadManager.stop();
			e.printStackTrace();
		}
		
		try {
			mJoinOperationThreadManager.join();
		} catch (InterruptedException e) {
			mJoinOperationThreadManager.stop();
			e.printStackTrace();
		}
	}  
	
	public boolean isUniqueTabName(String name) {
		return indexOfTab(name) == -1;
	}
	
	public String makeSafeTabName(String name) {
		String uniqueName = new String(name);
		Integer i = 1;
		while(!isUniqueTabName(uniqueName)) {
			uniqueName = name + " (" + i.toString() + ")";
			i = i + 1;
		}
		return uniqueName;
	}
	
	public String[] makeSafeColumnIdentifers(String[] columnIdentifiers) {
		return columnIdentifiers;
	}
	
	public void removeTab(Tab removeTab) {
		//this.mSearchThreadManager.remove(removeTab);
		//this.mJoinOperationThreadManager.removeTab(removeTab);
		this.remove(removeTab);
		mDatabaseTabs.remove(removeTab);
	}
	
	public void addTab(String tabName, String[] columnIdentifiers, int[] primaryKeys, ArrayList<Object[]> rows) throws SQLException {
		
		Database.Table databaseTable = Database.getInstance().createTable(tabName, columnIdentifiers, new int[]{});
		
		String safeTableName = makeSafeTabName(tabName);
		String[] safeColumnIdentifers = makeSafeColumnIdentifers(columnIdentifiers);
		
		Tab tab = new Tab(safeTableName, safeColumnIdentifers);
		tab.setTable(databaseTable);
		tab.insertRows(rows);
		tab.updateModel();
		tab.setStatus(Tab.Status.AVALIABLE);
		
		Tab.ButtonTabComponent.TabButton tabButton = tab.getTabButton();
		TabCloseActionListener tabCloseActionListener = new TabCloseActionListener(tab);
		tabButton.addActionListener(tabCloseActionListener);
		
		addTab(tab);
		
	}
	
	public boolean isEmpty() {
		return this.getTabCount() == 0;
	}
	
	public boolean hasSelection() {
		return getSelection() != null;
	}
	
	public void addSearch(Search search) throws SQLException {
		
		SearchInterface searchInterface = search.getSearchInterface();
		String tableName = searchInterface.getTitle();
		String safeTableName = makeSafeTabName(tableName);
		String[] columnIdentifiers = searchInterface.getColumnIdentifers();
		String[] safeColumnIdentifiers = makeSafeColumnIdentifers(columnIdentifiers);
		int[] primaryKeys = searchInterface.getPrimaryKeys();
		Database.Table tabTable 
			= Database.getInstance().createTable(safeTableName, safeColumnIdentifiers, primaryKeys);
		
		Tab resultsTab = new Tab(safeTableName, safeColumnIdentifiers);
		resultsTab.setStatus(Tab.Status.SEARCHING);
		resultsTab.setTable(tabTable);
		addTab(resultsTab);
		SearchOperation seachOperation = new SearchOperation(resultsTab, search);
		mSearchThreadManager.addSearch(seachOperation);
	}
	
	public ArrayList<Tab> getTabs() {
    	return this.mDatabaseTabs;
    }
	
    public DataTable getActiveDataTable() {
    	int i = getSelectedIndex();
        System.out.println("Close tab, index of tab component: " + i);
        if (i != -1) {
            Tab scrollPane = getTabs()[i];
            JViewport viewport = scrollPane.getViewport();
            DataTable dataTable = (DataTable) viewport.getComponent(0);
            return dataTable;
        } else {
        	return null;
        }
    }
	
	public String[] getSelection() {
		
		int i = getSelectedIndex();
		Tab databaseTab = mDatabaseTabs.get(i);
		
		if(databaseTab != null) {
			return databaseTab.getSelected();
		}
		
		return null;
	}

    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();
        DataTable dataTable = getActiveDataTable();
        try {
			dataTable.updateTable();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
        JOptionPane.showMessageDialog(null, "Selected Index: " + selectedIndex);
    }
	
    private class JoinOperation
    {
		private String mTableName;
		
		private Tab mTabC;
		
		private Tab getTabC() {
			return mTabC;
		}
		
		private void setTabC(Tab tabC) {
			this.mTabC = tabC;
		}
		
		public String getTableName() {
			return mTableName;
		}

		private void setTableName(String mTableName) {
			this.mTableName = mTableName;
		}
		private Tab getTabA() {
			return mTabA;
		}
		private void setTabA(Tab mTabA) {
			this.mTabA = mTabA;
		}
		private int[] getSelectA() {
			return mSelectA;
		}
		private void setSelectA(int[] mTabASelect) {
			this.mSelectA = mTabASelect;
		}
		private int[] getKeyA() {
			return mKeyA;
		}
		private void setKeyA(int[] mTabAJoin) {
			this.mKeyA = mTabAJoin;
		}
		private Tab getTabB() {
			return mTabB;
		}
		private void setTabB(Tab mTabB) {
			this.mTabB = mTabB;
		}
		private int[] getSelectB() {
			return mSelectB;
		}
		private void setSelectB(int[] mTabBSelect) {
			this.mSelectB = mTabBSelect;
		}
		private int[] getKeyB() {
			return mKeyB;
		}
		private void setKeyB(int[] mTabBJoin) {
			this.mKeyB = mTabBJoin;
		}
		private void setJoinType(Database.Join.Type joinType) {
			mJoinType = joinType;
		}
		private Database.Join.Type getJoinType() {
			return mJoinType;
		}
		
		private Tab mTabA;
		private int[] mSelectA;
		private int[] mKeyA;
		private Tab mTabB;
		private int[] mSelectB;
		private int[] mKeyB;
		private Database.Join.Type mJoinType;
		
    	public JoinOperation
    	(
    			Tab tabA, int[] selectA,int[] keyA,
    			Tab tabB, int[] selectB, int[] keyB,
    			Database.Join.Type joinType,
    			Tab tabC)
    	{
    		setTabA(tabA);
    		setSelectA(selectA);
    		setKeyA(keyA);
    		setTabB(tabB);
    		setSelectB(selectB);
    		setKeyB(keyB);
    		setJoinType(joinType);
    		setTabC(tabC);
    	}
    	
    	
    }
    
    private class JoinOperationThreadManager extends Thread {
    	private CopyOnWriteArrayList<JoinOperation> mJoinOperations;
    	private final Object mWakeLock; 
    	private Boolean mWake; 
    	private Boolean mEndThread; 
    	
    	public JoinOperationThreadManager(){
    		mWake = false;
    		mWakeLock = new Object();
    		mEndThread = false; 
    		mJoinOperations = new CopyOnWriteArrayList<JoinOperation>();
    	}
    	
    	public void addJoinOperation(JoinOperation joinOperation) {
    		
    		mJoinOperations.add(joinOperation);
    		synchronized (mWakeLock) {
    			mWake = true; 
    			mWakeLock.notify();
    		}
    	}
    	
		public boolean finishing() {
			return mEndThread;
		}
    	
		public void end() {
			mJoinOperations.clear();
			this.mEndThread = true;
			synchronized (mWakeLock) {
				mWake = true; 
				mWakeLock.notify();
    		}
		}
			
		private void executeJoinOperations()
		{
			for(int i = 0; i < mJoinOperations.size(); i++) {
				JoinOperation joinOperation = mJoinOperations.get(i);
				Tab tabA = joinOperation.getTabA();
				Tab tabB = joinOperation.getTabB();
				if(tabA.hasDataAvaliable() && tabB.hasDataAvaliable()) {
					System.out.println("hasDataAvaliable()!");
					Tab tabC = joinOperation.getTabC();	
					
					Database.Table aTable = tabA.getDatabaseTable();
					int[] aSelect = joinOperation.getSelectA();
					int[] aKey = joinOperation.getKeyA();
					
					Database.Table bTable = tabB.getDatabaseTable();
					int[] bSelect = joinOperation.getSelectB();
					int[] bKey = joinOperation.getKeyB();
					
					Database.Join.Type joinType = joinOperation.getJoinType();
					
					Database.Join join = Database.getInstance().createJoin(aTable, aSelect, aKey, bTable, bSelect, bKey, joinType);
					
					Database.Table databaseTable = null;
					try {
						databaseTable = Database.getInstance().joinAndCreate(tabC.getName(), join);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					tabC.setTable(databaseTable);
					tabC.setStatus(Tab.Status.AVALIABLE);
					System.out.println("settingTable!");
				}
				
			}
			
		}
		
		private int indexOf(Tab findTab) {
			for(int i = 0; i < mJoinOperations.size(); i++) {
				JoinOperation check = mJoinOperations.get(i);
				Tab checkTab = check.getTabC();
				if(checkTab.equals(findTab)) {
					return i;
				}
			}
			return -1;
		}
		
		private void removeTab(Tab removeTab) {
			
			for(int i = 0; i < mJoinOperations.size(); i++) {
				JoinOperation joinOperation = mJoinOperations.get(i);
				Tab c = joinOperation.getTabC();
				if(c.equals(removeTab)) {
					Tab a = joinOperation.getTabA(); // depends
					removeTab(a);
					Tab b = joinOperation.getTabB(); // depends
					removeTab(b);
					mJoinOperations.remove(c);
				}	
				
			}
			synchronized (mWakeLock) {
				mWake = true; 
				mWake.notifyAll();
    		}
			
		}
		
		private void waitForLock() throws InterruptedException {
			synchronized (mWakeLock) {
				while(!mWake) {
					mWakeLock.wait();
				}
				mWake = false; 
				mWakeLock.notifyAll();
			}
		}
		
		public void wake() {
			synchronized (mWakeLock) {
				mWake = true; 
				mWakeLock.notifyAll();
			}
		}
		
    	public void run() {
    		
    		while(!finishing()) {
    			try {
    				waitForLock();
	    			executeJoinOperations();
	    			
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
    		}
    	}
    	
    }
    
    public Tab getTab(String tabName) {
    	for(Tab tab : this.mDatabaseTabs) {
    		if(tab.getName().equals(tabName))
    			return tab;
    	}
    	return null;
    }
    
    public class TabCloseActionListener implements ActionListener {
    	
    	Tab mTab; 
    	
    	TabCloseActionListener(Tab tab){
    		mTab = tab;
    	}
    	
		@Override
		public void actionPerformed(ActionEvent arg0) {
			removeTab(mTab);
		}
    	
    }
    
    static public class Tab extends JScrollPane {
    	
    	public enum Status {
    		AVALIABLE,
    		WAITING,
    		SEARCHING,
    		SEARCH_COMPLETE
    	};
    	
    	private String mName; 
    	private Status mStatus;
    	private JTable mTable;
    	private Database.Table mDatabaseTable;
    	private ButtonTabComponent mHeader; 
    	private TableMouseListener mTableMouseListener;
    	private boolean mDatabaseChanged;

    	private Database.Table getDatabaseTable() {
    		return mDatabaseTable;
    	}
    	
    	public boolean hasDatabase() {
    		return mDatabaseTable != null;
    	}
    	
    	private void setDatabaseTable(Database.Table databaseTable) {
    		mDatabaseTable = databaseTable;
    		mDatabaseChanged = true;
    		updateModel();
    	}
    	
    	public boolean hasDataAvaliable() { 
    		return mStatus.equals(Status.AVALIABLE) || mStatus.equals(Status.SEARCH_COMPLETE);
    	}
    	
    	public void insertRows(ArrayList<Object[]> rows) throws SQLException {
    		System.out.println("insertRows():" + rows.get(0).length + ":" + this.getColumnIdentifers().length);
    		mDatabaseTable.insertRows(rows);
    		mDatabaseChanged = true;
    		// updateModel();
    	}
    	
    	public void updateModel() {

    		if(mDatabaseTable == null)
    			return;
    		
    		if(mDatabaseChanged == false)
    			return;
    		
    		ArrayList<Object[]> tableResults = null;
    		try {
				tableResults = mDatabaseTable.getTable();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		
    		
    		
    		DefaultTableModel dtm = new DefaultTableModel();
    		dtm.setColumnCount(0);
    		dtm.setRowCount(0);
    		
    		String[] columnIdentifiers = getColumnIdentifers();
    		dtm.setColumnIdentifiers(columnIdentifiers);
    		
    		if(!tableResults.isEmpty()) {
    			System.out.println("updateModel():" + tableResults.get(0).length + ":" + this.getColumnIdentifers().length);
	    		for(Object[] row : tableResults) {
	    			dtm.addRow(row);
	    		}
    		}
    		
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	mTable.setModel(dtm);
                }
            });
    		mDatabaseChanged = false;
    		
    	}
    	
    	private Tab(String name, String[] columnIdentifiers){
    		
    		mName = name; 
    		
    		mHeader = new ButtonTabComponent(this);
    		
    		mTableMouseListener = new TableMouseListener();
    		DefaultTableModel dtm = new DefaultTableModel();
    		dtm.setColumnCount(0);
    		dtm.setRowCount(0);
    		dtm.setColumnIdentifiers(columnIdentifiers);
    		
    		
    		mTable = new JTable();
    		mTable.setModel(dtm);
    		mTable.setCellSelectionEnabled(true);
    		mTable.setRowSelectionAllowed(true);
    		mTable.setColumnSelectionAllowed(true);
    		mTable.setAutoCreateRowSorter(true);
    		mTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    		mTable.getTableHeader().addMouseListener(mTableMouseListener);
    		
    		mTable.addMouseListener(new MouseListener() {

				@Override
				public void mousePressed(MouseEvent e) {
    		        int row = mTable.rowAtPoint(e.getPoint());
    		        int col = mTable.columnAtPoint(e.getPoint());
    		        System.out.println("mTable.addMouseListener");
    		        //System.out.println("mTable.setRowSelectionAllowed(true);");
    		        	//mTable.clearSelection();
    	    	    mTable.setRowSelectionAllowed(true);
    	    		mTable.setColumnSelectionAllowed(true);
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

    		});
    		
    		
    		
    		sortKeys = new ArrayList<>();
    		
    		getViewport().add(mTable);
    		
    	}
    	
    	private JScrollPane getBody() {
    		return this; 
    	}
    	
    	private void setStatus(Status status) {
    		if(status.equals(Status.AVALIABLE) || status.equals(Status.SEARCH_COMPLETE)) {
    			mHeader.disableLoadingIcon();
    		} else {
    			mHeader.enableLoadingIcon();
    		}
    		this.mStatus = status; 
    	}
    	
    	private int indexOf(String find, String[] array) {
			for(int i = 0; i < array.length; i++) {
				String check = array[i];
				if(find.equals(check)) {
					return i;
				}
			}
			return -1;
    	}
    	

    	
    	public int[] getColumnSelect(String[] findColumnsIdentifiers) {
    		int len = findColumnsIdentifiers.length;
    		int[] foundColumnIdentifiersIndicies = new int[len];
    		String[] columnsIdentifiers = getColumnIdentifers();
    		for(int i = 0; i < findColumnsIdentifiers.length; i++) {
    			String findColumnIdentifer = findColumnsIdentifiers[i];
    			int indexColumnIdentifer = indexOf(findColumnIdentifer, columnsIdentifiers);
    			foundColumnIdentifiersIndicies[i] = indexColumnIdentifer; 
    		}
    		return foundColumnIdentifiersIndicies;
    	}
    	
    	ButtonTabComponent getHeader() {
    		return mHeader; 
    	}
    	
    	ButtonTabComponent.TabButton getTabButton() {
    		return getHeader().getTabButton();
    	}
    	
    	public Status getStatus()		{ 	return mStatus;		}
    	public String getName()			{ 	return mName; 		}
    	public String getToolTip() 		{ 	return null;		}
    	public Icon getIcon() 			{	return null;		}
    	
    	private class TableMouseListener implements MouseListener {
    		
    		private class ColumnSort {
    			int mColumnIndex; 
    			SortOrder mSortOrder;
    			
    			int getColumnIndex() { return mColumnIndex; }
    			SortOrder getSortOrder() { return mSortOrder; }
    			
    			public ColumnSort(int columnIndex, SortOrder sortOrder){
    				mColumnIndex = columnIndex;
    				mSortOrder = sortOrder;
    			}
    		};
    		
    		private boolean isSelectingColumns() {
    			return true;
    		}
    		
    		
    		
    		public void selectColumn(MouseEvent e) {
                JTableHeader h = (JTableHeader) e.getSource();
                int i = h.columnAtPoint(e.getPoint());
                
                if(isSelectingColumns()) {
                	mTable.setColumnSelectionAllowed(true);
    				mTable.setRowSelectionAllowed(false);
    				//mTable.getTableHeader().setReorderingAllowed(false);
                	// int[] selectedCols = mTable.getSelectedColumns();
    				
                	if(e.isControlDown()) {
                		int[] selectedColumns = mTable.getSelectedColumns();
                		boolean containsColumn = IntStream.of(selectedColumns).anyMatch(x -> x == i);
                		
                		
                		if(containsColumn) {
                			System.out.println("Contains Column!");
                			mTable.clearSelection();
                			
                		
                			int[] columnsToSelect = new int[selectedColumns.length-1];
                			int jColumnsToSelect = 0;
                			int jSelectedColumns = 0;
                			
                			for(; jSelectedColumns < selectedColumns.length; jSelectedColumns++) {
                				if(selectedColumns[jSelectedColumns] != i) {
                					columnsToSelect[jColumnsToSelect] = selectedColumns[jSelectedColumns];
                					jColumnsToSelect++;
                				}
                			}
                			
                			for(int j = 0; j < columnsToSelect.length; j++) {
                				int k = columnsToSelect[j];
                				mTable.addColumnSelectionInterval(k, k);
                			}
                			
                		} else {
                			System.out.println("Does Not Contain The Column!");
                			mTable.addColumnSelectionInterval(i,i);	
                		}
                		
            			System.out.println("addColumnSelectionInterval");
            			
                	} else {
            			System.out.println("Does Not Contain The Column!");
            			mTable.setColumnSelectionInterval(i, i);
            		}
                	
                }
                
    		}
    		
    		
            @Override
            public void mouseClicked(MouseEvent e) {
            	System.out.println("mouseClicked(MouseEvent e)");
            	
            	
            	///
                JTableHeader h = (JTableHeader) e.getSource();
                int i = h.columnAtPoint(e.getPoint());
                if (i < 0) {
                    return;
                }
                Object o = h.getColumnModel().getColumn(i).getHeaderValue();
                Object selectedColumn = o;
                ///
                
            	
            	/*
                
                final JPopupMenu popup = new JPopupMenu();
                
                JMenuItem menuItem = new JMenuItem("Sort by");
                menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Sort By!");
						TableRowSorter<TableModel> sorter = new TableRowSorter<>(mTable.getModel());
						mTable.setRowSorter(sorter);
						sortKeys = new ArrayList<>();
						sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
						sorter.setSortKeys(sortKeys);
						sorter.sort();
						
					}
                	
                });
                
                menuItem.addMouseListener(new MouseAdapter() {
                	 
                    @Override
                    public void mousePressed(MouseEvent e) {
                        showPopup(e);
                    }
         
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        showPopup(e);
                    }
         
                    private void showPopup(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                        	if(e.getButton() == MouseEvent.BUTTON1)
                        		popup.show(e.getComponent(),
                        					e.getX(), e.getY());
                        }
                    }
                }
                );
                
                popup.add(menuItem);
                menuItem = new JMenuItem("Order by");
                menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Order by!");
						TableRowSorter<TableModel> sorter = new TableRowSorter<>(mTable.getModel());
					
						mTable.setRowSorter(sorter);
						sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
						sorter.setSortKeys(sortKeys);
						sorter.sort();
					}
                	
                });
                popup.add(menuItem);
                popup.show(h, e.getPoint().x, e.getPoint().y);
                
                System.out.println(selectedColumn);
                */
            }

			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("mouseEntered(MouseEvent e)");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("mouseExited(MouseEvent e)");
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("mousePressed(MouseEvent e)");
				if(SwingUtilities.isLeftMouseButton(e)) {
            		System.out.println("mouseClicked(MouseEvent e):Button 1");
            		selectColumn(e);
            	}else if(SwingUtilities.isMiddleMouseButton(e)) {
            		System.out.println("mouseClicked(MouseEvent e):Button 2");
            	}else if(SwingUtilities.isRightMouseButton(e)) {
            		System.out.println("mouseClicked(MouseEvent e):Button 3");
            	}
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
            
        }
    	
    	private List<RowSorter.SortKey> sortKeys;
    	
    	public String[] getColumnIdentifers() {
    		TableColumnModel tcm = mTable.getTableHeader().getColumnModel();
    		String[] columnIdentifers = new String[tcm.getColumnCount()];
    		for(int i = 0; i < tcm.getColumnCount(); i++) {
    			columnIdentifers[i] = tcm.getColumn(i).getHeaderValue().toString();
    		}
    		return columnIdentifers;
    	}
    	
    	public String[] getSelected() {
    		
    		int[] rows = mTable.getSelectedRows();
    		int[] cols = mTable.getSelectedColumns();
    		
    		Set<String> tree_set = new TreeSet<String>(); 
    		for(int i = 0; i < rows.length; i++) {
    			for(int j = 0; j < cols.length; j++) {
    				int k = rows[i];
    				String cell = (String)mTable.getValueAt(k, cols[j]);
    				tree_set.add(cell);
    			}
    		}
    		
    		int len = tree_set.size();
    		return tree_set.toArray(new String[len]);
    	}
    	
    	public String[] getUniqueSelected() {
    		
    		int[] rows = mTable.getSelectedRows();
    		int[] cols = mTable.getSelectedColumns();
    		
    		LinkedList<String> unique = new LinkedList<String>(); 
    		Object last_added = null;
    		for(int i = 0; i < rows.length; i++) {
    			int r = rows[i];
    			for(int j = 0; j < cols.length; j++) {
    				int c = cols[j];
    				int vr = mTable.convertRowIndexToModel(r);
    				int vc = mTable.convertColumnIndexToModel(c);
    				
    				String cell;
    				if(mTable.getModel().getValueAt(vr, vc) != null) {
    					cell = mTable.getModel().getValueAt(vr, vc).toString();
    				}else {
    					break;
    				}
    				
    				if(!cell.equals(last_added) && !unique.contains(cell)) {
    					unique.add(cell);
    					last_added = cell;
    				}
    					
    			}
    		}
    		int len = unique.size();
    		return unique.toArray(new String[len]);
    		
    	}
    	
    	public boolean isEmpty() {
    		return !(mTable.getModel().getRowCount() > 0);
    	} 
    	
    	public boolean hasSelection() {
    		return mTable.getSelectedRowCount() > 0;
    	}
    	
    	public boolean isCellEditable(int row, int column) {                
            return false;               
        }
    	
    	public void setTable(Database.Table set) {
    		this.mDatabaseTable = set;
    		this.mDatabaseChanged = true;
    		updateModel();
    	}
    	
    	public class ButtonTabComponent extends JPanel {

    	    static private final String TAB_ICON_LABEL_NAME = "TAB_HEADER_LABEL";
    	    
    	    Tab mTab;
    	    JLabel mLabel;
    	    TabButton mTabButton; 
    	    
    	    TabButton getTabButton() {
    	    	return mTabButton;
    	    }
    	    
    	    public void enableLoadingIcon() {
    	        ImageIcon loading = new ImageIcon("C:\\Users\\Richard\\Documents\\GitHub\\RONE\\icons\\ajax-loader.gif");
    	        Image image = loading.getImage(); // transform it 
    	        Image newimg = image.getScaledInstance(15, 15,  java.awt.Image.SCALE_DEFAULT); // scale it the smooth way  
    	        loading = new ImageIcon(newimg);
    	        mLabel.setIcon(loading);
    	    }
    	    
    	    public void disableLoadingIcon() {
    	    	mLabel.setIcon(null);
    	    }
    	    
    	    public ButtonTabComponent(Tab tab) {
    	        //unset default FlowLayout' gaps
    	        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

    	        mTab = tab;
    	        
    	        setOpaque(false);
    	        
    	        mLabel = new JLabel(mTab.getName());
    	        
    	        add(mLabel);
    	        //add more space between the label and the button
    	        mLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    	        
    	        
    	        //tab button
    	        mTabButton = new TabButton(mTab);
    	        //button.addActionListener(actionListenerTabClose);
    	        add(mTabButton);
    	        //add more space to the top of the component
    	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    	        
    	        
    	    }

    	    private class TabButton extends JButton {
    	    	
    	    	Tab mTab; 
    	    	
    	        public TabButton(Tab tab) {
    	        	mTab = tab;
    	            int size = 17;
    	            setPreferredSize(new Dimension(size, size));
    	            setToolTipText("close this tab");
    	            //Make the button looks the same for all Laf's
    	            setUI(new BasicButtonUI());
    	            //Make it transparent
    	            setContentAreaFilled(false);
    	            //No need to be focusable
    	            setFocusable(false);
    	            setBorder(BorderFactory.createEtchedBorder());
    	            setBorderPainted(false);
    	            //Making nice roll over effect
    	            //we use the same listener for all buttons
    	            addMouseListener(buttonMouseListener);
    	            setRolloverEnabled(true);
    	        }
    	      
    	        //paint the cross
    	        protected void paintComponent(Graphics g) {
    	            super.paintComponent(g);
    	            Graphics2D g2 = (Graphics2D) g.create();
    	            //shift the image for pressed buttons
    	            if (getModel().isPressed()) {
    	                g2.translate(1, 1);
    	            }
    	            g2.setStroke(new BasicStroke(2));
    	            g2.setColor(Color.BLACK);
    	            if (getModel().isRollover()) {
    	                g2.setColor(Color.MAGENTA);
    	            }
    	            int delta = 6;
    	            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
    	            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
    	            g2.dispose();
    	        }
    	    }

    	    private final MouseListener buttonMouseListener = new MouseAdapter() {
    	        public void mouseEntered(MouseEvent e) {
    	            Component component = e.getComponent();
    	            if (component instanceof AbstractButton) {
    	                AbstractButton button = (AbstractButton) component;
    	                button.setBorderPainted(true);
    	            }
    	        }

    	        public void mouseExited(MouseEvent e) {
    	            Component component = e.getComponent();
    	            if (component instanceof AbstractButton) {
    	                AbstractButton button = (AbstractButton) component;
    	                button.setBorderPainted(false);
    	            }
    	        }
    	    };
    	}
    	
    	public boolean hasDatabaseTable() {
    		return this.mDatabaseTable != null;
    	}
    	
    	@Override
        public boolean equals(Object o) { 
    		
            // If the object is compared with itself then return true   
            if (o == this) { 
                return true; 
            } 
      
            if(o == null) {
            	return false;
            }
            
            /* Check if o is an instance of Complex or not 
              "null instanceof [type]" also returns false */
            if (!(o instanceof Tab)) { 
                return false; 
            } 
              
            // typecast o to Complex so that we can compare data members  
            Tab t = (Tab) o; 
              
            // Compare the data members and return accordingly 

	        
            if(this.hasDatabaseTable() && t.hasDatabaseTable()) {
            	
    	        System.out.println("----");
    	        System.out.println(this.getDatabaseTable().getName());
    	        System.out.println(t.getDatabaseTable().getName());
    	        System.out.println(this.getDatabaseTable());
    	        System.out.println(t.getDatabaseTable());
    	        System.out.println("----");
            	
            	return this.getDatabaseTable().equals(t.getDatabaseTable());
            }
            
            
            return false;
        } 
    } 
    
    private String[] getSelectedColumns(String[] columns, int[] columnSelect) {
		int len = columnSelect.length;
		String[] selected = new String[len];
		for(int i = 0; i < len; i++) {
			int idx = columnSelect[i];
			selected[i] = columns[idx];
		}
		return selected;
	}
    
	private String[] createColumns(Tab a, int[] selectA, int[] keyA, Tab b, int[] selectB, int[] keyB) {
		
		String[] aIdentifers = a.getColumnIdentifers();
		String[] bIdentifers = b.getColumnIdentifers();
		
		String[] aSelectedColumns = getSelectedColumns(aIdentifers, selectA);
		String[] bSelectedColumns = getSelectedColumns(bIdentifers, selectB);
		
		int aLen = aSelectedColumns.length;
		int bLen = bSelectedColumns.length;
		
		int aKeyLen = keyA.length;
		int bKeyLen = keyB.length;
		
		assert(aKeyLen == bKeyLen);
		
		int outputLength = aLen + bLen;
		
		String[] result = new String[outputLength];
		
		/*for(int i = 0; i < aKeyLen; i++) {
			int idx = keyA[i];
			result[i] = aSelectedColumns[idx];
		}*/
		
		System.arraycopy(aSelectedColumns, 0, result, 0, aLen);
		System.arraycopy(bSelectedColumns, 0, result, aLen, bLen);
		
		return result;
	}
    
	public void addTab(Tab tab) {
		mDatabaseTabs.add(tab);
		
		addTab(tab.getName(), tab.getBody());
		int index = indexOfTab(tab.getName());
		setTabComponentAt(index, tab.getHeader());

	}
	
    public void addJoinOperations(
    		String tableName, 
			Tab tabA, int[] selectA, int[] keyA,
			Tab tabB, int[] selectB, int[] keyB,
			Database.Join.Type joinType){
    	
		String safeTableName = makeSafeTabName(tableName);
		String[] columnIdentifers = createColumns(tabA, selectA, keyA, tabB, selectB, keyB);
		String[] safeColumnIdentifers = makeSafeColumnIdentifers(columnIdentifers);
		
		Tab tabC = new Tab(safeTableName, safeColumnIdentifers);
		tabC.setStatus(Tab.Status.WAITING);
		
		Tab.ButtonTabComponent.TabButton tabButton = tabC.getTabButton();
		TabCloseActionListener tabCloseActionListener = new TabCloseActionListener(tabC);
		tabButton.addActionListener(tabCloseActionListener);
		addTab(tabC);
		
		JoinOperation joinOperation 
			= new JoinOperation(tabA, selectA, keyA, tabB, selectB, keyB, joinType, tabC);    	
		
		mJoinOperationThreadManager.addJoinOperation(joinOperation);
    }
    
    private class SearchOperation {
    	
    	Tab mResultsTab; 
    	Search mSearch;
    	
    	public SearchOperation(Tab resultsTab, Search search){
    		this.mResultsTab = resultsTab;
    		this.mSearch = search; 
    	}
    	
    	public Tab getResultsTab() {
    		return this.mResultsTab;
    	}
    	
    	public Search getSearch() {
    		return this.mSearch;
    	}
    } 
    
    private class SearchThreadManager extends Thread {
		
		private CopyOnWriteArrayList<SearchOperation> mSearchQueue; 
		private CopyOnWriteArrayList<MasterThread> mActiveQueue; 
		
		private boolean mEnd;
		
		SearchThreadManager(){
			mEnd = false;
			mSearchQueue = new CopyOnWriteArrayList<SearchOperation>();
			mActiveQueue = new CopyOnWriteArrayList<MasterThread>();
		}
		
		public void end() {
			mSearchQueue.clear();
			mEnd = true;
		}
		
		public void addSearch(SearchOperation search) {
			synchronized(mSearchQueue) {
				mSearchQueue.add(search);
				mSearchQueue.notifyAll();
			}
		}
		
		public boolean ending() {
			return mEnd;
		}
		
		private boolean hasInActiveSearches() 	{ 
			return mSearchQueue.size() > 0;
		}
		
		private boolean hasActiveSearches() 	{ 
			return mActiveQueue.size() > 0;
		}
		
		private boolean hasSearches() {
			return hasInActiveSearches() || hasActiveSearches();
		}
		
		public void startInActiveSearches() throws InterruptedException {
			while(hasInActiveSearches()) {
				synchronized(mSearchQueue) {
					while(!mSearchQueue.isEmpty()) {
						SearchOperation search = mSearchQueue.remove(0);
						
						MasterThread startThread = new MasterThread(search);
						startThread.start();
						mActiveQueue.add(startThread);						
					}
					mSearchQueue.notifyAll();
				}
			}
		}
		
		public void endThreads() {
			for(MasterThread mt : mActiveQueue) {
				mt.stopProcess();
			}
		}
		
		public void run() 
	    { 
			this.mEnd = false;
			while(!ending()) {
				try {
					synchronized (this.mSearchQueue) {
						while(mSearchQueue.isEmpty()) {
							//System.out.println("Waiting for search query!");
							mSearchQueue.wait();
						}
						
						//while(hasSearches()) {
							joinCompleteSearches();
							startInActiveSearches();
						//}
						mSearchQueue.notifyAll();
					}
					
					
				} catch(InterruptedException e) {  
					//System.out.println("SearchThreadManager:"  + this.getName() + ": Interupted, Finishing = " + Boolean.toString(finishing()) + ".");
				}
			}
			
			try {
				cleanUp();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
		
		private boolean threadEnded(MasterThread mt) {
			return mt.processSuccess() || mt.hasToStopWorking();
		}
		
		private void cleanUp() throws InterruptedException {

			endThreads();
			
			//System.out.println("SearchThreadManager:cleaning up!");
			synchronized(mSearchQueue) {
				while(!mActiveQueue.isEmpty()) {
					MasterThread mt = mActiveQueue.remove(0);
					try {
						mt.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mSearchQueue.notifyAll();
			}
		}
		
		private void joinCompleteSearches() throws InterruptedException {
			//while(hasActiveSearches()) {
				////System.out.println("SearchThreadManager: Trying to join threads!" + Boolean.toString(hasInActiveSearches()));
				for(int i = 0; i < mActiveQueue.size(); ) {
					MasterThread mt = mActiveQueue.get(i);
					if(threadEnded(mt)) {
						mt.join();
						mActiveQueue.remove(i);
						//System.out.println("SearchThreadManager: Successfully joined thread!" + mt.getId());
					} else {
						i++;
					}
					//TODO: Fix this later, should be no thread.sleep - wait for array to update
				}
				//System.out.println("SearchThreadManager:joinCompleteSearches(): done!");
			}
			//}
		
	};

	private class MasterThread extends Thread {
    	private int mThreadPoolSize = 5;
    	private int mWorkSize = 20;
    	
    	private WorkerThread mThreadPool[];
    	
    	private Boolean mProcessSuccess;
    	private boolean mStopWorking;
    	private int mWorkHeadIndex;
    	private Search mSearch;
    	
    	private SearchOperation mSearchOperation;
    	private SearchInterface mSearchInterface;
    	private String[] mSearchInterfaceRequests;
    	
    	private int mRequestsSearched;
    	
    	private Tab mResultsTab; 
    	private Database.Table mResultsTable; 
    	
    	public MasterThread(SearchOperation searchOperation) {
    		mSearchOperation = searchOperation;
    		mSearch = searchOperation.getSearch();
    		mSearchInterface = mSearch.getSearchInterface();
    		mSearchInterfaceRequests = mSearch.getSearchInterfaceRequests();
    		mResultsTab = searchOperation.getResultsTab();
    		
    		mThreadPoolSize = mSearchInterface.getThreadPoolSize();
    		mWorkSize = mSearchInterface.getWorkSize();
    		
    		mRequestsSearched = 0;
    		
    		mResultsTable = null;
    		
    		//System.out.println("MasterThread():" + this.getName());
    		
    		setProcessSuccess(false);
    	}
    	
    	public void stopProcess() {
    		//System.out.println("MasterThread:stopProcess()");
    		mStopWorking = true;
    	}
    	
    	private int nextIndex(int i) {
        	return ((i+mWorkSize > mSearchInterfaceRequests.length) ? mSearchInterfaceRequests.length : i+mWorkSize);
        }
    	
    	public String[] getNextWork() {
    		if(mWorkHeadIndex < mSearchInterfaceRequests.length) {
    			int mNewWorkHead = nextIndex(mWorkHeadIndex);
    			String[] slice = Arrays.copyOfRange(mSearchInterfaceRequests, mWorkHeadIndex, mNewWorkHead);
    			mWorkHeadIndex = mNewWorkHead;
    			return slice;
    		}else{
    			return new String[] {};
    		}
    	}
    	
    	private boolean hasResults(ArrayList<Object[]> results) {
    		return (results != null) && results.size() > 0;
    	}
    	
    	private int totalCompletedWork(String[] work) {
    		return work == null ? 0 : work.length; 
    	}
    	
    	
    	public void processResults(String[] completeWork, ArrayList<Object[]> results) {
    		
    		if(!hasResults(results)) 
    			return;
    		
    		try {
    			mResultsTab.insertRows(results);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		
    		if(mResultsTab.isVisible()) {
    			mResultsTab.updateModel();
    		}
    		
    		mRequestsSearched = mRequestsSearched + totalCompletedWork(completeWork); 
    		
    		
    	}
    	
    	
    	public boolean hasWork(String[] work) {
    		return work.length > 0;
    	}
    	
    	private WorkerThread[] makeThreadPool(int size, int priority) {
    		WorkerThread[] threadPool = new WorkerThread[size];
    		for(int i = 0; i < mThreadPoolSize; i++) {
    			threadPool[i] = new WorkerThread(mSearchInterface);
    			threadPool[i].setPriority(priority);
    		}
    		return threadPool;
    	}
    	
    	public void giveWork(String[] work) {
			int i = 0;
			boolean givenWork = false;
			while(!givenWork) {
				if(hasToStopWorking()) 
					return;
				
				if(!mThreadPool[i].isAlive()) {
					try {
						mThreadPool[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ArrayList<Object[]> results = mThreadPool[i].getResults();
					String completeWork[] = mThreadPool[i].getWork();
					
					//TODO: check if i don't need a new worker thread
					mThreadPool[i] = new WorkerThread(mSearchInterface);
        			mThreadPool[i].setWork(work);
        			mThreadPool[i].start();
        			
        			
					processResults(completeWork, results);
    				
    				givenWork = true;
    			}
				i = (i + 1) % mThreadPoolSize;
				
			}
    	}
    	
    	private void cleanUp() {
    		//System.out.println("MasterThread:cleanUp()");
    		boolean doneCleanUp = false;
    		while(!doneCleanUp) {
    			doneCleanUp = true;
	    		for(int i = 0; i < mThreadPool.length; i++) {
	    			WorkerThread worker = mThreadPool[i];
	    			try {
	    				if(worker.isAlive()) {
	    					doneCleanUp = false;
	    					worker.join();
	    					//System.out.println("MasterThread: successfully joined worker thread -> " + worker.getId());
	    				}
	    				
	    				if(!hasToStopWorking() && !worker.hasGivenResults()) {
	    					ArrayList<Object[]> results = worker.getResults();
	    					String[] completeWork = worker.getWork();
	    					processResults(completeWork, results);
	    				}
	    				
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    		//System.out.println("MasterThread: cleanUp() success!");
    		}
    		mThreadPool = null;
    		mWorkHeadIndex = 0;
    	}
    	
    	private boolean hasToStopWorking() {
    		return mStopWorking;
    	}
    	
    	public Boolean processSuccess() {
    		return mProcessSuccess;
    	}
    	
    	private void setProcessSuccess(Boolean set) {
    		mProcessSuccess = set;
    		
    	}
    	
    	public void setUp() {
    		//System.out.println("MasterThread:Run():" + this.getName());
    		String tableName = mSearchInterface.getTitle();
    		//System.out.println("MasterThread:tableName():" + this.getName());
    		String[] columnIdentifiers = mSearchInterface.getColumnIdentifers();
    		//System.out.println("MasterThread:columnIdentifiers():" + this.getName());
    		int[] primaryKeys = mSearchInterface.getPrimaryKeys();
    		//System.out.println("MasterThread:primaryKeys():" + this.getName());
    		
    		mResultsTable = null;
			try {
				mResultsTable = Database.getInstance().createTable(tableName, columnIdentifiers, primaryKeys);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("MasterThread:Run():" + this.getName() + ":Database created!");
    		ActionListener actionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					stopProcess();
				}
    			
    		};
    		
    		//System.out.println("MasterThread: run():" + this.getName() + ": Imported Database Table!");
    		mThreadPool = makeThreadPool(mThreadPoolSize, MAX_PRIORITY);
    		//System.out.println("MasterThread: run():" + this.getName() + ": Made thread pool!");
    	}
    	
    	public void run() {
    		
    		setUp();
    		
    		setProcessSuccess(false);
    		String[] work = null;
    		//System.out.println("MasterThread:run():" + this.getName() + ":Allocating work.");
    		while(hasWork(work = getNextWork())) {
    			if(hasToStopWorking()) {
    				//System.out.println("Ending Process!");
    				break;
    			}
    			giveWork(work);
			}
	    		
    		cleanUp();
    		
    		if(!hasToStopWorking()) {
    			//System.out.println("MasterThread:run():" + this.getName() + ":was stopped from searching.");
    			mResultsTab.setStatus(Tab.Status.SEARCH_COMPLETE);
    			mResultsTab.updateModel();
    			setProcessSuccess(true);
    		}
    		//System.out.println("MasterThread:run():" + this.getName() + ":process complete!");
    	}

    
    }
	
    private class WorkerThread extends Thread 
	{ 
    	private ArrayList<Object[]> mResults;
    	private String[] mSearchInterfaceRequests;
    	private boolean mHasGivenResults;
    	private boolean mStopProcessing;
    	
    	private SearchInterface mSearchInterface;
    
    	WorkerThread(SearchInterface searchInterface){
    		//System.out.println("WorkerThread():" + this.getName());
    		mSearchInterface = searchInterface;
    		mResults = new ArrayList<Object[]>();
    	}
    	
    	public void setWork(String[] work) {
    		this.mSearchInterfaceRequests = work;
    	}
    	
    	public ArrayList<Object[]> getResults(){
    		setHasGivenResults(true);
    		return mResults;
    	}

    	public void setHasGivenResults(boolean set) {
    		mHasGivenResults = set;
    	}
    	
    	public void setResults(ArrayList<Object[]> results) {
    		mResults = results;
    	}
    	
	    public void run() 
	    {
	    	//System.out.println("WorkerThread:run()" + this.getName());
    		ArrayList<Object[]> results = mSearchInterface.query(mSearchInterfaceRequests);
    		//System.out.println("WorkerThread:run()" + this.getName() +": query complete!");
    		
    		setResults(results);
    		setHasGivenResults(false);
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}

		public String[] getWork() {
			return this.mSearchInterfaceRequests;
		} 
	
	} 
  
}
