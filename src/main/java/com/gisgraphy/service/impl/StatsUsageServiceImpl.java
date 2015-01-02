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
package com.gisgraphy.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.gisgraphy.domain.repository.IStatsUsageDao;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsage;
import com.gisgraphy.stats.StatsUsageType;

public class StatsUsageServiceImpl implements IStatsUsageService {


    private static final Long RESET_COUNTER_VALUE = Long.valueOf(0L);

    private Map<String, Long> counterMap = new HashMap<String, Long>();
    
    @Autowired
    IStatsUsageDao statsUsageDao;

    @Autowired
    private PlatformTransactionManager transactionManager;
    
    public static boolean disabled = false;


    @PostConstruct
    protected void init() {
	for (StatsUsageType statsUsageType : StatsUsageType.values()) {
	    initCounter(statsUsageType);
	}

    }

    private StatsUsage initCounter(StatsUsageType statsUsageType) {
	StatsUsage statsUsage = statsUsageDao
	    .getByUsageType(statsUsageType);
	if (statsUsage == null) {
	statsUsage = new StatsUsage(statsUsageType);
	statsUsageDao.save(statsUsage);
	}
	this.counterMap.put(statsUsage.getStatsUsageType().toString(),
	    statsUsage.getUsage());
	return statsUsage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.service.IStatsUsageService#GetNumberOfCounter()
     */
    public int getNumberOfCounter() {
    	return counterMap.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.service.IStatsUsageService#GetUsage(com.gisgraphy.stats.StatsUsageType)
     */
    public Long getUsage(StatsUsageType statsUsageType) {
    	return counterMap.get(statsUsageType.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.service.IStatsUsageService#increaseUsage(com.gisgraphy.stats.StatsUsageType)
     */
    public void increaseUsage(StatsUsageType statsUsageType) {
    	if (!disabled){
    		long newValue = counterMap.get(statsUsageType.toString()) + 1;
    		counterMap.put(statsUsageType.toString(), newValue);
    		if (newValue % IStatsUsageService.FLUSH_THRESHOLD == 0) {
    			flush(statsUsageType);
    		}
    	}
    }

    public void resetUsage(StatsUsageType statsUsageType) {
    	if (!disabled){
    		counterMap.put(statsUsageType.toString(), RESET_COUNTER_VALUE);
    		flush(statsUsageType);
    	}
    }

    public void flush(StatsUsageType statsUsageType) {
    	if (!disabled){
    		statsUsageDao.flushAndClear();
    		StatsUsage statsUsage = statsUsageDao.getByUsageType(statsUsageType);
    		if (statsUsage==null){
    			statsUsage= initCounter(statsUsageType);
    		}
    		TransactionStatus txStatus = null;
    		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
    		txDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    		txDefinition.setReadOnly(false);

    		txStatus = transactionManager.getTransaction(txDefinition);
    		statsUsage.setUsage(counterMap.get(statsUsageType.toString()));
    		statsUsageDao.save(statsUsage);
    		transactionManager.commit(txStatus);
    	}
    }


}
