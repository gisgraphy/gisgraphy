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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.valueobject.GisgraphyConfig;
import com.opensymphony.xwork2.ActionSupport;

/**
 * DisplayStreet Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class DisplayStreetAction extends ActionSupport implements GoogleMapApiKeyAware{
    
    /**
     * The reference in the localized file for  the fact that the street has no name
     */
    public static final String GLOBAL_STREET_NONAME = "global.street.noname";

    /**
     * The reference in the localized file for the error for the fact gid
     * is required
     */
    public static final String ERROR_REF_REQUIRED_FEATURE_ID = "required.gid";

    /**
     * The reference in the localized file for the error for the fact that the
     * specified gid is not a numeric value
     */
    public static final String ERROR_REF_NON_NUMERIC_FEATUREID = "displayfeature.gid.numeric";


    /**
     * The reference in the localized file for the error for the fact that no
     * features were found for the specified gid
     */
    public static final String ERROR_REF_NORESULT = "result.street.noresult";

    /**
     * The reference in the localized file for general error
     */
    static final String ERROR_REF_GENERAL_ERROR = "display.error";

    /**
     * Default Generated serial Id
     */
    private static final long serialVersionUID = 2940477476216677L;


    private static Logger logger = LoggerFactory
	    .getLogger(DisplayStreetAction.class);

    private IOpenStreetMapDao openStreetMapDao;

    private String gid;
    
    private String shape = null;

    private OpenStreetMap result = null;
    
    private String lat;

    private String lng;

    public static final String ERROR = "error";
    
    /**
     * @return the fullyqualified name if exists, or the name
     */
    public String getPreferedName() {
	if (result == null) {
	    return "";
	} else {
	    return !StringUtils.isEmpty(result.getName()) ? result.getName()
		    :getText(GLOBAL_STREET_NONAME);
	}
    }

   
    private boolean isNumeric(String number) {
	try {
	    Integer.parseInt(number);
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    /**
     * The error message reference in the localized properties file
     */
    private String errorRef = "";

    private String errorMessage = "";

    /*
     * (non-Javadoc)
     * 
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @Override
    public String execute() throws Exception {
	try {
	    if (StringUtils.isEmpty(gid)) {
		errorRef = ERROR_REF_REQUIRED_FEATURE_ID;
		return ERROR;
	    }
	    if (!isNumeric(gid)) {
		errorRef = ERROR_REF_NON_NUMERIC_FEATUREID;
		return ERROR;
	    }
	    result = openStreetMapDao.getByGid(Long.valueOf(gid));
	    if (result == null) {
		errorRef = ERROR_REF_NORESULT;
		return ERROR;
	    } else {
	    	this.shape= retrieveShape(result.getGid());
	    }
	} catch (RuntimeException e) {
	    if (e.getCause() != null) {
		logger.warn("An error occured during search : "
			+ e.getCause().getMessage());
	    } else {
		logger.warn("An error occured during search : "
			+ e.getMessage());
	    }
	    this.errorRef = ERROR_REF_GENERAL_ERROR;
	    this.errorMessage = e.getMessage();
	    return ERROR;
	}
	return SUCCESS;
    }


    protected String retrieveShape(Long gid) {
    	if (gid!=null){
    		return openStreetMapDao.getShapeAsWKTByGId(gid);
    	}
    	return null;
		
	}


    /**
     * @return the gid
     */
    public String getGid() {
        return gid;
    }



    /**
     * @param gid the gid to set
     */
    public void setGid(String gid) {
        this.gid = gid;
    }


    /**
     * @param openStreetMapDao the openStreetMapDao to set
     */
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }





    /**
     * @return the errorRef
     */
    public String getErrorRef() {
	return errorRef;
    }

    /**
     * @return the result
     */
    public OpenStreetMap getResult() {
	return result;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }
    
    /**
     * @return the googleMapAPIKey
     */
    public String getGoogleMapAPIKey() {
        return GisgraphyConfig.googleMapAPIKey == null ? "" : GisgraphyConfig.googleMapAPIKey;
    }


	public String getShape() {
		return shape;
	}


	public String getLat() {
		return lat;
	}


	public void setLat(String lat) {
		this.lat = lat;
	}


	public String getLng() {
		return lng;
	}


	public void setLng(String lng) {
		this.lng = lng;
	}


}
