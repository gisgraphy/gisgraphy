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

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.CountryDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.FeatureCode;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.service.IInternationalisationService;
import com.gisgraphy.street.StreetType;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.vividsolutions.jts.geom.Point;

/**
 * Edit Feature all Geonames Entity, gisfeature and subclass action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class EditFeatureAction extends BaseAction implements Preparable {

	private static final long serialVersionUID = 4785676484073350068L;

	private static Logger logger = LoggerFactory
			.getLogger(EditFeatureAction.class);

	private IGisFeatureDao gisFeatureDao;

	private GisFeature gisfeature;

	private IInternationalisationService internationalisationService;

	private CountryDao countryDao;
	
	private IIdGenerator IdGenerator;
	
	private ISolRSynchroniser solRSynchroniser;

	private PlatformTransactionManager transactionManager;

	private TransactionStatus txStatus = null;

	private DefaultTransactionDefinition txDefinition;


	/*
	 * Those specific fields needs to be process separately because of decimal
	 * separator or enum type
	 */
	private String latitude;
	private String longitude;
	private Float latitudeAsFloat = null;
	private Float longitudeAsFloat = null;
	private Long id;
	
	private String errorMessage;
	private String stackTrace;
	private String classcode;


	/**
	 * @return the available countries
	 */
	public List<Country> getCountries() {
		return countryDao.getAllSortedByName();
	}

	public StreetType[] getStreetTypes() {
		return StreetType.values();
	}

	public void prepare() {
		// we have to test httpparameter because prepare is called before the
		// setters are called
		HttpServletRequest request = (HttpServletRequest) ActionContext
				.getContext().get(ServletActionContext.HTTP_REQUEST);
		String parameter = request.getParameter("featureid");
		if (parameter != null && !parameter.equals("")) {
			Long idAsLong=null;
			try {
			    idAsLong = Long.parseLong(parameter);
			} catch (NumberFormatException e) {
			    errorMessage="featureid should be numeric";
				logger.error(errorMessage);
			}
			id = idAsLong;
		}
		if (gisfeature != null && gisfeature.getId() != null) {
			gisfeature = gisFeatureDao.get(gisfeature.getId());
		} else if (id != null) {
			gisfeature = gisFeatureDao.getByFeatureId(getId());
		}
	}

	public String input() {
		return INPUT;
	}

	public String save() {
		return doSave();
	}
	
	public FeatureCode[] getPlacetypes(){
		return FeatureCode.values();
	}

	public String doSave() {
		if (gisfeature != null) {
			checkMissingRequiredfields();
			//we sync the idgenerator in case an import is in progress
			//or several person add street simultaneously
			IdGenerator.sync();
			if (gisfeature.getFeatureId()==null){
			    gisfeature.setFeatureId(generateFeatureId());
			}
			gisfeature.setLocation(processPoint());
			processFeatureClassCode(gisfeature);
			gisfeature.setSource(GISSource.PERSONAL);
			gisfeature.setModificationDate(new Date());
			if (getFieldErrors().keySet().size() > 0) {
				return INPUT;
			} else {
				startTransaction();
				try {
				    	if (gisfeature.getId()== null){
				    	gisfeature = getObjectFromFeatureClassCode(gisfeature);
				    	}
					gisFeatureDao.save(gisfeature);
				} catch (Exception e) {
					rollbackTransaction();
					errorMessage="could not save feature " + e.getMessage();
					stackTrace= StringHelper.getStackTraceAsString(e);
					logger.error(errorMessage, e);
					return ERROR;
				}
				commitTransaction();
				return SUCCESS;
			}
		} else {
			errorMessage="There is no feature to save";
			logger.error(errorMessage);
			return ERROR;
		}
	}

	protected void processFeatureClassCode(GisFeature feature) {
		if (classcode!=null && !"".equals(classcode.trim())){
			try {
				String[] splited = classcode.split("_");
				if (splited.length!=2){
					logger.warn("featurecode should be separated by '_'");
					return;
				}
				feature.setFeatureClass(splited[0]);
				feature.setFeatureCode(splited[1]);
			} catch (Exception e) {
				logger.warn("feature code "+classcode+" is not valid");
				return;
			}
		} else {
			feature.setFeatureClass(null);
			feature.setFeatureCode(null);
		}
		
	}
	
	protected GisFeature getObjectFromFeatureClassCode(GisFeature feature) {
		if (classcode!=null && !"".equals(classcode.trim())){
			try {
				FeatureCode featureCode = FeatureCode.valueOf(classcode);
				GisFeature placeType = featureCode.getObject();
				if (placeType==null){
				    logger.error("can not detemine placetype, the object retuned from classcode is null");
				    return gisfeature;
				}
				 placeType.populate(feature);
				 logger.info("class code correspond to placetype "+ placeType.getClass().getSimpleName());
				 return placeType;
				
				
			} catch (Exception e) {
				logger.warn("feature code "+classcode+" is not valid to determine the placetype");
				return gisfeature;
			}
		} else {
			return feature;
		}
		
	}

	private void checkMissingRequiredfields() {
		if (getLatitude()==null){
			addFieldError("latitude",internationalisationService.getString("errors.required", new String[]{"latitude"}) );
		}
		if (getLongitude()==null){
			addFieldError("longitude",internationalisationService.getString("errors.required", new String[]{"longitude"}) );
		}
		if (gisfeature!=null && (gisfeature.getName()==null || gisfeature.getName().trim().equals(""))){
			addFieldError("name",internationalisationService.getString("errors.required", new String[]{"name"}) );
		}
		if (gisfeature!=null && (gisfeature.getCountryCode()==null || gisfeature.getCountryCode().trim().equals(""))){
			addFieldError("country",internationalisationService.getString("errors.required", new String[]{"country"}) );
		}
	}

	private void commitTransaction() {
		transactionManager.commit(txStatus);
		solRSynchroniser.commit();
	}

	private void rollbackTransaction() {
		transactionManager.rollback(txStatus);
	}

	private void startTransaction() {
		txDefinition = new DefaultTransactionDefinition();
		txDefinition
				.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txDefinition.setReadOnly(false);

		txStatus = transactionManager.getTransaction(txDefinition);
	}

	
	protected Point processPoint() {
		try {
			if (latitude != null) {
				try {
					latitudeAsFloat = GeolocHelper
							.parseInternationalDouble(latitude);
					if (latitudeAsFloat < -90 || latitudeAsFloat > 90) {
						addFieldError("latitude",
								internationalisationService
										.getString("error.latitude.outOfRange"));
					}
				} catch (Exception e) {
					logger.warn("latitude : " + latitude + "is not a number");
					addFieldError("latitude",
							internationalisationService.getString(
									"error.notANumber",
									new String[] { "latitude" }));
				}

			}
			if (longitude != null) {
				try {
					longitudeAsFloat = GeolocHelper
							.parseInternationalDouble(longitude);
					if (longitudeAsFloat < -180 || longitudeAsFloat > 180) {
						addFieldError(
								"longitude",
								internationalisationService
										.getString("error.longitude.outOfRange"));
					}
				} catch (Exception e) {
					logger.warn("longitude : " + longitude + "is not a number");
					addFieldError("longitude",
							internationalisationService.getString(
									"error.notANumber",
									new String[] { "longitude" }));
				}
			}

			Point point = null;
			if (latitudeAsFloat != null && longitudeAsFloat != null) {
				point = GeolocHelper.createPoint(longitudeAsFloat,
						latitudeAsFloat);
				return point;
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.warn("can not determine point for lat/long : " + latitude
					+ "/" + longitude);
			addFieldError("latitude",
					internationalisationService
							.getString("error.not.gps.point"));
			return null;
		}

	}

	protected long generateFeatureId() {
		return IdGenerator.getNextFeatureId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String doDelete() {
		if (gisfeature!=null){
		startTransaction();
		try{
		gisFeatureDao.remove(gisfeature);
		} catch (Exception e){
			logger.error("Can not delete the street : "+e.getMessage(),e);
			stackTrace= StringHelper.getStackTraceAsString(e);
			rollbackTransaction();
			return ERROR;
		}
		commitTransaction();
		return SUCCESS;
		} else {
			errorMessage="there is no entity to delete";
			return ERROR;
		}
	}


	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the stackTrace
	 */
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * @param stackTrace the stackTrace to set
	 */
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	/**
	 * @return the gisfeature
	 */
	public GisFeature getGisfeature() {
		return gisfeature;
	}

	public void setGisfeature(GisFeature gisfeature) {
		this.gisfeature = gisfeature;
	}


	@Required
	public void setCountryDao(CountryDao countryDao) {
		this.countryDao = countryDao;
	}

	@Required
	public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
		this.gisFeatureDao = gisFeatureDao;
	}

	@Required
	public void setTransactionManager(
			PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Required
	public void setInternationalisationService(
			IInternationalisationService internationalisationService) {
		this.internationalisationService = internationalisationService;
	}

	
	@Required
	public void setIdGenerator(IIdGenerator idGenerator) {
		IdGenerator = idGenerator;
	}
	
	@Required
	public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
	    this.solRSynchroniser = solRSynchroniser;
	}

	/**
	 * @return the classcode
	 */
	public String getClasscode() {
		return classcode;
	}

	/**
	 * @param classcode the classcode to set
	 */
	public void setClasscode(String classcode) {
		this.classcode = classcode;
	}

}
