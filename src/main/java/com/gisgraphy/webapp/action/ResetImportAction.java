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

import com.gisgraphy.importer.IImporterManager;
import com.gisgraphy.importer.ImporterManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action that reset the import. SO USE THIS ACTION VERY CAREFULLY. The reset
 * will be launched only if the confirm attribute in session is equals to true
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @see ImporterManager
 */
public class ResetImportAction extends ActionSupport {

    private static final String RESET_IMPORT_CONFIRM = "RESET_IMPORT_CONFIRM";

    /**
     * Default serial ID
     */
    private static final long serialVersionUID = -259250843779247710L;

    private static Logger logger = LoggerFactory
	    .getLogger(ResetImportAction.class);

    private IImporterManager importerManager;

    private List<String> errorsAndWarningMessages = new ArrayList<String>();

    private boolean resetFailed = false;

    private String failedMessage;

    /**
     * view of the page that ask for confirmation
     */
    public static String ASK = "ask";

    /**
     * view of page that gives information after the reset
     */
    public static String RESET = "reset";

    /**
     * view of page that gives information after the reset
     */
    public static String IMPORT_IN_PROGRESS = "impossible";

    /*
     * (non-Javadoc)
     * 
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @Override
    public String execute() throws Exception {
	logger.info("the page for reseting the import has been called");
	if (importerManager.isInProgress()){
    	logger.info("can not reset the import because it is in progress");
    	return IMPORT_IN_PROGRESS;
    }
	return ASK;
    }

    /**
     * reset the import, only if {@linkplain #RESET_IMPORT_CONFIRM} is equals to
     * true
     * 
     * @see #confirm()
     * @return the reset view
     */
    public String reset() {
    if (importerManager.isInProgress()){
    	logger.info("can not reset the import because it is in progress");
    	return IMPORT_IN_PROGRESS;
    }
	if (isConfirmed()) {
	    logger.info("Reseting the import");
	    try {
		errorsAndWarningMessages = importerManager.resetImport();
	    } catch (Exception e) {
		resetFailed = true;
		failedMessage = e.getMessage()==null?e.toString():e.getMessage();
		logger.warn("The reset has failed : " + e.getMessage() +"due to "+e.getStackTrace());
	    }
	    unconfirm();
	    return RESET;
	} else {
	    logger
		    .info("Can not reset the import, because confirm is not true");
	    return ASK;
	}

    }

    public boolean isConfirmed() {
	Object confirmed = ServletActionContext.getRequest().getSession()
		.getAttribute(RESET_IMPORT_CONFIRM);
	logger.info("RESET_IMPORT_CONFIRM=" + confirmed + " in session");
	return confirmed == null ? false : ((Boolean) confirmed).booleanValue();
    }

    /**
     * set the {@linkplain #RESET_IMPORT_CONFIRM} in session to true
     * 
     * @return the reset view
     */
    public String confirm() {
	ServletActionContext.getRequest().getSession().setAttribute(
		RESET_IMPORT_CONFIRM, Boolean.TRUE);
	logger
		.info("Confirm has been set to true, the reset of the import will be possible");
	return ASK;
    }

    /**
     * remove the {@linkplain #RESET_IMPORT_CONFIRM} of the session
     */
    public void unconfirm() {
	ServletActionContext.getRequest().getSession().removeAttribute(
		RESET_IMPORT_CONFIRM);
    }

    /**
     * @param importerManager
     *                the importerManager to set
     */
    @Required
    public void setImporterManager(IImporterManager importerManager) {
	this.importerManager = importerManager;
    }

    /**
     * @return the deletedObjectInfo
     */
    public List<String> getErrorsAndWarningMessages() {
	return errorsAndWarningMessages;
    }

    /**
     * @return true if the reset has failed
     */
    public boolean isResetFailed() {
	return resetFailed;
    }

    /**
     * @return the failedMessage
     */
    public String getFailedMessage() {
	return failedMessage;
    }

}
