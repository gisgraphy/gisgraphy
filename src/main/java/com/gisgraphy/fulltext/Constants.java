/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.fulltext;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.Street;

public class Constants {
	
		/**
		 * convenence placetype for only city
		 */
		public final static Class[] ONLY_CITY_PLACETYPE = new Class[]{City.class};
		/**
		 * convenence placetype for only adm
		 */
		public final static Class[] ONLY_ADM_PLACETYPE = new Class[]{Adm.class};
		
		/**
		 * convenence placetype for city and citySubdivision
		 */
		public final static Class[] CITY_AND_CITYSUBDIVISION_PLACETYPE = new Class[] {City.class,CitySubdivision.class};
		
		
		/**
		 * convenence placetype for city and citySubdivision
		 */
		public final static Class[] ADDRESSES_PLACETYPE = new Class[] {City.class,CitySubdivision.class,Street.class,Adm.class}; 
		
		/**
		 * convenence placetype for city and citySubdivision
		 */
		public final static Class[] ONLY_CITYSUBDIVISION_PLACETYPE = new Class[] {CitySubdivision.class};
		
		/**
		 * convenence placetype for city and citySubdivision
		 */
		public final static Class[] STREET_PLACETYPE = new Class[] {Street.class};

}
