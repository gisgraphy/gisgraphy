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

import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.addressparser.AddressQuery;
import com.gisgraphy.addressparser.AddressResultsDto;
import com.gisgraphy.addressparser.IAddressParserService;
import com.gisgraphy.addressparser.web.AddressQueryHttpBuilder;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.repository.CountryDao;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.common.OutputFormat;

/**
 * geocoding by text action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class AddressParserAction extends SearchAction {
    
    private boolean autosubmit = false;

    private static final long serialVersionUID = -9018894533914543310L;

    private static Logger logger = LoggerFactory
	    .getLogger(AddressParserAction.class);

    private IAddressParserService addressParser;

    private CountryDao countryDao;

    private  AddressResultsDto addressResultsDto;

    // form parameters

    private String country;
    private String style;
    private String address;
    
     
    /**
     * @return Wether the search has been done and the results should be
     *         displayed
     */
    public boolean isDisplayResults() {
	return this.addressResultsDto != null;
    }

    private void executeQuery() {
	try {
	   AddressQuery query = AddressQueryHttpBuilder.getInstance().buildFromRequest(ServletActionContext.getRequest());
	    
	    this.addressResultsDto = addressParser.execute(query);
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
     * @return the available countries
     */
    public List<Country> getCountries() {
	return countryDao.getAllSortedByName();
    }

    
    /**
     * @return the available formats for fulltext search
     */
    public OutputFormat[] getFormats() {
	return OutputFormatHelper.listFormatByService(GisgraphyServiceType.GEOCODING);
    }

    

    /**
     * @param country
     *                the country parameter to set
     */
    public void setCountry(String country) {
	this.country = country;
    }

    
    

   
    /**
     * @param address the address to set
     */
    public void setAdress(String address) {
	this.address = address;
    }

    /**
     * @return the country
     */
    public String getCountry() {
	return this.country;
    }


    /**
     * @return the style
     */
    public String getStyle() {
	return this.style == null ? OutputStyle.getDefault().toString()
		: this.style;
    }

    /**
     * @return the address to parse
     */
    public String getAddress() {
	return this.address;
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
     * @return the response
     */
    public AddressResultsDto getAddressResultsDto() {
	return this.addressResultsDto;
    }
    

    /**
     * @return the autosubmit
     */
    public boolean isAutosubmit() {
        return autosubmit;
    }

    /**
     * @param autosubmit the autosubmit to set
     */
    public void setAutosubmit(boolean autosubmit) {
        this.autosubmit = autosubmit;
    }

	/**
	 * @param addressParser the addressParser to set
	 */
    @Required
	public void setAddressParser(IAddressParserService addressParser) {
		this.addressParser = addressParser;
	}

  

   
  

}
