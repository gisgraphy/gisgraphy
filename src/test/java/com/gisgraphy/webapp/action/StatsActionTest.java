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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.repository.IStatsUsageDao;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsage;
import com.gisgraphy.stats.StatsUsageType;
import com.opensymphony.xwork2.Action;

public class StatsActionTest {

    @Test
    public void execute() throws Exception {
	List<StatsUsage> statsUsageList = new ArrayList<StatsUsage>();
	StatsUsage statsUsage1 = new StatsUsage(StatsUsageType.FULLTEXT);
	StatsUsage statsUsage2 = new StatsUsage(StatsUsageType.GEOLOC);
	StatsUsage statsUsage3 = new StatsUsage(StatsUsageType.STREET);
	statsUsage1.setUsage(10L);
	statsUsage2.setUsage(20L);
	statsUsage3.setUsage(30L);
	statsUsageList.add(statsUsage1);
	statsUsageList.add(statsUsage2);
	statsUsageList.add(statsUsage3);
	
	StatsAction statsAction = new StatsAction();
	IStatsUsageDao mockStatsUsageDao = EasyMock.createMock(IStatsUsageDao.class);
	EasyMock.expect(mockStatsUsageDao.getAll()).andReturn(statsUsageList).times(2);
	EasyMock.replay(mockStatsUsageDao);
	statsAction.setStatsUsageDao(mockStatsUsageDao);
	String returnString = statsAction.execute();
	Assert.assertEquals("statsusage should be loaded when execute is called",statsUsageList,statsAction.getStatsUsages());
	Assert.assertEquals(Action.SUCCESS, returnString);
	Assert.assertEquals(60L, statsAction.getTotalUsage().longValue());
	returnString = statsAction.execute();
	Assert.assertEquals("totalusage should not be recursively added for each call to execute", 60L, statsAction.getTotalUsage().longValue());
	EasyMock.verify(mockStatsUsageDao);
    }
    
    @Test
    public void getFlushFrequency(){
	StatsAction statsAction = new StatsAction();
	assertEquals("getFlushFrequecy should return the flush threshold",IStatsUsageService.FLUSH_THRESHOLD, statsAction.getFlushFrequency());
    }

   
}
