
package rone.backend;

import java.nio.file.Path;
import java.util.ArrayList;

public interface SearchInterface {
	
	public String getTitle();
	public String getIconLocation();
	public String[] getColumnIdentifers();
	public int[] getPrimaryKeys();
	public int getWorkSize();
	public int getThreadPoolSize();

	public ArrayList<Object[]> query(String[] searchData);
}