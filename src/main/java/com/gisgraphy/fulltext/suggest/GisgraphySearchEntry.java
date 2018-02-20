package com.gisgraphy.fulltext.suggest;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gisgraphy.helper.CountryInfo;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphySearchEntry {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/*@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GisgraphySearchEntry [featureId=");
		builder.append(featureId);
		builder.append(", ");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (isIn != null) {
			builder.append("isIn=");
			builder.append(isIn);
			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}*/

	private static List<String> NAME_HOUSE_COUNTRYCODE = new ArrayList<String>() {
		{
			add("DE");
			add("BE");
			add("HR");
			add("IS");
			add("LV");
			add("NL");
			add("NO");
			add("NZ");
			add("PL");
			add("RU");
			add("SI");
			add("SK");
			add("SW");
			add("TR");
		}
	};


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("GisgraphySearchEntry [featureId=");
		builder.append(featureId);
		builder.append(", lat=");
		builder.append(lat);
		builder.append(", lng=");
		builder.append(lng);
		builder.append(", ");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (countryCode != null) {
			builder.append("countryCode=");
			builder.append(countryCode);
			builder.append(", ");
		}
		if (isIn != null) {
			builder.append("isIn=");
			builder.append(isIn);
			builder.append(", ");
		}
		if (isInPlace != null) {
			builder.append("isInPlace=");
			builder.append(isInPlace);
			builder.append(", ");
		}
		if (isInZip != null) {
			builder.append("isInZip=");
			builder.append(isInZip.subList(0, Math.min(isInZip.size(), maxLen)));
			builder.append(", ");
		}
		if (adm1Name != null) {
			builder.append("adm1Name=");
			builder.append(adm1Name);
			builder.append(", ");
		}
		if (zipCodes != null) {
			builder.append("zipCodes=");
			builder.append(zipCodes.subList(0,
					Math.min(zipCodes.size(), maxLen)));
			builder.append(", ");
		}
		if (houseNumbers != null) {
			builder.append("houseNumbers=");
			builder.append(houseNumbers.subList(0,
					Math.min(houseNumbers.size(), maxLen)));
			builder.append(", ");
		}
		if (houseNumber != null) {
			builder.append("houseNumber=");
			builder.append(houseNumber);
		}
		builder.append("]");
		return builder.toString();
	}

	@JsonProperty("feature_id")
	private long featureId;
	private double lat;
	private double lng;
	private String name;

	@JsonProperty("country_code")
	private String countryCode;

	@JsonProperty("is_in")
	private String isIn;

	@JsonProperty("is_in_place")
	private String isInPlace;

	@JsonProperty("is_in_zip")
	private List<String> isInZip;

	@JsonProperty("adm1_name")
	private String adm1Name;

	@JsonProperty("zip_code")
	private List<String> zipCodes;

	@JsonProperty("house_numbers")
	private List<String> houseNumbers = new ArrayList<String>();

	public List<String> getHouseNumbers() {
		return houseNumbers;
	}

	public void setHouseNumbers(List<String> houseNumbers) {
		this.houseNumbers = houseNumbers;
	}

	@JsonProperty("house_number")
	private String houseNumber;


	public long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(long featureId) {
		this.featureId = featureId;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getIsIn() {
		return isIn;
	}

	public void setIsIn(String isIn) {
		this.isIn = isIn;
	}
	@JsonProperty("is_in_place")
	public String getIsInPlace() {
		return isInPlace;
	}

	@JsonProperty("is_in_place")
	public void setIsInPlace(String isInPlace) {
		this.isInPlace = isInPlace;
	}

	@JsonProperty("is_in_zip")
	public List<String> getIsInZip() {
		return isInZip;
	}

	@JsonProperty("is_in_zip")
	public void setIsInZip(List<String> isInZip) {
		this.isInZip = isInZip;
	}

	public String getAdm1Name() {
		return adm1Name;
	}

	public void setAdm1Name(String adm1Name) {
		this.adm1Name = adm1Name;
	}

	public List<String> getZipCodes() {
		return zipCodes;
	}

	public void setZipCodes(List<String> zipCodes) {
		this.zipCodes = zipCodes;
	}


	public String getCountry() {
		if (countryCode!=null){
			return CountryInfo.countryLookupMap.get(countryCode.toUpperCase());
		}
		return null;
	}


	@JsonProperty("house_number")
	public String getHouseNumber() {
		return houseNumber;
	}

	@JsonProperty("house_number")
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getZipCode() {
		if (isInZip!=null && isInZip.size()>0){
			return isInZip.get(0);
		}
		if (zipCodes!=null && zipCodes.size()>0){
			return zipCodes.get(0);
		}
		return null;
	}

	@JsonProperty("label")
	public String getLabel() {
		StringBuilder addressFormated = new StringBuilder();
		if (countryCode != null && NAME_HOUSE_COUNTRYCODE.contains(countryCode.toUpperCase())) {
			if (name !=null){
				addressFormated.append(name);
			}
			if (houseNumber!=null){
				addressFormated.append(" ").append(houseNumber);
			}

		} else {
			if (houseNumber!=null) {
				addressFormated.append(houseNumber).append(", ");
			}
			if (name !=null){
				addressFormated.append(name);
			}

		}
		if (isIn !=null || isInPlace!=null) {
			if (isInPlace!=null) {
				addressFormated.append(", ").append(isInPlace);
			}
			if (isInZip!=null && isInZip.size()>0) {
				addressFormated.append(", ").append(isInZip.get(0));
			} else if (zipCodes !=null && zipCodes.size() > 0){
				addressFormated.append(", ").append(zipCodes.get(0));
			}
			if (isIn!=null) {
				addressFormated.append(", ").append(isIn);
			}
		}
		return addressFormated.toString();

	}

}
