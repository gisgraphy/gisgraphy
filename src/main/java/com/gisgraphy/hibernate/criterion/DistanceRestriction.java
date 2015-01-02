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
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.helper.GisHelper;
import com.vividsolutions.jts.geom.Point;

/**
 * An implementation of the <code>Criterion</code> interface that implements
 * distance restriction
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class DistanceRestriction implements Criterion {

    /**
     * Point we have to calculate the distance from
     */
    private Point point = null;

    private boolean useIndex = true;

    /**
     * The distance restriction
     */
    private double distance;

   

    private static final long serialVersionUID = 1L;

    /**
     * @param point
     *                Point we have to calculate the distance from
     * @param distance
     *                The distance restriction
     * @param useIndex
     *                Wether we must use index or not
     */
    public DistanceRestriction(Point point, double distance, boolean useIndex) {
	this.point = point;
	this.distance = distance;
	this.useIndex = useIndex;
    }

    /**
     * @param point
     *                Point we have to calculate the distance from
     * @param distance
     *                The distance restriction by default use index
     */
    public DistanceRestriction(Point point, double distance) {
	this.point = point;
	this.distance = distance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.criterion.Criterion#getTypedValues(org.hibernate.Criteria,
     *      org.hibernate.criterion.CriteriaQuery)
     */
    public TypedValue[] getTypedValues(Criteria criteria,
	    CriteriaQuery criteriaQuery) throws HibernateException {
	return new TypedValue[] { criteriaQuery.getTypedValue(criteria,
		GisFeature.LOCATION_COLUMN_NAME, point) };

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
		GisFeature.LOCATION_COLUMN_NAME);
	StringBuffer result = new StringBuffer("( st_distance_sphere(").append(
		columnName).append(", ?) <=").append(this.distance).append(")");
	return useIndex ? result.append(" AND ").append(
			GisHelper.getBoundingBox(criteriaQuery.getSQLAlias(criteria), this.point
			.getY(), this.point.getX(), distance)).toString()
		: result.toString();

    }

}
