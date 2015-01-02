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
package com.gisgraphy.domain.valueobject;

/**
 * An SRID enum
 * 
 * @see <a href="http://en.wikipedia.org/wiki/SRID" >SRID</a>
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public enum SRID {

    /**
     * 4326;"EPSG";4326;"GEOGCS["WGS 84"...
     */
   
    WGS84_SRID {
	@Override
	public int getSRID() {
	    return 4326;
	}
    },
    /**
     * 3395;"EPSG";3395;"PROJCS["WGS 84 / World Mercator"...
     */
    WGS84_SRID_PROJCS {
    	@Override
    	public int getSRID() {
    	    return 4326;
    	}
        };

    /**
     * @return an SRID (Spatial Reference IDentifier) *
     * @see <a href="http://en.wikipedia.org/wiki/SRID" >SRID</a>
     */
    public abstract int getSRID();
}
