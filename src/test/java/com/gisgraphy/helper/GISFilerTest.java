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

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.test.GisgraphyTestHelper;


public class GISFilerTest {
    
    File tempDir = null;

    @Before
    public void setup(){
	tempDir = FileHelper.createTempDir("gisgzip"+System.currentTimeMillis());
    }
    
    @After
    public void teardown(){
	Assert.assertTrue("The tempDir has not been deleted", GisgraphyTestHelper
		.DeleteNonEmptyDirectory(tempDir));
    }
    
    @Test
    public void untarGzipForGzipExtension() throws IOException{
	GISFiler untar = new GISFiler("./data/tests/gis/test.tar.gz",tempDir);
	untar.process();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarfilegzip.txt").exists());
    }
    
    @Test
    public void untarGzipForGZExtension() throws IOException{
	GISFiler untar = new GISFiler("./data/tests/gis/test.tar.gzip",tempDir);
	untar.process();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarfilegzip.txt").exists());
    }
    
    @Test
    public void manageGis() throws IOException{
	GISFiler gisFiler = new GISFiler("./data/tests/gis/test.gis",tempDir);
	gisFiler.process();
	Assert.assertTrue("",new File(tempDir+File.separator+"testgis").exists());
    }
    
   
    
    @Test (expected=RuntimeException.class)
    public void untarForUnknowCompression() throws IOException{
	GISFiler untar = new GISFiler("./data/tests/gis/test.tar.unknowext",tempDir);
	untar.process();
    }
    
    @Test
    public void untarForNotCompressed() throws IOException{
	GISFiler untar = new GISFiler("./data/tests/gis/test.tar",tempDir);
	untar.process();
	Assert.assertTrue("",new File(tempDir+File.separator+"tarwocompression.txt").exists());
    }
    
}
