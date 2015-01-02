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
package com.gisgraphy.webapp.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * Security interceptor checks to see if users are in the specified roles before
 * proceeding. Similar to Spring's UserRoleAuthorizationInterceptor. see
 * org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class UserRoleAuthorizationInterceptor implements Interceptor {
    private static final long serialVersionUID = 5067790608840427509L;

    private String[] authorizedRoles;

    /**
     * Intercept the action invocation and check to see if the user has the
     * proper role.
     * 
     * @param invocation
     *                the current action invocation
     * @return the method's return value, or null after setting
     *         HttpServletResponse.SC_FORBIDDEN
     * @throws Exception
     *                 when setting the error on the response fails
     */
    public String intercept(ActionInvocation invocation) throws Exception {
	HttpServletRequest request = ServletActionContext.getRequest();

	if (this.authorizedRoles != null) {
	    for (String authorizedRole : this.authorizedRoles) {
		if (request.isUserInRole(authorizedRole)) {
		    return invocation.invoke();
		}
	    }
	}

	HttpServletResponse response = ServletActionContext.getResponse();
	handleNotAuthorized(request, response);
	return null;
    }

    /**
     * Set the roles that this interceptor should treat as authorized.
     * 
     * @param authorizedRoles
     *                array of role names
     */
    public final void setAuthorizedRoles(String[] authorizedRoles) {
	this.authorizedRoles = authorizedRoles;
    }

    /**
     * Handle a request that is not authorized according to this interceptor.
     * Default implementation sends HTTP status code 403 ("forbidden").
     * <p>
     * This method can be overridden to write a custom message, forward or
     * redirect to some error page or login page, or throw a ServletException.
     * 
     * @param request
     *                current HTTP request
     * @param response
     *                current HTTP response
     * @throws javax.servlet.ServletException
     *                 if there is an internal error
     * @throws java.io.IOException
     *                 in case of an I/O error when writing the response
     */
    protected void handleNotAuthorized(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * This method currently does nothing.
     */
    public void destroy() {
    }

    /**
     * This method currently does nothing.
     */
    public void init() {
    }
}
