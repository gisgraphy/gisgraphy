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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateConverterTest  {
    private DateConverter converter = new DateConverter();

    @Test
    public void internationalization() throws Exception {
	List<Locale> locales = new ArrayList<Locale>() {
	    private static final long serialVersionUID = 1L;
	    {
		add(Locale.US);
		add(Locale.GERMANY);
		add(Locale.FRANCE);
		add(Locale.CHINA);
		add(Locale.ITALY);
	    }
	};

	for (Locale locale : locales) {
	    LocaleContextHolder.setLocale(locale);
	    convertStringToDate();
	    convertDateToString();
	    convertStringToTimestamp();
	    convertTimestampToString();
	}
    }

    @Test
    public void convertStringToDate() throws Exception {
	Date today = new Date();
	Calendar todayCalendar = new GregorianCalendar();
	todayCalendar.setTime(today);
	String datePart = DateUtil.convertDateToString(today);

	Date date = (Date) converter.convert(Date.class, datePart);

	Calendar cal = new GregorianCalendar();
	cal.setTime(date);
	assertEquals(todayCalendar.get(Calendar.YEAR), cal.get(Calendar.YEAR));
	assertEquals(todayCalendar.get(Calendar.MONTH), cal.get(Calendar.MONTH));
	assertEquals(todayCalendar.get(Calendar.DAY_OF_MONTH), cal
		.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void convertDateToString() throws Exception {
	Calendar cal = new GregorianCalendar(2005, 0, 16);
	String date = (String) converter.convert(String.class, cal.getTime());
	assertEquals(DateUtil.convertDateToString(cal.getTime()), date);
    }

    @Test
    public void convertStringToTimestamp() throws Exception {
	Date today = new Date();
	Calendar todayCalendar = new GregorianCalendar();
	todayCalendar.setTime(today);
	String datePart = DateUtil.convertDateToString(today);

	Timestamp time = (Timestamp) converter.convert(Timestamp.class,
		datePart + " 01:02:03.4");
	Calendar cal = new GregorianCalendar();
	cal.setTimeInMillis(time.getTime());
	assertEquals(todayCalendar.get(Calendar.YEAR), cal.get(Calendar.YEAR));
	assertEquals(todayCalendar.get(Calendar.MONTH), cal.get(Calendar.MONTH));
	assertEquals(todayCalendar.get(Calendar.DAY_OF_MONTH), cal
		.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void convertTimestampToString() throws Exception {
	Timestamp timestamp = Timestamp.valueOf("2005-03-10 01:02:03.4");
	String time = (String) converter.convert(String.class, timestamp);
	assertEquals(DateUtil.getDateTime(DateUtil.getDateTimePattern(),
		timestamp), time);
    }

}
