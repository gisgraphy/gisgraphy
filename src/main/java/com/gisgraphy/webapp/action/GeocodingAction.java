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
package com.gisgraphy.webapp.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.repository.ICountryDao;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.Constants;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Ggeocoding Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GeocodingAction extends ActionSupport implements
	GoogleMapApiKeyAware {

    //private static Logger logger = LoggerFactory.getLogger(GeocodingAction.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The cities in case that more than one result match 
     */
    private List<SolrResponseDto> ambiguousCities;

    /**
     * the name of the city chosen in the ambiguous cities list 
     */
    private String ambiguousCity;

    /**
     * th search field value
     */
    private String city;

    private String lng;

    private String lat;

    /**
     * whether the city has been found (no more ambiguous)
     */
    private boolean cityFound = false;

    private IFullTextSearchEngine fullTextSearchEngine;

    Map<Long, String> featureIdLatLongMap = new HashMap<Long, String>();

    private String message = "";

    private String errorMessage = "";

    private ICountryDao countryDao;

    private String countryCode;

    @SuppressWarnings("unchecked")
    @Override
    public String execute() throws Exception {
	try {

	    if (city != null) {
		if (countryCode == null || "".equals(countryCode)) {
		    errorMessage = getText("search.country.required");
		    return Action.SUCCESS;
		}
		FulltextQuery fulltextQuery = new FulltextQuery(city,
			Pagination.DEFAULT_PAGINATION, Output.DEFAULT_OUTPUT,
			Constants.ONLY_CITY_PLACETYPE, getCountryCode());
		ambiguousCities = fullTextSearchEngine.executeQuery(
			fulltextQuery).getResults();
		int numberOfPossibleCitiesThatMatches = ambiguousCities.size();
		if (numberOfPossibleCitiesThatMatches == 0) {
		    return Action.SUCCESS;
		} else if (numberOfPossibleCitiesThatMatches == 1) {
		    SolrResponseDto cityfound = ambiguousCities.get(0);
		    lat = cityfound.getLat().toString();
		    lng = cityfound.getLng().toString();
		    city = buildCityDisplayName(cityfound); 
		    cityFound = true;
		    return Action.SUCCESS;
		} else {
		    //more than one city suits
		    return Action.SUCCESS;
		}

	    } else if (ambiguousCity != null) {
		return Action.SUCCESS;
	    }

	} catch (Exception e) {
	    errorMessage = getText("search.error", e.getMessage());
	}

	return Action.SUCCESS;
    }

	protected String buildCityDisplayName(SolrResponseDto cityfound) {
		String diplayName = cityfound.getName();
		Set<String> zipcodes = cityfound.getZipcodes();
		if (zipcodes != null && zipcodes.size()==1) {
			diplayName = cityfound.getName() + " (" + zipcodes.iterator().next() + ")";
		}
		return diplayName;
	}

    public String getLatLongJson() {
	if (ambiguousCities == null) {
	    return "";
	}
	StringBuffer sb = new StringBuffer("[");
	int index = 1;
	for (SolrResponseDto city : ambiguousCities) {
	    sb.append("{\"lat\":");
	    sb.append(city.getLat());
	    sb.append(",");
	    sb.append("\"lng\":");
	    sb.append(city.getLng());
	    sb.append("}");
	    if (index != ambiguousCities.size()) {
		sb.append(",");
	    }
	    index = index + 1;

	}
	sb.append("]");
	return sb.toString();
    }

    /**
     * @return the ambiguousCities
     */
    public List<SolrResponseDto> getAmbiguousCities() {
	return ambiguousCities;
    }

    public void setAmbiguousCities(List<SolrResponseDto> ambiguousCities) {
	this.ambiguousCities = ambiguousCities;
    }

    /**
     * @return the ambiguousCity
     */
    public String getAmbiguousCity() {
	return ambiguousCity;
    }

    /**
     * @return the available countries
     */
    public List<Country> getCountries() {
	return countryDao.getAllSortedByName();
    }

    /**
     * @param ambiguousCity the ambiguousCity to set
     */
    public void setAmbiguousCity(String ambiguousCity) {
	this.ambiguousCity = ambiguousCity;
    }

    /**
     * @param countryDao
     *                the countryDao to set
     */
    @Required
    public void setCountryDao(ICountryDao countryDao) {
	this.countryDao = countryDao;
    }

    /**
     * @param fullTextSearchEngine the fullTextSearchEngine to set
     */
    public void setFullTextSearchEngine(
	    IFullTextSearchEngine fullTextSearchEngine) {
	this.fullTextSearchEngine = fullTextSearchEngine;
    }

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
     * @return the countryCode
     */
    public String getCountryCode() {
	return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
	this.countryCode = countryCode;
    }

    /**
     * @return the message
     */
    public String getMessage() {
	return message;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @return the lng
     */
    public String getLng() {
	return lng;
    }

    /**
     * @param lng the lng to set
     */
    public void setLng(String lng) {
	this.lng = lng;
    }

    /**
     * @return the lat
     */
    public String getLat() {
	return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(String lat) {
	this.lat = lat;
    }

    /**
     * @return the googleMapAPIKey
     */
    public String getGoogleMapAPIKey() {
	return GisgraphyConfig.googleMapAPIKey == null ? ""
		: GisgraphyConfig.googleMapAPIKey;
    }

    /**
     * @return the cityFound
     */
    public boolean isCityFound() {
	return cityFound;
    }

}
