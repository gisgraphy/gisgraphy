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

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.repository.CountryDao;
import com.gisgraphy.domain.repository.IAlternateNameDao;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextQueryHttpBuilder;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.fulltext.spell.SpellCheckerConfig;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.helper.URLUtils;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * fulltext search Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class FulltextSearchAction extends SearchAction {

    private static final long serialVersionUID = -9018894533914543310L;

    private static Logger logger = LoggerFactory
	    .getLogger(FulltextSearchAction.class);

    private IFullTextSearchEngine fullTextSearchEngine;

    private IAlternateNameDao alternateNameDao;

    private CountryDao countryDao;

    private FulltextResultsDto responseDTO;

    private String placetype;

    private boolean allwordsrequired = FulltextQuery.ALL_WORDS_REQUIRED_DEFAULT_OPTION;
    
    /**
     * force admin mode to false
     */
    private Boolean admin=false;

    // form parameters

    private String country;
    private String lang;

    private String style;
    private String q;
    private boolean spellchecking = SpellCheckerConfig.activeByDefault;
    
    private static List<String> cachedUsedLanguageCode = new ArrayList<String>();
    
    private static String syncToken = "";

   

    /**
     * @return Wether the search has been done and the results should be
     *         displayed
     */
    public boolean isDisplayResults() {
	return this.responseDTO != null;
    }

    private void executeQuery() {
	try {
	    FulltextQuery fulltextQuery = FulltextQueryHttpBuilder.getInstance().buildFromRequest(
	    		ServletActionContext.getRequest());
	    setFrom(fulltextQuery.getFirstPaginationIndex());
	    setTo(fulltextQuery.getLastPaginationIndex());
	    this.responseDTO = fullTextSearchEngine.executeQuery(fulltextQuery);
	    //manually add the urls
	    for (SolrResponseDto dto :responseDTO.getResults()){
	    	dto.setYahoo_map_url(URLUtils.createYahooMapUrl(GeolocHelper.createPoint(dto.getLng(), dto.getLat())));
	    	dto.setGoogle_map_url(URLUtils.createGoogleMapUrl(GeolocHelper.createPoint(dto.getLng(), dto.getLat())));
	    	dto.setOpenstreetmap_map_url(URLUtils.createOpenstreetmapMapUrl((GeolocHelper.createPoint(dto.getLng(), dto.getLat()))));
	    }
	} catch (RuntimeException e) {
	    String exceptionMessage = "";
	    if (e.getCause() != null && e.getCause().getCause() != null) {
		exceptionMessage = e.getCause().getCause().toString();
		logger.error("An error occured during search : "
			+ exceptionMessage,e);
	    } else {
		exceptionMessage = e.getMessage();
		logger.error("An error occured during search : "
			+ e.getMessage(),e);
	    }
	    this.errorMessage = exceptionMessage == null? getText("errorPage.heading"):exceptionMessage;
	}
    }

    /**
     * Execute a fulltextSearch from the request parameters
     * 
     * @return SUCCESS if the search is successfull
     * @throws Exception
     *                 in case of errors
     */
    public String search() throws Exception {
	executeQuery();
	return SUCCESS;

    }

    /**
     * Execute a fulltextSearch from the request parameters
     * 
     * @return POPUPVIEW if the search is successfull The view will not be
     *         decorated by sitemesh (see decorators.xml)
     * @throws Exception
     *                 in case of errors
     */
    public String searchpopup() throws Exception {
	executeQuery();
	return POPUP_VIEW;
    }

    /**
     * @return the languages
     */
    public List<String> getLanguages() {
	synchronized (syncToken) {
	    if (cachedUsedLanguageCode.size()==0){
		    cachedUsedLanguageCode = alternateNameDao.getUsedLanguagesCodes();
		}
	}
	return cachedUsedLanguageCode;
    }

    /**
     * @return the available countries
     */
    public List<Country> getCountries() {
	return countryDao.getAllSortedByName();
    }

    /**
     * @return the available
     */
    public OutputStyle[] getVerbosityModes() {
	return Output.OutputStyle.values();
    }

    /**
     * @return the available formats for fulltext search
     */
    public OutputFormat[] getFormats() {
	return OutputFormatHelper.listFormatByService(GisgraphyServiceType.FULLTEXT);
    }

    /**
     * @param fullTextSearchEngine
     *                the fullTextSearchEngine to set
     */
    public void setFullTextSearchEngine(
	    IFullTextSearchEngine fullTextSearchEngine) {
	this.fullTextSearchEngine = fullTextSearchEngine;
    }

    /**
     * @param country
     *                the country parameter to set
     */
    public void setCountry(String country) {
	this.country = country;
    }

    /**
     * @param lang
     *                the lang parameter to set
     */
    public void setLang(String lang) {
	this.lang = lang;
    }

    /**
     * @param style
     *                the style parameter to set
     */
    public void setStyle(String style) {
	this.style = style;
    }

    /**
     * @param q
     *                The q parameter to set
     */
    public void setQ(String q) {
	this.q = q;
    }

    /**
     * @return the country
     */
    public String getCountry() {
	return this.country;
    }

    /**
     * @return the lang
     */
    public String getLang() {
	return this.lang;
    }

    /**
     * @return the style
     */
    public String getStyle() {
	return this.style == null ? OutputStyle.getDefault().toString()
		: this.style;
    }

    /**
     * @return the q
     */
    public String getQ() {
	return this.q;
    }

    /**
     * @param placetype
     *                the placetype to set
     */
    public void setPlacetype(String placetype) {
	this.placetype = placetype;
    }

    /**
     * @return the placetype
     */
    public String getPlacetype() {
	return placetype;
    }

    /**
     * @param countryDao
     *                the countryDao to set
     */
    @Required
    public void setCountryDao(CountryDao countryDao) {
	this.countryDao = countryDao;
    }

    /**
     * @param alternateNameDao
     *                the alternateNameDao to set
     */
    public void setAlternateNameDao(IAlternateNameDao alternateNameDao) {
	this.alternateNameDao = alternateNameDao;
    }

    /**
     * @return the response
     */
    public FulltextResultsDto getResponseDTO() {
	return this.responseDTO;
    }
    
    /**
     * @return the spellchecking
     */
    public boolean isSpellchecking() {
        return spellchecking;
    }

    /**
     * @param spellchecking the spellchecking to set
     */
    public void setSpellchecking(boolean spellchecking) {
        this.spellchecking = spellchecking;
    }

	/**
	 * @return the allwordsrequired
	 */
	public boolean isAllwordsrequired() {
		return allwordsrequired;
	}

	/**
	 * @param allwordsrequired the allwordsrequired to set
	 */
	public void setAllwordsrequired(boolean allwordsrequired) {
		this.allwordsrequired = allwordsrequired;
	}

	 public Boolean isAdmin() {
		    return this.admin;
		}
	    
	  

	

	
}
