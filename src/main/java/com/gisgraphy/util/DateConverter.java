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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;

/**
 * This class is converts a java.util.Date to a String and a String to a
 * java.util.Date.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class DateConverter implements Converter {

    /**
     * Convert a date to a String and a String to a Date
     * 
     * @param type
     *                String, Date or Timestamp
     * @param value
     *                value to convert
     * @return Converted value for property population
     */
    @SuppressWarnings("unchecked")
    public Object convert(Class type, Object value) {
	if (value == null) {
	    return null;
	} else if (type == Timestamp.class) {
	    return convertToDate(type, value, DateUtil.getDateTimePattern());
	} else if (type == Date.class) {
	    return convertToDate(type, value, DateUtil.getDatePattern());
	} else if (type == String.class) {
	    return convertToString(type, value);
	}

	throw new ConversionException("Could not convert "
		+ value.getClass().getName() + " to " + type.getName());
    }

    /**
     * Convert a String to a Date with the specified pattern.
     * 
     * @param type
     *                String
     * @param value
     *                value of String
     * @param pattern
     *                date pattern to parse with
     * @return Converted value for property population
     */
    @SuppressWarnings("unchecked")
    protected Object convertToDate(Class type, Object value, String pattern) {
	DateFormat df = new SimpleDateFormat(pattern);
	if (value instanceof String) {
	    try {
		if (StringUtils.isEmpty(value.toString())) {
		    return null;
		}

		Date date = df.parse((String) value);
		if (type.equals(Timestamp.class)) {
		    return new Timestamp(date.getTime());
		}
		return date;
	    } catch (Exception pe) {
		throw new ConversionException("Error converting String to Date");
	    }
	}

	throw new ConversionException("Could not convert "
		+ value.getClass().getName() + " to " + type.getName());
    }

    /**
     * Convert a java.util.Date to a String
     * 
     * @param type
     *                Date or Timestamp
     * @param value
     *                value to convert
     * @return Converted value for property population
     */
    @SuppressWarnings("unchecked")
    protected Object convertToString(Class type, Object value) {

	if (value instanceof Date) {
	    DateFormat df = new SimpleDateFormat(DateUtil.getDatePattern());
	    if (value instanceof Timestamp) {
		df = new SimpleDateFormat(DateUtil.getDateTimePattern());
	    }

	    try {
		return df.format(value);
	    } catch (Exception e) {
		throw new ConversionException("Error converting Date to String");
	    }
	} else {
	    return value.toString();
	}
    }
}
