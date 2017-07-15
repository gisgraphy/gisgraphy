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

import com.gisgraphy.stats.IStatsDataManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * stats Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class StatsDataAction extends ActionSupport {

	private static Logger logger = LoggerFactory
			.getLogger(StatsDataAction.class);

	/**
	 * Default serialId
	 */
	private static final long serialVersionUID = -9018894533914543310L;

	@Autowired
	protected IStatsDataManager statsDataManager;

	public boolean refresh = false;

	public String countrycode;
	
	public String statsAsJson = "{}";

	/**
	 * @return the countrycode
	 */
	public String getCountrycode() {
		return countrycode;
	}

	/**
	 * @param countrycode the countrycode to set
	 */
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	/**
	 * @return the refresh
	 */
	public boolean isRefresh() {
		return refresh;
	}

	/**
	 * @param refresh the refresh to set
	 */
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	public String alljson() throws Exception {
		statsAsJson = statsDataManager.getAllInJson(refresh,true);
		return Action.SUCCESS;
	}


	public String onejson() throws Exception {
		if (countrycode==null){
			logger.info("not countrycode specified");
			statsAsJson = "{}";
			return Action.SUCCESS;
		}
		statsAsJson = statsDataManager.getOneInJson(countrycode, refresh);
		return Action.SUCCESS;
	}

	
	/**
	 * @return the statsAsJson
	 */
	public String getStatsAsJson() {
		return statsAsJson;
	}




}
