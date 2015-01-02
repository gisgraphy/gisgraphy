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
package com.gisgraphy.reversegeocoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.servlet.GisgraphyServlet;
import com.gisgraphy.test.GisgraphyTestHelper;


public class ReverseGeocodingQueryHttpBuilderTest {
	
	   @Test
	    public void testBuildFromAnHttpServletRequest() {
		    MockHttpServletRequest request = GisgraphyTestHelper
			    .createMockHttpServletRequestForReverseGeocoding();
		    ReverseGeocodingQuery query = buildQuery(request);
		    assertEquals(OutputFormat.XML, query.getOutputFormat());
		    assertEquals(null, query.getOutputLanguage());
		    assertEquals(OutputStyle.getDefault(), query.getOutputStyle());
		    Assert.assertEquals(new Double(1.0), query.getLatitude());
		    assertEquals(new Double(2.0), query.getLongitude());


		    // test indentation
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.removeParameter(GisgraphyServlet.INDENT_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.INDENT_PARAMETER
			    + " is specified, the  parameter should be set to default",Output.DEFAULT_INDENTATION,
			    query.isOutputIndented());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GisgraphyServlet.INDENT_PARAMETER
			    + " is specified, the  parameter should be set to default",Output.DEFAULT_INDENTATION,
			    query.isOutputIndented());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "tRue");
		    query = buildQuery(request);
		    assertTrue(GisgraphyServlet.INDENT_PARAMETER
			    + " should be case insensitive  ", query.isOutputIndented());
		    // test 'on' value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(GisgraphyServlet.INDENT_PARAMETER, "oN");
		    query = buildQuery(request);
		    assertTrue(
			    GisgraphyServlet.INDENT_PARAMETER
				    + " should be true for 'on' value (case insensitive and on value)  ",
			    query.isOutputIndented());
		    
		    


		    // test outputFormat
		    // with no value specified
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.removeParameter(GisgraphyServlet.FORMAT_PARAMETER);
		    query = buildQuery(request);
		    assertEquals("When no " + GisgraphyServlet.FORMAT_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + OutputFormat.getDefault(), OutputFormat.getDefault(),
			    query.getOutputFormat());
		    // with wrong value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "UNK");
		    query = buildQuery(request);
		    assertEquals("When wrong " + GisgraphyServlet.FORMAT_PARAMETER
			    + " is specified, the  parameter should be set to  "
			    + OutputFormat.getDefault(), OutputFormat.getDefault(),
			    query.getOutputFormat());
		    // test case sensitive
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "json");
		    query = buildQuery(request);
		    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
			    + " should be case insensitive  ", OutputFormat.JSON, query
			    .getOutputFormat());
		    // test with unsupported value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(GisgraphyServlet.FORMAT_PARAMETER, "unsupported");
		    query = buildQuery(request);
		    assertEquals(GisgraphyServlet.FORMAT_PARAMETER
			    + " should set default if not supported  ", OutputFormat.getDefault(), query
			    .getOutputFormat());

		   

		    // test Point
		    // with missing lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.removeParameter(ReverseGeocodingQuery.LAT_PARAMETER);
		    try {
			query = buildQuery(request);
			fail("When there is no latitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // with empty lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LAT_PARAMETER, "");
		    try {
			query = buildQuery(request);
			fail("When there is empty latitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // With wrong lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LAT_PARAMETER, "a");
		    try {
			query = buildQuery(request);
			fail("A null lat should throw");
		    } catch (RuntimeException e) {
		    }
		    // With too small lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LAT_PARAMETER, "-92");
		    try {
			query = buildQuery(request);
			fail("latitude should not accept latitude < -90");
		    } catch (RuntimeException e) {
		    }

		    // With too high lat
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LAT_PARAMETER, "92");
		    try {
			query = buildQuery(request);
			fail("latitude should not accept latitude > 90");
		    } catch (RuntimeException e) {
		    }

		    // with missing long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.removeParameter(ReverseGeocodingQuery.LONG_PARAMETER);
		    try {
			query = buildQuery(request);
			fail("When there is no latitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // with empty long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LONG_PARAMETER, "");
		    try {
			query = buildQuery(request);
			fail("When there is empty longitude, query should throw");
		    } catch (RuntimeException e) {
		    }
		    // With wrong Long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LONG_PARAMETER, "a");
		    try {
			query = buildQuery(request);
			fail("A null lat should throw");
		    } catch (RuntimeException e) {
		    }

		    // with too small long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LONG_PARAMETER, "-182");
		    try {
			query = buildQuery(request);
			fail("longitude should not accept longitude < -180");
		    } catch (RuntimeException e) {
		    }

		    // with too high long
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LONG_PARAMETER, "182");
		    try {
			query = buildQuery(request);
			fail("longitude should not accept longitude > 180");
		    } catch (RuntimeException e) {
		    }

		    // with long with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LONG_PARAMETER, "10,3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept longitude with comma", 10.3D,
				query.getLongitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with long with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LONG_PARAMETER, "10.3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept longitude with comma", 10.3D,
				query.getLongitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with lat with comma
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LAT_PARAMETER, "10,3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept latitude with comma", 10.3D,
				query.getLatitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		    // with lat with point
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.LAT_PARAMETER, "10.3");
		    try {
			query = buildQuery(request);
			Assert.assertEquals(
				"request should accept latitude with point", 10.3D,
				query.getLatitude().doubleValue(), 0.1);

		    } catch (RuntimeException e) {
		    }

		   
		    
		    //callback not set
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    query = buildQuery(request);
		    assertNull("callback should be null when not set",
			     query.getCallback());
		    
		    //callback set with non alpha value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.CALLBACK_PARAMETER, "doit(");
		    query = buildQuery(request);
		    assertNull("callback should not be set when not alphanumeric",
			     query.getCallback());
		    
		    //callback set with alpha value
		    request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
		    request.setParameter(ReverseGeocodingQuery.CALLBACK_PARAMETER, "doit");
		    query = buildQuery(request);
		    assertEquals("callback should not be set when alphanumeric",
			     "doit",query.getCallback());
		    
		  //apiKey
			request = GisgraphyTestHelper.createMockHttpServletRequestForReverseGeocoding();
			request.setParameter(GisgraphyServlet.APIKEY_PARAMETER, "apiKey");
			query =buildQuery(request);
			Assert.assertEquals("apiKey", query.getApikey());

	    }

	private ReverseGeocodingQuery buildQuery(MockHttpServletRequest request) {
		return ReverseGeocodingQueryHttpBuilder.getInstance().buildFromHttpRequest(request);
	}

}
