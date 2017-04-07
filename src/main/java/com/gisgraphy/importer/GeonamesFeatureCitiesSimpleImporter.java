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
package com.gisgraphy.importer;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;

/**
 * Import the Features from a Geonames dump file.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeonamesFeatureCitiesSimpleImporter extends GeonamesFeatureSimpleImporter {

	protected static final Logger logger = LoggerFactory.getLogger(GeonamesFeatureCitiesSimpleImporter.class);
	
	 protected void onFileProcessed(File file){
		 logger.info("Don't rename files because they are needed for a next importer");
	 }
   
	 
	 @Override
	protected void setIsInFields(GisFeature poi) {
		// we don't want to set is in fields for city, just pois so we override
	}
    
    public boolean shouldImportPlaceType(GisFeature feature){
    	if (feature == null || feature instanceof City || feature instanceof CitySubdivision){
    		return true;
    	}
    	return false;
    }
    
    @Override
    Integer getImportKey() {
    	return 0;
    }


}
