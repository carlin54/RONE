package toxicologygadget.query;

import toxicologygadget.filemanager.Database;

public interface QueryThreadCallback {
	public void completeSearch(Database results); 
	public void unsuccessfulSearch(String error);
	public void status(int complete, int unsuccessful, int total);
}