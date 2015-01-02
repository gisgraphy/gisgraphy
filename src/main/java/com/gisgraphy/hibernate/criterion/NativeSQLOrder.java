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
package com.gisgraphy.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;
import org.hibernate.util.StringHelper;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a> An
 *         hibernate order for SQL
 */
public class NativeSQLOrder extends Order {
    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.criterion.Order#toSqlString(org.hibernate.Criteria,
     *      org.hibernate.criterion.CriteriaQuery)
     */
    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
	    throws HibernateException {
	StringBuilder fragment = new StringBuilder();
	fragment.append("(");
	fragment.append(sql);
	fragment.append(")");
	fragment.append(ascending ? " asc" : " desc");
	return StringHelper.replace(fragment.toString(), "{alias}",
		criteriaQuery.getSQLAlias(criteria));
    }

    private static final long serialVersionUID = 1L;

    private boolean ascending;
    private String sql;

    /**
     * @param sql
     *                the sql code
     * @param ascending
     *                wether we want to sort asc or desc
     */
    public NativeSQLOrder(String sql, boolean ascending) {
	super(null, ascending);
	this.sql = sql;
	this.ascending = ascending;

    }

    /**
     * @param sql
     *                the sql code Default sorting will be ascending
     */
    public NativeSQLOrder(String sql) {
	super(null, true);
	this.sql = sql;
	this.ascending = true;

    }

}
