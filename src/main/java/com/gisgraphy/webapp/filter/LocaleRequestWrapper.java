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

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HttpRequestWrapper overriding methods getLocale(), getLocales() to include
 * the user's preferred locale.
 */
public class LocaleRequestWrapper extends HttpServletRequestWrapper {
    private final transient Log log = LogFactory
	    .getLog(LocaleRequestWrapper.class);

    private final Locale preferredLocale;

    /**
     * Sets preferred local to user's locale
     * 
     * @param decorated
     *                the current decorated request
     * @param userLocale
     *                the user's locale
     */
    public LocaleRequestWrapper(final HttpServletRequest decorated,
	    final Locale userLocale) {
	super(decorated);
	preferredLocale = userLocale;
	if (null == preferredLocale) {
	    log.error("preferred locale = null, it is an unexpected value!");
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
	if (null != preferredLocale) {
	    return preferredLocale;
	} else {
	    return super.getLocale();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<Locale> getLocales() {
	if (null != preferredLocale) {
	    List<Locale> l = Collections.list(super.getLocales());
	    if (l.contains(preferredLocale)) {
		l.remove(preferredLocale);
	    }
	    l.add(0, preferredLocale);
	    return Collections.enumeration(l);
	} else {
	    return super.getLocales();
	}
    }

}
