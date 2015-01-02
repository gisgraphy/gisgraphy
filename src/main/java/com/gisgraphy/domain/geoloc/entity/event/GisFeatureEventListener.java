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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import com.gisgraphy.domain.repository.ISolRSynchroniser;

/**
 * Basic implementation that must be aware of {@link GisFeatureEvent}s. It will
 * syncronized the database with the full text search engine
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Component
public class GisFeatureEventListener implements IEventListener {

    ISolRSynchroniser solRSynchroniser;

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.entity.event.IEventListener#handleEvent(com.gisgraphy.domain.geoloc.entity.event.IEvent)
     */
    public void handleEvent(IEvent event) {
	solRSynchroniser.handleEvent(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.entity.event.IEventListener#supports(com.gisgraphy.domain.geoloc.entity.event.IEvent)
     */
    public boolean supports(IEvent event) {
	if (event instanceof IGisRepositoryEvent) {
	    return true;
	}
	return false;
    }

    /**
     * @param solRSynchroniser
     *                The SolRSynchroniser to set
     */
    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
	this.solRSynchroniser = solRSynchroniser;
    }

}
