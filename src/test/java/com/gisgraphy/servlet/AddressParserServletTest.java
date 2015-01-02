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
package com.gisgraphy.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.jstester.util.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.testing.ServletTester;
import org.springframework.mock.web.MockHttpServletResponse;

import com.gisgraphy.addressparser.AddressQuery;
import com.gisgraphy.addressparser.IAddressParserService;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;


public class AddressParserServletTest {
    
    public static final String SERVLET_CONTEXT = "/addressparser";
    
    private static ServletTester servletTester;
    
    @Test
    public void checkParameter() throws IOException{
    	AddressParserServlet addressParserServlet = new AddressParserServlet();
        Assert.assertTrue(addressParserServlet.checkparameter());
    }
    
   @Test
   public void processRequestShouldCallAddressParserService() throws IOException{
       AddressParserServlet addressParserServlet = new AddressParserServlet();
       HttpServletResponse response = new MockHttpServletResponse();
       AddressQuery query = new AddressQuery("address", "us");
       IAddressParserService addressParserService = EasyMock.createMock(IAddressParserService.class);
       IStatsUsageService statsUsageService = EasyMock.createMock(IStatsUsageService.class);
       statsUsageService.increaseUsage(StatsUsageType.ADDRESSPARSER);
       EasyMock.replay(statsUsageService);
       
       addressParserService.executeAndSerialize(query, response.getOutputStream());
       EasyMock.replay(addressParserService);
       addressParserServlet.setAddressParserService(addressParserService);
       addressParserServlet.setStatsUsageService(statsUsageService);
  
       addressParserServlet.processRequest(query, response);
       
       EasyMock.verify(addressParserService);
       EasyMock.verify(statsUsageService);
   }
    
    @Test
    public void ServletShouldStart() throws Exception{
	    // we only launch geoloc servlet once
	    servletTester = new ServletTester();
	    servletTester.setContextPath("/");
	    ServletHolder holder = servletTester.addServlet(
		    AddressParserServlet.class, SERVLET_CONTEXT + "/*");
	    servletTester.createSocketConnector(true);
	    servletTester.start();
	    AddressParserServlet geocodingServlet = (AddressParserServlet) holder.getServlet();
	    Assert.assertNotNull(geocodingServlet);
    }
    
    @Test
    public void getGisgraphyServiceTypeShouldReturnTheCorrectValue(){
	AddressParserServlet servlet = new AddressParserServlet();
	Assert.assertEquals(GisgraphyServiceType.ADDRESS_PARSER, servlet.getGisgraphyServiceType());
    }
    

}
