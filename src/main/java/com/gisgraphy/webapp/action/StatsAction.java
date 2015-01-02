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

import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.domain.repository.IStatsUsageDao;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsage;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * stats Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class StatsAction extends ActionSupport {

    /**
     * Default serialId
     */
    private static final long serialVersionUID = -9018894533914543310L;

    private IStatsUsageDao statsUsageDao;

    private List<StatsUsage> statsUsages;
    
    private Long totalUsage;

    /**
     * @return the statsUsages
     */
    public List<StatsUsage> getStatsUsages() {
	return statsUsages;
    }

    /**
     * @param statsUsageDao
     *                the statsUsageDao to set
     */
    @Required
    public void setStatsUsageDao(IStatsUsageDao statsUsageDao) {
	this.statsUsageDao = statsUsageDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @Override
    public String execute() throws Exception {
	statsUsages = statsUsageDao.getAll();
	totalUsage = 0L;
	for (StatsUsage statsUsage: statsUsages){
	    totalUsage = totalUsage + statsUsage.getUsage();
	}
	return Action.SUCCESS;
    }

    /**
     * @return the totalUsage
     */
    public Long getTotalUsage() {
        return totalUsage;
    }
    
    public int getFlushFrequency(){
	return IStatsUsageService.FLUSH_THRESHOLD;
    }

}
