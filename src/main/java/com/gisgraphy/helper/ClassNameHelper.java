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
package com.gisgraphy.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some useful (hack) functions
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class ClassNameHelper {

    protected static final Logger logger = LoggerFactory
	    .getLogger(ClassNameHelper.class);

    /**
     * This strips the cglib class name out of the enhanced classes.
     *
     * @param className the name of the class
     * @return the striped name, or the GisFeture className if an error occured
     */
    @SuppressWarnings("unchecked")
    public static String stripEnhancerClass(String className) {
	int enhancedIndex = className.indexOf("$$EnhancerByCGLIB");
	if (enhancedIndex != -1) {
	    return className.substring(0, enhancedIndex);
	} else {
	    return className;
	}

    }

}
