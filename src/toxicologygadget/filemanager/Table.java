package toxicologygadget.filemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Table {
	
	private ArrayList<ArrayList<Object>> mTable;
	private ArrayList<String> mIdentifiers;
	
	public Table(){
		mTable = new ArrayList<ArrayList<Object>>();
		mIdentifiers = new ArrayList<String>();
	}
	
	public boolean isEmpty() {
		return mTable.isEmpty();
	}
	
	public boolean containsColumn(String col) {
		for(int i = 0; i < mIdentifiers.size(); i++) {
			if(mIdentifiers.get(i).equals(col)) return true;
		}
		return false;
	}
	
	public Table(ArrayList<ArrayList<Object>> table, ArrayList<String> columnIdentifiers){
		mTable = table;
		mIdentifiers = columnIdentifiers;
	}
	
	public Table(String[][] table){
		
		mIdentifiers = new ArrayList<String> ();
		
		Collections.addAll(mIdentifiers, table[0]);

		mTable = new ArrayList<ArrayList<Object>>();
		
		for(int i = 1; i < table.length; i++) {
			ArrayList<Object> row = new ArrayList<Object>();
			
			for(int j = 0; j < table[i].length; j++) {
				row.add(table[i][j]);
			}
			
			mTable.add(row);
			
		}
		
	}
	
	public boolean hasColumn(String col) {
		for(int i = 0; i < mIdentifiers.size(); i++) {
			if(mIdentifiers.get(i).equals(col)) 
				return true;
		}
		return false;
	}
	
	public int columnIndex(String col){
		for(int i = 0; i < mIdentifiers.size(); i++) {
			if(mIdentifiers.get(i).equals(col)) 
				return i;
		}
		return -1;
	}
		
	public static ArrayList<Integer> uniqueIndicies(String[] a, String[] b){
		
		ArrayList<Integer> aIndicies = new ArrayList<Integer>();
		
		for(int i = 0; i < a.length; i++) {
			boolean foundCol = false;
			for(int j = i; j < b.length; j++) {
				if(a[i].equals(b[j])){
					foundCol = true;
					break;
				}
			}
			
			if(!foundCol) {
				aIndicies.add(i);
			}
		}
		
		return aIndicies;
		
	}
	
	public static ArrayList<Integer> rangeIndicies(int a, int b){
		ArrayList<Integer> aIndicies = new ArrayList<Integer>();
		
		for(int i = a; i < b; i++) {
			aIndicies.add(i);
		}
		
		return aIndicies;
		
	}
	
	public ArrayList<String> getIdentifiers(){
		return mIdentifiers;
	}
	
	public static Table leftJoin(Table a, Table b, String keyA, String keyB) {
		
		// TODO: Make exceptions
		if(!a.hasColumn(keyA) || !b.hasColumn(keyB)) 
			return null; 
		
		int keyIndexA = a.columnIndex(keyA);
		int keyIndexB = b.columnIndex(keyB);
		
		// the old way
		ArrayList<Integer> aColumnSelect = rangeIndicies(0, a.getIdentifiers().size());
		ArrayList<Integer> bColumnSelect = rangeIndicies(0, b.getIdentifiers().size());
		bColumnSelect.remove(b.columnIndex(keyB));
		
		Set<String> abID = new HashSet<String>();
		abID.addAll(a.getIdentifiers());
		abID.addAll(b.getIdentifiers());
		
		// the new way
		aColumnSelect = new ArrayList<Integer>();
		bColumnSelect = new ArrayList<Integer>();
		ArrayList<String> aid = a.getIdentifiers();
		ArrayList<String> bid = b.getIdentifiers();
		
		for(Integer i = 0; i < a.getIdentifiers().size(); i++) {
			String o = aid.get(i);
			if(!bid.contains(o)) {
				aColumnSelect.add(i);
			}
		}
		bColumnSelect = rangeIndicies(0, b.getIdentifiers().size());
		bColumnSelect.remove(keyIndexB);
		aColumnSelect.add(keyIndexA);
		
		ArrayList<String> newColumnIdentifiers = joinIdentifiers(aColumnSelect, bColumnSelect, a.getIdentifiers(), b.getIdentifiers());
		ArrayList<Object> emptyArray = new ArrayList<Object>();
		
		for(int i = 0; i < bColumnSelect.size(); i++) {
			emptyArray.add(null);
		}
		
		
		ArrayList<ArrayList<Object>> newTable = new ArrayList<ArrayList<Object>>();
		
		for(int i = 0; i < a.mTable.size(); i++){
			
			boolean match = false;
			
			for(int j = i; j < b.mTable.size(); j++){
				Object aKey = a.mTable.get(i).get(keyIndexA);
				Object bKey = b.mTable.get(j).get(keyIndexB);
				
				if (aKey.equals(bKey)){
					ArrayList<Object> leftRow = a.mTable.get(i);
					ArrayList<Object> rightRow = b.mTable.get(j);
					ArrayList<Object> row = joinRows(aColumnSelect, bColumnSelect, leftRow, rightRow);
					newTable.add(row);
					assert(row.size() == (aColumnSelect.size() + bColumnSelect.size()));
					match = true;
				}
				
				
			}
			
			if(!match) {
				ArrayList<Object> row = (ArrayList<Object>) a.mTable.get(i).clone();
				
				row.addAll(emptyArray);
				newTable.add(row);
			}
			
		}
		
		return new Table(newTable, newColumnIdentifiers);
	}
	
	public static Table leftJoin(Table a, Table b, String keyCol) {
		
		// TODO: Make exceptions
		if(!a.hasColumn(keyCol) || !b.hasColumn(keyCol)) 
			return null; 
		
		int keyIndexA = a.columnIndex(keyCol);
		int keyIndexB = b.columnIndex(keyCol);
		
		// the old way
		ArrayList<Integer> aColumnSelect = rangeIndicies(0, a.getIdentifiers().size());
		ArrayList<Integer> bColumnSelect = rangeIndicies(0, b.getIdentifiers().size());
		bColumnSelect.remove(b.columnIndex(keyCol));
		
		Set<String> abID = new HashSet<String>();
		abID.addAll(a.getIdentifiers());
		abID.addAll(b.getIdentifiers());
		
		// the new way
		aColumnSelect = new ArrayList<Integer>();
		bColumnSelect = new ArrayList<Integer>();
		ArrayList<String> aid = a.getIdentifiers();
		ArrayList<String> bid = b.getIdentifiers();
		
		for(Integer i = 0; i < a.getIdentifiers().size(); i++) {
			String o = aid.get(i);
			if(!bid.contains(o)) {
				aColumnSelect.add(i);
			}
		}
		bColumnSelect = rangeIndicies(0, b.getIdentifiers().size());
		bColumnSelect.remove(keyIndexB);
		aColumnSelect.add(keyIndexA);
		
		ArrayList<String> newColumnIdentifiers = joinIdentifiers(aColumnSelect, bColumnSelect, a.getIdentifiers(), b.getIdentifiers());
		ArrayList<Object> emptyArray = new ArrayList<Object>();
		
		for(int i = 0; i < bColumnSelect.size(); i++) {
			emptyArray.add(null);
		}
		
		
		ArrayList<ArrayList<Object>> newTable = new ArrayList<ArrayList<Object>>();
		
		for(int i = 0; i < a.mTable.size(); i++){
			
			boolean match = false;
			
			for(int j = i; j < b.mTable.size(); j++){
				Object aKey = a.mTable.get(i).get(keyIndexA);
				Object bKey = b.mTable.get(j).get(keyIndexB);
				
				if (aKey.equals(bKey)){
					ArrayList<Object> leftRow = a.mTable.get(i);
					ArrayList<Object> rightRow = b.mTable.get(j);
					ArrayList<Object> row = joinRows(aColumnSelect, bColumnSelect, leftRow, rightRow);
					newTable.add(row);
					match = true;
				}
				
				
			}
			
			if(!match) {
				ArrayList<Object> row = (ArrayList<Object>) a.mTable.get(i).clone();
				
				
				
				row.addAll(emptyArray);
				newTable.add(row);
			}
			
		}
		
		return new Table(newTable, newColumnIdentifiers);
		
	}


	public void addRow(ArrayList<Object> row) {
		//TODO: add exception handling
		mTable.add(row);
	}
	
	public int rowCount() {
		return mTable.size();
	}
	
	public int columnCount() {
		return mIdentifiers.size();
	}
	
	public Object get(int row, int col) {
		return mTable.get(row).get(col);
	}
	
	private static ArrayList<Object> joinRows(ArrayList<Integer> aColumnSelect, ArrayList<Integer> bColumnSelect,
											  ArrayList<Object> leftRow, 		ArrayList<Object> rightRow) {
		ArrayList<Object> row = new ArrayList<Object>();
		for(Integer i : aColumnSelect) {
			row.add(leftRow.get(i));
		}
		
		for(Integer i : bColumnSelect) {
			row.add(rightRow.get(i));
		}
		return row;
	}
	
	private static ArrayList<String> joinIdentifiers(ArrayList<Integer> aColumnSelect, 
												 	 ArrayList<Integer> bColumnSelect,
												 	 ArrayList<String> leftCol, 
												 	 ArrayList<String> rightCol) 
	{
		
		ArrayList<String> row = new ArrayList<String>();
		for(Integer i : aColumnSelect) {
			row.add(leftCol.get(i));
		}
		
		for(Integer i : bColumnSelect) {
			row.add(rightCol.get(i));
		}
		
		return row;
		
	}	
	
	
	
}
