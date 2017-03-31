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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.importer.IImporterManager;
import com.gisgraphy.importer.ImporterManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action that do the all import
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * @see ImporterManager
 */
public class HookAction extends ActionSupport {
    /**
     * 
     */
    private static final long serialVersionUID = -1459641055753044129L;

    private static Logger logger = LoggerFactory.getLogger(HookAction.class);

    
    public static String HOOKAJAX = "hookajax";
    
    private String hookURL;
    
    private String importerManagerHookURL="n/a";
    
	@Autowired
	private IImporterManager importerManager;
    
    
    

	
	/**
	 * @return the importerManagerHookURL
	 */
	public String getImporterManagerHookURL() {
		return importerManager.getHookURL();
	}





    
    /**
	 * @return the hookURL
	 */
	public String getHookURL() {
		return hookURL;
	}



	/**
	 * @param hookURL the hookURL to set
	 */
	public void setHookURL(String hookURL) {
		this.hookURL = hookURL;
	}





	public String definehookurl(){
    	if (hookURL!=null){
			importerManager.setHookURL(hookURL);
		} 
    	return HOOKAJAX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @Override
    public String execute() throws Exception {
    	return SUCCESS;
    }
    
   
    /**
     * @return the importerManager
     */
    @Required
    public IImporterManager getImporterManager() {
	return importerManager;
    }


  

}
