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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import net.sf.jstester.util.Assert;

import org.junit.Test;

import com.gisgraphy.domain.valueobject.Constants;

public class CommentedPropertiesTest {

    private static final String INITIAL_VALUE = "initialValue";
    private static final String INPUT_FILENAME_PATH = "./data/tests/propertyEdition/env.properties";
    private static final String OUTPUT_FILENAME_PATH = "./data/tests/propertyEdition/env2.properties";
    private String key = "myproperty";
    private String value = "changedvalue";

    @Test
    public void loadAndStore() throws Exception {
	CommentedProperties commentedProperties = new CommentedProperties();
	FileInputStream inputStream = new FileInputStream(new File(INPUT_FILENAME_PATH));
	commentedProperties.load(inputStream);

	commentedProperties.setProperty(key, value);
	FileOutputStream outputStream = new FileOutputStream(new File(OUTPUT_FILENAME_PATH));
	commentedProperties.store(outputStream, "#header");

	verify(OUTPUT_FILENAME_PATH);
	Assert.assertTrue("the output filename should be deleted",new File(OUTPUT_FILENAME_PATH).delete());

    }
    
    @Test
    public void editProperty() throws Exception {
	CommentedProperties.editProperty(INPUT_FILENAME_PATH, key, value);
	verify(INPUT_FILENAME_PATH);
	CommentedProperties.editProperty(INPUT_FILENAME_PATH, key, INITIAL_VALUE);
    }
    
    @Test
    public void editPropertyFromClassPath() throws URISyntaxException, FileNotFoundException, UnsupportedEncodingException, IOException{
	CommentedProperties.editPropertyFromClassPathRessource("/classpathtest.properties", key, value);
	URL resourceUrl = this.getClass().getResource("/classpathtest.properties");
	  File  file = new File(resourceUrl.toURI());
	  String path = file.getAbsolutePath();
	  verify(path);
	  CommentedProperties.editPropertyFromClassPathRessource("/classpathtest.properties", key, INITIAL_VALUE);
    }

    protected void verify(String path) throws IOException, FileNotFoundException, UnsupportedEncodingException {
	Properties properties = new Properties();
	properties.load(new FileInputStream(new File(path)));
	String actual = properties.getProperty(key);
	Assert.assertEquals(value, actual);

	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)));
	BufferedReader br = new BufferedReader(new InputStreamReader(bis, Constants.CHARSET));
	Assert.assertTrue("comments should be preserved", br.readLine().startsWith("#"));
	
    }

}
