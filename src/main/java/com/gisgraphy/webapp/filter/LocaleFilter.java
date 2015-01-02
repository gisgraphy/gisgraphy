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
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gisgraphy.Constants;

/**
 * Filter to wrap request with a request including user preferred locale.
 */
public class LocaleFilter extends OncePerRequestFilter {

    /**
     * This method looks for a "locale" request parameter. If it finds one, it
     * sets it as the preferred locale and also configures it to work with JSTL.
     * 
     * @param request
     *                the current request
     * @param response
     *                the current response
     * @param chain
     *                the chain
     * @throws IOException
     *                 when something goes wrong
     * @throws ServletException
     *                 when a communication failure happens
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doFilterInternal(HttpServletRequest request,
	    HttpServletResponse response, FilterChain chain)
	    throws IOException, ServletException {

	String locale = request.getParameter("locale");
	Locale preferredLocale = null;

	if (locale != null) {
	    int indexOfUnderscore = locale.indexOf('-');
	    if (indexOfUnderscore != -1) {
		String language = locale.substring(0, indexOfUnderscore);
		String country = locale.substring(indexOfUnderscore + 1);
		preferredLocale = new Locale(language, country);
	    } else {
		preferredLocale = new Locale(locale);
	    }
	}

	HttpSession session = request.getSession(false);

	if (session != null) {
	    if (preferredLocale == null) {
		preferredLocale = (Locale) session
			.getAttribute(Constants.PREFERRED_LOCALE_KEY);
	    } else {
		session.setAttribute(Constants.PREFERRED_LOCALE_KEY,
			preferredLocale);
		Config.set(session, Config.FMT_LOCALE, preferredLocale);
	    }

	    if (preferredLocale != null
		    && !(request instanceof LocaleRequestWrapper)) {
		request = new LocaleRequestWrapper(request, preferredLocale);
		LocaleContextHolder.setLocale(preferredLocale);
	    }
	}

	String theme = request.getParameter("theme");
	if (theme != null && request.isUserInRole(Constants.ADMIN_ROLE)) {
	    Map<String, Object> config = (Map) getServletContext()
		    .getAttribute(Constants.CONFIG);
	    config.put(Constants.CSS_THEME, theme);
	}

	chain.doFilter(request, response);

	// Reset thread-bound LocaleContext.
	LocaleContextHolder.setLocaleContext(null);
    }
}
