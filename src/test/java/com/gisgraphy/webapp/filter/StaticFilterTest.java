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
package com.gisgraphy.webapp.filter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class StaticFilterTest  {
    private StaticFilter filter = null;

   @Before
    public void setUp() throws Exception {
	filter = new StaticFilter();
	MockFilterConfig config = new MockFilterConfig();
	config.addInitParameter("includes", "/scripts/*");
	filter.init(config);
    }

   @Test
    public void filterDoesntForwardWhenPathMatches() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/scripts/dojo/test.html");
	MockHttpServletResponse response = new MockHttpServletResponse();
	MockFilterChain chain = new MockFilterChain();

	filter.doFilter(request, response, chain);

	assertNull(chain.getForwardURL());
    }

   @Test
    public void filterForwardsWhenPathDoesntMatch() throws Exception {
	MockHttpServletRequest request = new MockHttpServletRequest("GET",
		"/editProfile.html");
	MockHttpServletResponse response = new MockHttpServletResponse();
	MockFilterChain chain = new MockFilterChain();

	filter.doFilter(request, response, chain);

	assertNotNull(chain.getForwardURL());
    }
}
