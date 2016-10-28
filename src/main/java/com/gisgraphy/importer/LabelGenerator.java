package com.gisgraphy.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gisgraphy.addressparser.format.AddressFormatInfo;
import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.valueobject.AlternateNameSource;

/**
 * Utility to generate labels for street, city, adm,
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class LabelGenerator {

	BasicAddressFormater formater;

	private static LabelGenerator instance = new LabelGenerator();

	private LabelGenerator() {
		formater = BasicAddressFormater.getInstance();
	}

	public static LabelGenerator getInstance() {
		return instance;
	}

	Set<String> generateLabels(OpenStreetMap street) {
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
						if (name.getName() != null) {
							labels.add(name.getName() + ", " + city);
						}
					}
				}
			} else {
				// no cities alt names =>label = street alternatenames
				for (AlternateOsmName name : street.getAlternateNames()) {
					if (name.getName() != null) {
						labels.add(name.getName());
					}
				}
				// finaly add the common name
				if (street.getName() != null) {
					labels.add(street.getName());
				}
			}
		}

		return labels;

	}

	List<String> generateLabels(City city) {
		List<String> labels = new ArrayList<String>();
		return labels;

	}

	List<String> generateLabels(Adm city) {
		List<String> labels = new ArrayList<String>();
		return labels;

	}

	List<String> generateLabels(GisFeature city) {
		List<String> labels = new ArrayList<String>();
		return labels;

	}

	/**
	 * Returns a name with adm1Name and adm2Name added (if not null).
	 * Paris(Zipcode), Département de Ville-De-Paris, Ile-De-France, (FR)
	 * 
	 * @param withCountry
	 *            Whether the country information should be added
	 * @return a name with the Administrative division and Country
	 */
	public String getFullyQualifiedName(City city, boolean withCountry) {
		StringBuilder completeCityName = new StringBuilder();
		completeCityName.append(city.getName());
		Set<ZipCode> zipCodes = city.getZipCodes();
		if (zipCodes != null && zipCodes.size() == 1) {
			completeCityName.append(" (");
			completeCityName.append(zipCodes.iterator().next());
			completeCityName.append(")");
		}
		if (city.getAdm2Name() != null && !city.getAdm2Name().trim().equals("")) {
			completeCityName.append(", " + city.getAdm2Name());
		}
		if (city.getAdm1Name() != null && !city.getAdm1Name().trim().equals("")) {
			completeCityName.append(", " + city.getAdm1Name());
		}

		if (withCountry) {
			String country = city.getCountry();
			if (country != null) {
				completeCityName.append(" , " + country + "");
			}
		}

		return completeCityName.toString();
	}

	/**
	 * Returns a name of the form : (adm1Name et adm2Name are printed) Paris,
	 * Département de Ville-De-Paris, Ile-De-France, (FR)
	 * 
	 * @param withCountry
	 *            Whether the country information should be added
	 * @return a name with the Administrative division and Country
	 */
	public String getFullyQualifiedName(GisFeature gisFeature,
			boolean withCountry) {
		StringBuilder completeCityName = new StringBuilder();
		completeCityName.append(gisFeature.getName());
		String adm2Name = gisFeature.getAdm2Name();
		if (adm2Name != null && !adm2Name.trim().equals("")) {
			completeCityName.append(", " + adm2Name);
		}
		String adm1Name = gisFeature.getAdm1Name();
		if (adm1Name != null && !adm1Name.trim().equals("")) {
			completeCityName.append(", " + adm1Name);
		}

		if (withCountry && gisFeature.getCountryCode() != null) {
			String country = getCountry(gisFeature.getCountryCode());
			if (country != null) {
				completeCityName.append(" , " + country);
			}
		}

		return completeCityName.toString();
	}

	/**
	 * @return a name with the Administrative division (but without Country)
	 */
	public String getFullyQualifiedName(GisFeature gisFeature) {
		return getFullyQualifiedName(gisFeature, false);
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
	
	public String generatePostal(OpenStreetMap street){
		return "";
	}
	
	public String generatePostal(GisFeature gisFeature){
		return "";
	}

}
