package toxicologygadget.ui;

import javax.swing.CellRendererPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import antlr.collections.impl.Vector;
import javafx.scene.control.TableColumn;
import toxicologygadget.filemanager.DataTable;

import java.util.ArrayList;

public class GeneTable extends JTable {
	
	DataTable dataTable;
	
	public GeneTable(){
	
	}
	
	public boolean isCellEditable(int row, int column) {                
        return false;               
    }
	
	private void clearTable() {
		dataTable = new DataTable();
		
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
		
		String[] identifiers = dataTable.getColumnIdentifiers();
				
		for(int i = 0; i < identifiers.length; i++) {
			model.addColumn(identifiers[i]);	
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
		
		
		dataTable = new DataTable(genelistData);
		
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
		
	public void importTable(DataTable importTable) {
		
		if(dataTable == null) {
			if(dataTable.containsColumn("Gene")) {
				
			}
		}else{
			dataTable = DataTable.leftJoin(dataTable, importTable, "Gene");
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
	
}
