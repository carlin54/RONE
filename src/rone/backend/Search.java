package rone.backend;

public class Search {
	private SearchInterface mSearchInterface;
	private String[] mSearchInterfaceRequests;
	
	public SearchInterface getSearchInterface() 		{	return mSearchInterface;		}
	public String[] getSearchInterfaceRequests() 		{	return mSearchInterfaceRequests;}
	
	public Search(	SearchInterface searchInterface, 
			String[] searchInterfaceRequests)
	{
		this.mSearchInterface = searchInterface;
		this.mSearchInterfaceRequests = searchInterfaceRequests;
	}
}