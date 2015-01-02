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
package com.gisgraphy.webapp.taglib;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gisgraphy.Constants;

/**
 * Implementation of <code>TagExtraInfo</code> for the <b>constants</b> tag,
 * identifying the scripting object(s) to be made visible.
 * 
 * @author Matt Raible
 */
public class ConstantsTei extends TagExtraInfo {
    private final Log log = LogFactory.getLog(ConstantsTei.class);

    /**
     * Return information about the scripting variables to be created.
     * 
     * @param data
     *                the input data
     * @return VariableInfo array of variable information
     */
    @Override
    public VariableInfo[] getVariableInfo(TagData data) {
	// loop through and expose all attributes
	List<VariableInfo> vars = new ArrayList<VariableInfo>();

	try {
	    String clazz = data.getAttributeString("className");

	    if (clazz == null) {
		clazz = Constants.class.getName();
	    }

	    Class<?> c = Class.forName(clazz);

	    // if no var specified, get all
	    if (data.getAttributeString("var") == null) {
		Field[] fields = c.getDeclaredFields();

		AccessibleObject.setAccessible(fields, true);

		for (Field field : fields) {
		    String type = field.getType().getName();
		    vars.add(new VariableInfo(field.getName(), ((field
			    .getType().isArray()) ? type.substring(2, type
			    .length() - 1)
			    + "[]" : type), true, VariableInfo.AT_END));
		}
	    } else {
		String var = data.getAttributeString("var");
		String type = c.getField(var).getType().getName();
		vars.add(new VariableInfo(c.getField(var).getName(), ((c
			.getField(var).getType().isArray()) ? type.substring(2,
			type.length() - 1)
			+ "[]" : type), true, VariableInfo.AT_END));
	    }
	} catch (Exception cnf) {
	    log.error(cnf.getMessage());
	}

	return vars.toArray(new VariableInfo[] {});
    }
}
