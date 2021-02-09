package rone.ui;

import javax.swing.DefaultRowSorter;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rone.filemanager.Database;
import rone.filemanager.Table;
import rone.ui.DatabaseTabbedPane.Tab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DataTable extends JTable {
	
	private Database.Table mDatabaseTable;
	private ArrayList<Object[]> mTableContence;
	private List<RowSorter.SortKey> sortKeys;
	private String[] mColumnIdentifiers;
	
	@Override 
	public boolean equals(Object o) {
	    return (o instanceof DataTable) 
	    	&& (this.mDatabaseTable == ((DataTable) o).mDatabaseTable)
	    	&& (this == ((DataTable)o));
	}
	
	public Database.Table getDatabaseTable(){
		return this.mDatabaseTable;
	}
	
	
	
 	public String[] getIdentifiers() {
		return mColumnIdentifiers;
	}
	
	public String[] getSelected() {
		
		int[] rows = this.getSelectedRows();
		int[] cols = this.getSelectedColumns();
		
		Set<String> treeSet = new TreeSet<String>(); 
		for(int i = 0; i < rows.length; i++) {
			for(int j = 0; j < cols.length; j++) {
				int k = rows[i];
				String cell = (String)this.getValueAt(k, cols[j]);
				treeSet.add(cell);
			}
		}
		
		int len = treeSet.size();
		return treeSet.toArray(new String[len]);
	}
	
	public String[] getUniqueSelected() {
		
		int[] rows = this.getSelectedRows();
		int[] cols = this.getSelectedColumns();
		
		ArrayList<String> unique = new ArrayList<String>(); 
		
		Object last_added = null;
		for(int i = 0; i < rows.length; i++) {
			int r = rows[i];
			for(int j = 0; j < cols.length; j++) {
				int c = cols[j];
				int vr = this.convertRowIndexToModel(r);
				int vc = this.convertColumnIndexToModel(c);
				
				String cell;
				if(getModel().getValueAt(vr, vc) != null) {
					cell = getModel().getValueAt(vr, vc).toString();
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
	
	
	private void init() {
		sortKeys = new ArrayList<>();
		setAutoCreateRowSorter(true);
		
		this.tableHeader.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader h = (JTableHeader) e.getSource();
                int i = h.columnAtPoint(e.getPoint());
                if (i < 0) {
                    return;
                }
                Object o = h.getColumnModel().getColumn(i).getHeaderValue();
                Object selectedColumn = o;
                
                /*
                final JPopupMenu popup = new JPopupMenu();
                
                JMenuItem menuItem = new JMenuItem("Sort by");
                menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Sort By!");
						TableRowSorter<TableModel> sorter = new TableRowSorter<>(getModel());
						setRowSorter(sorter);
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
						TableRowSorter<TableModel> sorter = new TableRowSorter<>(getModel());
						setRowSorter(sorter);
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
            
            
            
        });
	
		
	}
	
	private void setDatabaseTable(Database.Table databaseTable) throws SQLException {
		this.mDatabaseTable = databaseTable;
		this.updateTable();
	}
	
	/*public DataTable(String[] columnIdentifiers) {
		init();
		this.setCellSelectionEnabled(true);
		this.mColumnIdentifiers = columnIdentifiers;
	}*/
	
	public DataTable(Database.Table databaseTable) throws SQLException{
		this.setDatabaseTable(databaseTable);
		init();
	}
	
	public boolean hasSelection() {
		return this.getSelectedRowCount() > 0;
	}
	
	public boolean isCellEditable(int row, int column) {                
        return false;               
    }
	
	public void setTable(Database.Table set) throws SQLException {
		this.mDatabaseTable = set;
		updateTable();
	}
	
	public void clearTable() throws SQLException {
		Database.getInstance().removeTable(this.mDatabaseTable);
		DefaultTableModel model = new DefaultTableModel();
		setModel(model);
		
	}
	
	private boolean clearTableConfirmation() {
		if(mDatabaseTable != null) {
			int n = JOptionPane.showConfirmDialog(
				    this,
				    "Are you sure you would like to clear the current table?",
				    "Import Genelist",
				    JOptionPane.YES_NO_OPTION);
			return n == 0;
		}else {
			return true;
		}

	}
	
	
	private DefaultTableModel makeDefaultTableModel() {
		
		DefaultTableModel model = new DefaultTableModel();
		for(int i = 0; i < mColumnIdentifiers.length; i++) {
			String identifier = mColumnIdentifiers[i];
			model.addColumn(identifier);
		}
		return model;
	}
	
	private DefaultTableModel loadDataIntoTableModel(DefaultTableModel model) throws SQLException {
		
		mTableContence = mDatabaseTable.getTable();
		
		if(!mTableContence.isEmpty()) {
			int numRows = mTableContence.size();
			int numCols =  mTableContence.get(0).length;
			
			//System.out.println("Rows: " + numRows);
			//System.out.println("Cols: " + numCols);
			
			for(int iRow = 0; iRow < numRows; iRow++) {
				Object[] row = mTableContence.get(iRow);	
				model.addRow(row);
			}
		}
		return model;
	}
	
	public void updateTable() throws SQLException {
		assert(mDatabaseTable != null);
		setUpdatingTable(true);
	
		mTableContence = mDatabaseTable.getTable();
	
		DefaultTableModel model = makeDefaultTableModel();
		model = loadDataIntoTableModel(model);

		setModel(model);
		
		setUpdatingTable(false);
	}
	
	public boolean hasColumn(String col) {
		return mDatabaseTable.getIdentifiers().toString().contains(col);
	}
	
	public Object getCell(int row, int col) {
		return this.mTableContence.get(col)[row];
	}
		
}
