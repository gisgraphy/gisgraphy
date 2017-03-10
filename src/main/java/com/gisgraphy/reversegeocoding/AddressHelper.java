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
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.addressparser.format.DisplayMode;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.countryInfo;
import com.gisgraphy.importer.LabelGenerator;
import com.vividsolutions.jts.geom.Point;


/**
 * Some useful method for housenumber
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Service
public class AddressHelper {
	
	BasicAddressFormater formater = BasicAddressFormater.getInstance();
	
	LabelGenerator labelGenerator = LabelGenerator.getInstance();
	
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
		return buildAddressFromOpenstreetMap(openStreetMap, new Address());
	}
	
	public  Address buildAddressFromOpenstreetMap(OpenStreetMap openStreetMap,Address address) {
		if (openStreetMap==null){
			return null;
		}
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
		if (openStreetMap.getAdm1Name()!=null){
			address.setAdm1Name(openStreetMap.getAdm1Name());
		}
		if (openStreetMap.getAdm2Name()!=null){
			address.setAdm2Name(openStreetMap.getAdm2Name());
		}
		if (openStreetMap.getAdm3Name()!=null){
			address.setAdm3Name(openStreetMap.getAdm3Name());
		}
		if (openStreetMap.getAdm4Name()!=null){
			address.setAdm4Name(openStreetMap.getAdm4Name());
		}
		if (openStreetMap.getAdm5Name()!=null){
			address.setAdm5Name(openStreetMap.getAdm5Name());
		}
		
		if (openStreetMap.isToll()!=null){
			address.setToll(openStreetMap.isToll());
		}
		if (openStreetMap.getLanes()!=null){
			address.setLanes(openStreetMap.getLanes());
		}
		if (openStreetMap.getSurface()!=null){
			address.setSurface(openStreetMap.getSurface());
		}
		
		if (openStreetMap.getMaxSpeed()!=null){
			address.setMaxSpeed(openStreetMap.getMaxSpeed());
		}
		if (openStreetMap.getMaxSpeedBackward()!=null){
			address.setMaxSpeedBackward(openStreetMap.getMaxSpeedBackward());
		}
		if (openStreetMap.getSpeedMode()!=null){
			address.setSpeedMode(openStreetMap.getSpeedMode().toString());
		}
		if (openStreetMap.getAzimuthStart()!=null){
			address.setAzimuthStart(openStreetMap.getAzimuthStart());
		}
		if (openStreetMap.getAzimuthEnd()!=null){
			address.setAzimuthEnd(openStreetMap.getAzimuthEnd());
		}
		if (openStreetMap.getStreetType()!=null){
			address.setStreetType(openStreetMap.getStreetType().toString());
		}
		if (openStreetMap.getLength()!=null){
			address.setLength(openStreetMap.getLength());
		}
		if (openStreetMap.isOneWay()){
			address.setOneWay(openStreetMap.isOneWay());
		}
		if (openStreetMap.getZipCode()!=null) {
			address.setZipCode(openStreetMap.getZipCode());
		} else 	if (openStreetMap.getIsInZip()!=null && openStreetMap.getIsInZip().size() >=1){  
			address.setZipCode(labelGenerator.getBestZipString(openStreetMap.getIsInZip()));
		}
		if (openStreetMap.getLocation()!=null){
			address.setLng(openStreetMap.getLongitude());
			address.setLat(openStreetMap.getLatitude());
		}
		if (openStreetMap.getCountryCode()!=null){
			address.setCountryCode(openStreetMap.getCountryCode());
		}
		address.setGeocodingLevel(GeocodingLevels.STREET);//We set it and don't calculate it cause if streetname is null
		
		address.setFormatedFull(labelGenerator.getFullyQualifiedName(address));
		address.setFormatedPostal(formater.getEnvelopeAddress(address, DisplayMode.COMMA));
		return address;
	}

	public  Address buildAddressFromOpenstreetMapAndPoint(OpenStreetMap openStreetMap, Point point) {
		if (openStreetMap==null || point == null){
			return null;
		}
		Address address = new Address();
		if (openStreetMap.getLocation()!=null){
			address.setDistance(GeolocHelper.distance(point, openStreetMap.getLocation()));
		}
		//we do the build address at the end because the formatedurl should take the housenumber into account
				// and we have to set it first
		address = buildAddressFromOpenstreetMap(openStreetMap,address);
		
		return address;
	}

	public  Address buildAddressFromHouseNumberDistance(HouseNumberDistance houseNumberDistance) {
		if (houseNumberDistance==null || houseNumberDistance.getHouseNumber()==null){
			return null;
		}
		Address address = new Address();
		//we do the build address at the end because the formatedurl should take the housenumber into account
		// and we have to set it first
		if (houseNumberDistance.getHouseNumber().getNumber()!=null){
			address.setHouseNumber(houseNumberDistance.getHouseNumber().getNumber());
		}
		if (houseNumberDistance.getHouseNumber().getName()!=null){
			address.setName(houseNumberDistance.getHouseNumber().getName());
		}
		address.setDistance(houseNumberDistance.getDistance());
		//then enrich the address
		address = buildAddressFromOpenstreetMap(houseNumberDistance.getHouseNumber().getStreet(),address);
		//and overide lat / long 
		if (houseNumberDistance.getHouseNumber().getLatitude()!=null){
			address.setLat(houseNumberDistance.getHouseNumber().getLatitude());
		}
		if (houseNumberDistance.getHouseNumber().getLongitude()!=null){
			address.setLng(houseNumberDistance.getHouseNumber().getLongitude());
		}
		address.setGeocodingLevel(GeocodingLevels.HOUSE_NUMBER);
		return address;
	}
	


	public Address buildAddressFromCityAndPoint(City city,Point point) {
		if (city==null || point == null){
			return null;
		}
		Address address = buildAddressFromcity(city);
		if (city.getLocation()!=null){
			address.setDistance(GeolocHelper.distance(point, city.getLocation()));
		}
		
		return address;
	}

	public Address buildAddressFromcity(City city) {
		if (city == null){
			return null;
		}
		Address address = new Address();
		if (city.getName()!=null){
			address.setCity(city.getName());
		}
		if (city.getIsInAdm()!=null){
			address.setState(city.getIsInAdm());
		}
		if (city.getAdm1Name()!=null){
			address.setAdm1Name(city.getAdm1Name());
		}
		if (city.getAdm2Name()!=null){
			address.setAdm2Name(city.getAdm2Name());
		}
		if (city.getAdm3Name()!=null){
			address.setAdm3Name(city.getAdm3Name());
		}
		if (city.getAdm4Name()!=null){
			address.setAdm4Name(city.getAdm4Name());
		}
		if (city.getAdm5Name()!=null){
			address.setAdm5Name(city.getAdm5Name());
		}
		if (city.getZipCodes()!=null && city.getZipCodes().size() >=1){  
			address.setZipCode(city.getZipCodes().iterator().next().toString());
		}
		if (city.getLocation()!=null){
			address.setLng(city.getLongitude());
			address.setLat(city.getLatitude());
		}
		if (city.getCountryCode()!=null){
			address.setCountryCode(city.getCountryCode());
		}
		address.setGeocodingLevel(GeocodingLevels.CITY);//We set it and don't calculate it cause if streetname is null
		//geocoding level will be street
		
		address.setFormatedFull(labelGenerator.getFullyQualifiedName(address));
		address.setFormatedPostal(formater.getEnvelopeAddress(address, DisplayMode.COMMA));
		return address;
	}


}
