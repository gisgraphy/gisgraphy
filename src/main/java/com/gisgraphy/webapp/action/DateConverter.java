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

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.gisgraphy.util.DateUtil;
import com.opensymphony.xwork2.conversion.TypeConversionException;

public class DateConverter extends StrutsTypeConverter {

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map ctx, String[] value, Class arg2) {
	if (value[0] == null || value[0].trim().equals("")) {
	    return null;
	}

	try {
	    return DateUtil.convertStringToDate(value[0]);
	} catch (ParseException pe) {
	    throw new TypeConversionException(pe.getMessage());
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convertToString(Map ctx, Object data) {
	return DateUtil.convertDateToString((Date) data);
    }
}