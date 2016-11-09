/*******************************************************************************
 *   Gisgraphy Project 
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 *  Copyright 2008  Gisgraphy project 
 *  David Masclet <davidmasclet@gisgraphy.com>
 *  
 *  
 *******************************************************************************/

package com.gisgraphy.importer.dto;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Represents a house number member in the Karlsruhe schema.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class AssociatedStreetMember {
	
	

	public AssociatedStreetMember() {
		super();
	}

	public AssociatedStreetMember(String id, Point location, String houseNumber, String streetName, String type,String role,String houseName,String city,String zipCode,String suburb,Geometry shape) {
		super();
		this.id = id;
		this.location = location;
		this.houseNumber = houseNumber;
		this.houseName = houseName;
		this.streetName = streetName;
		this.role = role;
		this.type = type;
		this.city=city;
		this.zipCode=zipCode;
		this.suburb = suburb;
		this.shape=shape;
	}
	private String city;
	private String zipCode;
	private String suburb;
	private Geometry shape;
	
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return the suburb
	 */
	public String getSuburb() {
		return suburb;
	}
	/**
	 * @param suburb the suburb to set
	 */
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}
	/**
	 * @return the shape
	 */
	public Geometry getShape() {
		return shape;
	}
	/**
	 * @param shape the shape to set
	 */
	public void setShape(Geometry shape) {
		this.shape = shape;
	}

	/**
	 * @return the houseName
	 */
	public String getHouseName() {
		return houseName;
	}

	/**
	 * @param houseName the houseName to set
	 */
	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}
	/**
	 * the id of the relation
	 */
	private String id;
	/**
	 * the gis location of the member (middle point or location )
	 */
	private Point location;
	 /**
	 * house or street
	 */
	private String role;
	/**
	 * the type of node (way or node), note that house can be way too!
	 */
	private String type;
	
	/**
	 * the name of the house
	 */
	private String houseName;
	
	/**
	 * the number of the house (if type=house). it is a string because of latin that is can have bis ter or a letter (3c)
	 */
	private String houseNumber;
	
	/**
	 * the streetName of the street
	 */
	private String streetName;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the location
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Point location) {
		this.location = location;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the houseNumber
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

	/**
	 * @return the streetName
	 */
	public String getStreetName() {
		return streetName;
	}

	/**
	 * @param name the name to set
	 */
	public void setStreetName(String name) {
		this.streetName = name;
	}

	@Override
	public String toString() {
		return String.format("AssociatedStreetMember [id=%s, location=%s, role=%s, type=%s, houseNumber=%s, streetName=%s]", id, location, role, type, houseNumber, streetName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result
				+ ((houseName == null) ? 0 : houseName.hashCode());
		result = prime * result
				+ ((houseNumber == null) ? 0 : houseNumber.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result
				+ ((streetName == null) ? 0 : streetName.hashCode());
		result = prime * result + ((suburb == null) ? 0 : suburb.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
		AssociatedStreetMember other = (AssociatedStreetMember) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (houseName == null) {
			if (other.houseName != null)
				return false;
		} else if (!houseName.equals(other.houseName))
			return false;
		if (houseNumber == null) {
			if (other.houseNumber != null)
				return false;
		} else if (!houseNumber.equals(other.houseNumber))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (streetName == null) {
			if (other.streetName != null)
				return false;
		} else if (!streetName.equals(other.streetName))
			return false;
		if (suburb == null) {
			if (other.suburb != null)
				return false;
		} else if (!suburb.equals(other.suburb))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		return true;
	}
	
	public boolean isStreet(){
		if ("street".equalsIgnoreCase(role)||
				("way".equalsIgnoreCase(role)) && !isNumeric(houseNumber)||
				(streetName!=null && streetName.equalsIgnoreCase(houseNumber))
				){
			return true;
		}
		return false;
	}
	
	public boolean isHouse(){
		if ("house".equalsIgnoreCase(role)||
				("node".equalsIgnoreCase(role)) && isNumeric(houseNumber)||
				(houseNumber!=null && !houseNumber.equalsIgnoreCase(streetName))
				){
			return true;
		}
		return false;
	}
	
	
	private static boolean isNumeric(String houseNumber) {
		try {
			Integer.parseInt(houseNumber);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
