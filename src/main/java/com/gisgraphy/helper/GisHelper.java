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
package com.gisgraphy.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.FeatureCode;
import com.gisgraphy.domain.valueobject.SRID;
import com.vividsolutions.jts.geom.Point;

/**
 * Provides useful methods for geolocalisation
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GisHelper {

    private static final double COS0 = Math.cos(0);
    private static final double SIN90 = Math.sin(90);
    
    private static final String INTERSECTION = "&&";
    private static final String BBOX = "BOX3D";
    
    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(GeolocHelper.class);


    /**
     * Return the class corresponding to the specified String or null if not
     * found. The Class will be searched in the 'entity' package. The search is
     * not case sensitive. This method is mainly used to determine an entity
     * Class from a web parameter
     * 
     * @param classNameWithoutPackage
     *                the simple name of the Class we want to retrieve
     * @return The class corresponding to the specified String or null if not
     *         found.
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends GisFeature> getClassEntityFromString(
	    String classNameWithoutPackage) {
	if (classNameWithoutPackage != null) {
	    return FeatureCode.entityClass.get(classNameWithoutPackage
		    .toLowerCase());
	}
	return null;
    }

   

   
    

    /**
     * @param alias the
     *                sql alias
     * @param latInDegree
     *                the latitude in degree
     * @param longInDegree
     *                the longitude in degree
     * @param distance
     *                the boundingbox distance
     * @return a sql String that represents the bounding box
     */
    public static String getBoundingBox(String alias, double latInDegree, double longInDegree,
	    double distance) {
    
    double lat = Math.toRadians(latInDegree);
    double lon = Math.toRadians(longInDegree);

	double deltaXInDegrees = Math.abs(
			Math.asin(
					Math.sin(distance / Constants.RADIUS_OF_EARTH_IN_METERS)/ Math.cos(lat)
					)
		);
if (Double.isNaN(deltaXInDegrees)){
	deltaXInDegrees=0;
}
double deltaYInDegrees = Math.abs(distance
	/ Constants.RADIUS_OF_EARTH_IN_METERS);
if (Double.isNaN(deltaYInDegrees)){
	deltaYInDegrees=0;
}
double minX = Math.toDegrees(lon - deltaXInDegrees);
if (Double.isNaN(minX)){minX=lon;}
double maxX = Math.toDegrees(lon + deltaXInDegrees);
if (Double.isNaN(maxX)){minX=lon;}
double minY = Math.toDegrees(lat - deltaYInDegrees);
if (Double.isNaN(minY)){minX=lat;}
double maxY = Math.toDegrees(lat + deltaYInDegrees);
if (Double.isNaN(maxY)){minX=lat;}

	StringBuffer sb = new StringBuffer();
	// {alias}.location && setSRID(BOX3D(...), 4326)
	sb.append(alias);
	sb.append(".").append(GisFeature.LOCATION_COLUMN_NAME);
	sb.append(" ");
	sb.append(INTERSECTION);
	sb.append(" st_setSRID(");

	// Construct the BBOX : 'BOX3D(-119.2705528794688
	// 33.15289952334886,-117.2150071205312 34.95154047665114)'::box3d
	sb.append("cast (");
	sb.append("'");
	sb.append(BBOX);
	sb.append("(");
	sb.append(minX); // minX
	sb.append(" ");
	sb.append(minY); // minY
	sb.append(",");
	sb.append(maxX); // maxX
	sb.append(" ");
	sb.append(maxY); // maxY
	sb.append(")'as box3d)"); // cannot use the ::box3d notation, since
	// nativeSQL interprets :param as a named
	// parameter

	// end of the BBOX, finish the setSRID
	sb.append(", ");
	sb.append(SRID.WGS84_SRID.getSRID());
	sb.append(") ");

	return sb.toString();

    }

    
    /**
     * @param alias the
     *                sql alias
     * @param latInDegree
     *                the latitude in degree
     * @param longInDegree
     *                the longitude in degree
     * @param distance
     *                the boundingbox distance
     * @return a sql String that represents an envelope
     */
    public static String makeEnvelope(String alias, double latInDegree, double longInDegree,
	    double distance) {
    
    double lat = Math.toRadians(latInDegree);
    double lon = Math.toRadians(longInDegree);

	double deltaXInDegrees = Math.abs(
				Math.asin(
						Math.sin(distance / Constants.RADIUS_OF_EARTH_IN_METERS)/ Math.cos(lat)
						)
			);
	if (Double.isNaN(deltaXInDegrees)){
		deltaXInDegrees=0;
	}
	double deltaYInDegrees = Math.abs(distance
		/ Constants.RADIUS_OF_EARTH_IN_METERS);
	if (Double.isNaN(deltaYInDegrees)){
		deltaYInDegrees=0;
	}
	double minX = Math.toDegrees(lon - deltaXInDegrees);
	if (Double.isNaN(minX)){minX=lon;}
	double maxX = Math.toDegrees(lon + deltaXInDegrees);
	if (Double.isNaN(maxX)){minX=lon;}
	double minY = Math.toDegrees(lat - deltaYInDegrees);
	if (Double.isNaN(minY)){minX=lat;}
	double maxY = Math.toDegrees(lat + deltaYInDegrees);
	if (Double.isNaN(maxY)){minX=lat;}

	//"ST_MakeEnvelope(39.875947845588854, -6.649904839690944,57.85738955675488, 11.316505067046899, 4326)";
	StringBuffer sb = new StringBuffer();
	// {alias}.location && setSRID(BOX3D(...), 4326)
	sb.append("st_contains(");
	sb.append("ST_MakeEnvelope(");
	sb.append(minX); // minX
	sb.append(", ");
	sb.append(minY); // minY
	sb.append(", ");
	sb.append(maxX); // maxX
	sb.append(", ");
	sb.append(maxY); // maxY
	sb.append(", ");
	sb.append(SRID.WGS84_SRID.getSRID());
	sb.append(")  ");
	sb.append(",");
	sb.append(alias);
	sb.append(".").append(GisFeature.LOCATION_COLUMN_NAME);
	sb.append(")=true ");

	return sb.toString();

    }
    
   
    
  

}
