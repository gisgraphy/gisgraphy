package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.commons.GeocodingLevels;
import com.gisgraphy.addressparser.format.AddressFormatInfo;
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.addressparser.format.DisplayMode;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StateAbbreviator;
import com.gisgraphy.helper.countryInfo;
import com.gisgraphy.reversegeocoding.HouseNumberDistance;
import com.vividsolutions.jts.geom.Point;

/**
 * Utility to generate labels for street, city, adm,
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class LabelGenerator {

	private BasicAddressFormater formater =  BasicAddressFormater.getInstance();

	private static LabelGenerator instance = new LabelGenerator();
	
	
	

	protected LabelGenerator() {
	}

	public static LabelGenerator getInstance() {
		return instance;
	}
	
	public String generateLabel(OpenStreetMap street) {
		if (street!=null & street.getName()!=null){
			if (street.getIsIn()!=null){
				return street.getName()+", "+street.getIsIn();
			} else {
				return street.getName();
			}
		}
		return null;
	}
	
	public String generateLabel(GisFeature feature) {
		if (feature!=null & feature.getName()!=null){
			if (feature.getIsIn()!=null){
				return feature.getName()+", "+feature.getIsIn();
			} else {
				return feature.getName();
			}
		}
		return null;
	}
	
	public String generateLabel(Adm feature) {
		if (feature!=null & feature.getName()!=null){
			if (feature.getLevel()>1  && feature.getAdm1Name()!=null){
				return feature.getName()+", "+feature.getAdm1Name();
			} else {
				return feature.getName();
			}
		}
		return null;
	}

	public Set<String> generateLabels(OpenStreetMap street) {
		Set<String> labels = new HashSet<String>();

		List<AlternateOsmName> altnames = new ArrayList<AlternateOsmName>();
		if (street.getAlternateNames() != null) {
			altnames.addAll(street.getAlternateNames());
		}
		if (street.getName() != null) {
			altnames.add(new AlternateOsmName(street.getName(),
					AlternateNameSource.PERSONAL));
		}

		List<String> altcities = new ArrayList<String>();
		if (street.getIsInCityAlternateNames() != null) {
			altcities.addAll(street.getIsInCityAlternateNames());
		}
		if (street.getIsIn() != null) {
			altcities.add(street.getIsIn());
		} else if (street.getIsInPlace()!=null){
			altcities.add(street.getIsInPlace());
		}

		if (altnames.size() > 0) {
			// some alt names
			if (altcities.size() > 0) {
				for (String city : altcities) {
					// cross all data
					if (city !=null){
					for (AlternateOsmName name : altnames) {
						if (name.getName() != null && !name.getName().startsWith("http") && !city.startsWith("http")) {
							labels.add(name.getName() + ", " + city);
						}
					}
					}
				}
			} else {
				// no cities alt names =>label = street alternatenames
				for (AlternateOsmName name : altnames) {
					if (name.getName() != null && !name.getName().startsWith("http")) {
						labels.add(name.getName());
					}
				}
				// finaly add the common name
				if (street.getName() != null && !street.getName().startsWith("http")) {
					labels.add(street.getName());
				}
			}
		}

		return labels;

	}


	public  Set<String> generateLabels(GisFeature feature) {
		Set<String> labels = new HashSet<String>();
		
		if (feature.getClass()== City.class || feature.getClass()== Adm.class ){
			if (feature.getAlternateNames() != null) {
				for (AlternateName altname:feature.getAlternateNames()){
					if (altname.getName()!=null && !altname.getName().startsWith("http")){
						labels.add(altname.getName());
					}
				}
			}
			if (feature.getName() != null && !feature.getName().startsWith("http")) {
				labels.add(feature.getName());
			}
		} else {
			//it is a poi, we put name and cities
			return generateLabelsForPois(feature);
		}
		
		
		return labels;

	}
	
	public  Set<String> generateLabelsForPois(GisFeature poi) {
		Set<String> labels = new HashSet<String>();
		List<AlternateName> altnames = new ArrayList<AlternateName>();
		if (poi.getAlternateNames() != null) {
			altnames.addAll(poi.getAlternateNames());
		}
		if (poi.getName() != null) {
			altnames.add(new AlternateName(poi.getName(),
					AlternateNameSource.PERSONAL));
		}

		List<String> altcities = new ArrayList<String>();
		if (poi.getIsInCityAlternateNames() != null) {
			altcities.addAll(poi.getIsInCityAlternateNames());
		}
		if (poi.getIsIn() != null) {
			altcities.add(poi.getIsIn());
		}

		if (altnames.size() > 0) {
			// some alt names
			if (altcities.size() > 0) {
				for (String city : altcities) {
					// cross all data
					for (AlternateName name : altnames) {
						if (name.getName() != null && !name.getName().startsWith("http") && !city.startsWith("http")) {
							labels.add(name.getName() + ", " + city);
						}
					}
				}
			} else {
				// no cities alt names =>label = alternatenames
				for (AlternateName name : altnames) {
					if (name.getName() != null && !name.getName().startsWith("http")) {
						labels.add(name.getName());
					}
				}
				// finaly add the common name
				if (poi.getName() != null && !poi.getName().startsWith("http")) {
					labels.add(poi.getName());
				}
			}
		}

		return labels;
	}
/*-------------------FQDN-----------------------------------*/

	
	public String getFullyQualifiedName(GisFeature gisFeature,
			boolean withCountry) {
		StringBuilder completeCityName = new StringBuilder();
		if (gisFeature.getName()!=null){
			completeCityName.append(gisFeature.getName());
		}
		String lastname = "";
		String isInPlace = gisFeature.getIsInPlace();
		if (isInPlace != null && !isInPlace.trim().equals("")) {
			completeCityName.append(", " + isInPlace);
			lastname = isInPlace;
		}
		String isIn = gisFeature.getIsIn();
		if (isIn != null && !isIn.trim().equals("")) {
			completeCityName.append(", " + isIn);
			lastname = isIn;
		}
		String adm5Name = gisFeature.getAdm5Name();
		if (adm5Name != null && !adm5Name.trim().equals("")) {
			completeCityName.append(", " + adm5Name);
			lastname = adm5Name;
		}
		String adm4Name = gisFeature.getAdm4Name();
		if (adm4Name != null && !adm4Name.trim().equals("") && !adm4Name.equalsIgnoreCase(lastname)) {
			completeCityName.append(", " + adm4Name);
			lastname = adm4Name;
		}
		String adm3Name = gisFeature.getAdm3Name();
		if (adm3Name != null && !adm3Name.trim().equals("") && !adm3Name.equalsIgnoreCase(lastname)) {
			completeCityName.append(", " + adm3Name);
			lastname = adm3Name;
		}
		String adm2Name = gisFeature.getAdm2Name();
		if (adm2Name != null && !adm2Name.trim().equals("")&& !adm2Name.equalsIgnoreCase(lastname)) {
			completeCityName.append(", " + adm2Name);
			lastname = adm2Name;
		}
		String adm1Name = gisFeature.getAdm1Name();
		if (adm1Name != null && !adm1Name.trim().equals("") && !adm1Name.equalsIgnoreCase(lastname)) {
			if (gisFeature.getCountryCode()!=null){
				completeCityName.append(", " + StateAbbreviator.addStateCode(gisFeature.getCountryCode(),adm1Name));
			} else {
				completeCityName.append(", " + adm1Name);
			}
		}
		if (gisFeature instanceof City){
			Set<ZipCode> zipCodes = gisFeature.getZipCodes();
			String bestZip = getBestZip(zipCodes);
			if (bestZip != null) {
				completeCityName.append(", (");
				completeCityName.append(bestZip);
				completeCityName.append(")");
			}
			}

		if (withCountry && gisFeature.getCountryCode() != null) {
			String country = getCountry(gisFeature.getCountryCode());
			if (country != null) {
				completeCityName.append(" , " + country);
			}
		}
		if (completeCityName.length()==0){
			return null;
		} else {
			return completeCityName.toString();
		}
	}
	
	
	public String getFullyQualifiedName(OpenStreetMap osm,
			boolean withCountry) {
		StringBuilder completeCityName = new StringBuilder();
		if (osm.getName()!=null){
			completeCityName.append(osm.getName());
		}
		String lastname = "";
		String isInPlace = osm.getIsInPlace();
		if (isInPlace != null && !isInPlace.trim().equals("")) {
			completeCityName.append(", " + isInPlace);
			lastname = isInPlace;
		}
		String isIn = osm.getIsIn();
		if (isIn != null && !isIn.trim().equals("")) {
			completeCityName.append(", " + isIn);
			lastname = isIn;
		}
		String adm5Name = osm.getAdm5Name();
		if (adm5Name != null && !adm5Name.trim().equals("")) {
			completeCityName.append(", " + adm5Name);
			lastname = adm5Name;
		}
		String adm4Name = osm.getAdm4Name();
		if (adm4Name != null && !adm4Name.trim().equals("") && !adm4Name.equalsIgnoreCase(lastname)) {
			completeCityName.append(", " + adm4Name);
			lastname = adm4Name;
		}
		String adm3Name = osm.getAdm3Name();
		if (adm3Name != null && !adm3Name.trim().equals("") && !adm3Name.equalsIgnoreCase(lastname)) {
			completeCityName.append(", " + adm3Name);
			lastname = adm3Name;
		}
		String adm2Name = osm.getAdm2Name();
		if (adm2Name != null && !adm2Name.trim().equals("")&& !adm2Name.equalsIgnoreCase(lastname)) {
			completeCityName.append(", " + adm2Name);
			lastname = adm2Name;
		}
		String adm1Name = osm.getAdm1Name();
		if (adm1Name != null && !adm1Name.trim().equals("") && !adm1Name.equalsIgnoreCase(lastname)) {
			if (osm.getCountryCode()!=null){
				completeCityName.append(", " + StateAbbreviator.addStateCode(osm.getCountryCode(),adm1Name));
			} else {
				completeCityName.append(", " + adm1Name);
			}
		}
		String bestZip = null;
		if (osm.getZipCode()!=null){
			bestZip = osm.getZipCode();
		}
		else if (osm.getIsInZip()!=null ){
			bestZip = getBestZipString(osm.getIsInZip());
		}
		if (bestZip != null){
			completeCityName.append(", (");
			completeCityName.append(bestZip);
			completeCityName.append(")");
		}

		if (withCountry && osm.getCountryCode() != null) {
			String country = getCountry(osm.getCountryCode());
			if (country != null) {
				completeCityName.append(" , " + country);
			}
		}
		if (completeCityName.length()==0){
			return null;
		} else {
			return completeCityName.toString();
		}
	}

	/**
	 * @return a name with the Administrative division (but without Country)
	 */
	public String getFullyQualifiedName(GisFeature gisFeature) {
		return getFullyQualifiedName(gisFeature, false);
	}
	
	public  String getFullyQualifiedName(Address address){
		StringBuffer sb = new StringBuffer();
		if (address.getHouseNumber()!=null){
			sb.append(address.getHouseNumber()).append(", ");
		}
		if (address.getStreetName()!=null){
			sb.append(address.getStreetName()).append(", ");
		}
		if (address.getDependentLocality()!=null){
			sb.append(address.getDependentLocality()).append(", ");
		}
		if (address.getDistrict()!=null){
			sb.append(address.getDistrict()).append(", ");
		}
		if (address.getQuarter()!=null){
			sb.append(address.getQuarter()).append(", ");
		}
		if (address.getCitySubdivision()!=null){
			sb.append(address.getCitySubdivision()).append(", ");
		}
		if (address.getCity()!=null){
			sb.append(address.getCity()).append(", ");
		}
		if (address.getAdm1Name()==null && address.getAdm2Name()==null && address.getAdm3Name()==null && address.getAdm4Name()==null && address.getAdm5Name()==null && address.getState()!=null ){
			sb.append(address.getState()).append(", ");
		} else {
			String lastState = "";
			if (address.getAdm5Name()!=null){
				sb.append(address.getAdm5Name()).append(", ");
				lastState = address.getAdm5Name();
			}
			if (address.getAdm4Name()!=null && !address.getAdm4Name().equalsIgnoreCase(lastState)){
				sb.append(address.getAdm4Name()).append(", ");
				lastState = address.getAdm4Name();
			}
			if (address.getAdm3Name()!=null && !address.getAdm3Name().equalsIgnoreCase(lastState)){
				sb.append(address.getAdm3Name()).append(", ");
				lastState = address.getAdm3Name();
			}
			if (address.getAdm2Name()!=null && !address.getAdm2Name().equalsIgnoreCase(lastState)){
				sb.append(address.getAdm2Name()).append(", ");
				lastState = address.getAdm2Name();
			}
			if (address.getAdm1Name()!=null && !address.getAdm1Name().equalsIgnoreCase(lastState)){
				if (address.getCountryCode()!=null){
					sb.append(StateAbbreviator.addStateCode(address.getCountryCode(),address.getAdm1Name())).append(", ");
				} else {
					sb.append(address.getAdm1Name()).append(", ");
				}
				
			}
		}
		if (address.getZipCode() != null){
			sb.append(address.getZipCode()).append(", ");
		} 
		if (address.getCountryCode()!=null){
			String countryName = countryInfo.countryLookupMap.get(address.getCountryCode().toUpperCase());
			if (countryName!=null){
				sb.append(countryName);
				//.append(", ");
			} else {
				return sb.substring(0, sb.length()-2);
			}
			//sb.append(address.getCountryCode().toUpperCase());
		}
		String str =  sb.toString();
		//System.out.println(str);
		return str;
	}
	
	/*-------------------------------------------------Postal ----------------*/

	
	
	public String generatePostal(OpenStreetMap street){
		return generatePostal(buildAddressFromOpenstreetMap(street));
	}
	
	
	public String generatePostal(Address address){
		if (address!=null && address.getAdm1Name()!=null && address.getCountryCode()!=null){
			address.setAdm1Name(StateAbbreviator.addStateCode(address.getCountryCode(),address.getAdm1Name()));
		}
		return formater.getEnvelopeAddress(address, DisplayMode.COMMA);
	}
	
	

	
	/*-------------------------------------Utilities---------------*/
	
	public String getBestZip(Collection<ZipCode> zips){
		if(zips!=null){
			if (zips.size()==1){
				return zips.iterator().next().getCode();
			}
			String bestZip=null;
			String zip;
			for (ZipCode zipCode:zips){
				if (zipCode !=null){
					zip = zipCode.getCode();
				} else {
					continue;
				}
				if (bestZip==null ||(zip != null && zip.compareTo(bestZip)<0)){
					bestZip = zip;
				}
			}
			return bestZip;
		}
		return null;
	}
	
	
	public String getBestZipString(Collection<String> zips){
		if(zips!=null){
			if (zips.size()==1){
				return zips.iterator().next();
			}
			String bestZip=null;
			String zip;
			for (String zipCode:zips){
				if (zipCode !=null){
					zip = zipCode;
				} else {
					continue;
				}
				if (bestZip==null ||(zip != null && zip.compareTo(bestZip)<0)){
					bestZip = zip;
				}
			}
			return bestZip;
		}
		return null;
	}
	
	/**
	 * @return the country from the country code. Return null if the country
	 *         Code is null or if no country is found
	 */
	public String getCountry(String countryCode) {
		if (countryCode != null) {
			AddressFormatInfo info = formater.getCountryInfo(countryCode);
			if (info != null) {
				return info.getCountryName();
			}
		}
		return null;
	}
	
	

	
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
			String bestZipString = getBestZipString(openStreetMap.getIsInZip());
			address.setZipCode(bestZipString);
		}
		if (openStreetMap.getLocation()!=null){
			address.setLng(openStreetMap.getLongitude());
			address.setLat(openStreetMap.getLatitude());
		}
		if (openStreetMap.getCountryCode()!=null){
			address.setCountryCode(openStreetMap.getCountryCode());
		}
		address.setGeocodingLevel(GeocodingLevels.STREET);//We set it and don't calculate it cause if streetname is null
		
		address.setFormatedFull(getFullyQualifiedName(address));
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
		
		address.setFormatedFull(getFullyQualifiedName(address));
		address.setFormatedPostal(formater.getEnvelopeAddress(address, DisplayMode.COMMA));
		return address;
	}

	

}
