package toxicologygadget.ui;

import javax.swing.DefaultRowSorter;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import toxicologygadget.filemanager.Table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ToxicologyTable extends JTable {
	private JTable mJTable;
	private Table mDataTable;
	
	private List<RowSorter.SortKey> sortKeys;
	
	public ArrayList<String> getIdentifiers() {
		return mDataTable.getIdentifiers();
	}
	
	public String[] getSelected() {
		
		int[] rows = this.getSelectedRows();
		int[] cols = this.getSelectedColumns();
		
		Set<String> tree_set = new TreeSet<String>(); 
		for(int i = 0; i < rows.length; i++) {
			for(int j = 0; j < cols.length; j++) {
				int k = rows[i];
				String cell = (String)this.getValueAt(k, cols[j]);;
				 
				tree_set.add(cell);
			}
		}
		
		int len = tree_set.size();
		return tree_set.toArray(new String[len]);
	}
	
	public String[] getUniqueSelected() {
		
		int[] rows = this.getSelectedRows();
		int[] cols = this.getSelectedColumns();
		
		LinkedList<String> unique = new LinkedList<String>(); 
		
		
		Object last_added = null;
		for(int i = 0; i < rows.length; i++) {
			int r = rows[i];
			for(int j = 0; j < cols.length; j++) {
				int c = cols[j];
				int vr = this.convertRowIndexToModel(r);
				int vc = this.convertColumnIndexToModel(c);
				
				String cell =  getModel().getValueAt(vr, vc).toString();
				
				if(!cell.equals(last_added) && !unique.contains(cell)) {
					unique.add(cell);
					last_added = cell;
				}
					
			}
		}
		int len = unique.size();
		return unique.toArray(new String[len]);
		
	}
	
	public ToxicologyTable(){
		mJTable = this;
		mDataTable = new Table();
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
                
                final JPopupMenu popup = new JPopupMenu();
                
                JMenuItem menuItem = new JMenuItem("Sort by");
                menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Sort By!");
						TableRowSorter<TableModel> sorter = new TableRowSorter<>(mJTable.getModel());
						mJTable.setRowSorter(sorter);
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
						TableRowSorter<TableModel> sorter = new TableRowSorter<>(mJTable.getModel());
						mJTable.setRowSorter(sorter);
						sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
						sorter.setSortKeys(sortKeys);
						sorter.sort();
					}
                	
                });
                popup.add(menuItem);
                popup.show(h, e.getPoint().x, e.getPoint().y);
                
                System.out.println(selectedColumn);
                
            }
            
            
            
        });
	}
	
	public boolean isEmpty() {
		return mDataTable.isEmpty();
	} 
	
	public boolean hasSelection() {
		return this.getSelectedRowCount() > 0;
	}
	
	public boolean isCellEditable(int row, int column) {                
        return false;               
    }
	
	public void setTable(Table set) {
		this.mDataTable = set;
		updateTable();
	}
	
	public void clearTable() {
		mDataTable = new Table();
		
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		model.setRowCount(0);
		model.setColumnCount(0);
	}
	
	private boolean clearTableConfirmation() {
		if(mDataTable != null) {
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
	
	private void updateTable() {
		
		DefaultTableModel model = new DefaultTableModel();
		
		ArrayList<String> identifiers = mDataTable.getIdentifiers();
				
		for(int i = 0; i < identifiers.size(); i++) {
			model.addColumn(identifiers.get(i));
			System.out.println(identifiers.get(i));
		}
		int numRows = mDataTable.rowCount();
		int numCols = mDataTable.columnCount();
		
		System.out.println("Cols: " + numCols);
		
		for(int iRow = 0; iRow < numRows; iRow++) {
			Object[] row = new Object[numCols];
			for(int iCol = 0; iCol < numCols; iCol++) {
				row[iCol] = mDataTable.get(iRow, iCol);				
			}
			model.addRow(row);
		}
		
		this.setModel(model);
		
	}
	
	public boolean hasColumn(String col) {
		return mDataTable.hasColumn(col);
	}
	
	public void importTable(String keyA, String keyB, Table importTable) {
		
		
		if(this.mDataTable.columnCount() == 0) { 
			this.mDataTable = importTable;
		}else {
			Table tableA = this.mDataTable;
			Table tableB = importTable;
			
			this.mDataTable = Table.leftJoin(tableA, tableB, keyA, keyB);
		}
		updateTable();
	}
	
	public String getGenelistStringTxt() {
		String genelist = new String(); 
		
		int numRows = mDataTable.rowCount();
		int geneColIndex = mDataTable.columnIndex("Gene");
		
		for(int iRow = 0; iRow < numRows-1; iRow++) {
			genelist = genelist + mDataTable.get(iRow, geneColIndex) + "\n";
		}
		genelist = genelist + mDataTable.get(numRows-1, geneColIndex);
		
		return genelist;
	}
	
	public Object getCell(int row, int col) {
		return mDataTable.get(row, col);
	}
	
}
