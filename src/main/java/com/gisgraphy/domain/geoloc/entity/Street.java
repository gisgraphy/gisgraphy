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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.gisgraphy.domain.valueobject.SpeedMode;
import com.gisgraphy.helper.IntrospectionIgnoredField;
import com.gisgraphy.street.StreetType;

/**
 * Represents a {@link Street}.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Street extends GisFeature {

    private boolean oneWay = false;
    private StreetType streetType;
    private Double length;
    //private Long openstreetmapId;
    //those fields are only used to sync the fulltext engine,
    //there are not used to be stored in Datastore
    
    @IntrospectionIgnoredField
    private SortedSet<HouseNumber> houseNumbers;
 
    private Integer lanes;
    
    private Boolean toll;
    
    private String surface;
    
    private String maxSpeed;
    
    private String maxSpeedBackward;
    
    private SpeedMode speedMode;
    
    
  
    
  
    
    
  
	/**
	 * @return the speedMode
	 */
	public SpeedMode getSpeedMode() {
		return speedMode;
	}

	/**
	 * @param speedMode the speedMode to set
	 */
	public void setSpeedMode(SpeedMode speedMode) {
		this.speedMode = speedMode;
	}

	private Integer azimuthStart;
    
    private Integer azimuthEnd;
    
   
    
    

    
    
    

	/**
	 * @return the number of lanes
	 */
	public Integer getLanes() {
		return lanes;
	}

	/**
	 * @param lanes the number of lanes to set
	 */
	public void setLanes(Integer lanes) {
		this.lanes = lanes;
	}

	/**
	 * @return whether the street is toll or free
	 */
	public Boolean isToll() {
		return toll;
	}

	/**
	 * @param toll whether the street is toll or free
	 */
	public void setToll(Boolean toll) {
		this.toll = toll;
	}

	/**
	 * @return the physical surface of the street
	 */
	public String getSurface() {
		return surface;
	}

	/**
	 * @param surface the surface to set
	 */
	public void setSurface(String surface) {
		this.surface = surface;
	}

	/**
	 * @return the maxSpeed of the street
	 */
	public String getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * @param maxSpeed the max speed to set
	 */
	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * @return the max Speed in the backward direction
	 */
	public String getMaxSpeedBackward() {
		return maxSpeedBackward;
	}

	/**
	 * @param maxSpeedBackward the max Speed in the Backward to set
	 */
	public void setMaxSpeedBackward(String maxSpeedBackward) {
		this.maxSpeedBackward = maxSpeedBackward;
	}

	/**
	 * @return the azimuth at the start of the street
	 */
	public Integer getAzimuthStart() {
		return azimuthStart;
	}

	/**
	 * @param azimuthStart the azimuth to set
	 */
	public void setAzimuthStart(Integer azimuthStart) {
		this.azimuthStart = azimuthStart;
	}

	/**
	 * @return the azimuth at the end of the street
	 */
	public Integer getAzimuthEnd() {
		return azimuthEnd;
	}

	/**
	 * @param azimuthEnd the azimuth to set
	 */
	public void setAzimuthEnd(Integer azimuthEnd) {
		this.azimuthEnd = azimuthEnd;
	}

    
    

	/*public Long getOpenstreetmapId() {
	return openstreetmapId;
    }

    public void setOpenstreetmapId(Long openstreetmapId) {
	this.openstreetmapId = openstreetmapId;
    }*/

    /**
     * Override the gisFeature value.<br>
     * Default to true;<br>
     * If this field is set to false, then the object won't be synchronized with
     * the fullText search engine
     */
    @Override
    @Transient
    public boolean isFullTextSearchable() {
	return true;
    }

    /**
     * Constructor that populate the {@link Street} with the gisFeature fields<br>
     * 
     * @param gisFeature
     *            The gisFeature from which we want to populate the
     *            {@link Street}
     */
    public Street(GisFeature gisFeature) {
	super(gisFeature);
    }

    /**
     * Needed by CGLib
     */
    public Street() {
	super();
    }

    public boolean isOneWay() {
	return oneWay;
    }

    public void setOneWay(boolean oneWay) {
	this.oneWay = oneWay;
    }

    public StreetType getStreetType() {
	return streetType;
    }

    public void setStreetType(StreetType streetType) {
	this.streetType = streetType;
    }

    public Double getLength() {
	return length;
    }

    public void setLength(Double length) {
	this.length = length;
    }


   	@Transient
    public SortedSet<HouseNumber> getHouseNumbers() {
		return houseNumbers;
	}

	public void setHouseNumbers(SortedSet<HouseNumber> houseNumbers) {
		this.houseNumbers = houseNumbers;
	}
	
	

	
	
}
