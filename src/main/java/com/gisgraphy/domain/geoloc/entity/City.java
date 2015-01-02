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

	
	/**
	 * Returns a name with adm1Name and adm2Name added (if not null).
	 * Paris(Zipcode), Département de Ville-De-Paris, Ile-De-France, (FR)
	 * 
	 * @param withCountry
	 *                Whether the country information should be added
	 * @return a name with the Administrative division and Country
	 */
	@Transient
	@Override
	public String getFullyQualifiedName(boolean withCountry) {
		StringBuilder completeCityName = new StringBuilder();
		completeCityName.append(getName());
		Set<ZipCode> zipCodes = getZipCodes();
		if (zipCodes != null && zipCodes.size() == 1) {
			completeCityName.append(" (");
			completeCityName.append(zipCodes.iterator().next());
			completeCityName.append(")");
		}
		if (getAdm2Name() != null && !getAdm2Name().trim().equals("")) {
			completeCityName.append(", " + getAdm2Name());
		}
		if (getAdm1Name() != null && !getAdm1Name().trim().equals("")) {
			completeCityName.append(", " + getAdm1Name());
		}

		if (withCountry) {
			Country countryObj = getCountry();
			if (countryObj != null && countryObj.getName() != null && !countryObj.getName().trim().equals("")) {
				completeCityName.append(" , " + countryObj.getName() + "");
			}
		}

		return completeCityName.toString();
	}

	@Index(name = "cityMunicipalityIndex")
	public boolean isMunicipality() {
		return municipality;
	}

	public void setMunicipality(boolean isMunicipality) {
		this.municipality = isMunicipality;
	}

	

}
