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
			street.setStreetRef(openstreetmap.getStreetRef());
			street.setLength(openstreetmap.getLength());
			street.setOneWay(openstreetmap.isOneWay());
			street.setIsIn(openstreetmap.getIsIn());
			street.setIsInPlace(openstreetmap.getIsInPlace());
			
			street.setIsInZip(openstreetmap.getIsInZip());
			street.setZipCode(openstreetmap.getZipCode());
			
			street.setIsInAdm(openstreetmap.getIsInAdm());
			street.setAdm1Name(openstreetmap.getAdm1Name());
			street.setAdm2Name(openstreetmap.getAdm2Name());
			street.setAdm3Name(openstreetmap.getAdm3Name());
			street.setAdm4Name(openstreetmap.getAdm4Name());
			street.setAdm5Name(openstreetmap.getAdm5Name());
			street.setFullyQualifiedName(openstreetmap.getFullyQualifiedName());
			street.setPopulation(openstreetmap.getPopulation());
			street.setHouseNumbers(openstreetmap.getHouseNumbers());
			street.setLanes(openstreetmap.getLanes());
			street.setToll(openstreetmap.isToll());
			street.setSurface(openstreetmap.getSurface());
			street.setMaxSpeed(openstreetmap.getMaxSpeed());
			street.setSpeedMode(openstreetmap.getSpeedMode());
			street.setMaxSpeedBackward(openstreetmap.getMaxSpeedBackward());
			street.setAzimuthStart(openstreetmap.getAzimuthStart());
			street.setAzimuthEnd(openstreetmap.getAzimuthEnd());

			street.setLabel(openstreetmap.getLabel());
			street.setLabelPostal(openstreetmap.getLabelPostal());
			if (openstreetmap.getAlternateLabels()!=null){
				street.addAlternateLabels(openstreetmap.getAlternateLabels());
			}
			if (openstreetmap.getAlternateNames()!=null){
				for (AlternateOsmName alternateOsmName : openstreetmap.getAlternateNames()){
					street.addAlternateName(new AlternateName(alternateOsmName.getName(),alternateOsmName.getSource(),openstreetmap.getCountryCode()));

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
