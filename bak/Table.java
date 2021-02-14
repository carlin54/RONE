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
	}
	
	public boolean isEmpty() {
		return mTable.isEmpty();
	}
	
	public boolean containsColumn(String col) {
		return false;
	}
	
	public Table(ArrayList<ArrayList<Object>> table, ArrayList<String> columnIdentifiers){
		mTable = table;
		mIdentifiers = columnIdentifiers;
	}
	
	public Table(ArrayList<String> identifiers) {
	}
	
	
	public boolean hasColumn(String col) {
		for(int i = 0; i < mIdentifiers.size(); i++) {
			if(mIdentifiers.get(i).equals(col)) 
				return true;
		}
		return false;
	}
	
	public int columnIndex(String col){

	}
		
	public static ArrayList<Integer> uniqueIndicies(String[] a, String[] b){

		
	}
	
	public static ArrayList<Integer> rangeIndicies(int a, int b){

	}
	
	public ArrayList<String> getIdentifiers(){
	}
	
	
	public ArrayList<Object> getRow(int i){
	}
	
	public ArrayList<Object> getSelected(ArrayList<Integer> sel, ArrayList<Object> row){

	}
	
	// back burner
	public static Table join(Table a, Table b, String keyA, String keyB) {
	
		
		return null;
	}
	
	public static Table leftJoin(Table a, Table b, String keyA, String keyB) {
		
		return null;
	}
	
	public boolean addRow(Object[] row) {
		return null;
	}
	
	public static Table leftJoin(Table a, Table b, String keyCol) {
	
		
		return null;
		
	}
	
	public int rowCount() {
		return null;
	}
	
	public int columnCount() {
		return null;
	}
	
	public Object get(int row, int col) {
		return null;
	}
	
	private static ArrayList<Object> joinRows(ArrayList<Integer> aColumnSelect, ArrayList<Integer> bColumnSelect,
											  ArrayList<Object>  leftRow, 		ArrayList<Object> rightRow) {
		return null;
		
	}
	
	private static ArrayList<String> joinIdentifiers(ArrayList<Integer> aColumnSelect, 
												 	 ArrayList<Integer> bColumnSelect,
												 	 ArrayList<String> leftCol, 
												 	 ArrayList<String> rightCol) 
	{
		return null;
	}	
	
	
	
}




