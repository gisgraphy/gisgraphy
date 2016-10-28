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

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.gisgraphy.helper.IntrospectionIgnoredField;

/**
 * Represents a city Object
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class City extends GisFeature implements ZipCodesAware {

	public final static String MUNICIPALITY_FIELD_NAME = "municipality";
	
	/**
	 * This fields indicates that the city has some properties(admCode, population that make the city a municipality. 
	 * This is necessary because some place in geonames are marked as popular places and can be quater or common place.
	 * by identifing city we can restrict the search to city.
	 */
	@IntrospectionIgnoredField
	private boolean municipality=false;
	
	/**
	 * Constructor that populate the {@link City} with the gisFeature fields<br>
	 * 
	 * @param gisFeature
	 *                The gisFeature from which we want to populate the
	 *                {@linkplain City}
	 */
	public City(GisFeature gisFeature) {
		super(gisFeature);
	}

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
	 * Default constructor (Needed by CGLib)
	 */
	public City() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gisgraphy.domain.geoloc.entity.GisFeature#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((getFeatureId() == null) ? 0 : getFeatureId().hashCode());
		return result;
	}

	
	

	@Index(name = "cityMunicipalityIndex")
	public boolean isMunicipality() {
		return municipality;
	}

	public void setMunicipality(boolean isMunicipality) {
		this.municipality = isMunicipality;
	}

	

}
