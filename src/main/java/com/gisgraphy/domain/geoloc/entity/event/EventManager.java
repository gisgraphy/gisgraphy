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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

/**
 * Manage all the Event Received. Default implementation of
 * {@link IEventManager}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Component
public class EventManager implements IEventManager {

    /**
     * The listeners that must be aware of events
     */
    private List<IEventListener> listeners;

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.entity.event.IEventManager#handleEvent(com.gisgraphy.domain.geoloc.entity.event.IEvent)
     */
    public void handleEvent(IEvent event) {
	for (IEventListener listener : listeners) {
	    if (listener.supports(event)) {
		listener.handleEvent(event);
	    }
	}
    }

    /**
     * @param listeners
     *                The listeners to set that must be aware of events
     */
    @Required
    public void setListeners(List<IEventListener> listeners) {
	this.listeners = listeners;
    }

}
