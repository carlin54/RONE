/*
 * RONE
 * Copyright (C) [2021] [Carlin. R. Connell]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
