package rone.filemanager;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.derby.drda.NetworkServerControl;


public class Database {
	
	private static Database single_instance = null;
	
	private Database() throws Exception {
		System.out.println("Database()");
		this.mDatabaseName = "Singleton"; 
		this.mServer = new NetworkServerControl(InetAddress.getByName("localhost"), SERVER_PORT);
		
		System.out.println("Created server on port " + SERVER_PORT);
	
		this.mServer.start(null);
	
	
		String connectionString = "jdbc:derby:memory:" + mDatabaseName + ";create=true;";
		this.mConnection = DriverManager.getConnection(connectionString);
		System.out.println("Server started");
		
		this.mTableNames = new ArrayList<String>();
	}
	
	public static Database getInstance() {
		
		if(single_instance == null) {
			try {
				single_instance = new Database();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return single_instance;
	}
	
	private static Integer SERVER_PORT = 1528;
	private NetworkServerControl mServer;
	private String mDatabaseName;
	private Connection mConnection; 
	
	//TODO: make safe table names
	ArrayList<String> mTableNames;
	
	private void removeTable(String tableName) {
        Iterator itr = mTableNames.iterator(); 
        while (itr.hasNext()) 
        { 
            if(itr.equals(tableName)) {
                itr.remove(); 
                break;
            }
        } 
  
	}
	
	public class Table {
		
		String mTabelName;
		String[] mColumnIdentifiers;
		Database mDatabase;
		
		public int mRowCount;
		boolean mHasData;
		
		private void setHasData(boolean set) {
			mHasData = set;
		}
		
		
		private Table(Database database, String tableName, String[] columnIdentifiers) {
			this.mDatabase = database;
			this.mTabelName = tableName;
			this.mColumnIdentifiers = columnIdentifiers;
			this.mRowCount = 0;
		}
		
		
		public String getName() {
			return this.mTabelName; 
		}
		
		
		private String generateGetTableQuery() {
			String query = "SELECT * FROM " + mTabelName;
			return query;
		}
		
		
		public ArrayList<Object[]> getTable() throws SQLException {
			String query = generateGetTableQuery();
			System.out.println("----generated sql query----");
			System.out.println(query);
			System.out.println("----generated sql query----");
			
			java.sql.Statement statement = mConnection.createStatement();
			java.sql.ResultSet rs = statement.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			System.out.println("----query results----");
			ArrayList<Object[]> queryResults = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = new Object[columnCount]; 
			    for (int i = 0; i < columnCount; i++) {
			        if (i > 0) System.out.print(",  ");
			        //TODO: fix get object
			        Object columnValue = rs.getString(i+1);
			        row[i] = columnValue;
			        System.out.print(columnValue);
			    }
			    queryResults.add(row);
			    System.out.println("");
			}
			rs.close();
			System.out.println("----query results----");
			return queryResults;
		}
		
		
		public String getColumnIdentifier(int idx) {
			return this.mColumnIdentifiers[idx];
		}
		
		
		public String[] getColumnIdentifiers() {
			return this.mColumnIdentifiers;
		}
		
		
		private int finalIdx(String[] array) { return array.length-1; }; 
		
		
		private String generateDeleteTableQuery() {
			String query = "DROP TABLE " + this.mTabelName;
			return query;
		}
		
		
		public void clearTable() throws SQLException {
			String query = generateDeleteTableQuery();
			System.out.println("----generated sql----");
			System.out.println(query);
			System.out.println("----generated sql----");
			
			Statement statement = mConnection.createStatement();
			statement.executeUpdate(query);
			
			setHasData(false);
		}
		
		
		public void finalize() throws Throwable {
			String query = generateDeleteTableQuery();
			System.out.println("----generated sql----");
			System.out.println(query);
			System.out.println("----generated sql----");
			
			Statement statement = mConnection.createStatement();
			statement.executeUpdate(query);
			
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
			return this.getColumnIdentifiers().length;
		}
		
		
		public int[] insertRows(ArrayList<Object[]> rows) throws SQLException {
			String code = generateInsertRowQuery();
			System.out.println("----generated sql----");
			System.out.println(code);
			System.out.println("----generated sql----");
			
			System.out.println("----inserting data into " + mTabelName + " ----");
			int[] success; 
			try (PreparedStatement ps = mDatabase.mConnection.prepareStatement(code)) {
	        	
	        	for(Object[] row : rows) {
	        		System.out.println(Arrays.toString(row));
		            for(int i = 1; i <= row.length; i++) {
		                ps.setObject(i, row[i-1]);
		            }
		            mRowCount += 1;
		            ps.addBatch();
	        	}
	            success = ps.executeBatch();
	        }
			System.out.println("----inserting data into " + mTabelName + " ----");
			
			System.out.println("----successful insertion----");
			System.out.println(Arrays.toString(success));
			setHasData(true);
			System.out.println("----successful insertion----");
	        return success;
		}


		public boolean isEmpty() {
			return this.mHasData == false;
		}
		
		
	}
	
	
	private boolean hasPrimaryKeys(int[] primaryKeys) {
		return primaryKeys != null && primaryKeys.length > 0;
	}
	
	
	private boolean hasColumnIdentifiers(String[] columnIdentifiers) {
		return columnIdentifiers != null && columnIdentifiers.length > 0;
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
	
	public String makeSafeTableName(String str) {
		String safeTableName = makeNameSafe(str);
		return makeUniqueTableName(safeTableName);
	}
	
	public String[] makeColumnIdentifersSafe(String[] columnIdentifiers) {
		//TODO: FIX THIS WEIRD ISSUE
		String[] columnIdentifiersSQL = new String[columnIdentifiers.length];
		for(int i = 0; i < columnIdentifiers.length; i++) {
			columnIdentifiersSQL[i] = makeNameSafe(columnIdentifiers[i]);
			System.out.println(columnIdentifiers[i] + ":" + columnIdentifiersSQL[i]);
		}
		return columnIdentifiersSQL;
	}
	
	
	public String buildCreateTableCode(String tableName, String[] columnIdentifiers, int[] primaryKeys) {
		// assert the column identifiers are the same as the safe ones
		// assert(java.util.Arrays.equals(columnIdentifiers, makeColumnIdentifersSafe(columnIdentifiers)));
		
		int finalColumnIdx = columnIdentifiers.length-1;
		int finalPrimaryKeyIdx = primaryKeys.length-1;
		int i = 0;
		
		// columns
		String query = "CREATE TABLE " + tableName + "(\r\n";
		for(i = 0; i < columnIdentifiers.length-1; i++) {
			query += "\t" + columnIdentifiers[i] + " VARCHAR(8000), \r\n";
		}
		
		query += "\t" + columnIdentifiers[finalColumnIdx] + " VARCHAR(8000)";
        if(hasPrimaryKeys(primaryKeys)) {
        	query += ",";
        }
        query += " \r\n";
        
        // primary keys
        if(hasPrimaryKeys(primaryKeys)) {
        	query += "\tPRIMARY KEY(";
    		for(i = 0; i < primaryKeys.length-1; i++) {
    			query += columnIdentifiers[primaryKeys[i]] + ", ";
    		}
    		query += "\t" + columnIdentifiers[primaryKeys[finalPrimaryKeyIdx]] + ")\r\n";
        }
		query += ")";
		return query;
	}
	
	
	public Table createTable(String tableName, String[] columnIdentifiers, int[] primaryKeys) throws SQLException {
		// generate query
		//assert(hasColumnIdentifiers(columnIdentifiers));
		
		//TODO: implement this
		String safeTableName = makeSafeTableName(tableName);
		String[] safeColumnIdentifiers = makeColumnIdentifersSafe(columnIdentifiers);
		//System.out.println(safeColumnIdentifiers.toString());
		
		String code = buildCreateTableCode(safeTableName, safeColumnIdentifiers, primaryKeys);
		
		System.out.println("----generated sql----");
		System.out.println(code);
		System.out.println("----generated sql----");
		
		Database.Table create = new Database.Table(this, safeTableName, safeColumnIdentifiers);
		
		java.sql.Statement statement = this.mConnection.createStatement();
		boolean success = statement.execute(code);
		System.out.println("Query success: " + Boolean.toString(success));
		statement.close();
		
		this.mTableNames.add(safeTableName);
		return create;
	}
	
	public Table createTable(java.sql.ResultSet results) {
		return null;
	}
	
	public java.sql.ResultSet join(Table a, int a_key, Table b, int b_key, JOIN j) throws SQLException {
		assert(a.mDatabase == b.mDatabase);
		String query = 	  "SELECT * "
				 		+ "FROM " + a.getName() + " \r\n"
				 		+ j.toString() + " JOIN " + b.getName() + " \r\n"
				 		+ "ON " + a.getName() + "." + a.getColumnIdentifier(a_key) + 
				 		  " = " + b.getName() + "." + b.getColumnIdentifier(b_key);
		
		System.out.println("----generated sql query----");
		System.out.println(query);
		System.out.println("----generated sql query----");
		java.sql.Statement statement = this.mConnection.createStatement();
		return statement.executeQuery(query); 
	}
	
	
	private String generateDeleteTablesQuery() {
		String query = "DROP DATABASE " + this.mDatabaseName;
		return query;
	    
	}
	
	
	public void finalize() throws Throwable {
		String query = generateDeleteTablesQuery();
		System.out.println("----generated sql----");
		System.out.println(query);
		System.out.println("----generated sql----");
		
		
		Statement statement = mConnection.createStatement();
		statement.executeUpdate(query);
		statement.close();
		System.out.println("Database " + mDatabaseName + " deleted successfully...");
		
		System.out.println("Object is destroyed by the Garbage Collector");
		System.out.println("Disconnecting");
        String shutdownConnectionString = mDatabaseName + ";shutdown=true";
        DriverManager.getConnection(shutdownConnectionString);
        single_instance = null;
	}

	
	public enum JOIN {
		OUTER, INNER, LEFT, RIGHT, FULL, SELF
	} 

}
