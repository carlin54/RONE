package toxicologygadget.query;

import java.util.ArrayList;

import toxicologygadget.filemanager.Table;

public interface QueryThreadCallback {
	
	public void completeSearch(Table results, int statusCode); 
	public void statusUpdate(int complete, int total, int totalFound);
	public static int statusCodeFinishSuccess = 1;
	public static int statusCodeFinishStopped = 2;
	public static int statusCodeFinishUnsuccess = 3;
	public void startSearch(int total);
}