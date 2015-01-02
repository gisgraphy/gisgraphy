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
/**
 * This work was partially supported by the European Commission, under the 6th
 * Framework Programme, contract IST-2-004688-STP. This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.gisgraphy.hibernate.projection;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class ProjectionBean extends ProjectionList {

    private static Logger logger = LoggerFactory
	    .getLogger(ProjectionBean.class);

    /**
     * @param fieldList
     *                a list of fields to retrieve <b>Important Note</b> : the
     *                projection are automatically aliased with the same name,
     *                you could have some problems when using restriction
     *                (column yX_ doesn't exists) see
     *                http://opensource.atlassian.com/projects/hibernate/browse/HHH-817.
     *                To avoid this problem you could disable aliasing .<br/>
     *                activate auto-aliasing if you want to use aliasToBean
     *                Transformers. disable auto-aliasing if you want to add a
     *                restriction on a field that is in the fields list
     * @return a new ProjectionList
     */
    public static ProjectionList fieldList(List<String> fieldList,
	    boolean autoaliasing) {
	return new ProjectionBean(fieldList, autoaliasing).projectionList;
    }

    /**
     * @param clazz
     *                the class to inspect to retrieve the fields
     * @param ignoreFields
     *                an array of fields that should be ignore
     * @param autoaliasing
     *                if the fields should be auto-aliased <b>See important note
     *                above</b>
     * @return a new ProjectionList
     */
    public static ProjectionList beanFieldList(Class<?> clazz,
	    String[] ignoreFields, boolean autoaliasing) {
	return new ProjectionBean(inspectBean(clazz, ignoreFields),
		autoaliasing).projectionList;
    }

    private static List<String> inspectBean(Class<?> clazz,
	    String[] ignoreFields) {
	if (clazz == null) {
	    throw new IllegalArgumentException("Can not inspect a null bean");
	}
	List<String> fieldNames = new ArrayList<String>();
	PropertyDescriptor[] att = null;
	try {
	    att = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
	    List<String> ignoreFieldsList = Arrays.asList(ignoreFields);
	    for (int i = 0; i < att.length; i++) {
		String fieldName = att[i].getDisplayName();
		if (!"class".equals(fieldName)
			&& !isIgnoreFields(ignoreFieldsList, fieldName)) {
		    fieldNames.add(fieldName);
		}
	    }
	} catch (Exception e) {
	    logger.error("can not inspect bean " + clazz.getSimpleName()+" : "+e.getMessage(),e);
	}

	return fieldNames;
    }

    private static boolean isIgnoreFields(List<String> ignoreFieldsList,
	    String fieldName) {
	return ignoreFieldsList.contains(fieldName);
    }

    /**
     * Default SerialId
     */
    private static final long serialVersionUID = -5528427011925538257L;

    private ProjectionList projectionList = Projections.projectionList();

    /**
     * 
     */
    /**
     * @param fields
     *                the list of the field names
     * @param autoAliasing
     *                if the fields should be autoaliased. <b>See important note
     *                above</b>
     */
    public ProjectionBean(List<String> fields, boolean autoAliasing) {
	super();
	for (String field : fields) {
	    if (autoAliasing) {
		projectionList.add(Projections.property(field).as(field));
	    } else {
		projectionList.add(Projections.property(field));
	    }
	}
    }

}
