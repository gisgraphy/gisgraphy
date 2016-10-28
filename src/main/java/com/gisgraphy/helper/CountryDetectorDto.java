package com.gisgraphy.helper;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class CountryDetectorDto {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CountryDetectorDto [");
		if (address != null) {
			builder.append("address=");
			builder.append(address);
			builder.append(", ");
		}
		if (countryCode != null) {
			builder.append("countryCode=");
			builder.append(countryCode);
		}
		builder.append("]");
		return builder.toString();
	}
	private String address;
	private String countryCode;
	
	
	public CountryDetectorDto(String address, String countryCode) {
		super();
		this.address = address;
		this.countryCode = countryCode;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countrycode the countryCode to set
	 */
	public void setCountrycode(String countrycode) {
		this.countryCode = countrycode;
	}

}
