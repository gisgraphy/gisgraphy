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
package com.gisgraphy.service.impl;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.MockObjectTestCase;

import com.gisgraphy.helper.PropertiesHelper;

public abstract class BaseManagerMockTestCase extends MockObjectTestCase {
    // ~ Static fields/initializers
    // =============================================

    protected final Log log = LogFactory.getLog(getClass());

    protected ResourceBundle rb;

    // ~ Constructors
    // ===========================================================

    public BaseManagerMockTestCase() {
	// Since a ResourceBundle is not required for each class, just
	// do a simple check to see if one exists
	String className = this.getClass().getName();

	try {
	    rb = ResourceBundle.getBundle(className);
	} catch (MissingResourceException mre) {
	    // log.warn("No resource bundle found for: " + className);
	}
    }

    // ~ Methods
    // ================================================================

    /**
     * Utility method to populate a javabean-style object with values from a
     * Properties file
     * 
     * @param obj
     *                the model object to populate
     * @return Object populated object
     * @throws Exception
     *                 if BeanUtils fails to copy properly
     */
    protected Object populate(Object obj) throws Exception {
	// loop through all the beans methods and set its properties from
	// its .properties file
	Map<String, String> map = PropertiesHelper.convertBundleToMap(rb);

	BeanUtils.copyProperties(obj, map);

	return obj;
    }
}
