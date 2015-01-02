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

import com.gisgraphy.domain.valueobject.SRID;
import com.vividsolutions.jts.geom.Point;

/**
 * A criteria that sort by distance for better performance you can use
 * {@link ProjectionOrder} if a projection for distance calculation have been
 * added
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class DistanceOrder extends Order {

    /**
     * Default serial id
     */
    private static final long serialVersionUID = 7382013976844760232L;

    private Point point;
    private boolean ascending = true;

    /**
     * @param point
     *                The point from which we calculate the distance
     * @param ascending
     *                Whether we sort Ascending
     */
    public DistanceOrder(Point point, boolean ascending) {
	super(null, ascending);
	this.point = point;
	this.ascending = ascending;
    }

    /**
     * @param point
     *                the point from which we calculate the distance, default
     *                ascending is true
     */
    public DistanceOrder(Point point) {
	super(null, true);
	this.point = point;
    }

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
	fragment.append(" st_distance_sphere(");
	fragment.append(criteriaQuery.getSQLAlias(criteria));
	fragment.append(".location, st_geometryfromtext('");
	fragment.append(point.toText());
	fragment.append("',");
	fragment.append(SRID.WGS84_SRID.getSRID());
	fragment.append(ascending ? ")) asc" : ")) desc");
	return fragment.toString();
    }

}
