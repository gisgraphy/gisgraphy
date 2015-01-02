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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

/**
 * Represents a {@link ZipCode}.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "zipcode", sequenceName = "zipcode_sequence")
public class ZipCode {

    private Long id;

    private String code;

    private GisFeature gisFeature;

    /**
     * /** Default Constructor
     */
    public ZipCode() {
    }

    /**
     * @param code
     *            the code of the zipcode (will be uppercased)
     */
    public ZipCode(String code) {
	if (code != null) {
	    this.code = code.toUpperCase();
	} else {
	    throw new IllegalArgumentException("Can not create a zip code with null code");
	}
    }

    /**
     * The datastore id
     * 
     * @return The datastore id, it is not a domain value, just a technical One
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zipcode")
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
     * @return the code
     */
    @Column(nullable = false, length = 80)
    @Index(name = "zipcode_code")
    public String getCode() {
	return code;
    }

    /**
     * @param code
     *            the code to set, (will be uppercased)
     */
    public void setCode(String code) {
	if (code!=null){
	    this.code = code.toUpperCase();
	} else {
	    this.code =null;
	}
    }

    /**
     * @param gisFeature
     *            the gisfeature of the zipcode
     */
    public void setGisFeature(GisFeature gisFeature) {
	this.gisFeature = gisFeature;
    }

    /**
     * @return the gisFeature associated to this zip code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "gisFeature")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Index(name = "zipcodefeatureidindex")
    public GisFeature getGisFeature() {
	return this.gisFeature;
    }

    @Override
    public String toString() {
	return this.code;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
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
	final ZipCode other = (ZipCode) obj;
	if (code == null) {
	    if (other.code != null)
		return false;
	} else if (!code.equals(other.code))
	    return false;
	return true;
    }

}
