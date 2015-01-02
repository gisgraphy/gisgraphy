/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.helper;

import static com.gisgraphy.test.GisgraphyTestHelper.isFileContains;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import junit.framework.Assert;

import org.junit.Test;



public class FileLineFilterTest {
   @Test
public void testFilter() throws Exception{
    	File tempDir = FileHelper.createTempDir(this.getClass().getSimpleName());
	File file = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "fileToFilter.sql");
	File filteredFile = new File(tempDir.getAbsolutePath() + System.getProperty("file.separator") + "filteredFile.sql");
	FileOutputStream fos = null;
	OutputStreamWriter out = null;
	try {
	    fos = new FileOutputStream(file);
	    out = new OutputStreamWriter(fos, "UTF-8");

	    out.write(" sentence with filterd word number 1\r\n");
	    out.write("sentence with  second word number 2\r\n");
	    out.write("good sentence\r\n");
	    out.write("nothing\r\n");
	    out.flush();
	} finally {
	    try {
		if (fos != null) {
		    fos.flush();
		    fos.close();
		}
		if (out != null) {
		    out.flush();
		    out.close();
		}
	    } catch (Exception ignore) {
		// ignore
	    }
	}

	FileLineFilter filter = new FileLineFilter(new String[]{"filterd word", " second word"});
	filter.filter(file, filteredFile);
	Assert.assertTrue("The output file should have been created",filteredFile.exists());
	Assert.assertFalse(isFileContains(filteredFile, "second word"));
	Assert.assertFalse(isFileContains(filteredFile, "filterd word"));
	Assert.assertTrue(isFileContains(filteredFile, "nothing"));
	Assert.assertTrue(isFileContains(filteredFile, "good sentence"));
	file.delete();
	filteredFile.delete();
	tempDir.delete();
	
	
	
}

}
