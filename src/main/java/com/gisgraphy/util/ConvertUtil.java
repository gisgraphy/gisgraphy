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

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gisgraphy.helper.PropertiesHelper;
import com.gisgraphy.model.LabelValue;

/**
 * Utility class to convert one object to another.
 */
public final class ConvertUtil {
    private static final Log log = LogFactory.getLog(ConvertUtil.class);

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private ConvertUtil() {
    }

    

    /**
     * Convert a java.util.List of LabelValue objects to a LinkedHashMap.
     * 
     * @param list
     *                the list to convert
     * @return the populated map with the label as the key
     */
    public static Map<String, String> convertListToMap(List<LabelValue> list) {
	Map<String, String> map = new LinkedHashMap<String, String>();

	for (LabelValue option : list) {
	    map.put(option.getLabel(), option.getValue());
	}

	return map;
    }

    /**
     * Method to convert a ResourceBundle to a Properties object.
     * 
     * @param rb
     *                a given resource bundle
     * @return Properties a populated properties object
     */
    public static Properties convertBundleToProperties(ResourceBundle rb) {
	Properties props = new Properties();

	for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
	    String key = keys.nextElement();
	    props.put(key, rb.getString(key));
	}

	return props;
    }

    /**
     * Convenience method used by tests to populate an object from a
     * ResourceBundle
     * 
     * @param obj
     *                an initialized object
     * @param rb
     *                a resource bundle
     * @return a populated object
     */
    public static Object populateObject(Object obj, ResourceBundle rb) {
	try {
	    Map<String, String> map = PropertiesHelper.convertBundleToMap(rb);
	    BeanUtils.copyProperties(obj, map);
	} catch (Exception e) {
	    log
		    .error("Exception occurred populating object: "
			    + e.getMessage());
	}

	return obj;
    }
}
