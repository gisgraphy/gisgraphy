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
package com.gisgraphy.reversegeocoding;

import java.util.SortedSet;

import org.springframework.stereotype.Service;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.helper.GeolocHelper;
import com.vividsolutions.jts.geom.Point;


/**
 * Some useful method for housenumber
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Service
public class AddressHelper {
	
	public  HouseNumberDistance getNearestHouse(SortedSet<HouseNumber> houses, Point location){
		if (location==null || houses==null || houses.size()==0){
			return null;
		}
		if (houses.size()==1){
			HouseNumber housenumber = houses.first();
			if (housenumber!=null){
				if (housenumber.getLocation()!=null){
					return new HouseNumberDistance(housenumber, GeolocHelper.distance(housenumber.getLocation(), location));
				} else {
					return new HouseNumberDistance(housenumber, null);
				}
			} else {
				return null;
			}
			
		} else {
			Double smallestDistance = null;
			HouseNumber nearestHouse = null;
			for (HouseNumber house:houses){
				if (house!=null && house.getLocation()!=null){
					Double distance = GeolocHelper.distance(house.getLocation(), location) ;
					if (nearestHouse==null){
						smallestDistance=distance;
						nearestHouse= house;
						continue;
					} else if (distance <= smallestDistance){
						nearestHouse = house;
						smallestDistance=distance;
					} //house number are sorted by name, we can may be consider that if distance is greater is increase the last smallest distance is the nearest, but it is not,
					//the street can have circle or strange shape
				}
			}
			return new HouseNumberDistance(nearestHouse, smallestDistance);
		}
	}
	
	public  Address buildAddressFromOpenstreetMap(OpenStreetMap openStreetMap) {
		if (openStreetMap==null){
			return null;
		}
		Address address = new Address();
		if (openStreetMap.getName()!=null){
			address.setStreetName(openStreetMap.getName());
		}
		if (openStreetMap.getIsIn()!=null){
			address.setCity(openStreetMap.getIsIn());
		}
		if (openStreetMap.getIsInPlace()!=null){
			address.setCitySubdivision(openStreetMap.getIsInPlace());
		}
		if (openStreetMap.getIsInAdm()!=null){
			address.setState(openStreetMap.getIsInAdm());
		}
		if (openStreetMap.getIsInZip()!=null && openStreetMap.getIsInZip().size() >=1){  
			address.setZipCode(openStreetMap.getIsInZip().iterator().next());
		}
		if (openStreetMap.getLocation()!=null){
			address.setLng(openStreetMap.getLongitude());
			address.setLat(openStreetMap.getLatitude());
		}
		if (openStreetMap.getCountryCode()!=null){
			address.setCountryCode(openStreetMap.getCountryCode());
		}
		address.setGeocodingLevel(GeocodingLevels.STREET);//We set it and don't calculate it cause if streetname is null
		//geocoding level will be street
		
		return address;
	}

	public  Address buildAddressFromOpenstreetMapAndPoint(OpenStreetMap openStreetMap, Point point) {
		if (openStreetMap==null || point == null){
			return null;
		}
		Address address = buildAddressFromOpenstreetMap(openStreetMap);
		if (openStreetMap.getLocation()!=null){
			address.setDistance(GeolocHelper.distance(point, openStreetMap.getLocation()));
		}
		
		return address;
	}

	public  Address buildAddressFromHouseNumberDistance(HouseNumberDistance houseNumberDistance) {
		if (houseNumberDistance==null || houseNumberDistance.getHouseNumber()==null){
			return null;
		}
		Address address = buildAddressFromOpenstreetMap(houseNumberDistance.getHouseNumber().getStreet());
		if (houseNumberDistance.getHouseNumber().getLatitude()!=null){
			address.setLat(houseNumberDistance.getHouseNumber().getLatitude());
		}
		if (houseNumberDistance.getHouseNumber().getLongitude()!=null){
			address.setLng(houseNumberDistance.getHouseNumber().getLongitude());
		}
		if (houseNumberDistance.getHouseNumber().getNumber()!=null){
			address.setHouseNumber(houseNumberDistance.getHouseNumber().getNumber());
		}
		if (houseNumberDistance.getHouseNumber().getName()!=null){
			address.setName(houseNumberDistance.getHouseNumber().getName());
		}
		address.setDistance(houseNumberDistance.getDistance());
		address.setGeocodingLevel(GeocodingLevels.HOUSE_NUMBER);
		return address;
	}

}
