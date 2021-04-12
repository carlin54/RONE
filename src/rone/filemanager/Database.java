/*
 * RONE
 * Copyright (C) [2021] [Carlin. R. Connell]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rone.filemanager;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.derby.drda.NetworkServerControl;

public class Database {
	
	private static Database single_instance = null;
	
	private static Object mutex = new Object();
	
	private Database() throws Exception {
		this.mDatabaseName = "Singleton"; 
		this.mServer = new NetworkServerControl(InetAddress.getByName("localhost"), SERVER_PORT);
		this.mServer.start(null);
	
	
		String connectionString = "jdbc:derby:memory:" + mDatabaseName + ";create=true;";
		this.mConnection = DriverManager.getConnection(connectionString);
		
		this.mTableNames = new ArrayList<String>();
	}
	
	
	
	public synchronized static Database getInstance() {
		
		Database result = single_instance;
		if (result == null) {
			synchronized (mutex) {
				result = single_instance;
				if (result == null) {
					try {
						single_instance = result = new Database();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
		
	}
	
	private static Integer SERVER_PORT = 1528;
	private NetworkServerControl mServer;
	private Connection mConnection; 
	
	
	private synchronized static NetworkServerControl getNetworkServerControl(){
		return getInstance().mServer;
	}
	
	private synchronized static Connection getConnection(){
		return getInstance().mConnection;
	}
	
	
	private String mDatabaseName;
	
	ArrayList<String> mTableNames;
	
	private void removeTable(String tableName) {
        Iterator itr = mTableNames.iterator(); 
        while (itr.hasNext()) 
        { 
            String str = (String)itr.next(); 
            if(itr.equals(str)) {
                itr.remove(); 
                break;
            }
        } 
  
	}
	
	public static class Table {
		
		String mTabelName;
		String[] mColumnIdentifiers;
		
		private Database getDatabase() {
			return Database.getInstance(); 
		}
		
		
		public int mRowCount;
		
		boolean mExistsInDatabase;
		
		@Override
	    public boolean equals(Object o) { 
	  
	        if (o == this) { 
	            return true; 
	        } 
	        
	        if (!(o instanceof Table)) { 
	            return false; 
	        } 
	          
	        Table t = (Table) o; 
	          
	        return this.getDatabase().equals(t.getDatabase())
	        	&& this.getName().equals(t.getName());
	    } 
		
		
		private void setExistsInDatabase(boolean set) {
			mExistsInDatabase = set;
		}
		
		private Table(Database database, String tableName, String[] columnNames) {
			this.mTabelName = tableName;
			this.mColumnIdentifiers = columnNames;
			this.mRowCount = 0;
		}
		
		
		public String getName() {
			return this.mTabelName; 
		}
		
		
		private String generateGetTableQuery() {
			String query = "SELECT * FROM " + mTabelName;
			return query;
		}
		
		
		
		public synchronized ArrayList<Object[]> getTable() throws SQLException {
			String query = generateGetTableQuery();
			
			java.sql.Statement statement = getConnection().createStatement();
			java.sql.ResultSet resultSet = statement.executeQuery(query);
			
			ArrayList<Object[]> queryResults = getResults(resultSet);
			return queryResults;
		}
		
		public String getColumnIdentifier(int idx) {
			return this.mColumnIdentifiers[idx];
		}
		
		public String[] getIdentifiers() {
			return this.mColumnIdentifiers;
		}
		
		private int finalIdx(String[] array) { return array.length-1; }; 
		
		public boolean existsInDatabase() {
			return this.mExistsInDatabase;
		}
		
		public void finalize() throws Throwable {
			Database.getInstance().removeTable(this);
		}
		
		public String generateInsertRowQuery() {
			String code = "INSERT INTO " + mTabelName + " (";
			String tail = "(";
			int i = 0;
			for(i = 0; i < mColumnIdentifiers.length-1; i++) {
				code += mColumnIdentifiers[i] + ',';
				tail += "?, ";
			}
			i = finalIdx(mColumnIdentifiers);
			tail += "?) \r\n";
			code += mColumnIdentifiers[i] + ") VALUES " + tail;
			return code;
		}
		
		
		public int getRowCount() {
			return mRowCount;
		}
		
		
		public int getColumnCount() {
			return this.getIdentifiers().length;
		}
		
		
		@SuppressWarnings("static-access")
		public synchronized int[] insertRows(ArrayList<Object[]> rows) throws SQLException {
			
			String code = generateInsertRowQuery();
			
			int[] success; 
			try (PreparedStatement ps = Database.getInstance().getConnection().prepareStatement(code)) {
	        	
	        	for(Object[] row : rows) {
		            for(int i = 1; i <= row.length; i++) {
		                Object cell = row[i-1];
		                if(cell == null) {
		                	cell = new String("null");
		                }
		            	ps.setString(i, cell.toString());

		            }
		            mRowCount += 1;
		            ps.addBatch();
	        	}
	            success = ps.executeBatch();
	        }
			setExistsInDatabase(true);
	        return success;
		}

		
		
	}
	
	private boolean hasPrimaryKeys(int[] primaryKeys) {
		return primaryKeys != null && primaryKeys.length > 0;
	}
	
	public String makeNameSafe(String str) {
		String newstr = str.replaceAll("[^A-Za-z]+", "_");
		
		while(newstr.startsWith("_")) {
			newstr = newstr.substring(1);
		}
		
		return newstr;
	}
	
	public String makeUniqueTableName(String tableName) {
		String checkTableName = new String(tableName);
		Integer i = 0;
		while(!isUniqueTableName(checkTableName)) {
			checkTableName = tableName + "_" + i.toString();
			if(isUniqueTableName(checkTableName)) {
				return checkTableName;
			}
			i = i + 1;
		}
		return checkTableName;
	}
	
	public boolean isUniqueTableName(String tableName) {
		for(String checkTableName : this.mTableNames) {
			if(checkTableName.equals(tableName)) {
				return false;
			}
		}
		return true;
	}
	
	public String makeTableNameSafe(String str) {
		String safeTableName = makeNameSafe(str);
		return makeUniqueTableName(safeTableName);
	}
	
	public String[] makeColumnIdentifiersUnique(String[] columnNames) {
		String[] uniqueColumnIdentifiers = columnNames.clone();

		boolean isUnique = false;
		while(!isUnique) {
			isUnique = true;
			Stack<Integer> skipIndicies = new Stack<Integer>();
			LinkedList<Integer> similarIndicies = new LinkedList<Integer>();
			for(Integer i = 0; i < uniqueColumnIdentifiers.length; i++) {
				String uniqueColumn = new String(uniqueColumnIdentifiers[i]);
					
				for(Integer j = i; j < uniqueColumnIdentifiers.length; j++) {
					if(skipIndicies.indexOf(j) != -1) {
						break;
					}
					if(uniqueColumn.equals(uniqueColumnIdentifiers[j])) {
						similarIndicies.add(j);
					}
				}
				
				if(similarIndicies.size() > 1) {
					isUnique = false;
					Integer j = 1;
					while(!similarIndicies.isEmpty()) {
						Integer k = similarIndicies.remove();
						uniqueColumnIdentifiers[k] = new String(uniqueColumn + "_" + j.toString());
						skipIndicies.add(k);
						j = j + 1;
					}
				}
				similarIndicies.clear();
			}
	
		}
		
		return uniqueColumnIdentifiers;
	}
	
	public String[] makeColumnIdentifiersSafe(String[] columnNames) {
		//TODO: FIX THIS WEIRD ISSUE
		String[] columnNamesSQL = new String[columnNames.length];
		for(int i = 0; i < columnNames.length; i++) {
			columnNamesSQL[i] = makeNameSafe(columnNames[i]);
		}
		
		columnNamesSQL = makeColumnIdentifiersUnique(columnNamesSQL);
		
		for(int i = 0; i < columnNamesSQL.length; i++) {
			columnNamesSQL[i] = "\"" + columnNamesSQL[i] + "\"";
		}
		
		columnNamesSQL = addQuotations(columnNamesSQL);
		return columnNamesSQL;
		
	}
	
	public String[] addQuotations(String[] columns) {
		for(int i = 0; i < columns.length; i++) {
			if(!isEncasedInQuotations(columns[i])) {
				columns[i] = "\"" + columns[i] + "\"";
			}
		}
		return columns;
	}
	
	public String generateCreateTableSQL(String tableName, String[] columnNames, int[] primaryKeys) {
		//TODO: make it accept all sorts of Objects
		assert(columnNames.length > 0);
		int finalColumnIdx = columnNames.length-1;
		int i = 0;
		
		// columns
		String query = "";
    	
		query = "CREATE TABLE " + tableName + "(\r\n";
		for(i = 0; i < columnNames.length-1; i++) {
			query += "\t" + columnNames[i] + " VARCHAR(32672), \r\n";
		}
		
		query += "\t" + columnNames[finalColumnIdx] + " VARCHAR(32672)";
        if(hasPrimaryKeys(primaryKeys)) {
        	query += ",";
        }
        query += " \r\n";
        
        // primary keys	
        if(hasPrimaryKeys(primaryKeys)) {
    		int finalPrimaryKeyIdx = primaryKeys.length-1;
        	query += "\tPRIMARY KEY(";
    		for(i = 0; i < primaryKeys.length-1; i++) {
    			query += columnNames[primaryKeys[i]] + ", ";
    		}
    		query += "\t" + columnNames[primaryKeys[finalPrimaryKeyIdx]] + ")\r\n";
        }
		query += ")";
		return query;
	}

	private static final char CHAR_QUOTATION = '\"';
	private static boolean isEncasedInQuotations(String str) {
		int len = str.length();
		return len > 2 ? str.charAt(0) == CHAR_QUOTATION && str.charAt(len-1) == CHAR_QUOTATION : false;
	}
	
	@SuppressWarnings("static-access")
	public Table createTable(String tableName, String[] columnNames, int[] primaryKeys) throws SQLException {

		String safeTableName = makeTableNameSafe(tableName);
		
		String[] safeColumnIdentifiers = makeColumnIdentifiersSafe(columnNames);
		
		String code = generateCreateTableSQL(safeTableName, safeColumnIdentifiers, primaryKeys);
		
		Database.Table create = new Database.Table(this, safeTableName, safeColumnIdentifiers);
		
		java.sql.Statement statement = this.getConnection().createStatement();
		boolean success = statement.execute(code);
		statement.close();
		
		this.mTableNames.add(safeTableName);
		create.setExistsInDatabase(true);
		return create;
	}
	
	private String generateDeleteTableQuery(String tableName) {
		String query = "DROP TABLE " + tableName;
		return query;
	}
	
	public void removeTable(Table removeTable) throws SQLException {
		if(removeTable.existsInDatabase()) {
			String query = generateDeleteTableQuery(removeTable.getName());
			
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(query);
			
			removeTable(removeTable.getName());
			
			removeTable.mTabelName = null;
			removeTable.mExistsInDatabase = false;
			removeTable.mColumnIdentifiers = null;
			
			removeTable.setExistsInDatabase(false);
		}
	}
	
	public Join createJoin (
			Database.Table tableA, 
			int[] tableSelectA, 
			int[] tableJoinA, 
			Database.Table tableB, 
			int[] tableSelectB, 
			int[] tableJoinB, 
			Join.Type joinType)
	{
		return new Join(tableA, tableSelectA, tableJoinA, tableB, tableSelectB, tableJoinB, joinType);
	}
	
	static public class Join {
		
		public enum Type {
			LEFT_INCLUSIVE, 
			LEFT_EXCLUSIVE, 
			RIGHT_INCLUSIVE, 
			RIGHT_EXCLUSIVE, 
			FULL_OUTER_INCLUSIVE, 
			FULL_OUTER_EXCLUSIVE, 
			INNER
		} 
		
		private final static HashMap<Type, String> TYPE_TO_SQL;
		static{
			TYPE_TO_SQL = new HashMap<Type, String>();
			TYPE_TO_SQL.put(Type.LEFT_INCLUSIVE, "LEFT");
			TYPE_TO_SQL.put(Type.LEFT_EXCLUSIVE, "LEFT");
			TYPE_TO_SQL.put(Type.RIGHT_INCLUSIVE, "RIGHT");
			TYPE_TO_SQL.put(Type.RIGHT_EXCLUSIVE, "RIGHT");
			TYPE_TO_SQL.put(Type.FULL_OUTER_INCLUSIVE, "FULL");
			TYPE_TO_SQL.put(Type.FULL_OUTER_EXCLUSIVE, "FULL");
			TYPE_TO_SQL.put(Type.INNER, "INNER");
		};
		
		private Database.Table mTableA; 
		private int[] mSelectA; 
		private int[] mKeyA; 
		private Database.Table mTableB; 
		private int[] mSelectB; 
		private int[] mKeyB; 
		private Type mJoinType;
		
		public Database.Table getTableA()	{	return mTableA;		}
		public int[] getSelectA()			{	return mSelectA;	}
		public int[] getKeyA()				{	return mKeyA; 		}
		public Database.Table getTableB()	{	return mTableB;		}
		public int[] getSelectB()			{	return mSelectB;	}
		public int[] getKeyB()				{	return mKeyB;		}
		public Type getJoinType()			{	return mJoinType;	}
		
		public boolean hasWhereStatement() {
			return 		this.getJoinType().equals(Type.LEFT_EXCLUSIVE) 
					||	this.getJoinType().equals(Type.RIGHT_EXCLUSIVE) 
					||	this.getJoinType().equals(Type.FULL_OUTER_EXCLUSIVE);
		}
		
		public boolean hasWhereStatementANull() {
			return 		this.getJoinType().equals(Type.RIGHT_EXCLUSIVE) 
					||	this.getJoinType().equals(Type.FULL_OUTER_EXCLUSIVE);
		}
		
		public boolean hasWhereStatementBNull() {
			return 		this.getJoinType().equals(Type.LEFT_EXCLUSIVE) 
					||	this.getJoinType().equals(Type.FULL_OUTER_EXCLUSIVE);
		}
		
		private Join(Database.Table tableA, 
					int[] selectA, 
					int[] keyA, 
					Database.Table tableB, 
					int[] selectB, 
					int[] keyB, 
					Join.Type joinType) {
			mTableA = tableA; 
			mSelectA = selectA; 
			mKeyA = keyA; 
			mTableB = tableB; 
			mSelectB = selectB; 
			mKeyB = keyB; 
			mJoinType = joinType;
		}
		
	}
	
	public String generateSelectForTableSQL(Database.Table t, int[] select) {
		String tableName = t.getName();
		String[] identifers = t.getIdentifiers();
		String selectSQL = ""; 
		int len = select.length;
		for(int i = 0; i < len-1; i++) {
			int idx = select[i];
			selectSQL += tableName + "." + identifers[idx] + ", ";
		}
		
		int finalIdx = len-1;
		if(finalIdx > -1) {
			selectSQL += tableName + "." + identifers[finalIdx];
		}
		
		return selectSQL;
	}
	
	public String generateSelectSQL(Database.Table a, int[] aKey, Database.Table b, int[] bKey) {
		String selectStatement = "SELECT ";
		if(aKey.length > 0)
			selectStatement += generateSelectForTableSQL(a, aKey);
		
		if(aKey.length > 0 && bKey.length > 0)
			selectStatement += ", ";
		
		if(bKey.length > 0)
			selectStatement += generateSelectForTableSQL(b, bKey);		
		
		return selectStatement;
	}
	
	public String generateOnSQL(Database.Table a, int[] aKey, Database.Table b, int[] bKey) {
		assert(aKey.length == bKey.length);
		
		String aTableName = a.getName();
		String[] aIdentifers = a.getIdentifiers();
		
		String bTableName = b.getName();
		String[] bIdentifers = b.getIdentifiers();
		
		String onStatement = "ON ";
		int len = Math.min(aKey.length, bKey.length);
		
		for(int i = 0; i < len-1; i++) {
			int aIdx = aKey[i];
			int bIdx = bKey[i];
			
			onStatement += aTableName + "." + aIdentifers[aIdx] + " = " + bTableName + "." + bIdentifers[bIdx] + "\r\n AND ";
		}
		
		int finalIdx = len-1;
		if(finalIdx > -1) {
			int aIdxFinal = aKey[finalIdx];
			int bIdxFinal = bKey[finalIdx];
			
			onStatement += aTableName + "." + aIdentifers[aIdxFinal] + " = " + bTableName + "." + bIdentifers[bIdxFinal] + "\r\n";
		}
		return onStatement;
		
	}
	
	public String generateKeyIsNull(Database.Table t, int[] isNull) {
		String tableName = t.getName();
		String[] identifers = t.getIdentifiers();
		int len = isNull.length;
		String statementIsNull = "";
		
		for(int i = 0; i < len-1; i++) {
			int idx = isNull[i];
			String identifer = identifers[idx];
			statementIsNull += tableName + "." + identifer + " IS NULL\r\n\tOR ";
		}
		
		int finalIdx = isNull[len-1];
		if(finalIdx > -1) {
			String identifer = identifers[finalIdx];
			statementIsNull +=  tableName + "." + identifer + " IS NULL";
		}
		return statementIsNull;
	}
	
	public String generateWhereSQL(Join join) {
		Database.Table aTable = join.getTableA();
		int[] aKey = join.getKeyA();
		Database.Table bTable = join.getTableB();
		int[] bKey = join.getKeyB();
		
		String whereStatement = new String("");
		if(join.hasWhereStatement()) {
			whereStatement += "WHERE ";
			boolean aIsNull = join.hasWhereStatementANull();
			boolean bIsNull = join.hasWhereStatementBNull();
			
			if(aIsNull && bIsNull) {
				whereStatement += generateKeyIsNull(aTable, aKey) + "\r\n";
				whereStatement += "\tOR ";
				whereStatement += generateKeyIsNull(bTable, bKey) + "\r\n";
				return whereStatement;
			}
			
			if(aIsNull) {
				whereStatement += generateKeyIsNull(aTable, aKey) + "\r\n";
				return whereStatement;
			}
			
			if(bIsNull) {
				whereStatement += generateKeyIsNull(bTable, bKey) + "\r\n";
				return whereStatement;
			}
			
		}
		return whereStatement;
	}
	
	public String joinToString(Join join) {
		Join.Type joinType = join.getJoinType();
		String typeSQL = Join.TYPE_TO_SQL.get(joinType);
		return typeSQL;
	}
	
	public String generateJoinQuery(Join join) {
		Database.Table aTable = join.getTableA();
		String aTableName = join.getTableA().getName();
		int[] aSelect = join.getSelectA();
		int[] aKey = join.getKeyA();
		
		Database.Table bTable = join.getTableB();
		String bTableName = join.getTableB().getName();
		int[] bSelect = join.getSelectB();
		int[] bKey = join.getKeyB();
		
		
		String query = generateSelectSQL(aTable, aSelect, bTable, bSelect) 	+ " \r\n" 
					  + "FROM " + aTableName								+ " \r\n"
					  + joinToString(join) + " JOIN " + bTableName 			+ " \r\n"
					  + generateOnSQL(aTable, aKey, bTable, bKey) 	+ " \r\n"
					  + generateWhereSQL(join) + " \r\n";
		
		
		return query;
	}
	
	String[] getColumnNames(java.sql.ResultSet resultSet) throws SQLException {
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		int rowCount = resultSet.getFetchSize();
		int columnCount = resultSetMetaData.getColumnCount();
		
		String[] columnNames = new String[columnCount];
		for(int i = 1; i <= columnCount; i++) {
			String columnName = resultSetMetaData.getColumnName(i);
			columnNames[i-1] = columnName;
		}
		return columnNames; 
	}
	
	static ArrayList<Object[]> getResults(java.sql.ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsmd = resultSet.getMetaData();
		
		int columnCount = rsmd.getColumnCount();

		ArrayList<Object[]> queryResults = new ArrayList<Object[]>();
		while (resultSet.next()) {
			Object[] row = new Object[columnCount]; 
		    for (int i = 0; i < columnCount; i++) {
		        //TODO: fix get object
		        Object columnValue = resultSet.getString(i+1);
		        row[i] = columnValue;
		    }
		    queryResults.add(row);
		}
		resultSet.close();
		return queryResults;
	}
	
	public Table createTableFromResults(String tableName, java.sql.ResultSet resultSet) throws SQLException {
		
		String[] columnNames = getColumnNames(resultSet);	
		
		Table createdTable = createTable(tableName, columnNames, null);
		ArrayList<Object[]> results = getResults(resultSet);
		createdTable.insertRows(results);
		
		return createdTable;
	}
	
	public Table joinAndCreate(String tableName, Join join) throws SQLException {
		
		java.sql.ResultSet joinResults = join(join);
		
		return createTableFromResults(tableName, joinResults);
		
	}
	
	
	public java.sql.ResultSet join(Join join) throws SQLException {
		String query = generateJoinQuery(join);
		java.sql.Statement statement = getConnection().createStatement();
		return statement.executeQuery(query); 
	}
	
	
	private String generateDeleteTablesQuery() {
		String query = "DROP DATABASE " + this.mDatabaseName;
		return query;
	    
	}
	
	
	public void finalize() throws Throwable {
		String query = generateDeleteTablesQuery();
		Statement statement = getConnection().createStatement();
		statement.executeUpdate(query);
		statement.close();
        String shutdownConnectionString = mDatabaseName + ";shutdown=true";
        DriverManager.getConnection(shutdownConnectionString);
        single_instance = null;
	}

	@Override
    public boolean equals(Object o) { 
  
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
        
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Database)) { 
            return false; 
        } else {
        	// assuming singleton
        	return true;
        }
          
    } 
	

}
