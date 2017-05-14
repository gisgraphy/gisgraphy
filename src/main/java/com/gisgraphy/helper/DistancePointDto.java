package com.gisgraphy.helper;

import com.vividsolutions.jts.geom.Point;

public class DistancePointDto {
	
	Point point;
	Double distance;
	/**
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}
	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}
	public DistancePointDto(Point point, Double distance) {
		super();
		this.point = point;
		this.distance = distance;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DistancePointDto [");
		if (point != null) {
			builder.append("point=");
			builder.append(point);
			builder.append(", ");
		}
		if (distance != null) {
			builder.append("distance=");
			builder.append(distance);
		}
		builder.append("]");
		return builder.toString();
	}
	
	

}
