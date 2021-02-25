package rone.plugins;

import java.util.ArrayList;

public class Selection {
	
	private ArrayList<Object[]> mSelected;
	private String[] mColumns;
	
	public Selection(String[] columns, ArrayList<Object[]> selection){
		mSelected = selection;
		mColumns = columns;
	}
	
	public ArrayList<Object[]> getSelected() { 
		return mSelected; 
	}
	
	public String[] getColumns() { 
		return mColumns;
	}
	
	public String[] toStringArray() {
		ArrayList<String> cellsAsString = new ArrayList<String>();
		for(int i = 0; i < mSelected.size(); i++) {
			Object[] row = mSelected.get(i);
			for(int j = 0; j < row.length; j++) {
				String cell = row[j].toString();
				cellsAsString.add(cell);
			}
		}
		
		String[] output = new String[cellsAsString.size()];
		for(int i = 0; i < output.length; i++) {
			output[i] = cellsAsString.get(i);
		}
		return output;
	}
	
	public Object[] toObjectArray(){
		ArrayList<Object> cells = new ArrayList<Object>();
		for(int i = 0; i < mSelected.size(); i++) {
			Object[] row = mSelected.get(i);
			for(int j = 0; j < row.length; j++) {
				Object cell = row[j];
				cells.add(cell);
			}
		}
		return cells.toArray();
	}
	
	public ArrayList<Object[]> getRows(int begin, int end) {
		ArrayList<Object[]> segment = new ArrayList<Object[]>();
		int len = mSelected.size();
		int min = len > end ? end : len;
		
		for(int i = begin; i < min; i++) {
			Object[] row = mSelected.get(i);
			segment.add(row);
		}
		
		return segment;
	}
	
	public Object getCell(int rowIndex, int colIndex) {
		Object[] row = mSelected.get(rowIndex);
		return row[colIndex];
	}
	
	public int numRows() {
		return mSelected.size();
	}
	
	public int numCols() {
		return this.mColumns.length;
	}
	
	public boolean isEmpty() {
		return mSelected.isEmpty();
	}
	
}
