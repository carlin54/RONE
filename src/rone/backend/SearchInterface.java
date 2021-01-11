
package rone.backend;

import java.nio.file.Path;
import java.util.ArrayList;

public interface SearchInterface {
	
	public String title();
	public String iconLocation();
	public int workSize();
	public int numberOfWorkers();
	public ArrayList<ArrayList<Object>> query(String[] searchData);
	
}
