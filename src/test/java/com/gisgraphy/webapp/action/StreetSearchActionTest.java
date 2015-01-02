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
package com.gisgraphy.webapp.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.street.IStreetSearchEngine;
import com.gisgraphy.street.StreetSearchQuery;
import com.gisgraphy.street.StreetSearchQueryHttpBuilder;
import com.gisgraphy.street.StreetType;


public class StreetSearchActionTest {
    
    @Test
    public void isDisplayResultsShouldReturnTrueIfThereIsSomeResults(){
	StreetSearchAction streetSearchAction = new StreetSearchAction();
	Assert.assertFalse("isDisplayResults sould be false if there is no results to display",streetSearchAction.isDisplayResults());
    }

    @Test
    public void getStreetTypesShouldReturnStreetTypeEnumValues(){
	StreetSearchAction streetSearchAction = new StreetSearchAction();
	Assert.assertArrayEquals(StreetType.values(),streetSearchAction.getStreetTypes());
    }
    
    @Test
    public void searchpopupShouldReturnCorrectView() throws Exception{
	final MockHttpServletRequest request = new MockHttpServletRequest();
	      request.addParameter(StreetSearchQuery.LAT_PARAMETER, "3.2");
	      request.addParameter(StreetSearchQuery.LONG_PARAMETER, "1.5");
	
	StreetSearchAction streetSearchAction = new StreetSearchAction(){
	    @Override
	    protected HttpServletRequest getRequest() {
	      
	      return request;
	    }
	};
	IStreetSearchEngine streetSearchEngine = EasyMock.createMock(IStreetSearchEngine.class);
	EasyMock.expect(streetSearchEngine.executeQuery(StreetSearchQueryHttpBuilder.getInstance().buildFromHttpRequest(request))).andReturn(new StreetSearchResultsDto());
	EasyMock.replay(streetSearchEngine);
	
	streetSearchAction.setStreetSearchEngine(streetSearchEngine);
	Assert.assertEquals(SearchAction.POPUP_VIEW,streetSearchAction.searchpopup());
    }
    
    @Test
    public void searchShouldReturnCorrectView() throws Exception{
	final MockHttpServletRequest request = new MockHttpServletRequest();
	      request.addParameter(StreetSearchQuery.LAT_PARAMETER, "3.2");
	      request.addParameter(StreetSearchQuery.LONG_PARAMETER, "1.5");
	
	StreetSearchAction streetSearchAction = new StreetSearchAction(){
	    @Override
	    protected HttpServletRequest getRequest() {
	      
	      return request;
	    }
	};
	IStreetSearchEngine streetSearchEngine = EasyMock.createMock(IStreetSearchEngine.class);
	EasyMock.expect(streetSearchEngine.executeQuery(StreetSearchQueryHttpBuilder.getInstance().buildFromHttpRequest(request))).andReturn(new StreetSearchResultsDto());
	EasyMock.replay(streetSearchEngine);
	
	streetSearchAction.setStreetSearchEngine(streetSearchEngine);
	Assert.assertEquals(SearchAction.SUCCESS,streetSearchAction.search());
    }
    
    @Test
    public void searchShouldSetErrorMassageIfAnErrorOccured() throws Exception{
	final MockHttpServletRequest request = new MockHttpServletRequest();
	      request.addParameter(StreetSearchQuery.LAT_PARAMETER, "3.2");
	      request.addParameter(StreetSearchQuery.LONG_PARAMETER, "1.5");
	
	StreetSearchAction streetSearchAction = new StreetSearchAction(){
	    @Override
	    protected HttpServletRequest getRequest() {
	      
	      return request;
	    }
	};
	IStreetSearchEngine streetSearchEngine = EasyMock.createMock(IStreetSearchEngine.class);
	String errorMessage = "errorMessage";
	EasyMock.expect(streetSearchEngine.executeQuery((StreetSearchQuery)EasyMock.anyObject())).andThrow(new RuntimeException(errorMessage));
	EasyMock.replay(streetSearchEngine);
	
	streetSearchAction.setStreetSearchEngine(streetSearchEngine);
	Assert.assertEquals(SearchAction.SUCCESS,streetSearchAction.search());
	Assert.assertEquals(errorMessage,streetSearchAction.getErrorMessage());
    }
    
    @Test
    public void getNameOptionsShouldReturnCorrectValues(){
	StreetSearchAction streetSearchAction = new StreetSearchAction(){
	    @Override
	    public String getText(String textName) {
	        if ("search.street.includeNoNameStreet".equals(textName)){
	            return "includeValue";
	        }
	        if ("search.street.dont.includeNoNameStreet".equals(textName)){
	            return "notincludeValue";
	        }
	        return null;
	    }
	};
	
	Map<String, String> nameOptions = streetSearchAction.getNameOptions();
	Assert.assertEquals("includeValue", nameOptions.get(""));
	Assert.assertEquals("notincludeValue", nameOptions.get("%"));
    }
    
    

}
