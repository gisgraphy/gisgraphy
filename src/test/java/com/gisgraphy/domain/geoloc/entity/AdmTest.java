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
package com.gisgraphy.domain.geoloc.entity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

public class AdmTest extends AbstractIntegrationHttpSolrTestCase {

    // test addchildren()
    @Test
    public void testAddChildrenShouldAddChildrenAndNotReplace() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 4);
	Adm admChildren = new Adm(gisFeatureChildren, 4);
	GisFeature gisFeatureChildren2 = GisgraphyTestHelper
		.createGisFeatureForAdm("child2", 2.5F, 3.2F, 1002L, 4);
	Adm admChildren2 = new Adm(gisFeatureChildren2, 4);
	GisFeature gisFeatureChildren3 = GisgraphyTestHelper
		.createGisFeatureForAdm("child2", 2.5F, 3.2F, 1003L, 4);
	Adm admChildren3 = new Adm(gisFeatureChildren3, 4);
	List<Adm> adms = new ArrayList<Adm>();
	adms.add(admChildren2);
	adms.add(admChildren3);
	// add a child
	adm.addChild(admChildren);
	assertEquals(1, adm.getChildren().size());

	// add some children
	adm.addChildren(adms);
	// check that children has benn added and not replace
	assertEquals(3, adm.getChildren().size());
    }

    @Test
    public void testAddChildrenShouldDoADoubleSet() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 4);
	Adm admChildren = new Adm(gisFeatureChildren, 4);
	GisFeature gisFeatureChildren2 = GisgraphyTestHelper
		.createGisFeatureForAdm("child2", 2.5F, 3.2F, 1002L, 4);
	Adm admChildren2 = new Adm(gisFeatureChildren2, 4);
	List<Adm> adms = new ArrayList<Adm>();
	adms.add(admChildren);
	adms.add(admChildren2);

	// add some children
	adm.addChildren(adms);
	// check that children has been added and not replace
	for (Adm child : adm.getChildren()) {
	    assertEquals(adm, child.getParent());
	}
    }

    @Test
    public void testAddChildrenWithAChildWithLowerLevelShouldThrows() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 2);
	Adm admChildren = new Adm(gisFeatureChildren, 2);
	List<Adm> adms = new ArrayList<Adm>();
	adms.add(admChildren);

	// add some children
	try {
	    adm.addChildren(adms);
	    fail("Adding children with a lower Level should throws an IllegalArgumentException");
	} catch (IllegalArgumentException e) {
	}

    }

    @Test
    public void testAddChildrenWithAChildWithSameLevelShouldThrows() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 3);
	Adm admChildren = new Adm(gisFeatureChildren, 3);
	List<Adm> adms = new ArrayList<Adm>();
	adms.add(admChildren);

	// add some children
	try {
	    adm.addChildren(adms);
	    fail("Adding a children with same Level should throws an IllegalArgumentException");
	} catch (IllegalArgumentException e) {
	}

    }

    @Test
    public void testAddChildrenWithANullListShouldNotThrows() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 4);
	Adm admChildren = new Adm(gisFeatureChildren, 4);
	List<Adm> adms = new ArrayList<Adm>();
	adms.add(admChildren);

	try {
	    adm.addChildren(null);
	} catch (IllegalArgumentException e) {
	    fail("Adding a null children List should not throw");
	}

    }

    // test add child
    @Test
    public void testAddChildWithAChildWithLowerLevelShouldThrows() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 2);
	Adm admChildren = new Adm(gisFeatureChildren, 2);

	try {
	    adm.addChild(admChildren);
	    fail("Adding a child with lower Level should throws an IllegalArgumentException");
	} catch (IllegalArgumentException e) {
	}

    }

    @Test
    public void testAddChildWithANullChildWShouldThrows() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	try {
	    adm.addChild(null);
	    fail("Adding a null child should throws an IllegalArgumentException");
	} catch (IllegalArgumentException e) {
	}

    }

    @Test
    public void testAddChildWithAChildWithSameLevelShouldThrows() {
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm("adm",
		2.5F, 3.2F, 1000L, 3);
	Adm adm = new Adm(gisFeature, 3);

	GisFeature gisFeatureChildren = GisgraphyTestHelper
		.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 3);
	Adm admChildren = new Adm(gisFeatureChildren, 3);

	try {
	    adm.addChild(admChildren);
	    fail("Adding a child with same Level should throws an IllegalArgumentException");
	} catch (IllegalArgumentException e) {
	}

    }

    /* check level consistence */
    @Test
    public void testThatAnAdmSouldNotHaveAlevelsuperiorTo4() {
	try {
	    GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3", "D4",
		    null, 5);
	    fail("An adm should not have a level > 4");
	} catch (IllegalArgumentException e) {
	}
    }

    @Test
    public void testThatAnAdmSouldNotHaveAlevelInferiorTo1() {
	try {
	    GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3", "D4",
		    null, 0);
	    fail("An adm should not have a level < 1");
	} catch (IllegalArgumentException e) {
	}
    }
    // TODO
    /*
     * //test setChildren //we can not put integrity on setter field because all
     * the fields are not setted and so we can not check @Test public void
     * testSetChildrenWithAChildWithLowerLevelShouldThrows() { GisFeature
     * gisFeature = GeolocTestHelper.createGisFeatureForAdm("adm", 2.5F, 3.2F,
     * 1000L, 3); Adm adm =new Adm(gisFeature,3); GisFeature gisFeatureChildren =
     * GeolocTestHelper.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 2);
     * Adm admChildren =new Adm(gisFeatureChildren,2); List<Adm> adms = new
     * ArrayList<Adm>(); adms.add(admChildren); try { adm.setChildren(adms);
     * fail("Setting children with a lower Level should throws an
     * IllegalArgumentException"); } catch (IllegalArgumentException e) { } }
     * @Test public void testSetChildrenWithAChildWithSameLevelShouldThrows() {
     * GisFeature gisFeature = GeolocTestHelper.createGisFeatureForAdm("adm",
     * 2.5F, 3.2F, 1000L, 3); Adm adm =new Adm(gisFeature,3); GisFeature
     * gisFeatureChildren = GeolocTestHelper.createGisFeatureForAdm("child1",
     * 2.5F, 3.2F, 1001L, 3); Adm admChildren =new Adm(gisFeatureChildren,3);
     * List<Adm> adms = new ArrayList<Adm>(); adms.add(admChildren); try {
     * adm.setChildren(adms); fail("Setting children with the same Level should
     * throws an IllegalArgumentException"); } catch (IllegalArgumentException
     * e) { } } @Test public void testSetChildrenWithANullListShouldNotThrows() {
     * GisFeature gisFeature = GeolocTestHelper.createGisFeatureForAdm("adm",
     * 2.5F, 3.2F, 1000L, 3); Adm adm =new Adm(gisFeature,3); GisFeature
     * gisFeatureChildren = GeolocTestHelper.createGisFeatureForAdm("child1",
     * 2.5F, 3.2F, 1001L, 4); Adm admChildren =new Adm(gisFeatureChildren,4);
     * List<Adm> adms = new ArrayList<Adm>(); adms.add(admChildren); try {
     * adm.setChildren(null); } catch (IllegalArgumentException e) {
     * fail("Adding a null children should not throws an
     * IllegalArgumentException"); } } //test parent @Test public void
     * testSetParentWithANullParentShouldNotThrows() { GisFeature gisFeature =
     * GeolocTestHelper.createGisFeatureForAdm("adm", 2.5F, 3.2F, 1000L, 3); Adm
     * adm =new Adm(gisFeature,3); try { adm.setParent(null); } catch
     * (IllegalArgumentException e) { fail("Setting a null Parent should not
     * throws an IllegalArgumentException"); } } @Test public void
     * testSetParentWithASameLevelShouldThrows() { GisFeature gisFeature =
     * GeolocTestHelper.createGisFeatureForAdm("adm", 2.5F, 3.2F, 1000L, 3); Adm
     * adm =new Adm(gisFeature,3); GisFeature gisFeatureParent =
     * GeolocTestHelper.createGisFeatureForAdm("child1", 2.5F, 3.2F, 1001L, 3);
     * Adm admParent =new Adm(gisFeatureParent,3); try {
     * adm.setParent(admParent); fail("Adding a parent with a same level should
     * throws an IllegalArgumentException"); } catch (IllegalArgumentException
     * e) { } } @Test public void testSetParentWithAHigherLevelShouldThrows() {
     * GisFeature gisFeature = GeolocTestHelper.createGisFeatureForAdm("adm",
     * 2.5F, 3.2F, 1000L, 3); Adm adm =new Adm(gisFeature,3); GisFeature
     * gisFeatureParent = GeolocTestHelper.createGisFeatureForAdm("child1",
     * 2.5F, 3.2F, 1001L, 4); Adm admParent =new Adm(gisFeatureParent,4); try {
     * adm.setParent(admParent); fail("Adding a parent with a higher level
     * should throws an IllegalArgumentException"); } catch
     * (IllegalArgumentException e) { } }
     */

}
