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
package com.gisgraphy.domain.geoloc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.gisgraphy.domain.valueobject.HouseNumberType;
import com.gisgraphy.domain.valueobject.SRID;
import com.gisgraphy.helper.IntrospectionIgnoredField;
import com.gisgraphy.street.HouseNumberComparator;
import com.vividsolutions.jts.geom.Point;

/**
 * Represents a housenumber (typically link to an {@link OpenStreetMap}.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "housenumbersequence", sequenceName = "housenumber_sequence")
public class HouseNumber implements Comparable<HouseNumber>{
	

    public static final String LOCATION_COLUMN_NAME = "location";

    /**
     * Needed by CGLib
     */
    public HouseNumber() {
    }

    public HouseNumber(String number, Point location) {
    	if (number==null || "".equals(number.trim())){
    		throw new IllegalArgumentException("wrong number given for housenumber");
    	}
    	if (location==null){
    		throw new IllegalArgumentException("wrong location for housenumber");
    	}
    	this.number = number;
		this.location = location;
	}

	@IntrospectionIgnoredField
    private Long id;

    private Long openstreetmapId;
    
    private String number;
    
    @IntrospectionIgnoredField
    private HouseNumberType type;
    
    @IntrospectionIgnoredField
    private static final HouseNumberComparator comparator = new HouseNumberComparator();

    private String name;

    private Point location;
    
    private OpenStreetMap street;

    
    /**
     * @return the id (technical one)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "housenumbersequence")
    public Long getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
	this.id = id;
    }

       
    /**
     * @return the openstreetmap internal id
     */
    @Column(unique = false, nullable = true)
    public Long getOpenstreetmapId() {
        return openstreetmapId;
    }

    /**
     * @param openstreetmapId the openstreetmap internal id
     */
    public void setOpenstreetmapId(Long openstreetmapId) {
        this.openstreetmapId = openstreetmapId;
    }

    /**
     * @return the name of the house. 
     * It can be the name of the shop if present or of the house.
     * associated to the house number, it gives more informations.
     *  
     */
    @Column(length = 255)
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }


    /**
     * Returns The JTS location point of the current house : The Geometry
     * representation for the latitude, longitude. The Return type is a JTS
     * point. The Location is calculate from the 4326 {@link SRID}
     * 
     * @see SRID
     * @return The JTS Point
     */
    @Type(type = "org.hibernatespatial.GeometryUserType")
    @Column(name = HouseNumber.LOCATION_COLUMN_NAME,nullable=false)
    public Point getLocation() {
	return location;
    }
    
    /**
     * @return Returns the latitude (north-south) from the Location
     *         {@link #getLocation()}.
     * @see #getLongitude()
     * @see #getLocation()
     */
    @Transient
    public Double getLatitude() {
	Double latitude = null;
	if (this.location != null) {
	    latitude = this.location.getY();
	}
	return latitude;
    }
    
    /**
     * @return Returns the longitude (east-west) from the Location
     *         {@link #getLocation()}.
     * @see #getLongitude()
     * @see #getLocation()
     */
    @Transient
    public Double getLongitude() {
	Double longitude = null;
	if (this.location != null) {
	    longitude = this.location.getX();
	}
	return longitude;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(Point location) {
	this.location = location;
    }

	/**
	 * @return the number of the house.
	 * it can be null if the house has only a name. but it is typically filled
	 */
    @Column(nullable=true)
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the type of node. 
	 * @see HouseNumberType for details
	 */
	@Enumerated(EnumType.STRING)
   // @Column(nullable = false)
	public HouseNumberType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(HouseNumberType type) {
		this.type = type;
	}

	/**
	 * @return the street associated to this house number
	 */
	 @ManyToOne(fetch = FetchType.LAZY)//TODO HN
	 @JoinColumn(nullable = false, name = "street")
	 @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	 @Index(name = "housenumberstreetindex")
	public OpenStreetMap getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(OpenStreetMap street) {
		this.street = street;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		HouseNumber other = (HouseNumber) obj;
		if (openstreetmapId == null) {
			if (other.openstreetmapId != null){
				return false;
			} else {
				return other.is_same(this);
			}
		} else if (!openstreetmapId.equals(other.openstreetmapId)){
			return other.is_same(this);
		}
		return true;
	}
*/
	
	 @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HouseNumber other = (HouseNumber) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	 
	@Override
	public String toString() {
		return "HouseNumber [id=" + id + ", openstreetmapId=" + openstreetmapId
				+ ", number=" + number + ", type=" + type + ", name=" + name
				+ ", location=" + location + ", street=" + street
				+ ", getId()=" + getId() + ", getOpenstreetmapId()="
				+ getOpenstreetmapId() + ", getName()=" + getName()
				+ ", getLocation()=" + getLocation() + ", getLatitude()="
				+ getLatitude() + ", getLongitude()=" + getLongitude()
				+ ", getNumber()=" + getNumber() + ", getType()=" + getType()
				+ ", getStreet()=" + getStreet() + ", hashCode()=" + hashCode()
				+ ", getClass()=" + getClass() + ", toString()="
				+ super.toString() + "]";
	}

	/**
	 * check if address are content identical
	 * If number are equals and street (if number is null, we look at the name)
	 */
	public boolean is_same(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HouseNumber other = (HouseNumber) obj;
		if (number == null) {
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		return true;
	}

	public int compareTo(HouseNumber o) {
		
		return comparator.compare(this, o);
	}

   



}
