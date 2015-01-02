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
/**
 * 
 */
package com.gisgraphy.service;

import com.gisgraphy.stats.StatsUsage;
import com.gisgraphy.stats.StatsUsageType;

/**
 * Manage The {@link StatsUsage}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public interface IStatsUsageService {

    /**
     * The satsUsage will be flush into the database every ... It is recommended
     * to increase this value to a higher value to improve performances. if the
     * server is stopped, you can loose FLUSH_THRESHOLD usage at most because the
     * value won't be persist in the datastore
     */
    int FLUSH_THRESHOLD = 50;

    /**
     * @return the number of counter that are managed
     */
    int getNumberOfCounter();

    /**
     * @param statsUsageType
     *                the usagetype
     * @return the Usage for the specified {@link StatsUsageType}
     */
    Long getUsage(StatsUsageType statsUsageType);

    /**
     * @param statsUsageType
     *                the {@link StatsUsageType} counter to increase Increase
     *                the counter of the specified type
     */
    void increaseUsage(StatsUsageType statsUsageType);

    /**
     * @param statsUsageType
     *                the {@link StatsUsageType} to reset reset the stats for
     *                the specified {@link StatsUsageType}
     */
    void resetUsage(StatsUsageType statsUsageType);

    /**
     * @param statsUsageType
     *                the {@link StatsUsageType} to flush flush the value into
     *                database
     */
    void flush(StatsUsageType statsUsageType);

}
