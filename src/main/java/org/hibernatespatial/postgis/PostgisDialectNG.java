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
package org.hibernatespatial.postgis;

import org.hibernate.Hibernate;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.CustomType;
import org.hibernate.usertype.UserType;
import org.hibernatespatial.SpatialDialect;
import org.hibernatespatial.SpatialRelation;

import com.gisgraphy.domain.repository.DatabaseHelper;

/**
 * Extends the PostgreSQLDialect by also including information on spatial
 * operators, constructors and processing functions.
 * 
 * @author Karel Maesen
 */
public class PostgisDialectNG extends PostgreSQLDialect implements SpatialDialect {

	public PostgisDialectNG() {
		super();
		registerColumnType(java.sql.Types.STRUCT, "geometry");

        // Gisgraphy text normalization function
		registerFunction(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME, new StandardSQLFunction(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME, Hibernate.INTEGER));

		// registering SQL/MM Spatial functions
		registerFunction("st_dimension", new StandardSQLFunction("st_dimension", Hibernate.INTEGER));
		registerFunction("st_geometrytype", new StandardSQLFunction("st_geometrytype", Hibernate.STRING));
		registerFunction("st_srid", new StandardSQLFunction("st_srid", Hibernate.INTEGER));
		registerFunction("st_envelope", new StandardSQLFunction("st_envelope", new CustomType(PGGeometryUserType.class, null)));
		registerFunction("st_astext", new StandardSQLFunction("st_astext", Hibernate.STRING));
		registerFunction("st_asbinary", new StandardSQLFunction("st_asbinary", Hibernate.BINARY));
		registerFunction("st_isempty", new StandardSQLFunction("st_isempty", Hibernate.BOOLEAN));
		registerFunction("st_issimple", new StandardSQLFunction("st_issimple", Hibernate.BOOLEAN));
		registerFunction("st_boundary", new StandardSQLFunction("st_boundary", new CustomType(PGGeometryUserType.class, null)));

		// Register functions for spatial relation constructs
		registerFunction("st_overlaps", new StandardSQLFunction("st_overlaps", Hibernate.BOOLEAN));
		registerFunction("st_intersects", new StandardSQLFunction("st_intersects", Hibernate.BOOLEAN));
		registerFunction("st_equals", new StandardSQLFunction("st_equals", Hibernate.BOOLEAN));
		registerFunction("st_contains", new StandardSQLFunction("st_contains", Hibernate.BOOLEAN));
		registerFunction("st_crosses", new StandardSQLFunction("st_crosses", Hibernate.BOOLEAN));
		registerFunction("st_disjoint", new StandardSQLFunction("st_disjoint", Hibernate.BOOLEAN));
		registerFunction("st_touches", new StandardSQLFunction("st_touches", Hibernate.BOOLEAN));
		registerFunction("st_within", new StandardSQLFunction("st_within", Hibernate.BOOLEAN));
		registerFunction("st_relate", new StandardSQLFunction("st_relate", Hibernate.BOOLEAN));

		// register the spatial analysis functions
		registerFunction("st_distance", new StandardSQLFunction("st_distance", Hibernate.DOUBLE));
		registerFunction("st_distance_sphere", new StandardSQLFunction("st_distance_sphere", Hibernate.DOUBLE));
		registerFunction("st_line_locate_point", new StandardSQLFunction("st_line_locate_point", Hibernate.DOUBLE));
		registerFunction("st_buffer", new StandardSQLFunction("st_buffer", new CustomType(PGGeometryUserType.class, null)));
		registerFunction("st_convexhull", new StandardSQLFunction("st_convexhull", new CustomType(PGGeometryUserType.class, null)));
		registerFunction("st_difference", new StandardSQLFunction("st_difference", new CustomType(PGGeometryUserType.class, null)));
		registerFunction("st_intersection", new StandardSQLFunction("st_intersection", new CustomType(PGGeometryUserType.class, null)));
		registerFunction("st_symdifference", new StandardSQLFunction("st_symdifference", new CustomType(PGGeometryUserType.class, null)));
		registerFunction("st_union", new StandardSQLFunction("st_union", new CustomType(PGGeometryUserType.class, null)));
		registerKeyword("&&");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.walkonweb.spatial.dialect.SpatialEnabledDialect#getSpatialRelateExpression(java.lang.String,
	 *      int, boolean)
	 */
	public String getSpatialRelateSQL(String columnName, int spatialRelation, boolean hasFilter) {
		switch (spatialRelation) {
			case SpatialRelation.WITHIN:
				return hasFilter ? "(" + columnName + " && ? AND st_within(" + columnName + ", ?))" : " st_within(" + columnName + ",?)";
			case SpatialRelation.CONTAINS:
				return hasFilter ? "(" + columnName + " && ? AND st_contains(" + columnName + ", ?))" : " st_contains(" + columnName + ", ?)";
			case SpatialRelation.CROSSES:
				return hasFilter ? "(" + columnName + " && ? AND st_crosses(" + columnName + ", ?))" : " st_crosses(" + columnName + ", ?)";
			case SpatialRelation.OVERLAPS:
				return hasFilter ? "(" + columnName + " && ? AND st_overlaps(" + columnName + ", ?))" : " st_overlaps(" + columnName + ", ?)";
			case SpatialRelation.DISJOINT:
				return hasFilter ? "(" + columnName + " && ? AND st_disjoint(" + columnName + ", ?))" : " st_disjoint(" + columnName + ", ?)";
			case SpatialRelation.INTERSECTS:
				return hasFilter ? "(" + columnName + " && ? AND st_intersects(" + columnName + ", ?))" : " st_intersects(" + columnName + ", ?)";
			case SpatialRelation.TOUCHES:
				return hasFilter ? "(" + columnName + " && ? AND st_touches(" + columnName + ", ?))" : " st_touches(" + columnName + ", ?)";
			case SpatialRelation.EQUALS:
				return hasFilter ? "(" + columnName + " && ? AND st_equals(" + columnName + ", ?))" : " st_equals(" + columnName + ", ?)";
			default:
				throw new IllegalArgumentException("Spatial relation is not known by this dialect");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.walkonweb.spatial.dialect.SpatialEnabledDialect#getSpatialFilterExpression(java.lang.String)
	 */
	public String getSpatialFilterExpression(String columnName) {
		return "(" + columnName + " && ? ) ";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernatespatial.SpatialDialect#getGeometryUserType()
	 */
	public UserType getGeometryUserType() {
		return new PGGeometryUserType();
	}

}
