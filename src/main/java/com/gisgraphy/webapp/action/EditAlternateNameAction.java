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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.IAlternateNameDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.helper.StringHelper;
import com.gisgraphy.service.IInternationalisationService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

/**
 * Edit AlternateName action (crud) specially in ajax
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class EditAlternateNameAction extends BaseAction implements Preparable {

    private static final long serialVersionUID = 3331811022397633034L;

    private static Logger logger = LoggerFactory.getLogger(EditAlternateNameAction.class);

    private IAlternateNameDao alternateNameDao;

    private IGisFeatureDao gisFeatureDao;

    private AlternateName alternatename;

    private IInternationalisationService internationalisationService;

    private ISolRSynchroniser solRSynchroniser;
    /**
     * The transaction manager
     */
    private PlatformTransactionManager transactionManager;

    private TransactionStatus txStatus = null;

    private DefaultTransactionDefinition txDefinition;

    private Long id;

    private Long gisFeatureId;

    /*
     * Those specific fields needs to be process separately because of decimal
     * separator or enum type
     */
    private String errorMessage;
    private String stackTrace;

    public void prepare() {
	// we have to test httpparameter because prepare is called before the
	// setters are called
	HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
	String parameter = request.getParameter("id");
	if (parameter != null && !parameter.equals("")) {
	    Long idAsLong = null;
	    try {
		idAsLong = Long.parseLong(parameter);
	    } catch (NumberFormatException e) {
		errorMessage = "id should be numeric";
		logger.error(errorMessage);
	    }
	    id = idAsLong;
	}
	if (id != null) {
	    alternatename = alternateNameDao.get(getId());
	}
    }

    public String persist() {
	return doPersist();
    }

    public String doPersist() {
	if (alternatename != null) {
	    if (alternatename.getGisFeature()!=null){
		setGisFeatureId(alternatename.getGisFeature().getFeatureId());
	    }
	    GisFeature gisfeature = gisFeatureDao.getByFeatureId(getGisFeatureId());
	    if (gisfeature == null) {
		addFieldError("gisfeatureId", "no gisfeature to be link to the alternatename");
		return ERROR;
	    }
	    checkMissingRequiredfields();
	    alternatename.setSource(AlternateNameSource.PERSONAL);
	    if (getFieldErrors().keySet().size() > 0) {
		return ERROR;
	    } else {
		if (alternatename.getId() != null) {
		    // it is an update
		    startTransaction();
		    try {
			gisfeature.getAlternateNames().remove(alternatename);
			gisfeature.addAlternateName(alternatename);
			gisFeatureDao.save(gisfeature);
		    } catch (Exception e) {
			rollbackTransaction();
			errorMessage = "could not save alternateName " + e.getMessage();
			stackTrace = StringHelper.getStackTraceAsString(e);
			logger.error(errorMessage, e);
			return ERROR;
		    }
		    commitTransaction();
		    return SUCCESS;
		} else {
		    // it is a creation
			gisfeature.addAlternateName(alternatename);
			startTransaction();
			try {
			    gisFeatureDao.save(gisfeature);
			} catch (Exception e) {
			    rollbackTransaction();
			    errorMessage = "could not save alternateName " + e.getMessage();
			    stackTrace = StringHelper.getStackTraceAsString(e);
			    logger.error(errorMessage, e);
			    return ERROR;
			}
			commitTransaction();
			return SUCCESS;
		    }
	    }
	} else {
	    errorMessage = "There is no alternatename to save";
	    logger.error(errorMessage);
	    return ERROR;
	}
    }

    private void checkMissingRequiredfields() {
	if (alternatename != null) {
	    if (alternatename.getName() == null) {
		addFieldError("name", internationalisationService.getString("errors.required", new String[] { "name" }));
	    }
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
	txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	txDefinition.setReadOnly(false);

	txStatus = transactionManager.getTransaction(txDefinition);
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String doDelete() {
	if (alternatename != null) {
	    GisFeature feature = gisFeatureDao.getByFeatureId(alternatename.getGisFeature().getFeatureId());
	    feature.getAlternateNames().remove(alternatename);
	   // alternateName.setGisFeature(null);

	    startTransaction();
	    try {

		gisFeatureDao.save(feature);
		alternateNameDao.remove(alternatename);
		gisFeatureDao.flushAndClear();
	    } catch (Exception e) {
		logger.error("Can not delete the alternatename : " + e.getMessage(), e);
		stackTrace = StringHelper.getStackTraceAsString(e);
		rollbackTransaction();
		return ERROR;
	    }
	    commitTransaction();
	    return SUCCESS;
	} else {
	    errorMessage = "there is no entity to delete";
	    return ERROR;
	}
    }
    
    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
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
     * @param stackTrace
     *            the stackTrace to set
     */
    public void setStackTrace(String stackTrace) {
	this.stackTrace = stackTrace;
    }


    public AlternateName getAlternatename() {
	return alternatename;
    }

    public void setAlternatename(AlternateName alternateName) {
	this.alternatename = alternateName;
    }

    public Long getGisFeatureId() {
	return gisFeatureId;
    }

    public void setGisFeatureId(Long gisFeatureId) {
	this.gisFeatureId = gisFeatureId;
    }

    @Required
    public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
	this.gisFeatureDao = gisFeatureDao;
    }
    
    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
	this.transactionManager = transactionManager;
    }
    
    @Required
    public void setInternationalisationService(IInternationalisationService internationalisationService) {
	this.internationalisationService = internationalisationService;
    }
    
    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
	this.solRSynchroniser = solRSynchroniser;
    }
    
    @Required
    public void setAlternateNameDao(IAlternateNameDao alternateNameDao) {
	this.alternateNameDao = alternateNameDao;
    }

}
