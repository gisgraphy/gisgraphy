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
/**
 *
 */
package com.gisgraphy.domain.valueobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.junit.Test;

import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;

public class StreetSearchResultsDtoTest {

    
    @Test
    public void constructor(){
	 StreetDistance streetDistance = GisgraphyTestHelper.createStreetDistance();
	 Long qtime = 300L;
	 List<StreetDistance> list = new ArrayList<StreetDistance>();
	 list.add(streetDistance);
	 String queryString = "query";
	StreetSearchResultsDto streetSearchResultsDto = new StreetSearchResultsDto(list,qtime,queryString);
	 assertEquals("The QTime is not well set",qtime, streetSearchResultsDto.getQTime());
	 assertEquals("The query is not well set",queryString, streetSearchResultsDto.getQuery());
	 assertEquals("The list of results is not well set",list, streetSearchResultsDto.getResult());
    }
    
    @Test
    public void streetSearchResultsDtoShouldBeMappedWithJAXB() {
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(StreetSearchResultsDto.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    StreetSearchResultsDto streetSearchResultsDto = GisgraphyTestHelper.createStreetSearchResultsDto();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(streetSearchResultsDto, outputStream);
	    FeedChecker.checkStreetSearchResultsDtoJAXBMapping(streetSearchResultsDto, outputStream);
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    public void streetSearchResultsDtoForEmptyListShouldreturnValidXML() {
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(StreetSearchResultsDto.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    List<StreetDistance> list = new ArrayList<StreetDistance>();
	    long qTime = 200L;
	    String query = "query";
	    StreetSearchResultsDto streetSearchResultsDto = new StreetSearchResultsDto(list, qTime,query);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(streetSearchResultsDto, outputStream);
	    try {
		FeedChecker.assertQ(
			"streetSearchResultsDto for an Empty List should return valid XML",
			outputStream.toString(Constants.CHARSET), "/"
				+ Constants.STREETSEARCHRESULTSDTO_JAXB_NAME + "",
			"/" + Constants.STREETSEARCHRESULTSDTO_JAXB_NAME
				+ "/numFound[.='0']", "/"
				+ Constants.STREETSEARCHRESULTSDTO_JAXB_NAME
				+ "/QTime[.='" + qTime + "']");
	    } catch (UnsupportedEncodingException e) {
		fail("unsupported encoding exception for " + Constants.CHARSET);
	    }
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	}
    }

   
   

}
