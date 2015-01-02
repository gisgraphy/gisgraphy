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
package com.gisgraphy.domain.repository;

import java.util.List;

import com.gisgraphy.domain.geoloc.entity.ZipCode;

public interface IZipCodeDao extends IDao<ZipCode, java.lang.Long> {

	
	/**
	 * @param code the zipcode to search
	 * @param countryCode
         * The ISO 3166 Alpha 2 code in upper case
	 * @return the zipcode
	 */
	public List<ZipCode> getByCodeAndCountry(String code,String countryCode);
	
	/**
	 * 
	 * same as {@link #getByCodeAndCountry(String, String)} but do a starts with method if country is GB or CA
	 * @param code the zipcode to search
	 * @param countryCode
         * The ISO 3166 Alpha 2 code in upper case
	 * @return the zipcode
	 */
	public List<ZipCode> getByCodeAndCountrySmart(String code,String countryCode);
	
	/**
	 * @param code The zipcode to search
	 * @return a list of all zipcode where the given code matches or an empty list if no result
	 */
	public List<ZipCode> listByCode(String code);
	
	
   
	
    
    
}
