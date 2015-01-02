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
package com.gisgraphy.domain.geoloc.entity.event;

import java.util.List;

import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.GisFeature;

/**
 * Event that occurred on several {@link GisFeature}s
 * 
 * @see GisFeatureEvent
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class GisFeaturesEvent implements IGisRepositoryEvent {

    /**
     * The {@link GisFeature}s the current event refers to
     */
    private List<? extends GisFeature> gisFeatures;

    /**
     * @return The {@link GisFeature}s the current event refers to
     */
    public List<? extends GisFeature> getGisFeatures() {
	return this.gisFeatures;
    }

    /**
     * Default constructor
     * 
     * @param gisFeatures
     *                The {@link GisFeature}s the current event refers to
     */
    public GisFeaturesEvent(List<? extends GisFeature> gisFeatures) {
	Assert.notNull(gisFeatures, "can not create an event for a null list");
	this.gisFeatures = gisFeatures;
    }

}
