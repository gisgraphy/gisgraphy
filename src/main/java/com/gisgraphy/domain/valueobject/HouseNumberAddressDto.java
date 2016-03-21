package com.gisgraphy.domain.valueobject;

/**
 * 
 * DTO to handle a housenumber and the new address without it
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class HouseNumberAddressDto {
	
	private String addressWithoutHouseNumber;
	
	private String addresWithHouseNumber;
	
	private String houseNumber;
	
	

	public HouseNumberAddressDto(String addressWithoutHouseNumber,
			String addresWithHouseNumber, String houseNumber) {
		super();
		this.addressWithoutHouseNumber = addressWithoutHouseNumber;
		this.addresWithHouseNumber = addresWithHouseNumber;
		this.houseNumber = houseNumber;
	}

	/**
	 * @return the addressWithoutHouseNumber
	 */
	public String getAddressWithoutHouseNumber() {
		return addressWithoutHouseNumber;
	}

	/**
	 * @param addressWithoutHouseNumber the addressWithoutHouseNumber to set
	 */
	public void setAddressWithoutHouseNumber(String addressWithoutHouseNumber) {
		this.addressWithoutHouseNumber = addressWithoutHouseNumber;
	}

	/**
	 * @return the addresWithHouseNumber
	 */
	public String getAddresWithHouseNumber() {
		return addresWithHouseNumber;
	}

	/**
	 * @param addresWithHouseNumber the addresWithHouseNumber to set
	 */
	public void setAddresWithHouseNumber(String addresWithHouseNumber) {
		this.addresWithHouseNumber = addresWithHouseNumber;
	}

	/**
	 * @return the houseNumber, it is a string because it has to handle  things like 32a...
	 */
	public String getHouseNumber() {
		return houseNumber;
	}

	/**
	 * @param houseNumber the houseNumber to set
	 */
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

}
