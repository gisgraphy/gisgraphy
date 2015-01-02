/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.helper;

import org.easymock.EasyMock;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.AbstractTransactionalTestCase;

public class GisFeatureHelperTest extends AbstractTransactionalTestCase {
    
    private GisFeatureHelper gisFeatureHelper;

    @Test
    public void testGetInstance() {
	assertNotNull("gisFeatureHelper should return an instance", GisFeatureHelper.getInstance());
    }

    @Test
    public void testGetFullyQualifiedNameGisFeatureBoolean() {
	GisFeature gisFeature = createGisFeatureMock();
	Country country = new Country();
	country.setName("countryName");
	EasyMock.expect(gisFeature.getCountryCode()).andReturn("FR");
	EasyMock.replay(gisFeature);
	GisFeatureHelper.getInstance().getFullyQualifiedName(gisFeature, true);
	EasyMock.verify(gisFeature);
    }

    private GisFeature createGisFeatureMock() {
	GisFeature gisFeature = EasyMock.createMock(GisFeature.class);
	EasyMock.expect(gisFeature.getAdm1Name()).andReturn("adm1name");
	EasyMock.expect(gisFeature.getAdm2Name()).andReturn("adm2name");
	EasyMock.expect(gisFeature.getName()).andReturn("name");
	
	return gisFeature;
    }

    @Test
    public void testGetFullyQualifiedNameGisFeature() {
	GisFeature gisFeature = createGisFeatureMock();
	EasyMock.replay(gisFeature);
	GisFeatureHelper.getInstance().getFullyQualifiedName(gisFeature);
	EasyMock.verify(gisFeature);
    }

    @Test
    public void testGetCountry() {
	//fail("Not yet implemented");
    }

  

    public void setGisFeatureHelper(GisFeatureHelper gisFeatureHelper) {
        this.gisFeatureHelper = gisFeatureHelper;
    }

}
