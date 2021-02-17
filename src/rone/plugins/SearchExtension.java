package rone.plugins;

import java.util.ArrayList;

import org.pf4j.ExtensionPoint;

public interface SearchExtension extends ExtensionPoint {
	
	public String getTitle();

	public String[] getColumnIdentifers();

	public int getSearchSize();

	public int getThreadPoolSize();

	public int getSearchTimeoutDuration();

	public ArrayList<Object[]> search(String[] columnIdentifers, ArrayList<Object[]> tableSelection);
	
}
