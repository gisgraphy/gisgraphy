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
package com.gisgraphy.street;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.geoloc.GeolocResultsDtoSerializer;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.serializer.exception.UnsupportedFormatException;
import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;

public class StreetSearchResultsDtoSerializerTest {
    
    Map<String, Object> extraParameter = new HashMap<String, Object>(){
	{
	    put(GeolocResultsDtoSerializer.START_PAGINATION_INDEX_EXTRA_PARAMETER, 1);
	}
    };

    
    @Test
    public void serializeshouldThrowAnUnsupportedFormatExceptionWhenFormatIsNotSupported(){
	StreetSearchResultsDtoSerializer serializer = new StreetSearchResultsDtoSerializer();
	try {
	    serializer.serialize(new ByteArrayOutputStream(), OutputFormat.UNSUPPORTED, new StreetSearchResultsDto(), false, extraParameter);
	    fail("An UnsupportedFormatException should be throw when the format is not supported");
	} catch (UnsupportedFormatException e) {
	   //ok
	}
    }
    
    @Test
    public void serializeShouldSerializeInXML() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	    StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.XML, streetSearchResultsDto,true,extraParameter);
	    FeedChecker.checkStreetSearchResultsDtoJAXBMapping(streetSearchResultsDto, 
		    byteArrayOutputStream);
    }

    @Test
    public void serializeShouldSerializeInJSON() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	    StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.JSON, streetSearchResultsDto,true,extraParameter);
	    FeedChecker.checkStreetSearchResultsDtoJSON(streetSearchResultsDto, 
		    byteArrayOutputStream.toString(Constants.CHARSET));
    }
   
    @Test
    public void serializeShouldSerializeInGEORSS() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	   StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.GEORSS, streetSearchResultsDto,true,extraParameter);
	    FeedChecker.checkStreetSearchResultsDtoGEORSS(streetSearchResultsDto, 
		    byteArrayOutputStream.toString(Constants.CHARSET));
    }
    
    @Test
    public void serializeShouldSerializeInATOM() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	   StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.ATOM, streetSearchResultsDto,true,extraParameter);
	    FeedChecker.checkStreetSearchResultsDtoATOM(streetSearchResultsDto, 
		    byteArrayOutputStream.toString(Constants.CHARSET));
    }
    
    @Test
    public void serializeShouldSerializeInPHP() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	   StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.PHP, streetSearchResultsDto,true,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    @Test
    public void serializeShouldSerializeInYAML() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	   StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.YAML, streetSearchResultsDto,true,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    @Test
    public void serializeShouldSerializeInPython() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	   StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.PYTHON, streetSearchResultsDto,true,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
    @Test
    public void serializeShouldSerializeInRuby() throws UnsupportedEncodingException {
	IStreetSearchResultsDtoSerializer streetSearchResultsDtoSerializer = new StreetSearchResultsDtoSerializer();
	   StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    streetSearchResultsDtoSerializer.serialize(byteArrayOutputStream,
		    OutputFormat.RUBY, streetSearchResultsDto,true,extraParameter);
	    String feed = byteArrayOutputStream.toString(Constants.CHARSET);
	    Assert.assertFalse(feed.toLowerCase().contains("xml"));
	    System.out.println(feed);
    }
    
}
