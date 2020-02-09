package toxicologygadget.query;

import toxicologygadget.filemanager.DataTable;

public interface QueryThreadCallback {
	public void completeSearch(DataTable results); 
	public void unsuccessfulSearch(String error);
}