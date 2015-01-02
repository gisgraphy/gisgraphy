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
package com.gisgraphy.helper;

import javax.persistence.Transient;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.ICountryDao;

@Service
public class GisFeatureHelper implements ApplicationContextAware{
    
    /**
     * this method avoid the necessary injection in gisfeature
     */
    public static GisFeatureHelper getInstance(){
	if (applicationContext!=null){
	return (GisFeatureHelper) applicationContext.getBean("gisFeatureHelper");
	} else throw new RuntimeException("the application context is not injected");
    }
    
    @Autowired
    public ICountryDao countryDao;
    //it is not a good idea to have a static property here but i need the get instance to be static to avoid 
    //injection in gisFeature
    private static ApplicationContext applicationContext;
    
    /**
     * Returns a name of the form : (adm1Name et adm2Name are printed) Paris,
     * Département de Ville-De-Paris, Ile-De-France, (FR)
     * 
     * @param withCountry
     *                Whether the country information should be added
     * @return a name with the Administrative division and Country
     */
    @Transient
    public String getFullyQualifiedName(GisFeature gisFeature ,boolean withCountry) {
	StringBuilder completeCityName = new StringBuilder();
	completeCityName.append(gisFeature.getName());
	String adm2Name = gisFeature.getAdm2Name();
	if (adm2Name != null && !adm2Name.trim().equals("")) {
	    completeCityName.append(", " + adm2Name);
	}
	String adm1Name = gisFeature.getAdm1Name();
	if (adm1Name != null && !adm1Name.trim().equals("")) {
	    completeCityName.append(", " + adm1Name);
	}

	if (withCountry) {
	    Country countryObj = getCountry(gisFeature.getCountryCode());
	    if (countryObj != null && countryObj.getName() != null
		    && !countryObj.getName().trim().equals("")) {
		completeCityName.append(" , " + countryObj.getName() + "");
	    }
	}

	return completeCityName.toString();
    }
    
    /**
     * @return a name with the Administrative division (but without Country)
     */
    @Transient
    public String getFullyQualifiedName(GisFeature gisFeature) {
	return getFullyQualifiedName(gisFeature,false);
    }

    
    /**
     * @return the country from the country code. Return null if the country Code
     *         is null or if no country is found
     */
    @Transient
    public Country getCountry(String countryCode) {
	Country country = null;
	if (countryCode != null) {
	    country = countryDao.getByIso3166Alpha2Code(countryCode);
	}
	return country;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	GisFeatureHelper.applicationContext = applicationContext;
	
    }

}
