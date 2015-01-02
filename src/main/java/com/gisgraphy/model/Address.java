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
package com.gisgraphy.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is used to represent an address with address, city, province and
 * postal-code information.
 * 
 * !!!!THIS CLASS IS AN OLD CLASS TO STORE USER INFORMATION, DO NOT USE FOR GEOCODING OR GIS!!!!
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Embeddable
public class Address extends BaseObject implements Serializable {
    private static final long serialVersionUID = 3617859655330969141L;

    private String address;

    private String city;

    private String province;

    private String country;

    private String postalCode;

    @Column(length = 150)
    public String getAddress() {
	return address;
    }

    @Column(nullable = false, length = 50)
    public String getCity() {
	return city;
    }

    @Column(length = 100)
    public String getProvince() {
	return province;
    }

    @Column(length = 100)
    public String getCountry() {
	return country;
    }

    @Column(name = "postal_code", nullable = false, length = 15)
    public String getPostalCode() {
	return postalCode;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public void setProvince(String province) {
	this.province = province;
    }

    /**
     * Overridden equals method for object comparison. Compares based on
     * hashCode.
     * 
     * @param o
     *                Object to compare
     * @return true/false based on hashCode
     */
    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (!(o instanceof Address)) {
	    return false;
	}

	final Address address1 = (Address) o;

	return this.hashCode() == address1.hashCode();
    }

    /**
     * Overridden hashCode method - compares on address, city, province, country
     * and postal code.
     * 
     * @return hashCode
     */
    @Override
    public int hashCode() {
	int result;
	result = (address != null ? address.hashCode() : 0);
	result = 29 * result + (city != null ? city.hashCode() : 0);
	result = 29 * result + (province != null ? province.hashCode() : 0);
	result = 29 * result + (country != null ? country.hashCode() : 0);
	result = 29 * result + (postalCode != null ? postalCode.hashCode() : 0);
	return result;
    }

    /**
     * Returns a multi-line String with key=value pairs.
     * 
     * @return a String representation of this class.
     */
    @Override
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
		.append("country", this.country)
		.append("address", this.address).append("province",
			this.province).append("postalCode", this.postalCode)
		.append("city", this.city).toString();
    }
}
