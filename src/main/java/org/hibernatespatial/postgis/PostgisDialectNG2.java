/**
 * $Id: PostgisDialect.java 289 2011-02-15 21:34:56Z maesenka $
 *
 * This file is part of Hibernate Spatial, an extension to the 
 * hibernate ORM solution for geographic data. 
 *
 * Copyright © 2007 Geovise BVBA
 * Copyright © 2007 K.U. Leuven LRD, Spatial Applications Division, Belgium
 *
 * This work was partially supported by the European Commission, 
 * under the 6th Framework Programme, contract IST-2-004688-STP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, visit: http://www.hibernatespatial.org/
 */
package org.hibernatespatial.postgis;


import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.CustomType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.hibernatespatial.SpatialAggregate;
import org.hibernatespatial.SpatialDialect;
import org.hibernatespatial.SpatialFunction;
import org.hibernatespatial.SpatialRelation;

/**
 * Extends the PostgreSQLDialect by also including information on spatial
 * operators, constructors and processing functions.
 *
 * @author Karel Maesen
 */
public class PostgisDialectNG2 extends PostgreSQLDialect implements SpatialDialect {

    protected static final Type geometryCustomType = new CustomType(new PGGeometryUserType(), new String[]{"postgis_geometry"});

    public PostgisDialectNG2() {
        super();
        registerTypesAndFunctions();

    }

    protected void registerTypesAndFunctions() {
        registerColumnType(java.sql.Types.STRUCT, "geometry");

        // registering OGC functions
        // (spec_simplefeatures_sql_99-04.pdf)

        // section 2.1.1.1
        // Registerfunction calls for registering geometry functions:
        // first argument is the OGC standard functionname, second the name as
        // it occurs in the spatial dialect
        registerFunction("dimension", new StandardSQLFunction("st_dimension",
                StandardBasicTypes.INTEGER));
        registerFunction("geometrytype", new StandardSQLFunction(
                "st_geometrytype", StandardBasicTypes.STRING));
        registerFunction("srid", new StandardSQLFunction("st_srid",
                StandardBasicTypes.INTEGER));
        registerFunction("envelope", new StandardSQLFunction("st_envelope",
                geometryCustomType));
        registerFunction("st_astext", new StandardSQLFunction("st_astext",
                StandardBasicTypes.STRING));
        registerFunction("asbinary", new StandardSQLFunction("st_asbinary",
                StandardBasicTypes.BINARY));
        registerFunction("isempty", new StandardSQLFunction("st_isempty",
                StandardBasicTypes.BOOLEAN));
        registerFunction("issimple", new StandardSQLFunction("st_issimple",
                StandardBasicTypes.BOOLEAN));
        registerFunction("boundary", new StandardSQLFunction("st_boundary",
                geometryCustomType));

        // Register functions for spatial relation constructs
        registerFunction("overlaps", new StandardSQLFunction("st_overlaps",
                StandardBasicTypes.BOOLEAN));
        registerFunction("intersects", new StandardSQLFunction("st_intersects",
                StandardBasicTypes.BOOLEAN));
        registerFunction("equals", new StandardSQLFunction("st_equals",
                StandardBasicTypes.BOOLEAN));
        registerFunction("contains", new StandardSQLFunction("st_contains",
                StandardBasicTypes.BOOLEAN));
        registerFunction("crosses", new StandardSQLFunction("st_crosses",
                StandardBasicTypes.BOOLEAN));
        registerFunction("disjoint", new StandardSQLFunction("st_disjoint",
                StandardBasicTypes.BOOLEAN));
        registerFunction("touches", new StandardSQLFunction("st_touches",
                StandardBasicTypes.BOOLEAN));
        registerFunction("within", new StandardSQLFunction("st_within",
                StandardBasicTypes.BOOLEAN));
        registerFunction("relate", new StandardSQLFunction("st_relate",
                StandardBasicTypes.BOOLEAN));

        // register the spatial analysis functions
        registerFunction("distance", new StandardSQLFunction("st_distance",
                StandardBasicTypes.DOUBLE));
        registerFunction("buffer", new StandardSQLFunction("st_buffer",
                geometryCustomType));
        registerFunction("convexhull", new StandardSQLFunction("st_convexhull",
                geometryCustomType));
        registerFunction("difference", new StandardSQLFunction("st_difference",
                geometryCustomType));
        registerFunction("intersection", new StandardSQLFunction(
                "st_intersection", geometryCustomType));
        registerFunction("symdifference",
                new StandardSQLFunction("st_symdifference", geometryCustomType));
        registerFunction("geomunion", new StandardSQLFunction("st_union",
                geometryCustomType));

        //register Spatial Aggregate function
        registerFunction("extent", new StandardSQLFunction("extent",
                geometryCustomType));

        //other common functions
        registerFunction("dwithin", new StandardSQLFunction("st_dwithin",
                StandardBasicTypes.BOOLEAN));
        registerFunction("transform", new StandardSQLFunction("st_transform",
                geometryCustomType));
        
        
        
		registerFunction("st_distance_sphere", new StandardSQLFunction("st_distance_sphere", StandardBasicTypes.DOUBLE));
		registerFunction("st_line_locate_point", new StandardSQLFunction("st_line_locate_point", StandardBasicTypes.DOUBLE));
		registerKeyword("&&");
        
        
        
    }

    public String getSpatialRelateSQL(String columnName, int spatialRelation) {
        switch (spatialRelation) {
            case SpatialRelation.WITHIN:
                return " ST_within(" + columnName + ",?)";
            case SpatialRelation.CONTAINS:
                return " ST_contains(" + columnName + ", ?)";
            case SpatialRelation.CROSSES:
                return " ST_crosses(" + columnName + ", ?)";
            case SpatialRelation.OVERLAPS:
                return " ST_overlaps(" + columnName + ", ?)";
            case SpatialRelation.DISJOINT:
                return " ST_disjoint(" + columnName + ", ?)";
            case SpatialRelation.INTERSECTS:
                return " ST_intersects(" + columnName
                        + ", ?)";
            case SpatialRelation.TOUCHES:
                return " ST_touches(" + columnName + ", ?)";
            case SpatialRelation.EQUALS:
                return " ST_equals(" + columnName + ", ?)";
            default:
                throw new IllegalArgumentException(
                        "Spatial relation is not known by this dialect");
        }

    }

    public String getDWithinSQL(String columnName) {
        return "ST_DWithin(" + columnName + ",?,?)";
    }

    public String getHavingSridSQL(String columnName) {
        return "( ST_srid(" + columnName + ") = ?)";
    }

    public String getIsEmptySQL(String columnName, boolean isEmpty) {
        String emptyExpr = " ST_IsEmpty(" + columnName + ") ";
        return isEmpty ? emptyExpr : "( NOT " + emptyExpr + ")";
    }

    public String getSpatialFilterExpression(String columnName) {
        return "(" + columnName + " && ? ) ";
    }

    public UserType getGeometryUserType() {
        return new PGGeometryUserType();
    }

    public String getSpatialAggregateSQL(String columnName, int aggregation) {
        switch (aggregation) {
            case SpatialAggregate.EXTENT:
                StringBuilder stbuf = new StringBuilder();
                stbuf.append("extent(").append(columnName).append(")");
                return stbuf.toString();
            default:
                throw new IllegalArgumentException("Aggregation of type "
                        + aggregation + " are not supported by this dialect");
        }
    }

    public String getDbGeometryTypeName() {
        return "GEOMETRY";
    }

    public boolean isTwoPhaseFiltering() {
        return true;
    }

    public boolean supportsFiltering() {
        return true;
    }

    public boolean supports(SpatialFunction function) {
        return (getFunctions().get(function.toString()) != null);
    }
}