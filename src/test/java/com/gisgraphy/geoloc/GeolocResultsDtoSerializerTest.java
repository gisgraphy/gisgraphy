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
package com.gisgraphy.geoloc;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.serializer.exception.UnsupportedFormatException;
import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GeolocResultsDtoSerializerTest {
    
    Map<String, Object> extraParameter = new HashMap<String, Object>(){
	{
	    put(GeolocResultsDtoSerializer.START_PAGINATION_INDEX_EXTRA_PARAMETER, 1);
	}
    };
    
   

    @Test
    public void testSerializeShouldThrowIfTheformatIsNotSupported() {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	try {
	    geolocResultsDtoSerializer.serialize(new ByteArrayOutputStream(),
		    OutputFormat.UNSUPPORTED, new GeolocResultsDto(),true,extraParameter);
	    fail();
	} catch (UnsupportedFormatException e) {
	    //ok
	}
    }
    
    @Test
    public void testSerializeShouldSerializeInXML() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.XML, geolocResultsDto,true,extraParameter);
	    FeedChecker.checkGeolocResultsDtoJAXBMapping(geolocResultsDto, 
		    byteArrayOutputStream.toString(Constants.CHARSET));
    }
    
    @Test
    public void testSerializeShouldSerializeInJSON() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.JSON, geolocResultsDto,true,extraParameter);
	    String ResultAsString = byteArrayOutputStream.toString(Constants.CHARSET);
	    FeedChecker.checkGeolocResultsDtoJSON(geolocResultsDto, 
		    ResultAsString);
    }
    
    @Test
    public void testSerializeShouldSerializeInGEORSS() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.GEORSS, geolocResultsDto,true,extraParameter);
	    FeedChecker.checkGeolocResultsDtoGEORSS(geolocResultsDto, 
		    byteArrayOutputStream.toString(Constants.CHARSET));
    }
    
    @Test
    public void testSerializeShouldSerializeInATOM() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.ATOM, geolocResultsDto,true,extraParameter);
	    FeedChecker.checkGeolocResultsDtoATOM(geolocResultsDto, 
		    byteArrayOutputStream.toString(Constants.CHARSET));
    }
    
    @Test
    public void testSerializeShouldSerializeInPHP() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.PHP, geolocResultsDto,false,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    @Test
    public void testSerializeShouldSerializeInYAML() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.YAML, geolocResultsDto,false,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    @Test
    public void testSerializeShouldSerializeInPython() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.PYTHON, geolocResultsDto,false,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    @Test
    public void testSerializeShouldSerializeInRuby() throws UnsupportedEncodingException {
	IGeolocResultsDtoSerializer geolocResultsDtoSerializer = new GeolocResultsDtoSerializer();
	    GeolocResultsDto geolocResultsDto = GisgraphyTestHelper.createGeolocResultsDto(310L);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    geolocResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.RUBY, geolocResultsDto,false,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    
    

}
