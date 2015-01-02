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
package com.gisgraphy.fulltext;

import static com.gisgraphy.fulltext.FulltextQuery.DEFAULT_MAX_RESULTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.spell.SpellCheckerConfig;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.servlet.FulltextServlet;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.gisgraphy.test.GisgraphyTestHelper;


public class FulltextQueryHttpBuilderTest {

    @Test
    public void testFulltextQueryFromAnHttpServletRequest() {
    	
    	MockHttpServletRequest request = GisgraphyTestHelper
		.createMockHttpServletRequestForFullText();
    	FulltextQuery query = buildQuery(request);
    	
    	
    	 // test Point
	    // with empty lat
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LAT_PARAMETER, "");
	    try {
		query = buildQuery(request);
	    } catch (RuntimeException e) {
		fail("When there is empty latitude, query should throw");
	    }
	    // With wrong lat
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LAT_PARAMETER, "a");
	    try {
		query = buildQuery(request);
		fail("A null lat should throw");
	    } catch (RuntimeException e) {
	    }
	    // With too small lat
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LAT_PARAMETER, "-92");
	    try {
		query = buildQuery(request);
		fail("latitude should not accept latitude < -90");
	    } catch (RuntimeException e) {
	    }

	    // With too high lat
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LAT_PARAMETER, "92");
	    try {
		query = buildQuery(request);
		fail("latitude should not accept latitude > 90");
	    } catch (RuntimeException e) {
	    }

	    // with empty long
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LONG_PARAMETER, "");
	    try {
		query = buildQuery(request);
	    } catch (RuntimeException e) {
		fail("When there is empty longitude, query should throw");
	    }
	    // With wrong Long
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LONG_PARAMETER, "a");
	    try {
		query = buildQuery(request);
		fail("A null lat should throw");
	    } catch (RuntimeException e) {
	    }

	    // with too small long
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LONG_PARAMETER, "-182");
	    try {
		query = buildQuery(request);
		fail("longitude should not accept longitude < -180");
	    } catch (RuntimeException e) {
	    }

	    // with too high long
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LONG_PARAMETER, "182");
	    try {
		query = buildQuery(request);
		fail("longitude should not accept longitude > 180");
	    } catch (RuntimeException e) {
	    }

	    // with long with comma
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LONG_PARAMETER, "10,3");
	    try {
		query = buildQuery(request);
		Assert.assertEquals(
			"request should accept longitude with comma", 10.3D,
			query.getLongitude().doubleValue(), 0.1);

	    } catch (RuntimeException e) {
	    }

	    // with long with point
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LONG_PARAMETER, "10.3");
	    try {
		query = buildQuery(request);
		Assert.assertEquals(
			"request should accept longitude with comma", 10.3D,
			query.getLongitude().doubleValue(), 0.1);

	    } catch (RuntimeException e) {
	    }

	    // with lat with comma
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LAT_PARAMETER, "10,3");
	    try {
		query = buildQuery(request);
		Assert.assertEquals(
			"request should accept latitude with comma", 10.3D,
			query.getLatitude().doubleValue(), 0.1);

	    } catch (RuntimeException e) {
	    }

	    // with lat with point
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.LAT_PARAMETER, "10.3");
	    try {
		query = buildQuery(request);
		Assert.assertEquals(
			"request should accept latitude with point", 10.3D,
			query.getLatitude().doubleValue(), 0.1);

	    } catch (RuntimeException e) {
	    }

	    // test radius
	    // with missing radius
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.removeParameter(FulltextQuery.RADIUS_PARAMETER);
	    query = buildQuery(request);
	    assertEquals("When no " + FulltextQuery.RADIUS_PARAMETER
		    + " is specified, the  parameter should be set to  "
		    + FulltextQuery.DEFAULT_RADIUS, FulltextQuery.DEFAULT_RADIUS,
		    query.getRadius(), 0.1);
	    // With wrong radius
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.RADIUS_PARAMETER, "a");
	    query = buildQuery(request);
	    assertEquals("When wrong " + FulltextQuery.RADIUS_PARAMETER
		    + " is specified, the  parameter should be set to  "
		    + FulltextQuery.DEFAULT_RADIUS, FulltextQuery.DEFAULT_RADIUS,
		    query.getRadius(), 0.1);
	    // radius with comma
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.RADIUS_PARAMETER, "1,4");
	    query = buildQuery(request);
	    assertEquals("Radius should accept comma as decimal separator",
		    1.4D, query.getRadius(), 0.1);

	    // radius with point
	    request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	    request.setParameter(FulltextQuery.RADIUS_PARAMETER, "1.4");
	    query = buildQuery(request);
	    assertEquals("Radius should accept point as decimal separator",
		    1.4D, query.getRadius(), 0.1);
	    
    	
	request = GisgraphyTestHelper
		.createMockHttpServletRequestForFullText();
    query = buildQuery(request);
	int firstPaginationIndex = 3;
	assertEquals(firstPaginationIndex, query.getFirstPaginationIndex());
	assertEquals(DEFAULT_MAX_RESULTS+firstPaginationIndex-1, query.getLastPaginationIndex());
	assertEquals("the pagination should be limit to "
		+ DEFAULT_MAX_RESULTS,
		DEFAULT_MAX_RESULTS, query
			.getMaxNumberOfResults());
	assertEquals("FR", query.getCountryCode());
	assertEquals(OutputFormat.XML, query.getOutputFormat());
	assertEquals("FR", query.getOutputLanguage());
	assertEquals(OutputStyle.FULL, query.getOutputStyle());
	assertEquals(City.class, query.getPlaceTypes()[0]);
	assertEquals("query", query.getQuery());
	
	//test trim
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextServlet.FROM_PARAMETER, " "+request.getParameter(FulltextQuery.QUERY_PARAMETER)+" ");
	query = buildQuery(request);
	Assert.assertTrue("query parameter shoud be trimed",!query.getQuery().endsWith(" "));
	Assert.assertTrue("query parameter shoud be trimed",!query.getQuery().startsWith(" "));

	// test first pagination index
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextServlet.FROM_PARAMETER);
	query = buildQuery(request);
	assertEquals("When no " + FulltextServlet.FROM_PARAMETER
		+ " is specified, the parameter should be "
		+ Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
		.getFirstPaginationIndex());
	// with a wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextServlet.FROM_PARAMETER, "-1");
	query = buildQuery(request);
	assertEquals("When a wrong " + FulltextServlet.FROM_PARAMETER
		+ " is specified, the parameter should be "
		+ Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
		.getFirstPaginationIndex());
	// with a non mumeric value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextServlet.FROM_PARAMETER, "a");
	query = buildQuery(request);
	assertEquals("When a wrong " + FulltextServlet.FROM_PARAMETER
		+ " is specified, the parameter should be "
		+ Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
		.getFirstPaginationIndex());

	// test last pagination index
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextServlet.TO_PARAMETER);
	query = buildQuery(request);
	 int expectedLastPagination = (FulltextQuery.DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
	    assertEquals(
	           GisgraphyServlet.TO_PARAMETER
		    + " is wrong when no "+GisgraphyServlet.TO_PARAMETER+" is specified ",
		    expectedLastPagination, query
		    .getLastPaginationIndex());
		
	// with too high value specified
		request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
		request.removeParameter(FulltextServlet.TO_PARAMETER);
		request.setParameter(FulltextServlet.TO_PARAMETER, "100");
		query = buildQuery(request);
		expectedLastPagination = (FulltextQuery.DEFAULT_MAX_RESULTS+query.getFirstPaginationIndex()-1);
		    assertEquals(
		           GisgraphyServlet.TO_PARAMETER
			    + " is too high, to should be limited ",
			    expectedLastPagination, query
			    .getLastPaginationIndex());
		assertEquals(
			"When no "
				+ FulltextServlet.TO_PARAMETER
				+ " is specified, the  parameter should be set to limit results to "
				+ FulltextQuery.DEFAULT_MAX_RESULTS,
				FulltextQuery.DEFAULT_MAX_RESULTS, query
				.getMaxNumberOfResults());
	
	
	
	// with a wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextServlet.TO_PARAMETER, "2");// to<from
	query = buildQuery(request);
	 expectedLastPagination = (FulltextQuery.DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
	    assertEquals( GisgraphyServlet.TO_PARAMETER
		    + " is wrong when wrong "+GisgraphyServlet.TO_PARAMETER+" is specified ",
		    expectedLastPagination, query
			    .getLastPaginationIndex());
	assertEquals("When a wrong " + FulltextServlet.TO_PARAMETER
		+ " is specified, the number of results should be default nb results",
		FulltextQuery.DEFAULT_NB_RESULTS, query.getMaxNumberOfResults());
	assertEquals("a wrong to does not change the from value", 3, query
		.getFirstPaginationIndex());
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	//non numeric
	request.setParameter(FulltextServlet.TO_PARAMETER, "a");
	query = buildQuery(request);
	expectedLastPagination = (FulltextQuery.DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
	assertEquals( GisgraphyServlet.TO_PARAMETER
		    + " is wrong when non numeric "+GisgraphyServlet.TO_PARAMETER+" is specified ",
		    expectedLastPagination, query
			    .getLastPaginationIndex());
	assertEquals("a wrong to does not change the from value", 3, query
		.getFirstPaginationIndex());
	assertEquals("When a wrong " + FulltextServlet.TO_PARAMETER
		+ " is specified, the numberOf results should be "
		+ FulltextQuery.DEFAULT_NB_RESULTS,
		FulltextQuery.DEFAULT_NB_RESULTS, query.getMaxNumberOfResults());

	// test countrycode
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.COUNTRY_PARAMETER);
	query = buildQuery(request);
	Assert.assertNull("When no " + FulltextQuery.COUNTRY_PARAMETER
		+ " is specified, the parameter should be set to null", query
		.getCountryCode());
	// with a wrong value
	// can not have a wrong value=>always a string

	// test outputFormat
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextServlet.FORMAT_PARAMETER);
	query = buildQuery(request);
	assertEquals("When no " + FulltextServlet.FORMAT_PARAMETER
		+ " is specified, the  parameter should be set to  "
		+ OutputFormat.getDefault(), OutputFormat.getDefault(), query
		.getOutputFormat());
	// with wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextServlet.FORMAT_PARAMETER, "UNK");
	query = buildQuery(request);
	assertEquals("When wrong " + FulltextServlet.FORMAT_PARAMETER
		+ " is specified, the  parameter should be set to  "
		+ OutputFormat.getDefault(), OutputFormat.getDefault(), query
		.getOutputFormat());
	// test case sensitive
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextServlet.FORMAT_PARAMETER, "json");
	query = buildQuery(request);
	assertEquals(FulltextServlet.FORMAT_PARAMETER
		+ " should be case insensitive  ", OutputFormat.JSON, query
		.getOutputFormat());
	//with unsupported value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "unsupported");
    query = buildQuery(request);
    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
	    + " should set default if not supported  ", OutputFormat.getDefault(), query
	    .getOutputFormat());
	// test language
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.LANG_PARAMETER);
	query = buildQuery(request);
	assertNull(FulltextQuery.LANG_PARAMETER
		+ " should be null when not specified  ", query
		.getOutputLanguage());
	// with empty string
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.LANG_PARAMETER, " ");
	query = buildQuery(request);
	assertEquals(FulltextQuery.LANG_PARAMETER
		+ " should be null when not specified  ",
		Output.DEFAULT_LANGUAGE_CODE, query.getOutputLanguage());

	// test uppercase
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.LANG_PARAMETER, "fr");
	query = buildQuery(request);
	assertEquals(FulltextQuery.LANG_PARAMETER + " should be uppercase  ",
		"FR", query.getOutputLanguage());

	// test placetype
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.PLACETYPE_PARAMETER);
	query = buildQuery(request);
	assertNull("When no " + FulltextQuery.PLACETYPE_PARAMETER
		+ " is specified, the  parameter should be set null ", query
		.getPlaceTypes());
	// with wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.PLACETYPE_PARAMETER);
	request.setParameter(FulltextQuery.PLACETYPE_PARAMETER, "unk");
	query = buildQuery(request);
	assertNull("When wrong " + FulltextQuery.PLACETYPE_PARAMETER
		+ " is specified, the  parameter should be set null ", query
		.getPlaceTypes()[0]);
	// test case sensitive
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.PLACETYPE_PARAMETER, "ciTy");
	query = buildQuery(request);
	assertEquals(FulltextQuery.PLACETYPE_PARAMETER
		+ " should be case insensitive  ", City.class, query
		.getPlaceTypes()[0]);
	
	// test with multipleplacetype
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.PLACETYPE_PARAMETER, new String[]{"city","adm"});
	query = buildQuery(request);
	assertEquals(FulltextQuery.PLACETYPE_PARAMETER
		+ " should accept several placetype  ",2, query
		.getPlaceTypes().length);
	List<Class<?>> placetypeList = Arrays.asList(query.getPlaceTypes());

	assertTrue(FulltextQuery.PLACETYPE_PARAMETER
			+ " should accept several placetype  ", placetypeList.contains(City.class));
	assertTrue(FulltextQuery.PLACETYPE_PARAMETER
			+ " should accept several placetype  ", placetypeList.contains(Adm.class));
	
	// test with multipleplacetype with wrong values
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.PLACETYPE_PARAMETER, new String[]{"city","unk"});
	query = buildQuery(request);
	assertEquals(FulltextQuery.PLACETYPE_PARAMETER
		+ " should accept several placetype  ",2, query
		.getPlaceTypes().length);
	placetypeList = Arrays.asList(query.getPlaceTypes());

	assertTrue(FulltextQuery.PLACETYPE_PARAMETER
			+ " should accept several placetype  ", placetypeList.contains(City.class));
	assertTrue(FulltextQuery.PLACETYPE_PARAMETER
			+ " should accept several placetype  ", placetypeList.contains(null));

	// test output style
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.STYLE_PARAMETER);
	query = buildQuery(request);
	assertEquals("When no " + FulltextQuery.STYLE_PARAMETER
		+ " is specified, the  parameter should be set to  "
		+ OutputStyle.getDefault(), OutputStyle.getDefault(), query
		.getOutputStyle());
	// with wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.STYLE_PARAMETER, "UNK");
	query = buildQuery(request);
	assertEquals("When wrong " + FulltextQuery.STYLE_PARAMETER
		+ " is specified, the  parameter should be set to  "
		+ OutputStyle.getDefault(), OutputStyle.getDefault(), query
		.getOutputStyle());
	// test case sensitive
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.STYLE_PARAMETER, "medium");
	query = buildQuery(request);
	assertEquals(FulltextQuery.STYLE_PARAMETER
		+ " should be case insensitive  ", OutputStyle.MEDIUM, query
		.getOutputStyle());

	// test indentation
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(GisgraphyServlet.INDENT_PARAMETER);
	query = buildQuery(request);
	assertEquals("When no " + GisgraphyServlet.INDENT_PARAMETER
		+ " is specified, the  parameter should be set to default", Output.DEFAULT_INDENTATION
		,query.isOutputIndented());
	// with wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "UNK");
	query = buildQuery(request);
	assertEquals("When wrong " + GisgraphyServlet.INDENT_PARAMETER
		+ " is specified, the  parameter should be set to default",Output.DEFAULT_INDENTATION,
		query.isOutputIndented());
	// test case sensitive
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "True");
	query = buildQuery(request);
	assertTrue(GisgraphyServlet.INDENT_PARAMETER
		+ " should be case insensitive  ", query.isOutputIndented());
	// test with on value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "oN");
	query = buildQuery(request);
	assertTrue(
		GisgraphyServlet.INDENT_PARAMETER
			+ " should be true for 'on' value (case insensitive and on value)  ",
		query.isOutputIndented());
	
	// test suggest
		// with no value specified
		request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
		request.removeParameter(FulltextQuery.SUGGEST_PARAMETER);
		query = buildQuery(request);
		assertEquals("When no " + FulltextQuery.SUGGEST_PARAMETER
			+ " is specified, the  parameter should be set to default", false
			,query.isSuggest());
		// with wrong value
		request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
		request.setParameter(FulltextQuery.SUGGEST_PARAMETER, "UNK");
		query = buildQuery(request);
		assertEquals("When wrong " + FulltextQuery.SUGGEST_PARAMETER
			+ " is specified, the  parameter should be set to default",false,
			query.isSuggest());
		// test case sensitive
		request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
		request.setParameter(FulltextQuery.SUGGEST_PARAMETER, "True");
		query = buildQuery(request);
		assertTrue(FulltextQuery.SUGGEST_PARAMETER
			+ " should be case insensitive  ", query.isSuggest());
		// test with on value
		request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
		request.setParameter(FulltextQuery.SUGGEST_PARAMETER, "oN");
		query = buildQuery(request);
		assertTrue(
				FulltextQuery.SUGGEST_PARAMETER
				+ " should be true for 'on' value (case insensitive and on value)  ",
			query.isSuggest());
		
	
	
	// test allwordsRequired
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER);
	query = buildQuery(request);
	assertEquals("When no " + FulltextQuery.ALLWORDSREQUIRED_PARAMETER
		+ " is specified, the  parameter should be set to default"
		,FulltextQuery.ALL_WORDS_REQUIRED_DEFAULT_OPTION,query.isAllwordsRequired());
	// with wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER, "UNK");
	query = buildQuery(request);
	assertEquals("When wrong " + FulltextQuery.ALLWORDSREQUIRED_PARAMETER
		+ " is specified, the  parameter should be set to default",
		FulltextQuery.ALL_WORDS_REQUIRED_DEFAULT_OPTION,
		query.isAllwordsRequired());
	// test case sensitive
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER, "False");
	query = buildQuery(request);
	assertFalse(FulltextQuery.ALLWORDSREQUIRED_PARAMETER
		+ " should be case insensitive  ", query.isAllwordsRequired());
	// test with on value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.ALLWORDSREQUIRED_PARAMETER, "oN");
	query = buildQuery(request);
	assertTrue(
			FulltextQuery.ALLWORDSREQUIRED_PARAMETER
			+ " should be true for 'on' value (case insensitive and on value)  ",
		query.isAllwordsRequired());
	
	
	// test spellchecking
	// with no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.SPELLCHECKING_PARAMETER);
	query = buildQuery(request);
	assertEquals("When no " + FulltextQuery.SPELLCHECKING_PARAMETER
		+ " is specified, the  parameter should be the default one",SpellCheckerConfig.activeByDefault, query
		.hasSpellChecking());
	// with wrong value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.SPELLCHECKING_PARAMETER, "UNK");
	query = buildQuery(request);
	assertEquals("When wrong " + FulltextQuery.SPELLCHECKING_PARAMETER
		+ " is specified, the  parameter should be set to the default one",SpellCheckerConfig.activeByDefault, query
		.hasSpellChecking());
	// test case sensitive
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.SPELLCHECKING_PARAMETER, String.valueOf(!SpellCheckerConfig.activeByDefault).toUpperCase());
	query = buildQuery(request);
	assertEquals(FulltextQuery.SPELLCHECKING_PARAMETER
		+ " should be case insensitive  ", !SpellCheckerConfig.activeByDefault, query.hasSpellChecking());
	// test with on value
	
	boolean savedSpellCheckingValue = SpellCheckerConfig.activeByDefault;
	try {
		SpellCheckerConfig.activeByDefault = false;
		request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
		request.setParameter(FulltextQuery.SPELLCHECKING_PARAMETER, "oN");
		query = buildQuery(request);
		assertTrue(
				FulltextQuery.SPELLCHECKING_PARAMETER
				+ " should be true for 'on' value (case insensitive and on value)  ",
			query.hasSpellChecking());
	} catch (RuntimeException e) {
	    Assert.fail(e.getMessage());
	} finally {
	    //reset the last value
		SpellCheckerConfig.activeByDefault = savedSpellCheckingValue;
	}
	
	//apiKey
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(GisgraphyServlet.APIKEY_PARAMETER, "apiKey");
	query =buildQuery(request);
	Assert.assertEquals("apiKey", query.getApikey());

	// test query
	//test with good value
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	query = buildQuery(request);
	assertEquals("query should be set when specified",request.getParameter(FulltextQuery.QUERY_PARAMETER), query.getQuery());
	
	
	// With no value specified
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.removeParameter(FulltextQuery.QUERY_PARAMETER);
	try {
	    query = buildQuery(request);
	    fail("A null query should throw");
	} catch (RuntimeException e) {
	}
	// empty string
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.QUERY_PARAMETER, " ");
	try {
	    query = buildQuery(request);
	    fail("An empty query should throw");
	} catch (RuntimeException e) {
	}
	// too long string
	request = GisgraphyTestHelper.createMockHttpServletRequestForFullText();
	request.setParameter(FulltextQuery.QUERY_PARAMETER, RandomStringUtils
		.random(FulltextQuery.QUERY_MAX_LENGTH) + 1);
	try {
	    query = buildQuery(request);
	    fail("query must have a maximmum length of "
		    + FulltextQuery.QUERY_MAX_LENGTH);
	} catch (RuntimeException e) {
	}

    }

	private FulltextQuery buildQuery(MockHttpServletRequest request) {
		return FulltextQueryHttpBuilder.getInstance().buildFromRequest(request);
	}
	
}
