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

import com.gisgraphy.domain.repository.IAlternateNameDao;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.fulltext.spell.SpellCheckerConfig;
import com.gisgraphy.helper.OutputFormatHelper;

public class FulltextSearchActionTest {

    FulltextSearchAction action;
    List<SolrResponseDto> results;
    IFullTextSearchEngine mockSearchEngine;
    FulltextResultsDto mockResultDTO;

    @Before
    public void setup() {
	results = new ArrayList<SolrResponseDto>();
	action = new FulltextSearchAction();
	mockSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	action.setFullTextSearchEngine(mockSearchEngine);
	mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
	BaseActionTestCase.setUpActionContext();
    }

    @Test
    public void isDisplayResults() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter("q", "Paris");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	results.add(mockSolrResponseDto);
	EasyMock.expect(mockSolrResponseDto.getLat()).andStubReturn(3D);
	EasyMock.expect(mockSolrResponseDto.getLng()).andStubReturn(4D);
	mockSolrResponseDto.setYahoo_map_url(EasyMock.anyObject(String.class));
	mockSolrResponseDto.setGoogle_map_url(EasyMock.anyObject(String.class));
	mockSolrResponseDto.setOpenstreetmap_map_url(EasyMock.anyObject(String.class));
	EasyMock.replay(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
	assertTrue(action.isDisplayResults());
    }

    @Test
    public void search() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter("q", "Paris");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.expect(mockSolrResponseDto.getLat()).andStubReturn(3D);
	EasyMock.expect(mockSolrResponseDto.getLng()).andStubReturn(4D);
	mockSolrResponseDto.setYahoo_map_url(EasyMock.anyObject(String.class));
	mockSolrResponseDto.setGoogle_map_url(EasyMock.anyObject(String.class));
	mockSolrResponseDto.setOpenstreetmap_map_url(EasyMock.anyObject(String.class));
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
    }

    @Test
    public void searchShouldSetErrorMessageWhenFail() throws Exception {
	String errorMessage = "message";
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter("q", "Paris");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andThrow(
		new RuntimeException(errorMessage));
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertNull(action.getResponseDTO());
	assertEquals(errorMessage, action.getErrorMessage());
	assertFalse(action.isDisplayResults());
    }

    @Test
    public void searchPopupShouldReturnPopupView() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter("q", "Paris");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.expect(mockSolrResponseDto.getLat()).andStubReturn(3D);
	EasyMock.expect(mockSolrResponseDto.getLng()).andStubReturn(4D);
	mockSolrResponseDto.setYahoo_map_url(EasyMock.anyObject(String.class));
	mockSolrResponseDto.setGoogle_map_url(EasyMock.anyObject(String.class));
	mockSolrResponseDto.setOpenstreetmap_map_url(EasyMock.anyObject(String.class));
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.searchpopup();
	assertEquals(FulltextSearchAction.POPUP_VIEW, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
    }

    @Test
    public void getStyleShouldReturnDefaultStyle() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/search.html");
	ServletActionContext.setRequest(request);
	request.setParameter("q", "Paris");
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.search();
	assertEquals(FulltextSearchAction.SUCCESS, returnAction);
	assertEquals(mockResultDTO, action.getResponseDTO());
	assertEquals(OutputStyle.getDefault().toString(), action.getStyle());
    }

    @Test
    public void getFormatsShouldReturnFormatForFullText() {
	Assert.assertEquals(Arrays.asList(OutputFormatHelper
		.listFormatByService(GisgraphyServiceType.FULLTEXT)), Arrays
		.asList(action.getFormats()));

    }
    
    @Test
    public void isSpellCheckingShouldHaveDefaultValue() {
	boolean savedvalue = SpellCheckerConfig.activeByDefault;
	try {
	    SpellCheckerConfig.activeByDefault = false;
	    FulltextSearchAction action = new FulltextSearchAction();
	    assertFalse(action.isSpellchecking());
	    SpellCheckerConfig.activeByDefault = true;
	    action = new FulltextSearchAction();
	    assertTrue(action.isSpellchecking());
	} finally{
	    SpellCheckerConfig.activeByDefault = savedvalue;
	}

    }
    
    @Test
    public void getLanguages(){
	FulltextSearchAction fulltextSearchAction = new FulltextSearchAction();	
	IAlternateNameDao mockAlternateNameDao = EasyMock.createMock(IAlternateNameDao.class);
	List<String> fakeUsedlanguages =new ArrayList<String>();
	fakeUsedlanguages.add("FR");
	fakeUsedlanguages.add("DE");
	EasyMock.expect(mockAlternateNameDao.getUsedLanguagesCodes()).andReturn(fakeUsedlanguages);
	EasyMock.replay(mockAlternateNameDao);
	fulltextSearchAction.setAlternateNameDao(mockAlternateNameDao);
	
	List<String> usedLanguaged =  fulltextSearchAction.getLanguages();
	Assert.assertEquals(fakeUsedlanguages, usedLanguaged);
	usedLanguaged =  fulltextSearchAction.getLanguages();
	Assert.assertEquals(fakeUsedlanguages, usedLanguaged);
	EasyMock.verify(mockAlternateNameDao);
    }


}
