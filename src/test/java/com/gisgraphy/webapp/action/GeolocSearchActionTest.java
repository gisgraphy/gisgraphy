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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.geocoloc.IGeolocSearchEngine;
import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.geoloc.GeolocResultsDto;
import com.gisgraphy.helper.OutputFormatHelper;

public class GeolocSearchActionTest {

    GeolocSearchAction action;
    List<GisFeatureDistance> results;
    IGeolocSearchEngine mockSearchEngine;
    GeolocResultsDto mockResultDTO;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
	BaseActionTestCase.setUpActionContext();
	results = new ArrayList<GisFeatureDistance>();
	action = new GeolocSearchAction();
	mockSearchEngine = EasyMock.createMock(IGeolocSearchEngine.class);
	action.setGeolocSearchEngine(mockSearchEngine);
	mockResultDTO = EasyMock.createMock(GeolocResultsDto.class);
	EasyMock.expect(mockResultDTO.getResult()).andReturn(results);
    }

    @Test
    public void search() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter(GeolocQuery.LAT_PARAMETER.toString(), "3.5");
	request.setParameter(GeolocQuery.LONG_PARAMETER.toString(), "4.5");
	GisFeatureDistance mockGisFeatureDistance = EasyMock
		.createMock(GisFeatureDistance.class);
	EasyMock.replay(mockGisFeatureDistance);
	results.add(mockGisFeatureDistance);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((GeolocQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
    }

    @Test
    public void isDisplayResults() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter(GeolocQuery.LAT_PARAMETER.toString(), "3.5");
	request.setParameter(GeolocQuery.LONG_PARAMETER.toString(), "4.5");
	GisFeatureDistance mockGisFeatureDistance = EasyMock
		.createMock(GisFeatureDistance.class);
	EasyMock.replay(mockGisFeatureDistance);
	results.add(mockGisFeatureDistance);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((GeolocQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
	assertTrue(action.isDisplayResults());
    }

    @Test
    public void searchWhenFailed() throws Exception {
	String errorMessage = "message";
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter(GeolocQuery.LAT_PARAMETER.toString(), "3.5");
	request.setParameter(GeolocQuery.LONG_PARAMETER.toString(), "4.5");
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((GeolocQuery) EasyMock
			.anyObject())).andThrow(
		new RuntimeException(errorMessage));
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertEquals(errorMessage, action.getErrorMessage());
	assertNull(action.getResponseDTO());
	assertFalse(action.isDisplayResults());
    }

    @Test
    public void searchPopupShouldReturnPopupView() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter(GeolocQuery.LAT_PARAMETER.toString(), "3.5");
	request.setParameter(GeolocQuery.LONG_PARAMETER.toString(), "4.5");
	GisFeatureDistance mockGisFeatureDistance = EasyMock
		.createMock(GisFeatureDistance.class);
	EasyMock.replay(mockGisFeatureDistance);
	results.add(mockGisFeatureDistance);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((GeolocQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.searchpopup();
	assertEquals(FulltextSearchAction.POPUP_VIEW, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
    }

    @Test
    public void getStyleShouldReturnStyle() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter(GeolocQuery.LAT_PARAMETER.toString(), "3.5");
	request.setParameter(GeolocQuery.LONG_PARAMETER.toString(), "4.5");
	action.setPlacetype("GisFeature");
	assertEquals("GisFeature", action.getPlacetype());
    }

    @Test
    public void getStyleShouldReturnDefaultStyle() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter(GeolocQuery.LAT_PARAMETER.toString(), "3.5");
	request.setParameter(GeolocQuery.LONG_PARAMETER.toString(), "4.5");
	action.setPlacetype(null);
	assertEquals(GisgraphyConfig.defaultGeolocSearchPlaceTypeClass, action
		.getPlacetype());
    }

    @Test
    public void getFormatsShouldReturnFormatForGeoloc() {
	Assert.assertEquals(Arrays.asList(OutputFormatHelper
		.listFormatByService(GisgraphyServiceType.GEOLOC)), Arrays
		.asList(action.getFormats()));

    }

}
