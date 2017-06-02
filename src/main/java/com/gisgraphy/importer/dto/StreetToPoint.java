package com.gisgraphy.importer.dto;

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.helper.DistancePointDto;

public class StreetToPoint {
	
	private OpenStreetMap street;
	private DistancePointDto distancePointDto;
	
	public StreetToPoint(OpenStreetMap street, DistancePointDto distancePointDto) {
		super();
		this.street = street;
		this.distancePointDto = distancePointDto;
	}
	

	/**
	 * @return the street
	 */
	public OpenStreetMap getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(OpenStreetMap street) {
		this.street = street;
	}

	/**
	 * @return the distancePointDto
	 */
	public DistancePointDto getDistancePointDto() {
		return distancePointDto;
	}

	/**
	 * @param distancePointDto the distancePointDto to set
	 */
	public void setDistancePointDto(DistancePointDto distancePointDto) {
		this.distancePointDto = distancePointDto;
	}

	


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StreetToPoint [");
		if (street != null) {
			builder.append("street=");
			builder.append(street);
			builder.append(", ");
		}
		if (distancePointDto != null) {
			builder.append("distancePointDto=");
			builder.append(distancePointDto);
		}
		builder.append("]");
		return builder.toString();
	}
	
	

}
