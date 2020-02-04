package toxicologygadget.ui;

import javax.swing.CellRendererPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import antlr.collections.impl.Vector;
import javafx.scene.control.TableColumn;
import java.util.ArrayList;

public class GeneTable extends JTable {
	
	private ArrayList<String> header;
	private ArrayList<ArrayList<Object>> dataList;
	
	public GeneTable(){
		header = new ArrayList<String>();
		dataList = new ArrayList<ArrayList<Object>>();
	}
	
	private void clearTable() {
		header = new ArrayList<String>();
		dataList = new ArrayList<ArrayList<Object>>();
		
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
	
	private void updateTable() {
		
		DefaultTableModel model = new DefaultTableModel();
		
		for(int i = 0; i < header.size(); i++) {
			model.addColumn(header.get(i));	
		}
		
		int rowWidth = dataList.size();
		int columnHeight = dataList.get(0).size();
		
		for(int i = 0; i < columnHeight; i++) {
			Object[] row = new Object[rowWidth];
			for(int j = 0; j < rowWidth; j++) {
				System.out.println("i: " + i + "j: " + j);
				row[j] = dataList.get(j).get(i);				
			}
			
			model.addRow(row);
		}
		
		this.setModel(model);
	}
	
	public void loadGenelist(String[] genelist) {
		
		//if(clearTableConfirmation())
		//	clearTable();
		//else 
		//	return;
		
		header.add("Genes");
		ArrayList<Object> column = new ArrayList<Object>();
		
		for(int i = 0; i < genelist.length; i++) {
			column.add(genelist[i]);
		}
		
		dataList.add(column);
		
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
	
}
