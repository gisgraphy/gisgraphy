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

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.test.GisgraphyTestHelper;


public class FileHelperTest {

    
    @Test
    public void createTempDirShouldCreateAtempDir(){
	String name = this.getClass().getSimpleName();
	File directory = FileHelper.createTempDir(name);
	Assert.assertTrue("the temporary directory should be a directory", directory.isDirectory());
	Assert.assertTrue("the temporary directory should be writable", directory.canWrite());
	Assert.assertTrue("the temporary directory haven't been created", directory.exists());
	Assert.assertTrue("the temporary directory should contains the specified parameter", directory.getName().contains(name));
	Assert.assertTrue("the tempDir has not been deleted", GisgraphyTestHelper.DeleteNonEmptyDirectory(directory));
    }
}
