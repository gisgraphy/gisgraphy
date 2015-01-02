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
package com.gisgraphy.stats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Represent the usage of a {@link StatsUsageType}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SequenceGenerator(name = "statsUsageSequence", sequenceName = "stats_Usage_Sequence")
public class StatsUsage {

    /**
     * Default constructor
     */
    protected StatsUsage() {
	super();
    }

    private Long id;

    private StatsUsageType statsUsageType;

    private Long usage = 0L;

    /**
     * Constructor to init statsUsageType
     * 
     * @param statsUsageType
     *                statsUsageType
     */
    public StatsUsage(StatsUsageType statsUsageType) {
	this.statsUsageType = statsUsageType;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statsUsageSequence")
    public Long getId() {
	return id;
    }

    /**
     * @param id
     *                the id to set
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * @return the usageType
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    public StatsUsageType getStatsUsageType() {
	return statsUsageType;
    }

    /**
     * @param statsUsageType
     */
    public void setStatsUsageType(StatsUsageType statsUsageType) {
	this.statsUsageType = statsUsageType;
    }

    /**
     * @return the usage
     */
    public Long getUsage() {
	return usage;
    }

    /**
     * @param usage
     *                the usage to set
     */
    public void setUsage(Long usage) {
	this.usage = usage;
    }

    public void increaseUsage() {
	usage++;
    }

}
