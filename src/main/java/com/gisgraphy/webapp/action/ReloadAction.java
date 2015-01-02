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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.gisgraphy.webapp.listener.StartupListener;

/**
 * This class is used to reload the drop-downs initialized in the
 * StartupListener.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class ReloadAction extends BaseAction {
    private static final long serialVersionUID = 295460450224891051L;

    /**
     * Method that calls StartupListener.setContext() and returns user to
     * referrer location (or does a popup if none found).
     * 
     * @return sucess when everything goes right
     * @throws IOException
     *                 when response.sendRedirect fails
     */
    @Override
    public String execute() throws IOException {
	StartupListener.setupContext(getSession().getServletContext());

	String referer = getRequest().getHeader("Referer");
	HttpServletResponse response = ServletActionContext.getResponse();

	if (referer != null) {
	    log.info("reload complete, reloading user back to: " + referer);
	    saveMessage(getText("reload.succeeded"));
	    response.sendRedirect(response.encodeRedirectURL(referer));
	    return SUCCESS;
	} else {
	    response.setContentType("text/html");

	    PrintWriter out = response.getWriter();

	    out.println("<html>");
	    out.println("<head>");
	    out.println("<title>Context Reloaded</title>");
	    out.println("</head>");
	    out.println("<body bgcolor=\"white\">");
	    out.println("<script type=\"text/javascript\">");
	    out
		    .println("alert('Reloading options succeeded! Click OK to continue.');");
	    out.println("history.back();");
	    out.println("</script>");
	    out.println("</body>");
	    out.println("</html>");
	}

	return SUCCESS;
    }
}
