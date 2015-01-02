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
package com.gisgraphy.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.test.GisgraphyTestHelper;


public class FileSpliterTest {
    
    FileSpliter fileSpliter = new FileSpliter();
    
    @Test
    public void splitBylength() throws IOException{
	File fileToSplit = File.createTempFile(System.currentTimeMillis()+"", "filename.txt");
	GisgraphyTestHelper.copyfile("./data/tests/split/31lines.txt", fileToSplit.getAbsolutePath());
	File file = new File(fileToSplit.getAbsolutePath());
	List<File> splitedFiles = fileSpliter.SplitByLength(file, 10);
	Assert.assertNotNull(splitedFiles);
	Assert.assertEquals(4, splitedFiles.size());
	for (int i = 0; i<splitedFiles.size()-1;i++){
		Assert.assertEquals("file "+splitedFiles.get(i).getAbsolutePath()+" has not the expected number of lines",10, fileSpliter.countLines(splitedFiles.get(i).getAbsolutePath()));
	}
	Assert.assertEquals(1, fileSpliter.countLines(splitedFiles.get(splitedFiles.size()-1).getAbsolutePath()));
	Assert.assertTrue(!fileToSplit.exists());
	fileToSplit.deleteOnExit();
    }
    
    
    @Test
    public void splitBylengthNumberOfLinesInferiorToSplitLength() throws IOException{
	File fileToSplit = File.createTempFile(System.currentTimeMillis()+"", "filename.txt");
	GisgraphyTestHelper.copyfile("./data/tests/split/8lines.txt", fileToSplit.getAbsolutePath());
	File file = new File(fileToSplit.getAbsolutePath());
	List<File> splitedFiles = fileSpliter.SplitByLength(file, 10);
	Assert.assertNotNull(splitedFiles);
	Assert.assertEquals(1, splitedFiles.size());
	Assert.assertEquals(8, fileSpliter.countLines(splitedFiles.get(splitedFiles.size()-1).getAbsolutePath()));
	Assert.assertFalse(fileToSplit.getAbsolutePath().equals(splitedFiles.get(splitedFiles.size()-1).getAbsolutePath()));
	Assert.assertTrue(!fileToSplit.exists());
	fileToSplit.deleteOnExit();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void splitBylengthNegativeOrNullLength() throws IOException{
	File fileToSplit = File.createTempFile(System.currentTimeMillis()+"", "filename.txt");
	GisgraphyTestHelper.copyfile("./data/tests/split/31lines.txt", fileToSplit.getAbsolutePath());
	File file = new File(fileToSplit.getAbsolutePath());
	fileSpliter.SplitByLength(file,0);
	fileToSplit.deleteOnExit();
    }
    
    @Test(expected=FileNotFoundException.class)
    public void splitBylengthFileNotFound() throws IOException{
	File file = new File("fileNotExists.txt");
	List<File> splitedFiles = fileSpliter.SplitByLength(file, 10);
	Assert.assertNotNull(splitedFiles);
	Assert.assertEquals(0, splitedFiles.size());
    }
    
    @Test(expected=FileNotFoundException.class)
    public void splitBylengthDirectory() throws IOException{
	List<File> splitedFiles = fileSpliter.SplitByLength(File.createTempFile("todelete", ".txt").getParentFile(), 10);
	Assert.assertNotNull(splitedFiles);
	Assert.assertEquals(0, splitedFiles.size());
    }
    
    @Test
    public void splitEmptyfile() throws IOException{
	File fileToSplit = File.createTempFile(System.currentTimeMillis()+"", "filename.txt");
	GisgraphyTestHelper.copyfile("./data/tests/split/empty.txt", fileToSplit.getAbsolutePath());
	File file = new File(fileToSplit.getAbsolutePath());
	List<File> splitedFiles = fileSpliter.SplitByLength(file, 10);
	Assert.assertNotNull(splitedFiles);
	Assert.assertEquals(0, splitedFiles.size());
	fileToSplit.deleteOnExit();
	
    }

}
