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

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;

import com.gisgraphy.domain.valueobject.SRID;
import com.vividsolutions.jts.geom.Point;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a> Some
 *         Spatial Projections. distance_sphere...
 */
public class SpatialProjection {
    
    public static final String DISTANCE_FUNCTION = "st_distance";
    public static final String DISTANCE_SPHERE_FUNCTION = "st_distanceSphere";
    public static String ST_LINE_INTERPOLATE_POINT_FUNCTION = "st_line_interpolate_point";
    public static String ST_CLOSEST_POINT = "ST_ClosestPoint";
    public static String ST_LINE_LOCATE_POINT_FUNCTION = "st_line_locate_point";
    public static String LINEMERGE_FUNCTION = "st_linemerge";

    /**
	 * projection to get the distance_sphere between a point and a LineString
	 * 
	 * @param point
	 *                the point to get the distance
	 * @param lineStringColumnName the name of the lineString column
	 * @return the projection
	 * @see #distance(Point, String)
	 */
	public static SimpleProjection distance_pointToLine(final Point point, final String lineStringColumnName) {
	    return new SimpleProjection() {

		private static final long serialVersionUID = -7424596977297450115L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.hibernate.criterion.Projection#getTypes(org.hibernate.Criteria,
		 *      org.hibernate.criterion.CriteriaQuery)
		 */
		public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			return new Type[] { Hibernate.DOUBLE };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.hibernate.criterion.Projection#toSqlString(org.hibernate.Criteria,
		 *      int, org.hibernate.criterion.CriteriaQuery)
		 */
		public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
			String columnName = criteriaQuery.getColumn(criteria, lineStringColumnName);
			String pointAsString = new StringBuffer("st_GeometryFromText( 'POINT(")
			.append(point.getX()).append(" ").append(point.getY()).append(")',").append(SRID.WGS84_SRID.getSRID()).append(")").toString();
			
			/*String lineMerge = new StringBuffer(LINEMERGE_FUNCTION)
			.append("(")
			.append(columnName)
			.append(")").toString();*/
			 String shape = columnName;
			
			String sqlString = new StringBuffer()
			.append(DISTANCE_SPHERE_FUNCTION)
			.append("(")
			.append(pointAsString)
			.append(",")
			.append(ST_CLOSEST_POINT)
				.append("(")
					.append(shape)
					.append(",")
					.append(pointAsString)
				.append(")")
			.append(")")
			.append("as y").append(position).append("_")
			.toString();
			
			
			 
			return sqlString;
		}

	};
	}

    

	/**
	 * projection to get the distance_sphere of a point
	 * if you're on a Cartesian ref use {@link SpatialProjection#distance(Point, String)}
	 * @param point
	 *                the point to get the distance
	 * @param locationColumnName the name of the column we want the distance
	 * @return the projection
	 * @see #distance(Point, String)
	 */
	public static SimpleProjection distance_sphere(final Point point, final String locationColumnName) {
		return distance_function(point, locationColumnName, DISTANCE_SPHERE_FUNCTION);
	}
	

	/**
	 * projection to get the distance from a point
	 * if you're on a lat/long ref, use @link {@link #distance_sphere(Point, String)}
	 * 
	 * @param point
	 *                the point to get the distance
	 * @param locationColumnName the name of the column we want the distance
	 * @return the projection
	 * @see #distance_sphere(Point, String)
	 */
	public static SimpleProjection distance(final Point point, final String locationColumnName) {
		return distance_function(point, locationColumnName, DISTANCE_FUNCTION);
	}

	private static SimpleProjection distance_function(final Point point, final String locationColumnName, final String distanceFunction) {
		return new SimpleProjection() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8771843067497785957L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.hibernate.criterion.Projection#getTypes(org.hibernate.Criteria,
			 *      org.hibernate.criterion.CriteriaQuery)
			 */
			public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
				return new Type[] { Hibernate.DOUBLE };
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.hibernate.criterion.Projection#toSqlString(org.hibernate.Criteria,
			 *      int, org.hibernate.criterion.CriteriaQuery)
			 */
			public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
				String columnName = criteriaQuery.getColumn(criteria, locationColumnName);
				StringBuffer sb = new StringBuffer();
				String sqlString = sb.append(distanceFunction).append("(").append(columnName).append(", st_GeometryFromText( 'POINT(").append(point.getX()).append(" ").append(point.getY()).append(")',").append(SRID.WGS84_SRID.getSRID()).append(")) as y").append(position).append("_").toString();
				return sqlString;
			}

		};

	}

}
