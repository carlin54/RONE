package toxicologygadget.query;

import toxicologygadget.filemanager.Table;

public interface QueryThreadCallback {
	public void completeSearch(Table results); 
	public void unsuccessfulSearch(String error);
	public void status(int complete, int unsuccessful, int total);
}