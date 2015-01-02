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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Borrowed from the Display Tag project:
 * http://displaytag.sourceforge.net/xref-test/org/displaytag/filter/MockFilterSupport.html
 * http://www.springframework.org/docs/api/org/springframework/mock/web/MockFilterChain.html
 */
public class MockFilterChain implements FilterChain {
    private final Log log = LogFactory.getLog(MockFilterChain.class);

    private String forwardURL;

    public void doFilter(ServletRequest request, ServletResponse response)
	    throws IOException, ServletException {
	String uri = ((HttpServletRequest) request).getRequestURI();
	String requestContext = ((HttpServletRequest) request).getContextPath();

	if (StringUtils.isNotEmpty(requestContext)
		&& uri.startsWith(requestContext)) {
	    uri = uri.substring(requestContext.length());
	}

	this.forwardURL = uri;
	log.debug("Forwarding to: " + uri);

	RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
	dispatcher.forward(request, response);
    }

    public String getForwardURL() {
	return this.forwardURL;
    }
}