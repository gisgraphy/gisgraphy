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
package com.gisgraphy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateUtilTest {

    private final Log log = LogFactory.getLog(DateUtilTest.class);

    @Test
    public void testGetInternationalDatePattern() {
	LocaleContextHolder.setLocale(new Locale("nl"));
	assertEquals("dd-MMM-yyyy", DateUtil.getDatePattern());

	LocaleContextHolder.setLocale(Locale.FRANCE);
	assertEquals("dd/MM/yyyy", DateUtil.getDatePattern());

	LocaleContextHolder.setLocale(Locale.GERMANY);
	assertEquals("dd.MM.yyyy", DateUtil.getDatePattern());

	// non-existant bundle should default to default locale
	LocaleContextHolder.setLocale(new Locale("fi"));
	String fiPattern = DateUtil.getDatePattern();
	LocaleContextHolder.setLocale(Locale.getDefault());
	String defaultPattern = DateUtil.getDatePattern();

	assertEquals(defaultPattern, fiPattern);
    }

    @Test
    public void testGetDate() throws Exception {
	if (log.isDebugEnabled()) {
	    log.debug("db date to convert: " + new Date());
	}

	String date = DateUtil.getDate(new Date());

	if (log.isDebugEnabled()) {
	    log.debug("converted ui date: " + date);
	}

	assertTrue(date != null);
    }

    @Test
    public void testGetDateTime() {
	if (log.isDebugEnabled()) {
	    log.debug("entered 'testGetDateTime' method");
	}
	String now = DateUtil.getTimeNow(new Date());
	assertTrue(now != null);
	log.debug(now);
    }
}
