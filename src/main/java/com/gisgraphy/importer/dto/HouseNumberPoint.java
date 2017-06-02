package com.gisgraphy.importer.dto;

import com.vividsolutions.jts.geom.Point;

public class HouseNumberPoint {
	
	Point point;
	String number;
	
	
	public HouseNumberPoint(Point point, String number) {
		super();
		this.point = point;
		this.number = number;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HouseNumberPoint [");
		if (point != null) {
			builder.append("point=");
			builder.append(point);
			builder.append(", ");
		}
		if (number != null) {
			builder.append("number=");
			builder.append(number);
		}
		builder.append("]");
		return builder.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((point == null) ? 0 : point.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HouseNumberPoint other = (HouseNumberPoint) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (point == null) {
			if (other.point != null)
				return false;
		} else if (!point.equals(other.point))
			return false;
		return true;
	}


	/**
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}


	/**
	 * @param point the point to set
	 */
	public void setPoint(Point point) {
		this.point = point;
	}


	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}


	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	
	

}
