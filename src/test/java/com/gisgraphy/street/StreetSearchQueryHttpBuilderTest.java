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
package com.gisgraphy.street;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.geoloc.GeolocQuery;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.gisgraphy.servlet.StreetServlet;
import com.gisgraphy.test.GisgraphyTestHelper;


public class StreetSearchQueryHttpBuilderTest {

	
	 @Test
	    public void streetSearchQueryFromAnHttpServletRequest() {
		    MockHttpServletRequest request = GisgraphyTestHelper
			    .createMockHttpServletRequestForStreetGeoloc();
		    StreetSearchQuery query = buildQuery(request);
		    int firstPaginationIndex =3;
		    assertEquals(firstPaginationIndex, query.getFirstPaginationIndex());
		    assertEquals(StreetSearchQuery.DEFAULT_MAX_RESULTS+firstPaginationIndex-1, query.getLastPaginationIndex());
		    assertEquals("the pagination should be limit to "
			    + StreetSearchQuery.DEFAULT_MAX_RESULTS,
			    StreetSearchQuery.DEFAULT_MAX_RESULTS, query
				    .getMaxNumberOfResults());
		    assertEquals(OutputFormat.XML, query.getOutputFormat());
		    assertEquals(null, query.getOutputLanguage());
		    assertEquals(OutputStyle.getDefault(), query.getOutputStyle());
		    assertEquals(City.class, query.getPlaceType());
		    assertEquals(1.0, query.getLatitude(),0.1);
		    assertEquals(2.0, query.getLongitude(),0.1);
		    assertEquals(10000D, query.getRadius(),0.000001);

		    // test first pagination index
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(GisgraphyServlet.FROM_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.FROM_PARAMETER
			    + " is specified, the parameter should be "
			    + Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
			    .getFirstPaginationIndex());
		    // with a wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.FROM_PARAMETER, "-1");
		    query = buildQuery(request);
		    assertEquals("When a wrong " + GisgraphyServlet.FROM_PARAMETER
			    + " is specified, the parameter should be "
			    + Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
			    .getFirstPaginationIndex());
		    // with a non mumeric value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.FROM_PARAMETER, "a");
		    query = buildQuery(request);
		    assertEquals("When a wrong " + GisgraphyServlet.FROM_PARAMETER
			    + " is specified, the parameter should be "
			    + Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
			    .getFirstPaginationIndex());

		    // test last pagination index
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(GisgraphyServlet.TO_PARAMETER);
		    query = buildQuery(request);
		    // not specify
		    int expectedLastPagination = (StreetSearchQuery.DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
		   	    assertEquals(
			           GisgraphyServlet.TO_PARAMETER
				    + " is wrong when no "+GisgraphyServlet.TO_PARAMETER+" is specified ",
				    expectedLastPagination, query
				    .getLastPaginationIndex());
		   	    
		   	 assertEquals("When no "
				    + GisgraphyServlet.TO_PARAMETER
				    + " is specified, the maxnumberOfResults should be "
				    + StreetSearchQuery.DEFAULT_NB_RESULTS,
				    StreetSearchQuery.DEFAULT_NB_RESULTS, query
					    .getMaxNumberOfResults());
		  // too high
		   	 request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		   	 request.removeParameter(GisgraphyServlet.TO_PARAMETER);
			    request.setParameter(GisgraphyServlet.TO_PARAMETER,StreetSearchQuery.DEFAULT_MAX_RESULTS+100+"");
			    query = buildQuery(request);
			 expectedLastPagination = (StreetSearchQuery.DEFAULT_MAX_RESULTS+query.getFirstPaginationIndex()-1);
			   	    assertEquals("when "+
				           GisgraphyServlet.TO_PARAMETER
					    + " is too high "+GisgraphyServlet.TO_PARAMETER+" should be limited",
					    expectedLastPagination, query
					    .getLastPaginationIndex());
			   	    
			   	 assertEquals("when "+
				           GisgraphyServlet.TO_PARAMETER
					    + " is too high "+GisgraphyServlet.TO_PARAMETER+" should be limited",
					    StreetSearchQuery.DEFAULT_MAX_RESULTS, query
						    .getMaxNumberOfResults()); 
		    // with a wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.TO_PARAMETER, "2");// to<from
		    query = buildQuery(request);
		    expectedLastPagination = (StreetSearchQuery.DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
		    assertEquals( GisgraphyServlet.TO_PARAMETER
			    + " is wrong when wrong "+GisgraphyServlet.TO_PARAMETER+" is specified ",
			    expectedLastPagination, query
				    .getLastPaginationIndex());
		    assertEquals("When a wrong " + GisgraphyServlet.TO_PARAMETER
			    + " is specified, the maxnumberOfResults should  be  "
			    + StreetSearchQuery.DEFAULT_NB_RESULTS,
			    StreetSearchQuery.DEFAULT_NB_RESULTS, query
				    .getMaxNumberOfResults());
		    
		    assertEquals("a wrong to does not change the from value", 3, query
			    .getFirstPaginationIndex());
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    // non numeric
		    request.setParameter(GisgraphyServlet.TO_PARAMETER, "a");// to<from
		    query = buildQuery(request);
		    assertEquals("a wrong to does not change the from value", 3, query
			    .getFirstPaginationIndex());
		    expectedLastPagination = (StreetSearchQuery.DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
		    assertEquals( GisgraphyServlet.TO_PARAMETER
			    + " is wrong when non numeric "+GisgraphyServlet.TO_PARAMETER+" is specified ",
			    expectedLastPagination, query
				    .getLastPaginationIndex());
		    assertEquals("When a wrong " + GisgraphyServlet.TO_PARAMETER
			    + " is specified, the maxnumberOfResults should not be > "
			    + StreetSearchQuery.DEFAULT_NB_RESULTS,
			    StreetSearchQuery.DEFAULT_NB_RESULTS, query
				    .getMaxNumberOfResults());

		    // test indentation
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(GisgraphyServlet.INDENT_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.INDENT_PARAMETER
			    + " is specified, the  parameter should be set to default",Output.DEFAULT_INDENTATION,
			    query.isOutputIndented());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GisgraphyServlet.INDENT_PARAMETER
			    + " is specified, the  parameter should be set to false",Output.DEFAULT_INDENTATION,
			    query.isOutputIndented());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "true");
		    query = buildQuery(request);
		    assertTrue(GisgraphyServlet.INDENT_PARAMETER
			    + " should be case insensitive  ", query.isOutputIndented());
		    // test 'on' value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "oN");
		    query = buildQuery(request);
		    assertTrue(
			    GisgraphyServlet.INDENT_PARAMETER
				    + " should be true for 'on' value (case insensitive and on value)  ",
			    query.isOutputIndented());

		    // test outputFormat
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(GisgraphyServlet.FORMAT_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.FORMAT_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + OutputFormat.getDefault(), OutputFormat.getDefault(),
			    query.getOutputFormat());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GisgraphyServlet.FORMAT_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + OutputFormat.getDefault(), OutputFormat.getDefault(),
			    query.getOutputFormat());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "json");
		    query = buildQuery(request);
		    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
			    + " should be case insensitive  ", OutputFormat.JSON, query
			    .getOutputFormat());
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "unsupported");
		    query = buildQuery(request);
		    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
			    + " should set default if not supported  ", OutputFormat.getDefault(), query
			    .getOutputFormat());
		    
		    // test streeSearchMode
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(StreetServlet.STREET_SEARCH_MODE_PARAMETER);
		    query = buildQuery(request);
		    assertEquals(
			    "When no "
				    + StreetServlet.STREET_SEARCH_MODE_PARAMETER
				    + " is specified, the  parameter should be set to defaultValue",StreetSearchMode.getDefault(), query
				    .getStreetSearchMode());
		   // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetServlet.STREET_SEARCH_MODE_PARAMETER, "unk");
		    query = buildQuery(request);
		    assertEquals(
			    "When wrong "
				    + StreetServlet.STREET_SEARCH_MODE_PARAMETER
				    + " is specified, the  parameter should be set to the default value ",
			    StreetSearchMode.getDefault(),query
				    .getStreetSearchMode());
		    //test with good value 
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetServlet.STREET_SEARCH_MODE_PARAMETER, StreetSearchMode.CONTAINS.toString());
		    query = buildQuery(request);
		    assertEquals(
			    "When good "
				    + StreetServlet.STREET_SEARCH_MODE_PARAMETER
				    + " is specified, the  parameter should be set to the Equivalent streetSearchMode ",StreetSearchMode.CONTAINS,
			    query
				    .getStreetSearchMode());
		  //test case sensitivity
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetServlet.STREET_SEARCH_MODE_PARAMETER, StreetSearchMode.CONTAINS.toString().toLowerCase());
		    query = buildQuery(request);
		    assertEquals(
				     StreetServlet.STREET_SEARCH_MODE_PARAMETER
				    + " should be case sensitive ",StreetSearchMode.CONTAINS,
			    query
				    .getStreetSearchMode());
		    // test streettype
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(StreetSearchQuery.STREETTYPE_PARAMETER);
		    query = buildQuery(request);
		    assertNull(
			    "When no "
				    + StreetSearchQuery.STREETTYPE_PARAMETER
				    + " is specified, the  parameter should be set to null", query
				    .getStreetType());
		   // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.STREETTYPE_PARAMETER, "unk");
		    query = buildQuery(request);
		    assertNull(
			    "When wrong "
				    + StreetSearchQuery.STREETTYPE_PARAMETER
				    + " is specified, the  parameter should be set to null ",
			    query
				    .getStreetType());
		    //test with good value 
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.STREETTYPE_PARAMETER, StreetType.BRIDLEWAY.toString());
		    query = buildQuery(request);
		    assertEquals(
			    "When good "
				    + StreetSearchQuery.STREETTYPE_PARAMETER
				    + " is specified, the  parameter should be set to the Equivalent streettype ",StreetType.BRIDLEWAY,
			    query
				    .getStreetType());
		  //test case sensitivity
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.STREETTYPE_PARAMETER, StreetType.BRIDLEWAY.toString().toLowerCase());
		    query = buildQuery(request);
		    assertEquals(
		    		StreetSearchQuery.STREETTYPE_PARAMETER
				    + " should be case sensitive ",StreetType.BRIDLEWAY,
			    query
				    .getStreetType());
		    
		    // test oneWay
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(StreetSearchQuery.ONEWAY_PARAMETER);
		    query = buildQuery(request);
		    assertNull("When no " + StreetSearchQuery.ONEWAY_PARAMETER
			    + " is specified, the  parameter should be set to null",
			    query.getOneWay());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.ONEWAY_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertNull("When wrong " + StreetSearchQuery.ONEWAY_PARAMETER
			    + " is specified, the  parameter should be set to false",
			    query.getOneWay());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.ONEWAY_PARAMETER, "True");
		    query = buildQuery(request);
		    assertTrue(StreetSearchQuery.ONEWAY_PARAMETER
			    + " should be case insensitive for true ", query.getOneWay());
		    
		 // test With false
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.ONEWAY_PARAMETER, "FaLse");
		    query = buildQuery(request);
		    assertFalse(StreetSearchQuery.ONEWAY_PARAMETER
			    + " should be case insensitive for false  ", query.getOneWay());
		    // test 'on' value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.ONEWAY_PARAMETER, "oN");
		    query = buildQuery(request);
		    assertTrue(
		    		StreetSearchQuery.ONEWAY_PARAMETER
				    + " should be true for 'on' value (case insensitive and on value)  ",
				    query.getOneWay());
		    
		    
		    //name
		    //test With good value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.NAME_PARAMETER, "prefix");
		    query = buildQuery(request);
		    assertEquals(StreetSearchQuery.NAME_PARAMETER+" should be set when specified",request.getParameter(StreetSearchQuery.NAME_PARAMETER), query.getName());
			   
			// empty string
			request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
			request.setParameter(StreetSearchQuery.NAME_PARAMETER, " ");
			query = buildQuery(request);
			assertNull(StreetSearchQuery.NAME_PARAMETER+" should be null when an empty String is specified", query.getName());
			
			// too long string
			request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
			request.setParameter(StreetSearchQuery.NAME_PARAMETER, RandomStringUtils
				.random(StreetSearchQuery.NAME_MAX_LENGTH) + 1);
			try {
			    query = buildQuery(request);
			    fail("Name Prefix must have a maximmum length of "
				    + StreetSearchQuery.NAME_MAX_LENGTH);
			} catch (StreetSearchException e) {
			}
		    
		    
		    // test Point
		    // with missing lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(StreetSearchQuery.LAT_PARAMETER);
		    try {
			query = buildQuery(request);
		    } catch (RuntimeException e) {
		    	fail("lattitude should be optionnal");
		    }
		    // with empty lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LAT_PARAMETER, "");
		    try {
			query = buildQuery(request);
			fail("Even if latitude is optional, it should be valid when specified");
		    } catch (RuntimeException e) {
		    }
		    // With wrong lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LAT_PARAMETER, "a");
		    try {
			query = buildQuery(request);
			fail("A wrong lat should throw");
		    } catch (RuntimeException e) {
		    }
		    // With too small lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LAT_PARAMETER, "-92");
		    try {
			query = buildQuery(request);
			fail("latitude should not accept latitude < -90");
		    } catch (RuntimeException e) {
		    }

		    // With too high lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LAT_PARAMETER, "92");
		    try {
			query = buildQuery(request);
			fail("latitude should not accept latitude > 90");
		    } catch (RuntimeException e) {
		    }

		    // with missing long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(StreetSearchQuery.LONG_PARAMETER);
		    try {
			query = buildQuery(request);
		    } catch (RuntimeException e) {
		    	fail("longitude should be optional");
		    }
		    // with empty long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LONG_PARAMETER, "");
		    try {
			query = buildQuery(request);
			fail("longitude should be valid even if optional");
		    } catch (RuntimeException e) {
		    }
		    // With wrong Long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LONG_PARAMETER, "a");
		    try {
			query = buildQuery(request);
			fail("Even if latitude is optional, it should be valid when specified");
		    } catch (RuntimeException e) {
		    }

		    // with too small long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LONG_PARAMETER, "-182");
		    try {
			query = buildQuery(request);
			fail("longitude should not accept longitude < -180");
		    } catch (RuntimeException e) {
		    }

		    // with too high long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LONG_PARAMETER, "182");
		    try {
			query = buildQuery(request);
			fail("longitude should not accept longitude > 180");
		    } catch (RuntimeException e) {
		    }

		    // with long with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LONG_PARAMETER, "10,3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept longitude with comma", 10.3D,
				query.getLongitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with long with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LONG_PARAMETER, "10.3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept longitude with comma", 10.3D,
				query.getLongitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with lat with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LAT_PARAMETER, "10,3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept latitude with comma", 10.3D,
				query.getLatitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with lat with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.LAT_PARAMETER, "10.3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept latitude with point", 10.3D,
				query.getLatitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // test radius
		    // with missing radius
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.removeParameter(StreetSearchQuery.RADIUS_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + StreetSearchQuery.RADIUS_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + GeolocQuery.DEFAULT_RADIUS, GeolocQuery.DEFAULT_RADIUS,
			    query.getRadius(), 0.1);
		    // With wrong radius
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.RADIUS_PARAMETER, "a");
		    query = buildQuery(request);
		    assertEquals("When wrong " + StreetSearchQuery.RADIUS_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + GeolocQuery.DEFAULT_RADIUS, GeolocQuery.DEFAULT_RADIUS,
			    query.getRadius(), 0.1);
		    // radius with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.RADIUS_PARAMETER, "1,4");
		    query = buildQuery(request);
		    assertEquals("Radius should accept comma as decimal separator",
			    1.4D, query.getRadius(), 0.1);

		    // radius with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForStreetGeoloc();
		    request.setParameter(StreetSearchQuery.RADIUS_PARAMETER, "1.4");
		    query = buildQuery(request);
		    assertEquals("Radius should accept point as decimal separator",
			    1.4D, query.getRadius(), 0.1);
		    
		    
		 // distanceField default value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    query = buildQuery(request);
		    assertTrue("By default distanceField should be true",
			     query.hasDistanceField());
		    
		 // distanceField case insensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.DISTANCE_PARAMETER, "falSE");
		    query = buildQuery(request);
		    assertFalse("distanceField should be set when specified",
			     query.hasDistanceField());
		    
		    // distanceField with off value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.DISTANCE_PARAMETER, "oFF");
		    query = buildQuery(request);
		    assertFalse("distanceField should take off value into account",
				     query.hasDistanceField());
		    
		    // distanceField with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.DISTANCE_PARAMETER, "wrong value");
		    query = buildQuery(request);
		    assertTrue("distanceField should be kept to his default value if specified with wrong value",
				     query.hasDistanceField());
		    
		    //callback not set
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    query = buildQuery(request);
		    assertNull("callback should be null when not set",
			     query.getCallback());
		    
		    //callback set with non alpha value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.CALLBACK_PARAMETER, "doit(");
		    query = buildQuery(request);
		    assertNull("callback should not be set when not alphanumeric",
			     query.getCallback());
		    
		    //callback set with alpha value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.CALLBACK_PARAMETER, "doit");
		    query = buildQuery(request);
		    assertEquals("callback should not be set when alphanumeric",
			     "doit",query.getCallback());
		    
		  //apiKey
			request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
			request.setParameter(GisgraphyServlet.APIKEY_PARAMETER, "apiKey");
			query =buildQuery(request);
			Assert.assertEquals("apiKey", query.getApikey());


	    }

	private StreetSearchQuery buildQuery(MockHttpServletRequest request) {
		return StreetSearchQueryHttpBuilder.getInstance().buildFromHttpRequest(request);
	}
}
