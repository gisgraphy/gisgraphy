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
 * $Id: SpatialRelateExpression.java 87 2008-02-25 19:43:14Z maesenka $ This
 * work was partially supported by the European Commission, under the 6th
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
package com.gisgraphy.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

import com.gisgraphy.helper.StringHelper;

/**
 * An implementation of the <code>Criterion</code> interface that implements
 * 'ts_query @@ ts_vector' restriction
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class FulltextRestriction implements Criterion {



    /**
	 * generated serial version id
	 */
	private static final long serialVersionUID = -1118549308820955458L;
	private String ts_vectorColumnName;
	private String searchedText;

	/**
     * @param ts_vectorColumnName The name of the ts_vector column
     * @param searchedText the text to search
     */
    public FulltextRestriction(String ts_vectorColumnName, String searchedText) {
    if (ts_vectorColumnName == null ){
    	throw new IllegalArgumentException("the name of the ts_vector column can not be null");
    }
    if (searchedText == null){
    	throw new IllegalArgumentException("the search text can not be null");
    }
	this.ts_vectorColumnName = ts_vectorColumnName;
	this.searchedText = searchedText;
    }

  
    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.criterion.Criterion#getTypedValues(org.hibernate.Criteria,
     *      org.hibernate.criterion.CriteriaQuery)
     */
    public TypedValue[] getTypedValues(Criteria criteria,
	    CriteriaQuery criteriaQuery) throws HibernateException {
	return  new TypedValue[] { new TypedValue( Hibernate.STRING, StringHelper.normalize(searchedText), EntityMode.POJO )};

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.criterion.Criterion#toSqlString(org.hibernate.Criteria,
     *      org.hibernate.criterion.CriteriaQuery)
     */
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
	    throws HibernateException {
	String columnName = criteriaQuery.getColumn(criteria,
			ts_vectorColumnName);
	StringBuffer result = new StringBuffer("(").append(columnName).append(
			" @@ plainto_tsquery('simple',?)").append(")");
			return result.toString();

    }


}
