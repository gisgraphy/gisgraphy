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
package com.gisgraphy.street;

import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.Street;


/**
 * Factory to create a street from an openstreetMap entity
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class StreetFactory implements IStreetFactory {

   /* (non-Javadoc)
 * @see com.gisgraphy.domain.geoloc.service.geoloc.street.IStreetFactory#create(com.gisgraphy.domain.geoloc.entity.OpenStreetMap)
 */
public Street create(OpenStreetMap openstreetmap){
       if (openstreetmap !=null){
	   Street street = new Street();
	   street.setOpenstreetmapId(openstreetmap.getOpenstreetmapId());
	   street.setFeatureId(openstreetmap.getGid());
	   street.setName(openstreetmap.getName());
	   street.setLocation(openstreetmap.getLocation());
	   street.setCountryCode(openstreetmap.getCountryCode());
	   street.setStreetType(openstreetmap.getStreetType());
	   street.setLength(openstreetmap.getLength());
	   street.setOneWay(openstreetmap.isOneWay());
	   street.setIsIn(openstreetmap.getIsIn());
	   street.setIsInPlace(openstreetmap.getIsInPlace());
	   street.setIsInZip(openstreetmap.getIsInZip());
	   street.setIsInAdm(openstreetmap.getIsInAdm());
	   street.setFullyQualifiedAddress(openstreetmap.getFullyQualifiedAddress());
	   street.setPopulation(openstreetmap.getPopulation());
	   street.setHouseNumbers(openstreetmap.getHouseNumbers());
	   if (openstreetmap.getAlternateNames()!=null){
		   for (AlternateOsmName alternateOsmName : openstreetmap.getAlternateNames()){
			   street.addAlternateName(new AlternateName(alternateOsmName.getName(),alternateOsmName.getSource()));
			   
		   }
	   }
	   if (openstreetmap.getIsInCityAlternateNames()!=null){
			   street.addIsInCitiesAlternateNames(openstreetmap.getIsInCityAlternateNames());
	   }
	   return street;
       }
       return null;
   }

}
