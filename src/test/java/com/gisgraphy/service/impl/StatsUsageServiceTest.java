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

import javax.annotation.Resource;

import org.junit.Test;

import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;
import com.gisgraphy.domain.repository.IStatsUsageDao;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsageType;

public class StatsUsageServiceTest extends AbstractTransactionalTestCase {

    @Resource
    IStatsUsageService statsUsageService;

    @Resource
    private IStatsUsageDao statsUsageDao;

    @Test
    public void testStatUsageServiceShouldInitAllTheCounter() {
	int counter = statsUsageService.getNumberOfCounter();
	assertEquals(StatsUsageType.values().length, counter);
    }

    @Test
    public void testGetUsage() {
	statsUsageService.resetUsage(StatsUsageType.FULLTEXT);
	statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
	Long usage = statsUsageService.getUsage(StatsUsageType.FULLTEXT);
	assertEquals(new Long(1), usage);
    }

    @Test
    public void testIncreaseUsageShouldIncrease() {
	Long usage = statsUsageService.getUsage(StatsUsageType.FULLTEXT);
	statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
	assertEquals(++usage, statsUsageService
		.getUsage(StatsUsageType.FULLTEXT));
    }

    @Test
    public void testFlush() {
	statsUsageService.resetUsage(StatsUsageType.FULLTEXT);
	statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
	assertEquals(new Long(0), statsUsageDao.getByUsageType(
		StatsUsageType.FULLTEXT).getUsage());
	assertEquals(new Long(1), statsUsageService
		.getUsage(StatsUsageType.FULLTEXT));
	statsUsageService.flush(StatsUsageType.FULLTEXT);
	assertEquals(new Long(1), statsUsageDao.getByUsageType(
		StatsUsageType.FULLTEXT).getUsage());
    }

    @Test
    public void testResetUsageShouldResetAndFlush() {
	statsUsageService.resetUsage(StatsUsageType.FULLTEXT);
	statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
	statsUsageService.flush(StatsUsageType.FULLTEXT);
	assertEquals(new Long(1), statsUsageService
		.getUsage(StatsUsageType.FULLTEXT));

	statsUsageService.resetUsage(StatsUsageType.FULLTEXT);
	assertEquals(new Long(0), statsUsageService
		.getUsage(StatsUsageType.FULLTEXT));
    }

    @Test
    public void testIncreaseUsageShouldflush() {
	statsUsageService.resetUsage(StatsUsageType.FULLTEXT);
	for (int i = 1; i < IStatsUsageService.FLUSH_THRESHOLD; i++) {
	    statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
	    assertEquals(new Long(0), statsUsageDao.getByUsageType(
		    StatsUsageType.FULLTEXT).getUsage());
	}
	statsUsageService.increaseUsage(StatsUsageType.FULLTEXT);
	assertEquals(new Long(IStatsUsageService.FLUSH_THRESHOLD),
		statsUsageDao.getByUsageType(StatsUsageType.FULLTEXT)
			.getUsage());
    }

}
