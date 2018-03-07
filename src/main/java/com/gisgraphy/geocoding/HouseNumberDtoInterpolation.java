package com.gisgraphy.geocoding;

import com.vividsolutions.jts.geom.Point;

public class HouseNumberDtoInterpolation {
	

	
	private Point exactLocation;
	private Integer exactNumber;
	private Point lowerLocation;
	private Integer lowerNumber;
	private Point higherLocation;
	private Integer HigherNumber;
	private Integer HouseNumberDif = 0;
	
	private boolean approximative = false;
	
	
	

	/**
	 * @return the houseNumberDif
	 */
	public Integer getHouseNumberDif() {
		return HouseNumberDif;
	}




	/**
	 * @param houseNumberDif the houseNumberDif to set
	 */
	public void setHouseNumberDif(Integer houseNumberDif) {
		HouseNumberDif = houseNumberDif;
	}




	public HouseNumberDtoInterpolation() {
		super();
	}




	public HouseNumberDtoInterpolation(Point exactLocation, Integer exactNumber) {
		super();
		this.exactLocation = exactLocation;
		this.exactNumber = exactNumber;
	}



	public String getExactNumerAsString(){
		if (exactNumber!=null){
			return exactNumber.toString();
		}else {
			return null;
		}
	}

	/**
	 * @return the exactLocation
	 */
	public Point getExactLocation() {
		return exactLocation;
	}




	/**
	 * @param exactLocation the exactLocation to set
	 */
	public void setExactLocation(Point exactLocation) {
		this.exactLocation = exactLocation;
	}




	/**
	 * @return the exactNumber
	 */
	public Integer getExactNumber() {
		return exactNumber;
	}




	/**
	 * @param exactNumber the exactNumber to set
	 */
	public void setExactNumber(Integer exactNumber) {
		this.exactNumber = exactNumber;
	}




	/**
	 * @return the lowerLocation
	 */
	public Point getLowerLocation() {
		return lowerLocation;
	}




	/**
	 * @param lowerLocation the lowerLocation to set
	 */
	public void setLowerLocation(Point lowerLocation) {
		this.lowerLocation = lowerLocation;
	}




	/**
	 * @return the lowerNumber
	 */
	public Integer getLowerNumber() {
		return lowerNumber;
	}




	/**
	 * @param lowerNumber the lowerNumber to set
	 */
	public void setLowerNumber(Integer lowerNumber) {
		this.lowerNumber = lowerNumber;
	}




	/**
	 * @return the higherLocation
	 */
	public Point getHigherLocation() {
		return higherLocation;
	}




	/**
	 * @param higherLocation the higherLocation to set
	 */
	public void setHigherLocation(Point higherLocation) {
		this.higherLocation = higherLocation;
	}




	/**
	 * @return the higherNumber
	 */
	public Integer getHigherNumber() {
		return HigherNumber;
	}




	/**
	 * @param higherNumber the higherNumber to set
	 */
	public void setHigherNumber(Integer higherNumber) {
		HigherNumber = higherNumber;
	}




	/**
	 * @return true if house has been interpolated
	 */
	public boolean isApproximative(){
		return approximative;
	}




	/**
	 * @param approximative the approximative to set
	 */
	public void setApproximative(boolean approximative) {
		this.approximative = approximative;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HouseNumberDtoInterpolation [");
		if (exactLocation != null) {
			builder.append("exactLocation=");
			builder.append(exactLocation);
			builder.append(", ");
		}
		if (exactNumber != null) {
			builder.append("exactNumber=");
			builder.append(exactNumber);
			builder.append(", ");
		}
		if (lowerLocation != null) {
			builder.append("lowerLocation=");
			builder.append(lowerLocation);
			builder.append(", ");
		}
		if (lowerNumber != null) {
			builder.append("lowerNumber=");
			builder.append(lowerNumber);
			builder.append(", ");
		}
		if (higherLocation != null) {
			builder.append("higherLocation=");
			builder.append(higherLocation);
			builder.append(", ");
		}
		if (HigherNumber != null) {
			builder.append("HigherNumber=");
			builder.append(HigherNumber);
			builder.append(", ");
		}
		if (HouseNumberDif != null) {
			builder.append("HouseNumberDif=");
			builder.append(HouseNumberDif);
			builder.append(", ");
		}
		builder.append("approximative=");
		builder.append(approximative);
		builder.append("]");
		return builder.toString();
	}
	


	
	

}
