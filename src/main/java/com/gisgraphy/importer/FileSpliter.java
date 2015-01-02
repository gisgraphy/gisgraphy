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
package com.gisgraphy.importer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.Constants;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class FileSpliter {

    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(FileSpliter.class);

    public int countLines(String filename) {
	int lines = 0;
	BufferedReader br = null;
	BufferedInputStream bis = null;
	try {
	    bis = new BufferedInputStream(new FileInputStream(filename));
	    br = new BufferedReader(new InputStreamReader(bis, Constants.CHARSET));
	    while (br.readLine() != null) {
		lines++;
	    }
	} catch (Exception e) {
	    logger.warn("can not count lines for " + filename + " : " + e.getMessage(), e);
	} finally {
	    if (bis != null) {
		try {
		    bis.close();
		} catch (IOException e) {

		}
	    }
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {

		}
	    }
	}
	return lines;
    }

    public List<File> SplitByLength(File file, int splitlen) throws FileNotFoundException {
	if (file == null || !file.exists() || !file.isFile()) {
	    throw new FileNotFoundException("File "+file+" must be not null and exists");
	}
	if (splitlen <= 0) {
	    throw new IllegalArgumentException("we can not split file for null or negative length");
	}
	String filename = file.getName();
	 FileInputStream fin=null;
	BufferedReader br = null;
	BufferedInputStream bis = null;
	List<File> splitedFiles = new ArrayList<File>();
	try {
	    int i = 0;
	    fin = new FileInputStream(file.getAbsolutePath());
	    bis = new BufferedInputStream(fin);
	    br = new BufferedReader(new InputStreamReader(bis,
			Constants.CHARSET));
	    String line = br.readLine();
	    int len = 0;
	    while (line != null) {
		String filenameWOExtension = filename;
		String extension = "";
		int index = filename.lastIndexOf('.');
		if (index > 0) {
		    filenameWOExtension = filename.substring(0, index);
		    extension = filename.substring(index+1, (filename.length()));
		}
		String splitedFileName = filenameWOExtension + "." + (i + 1) + "." + extension;

		String outputFileName = file.getParent()+File.separator+splitedFileName;
		logger.info("will split in a new splited file : "+outputFileName);

		File outputFile = new File(outputFileName);
		splitedFiles.add(outputFile);
		BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream(outputFile));
		OutputStreamWriter writer = new OutputStreamWriter(fw);
		while (line != null && len < splitlen) {
		    writer.write(line+"\r\n");
		    line = br.readLine();
		    len++;
		}
		len = 0;
		fw.flush();
		writer.flush();
		writer.close();
		i++;
	    }
	    file.delete();
	    return splitedFiles;
	}

	catch (IOException e) {
	    throw new FileNotFoundException();
	} finally {
	    if (fin!=null){
		try {
		    fin.close();
		} catch (IOException e) {
		}
	    }
	    if (br!=null){
		try {
		    br.close();
		} catch (IOException e) {
		}
	    }
	}
    }
}
