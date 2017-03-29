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

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.PositionSubstringFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.type.CustomType;
import org.hibernate.type.MaterializedBlobType;
import org.hibernate.usertype.UserType;
import org.hibernatespatial.SpatialDialect;
import org.hibernatespatial.SpatialFunction;
import org.hibernatespatial.SpatialRelation;

import com.gisgraphy.domain.repository.DatabaseHelper;

/**
 * Extends the PostgreSQLDialect by also including information on spatial
 * operators, constructors and processing functions.
 * 
 * @author Karel Maesen
 */
public class PostgisDialectNG extends PostgreSQLDialect //implements SpatialDialect
{

	public PostgisDialectNG() {
		super();
		registerColumnType(java.sql.Types.STRUCT, "geometry");

        // Gisgraphy text normalization function
		registerFunction(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME, new StandardSQLFunction(DatabaseHelper.NORMALIZE_TEXT_FUNCTION_NAME, Hibernate.INTEGER));

		// registering SQL/MM Spatial functions
		registerFunction("st_dimension", new StandardSQLFunction("st_dimension", Hibernate.INTEGER));
		registerFunction("st_geometrytype", new StandardSQLFunction("st_geometrytype", Hibernate.STRING));
		registerFunction("st_srid", new StandardSQLFunction("st_srid", Hibernate.INTEGER));
		registerFunction("st_envelope", new StandardSQLFunction("st_envelope", new CustomType(new PGGeometryUserType())));
		registerFunction("st_astext", new StandardSQLFunction("st_astext", Hibernate.STRING));
		registerFunction("st_asbinary", new StandardSQLFunction("st_asbinary", Hibernate.BINARY));
		registerFunction("st_isempty", new StandardSQLFunction("st_isempty", Hibernate.BOOLEAN));
		registerFunction("st_issimple", new StandardSQLFunction("st_issimple", Hibernate.BOOLEAN));
		registerFunction("st_boundary", new StandardSQLFunction("st_boundary", new CustomType(new PGGeometryUserType())));

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
		registerFunction("st_buffer", new StandardSQLFunction("st_buffer", new CustomType(new PGGeometryUserType())));
		registerFunction("st_convexhull", new StandardSQLFunction("st_convexhull", new CustomType(new PGGeometryUserType())));
		registerFunction("st_difference", new StandardSQLFunction("st_difference", new CustomType(new PGGeometryUserType())));
		registerFunction("st_intersection", new StandardSQLFunction("st_intersection", new CustomType(new PGGeometryUserType())));
		registerFunction("st_symdifference", new StandardSQLFunction("st_symdifference", new CustomType(new PGGeometryUserType())));
		registerFunction("st_union", new StandardSQLFunction("st_union", new CustomType(new PGGeometryUserType())));
		registerKeyword("&&");
		
		
		registerColumnType( Types.BIT, "bool" );
		registerColumnType( Types.BIGINT, "int8" );
		registerColumnType( Types.SMALLINT, "int2" );
		registerColumnType( Types.TINYINT, "int2" );
		registerColumnType( Types.INTEGER, "int4" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float4" );
		registerColumnType( Types.DOUBLE, "float8" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.BINARY, "bytea" );
		registerColumnType( Types.VARBINARY, "bytea" );
		registerColumnType( Types.LONGVARCHAR, "text" );
		registerColumnType( Types.LONGVARBINARY, "bytea" );
		registerColumnType( Types.CLOB, "text" );
		registerColumnType( Types.BLOB, "oid" );
		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
		registerColumnType( Types.OTHER, "uuid" );

		registerFunction( "abs", new StandardSQLFunction("abs") );
		registerFunction( "sign", new StandardSQLFunction("sign", Hibernate.INTEGER) );

		registerFunction( "acos", new StandardSQLFunction("acos", Hibernate.DOUBLE) );
		registerFunction( "asin", new StandardSQLFunction("asin", Hibernate.DOUBLE) );
		registerFunction( "atan", new StandardSQLFunction("atan", Hibernate.DOUBLE) );
		registerFunction( "cos", new StandardSQLFunction("cos", Hibernate.DOUBLE) );
		registerFunction( "cot", new StandardSQLFunction("cot", Hibernate.DOUBLE) );
		registerFunction( "exp", new StandardSQLFunction("exp", Hibernate.DOUBLE) );
		registerFunction( "ln", new StandardSQLFunction("ln", Hibernate.DOUBLE) );
		registerFunction( "log", new StandardSQLFunction("log", Hibernate.DOUBLE) );
		registerFunction( "sin", new StandardSQLFunction("sin", Hibernate.DOUBLE) );
		registerFunction( "sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE) );
		registerFunction( "cbrt", new StandardSQLFunction("cbrt", Hibernate.DOUBLE) );
		registerFunction( "tan", new StandardSQLFunction("tan", Hibernate.DOUBLE) );
		registerFunction( "radians", new StandardSQLFunction("radians", Hibernate.DOUBLE) );
		registerFunction( "degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE) );

		registerFunction( "stddev", new StandardSQLFunction("stddev", Hibernate.DOUBLE) );
		registerFunction( "variance", new StandardSQLFunction("variance", Hibernate.DOUBLE) );

		registerFunction( "random", new NoArgSQLFunction("random", Hibernate.DOUBLE) );

		registerFunction( "round", new StandardSQLFunction("round") );
		registerFunction( "trunc", new StandardSQLFunction("trunc") );
		registerFunction( "ceil", new StandardSQLFunction("ceil") );
		registerFunction( "floor", new StandardSQLFunction("floor") );

		registerFunction( "chr", new StandardSQLFunction("chr", Hibernate.CHARACTER) );
		registerFunction( "lower", new StandardSQLFunction("lower") );
		registerFunction( "upper", new StandardSQLFunction("upper") );
		registerFunction( "substr", new StandardSQLFunction("substr", Hibernate.STRING) );
		registerFunction( "initcap", new StandardSQLFunction("initcap") );
		registerFunction( "to_ascii", new StandardSQLFunction("to_ascii") );
		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", Hibernate.STRING) );
		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", Hibernate.STRING) );
		registerFunction( "md5", new StandardSQLFunction("md5") );
		registerFunction( "ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER) );
		registerFunction( "char_length", new StandardSQLFunction("char_length", Hibernate.LONG) );
		registerFunction( "bit_length", new StandardSQLFunction("bit_length", Hibernate.LONG) );
		registerFunction( "octet_length", new StandardSQLFunction("octet_length", Hibernate.LONG) );

		registerFunction( "age", new StandardSQLFunction("age") );
		registerFunction( "current_date", new NoArgSQLFunction("current_date", Hibernate.DATE, false) );
		registerFunction( "current_time", new NoArgSQLFunction("current_time", Hibernate.TIME, false) );
		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", Hibernate.TIMESTAMP, false) );
		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", Hibernate.TIMESTAMP ) );
		registerFunction( "localtime", new NoArgSQLFunction("localtime", Hibernate.TIME, false) );
		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", Hibernate.TIMESTAMP, false) );
		registerFunction( "now", new NoArgSQLFunction("now", Hibernate.TIMESTAMP) );
		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", Hibernate.STRING) );

		registerFunction( "current_user", new NoArgSQLFunction("current_user", Hibernate.STRING, false) );
		registerFunction( "session_user", new NoArgSQLFunction("session_user", Hibernate.STRING, false) );
		registerFunction( "user", new NoArgSQLFunction("user", Hibernate.STRING, false) );
		registerFunction( "current_database", new NoArgSQLFunction("current_database", Hibernate.STRING, true) );
		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", Hibernate.STRING, true) );
		
		registerFunction( "to_char", new StandardSQLFunction("to_char", Hibernate.STRING) );
		registerFunction( "to_date", new StandardSQLFunction("to_date", Hibernate.DATE) );
		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", Hibernate.TIMESTAMP) );
		registerFunction( "to_number", new StandardSQLFunction("to_number", Hibernate.BIG_DECIMAL) );

		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "(","||",")" ) );

		registerFunction( "locate", new PositionSubstringFunction() );

		registerFunction( "str", new SQLFunctionTemplate(Hibernate.STRING, "cast(?1 as varchar)") );

		addTypeOverride( MaterializedBlobType.INSTANCE.getAlternatives().getLobBindingType() );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
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
