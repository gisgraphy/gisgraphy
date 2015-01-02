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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.domain.repository.GisFeatureDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;

public class DisplayFeatureActionTest {

    DisplayFeatureAction action;
    List<SolrResponseDto> results;
    IFullTextSearchEngine mockSearchEngine;
    FulltextResultsDto mockResultDTO;

    @Before
    public void setup() {
	results = new ArrayList<SolrResponseDto>();
	action = new DisplayFeatureAction();
	mockSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
	action.setFullTextSearchEngine(mockSearchEngine);
	mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
	EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
    }

    @Test
    public void getPreferedNameShouldReturnFullyQualyfiedNameIfExists()
	    throws Exception {
	action.setFeatureId("3");
	String fullyQualifedName = "fully";
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.expect(mockSolrResponseDto.getFeature_id()).andReturn(123456L);
	EasyMock.expect(mockSolrResponseDto.getFully_qualified_name())
		.andReturn(fullyQualifedName);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	action.execute();
	assertEquals(fullyQualifedName, action.getPreferedName());
    }

    @Test
    public void getPreferedNameShouldReturnNameWhenNoFullyQualyfiedNameExists()
	    throws Exception {
	action.setFeatureId("3");
	String fullyQualifedName = "";
	String name = "the name";
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.expect(mockSolrResponseDto.getFeature_id()).andReturn(123456L);
	EasyMock.expect(mockSolrResponseDto.getFully_qualified_name())
		.andReturn(fullyQualifedName);
	EasyMock.expect(mockSolrResponseDto.getName()).andReturn(name);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	action.execute();
	assertEquals(name, action.getPreferedName());
    }

    @Test
    public void executeWithOutFeatureId() throws Exception {
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.execute();
	assertEquals(DisplayFeatureAction.ERROR, returnAction);
	assertEquals(DisplayFeatureAction.ERROR_REF_REQUIRED_FEATURE_ID, action
		.getErrorRef());
    }

    @Test
    public void executeWithNonNumericFeatureId() throws Exception {
	action.setFeatureId("a");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.execute();
	assertEquals(DisplayFeatureAction.ERROR, returnAction);
	assertEquals(DisplayFeatureAction.ERROR_REF_NON_NUMERIC_FEATUREID,
		action.getErrorRef());
    }

    @Test
    public void executeWithNonUniqueResult() throws Exception {
	action.setFeatureId("1");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.execute();
	assertEquals(DisplayFeatureAction.ERROR, returnAction);
	assertEquals(DisplayFeatureAction.ERROR_REF_NON_UNIQUE_RESULT, action
		.getErrorRef());
    }

    @Test
    public void executeWithNoResult() throws Exception {
	action.setFeatureId("1");
	EasyMock.replay(mockResultDTO);
	String errorMessage = "Message";
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andThrow(
		new RuntimeException(errorMessage));
	EasyMock.replay(mockSearchEngine);
	String returnAction = action.execute();
	assertEquals(DisplayFeatureAction.ERROR, returnAction);
	assertEquals(DisplayFeatureAction.ERROR_REF_GENERAL_ERROR, action
		.getErrorRef());
	assertEquals(errorMessage, action.getErrorMessage());
    }

    @Test
    public void execute() throws Exception {
	action.setFeatureId("1");
	SolrResponseDto mockSolrResponseDto = EasyMock
		.createMock(SolrResponseDto.class);
	EasyMock.expect(mockSolrResponseDto.getFeature_id()).andReturn(123456L);
	EasyMock.replay(mockSolrResponseDto);
	results.add(mockSolrResponseDto);
	EasyMock.replay(mockResultDTO);
	EasyMock.expect(
		mockSearchEngine.executeQuery((FulltextQuery) EasyMock
			.anyObject())).andReturn(mockResultDTO);
	EasyMock.replay(mockSearchEngine);
	
	IGisFeatureDao gisFeatureDao = EasyMock.createMock(GisFeatureDao.class);
	EasyMock.expect(gisFeatureDao.getShapeAsWKTByFeatureId(123456L)).andStubReturn("wkt");
	EasyMock.replay(gisFeatureDao);
	action.setGisFeatureDao(gisFeatureDao);
	
	String returnAction = action.execute();
	assertEquals(DisplayFeatureAction.SUCCESS, returnAction);
	assertEquals("", action.getErrorRef());
	assertEquals(mockSolrResponseDto, action.getResult());
	assertEquals("", action.getErrorMessage());
    }

}
