package rone.plugins;


public interface SearchCallback {

	public abstract void startSearch(Search search);
	
	public abstract Selection getSelection();
	
}

