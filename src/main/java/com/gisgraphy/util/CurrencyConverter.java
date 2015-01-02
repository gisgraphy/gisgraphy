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

import java.text.DecimalFormat;
import java.text.ParseException;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is converts a Double to a double-digit String (and vise-versa) by
 * BeanUtils when copying properties.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class CurrencyConverter implements Converter {
    private final Log log = LogFactory.getLog(CurrencyConverter.class);

    private DecimalFormat formatter = new DecimalFormat("###,###.00");

    public void setDecimalFormatter(DecimalFormat df) {
	this.formatter = df;
    }

    /**
     * Convert a String to a Double and a Double to a String
     * 
     * @param type
     *                the class type to output
     * @param value
     *                the object to convert
     * @return object the converted object (Double or String)
     */
    @SuppressWarnings("unchecked")
    public final Object convert(final Class type, final Object value) {
	// for a null value, return null
	if (value == null) {
	    return null;
	} else {
	    if (value instanceof String) {
		if (log.isDebugEnabled()) {
		    log.debug("value (" + value + ") instance of String");
		}

		try {
		    if (StringUtils.isBlank(String.valueOf(value))) {
			return null;
		    }

		    if (log.isDebugEnabled()) {
			log.debug("converting '" + value + "' to a decimal");
		    }

		    // formatter.setDecimalSeparatorAlwaysShown(true);
		    Number num = formatter.parse(String.valueOf(value));

		    return num.doubleValue();
		} catch (ParseException pe) {
		}
	    } else if (value instanceof Double) {
		if (log.isDebugEnabled()) {
		    log.debug("value (" + value + ") instance of Double");
		    log.debug("returning double: " + formatter.format(value));
		}

		return formatter.format(value);
	    }
	}

	throw new ConversionException("Could not convert " + value + " to "
		+ type.getName() + "!");
    }
}
