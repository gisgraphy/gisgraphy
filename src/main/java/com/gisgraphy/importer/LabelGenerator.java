package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.format.AddressFormatInfo;
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.helper.countryInfo;

/**
 * Utility to generate labels for street, city, adm,
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class LabelGenerator {

	BasicAddressFormater formater;

	private static LabelGenerator instance = new LabelGenerator();

	protected LabelGenerator() {
		formater = BasicAddressFormater.getInstance();
	}

	public static LabelGenerator getInstance() {
		return instance;
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
		}

		if (altnames.size() > 0) {
			// some alt names
			if (altcities.size() > 0) {
				for (String city : altcities) {
					// cross all data
					for (AlternateOsmName name : altnames) {
						if (name.getName() != null && !name.getName().startsWith("http") && !city.startsWith("http")) {
							labels.add(name.getName() + ", " + city);
						}
					}
				}
			} else {
				// no cities alt names =>label = street alternatenames
				for (AlternateOsmName name : street.getAlternateNames()) {
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
				// no cities alt names =>label = street alternatenames
				for (AlternateName name : poi.getAlternateNames()) {
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
			completeCityName.append(", " + adm1Name);
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
				sb.append(address.getAdm1Name()).append(", ");
			}
		}
		if (address.getZipCode() != null){
			sb.append(address.getZipCode()).append(", ");
		} 
		if (address.getCountryCode()!=null){
			String countryName = countryInfo.countryLookupMap.get(address.getCountryCode().toUpperCase());
			if (countryName!=null){
				sb.append(countryName).append(", ");
			}
			sb.append(address.getCountryCode().toUpperCase());
		}
		String str =  sb.toString();
		//System.out.println(str);
		return str;
	}
	
	/*-------------------------------------------------Postal ----------------*/

	
	
	public String generatePostal(OpenStreetMap street){
		return "";
	}
	
	public String generatePostal(GisFeature gisFeature){
		return "";
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
	

}
