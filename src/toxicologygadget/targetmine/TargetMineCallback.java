package toxicologygadget.targetmine;

import toxicologygadget.filemanager.DataTable;

public interface TargetMineCallback {
	public void completeSearch(DataTable results); 
	public void unsuccessfulSearch(String error);
}