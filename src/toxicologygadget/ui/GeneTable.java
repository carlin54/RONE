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
		

		ArrayList<ArrayList<Object>> table = dataTable.getTable();
		int rowWidth = table.get(0).size();
		int columnHeight = table.size();
		
		for(int i = 0; i < columnHeight; i++) {
			Object[] row = new Object[rowWidth];
			for(int j = 0; j < rowWidth; j++) {
				row[j] = table.get(i).get(j);				
			}
			
			model.addRow(row);
		}
		
		this.setModel(model);
	}
	
    public boolean isCellEditable(int row, int column) {                
        return false;               
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
	
	public void importColumn(Object[] data, String columnIdentifier) {
		
		header.add(columnIdentifier);
		
		ArrayList<Object> column = new ArrayList<Object>(); 
		for(int i = 0; i < data.length; i++) {
			column.add(data[i]);
		}
		
		dataList.add(column);
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
	
}
