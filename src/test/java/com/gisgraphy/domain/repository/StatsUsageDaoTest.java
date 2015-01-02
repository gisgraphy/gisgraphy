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
package com.gisgraphy.domain.repository;

import javax.annotation.Resource;

import org.junit.Test;

import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsUsage;
import com.gisgraphy.stats.StatsUsageType;

public class StatsUsageDaoTest extends AbstractTransactionalTestCase {

    @Resource
    IStatsUsageService statsUsageService;

    @Resource
    private IStatsUsageDao statUsageDao;

    @Test
    public void testGetByUsageType() {

	if (statUsageDao.getAll().size() == StatsUsageType.values().length) {
	    StatsUsage retrieved = statUsageDao
		    .getByUsageType(StatsUsageType.FULLTEXT);
	    assertNotNull(retrieved);
	    assertEquals(StatsUsageType.FULLTEXT, retrieved.getStatsUsageType());
	} else {
	    statsUsageService.resetUsage(StatsUsageType.FULLTEXT);
	    statsUsageService.resetUsage(StatsUsageType.GEOLOC);
	    StatsUsage statsUsageFulltext = new StatsUsage(
		    StatsUsageType.FULLTEXT);
	    statsUsageFulltext.increaseUsage();
	    statUsageDao.save(statsUsageFulltext);
	    statUsageDao.flushAndClear();

	    StatsUsage statsUsageGeoloc = new StatsUsage(StatsUsageType.GEOLOC);
	    statsUsageGeoloc.increaseUsage();
	    statUsageDao.save(statsUsageGeoloc);
	    statUsageDao.flushAndClear();
	}

	StatsUsage retrieved = statUsageDao
		.getByUsageType(StatsUsageType.FULLTEXT);
	assertNotNull(retrieved);
	assertEquals(StatsUsageType.FULLTEXT, retrieved.getStatsUsageType());
    }

}
