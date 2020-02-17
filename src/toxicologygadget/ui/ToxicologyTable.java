package toxicologygadget.ui;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import antlr.collections.impl.Vector;
import javafx.scene.control.TableColumn;
import toxicologygadget.filemanager.Database;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

public class ToxicologyTable extends JTable {
	
	Database dataTable;
	
	public String[] getSelected() {
		
		int[] rows = this.getSelectedRows();
		int[] cols = this.getSelectedColumns();
		
		Set<String> tree_set = new TreeSet<String>(); 
		for(int i = 0; i < rows.length; i++) {
			for(int j = 0; j < cols.length; j++) {
				int k = rows[i];
				String cell = (String) getModel().getValueAt(k, cols[j]);
				tree_set.add(cell);
			}
		}
		
		int len = tree_set.size();
		return tree_set.toArray(new String[len]);
	}
	
	public ToxicologyTable(){
		dataTable = new Database();
		
        
		this.tableHeader.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader h = (JTableHeader) e.getSource();
                int i = h.columnAtPoint(e.getPoint());
                Object o = h.getColumnModel().getColumn(i).getHeaderValue();
                Object selectedColumn;
                if (i < 0) {
                    selectedColumn = null;
                    return;
                }
                selectedColumn = o;
                h.getColumnModel().getColumn(i).setHeaderValue("Clicked");
                

                
            }
        });
	}
	
	public boolean isCellEditable(int row, int column) {                
        return false;               
    }
	
	private void clearTable() {
		dataTable = new Database();
		
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		model.setRowCount(0);
	}
	
	private boolean clearTableConfirmation() {
		if(dataTable != null) {
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
		
		ArrayList<String> identifiers = dataTable.getIdentifiers();
				
		for(int i = 0; i < identifiers.size(); i++) {
			model.addColumn(identifiers.get(i));	
		}
		

		int numRows = dataTable.rowCount();
		int numCols = dataTable.columnCount();
		
		for(int iRow = 0; iRow < numRows; iRow++) {
			Object[] row = new Object[numCols];
			for(int iCol = 0; iCol < numCols; iCol++) {
				row[iCol] = dataTable.get(iRow, iCol);				
			}
			model.addRow(row);
		}
		this.setModel(model);
		
		
		this.setAutoCreateRowSorter(true);
	}
	
	public boolean hasColumn(String col) {
		return dataTable.hasColumn(col);
	}
	
	public void loadGenelist(String[] genelist) {
		
		if(!clearTableConfirmation())
			return;
		
		
		String[][] genelistData = new String[genelist.length][1];
		genelistData[0][0] = "Gene";
		for(int i = 1; i < genelist.length; i++) {
			genelistData[i][0] = genelist[i-1];
		}
		
		
		dataTable = new Database(genelistData);
		
		updateTable();
		
	} 
	
	public void loadClusters(int[] clusters) {
		
	}
	
	public void importRow(Object[] data) {
		
	}
	
	public void importColumn(Object[] data, String columnIdentifier) {
		
		
		updateTable();
	}
	
	public void importTable(Object[][] data, String[] columnIdentifier) {
		
		for(int i = 0; i < data.length; i++) {
			importColumn(data[i], columnIdentifier[i]);
		}
			
	}
	
	public void importTable(Database importTable) {
		
		if(dataTable.columnCount() == 0) {
			dataTable = importTable;
		}else {
			if(importTable.containsColumn("Gene")) {
				dataTable = Database.leftJoin(dataTable, importTable, "Gene");
			}
		}
		
		
		updateTable();
	}
	
	public String getGenelistStringTxt() {
		String genelist = new String(); 
		
		int numRows = dataTable.rowCount();
		int geneColIndex = dataTable.columnIndex("Gene");
		
		for(int iRow = 0; iRow < numRows-1; iRow++) {
			genelist = genelist + dataTable.get(iRow, geneColIndex) + "\n";
		}
		genelist = genelist + dataTable.get(numRows-1, geneColIndex);
		
		return genelist;
	}
	
	public Object getCell(int row, int col) {
		return dataTable.get(row, col);
	}
	
}
