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
package com.gisgraphy.geoloc;

import static com.gisgraphy.geoloc.GeolocQuery.DEFAULT_MAX_RESULTS;
import static com.gisgraphy.geoloc.GeolocQuery.DEFAULT_NB_RESULTS;
import static com.gisgraphy.geoloc.GeolocQuery.MUNICIPALITY_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.servlet.GeolocServlet;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.gisgraphy.street.StreetSearchQuery;
import com.gisgraphy.test.GisgraphyTestHelper;


public class GeolocQueryHttpBuilderTest {
	
	   @Test
	    public void testBuildFromAnHttpServletRequest() {
		Class<?> savedDefaultType = GisgraphyConfig.defaultGeolocSearchPlaceTypeClass;
		try {
		    GisgraphyConfig.defaultGeolocSearchPlaceTypeClass = Country.class;
		    MockHttpServletRequest request = GisgraphyTestHelper
			    .createMockHttpServletRequestForGeoloc();
		    GeolocQuery query = buildQuery(request);
		    int firstPaginationIndex = 3;
		    assertEquals(firstPaginationIndex, query.getFirstPaginationIndex());
		    assertEquals(DEFAULT_MAX_RESULTS+firstPaginationIndex-1, query.getLastPaginationIndex());
		    assertEquals("the pagination should be limit to "
			    + DEFAULT_MAX_RESULTS,
			    DEFAULT_MAX_RESULTS, query
				    .getMaxNumberOfResults());
		    assertEquals(OutputFormat.XML, query.getOutputFormat());
		    assertEquals(null, query.getOutputLanguage());
		    assertEquals(OutputStyle.getDefault(), query.getOutputStyle());
		    assertEquals(City.class, query.getPlaceType());
		    Assert.assertEquals(new Double(1.0), query.getLatitude());
		    assertEquals(new Double(2.0), query.getLongitude());
		    assertEquals(10000D, query.getRadius(),0.001);

		    // test first pagination index
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GisgraphyServlet.FROM_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.FROM_PARAMETER
			    + " is specified, the parameter should be "
			    + Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
			    .getFirstPaginationIndex());
		    // with a wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FROM_PARAMETER, "-1");
		    query = buildQuery(request);
		    assertEquals("When a wrong " + GisgraphyServlet.FROM_PARAMETER
			    + " is specified, the parameter should be "
			    + Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
			    .getFirstPaginationIndex());
		    // with a non mumeric value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FROM_PARAMETER, "a");
		    query = buildQuery(request);
		    assertEquals("When a wrong " + GisgraphyServlet.FROM_PARAMETER
			    + " is specified, the parameter should be "
			    + Pagination.DEFAULT_FROM, Pagination.DEFAULT_FROM, query
			    .getFirstPaginationIndex());

		    // test last pagination index
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GisgraphyServlet.TO_PARAMETER);
		    query = buildQuery(request);
		    // non specify
		    int expectedLastPagination = (DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
	   	    assertEquals(
		           GisgraphyServlet.TO_PARAMETER
			    + " is wrong when no "+GisgraphyServlet.TO_PARAMETER+" is specified ",
			    expectedLastPagination, query
			    .getLastPaginationIndex());
		    assertEquals(
			    "When no "
				    + GisgraphyServlet.TO_PARAMETER
				    + " is specified, the  parameter should be set to "
				    + DEFAULT_NB_RESULTS,
				    DEFAULT_NB_RESULTS, query
				    .getMaxNumberOfResults());
		    // with a wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.TO_PARAMETER, "2");// to<from
		    query = buildQuery(request);
		    expectedLastPagination = (DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
		    assertEquals( GisgraphyServlet.TO_PARAMETER
			    + " is wrong when wrong "+GisgraphyServlet.TO_PARAMETER+" is specified ",
			    expectedLastPagination, query
				    .getLastPaginationIndex());
		    assertEquals("When a wrong " + GisgraphyServlet.TO_PARAMETER
			    + " is specified, the number Of results should be "
			    + DEFAULT_NB_RESULTS,
			    DEFAULT_NB_RESULTS, query
				    .getMaxNumberOfResults());
		    assertEquals("a wrong to does not change the from value", 3, query
			    .getFirstPaginationIndex());
		    //too high
			    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
			    request.removeParameter(GisgraphyServlet.TO_PARAMETER);
			    request.setParameter(GisgraphyServlet.TO_PARAMETER,StreetSearchQuery.DEFAULT_MAX_RESULTS+100+"");
			    query = buildQuery(request);
			    expectedLastPagination = (DEFAULT_MAX_RESULTS+query.getFirstPaginationIndex()-1);
			    assertEquals( GisgraphyServlet.TO_PARAMETER
				    + " is wrong when wrong "+GisgraphyServlet.TO_PARAMETER+" is specified ",
				    expectedLastPagination, query
					    .getLastPaginationIndex());
			    assertEquals("When too high " + GisgraphyServlet.TO_PARAMETER
				    + " is specified, the number Of results should be "
				    + DEFAULT_MAX_RESULTS,
				    DEFAULT_MAX_RESULTS, query
					    .getMaxNumberOfResults());
			    assertEquals("a wrong to does not change the from value", 3, query
				    .getFirstPaginationIndex());
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    // non numeric
		    request.setParameter(GisgraphyServlet.TO_PARAMETER, "a");// to<from
		    query = buildQuery(request);
		    assertEquals("a wrong to does not change the from value", 3, query
			    .getFirstPaginationIndex());
		    expectedLastPagination = (DEFAULT_NB_RESULTS+query.getFirstPaginationIndex()-1);
		    assertEquals( GisgraphyServlet.TO_PARAMETER
			    + " is wrong when non numeric "+GisgraphyServlet.TO_PARAMETER+" is specified ",
			    expectedLastPagination, query
				    .getLastPaginationIndex());
		    assertEquals("When a wrong " + GisgraphyServlet.TO_PARAMETER
			    + " is specified, the numberOf results should be "
			    + DEFAULT_NB_RESULTS,
			    DEFAULT_NB_RESULTS, query
				    .getMaxNumberOfResults());

		    // test indentation
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GisgraphyServlet.INDENT_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GeolocServlet.INDENT_PARAMETER
			    + " is specified, the  parameter should be set to default",Output.DEFAULT_INDENTATION,
			    query.isOutputIndented());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocServlet.INDENT_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GeolocServlet.INDENT_PARAMETER
			    + " is specified, the  parameter should be set to default",Output.DEFAULT_INDENTATION,
			    query.isOutputIndented());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocServlet.INDENT_PARAMETER, "tRue");
		    query = buildQuery(request);
		    assertTrue(GeolocServlet.INDENT_PARAMETER
			    + " should be case insensitive  ", query.isOutputIndented());
		    // test 'on' value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "oN");
		    query = buildQuery(request);
		    assertTrue(
			    GisgraphyServlet.INDENT_PARAMETER
				    + " should be true for 'on' value (case insensitive and on value)  ",
			    query.isOutputIndented());
		    
		    
		    
		 // test municipality
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(MUNICIPALITY_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + MUNICIPALITY_PARAMETER
			    + " is specified, the  parameter should be set to default",false,
			    query.hasMunicipalityFilter());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(MUNICIPALITY_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + MUNICIPALITY_PARAMETER
			    + " is specified, the  parameter should be set to default",false,
			    query.hasMunicipalityFilter());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(MUNICIPALITY_PARAMETER, "tRue");
		    query = buildQuery(request);
		    assertTrue(MUNICIPALITY_PARAMETER
			    + " should be case insensitive  ", query.hasMunicipalityFilter());
		    // test 'on' value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(MUNICIPALITY_PARAMETER, "oN");
		    query = buildQuery(request);
		    assertTrue(
		    		MUNICIPALITY_PARAMETER
				    + " should be true for 'on' value (case insensitive and on value)  ",
			    query.hasMunicipalityFilter());

		    // test outputFormat
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GisgraphyServlet.FORMAT_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.FORMAT_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + OutputFormat.getDefault(), OutputFormat.getDefault(),
			    query.getOutputFormat());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GisgraphyServlet.FORMAT_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + OutputFormat.getDefault(), OutputFormat.getDefault(),
			    query.getOutputFormat());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "json");
		    query = buildQuery(request);
		    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
			    + " should be case insensitive  ", OutputFormat.JSON, query
			    .getOutputFormat());
		    // test with unsupported value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "unsupported");
		    query = buildQuery(request);
		    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
			    + " should set default if not supported  ", OutputFormat.getDefault(), query
			    .getOutputFormat());

		    // test placetype
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GeolocQuery.PLACETYPE_PARAMETER);
		    query = buildQuery(request);
		    assertEquals(
			    "When no "
				    + GeolocQuery.PLACETYPE_PARAMETER
				    + " is specified, the  parameter should be set to defaultGeolocSearchPlaceType ",
			    GisgraphyConfig.defaultGeolocSearchPlaceTypeClass, query
				    .getPlaceType());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.PLACETYPE_PARAMETER, "unk");
		    query = buildQuery(request);
		    assertEquals(
			    "When wrong "
				    + GeolocQuery.PLACETYPE_PARAMETER
				    + " is specified, the  parameter should be set to defaultGeolocSearchPlaceType ",
			    GisgraphyConfig.defaultGeolocSearchPlaceTypeClass, query
				    .getPlaceType());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "city");
		    query = buildQuery(request);
		    assertEquals(GeolocQuery.PLACETYPE_PARAMETER
			    + " should be case insensitive  ", City.class, query
			    .getPlaceType());

		    // test Point
		    // with missing lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GeolocQuery.LAT_PARAMETER);
		    try {
			query = buildQuery(request);
			fail("When there is no latitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // with empty lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LAT_PARAMETER, "");
		    try {
			query = buildQuery(request);
			fail("When there is empty latitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // With wrong lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LAT_PARAMETER, "a");
		    try {
			query = buildQuery(request);
			fail("A null lat should throw");
		    } catch (RuntimeException e) {
		    }
		    // With too small lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LAT_PARAMETER, "-92");
		    try {
			query = buildQuery(request);
			fail("latitude should not accept latitude < -90");
		    } catch (RuntimeException e) {
		    }

		    // With too high lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LAT_PARAMETER, "92");
		    try {
			query = buildQuery(request);
			fail("latitude should not accept latitude > 90");
		    } catch (RuntimeException e) {
		    }

		    // with missing long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GeolocQuery.LONG_PARAMETER);
		    try {
			query = buildQuery(request);
			fail("When there is no latitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // with empty long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LONG_PARAMETER, "");
		    try {
			query = buildQuery(request);
			fail("When there is empty longitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // With wrong Long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LONG_PARAMETER, "a");
		    try {
			query = buildQuery(request);
			fail("A null lat should throw");
		    } catch (RuntimeException e) {
		    }

		    // with too small long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LONG_PARAMETER, "-182");
		    try {
			query = buildQuery(request);
			fail("longitude should not accept longitude < -180");
		    } catch (RuntimeException e) {
		    }

		    // with too high long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LONG_PARAMETER, "182");
		    try {
			query = buildQuery(request);
			fail("longitude should not accept longitude > 180");
		    } catch (RuntimeException e) {
		    }

		    // with long with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LONG_PARAMETER, "10,3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept longitude with comma", 10.3D,
				query.getLongitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with long with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LONG_PARAMETER, "10.3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept longitude with comma", 10.3D,
				query.getLongitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with lat with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LAT_PARAMETER, "10,3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept latitude with comma", 10.3D,
				query.getLatitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with lat with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.LAT_PARAMETER, "10.3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept latitude with point", 10.3D,
				query.getLatitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // test radius
		    // with missing radius
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.removeParameter(GeolocQuery.RADIUS_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GeolocQuery.RADIUS_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + GeolocQuery.DEFAULT_RADIUS, GeolocQuery.DEFAULT_RADIUS,
			    query.getRadius(), 0.1);
		    // With wrong radius
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.RADIUS_PARAMETER, "a");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GeolocQuery.RADIUS_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + GeolocQuery.DEFAULT_RADIUS, GeolocQuery.DEFAULT_RADIUS,
			    query.getRadius(), 0.1);
		    // radius with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.RADIUS_PARAMETER, "1,4");
		    query = buildQuery(request);
		    assertEquals("Radius should accept comma as decimal separator",
			    1.4D, query.getRadius(), 0.1);

		    // radius with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForGeoloc();
		    request.setParameter(GeolocQuery.RADIUS_PARAMETER, "1.4");
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

		} finally {
		    GisgraphyConfig.defaultGeolocSearchPlaceTypeClass = savedDefaultType;
		}
	    }

	private GeolocQuery buildQuery(MockHttpServletRequest request) {
		return GeolocQueryHttpBuilder.getInstance().buildFromHttpRequest(request);
	}

}
