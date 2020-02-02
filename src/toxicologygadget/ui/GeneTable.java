package toxicologygadget.ui;

import javax.swing.CellRendererPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import antlr.collections.impl.Vector;
import javafx.scene.control.TableColumn;

public class GeneTable extends JTable {
	
	private void clearTable() {
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		model.setRowCount(0);
	}
	
	private boolean clearTableConfirmation() {
		int n = JOptionPane.showConfirmDialog(
			    this,
			    "Are you sure you would like to clear the current table?",
			    "Import Genelist",
			    JOptionPane.YES_NO_OPTION);
		return n == 0;
	}
	
	public void loadGenelist(String[] genelist) {
		
		if(clearTableConfirmation())
			clearTable();
		else 
			return;
		
		String[] columnNames = {"Genelist"};
		
		
		DefaultTableModel model = new DefaultTableModel();
		
		model.addColumn("Genes");
		for(int i = 0; i < genelist.length; i++) {
			model.addRow(new Object[]{genelist[i]});
		}
		
		
		this.setModel(model);
		
	} 
	
	public void loadClusters(int[] clusters) {
		
	}
	
	public void importColumn(Object[] data, String columnIdentifier) {
		
	}
	
	
}
