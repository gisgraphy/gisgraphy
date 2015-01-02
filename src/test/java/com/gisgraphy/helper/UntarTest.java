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

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.test.GisgraphyTestHelper;


public class UntarTest {
    
    File tempDir = null;

    @Before
    public void setup(){
	tempDir = FileHelper.createTempDir("targzip"+System.currentTimeMillis());
    }
    
    @After
    public void teardown(){
	Assert.assertTrue("The tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));
    }
    
    @Test
    public void untarGzipForGzipExtension() throws IOException{
	Untar untar = new Untar("./data/tests/tar/test.tar.gz",tempDir);
	untar.untar();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarfilegzip.txt").exists());
    }
    
    @Test
    public void untarGzipForGZExtension() throws IOException{
	Untar untar = new Untar("./data/tests/tar/test.tar.gzip",tempDir);
	untar.untar();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarfilegzip.txt").exists());
    }
    
    @Test
    public void untarBzip2ForBZ2Extension() throws IOException{
	Untar untar = new Untar("./data/tests/tar/test.tar.bz2",tempDir);
	untar.untar();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarfilebz2.txt").exists());
    }
    
    @Test
    public void untarBzip2ForBzip2Extension() throws IOException{
	Untar untar = new Untar("./data/tests/tar/test.tar.bzip2",tempDir);
	untar.untar();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarfilebz2.txt").exists());
    }
    
    @Test (expected=RuntimeException.class)
    public void untarForUnknowCompression() throws IOException{
	Untar untar = new Untar("./data/tests/tar/test.tar.unknowext",tempDir);
	untar.untar();
    }
    
    @Test
    public void untarForNotCompressed() throws IOException{
	Untar untar = new Untar("./data/tests/tar/test.tar",tempDir);
	untar.untar();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarwocompression.txt").exists());
    }
    
}
