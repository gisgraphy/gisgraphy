/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/
package com.gisgraphy.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * Create a filter that take a file and remove all the line that contain some
 * specific string
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class FileLineFilter {

    private String[] stringToFilter;

    /**
     * @param stringToFilter
     *            All the lines that contains the word or sentence will be
     *            filtered
     */
    public FileLineFilter(String[] stringToFilter) {
	this.stringToFilter = stringToFilter;
    }

    /**
     * 
     * All the lines that contains the word or sentence of the filter will be
     * deleted, the result file will be written in the destFile
     * 
     * @param originalFile
     *            The file to filter
     * @param destFile
     *            The filtered file <br/>
     *            note : if the destination file does not exists, it will be
     *            created.
     */
    public void filter(File originalFile, File destFile) {
	if (originalFile == null) {
	    throw new IllegalArgumentException("can not check a null file");
	}
	if (!originalFile.exists()) {
	    throw new IllegalArgumentException("can not check a file that does not exists");
	}
	if (!originalFile.isFile()) {
	    throw new IllegalArgumentException("can only check file, not directory");
	}
	if (destFile == null) {
	    throw new IllegalArgumentException("can not write to a null file");
	}
	if (!destFile.exists()) {
	    try {
		if (!destFile.createNewFile()) {
		    throw new RuntimeException("Can not create file " + destFile.getAbsolutePath());
		}
	    } catch (IOException e) {
		throw new RuntimeException("Can not create file " + destFile.getAbsolutePath(), e);
	    }
	}
	if (!destFile.isFile()) {
	    throw new IllegalArgumentException("can only write to a file, not to a directory ");
	}
	BufferedWriter out = null;
	InputStream stream = null;
	DataInputStream in = null;
	try {
	    stream = new BufferedInputStream(new FileInputStream(originalFile));
	    in = new DataInputStream(stream);
	    out = new BufferedWriter(new FileWriter(destFile));
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    // Read File Line By Line
	    while ((strLine = br.readLine()) != null) {
		boolean shouldBeFiltered = false;
		for (int i = 0; i < stringToFilter.length; i++) {
		    if (strLine.contains(stringToFilter[i])) {
			shouldBeFiltered = true;
			break;
		    }
		}
		if (!shouldBeFiltered) {
		    out.write(strLine);
		    out.write("\r\n");
		}
	    }
	} catch (Exception e) {// Catch exception if any
	    throw new RuntimeException("an exception has occured durind filtering of file " + originalFile.getAbsolutePath() + " to " + destFile.getAbsolutePath(), e);
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		}
	    }
	    if (stream != null) {
		try {
		    stream.close();
		} catch (IOException e) {
		}
	    }
	    if (out != null) {
		try {
		    out.flush();
		    out.close();
		} catch (IOException e) {
		}
	    }
	}
    }

}
