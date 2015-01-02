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

package com.gisgraphy.importer.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a house number as an interpolation in the Karlsruhe schema.
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class InterpolationHouseNumber {

	/**
	 * the id ow the way
	 */
	private String wayId;
	
	/**
	 * the nodes of the way
	 */
	private List<InterpolationMember> members = new ArrayList<InterpolationMember>();

	/**
	 *  the name of the street associated to the way, not to the node
	 */
	private String streetName;
	
	/**
	 * The type of interpolation (even,odd,...)
	 */
	private InterpolationType interpolationType = InterpolationType.all;
	
	private AddressInclusion AddressInclusion;
	
	/**
	 * @return the wayID
	 */
	public String getWayId() {
		return wayId;
	}

	/**
	 * @param wayId the wayID to set
	 */
	public void setWayId(String wayId) {
		this.wayId = wayId;
	}

	/**
	 * @return the members
	 */
	public List<InterpolationMember> getMembers() {
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<InterpolationMember> members) {
		this.members = members;
	}
	/**
	 * Add a member
	 * @param member
	 */
	public void addMember(InterpolationMember member){
		members.add(member);
	}

	/**
	 * @return the interpolationType
	 */
	public InterpolationType getInterpolationType() {
		return interpolationType;
	}

	/**
	 * @param interpolationType the interpolationType to set
	 */
	public void setInterpolationType(InterpolationType interpolationType) {
		this.interpolationType = interpolationType;
	}

	/**
	 * @return the streetName
	 */
	public String getStreetName() {
		return streetName;
	}

	/**
	 * @param streetName the streetName to set
	 */
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	/**
	 * @return the addressInclusion
	 */
	public AddressInclusion getAddressInclusion() {
		return AddressInclusion;
	}

	/**
	 * @param addressInclusion the addressInclusion to set
	 */
	public void setAddressInclusion(AddressInclusion addressInclusion) {
		AddressInclusion = addressInclusion;
	}
}
