package rone.filemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.table.TableModel;

import java.util.HashMap;

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
	
	public Table(ArrayList<String> identifiers) {
		mIdentifiers = identifiers;
		mTable = new ArrayList<ArrayList<Object>>();
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
	
	
	public ArrayList<Object> getRow(int i){
		return this.mTable.get(i);
	}
	
	public ArrayList<Object> getSelected(ArrayList<Integer> sel, ArrayList<Object> row){
		ArrayList<Object> ret = new ArrayList<Object>();
		for(Integer i : sel) {
			ret.add(row.get(i));
		}
		return ret;
	}
	
	// back burner
	public static Table join(Table a, Table b, String keyA, String keyB) {
		
		if(!a.hasColumn(keyA) || !b.hasColumn(keyB)) 
			return null; 
		
		int aKeyIndex = a.columnIndex(keyA);
		int bKeyIndex = b.columnIndex(keyB);
		
		ArrayList<Integer> aColumnSelect = new ArrayList<Integer>();
		ArrayList<Integer> bColumnSelect = new ArrayList<Integer>();
		ArrayList<String> aid = a.getIdentifiers();
		ArrayList<String> bid = b.getIdentifiers();
		
		for(Integer i = 0; i < a.getIdentifiers().size(); i++) {
			String o = aid.get(i);
			if(!bid.contains(o)) {
				aColumnSelect.add(i);
			}
		}
		bColumnSelect = rangeIndicies(0, b.getIdentifiers().size());
		bColumnSelect.remove(bKeyIndex);
		aColumnSelect.add(aKeyIndex);
		LinkedList<ArrayList<Object>> s; 
		
		// compress table a into HashMap
		HashMap<Object, LinkedList<ArrayList<Object>>[]> aHashMap = new HashMap<Object, LinkedList<ArrayList<Object>>[]>();
		final int iA = 0;
		final int iB = 0;
		for(int i = 0; i < a.rowCount(); i++) {
			ArrayList<Object> row = a.getRow(i);
			Object key = row.get(aKeyIndex);
			
			if(aHashMap.containsKey(key)) {
				LinkedList<ArrayList<Object>> stack = aHashMap.get(key)[0];
				stack.add(row);
			}else {
				LinkedList<ArrayList<Object>>[] newStack = new LinkedList[2];
				newStack[iA] = new LinkedList<ArrayList<Object>>();
				newStack[iB] = null;
				newStack[iA].add(row);
				aHashMap.put(key, newStack);
			}
			
		}
		
		// put table b into table a HashMap
		for(int i = 0; i < b.rowCount(); i++) {
			ArrayList<Object> row = b.getRow(i);
			Object key = row.get(aKeyIndex);
			
			if(aHashMap.containsKey(key)) {
				LinkedList<ArrayList<Object>>[] stack = aHashMap.get(key);
				if(stack[iB] == null) {
					stack[iB] = new LinkedList<ArrayList<Object>>();
				}
				stack[iB].add(row);
			}
			
		}
		
		// join tables
		
		
		
		return null;
	}
	
	public static Table leftJoin(Table a, Table b, String keyA, String keyB) {
		
		// TODO: Make exceptions
		if(!a.hasColumn(keyA) || !b.hasColumn(keyB)) 
			return null; 
		
		int keyIndexA = a.columnIndex(keyA);
		int keyIndexB = b.columnIndex(keyB);
		
		// the new way
		ArrayList<Integer> aColumnSelect = new ArrayList<Integer>();
		ArrayList<Integer> bColumnSelect = new ArrayList<Integer>();
		ArrayList<String> aid = a.getIdentifiers();
		ArrayList<String> bid = b.getIdentifiers();
		
		for(Integer i = 0; i < aid.size(); i++) {
			String o = aid.get(i);
			if(i != keyIndexA && !bid.contains(o)) {
				aColumnSelect.add(i);
			}
		}
		bColumnSelect = rangeIndicies(0, b.getIdentifiers().size());
		bColumnSelect.remove(keyIndexB);
		aColumnSelect.add(0, keyIndexA);
		
		ArrayList<String> newColumnIdentifiers = joinIdentifiers(aColumnSelect, bColumnSelect, aid, bid);
		
		Table joinedTable = new Table(newColumnIdentifiers);
		
		ArrayList<Object> emptyArray = new ArrayList<Object>(); 
		
		for(int i = 0; i < bColumnSelect.size(); i++) {
			emptyArray.add(null);
		}
		
		for(int i = 0; i < a.mTable.size(); i++){
			
			boolean match = false;
			for(int j = 0; j < b.mTable.size(); j++){
				
				Object aKey = a.mTable.get(i).get(keyIndexA);
				Object bKey = b.mTable.get(j).get(keyIndexB);
				
				if (aKey.equals(bKey)){
					ArrayList<Object> leftRow = a.mTable.get(i);
					ArrayList<Object> rightRow = b.mTable.get(j);
					ArrayList<Object> row = joinRows(aColumnSelect, bColumnSelect, leftRow, rightRow);
					joinedTable.addRow(row);
					assert(row.size() == (aColumnSelect.size() + bColumnSelect.size()));
					match = true;
				}
				
			}
			
			if(!match) {
				ArrayList<Object> row = a.getRow(i);
				row.addAll(emptyArray);
				assert(row.size() == (aColumnSelect.size() + bColumnSelect.size()));
				joinedTable.addRow(row);
			}
			
		}
		
		return joinedTable;
	}
	
	public boolean addRow(Object[] row) {
		if(row.length != columnCount()) {
			return false; 
		}else {
			this.mTable.add(row);
			return true;
		}
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
											  ArrayList<Object>  leftRow, 		ArrayList<Object> rightRow) {
		
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




