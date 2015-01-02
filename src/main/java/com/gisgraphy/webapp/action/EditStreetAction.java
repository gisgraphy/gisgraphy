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
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.CountryDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.service.IInternationalisationService;
import com.gisgraphy.street.StreetType;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Edit Street (openstreetMap entity) action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */

public class EditStreetAction extends BaseAction implements Preparable {

	private static final long serialVersionUID = 4785676484073350068L;

	private static Logger logger = LoggerFactory
			.getLogger(EditStreetAction.class);

	private IOpenStreetMapDao openStreetMapDao;


	private OpenStreetMap openstreetmap;

	private IInternationalisationService internationalisationService;

	private CountryDao countryDao;
	
	private IIdGenerator IdGenerator;
	/**
	 * The transaction manager
	 */
	private PlatformTransactionManager transactionManager;

	private TransactionStatus txStatus = null;

	private DefaultTransactionDefinition txDefinition;
	
	private ISolRSynchroniser solRSynchroniser;


	/*
	 * Those specific fields needs to be process separately because of decimal
	 * separator or enum type
	 */
	private String latitude;
	private String longitude;
	private Float latitudeAsFloat = null;
	private Float longitudeAsFloat = null;
	private String streettype;
	private Long id;
	
	private String errorMessage;
	private String stackTrace;

	public String getStreettype() {
		return streettype;
	}

	public void setStreettype(String streettype) {
		this.streettype = streettype;
	}

	private String shape;

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
		String parameter = request.getParameter("gid");
		if (parameter != null && !parameter.equals("")) {
			Long idAsLong = null;
			try {
			    idAsLong = Long.parseLong(parameter);
			} catch (NumberFormatException e) {
			    errorMessage="gid should be numeric";
				logger.error(errorMessage);
			}
			id = idAsLong;
		}
		if (openstreetmap != null && openstreetmap.getId() != null) {
			openstreetmap = openStreetMapDao.get(openstreetmap.getId());
		} else if (id != null) {
			openstreetmap = openStreetMapDao.getByGid(getId());
		}
	}

	public String input() {
		return INPUT;
	}

	public String save() {
		return doSave();
	}

	public String doSave() {
		if (openstreetmap != null) {
			checkMissingRequiredfields();
			//we sync the idgenerator in case an import is in progress
			//or several person add street simultaneously
			IdGenerator.sync();
			if(openstreetmap.getGid()==null){
			    openstreetmap.setGid(generateGid());
			}
			openstreetmap.setLocation(processPoint());
			LineString shapeProcessed = processShape();
			openstreetmap.setShape(shapeProcessed);
			openstreetmap.setStreetType(processStreettype());
			openstreetmap.setLength(processLength(shapeProcessed));
			if (getFieldErrors().keySet().size() > 0) {
				return INPUT;
			} else {
				startTransaction();
				try {
				    	StringHelper.updateOpenStreetMapEntityForIndexation(openstreetmap);
					openStreetMapDao.save(openstreetmap);
					
				} catch (Exception e) {
					rollbackTransaction();
					errorMessage="could not save street " + e.getMessage();
					stackTrace= StringHelper.getStackTraceAsString(e);
					logger.error(errorMessage, e);
					return ERROR;
				}
				commitTransaction();
				return SUCCESS;
			}
		} else {
			errorMessage="There is no street to save";
			logger.error(errorMessage);
			return ERROR;
		}
	}

	private void checkMissingRequiredfields() {
		if (getLatitude()==null){
			addFieldError("latitude",internationalisationService.getString("errors.required", new String[]{"latitude"}) );
		}
		if (getLongitude()==null){
			addFieldError("longitude",internationalisationService.getString("errors.required", new String[]{"longitude"}) );
		}
		if (openstreetmap!=null && (openstreetmap.getCountryCode()==null || openstreetmap.getCountryCode().trim().equals(""))){
			addFieldError("country",internationalisationService.getString("errors.required", new String[]{"country"}) );
		}
		
	}

	protected Double processLength(LineString lineString) {
		Double lengthAsDouble = null;
		if (lineString != null) {
			lengthAsDouble = lineString.getLength();
		}
		return lengthAsDouble;
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

	protected StreetType processStreettype() {
		if (getStreettype() != null) {
			StreetType type;
			try {
				type = StreetType.valueOf(getStreettype());
				return type;
			} catch (Exception e) {
				logger.warn("can not determine streetType for "
						+ getStreettype() + " : " + e);
			}
		}
		return null;
	}

	protected LineString processShape() {
		LineString lineString;
		if ((shape == null || "".equals(shape)) && latitudeAsFloat != null && longitudeAsFloat != null) {
			try {
				lineString = GeolocHelper.createLineString("LINESTRING ("
						+ longitudeAsFloat + " " + latitudeAsFloat + "," + longitudeAsFloat + " "
						+ latitudeAsFloat + ")");
			} catch (Exception e) {
				logger.warn("can not createlinestring with lat/long "
						+ latitudeAsFloat + "/" + longitudeAsFloat);
				addFieldError(
						"shape",
						internationalisationService
								.getString("error.wrong.lat.long.for.linestring"));
				return null;
			}
		} else {
			try {
				lineString = GeolocHelper.createLineString(shape);
			} catch (Exception e) {
				logger.warn("Can not determine shape for " + shape);
				addFieldError("shape",
						internationalisationService
								.getString("errors.invalid",new String[]{"shape"}));
				return null;
			}
		}
		return lineString;
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

	protected long generateGid() {
		return IdGenerator.getNextGId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the max openstreetmap id found in the Datastore + an increment
	 */
	

	public String doDelete() {
		if (openstreetmap!=null){
		startTransaction();
		try{
		openStreetMapDao.remove(openstreetmap);
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


	public OpenStreetMap getOpenstreetmap() {
		return openstreetmap;
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

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public void setOpenstreetmap(OpenStreetMap openstreetmap) {
		this.openstreetmap = openstreetmap;
	}

	public void setopenstreetmaps(OpenStreetMap openstreetmap) {
		this.openstreetmap = openstreetmap;
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


	@Required
	public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
		this.openStreetMapDao = openStreetMapDao;
	}

	@Required
	public void setCountryDao(CountryDao countryDao) {
		this.countryDao = countryDao;
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

	

}
