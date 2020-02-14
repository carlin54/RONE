package toxicologygadget.query;

import toxicologygadget.filemanager.DataTable;

public interface QueryThreadCallback {
	public void completeSearch(DataTable results); 
	public void unsuccessfulSearch(String error);
	public void status(int complete, int unsuccessful, int total);
}