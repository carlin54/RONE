package rone.garuda;

import java.io.File;

public class DiscoveryTouple {
	private String fileFormat;
	private File fileToDiscover ;
	
	public DiscoveryTouple(String fileFormat, File fileToDiscover) {
		super();
		this.fileFormat = fileFormat;
		this.fileToDiscover = fileToDiscover;
	}

	public String getFileFormat() {
		return fileFormat;
	}
	
	public File getFileToDiscover() {
		return fileToDiscover;
	}
	
	
}
