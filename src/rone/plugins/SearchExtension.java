package rone.plugins;

import javax.swing.JMenu;

import org.pf4j.ExtensionPoint;

public abstract class SearchExtension implements ExtensionPoint {
	
	private SearchCallback mSearchCallback;
	
	public abstract JMenu getMenu();
	
	final public void setSearchCallback(SearchCallback searchCallback) {
		mSearchCallback = searchCallback;
	}
	
	final public SearchCallback getCallback() {
		return mSearchCallback;
	}
	
}
