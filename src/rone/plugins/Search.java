package rone.plugins;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Search {
	private String mTitle;
	private String[] mColumnIdentifers; 
	
	private int mMaximumSearchSize;
	private int mThreadPoolSize;
	
	private Object[] mSelectionRequests;
	
	public Search(String title, String[] columnIdentifiers){
		mTitle = title;
		mColumnIdentifers = columnIdentifiers;
		mMaximumSearchSize = 1;
		mThreadPoolSize = 1;
		mSelectionRequests = null;
	}
	
	public String getTitle() 										{ return mTitle;	}
	public String[] getColumnIdentifers()							{ return mColumnIdentifers; }
	public int getMaximumSearchSize() 								{ return mMaximumSearchSize; };
	public int getThreadPoolSize() 									{ return mThreadPoolSize; };
	public void setSearchSize(int searchSize) 						{ mMaximumSearchSize = (searchSize > 0) ? searchSize : 1;};
	public void setThreadPoolSize(int threadPoolSize) 				{ mThreadPoolSize = (threadPoolSize > 0) ? threadPoolSize : 1;};
	public void setSearchRequests(Object[] selection)				{ mSelectionRequests = selection; }
	public void setUniqueSearchRequests(Object[] selection)			{ mSelectionRequests = Arrays.stream(selection).distinct().toArray(Object[]::new);; }
	public Object[] getSelectionRequests()							{ return mSelectionRequests; }
	
	
	public abstract ArrayList<Object[]> getSearchResults(Object[] selection);
	
}
