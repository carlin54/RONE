package rone.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rone.filemanager.Database;
import rone.filemanager.FileManager;
import rone.plugins.Selection;



public class DatabaseTabbedPane extends JTabbedPane {
	
	private static final long serialVersionUID = 845770918437336262L;
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
	
	@SuppressWarnings("deprecation")
	protected void finalize ()
	{  
		mSearchThreadManager.end();
		mJoinOperationThreadManager.end();

		try {
			mSearchThreadManager.join();
		} catch (InterruptedException e) {
			mSearchThreadManager.stop();
			MainWindow.showError(e);
		}
		
		try {
			mJoinOperationThreadManager.join();
		} catch (InterruptedException e) {
			mJoinOperationThreadManager.stop();
			MainWindow.showError(e);
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
		mJoinOperationThreadManager.removeTab(removeTab);
		mSearchThreadManager.removeTab(removeTab);
		remove(removeTab);
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
		Tab tab = this.getActiveTab();
		return tab != null ? tab.hasSelection() : false;
	}
	
	public boolean hasActiveTab() {
		return this.getActiveTab() != null;
	}
	
	public String[] getSelectedColumns() {
		
		if(this.hasActiveTab()) {
			return this.getActiveTab().getSelectedColumnIdentifers();
		}else {
			return null;
		}
		
	}
	
	public ArrayList<Object[]> getSelectedRows(){
		if(this.hasActiveTab()) {
			return this.getActiveTab().getSelectedRows();
		}else {
			return null;
		}
	}
	
	public Selection getSelection() {
		return new Selection(this.getSelectedColumns(), this.getSelectedRows());
	}
	
	public void addSearch(rone.plugins.Search search) throws SQLException {
		
		String tableName = search.getTitle();
		String safeTableName = makeSafeTabName(tableName);
		String[] columnIdentifiers = search.getColumnIdentifers();
		String[] safeColumnIdentifiers = makeSafeColumnIdentifers(columnIdentifiers);
		
		Database.Table tabTable 
			= Database.getInstance().createTable(safeTableName, safeColumnIdentifiers, new int[] {});
		
		Tab resultsTab = new Tab(safeTableName, safeColumnIdentifiers);
		resultsTab.setStatus(Tab.Status.SEARCHING);
		resultsTab.setTable(tabTable);
		addTab(resultsTab);
		
		
		Tab.ButtonTabComponent.TabButton tabButton = resultsTab.getTabButton();
		TabCloseActionListener tabCloseActionListener = new TabCloseActionListener(resultsTab);
		tabButton.addActionListener(tabCloseActionListener);
		
		SearchOperation seachOperation = new SearchOperation(search, resultsTab);
		mSearchThreadManager.addSearch(seachOperation);
	}
	
	public ArrayList<Tab> getTabs() {
    	return this.mDatabaseTabs;
    }
	
    private class JoinOperation
    {
		
		private Tab mTabC;
		
		private Tab getTabC() {
			return mTabC;
		}
		
		private void setTabC(Tab tabC) {
			this.mTabC = tabC;
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
			for(JoinOperation joinOperation : mJoinOperations) {
				Tab tabA = joinOperation.getTabA();
				Tab tabB = joinOperation.getTabB();
				if(tabA.hasDataAvaliable() && tabB.hasDataAvaliable()) {
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
						MainWindow.showError(e);
					}
					
					tabC.setTable(databaseTable);
					tabC.setStatus(Tab.Status.AVALIABLE);
					
					mJoinOperations.remove(joinOperation);
				}
				
			}
			
		}
		
		private void removeTab(Tab removeTab) {
			for(int i = 0; i < mJoinOperations.size(); i++) {
				JoinOperation joinOperation = mJoinOperations.get(i);
				Tab a = joinOperation.getTabA(); // depends
				Tab b = joinOperation.getTabB(); // depends
				Tab c = joinOperation.getTabC();
				
				// produces
				if(a.equals(removeTab) || b.equals(removeTab)) {
					mJoinOperations.remove(joinOperation);
					DatabaseTabbedPane.this.removeTab(c);
				}
				
				// is product
				if(c.equals(removeTab)){
					mJoinOperations.remove(joinOperation);
					DatabaseTabbedPane.this.removeTab(c);
				}
				
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
		
    	public void run() {
    		
    		while(!finishing()) {
    			try {
    				waitForLock();
	    			executeJoinOperations();
	    			
	    		} catch (InterruptedException e) {
	    			MainWindow.showError(e);
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
    
	public boolean hasTab() {
		return !mDatabaseTabs.isEmpty();
	}
    
    public Tab getActiveTab() {
    	int i = this.getSelectedIndex();
    	if(i < 0 || i > this.mDatabaseTabs.size()-1)
    		return null;
    	
    	return this.mDatabaseTabs.get(i);
    }
    
    static public class Tab extends JScrollPane {
    	
		private static final long serialVersionUID = -7182226718587775509L;

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
    	private ArrayList<Object[]> mLoadedTable; 
    	
    	private Database.Table getDatabaseTable() {
    		return mDatabaseTable;
    	}
    	
    	public boolean hasDatabase() {
    		return mDatabaseTable != null;
    	}
    	
    	public boolean hasDataAvaliable() { 
    		return mStatus.equals(Status.AVALIABLE) || mStatus.equals(Status.SEARCH_COMPLETE);
    	}
    	
    	public void insertRows(ArrayList<Object[]> rows) throws SQLException {
    		mDatabaseTable.insertRows(rows);
    		mDatabaseChanged = true;
    	}
    	
    	@SuppressWarnings("unchecked")
		public ArrayList<Object[]> getRows() {
    		return (ArrayList<Object[]>) mLoadedTable.clone();
    	}
    	
    	public void updateModel() {

    		if(mDatabaseTable == null)
    			return;
    		
    		if(mDatabaseChanged == false)
    			return;
    		
    		mLoadedTable = null;
    		try {
    			mLoadedTable = mDatabaseTable.getTable();
			} catch (SQLException e) {
				mLoadedTable = new ArrayList<Object[]>();
				MainWindow.showError(e);
			}
    		
    		final DefaultTableModel dtm = new DefaultTableModel();
    		dtm.setColumnCount(0);
    		dtm.setRowCount(0);
    		
    		String[] columnIdentifiers = getColumnIdentifers();
    		dtm.setColumnIdentifiers(columnIdentifiers);
    		
    		if(!mLoadedTable.isEmpty()) {
	    		for(Object[] row : mLoadedTable) {
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
    	
    	private class TableHeaderMouseListener implements MouseListener {

			@Override
			public void mousePressed(MouseEvent e) {
	    	    mTable.setRowSelectionAllowed(true);
	    		mTable.setColumnSelectionAllowed(true);
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}

		}
    		
    	private class TableSortPopupMenu extends JPopupMenu {
			private static final long serialVersionUID = 1L;
			ArrayList<RowSorter.SortKey> mRowSorterKeys; 
    		
    		public TableSortPopupMenu(ArrayList<RowSorter.SortKey> rowSorterKeys) {
    			mRowSorterKeys = rowSorterKeys;
    		}
    		
    	
    		public SortOrder toggleSortOrder(SortOrder sortOrder) {
    			return (sortOrder == SortOrder.ASCENDING) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
    		}
    		
    		public void addSortColumn(int columnIndex) {
    			
    			if(mRowSorterKeys.isEmpty()) {
    				mRowSorterKeys.add(new RowSorter.SortKey(columnIndex, SortOrder.DESCENDING));
    			} else {
    				boolean foundSortKey = false;
    				for(int i = 0; i < mRowSorterKeys.size(); i++) {
    					RowSorter.SortKey sortKey = mRowSorterKeys.get(i);
    					int sortKeyColumn = sortKey.getColumn();
    					if(sortKeyColumn == columnIndex) {
    						SortOrder sortOrder = sortKey.getSortOrder();
    						SortOrder toggledSortOrder = toggleSortOrder(sortOrder);
    						RowSorter.SortKey toggledSortKey = new RowSorter.SortKey(sortKeyColumn, toggledSortOrder);
    						mRowSorterKeys.set(i, toggledSortKey);
    						foundSortKey = true;
    					} 
    				}
    				
    				if(!foundSortKey) {
    					mRowSorterKeys.add(new RowSorter.SortKey(columnIndex, SortOrder.DESCENDING));
    				}
    				
    			}
    			
				TableRowSorter<TableModel> sorter = new TableRowSorter<>(mTable.getModel());
				
				sorter.setSortKeys(mRowSorterKeys);
				sorter.sort();
				mTable.setRowSorter(sorter);
				mTable.repaint();
    		}
    		
    		public void clearSortColumns() {
    			mRowSorterKeys.clear();
				TableRowSorter<TableModel> sorter = new TableRowSorter<>(mTable.getModel());

				sorter.setSortKeys(mRowSorterKeys);
				sorter.sort();
				mTable.setRowSorter(sorter);
				mTable.repaint();
    		}
    		
    		private String getMenuItemText(TableColumn tableColumn, RowSorter.SortKey sortKey) {
    			String headerValue = tableColumn.getHeaderValue().toString();
    			String arrow = ((sortKey.getSortOrder().equals(SortOrder.DESCENDING)) ? "↓" : "↑");
				return headerValue + " (" + arrow + ")";
    		}
    		
    		public void show(MouseEvent e) {
    			
    			JTableHeader h = (JTableHeader)e.getSource();
                int i = h.columnAtPoint(e.getPoint());
                if (i < 0) {
                    return ;
                }
                TableColumn tableColumn = h.getColumnModel().getColumn(i);
                
    			this.removeAll();
    			if(mRowSorterKeys.isEmpty()) {
    				String columnName = (String) tableColumn.getHeaderValue();
    				JMenuItem menuItem = new JMenuItem("Sort by " + columnName);
    				menuItem.addActionListener(new SortByColumnActionListener(this, i));
    				add(menuItem);
    			} else {
    				
    				boolean foundSortKey = false; 
    				for(int j = 0; j < mRowSorterKeys.size(); j++) {
    					RowSorter.SortKey sortKey = mRowSorterKeys.get(j);
        				int column = sortKey.getColumn();
        				TableColumn sortKeyColumn = h.getColumnModel().getColumn(column);
    					String title = getMenuItemText(sortKeyColumn, sortKey);
        				JMenuItem menuItem = new JMenuItem(title);
        				menuItem.addActionListener(new SortByColumnActionListener(this, column));
        				
        				add(menuItem);
        				
        				if(column == i) {
        					foundSortKey = true;
        				}
        				
    				}
    				
    				if(!foundSortKey) {
        				String columnName = (String) tableColumn.getHeaderValue();
        				JMenuItem menuItem = new JMenuItem("Order by " + columnName);
        				menuItem.addActionListener(new SortByColumnActionListener(this, i));
        				add(menuItem);
    				}
    				
    				JMenuItem menuItem = new JMenuItem("Clear");
    				menuItem.addActionListener(new ClearColumnActionListener(this));
    				this.add(menuItem);
    			}
    			
    			this.setVisible(true);
    			show(e.getComponent(), e.getX(), e.getY());
    		}
    		
    		class SortByColumnActionListener implements ActionListener {
    			
    			private int mColumnIndex;
    			private TableSortPopupMenu mTableSortPopupMenu;
    			
    			public SortByColumnActionListener(TableSortPopupMenu tableSortPopupMenu, int columnIndex) {
    				mColumnIndex = columnIndex;
    				mTableSortPopupMenu = tableSortPopupMenu;
    			}
    			
				@Override
				public void actionPerformed(ActionEvent e) {
					mTableSortPopupMenu.addSortColumn(mColumnIndex);
				}
    			
    		}
    		
    		class ClearColumnActionListener implements ActionListener {
    			
    			TableSortPopupMenu mTableSortPopupMenu;
    			
    			public ClearColumnActionListener(TableSortPopupMenu tableSortPopupMenu) {
    				mTableSortPopupMenu = tableSortPopupMenu;
    			}
    			
				@Override
				public void actionPerformed(ActionEvent e) {
					mTableSortPopupMenu.clearSortColumns();
				}
    			
    		}
    		
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
    		mTable.addMouseListener(new TableHeaderMouseListener());
    		
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
    			foundColumnIdentifiersIndicies[i] = mTable.convertColumnIndexToModel(indexColumnIdentifer);
    		}
    		return foundColumnIdentifiersIndicies;
    	}
    	
    	public ButtonTabComponent getHeader() {
    		return mHeader; 
    	}
    	
    	public ButtonTabComponent.TabButton getTabButton() {
    		return getHeader().getTabButton();
    	}
    	
    	public Status getStatus()		{ 	return mStatus;		}
    	public String getName()			{ 	return mName; 		}
    	public String getToolTip() 		{ 	return null;		}
    	public Icon getIcon() 			{	return null;		}
    	
    	private class TableMouseListener implements MouseListener {
    		
    		private ArrayList<RowSorter.SortKey> mRowSorterKeys = new ArrayList<RowSorter.SortKey>(); 
    		private final TableSortPopupMenu mTableSortPopupMenu = new TableSortPopupMenu(mRowSorterKeys);
    		
    		private boolean isSelectingColumns() {
    			return true;
    		}
    	
    		public void selectColumn(MouseEvent e) {
                JTableHeader h = (JTableHeader) e.getSource();
                int i = h.columnAtPoint(e.getPoint());
                
                if(isSelectingColumns()) {
                	mTable.setColumnSelectionAllowed(true);
    				mTable.setRowSelectionAllowed(false);
    				
                	if(e.isControlDown()) {
                		int[] selectedColumns = mTable.getSelectedColumns();
                		boolean containsColumn = IntStream.of(selectedColumns).anyMatch(x -> x == i);
                		
                		if(containsColumn) {
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
                			mTable.addColumnSelectionInterval(i,i);	
                		}
            			
                	} else {
            			mTable.setColumnSelectionInterval(i, i);
            		}
                }
    		}

			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
            		selectColumn(e);
            	}else if(SwingUtilities.isRightMouseButton(e)) {
            		mTableSortPopupMenu.show(e);        
            	}
			}

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

            
        }
    	
    	public String[] getColumnIdentifers() {
    		TableColumnModel tcm = mTable.getTableHeader().getColumnModel();
    		String[] columnIdentifers = new String[tcm.getColumnCount()];
    		for(int i = 0; i < tcm.getColumnCount(); i++) {
    			columnIdentifers[i] = tcm.getColumn(i).getHeaderValue().toString();
    		}
    		return columnIdentifers;
    	}
 
    	public String[] getSelectedColumnIdentifers() {
    		
    		int[] cols = mTable.getSelectedColumns();
			for (int i = 0; i < cols.length; i++) {
				cols[i] = mTable.convertColumnIndexToModel(cols[i]);
			}
    		
			TableColumnModel tcm = mTable.getTableHeader().getColumnModel();
			String[] selectedColumns = new String[cols.length];
			for(int i = 0; i < selectedColumns.length; i++) {
				selectedColumns[i] = (String)tcm.getColumn(i).getHeaderValue();
			}
			
    		return selectedColumns; 
    	}

    	public ArrayList<Object[]> getSelectedRows() {
    		
    		int[] cols = mTable.getSelectedColumns();
			for (int i = 0; i < cols.length; i++) {
				cols[i] = mTable.convertColumnIndexToModel(cols[i]);
			}
			int[] rows;
			if(mTable.getRowSelectionAllowed()) {
				rows = mTable.getSelectedRows();
    			for (int i = 0; i < rows.length; i++) {
    				rows[i] = mTable.convertRowIndexToModel(rows[i]);
    			}
    		} else {
    			int numRows = mTable.getRowCount();
    			rows = new int[numRows];
    			for(int i = 0; i < numRows; i++) {
    				rows[i] = mTable.convertRowIndexToModel(i);
    			}
    		}
    		
			ArrayList<Object[]> selectedRows = new ArrayList<Object[]>();
			for(int i = 0; i < rows.length; i++) {
				Object[] row = new Object[cols.length];
				for(int j = 0; j < cols.length; j++) {
					int rowIdx = rows[i];
					int colIdx = cols[j];
					Object cell = mLoadedTable.get(rowIdx)[colIdx];
					row[j] = cell;
				}
				selectedRows.add(row);
			}
    		return selectedRows; 
    	}

    	public boolean isEmpty() {
    		return !(mTable.getModel().getRowCount() > 0);
    	} 
    	
    	public boolean hasSelection() {
    		return mTable.getSelectedColumnCount() > 0;
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

			private static final long serialVersionUID = -1607002728575939037L;
    	    
    	    Tab mTab;
    	    JLabel mLabel;
    	    TabButton mTabButton; 
    	    
    	    TabButton getTabButton() {
    	    	return mTabButton;
    	    }
    	    
    	    public void enableLoadingIcon() {
    	    	String iconDirectory = FileManager.getIconDirectory();
    	        ImageIcon loading = new ImageIcon(iconDirectory + "\\ajax-loader.gif");
    	        Image image = loading.getImage(); // transform it 
    	        Image newimg = image.getScaledInstance(15, 15,  java.awt.Image.SCALE_DEFAULT); // scale it the smooth way  
    	        loading = new ImageIcon(newimg);
    	        mLabel.setIcon(loading);
    	    }
    	    
    	    public void disableLoadingIcon() {
    	    	mLabel.setIcon(null);
    	    }
    	    
    	    public ButtonTabComponent(Tab tab) {
    	        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

    	        mTab = tab;
    	        
    	        setOpaque(false);
    	        
    	        mLabel = new JLabel(mTab.getName());
    	        
    	        add(mLabel);
    	        mLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    	        
    	        
    	        //tab button
    	        mTabButton = new TabButton(mTab);
    	        add(mTabButton);
    	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    	        
    	    }

    	    private class TabButton extends JButton {
    	    	
				private static final long serialVersionUID = 5107206656315177278L;
    	    	
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
            	return this.getDatabaseTable().equals(t.getDatabaseTable());
            }
            
            
            return false;
        } 
    
    }     
	
    private String[] concatenate(String[] selectionA, String[] selectionB) {
		
		int aLen = selectionA.length;
		int bLen = selectionB.length;
		int outputLength = aLen + bLen;
		
		String[] result = new String[outputLength];
	
		System.arraycopy(selectionA, 0, result, 0, aLen);
		System.arraycopy(selectionB, 0, result, aLen, bLen);
		
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
			Tab tabA, String[] selectA, String[] keyA,
			Tab tabB, String[] selectB, String[] keyB,
			Database.Join.Type joinType){
    	
    	int[] aSelect = tabA.getColumnSelect(selectA);
    	int[] aKey = tabA.getColumnSelect(keyA);
    	int[] bSelect = tabB.getColumnSelect(selectB);
    	int[] bKey = tabB.getColumnSelect(keyB);
    	
    	
		String safeTableName = makeSafeTabName(tableName);
		String[] columnIdentifers = concatenate(selectA, selectB);
		String[] safeColumnIdentifers = makeSafeColumnIdentifers(columnIdentifers);
	
		Tab tabC = new Tab(safeTableName, safeColumnIdentifers);
		tabC.setStatus(Tab.Status.WAITING);
		
		Tab.ButtonTabComponent.TabButton tabButton = tabC.getTabButton();
		TabCloseActionListener tabCloseActionListener = new TabCloseActionListener(tabC);
		tabButton.addActionListener(tabCloseActionListener);
		addTab(tabC);
		
		JoinOperation joinOperation 
			= new JoinOperation(tabA, aSelect, aKey, tabB, bSelect, bKey, joinType, tabC);    	
		
		mJoinOperationThreadManager.addJoinOperation(joinOperation);
    }
    
    private class SearchOperation {

    	private rone.plugins.Search mSearch;
    	private Object[] mSearchRequests; 
    	private Tab mResultsTab; 
    	
    	
    	public SearchOperation(rone.plugins.Search search, Tab resultsTab){
    		this.mSearch = search; 
    		this.mSearchRequests = search.getSelectionRequests(); 
    		this.mResultsTab = resultsTab;
    	}
    	
    	public Tab getResultsTab() {
    		return this.mResultsTab;
    	}
    	
    	public Object[] getSearchRequests() {
    		return mSearchRequests;
    	}
    	
    	public rone.plugins.Search getSearch() {
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
		
		@SuppressWarnings("deprecation")
		public void removeTab(Tab removeTab) {
			
			try {
				joinCompleteSearches();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(SearchOperation searchOperation : mSearchQueue) {
				Tab searchOperationTab = searchOperation.mResultsTab;
				boolean found = searchOperation.mResultsTab.equals(removeTab);
				
				if(found) {
					mSearchQueue.remove(searchOperation);
					DatabaseTabbedPane.this.removeTab(searchOperationTab);
				}
			}
			
			for(MasterThread masterThread : mActiveQueue) {
				Tab masterThreadTab = masterThread.mResultsTab;
				boolean found = masterThreadTab.equals(removeTab);
				
				if(found) {
					masterThread.stopProcess();
					try {
						masterThread.join();
					} catch (InterruptedException e) {
						masterThread.stop();
						MainWindow.showError(e);
					}
					
				}
			}
			
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
							mSearchQueue.wait();
						}
						
						joinCompleteSearches();
						startInActiveSearches();
						mSearchQueue.notifyAll();
					}
					
				} catch(InterruptedException e) {  
					e.printStackTrace();
				}
			}
			
			try {
				cleanUp();
			} catch (InterruptedException e) {
				MainWindow.showError(e);
			}
	    }
		
		private boolean threadEnded(MasterThread mt) {
			return mt.processSuccess() || mt.hasToStopWorking();
		}
		
		private void cleanUp() throws InterruptedException {

			endThreads();
			
			synchronized(mSearchQueue) {
				while(!mActiveQueue.isEmpty()) {
					MasterThread mt = mActiveQueue.remove(0);
					try {
						mt.join();
					} catch (InterruptedException e) {
						MainWindow.showError(e);
					}
				}
				mSearchQueue.notifyAll();
			}
		}
		
		private void joinCompleteSearches() throws InterruptedException {
			for(int i = 0; i < mActiveQueue.size(); ) {
					MasterThread mt = mActiveQueue.get(i);
					if(threadEnded(mt)) {
						mt.join();
						mActiveQueue.remove(i);
					} else {
						i++;
					}
					//TODO: Fix this later, should be no thread.sleep - wait for array to update
				}
			}
		
	};

	private class MasterThread extends Thread {
    	private int mThreadPoolSize = 5;
    	private int mWorkSize = 20;
    	private WorkerThread mThreadPool[];
    	
    	private Boolean mProcessSuccess;
    	private boolean mStopWorking;
    	private int mWorkHeadIndex;
    	
    	private rone.plugins.Search mSearch;
    	private Object[] mSearchRequests;
    	
    	private Tab mResultsTab; 
    	
    	public MasterThread(SearchOperation searchOperation) {
    		mSearch = searchOperation.getSearch();
    		mSearchRequests = searchOperation.getSearchRequests();
    		mResultsTab = searchOperation.getResultsTab();
    		
    		mThreadPoolSize = mSearch.getThreadPoolSize();
    		mWorkSize = mSearch.getMaximumSearchSize();
    		

    		setProcessSuccess(false);
    	}
    	
    	public void stopProcess() {
    		mStopWorking = true;
    	}
    	
    	private Object[] getRows(int begin, int end) {
    		assert(begin <= end);
    		int len = mSearchRequests.length;
    		end = len > end ? end : len;
    		int segment_len = end - begin;
    		
    		Object[] segment = new Object[segment_len];
    		for(int i = begin; i < end; i++) {
    			int j = i - begin;
    			segment[j] = mSearchRequests[i];
    		}
    		
    		return segment;
    	}
    	
    	public Object[] getNextWork() {
    		int begin = mWorkHeadIndex;
    		int end = mWorkHeadIndex + mWorkSize;
    		Object[] requestedRows = getRows(begin, end);
    		mWorkHeadIndex = mWorkHeadIndex + requestedRows.length;
    		return requestedRows;
    	}
    	
    	private boolean hasResults(ArrayList<Object[]> results) {
    		return (results != null) && results.size() > 0;
    	}
    	
    	private int mErrorCounter = 1;
    	private int MAX_ERROR_COUNTER = 3; 
    	
    	public void processResults(ArrayList<Object[]> results) {
    		
    		if(!hasResults(results)) 
    			return;
    		
    		try {
    			mResultsTab.insertRows(results);
			} catch (SQLException e) {
				if(mErrorCounter < MAX_ERROR_COUNTER) {
					MainWindow.showError(e);
					mErrorCounter++;
				}
				
			}
    		
    		if(mResultsTab.isVisible()) {
    			mResultsTab.updateModel();
    		}
    		
    	}
    	
    	public boolean hasWork(Object[] work) {
    		return work != null ? work.length > 0 : false;
    	}
    	
    	private WorkerThread[] makeThreadPool(int size, int priority) {
    		WorkerThread[] threadPool = new WorkerThread[size];
    		for(int i = 0; i < mThreadPoolSize; i++) {
    			threadPool[i] = new WorkerThread(mSearch);
    			threadPool[i].setPriority(priority);
    		}
    		return threadPool;
    	}
    	
    	public void giveWork(Object[] newSearchRequests) {
			int i = 0;
			boolean givenWork = false;
			while(!givenWork) {
				if(hasToStopWorking()) 
					return;
				
				if(!mThreadPool[i].isAlive()) {
					try {
						mThreadPool[i].join();
					} catch (InterruptedException e) {
						if(mErrorCounter < MAX_ERROR_COUNTER) {
							MainWindow.showError(e);
							mErrorCounter++;
						}
					}
					ArrayList<Object[]> results = mThreadPool[i].getSearchResults();
					
					//TODO: check if i don't need a new worker thread
					mThreadPool[i] = new WorkerThread(mSearch);
        			mThreadPool[i].setSearchRequests(newSearchRequests);
        			mThreadPool[i].start();
        			
					processResults(results);
    				
    				givenWork = true;
    			}
				i = (i + 1) % mThreadPoolSize;
				
			}
    	}
    	
    	private void cleanUp() {
    		boolean doneCleanUp = false;
    		while(!doneCleanUp) {
    			doneCleanUp = true;
	    		for(int i = 0; i < mThreadPool.length; i++) {
	    			WorkerThread worker = mThreadPool[i];
	    			try {
	    				if(worker.isAlive()) {
	    					doneCleanUp = false;
	    					worker.join();
	    				}
	    				
	    				if(!hasToStopWorking() && !worker.hasGivenResults()) {
	    					ArrayList<Object[]> results = worker.getSearchResults();
	    					processResults(results);
	    				}
	    				
					} catch (InterruptedException e) {
						MainWindow.showError(e);
					}
	    		}
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
    		mThreadPool = makeThreadPool(mThreadPoolSize, MAX_PRIORITY);
    	}
    	
    	public void run() {
    		
    		setUp();
    		
    		mResultsTab.setStatus(Tab.Status.SEARCHING);
    		
    		setProcessSuccess(false);
    		Object[] work = null;
    		while(hasWork(work = getNextWork())) {
    			if(hasToStopWorking()) {
    				break;
    			}
    			giveWork(work);
			}
	    		
    		cleanUp();
    		
    		if(!hasToStopWorking()) {
    			mResultsTab.setStatus(Tab.Status.SEARCH_COMPLETE);
    			mResultsTab.updateModel();
    			setProcessSuccess(true);
    		}
    	}

    
    }
	
    private class WorkerThread extends Thread 
	{ 
    	private boolean mHasGivenResults;
    	
    	private rone.plugins.Search mSearch;
    	private Object[] mSearchRequests;
    	private ArrayList<Object[]> mSearchResults;
    	
    	public WorkerThread(rone.plugins.Search search){
    		mSearch = search;
    		mSearchRequests = null;
    	}
    	
    	public void setSearchRequests(Object[] searchRequests) {
    		mSearchRequests = searchRequests;
    	}
    	
    	public ArrayList<Object[]> getSearchResults(){
    		setHasGivenResults(true);
    		return mSearchResults;
    	}

    	public void setHasGivenResults(boolean set) {
    		mHasGivenResults = set;
    	}
    	
    	public void setResults(ArrayList<Object[]> results) {
    		mSearchResults = results;
    	}
    	
	    public void run() 
	    {
    		ArrayList<Object[]> results = mSearch.getSearchResults(mSearchRequests);
    		setResults(results);
    		setHasGivenResults(false);
	    }

		public boolean hasGivenResults() {
			return mHasGivenResults;
		}
	
	} 
  
}
