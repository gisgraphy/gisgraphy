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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

public class AdmDaoTest extends AbstractIntegrationHttpSolrTestCase {

    private IAdmDao admDao;

    private IGisFeatureDao gisFeatureDao;

    private GisgraphyTestHelper geolocTestHelper;


    @Test
    public void testSaveAdmWithoutChildsShouldSaveAdm() {
	// save Adm
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrieved = this.admDao.get(savedAdm.getId());
	assertEquals(savedAdm, retrieved);
	assertEquals(savedAdm.getId(), retrieved.getId());
    }

    @Test
    public void testSaveAdmShouldSaveChildsInCascade() {
	int nbChilds = 2;
	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
		null, null, null, null, 1);
	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
		"B2", null, null, null, 2, nbChilds);
	// double set
	admParent.setChildren(childs);
	Adm savedAdmParent = this.admDao.save(admParent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);
	List<Adm> retrievedChilds = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChilds);
	assertEquals(nbChilds, retrievedChilds.size());
    }

    @Test
    public void testSaveAdmShouldSaveChildsOfAllLevelsInCascade() {
	// save adm with childs
	int nbChildsSubLevel = 2;
	int nbChildsSubSublevel = 3;
	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
		null, null, null, null, 1);
	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
		"B2", null, null, null, 2, nbChildsSubLevel);
	List<Adm> ChildsLevel2 = GisgraphyTestHelper.createAdms("admchilds", "FR",
		"A1", "B2", null, null, null, 3, nbChildsSubSublevel);
	// set childs
	admParent.setChildren(childs);
	childs.get(0).setChildren(ChildsLevel2);

	Adm savedAdmParent = this.admDao.save(admParent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);

	// check adm of sublevel are saved
	List<Adm> retrievedChildsSubLevel = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChildsSubLevel);
	assertEquals(nbChildsSubLevel, retrievedChildsSubLevel.size());

	// check adm of subsublevel are saved too
	List<Adm> retrievedChildsSubSubLevel = this.admDao.getAllbyLevel(3);
	assertNotNull(retrievedChildsSubSubLevel);
	assertEquals(nbChildsSubSublevel, retrievedChildsSubSubLevel.size());
    }

    @Test
    public void testSaveAdmWithParentShouldUpdateParentSChilds() {
	Adm parent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1", null,
		null, null, null, 1);
	Adm child1 = GisgraphyTestHelper.createAdm("admchilds", "FR", "A1", "B2",
		null, null, null, 2);
	Adm child2 = GisgraphyTestHelper.createAdm("admchilds", "FR", "A1", "B2",
		null, null, null, 2);

	List<Adm> childs = new ArrayList<Adm>();
	childs.add(child1);
	parent.setChildren(childs);

	// save parent and check it is saved and it have one child
	Adm savedAdmParent = this.admDao.save(parent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);
	assertNotNull(retrievedParent.getChildren());
	assertEquals(1, retrievedParent.getChildren().size());

	// child2.setParent(parent);
	parent.addChild(child2);

	// save child2 and check it is saved
	Adm savedChild2 = this.admDao.save(child2);
	assertNotNull(savedChild2.getId());
	Adm retrievedChild2 = this.admDao.get(savedChild2.getId());
	assertEquals(savedChild2, retrievedChild2);

	// check Parent have one more child
	Adm retrievedParentAfterSave = this.admDao.getAdm1("FR", "A1");
	assertNotNull(retrievedParentAfterSave);
	assertNotNull(retrievedParentAfterSave.getChildren());
	assertEquals(2, retrievedParentAfterSave.getChildren().size());
    }

    @Test
    public void testCountByLevelShouldRetrieveACorrectNumberAccordingToTheSpecifiedLevel() {
	// save adm with childs
	int nbChilds = 2;
	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
		null, null, null, null, 1);
	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
		"B2", null, null, null, 2, nbChilds);

	admParent.addChildren(childs);
	Adm savedAdmParent = this.admDao.save(admParent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);
	long nbRetrievedChilds = this.admDao.countByLevel(2);
	assertEquals(nbChilds, nbRetrievedChilds);
    }

    /* check level consistence according to admXcode */
    @Test
    public void testSaveAdmWithLeve11WithoutAdm1CodeShouldthrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", null, "B2", "C3",
		    "D4", null, 1);
	    admDao.save(adm);
	    fail("adm1 could not be saved if adm1code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve2WithoutAdm1CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", null, "B2", "C3",
		    "D4", null, 2);
	    admDao.save(adm);
	    fail("adm2 could not be saved if adm1code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve2WithoutAdm2CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, "C3",
		    "D4", null, 2);
	    admDao.save(adm);
	    fail("adm2 could not be saved if adm2code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve3WithoutAdm1CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", null, "B2", "C3",
		    "D4", null, 3);
	    admDao.save(adm);
	    fail("adm3 could not be saved if adm1code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve3WithoutAdm2CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, "C3",
		    "D4", null, 3);
	    admDao.save(adm);
	    fail("adm3 could not be saved if adm2code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve3WithoutAdm3CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		    "D4", null, 3);
	    admDao.save(adm);
	    fail("adm3 could not be saved if adm3code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve4WithoutAdm1CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", null, "B2", "C3",
		    "D4", null, 4);
	    admDao.save(adm);
	    fail("adm4 could not be saved if adm1code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve4WithoutAdm2CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, "C3",
		    "D4", null, 4);
	    admDao.save(adm);
	    fail("adm4 could not be saved if adm2code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve4WithoutAdm3CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		    "D4", null, 4);
	    admDao.save(adm);
	    fail("adm4 could not be saved if adm3code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithLeve4WithoutAdm4CodeShouldThrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		    null, null, 4);
	    admDao.save(adm);
	    fail("adm4 could not be saved if adm4code is missing");
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSaveAdmWithNoCountryCodeShouldthrow() {
	try {
	    Adm adm = GisgraphyTestHelper.createAdm("adm", null, "A1", "B2", "C3",
		    "D4", null, 4);
	    admDao.save(adm);
	    fail("adm could not be saved if countrycode is missing");
	} catch (RuntimeException e) {
	}
    }

    // * !!!!!!!!!!!!!!!!!!!!!update!!!!!!!!!!!!!!!!!!!!!!

    @Test
    public void testUpdateAdmWithGisFeatureShouldUpdate() {
	String newname = "adm_gisFeature";
	// save Adm
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrieved = this.admDao.get(savedAdm.getId());
	assertEquals(savedAdm, retrieved);
	assertEquals(savedAdm.getId(), retrieved.getId());

	// create and Save gisFeature
	GisFeature gisFeature = GisgraphyTestHelper.createGisFeatureForAdm(
		newname, 1.3F, 45F, null, 4);
	// we need to set the admXcode because populate will set them to null if
	// we don't
	gisFeature.setAdm1Code("A1");
	gisFeature.setAdm2Code("B2");
	gisFeature.setAdm3Code("C3");
	gisFeature.setAdm4Code("D4");

	// update adm
	savedAdm.populate(gisFeature);
	Adm savedAdmAfterGisFeature = this.admDao.save(savedAdm);
	Adm retrievedAdmAfterGisFeature = this.admDao
		.get(savedAdmAfterGisFeature.getId());
	assertEquals(newname, retrievedAdmAfterGisFeature.getName());

    }

    // test delete

    @Test
    public void testDeleteAdmShouldDeleteAdm() {
	// save Adm
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrieved = this.admDao.get(savedAdm.getId());
	assertEquals(savedAdm, retrieved);
	assertEquals(savedAdm.getId(), retrieved.getId());

	// delete adm
	Long id = retrieved.getId();
	this.admDao.remove(retrieved);
	Adm admAfterRemove = this.admDao.get(id);
	assertEquals(null, admAfterRemove);
    }

    @Test
    public void testDeleteAdmWithChildsShouldDeleteChildsOfFirstSublevel() {
	// save adm with childs
	int nbChilds = 2;
	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
		null, null, null, null, 1);
	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
		"B2", null, null, null, 2, nbChilds);
	// double set
	admParent.setChildren(childs);
	Adm savedAdmParent = this.admDao.save(admParent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);
	List<Adm> retrievedChilds = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChilds);
	assertEquals(nbChilds, retrievedChilds.size());

	// delete parentAdm
	Long id = retrievedParent.getId();
	this.admDao.remove(retrievedParent);
	// chek parent is deleted
	Adm admAfterRemove = this.admDao.get(id);
	assertEquals(null, admAfterRemove);

	// check childs are deleted
	List<Adm> retrievedChildsafterRemove = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChildsafterRemove);
	assertEquals(0, retrievedChildsafterRemove.size());

    }

    @Test
    public void testDeleteAdmWithChildsShouldDeleteChildsOfSublevels() {
	// save adm with childs
	int nbChildsSubLevel = 2;
	int nbChildsSubSublevel = 3;
	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
		null, null, null, null, 1);
	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
		"B2", null, null, null, 2, nbChildsSubLevel);
	List<Adm> ChildsLevel2 = GisgraphyTestHelper.createAdms("admchilds", "FR",
		"A1", "B2", null, null, null, 3, nbChildsSubSublevel);
	// set childs
	admParent.setChildren(childs);
	childs.get(0).setChildren(ChildsLevel2);

	Adm savedAdmParent = this.admDao.save(admParent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);

	// check adm of sublevel are saved
	List<Adm> retrievedChildsSubLevel = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChildsSubLevel);
	assertEquals(nbChildsSubLevel, retrievedChildsSubLevel.size());

	// check adm of subsublevel are saved too
	List<Adm> retrievedChildsSubSubLevel = this.admDao.getAllbyLevel(3);
	assertNotNull(retrievedChildsSubSubLevel);
	assertEquals(nbChildsSubSublevel, retrievedChildsSubSubLevel.size());

	// delete parentAdm
	Long id = retrievedParent.getId();
	this.admDao.remove(retrievedParent);
	// chek parent is deleted
	Adm admAfterRemove = this.admDao.get(id);
	assertEquals(null, admAfterRemove);

	// check childs of subLevel are deleted
	List<Adm> retrievedChildsSubLevelAfterRemove = this.admDao
		.getAllbyLevel(2);
	assertNotNull(retrievedChildsSubLevelAfterRemove);
	assertEquals(0, retrievedChildsSubLevelAfterRemove.size());

	// check childs of subLevel are deleted
	List<Adm> retrievedChildsSubSubLevelAfterRemove = this.admDao
		.getAllbyLevel(2);
	assertNotNull(retrievedChildsSubSubLevelAfterRemove);
	assertEquals(0, retrievedChildsSubSubLevelAfterRemove.size());

    }

    @Test
    public void testDeleteAdmShouldNotDeleteHisParentInCascade() {
	int nbChilds = 2;
	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
		null, null, null, null, 1);
	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
		"B2", null, null, null, 2, nbChilds);
	// double set
	admParent.setChildren(childs);
	Adm savedAdmParent = this.admDao.save(admParent);
	assertNotNull(savedAdmParent.getId());
	Adm retrievedParent = this.admDao.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParent);
	List<Adm> retrievedChilds = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChilds);
	assertEquals(nbChilds, retrievedChilds.size());

	// delete a child and remove from association (if not it will be resaved
	// in cascade)
	Adm admToDelete = retrievedChilds.get(0);
	this.admDao.remove(admToDelete);
	admParent.getChildren().remove(admToDelete);

	// check it is removed
	List<Adm> retrievedChildsAfterRemove = this.admDao.getAllbyLevel(2);
	assertNotNull(retrievedChildsAfterRemove);
	assertEquals(nbChilds - 1, retrievedChildsAfterRemove.size());

	// check his parent is not removed
	Adm retrievedParentAfterRemove = this.admDao
		.get(savedAdmParent.getId());
	assertEquals(savedAdmParent, retrievedParentAfterRemove);

    }

    @Test
    public void testDeleteAdmShouldDeleteGisFeatureContainedInCascade() {
	// save Adm
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm is saved
	Adm retrievedAdm = this.admDao.get(savedAdm.getId());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());

	// creategisFeatureand set his Adm
	GisFeature gisFeature = GisgraphyTestHelper.createCity("paris", 1.3F, 45F,
		null);
	gisFeature.setAdm(retrievedAdm);
	

	// save gisFeature
	GisFeature savedGisFeature = gisFeatureDao.save(gisFeature);

	// check it is saved
	GisFeature retrievedGisFeature = this.gisFeatureDao.get(savedGisFeature
		.getId());
	assertNotNull(retrievedGisFeature);
	assertEquals(savedGisFeature, retrievedGisFeature);

	// delete adm
	long AdmId = savedAdm.getId();
	this.admDao.remove(savedAdm);

	// check adm is deleted
	Adm retrievedAdmAfterRemove = this.admDao.get(AdmId);
	assertNull(retrievedAdmAfterRemove);

	// check gisFeature is not still in datastore
	//we must clear the cache
	gisFeatureDao.flushAndClear();
	GisFeature retrievedGisFeatrueAfterRemove = this.gisFeatureDao
		.get(retrievedGisFeature.getId());
	assertNull(retrievedGisFeatrueAfterRemove);
	// assertEquals(retrievedGisFeature, retrievedGisFeatrueAfterRemove);

    }

    @Test
    public void deleteAllByLevelShouldOnlyDeleteAdmOfTheSpecifiedLevel() {
	City city = geolocTestHelper
		.createAndSaveCityWithFullAdmTreeAndCountry(3L);
	gisFeatureDao.remove(city);
	assertEquals(3, admDao.count());
	assertEquals(1, admDao.deleteAllByLevel(3));
	assertEquals(2, admDao.count());

    }

    // * !!!!!!!!!!!!!!!!!!!!!get!!!!!!!!!!!!!!!!!!!!!!

    @Test
    public void testSuggestAdmShouldReturnCorrectValues() {
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm1 = this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm2 = this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm3 = this.admDao.save(adm3);
	Adm adm4 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm4 = this.admDao.save(adm4);
	// check all are saved
	assertEquals(4, this.admDao.count());

	// The Adm exists
	Adm suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "A1", "B2",
		"C3", "D4", null);
	assertEquals(savedAdm4, suggestAdm);

	// An Adm with the Highest not null code exist (an Adm with level 4 and
	// Adm4code=D4) and no parent exists
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "AA", "BB", "CC",
		"D4", null);
	assertEquals(savedAdm4, suggestAdm);

	// An Adm with the Highest not null code exist (an Adm with level 4 and
	// Adm4code=D4) and a parent exists but his level is 2 level higher
	// (adm2 with A1, B2)
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "A1", "B2", "CC",
		"D4", null);
	assertEquals(savedAdm4, suggestAdm);

	// An Adm with the Highest not null code exist (an Adm with level 4 and
	// Adm4code=D4) and a parent exists but his level is 3 level higher
	// (adm1 with A1)
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "A1", "BB", "CC",
		"D4", null);
	assertEquals(savedAdm1, suggestAdm);

	// No Adm with the Highest not null code exist (no Adm with level 4 and
	// Adm4code=D4) and a parent exists with level 3
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "A1", "B2", "C3",
		"DD", null);
	assertEquals(savedAdm3, suggestAdm);

	// No Adm with the Highest not null code exist (no Adm with level 4 and
	// Adm4code=D4) and a parent exists with level 2
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "A1", "B2", "CC",
		"DD", null);
	assertEquals(savedAdm2, suggestAdm);

	// No Adm with the Highest not null code exist (no Adm with level 4 and
	// Adm4code=D4) and a parent exists with level 1
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "A1", "BB", "CC",
		"DD", null);
	assertEquals(savedAdm1, suggestAdm);

	// No Adm with the Highest not null code exist (no Adm with level 4 and
	// Adm4code=D4) and a parent exists with level 1
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", "AA", "BB", "CC",
		"DD", null);
	assertNull(suggestAdm);

	// All params are null except countryCode
	suggestAdm = this.admDao.suggestMostAccurateAdm("FR", null, null, null,
		null, null);
	assertNull(suggestAdm);

	// All params are null except countryCode
	try {
	    suggestAdm = this.admDao.suggestMostAccurateAdm(null, null, null,
		    null, null, null);
	    fail();
	} catch (IllegalArgumentException e) {
	}

    }

    @Test
    public void testGetAdm1ShouldRetrieveTheCorrectAdm() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrievedAdm = this.admDao.getAdm1("FR", "A1");
	assertEquals(1, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm1ShouldBeCaseInsensitiveForCountryCode() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrievedAdm = this.admDao.getAdm1("fr", "A1");
	assertEquals(1, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm2ShouldRetrieveTheCorrectAdm() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm2 is saved
	Adm retrievedAdm = this.admDao.getAdm2("FR", "A1", "B2");
	assertEquals(2, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm2ShouldBeCaseInsensitiveForCountryCode() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm2 is saved
	Adm retrievedAdm = this.admDao.getAdm2("fr", "A1", "B2");
	assertEquals(2, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm3ShouldRetrieveTheCorrectAdm() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm3 is saved
	Adm retrievedAdm = this.admDao.getAdm3("FR", "A1", "B2", "C3");
	assertEquals(3, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm3ShouldBeCaseInsensitiveForCountryCode() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm3 is saved
	Adm retrievedAdm = this.admDao.getAdm3("fr", "A1", "B2", "C3");
	assertEquals(3, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm4ShouldRetrieveTheCorrectAdm() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm4 is saved
	Adm retrievedAdm = this.admDao.getAdm4("FR", "A1", "B2", "C3", "D4");
	assertEquals(4, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdm4BeCaseInsensitiveForCountryCode() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm4 is saved
	Adm retrievedAdm = this.admDao.getAdm4("fr", "A1", "B2", "C3", "D4");
	assertEquals(4, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());
    }

    @Test
    public void testGetAdmShouldretrieveTheCorrectAdmAccordingToNullParameters() {
	// create and save an Adm of each Level
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm1 = this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm2 = this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm3 = this.admDao.save(adm3);
	Adm adm4 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm4 = this.admDao.save(adm4);

	// test for adm1Code=null
	Adm retrievedAdm = this.admDao.getAdm("FR", null, "B2", "C3", "D4");
	assertNull(retrievedAdm);

	// test for adm1
	Adm retrievedAdm1 = this.admDao.getAdm("FR", "A1", null, null, null);
	assertNotNull(retrievedAdm1);
	assertEquals(savedAdm1, retrievedAdm1);

	// test for adm2
	Adm retrievedAdm2 = this.admDao.getAdm("FR", "A1", "B2", null, null);
	assertNotNull(retrievedAdm2);
	assertEquals(savedAdm2, retrievedAdm2);

	// test for adm3
	Adm retrievedAdm3 = this.admDao.getAdm("FR", "A1", "B2", "C3", null);
	assertNotNull(retrievedAdm3);
	assertEquals(savedAdm3, retrievedAdm3);

	// test for adm4
	Adm retrievedAdm4 = this.admDao.getAdm("FR", "A1", "B2", "C3", "D4");
	assertNotNull(retrievedAdm4);
	assertEquals(savedAdm4, retrievedAdm4);

    }

    @Test
    public void testGetAdmShouldBeCaseInsensitiveForCountryCode() {
	// create and save an Adm of each Level
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm1 = this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm2 = this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm3 = this.admDao.save(adm3);
	Adm adm4 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm4 = this.admDao.save(adm4);

	// test for adm1Code=null
	Adm retrievedAdm = this.admDao.getAdm("fr", null, "B2", "C3", "D4");
	assertNull(retrievedAdm);

	// test for adm1
	Adm retrievedAdm1 = this.admDao.getAdm("fr", "A1", null, null, null);
	assertNotNull(retrievedAdm1);
	assertEquals(savedAdm1, retrievedAdm1);

	// test for adm2
	Adm retrievedAdm2 = this.admDao.getAdm("fr", "A1", "B2", null, null);
	assertNotNull(retrievedAdm2);
	assertEquals(savedAdm2, retrievedAdm2);

	// test for adm3
	Adm retrievedAdm3 = this.admDao.getAdm("fr", "A1", "B2", "C3", null);
	assertNotNull(retrievedAdm3);
	assertEquals(savedAdm3, retrievedAdm3);

	// test for adm4
	Adm retrievedAdm4 = this.admDao.getAdm("fr", "A1", "B2", "C3", "D4");
	assertNotNull(retrievedAdm4);
	assertEquals(savedAdm4, retrievedAdm4);

    }

    @Test
    public void testGetAdmShouldretrieveTheCorrectAdmAccordingToEmptyStringParameters() {
	// create and save an Adm of each Level
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm1 = this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm2 = this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm3 = this.admDao.save(adm3);
	Adm adm4 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	Adm savedAdm4 = this.admDao.save(adm4);

	Adm retrievedAdm = this.admDao.getAdm("", "A1", "B2", "C3", "D4");
	assertNull(retrievedAdm);

	// test for countryCode=null
	retrievedAdm = this.admDao.getAdm("FR", "", "B2", "C3", "D4");
	assertNull(retrievedAdm);

	// test for adm1
	Adm retrievedAdm1 = this.admDao.getAdm("FR", "A1", "", "", "");
	assertNotNull(retrievedAdm1);
	assertEquals(savedAdm1, retrievedAdm1);

	// test for adm2
	Adm retrievedAdm2 = this.admDao.getAdm("FR", "A1", "B2", "", "");
	assertNotNull(retrievedAdm2);
	assertEquals(savedAdm2, retrievedAdm2);

	// test for adm3
	Adm retrievedAdm3 = this.admDao.getAdm("FR", "A1", "B2", "C3", "");
	assertNotNull(retrievedAdm3);
	assertEquals(savedAdm3, retrievedAdm3);

	// test for adm4
	Adm retrievedAdm4 = this.admDao.getAdm("FR", "A1", "B2", "C3", "D4");
	assertNotNull(retrievedAdm4);
	assertEquals(savedAdm4, retrievedAdm4);
    }

    @Test
    public void testGetUnused() {
	// save two adm1 with one unused

	Adm admUsed = GisgraphyTestHelper.createAdm("adm_used", "FR", "A1", "B2",
		"C3", "D4", null, 4);
	Adm admUnUsed = GisgraphyTestHelper.createAdm("adm_unused", "FR", "A1",
		"B2", "C3", "D5", null, 4);

	// save Admused and check it is well saved
	Adm savedAdmUsed = this.admDao.save(admUsed);
	assertNotNull(savedAdmUsed.getId());
	Adm retrievedAdmUsed = this.admDao.get(savedAdmUsed.getId());
	assertEquals(admUsed, retrievedAdmUsed);

	// save AdmUnused and check it is well saved
	Adm savedadmUnUsed = this.admDao.save(admUnUsed);
	assertNotNull(savedadmUnUsed.getId());
	Adm retrievedAdmUnUsed = this.admDao.get(savedadmUnUsed.getId());
	assertEquals(admUnUsed, retrievedAdmUnUsed);

	// double set not needeed
	// adm1.setAdm2s(adm2s);

	GisFeature gisFeature = GisgraphyTestHelper.createCity("gisFeature_city",
		1.3F, 45F, null);
	gisFeature.setAdm(savedAdmUsed);
	GisFeature savedGisFeature = this.gisFeatureDao.save(gisFeature);
	GisFeature retrievedGisFeature = this.gisFeatureDao.get(savedGisFeature
		.getId());
	assertNotNull(retrievedGisFeature.getId());
	assertEquals(savedGisFeature, retrievedGisFeature);

	List<Adm> unused = admDao.getUnused();
	assertNotNull(unused);
	assertEquals(1, unused.size());
	assertEquals(unused.get(0), savedadmUnUsed);
	assertEquals(unused.get(0).getId(), savedadmUnUsed.getId());

    }

    @Test
    public void testGetDirty() {
	Long durtyFeatureId = -3L;
	Long cleanfeatureId = 3L;
	Adm cleanAdm = GisgraphyTestHelper.createAdm("adm4_1", "FR", "A1", "B2",
		"C3", "D4", null, 4);
	cleanAdm.setFeatureId(cleanfeatureId);
	Adm durtyAdm = GisgraphyTestHelper.createAdm("adm4_2", "FR", "A1", "B2",
		"C3", "D5", null, 4);
	durtyAdm.setFeatureId(durtyFeatureId);

	// save Adm durty and check it is well saved
	Adm savedCleanAdm = this.admDao.save(cleanAdm);
	assertNotNull(savedCleanAdm.getId());
	Adm retrievedCleanAdm = this.admDao.get(savedCleanAdm.getId());
	assertEquals(cleanAdm, retrievedCleanAdm);

	// save Admclean and check it is well saved
	Adm savedDurtyAdm = this.admDao.save(durtyAdm);
	assertNotNull(savedDurtyAdm.getId());
	Adm retrievedDurtyAdm = this.admDao.get(savedDurtyAdm.getId());
	assertEquals(durtyAdm, retrievedDurtyAdm);

	List<Adm> durties = admDao.getDirties();
	assertNotNull(durties);
	assertEquals(1, durties.size());
	assertEquals(durties.get(0), retrievedDurtyAdm);
	assertEquals(durties.get(0).getId(), retrievedDurtyAdm.getId());
	assertEquals(durtyFeatureId, retrievedDurtyAdm.getFeatureId());
    }

    @Test
    public void testGetAdmByCountryAndCodeAndLevelShouldRetrieveTheCorrectAdm() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrievedAdm = this.admDao.getAdm1("FR", "A1");
	assertEquals(1, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());

	// check

	Adm retieved2 = this.admDao.getAdmByCountryAndCodeAndLevel("FR", "A1",
		1).get(0);
	assertNotNull(retieved2);
	assertEquals(1, retieved2.getLevel().intValue());
	assertEquals(savedAdm, retieved2);
    }

    @Test
    public void testGetAdmByCountryAndCodeAndLevelShouldBeCaseInsensitiveForCountryCode() {
	Adm adm = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm = this.admDao.save(adm);
	assertNotNull(savedAdm.getId());
	// check adm1 is saved
	Adm retrievedAdm = this.admDao.getAdm1("FR", "A1");
	assertEquals(1, retrievedAdm.getLevel().intValue());
	assertEquals(savedAdm, retrievedAdm);
	assertEquals(savedAdm.getId(), retrievedAdm.getId());

	// check

	Adm retieved2 = this.admDao.getAdmByCountryAndCodeAndLevel("fr", "A1",
		1).get(0);
	assertNotNull(retieved2);
	assertEquals(1, retieved2.getLevel().intValue());
	assertEquals(savedAdm, retieved2);
    }

    @Test
    public void testGetAdmByCountryAndCodeAndLevelShouldNeverReturnnullButAnEmptyList() {
	List<Adm> admByCountryAndCodeAndLevel = this.admDao
		.getAdmByCountryAndCodeAndLevel("fr", "A1", 1);
	assertNotNull(admByCountryAndCodeAndLevel);
	assertEquals(0, admByCountryAndCodeAndLevel.size());
    }

    @Test
    public void testGetAdmOrFirstValidParentIfNotFoundShouldReturnTheExistingParentIfTheAdmDoesnTExists() {
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	Adm savedAdm2 = this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	this.admDao.save(adm3);
	Adm retrieved = this.admDao.getAdmOrFirstValidParentIfNotFound("FR",
		"A1", "B2", "C4", null);
	assertNotNull(retrieved);
	assertEquals(savedAdm2, retrieved);
    }

    @Test
    public void testGetAdmOrFirstValidParentifNotFoundShouldReturnTheExistingGrandParentIfTheAdmDoesnTExists() {
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	Adm savedAdm1 = this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	this.admDao.save(adm3);
	Adm retrieved = this.admDao.getAdmOrFirstValidParentIfNotFound("FR",
		"A1", "B3", "C4", null);
	assertNotNull(retrieved);
	assertEquals(savedAdm1, retrieved);

    }

    @Test
    public void testGetAdmOrFirstValidParentifNotFoundShouldReturnTheAdmIfExists() {
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	Adm savedAdm3 = this.admDao.save(adm3);
	Adm retrieved = this.admDao.getAdmOrFirstValidParentIfNotFound("FR",
		"A1", "B2", "C3", null);
	assertNotNull(retrieved);
	assertEquals(savedAdm3, retrieved);
    }

    @Test
    public void testGetAdmOrFirstValidParentifNotFoundShouldReturnNullIfNoParentExists() {
	Adm adm1 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", null, null,
		null, null, 1);
	this.admDao.save(adm1);
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", null,
		null, null, 2);
	this.admDao.save(adm2);
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B2", "C3",
		null, null, 3);
	this.admDao.save(adm3);
	Adm retrieved = this.admDao.getAdmOrFirstValidParentIfNotFound("FR",
		"A2", "B3", "C4", null);
	assertNull(retrieved);
    }

    @Test
    public void testAdmConstructorWithLevelShouldAutomaticalySetTheCorrectFeatureClassAndCode() {
	int level = 4;
	Adm adm = new Adm(level);
	assertEquals("ADM" + level, adm.getFeatureCode());
	assertEquals("A", adm.getFeatureClass());
    }

    @Test
    public void testAdmConstructorWithGisFeatureAndLevelShouldAutomaticalySetTheCorrectFeatureClassAndCode() {
	int level = 2;
	Adm adm = new Adm(null, level);
	assertEquals("ADM" + level, adm.getFeatureCode());
	assertEquals("A", adm.getFeatureClass());
    }

    @Test
    public void testAdmConstructorShouldAutomaticalySetTheCorrectFeatureClass() {
	Adm adm = GisgraphyTestHelper.createAdm("adm1_1", "FR", "A1", "B2", "C3",
		"D4", null, 4);
	assertEquals("A", adm.getFeatureClass());
    }

    @Test
    public void testGetProcessedLevelFromFeatureClassCodeShouldReturnACorrectValue() {
	assertEquals(4, Adm.getProcessedLevelFromFeatureClassCode("A", "ADM4"));
	assertEquals(3, Adm.getProcessedLevelFromFeatureClassCode("A", "ADM3"));
	assertEquals(2, Adm.getProcessedLevelFromFeatureClassCode("A", "ADM2"));
	assertEquals(1, Adm.getProcessedLevelFromFeatureClassCode("A", "ADM1"));
	assertEquals(0, Adm.getProcessedLevelFromFeatureClassCode("B", "ADM4"));
	assertEquals(0, Adm.getProcessedLevelFromFeatureClassCode("A", "ADM"));
	assertEquals(0, Adm.getProcessedLevelFromFeatureClassCode("A", "ADM5"));
	assertEquals(0, Adm.getProcessedLevelFromFeatureClassCode(null, "ADM3"));
	assertEquals(0, Adm.getProcessedLevelFromFeatureClassCode("A", null));
    }

    @Test
    public void testGetProcessedLevelFromCodesShouldReturnACorrectValue() {
	assertEquals(0, Adm.getProcessedLevelFromCodes(null, null, null, null));
	assertEquals(1, Adm.getProcessedLevelFromCodes("A8", null, null, null));
	assertEquals(2, Adm.getProcessedLevelFromCodes("A8", "B1", null, null));
	assertEquals(2, Adm.getProcessedLevelFromCodes("A8", "B1", null, "D4"));
	assertEquals(3, Adm.getProcessedLevelFromCodes("A8", "B1", "C3", null));
	assertEquals(4, Adm.getProcessedLevelFromCodes("A8", "B1", "C3", "D4"));
    }

    // test Dao with null Value

    @Test
    public void testGetAdm1ShouldNotAcceptNullParameters() {
	try {
	    this.admDao.getAdm1(null, null);
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm1("FR", null);
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm1(null, "A1");
	    fail();
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testGetAdm2ShouldNotAcceptNullParameters() {
	try {
	    this.admDao.getAdm2(null, null, null);
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm2(null, "A1", "B2");
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm2("FR", null, "A1");
	    fail();
	} catch (RuntimeException e) {
	}
	try {
	    this.admDao.getAdm2("FR", "A1", null);
	    fail();
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testGetAdm3ShouldNotAcceptNullParameters() {
	try {
	    this.admDao.getAdm3(null, null, null, null);
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm3(null, "A1", "B2", "C3");
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm3("FR", null, "A1", "C3");
	    fail();
	} catch (RuntimeException e) {
	}
	try {
	    this.admDao.getAdm3("FR", "A1", null, "C3");
	    fail();
	} catch (RuntimeException e) {
	}
	try {
	    this.admDao.getAdm3("FR", "A1", "", null);
	    fail();
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testGetAdm4ShouldNotAcceptNullParameters() {
	try {
	    this.admDao.getAdm4(null, null, null, null, null);
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm4(null, "A1", "B2", "C3", "D4");
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm4("FR", null, "A1", "C3", "D4");
	    fail();
	} catch (RuntimeException e) {
	}
	try {
	    this.admDao.getAdm4("FR", "A1", null, "C3", "D4");
	    fail();
	} catch (RuntimeException e) {
	}
	try {
	    this.admDao.getAdm4("FR", "A1", "B2", null, "");
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdm4("FR", "A1", "B2", "", null);
	    fail();
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testGetAdmShouldNotAcceptNullCountry() {
	try {
	    this.admDao.getAdm(null, "", "", "", "");
	    fail();
	} catch (RuntimeException e) {
	}

    }

    @Test
    public void testGetAdmByCountryAndCodeAndLevelShouldNotAcceptNullParameters() {
	try {
	    this.admDao.getAdmByCountryAndCodeAndLevel(null, "", 3);
	    fail();
	} catch (RuntimeException e) {
	}

	try {
	    this.admDao.getAdmByCountryAndCodeAndLevel("", null, 3);
	    fail();
	} catch (RuntimeException e) {
	}

    }

    @Test
    public void testGetAdmOrFirstValidParentIfNotFoundShouldNotAcceptNullCountry() {
	try {
	    this.admDao
		    .getAdmOrFirstValidParentIfNotFound(null, "", "", "", "");
	    fail();
	} catch (RuntimeException e) {
	}
    }

    @Test
    public void testSuggestMostAccurateAdmShouldNotAcceptNullCountry() {
	try {
	    this.admDao.suggestMostAccurateAdm(null, "", "", "", "", null);
	    fail();
	} catch (RuntimeException e) {
	}

    }

    @Test
    public void testGetAdm2ShouldReturnNullWhenAmbiguousResultInFlexMode() {
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", null,
		null, null, 2);
	this.admDao.save(adm2);
	Adm adm2bis = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", null,
		null, null, 2);
	this.admDao.save(adm2bis);

	assertNull(this.admDao.getAdm2("FR", "00", "B1"));
	assertNull(this.admDao.getAdm("FR", "00", "B1", null, null));

    }

    @Test
    public void testGetAdm2ShouldThrowsWhenAmbiguousResultInNonFlexMode() {
	Adm adm2 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", null,
		null, null, 2);
	this.admDao.save(adm2);
	Adm adm2bis = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", null,
		null, null, 2);
	this.admDao.save(adm2bis);

	try {
	    this.admDao.getAdm2("FR", "A1", "B1");
	    fail("getAdm2 should throws if more than one result suits in non flex mode");
	} catch (RuntimeException e) {
	    assertTrue(true);
	}

	try {
	    this.admDao.getAdm("FR", "A1", "B1", null, null);
	    fail("getAdm for level 2 should throws if more than one result suits in non flex mode");
	} catch (RuntimeException e) {
	    assertTrue(true);
	}
    }

    @Test
    public void testGetAdm3ShouldReturnNullWhenAmbiguousResultInFlexMode() {
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		null, null, 3);
	this.admDao.save(adm3);
	Adm adm3bis = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		null, null, 3);
	this.admDao.save(adm3bis);

	assertNull(this.admDao.getAdm3("FR", "00", "B1", "C1"));
	assertNull(this.admDao.getAdm("FR", "00", "B1", "C1", null));

    }

    @Test
    public void testGetAdm3ShouldThrowsWhenAmbiguousResultInNonFlexMode() {
	Adm adm3 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		null, null, 3);
	this.admDao.save(adm3);
	Adm adm3bis = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		null, null, 3);
	this.admDao.save(adm3bis);

	try {
	    this.admDao.getAdm3("FR", "A1", "B1", "C1");
	    fail("getAdm3 should throws if more than one result suits in non flex mode");
	} catch (RuntimeException e) {
	    assertTrue(true);
	}

	try {
	    this.admDao.getAdm("FR", "A1", "B1", "C1", null);
	    fail("getAdm for level 3 should throws if more than one result suits in non flex mode");
	} catch (RuntimeException e) {
	    assertTrue(true);
	}

    }

    @Test
    public void testGetAdm4ShouldReturnNullWhenAmbiguousResultInFlexMode() {
	Adm adm4 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		"D1", null, 4);
	this.admDao.save(adm4);
	Adm adm4bis = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		"D1", null, 4);
	this.admDao.save(adm4bis);

	assertNull(this.admDao.getAdm4("FR", "00", "B1", "C1", "D1"));
	assertNull(this.admDao.getAdm("FR", "00", "B1", "C1", "D1"));

    }

    @Test
    public void testGetAdm4ShouldThrowsWhenAmbiguousResultInNonFlexMode() {
	Adm adm4 = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		"D1", null, 4);
	this.admDao.save(adm4);
	Adm adm4bis = GisgraphyTestHelper.createAdm("adm", "FR", "A1", "B1", "C1",
		"D1", null, 4);
	this.admDao.save(adm4bis);

	try {
	    assertNull(this.admDao.getAdm4("FR", "A1", "B1", "C1", "D1"));
	    fail("getAdm4 should throws if more than one result suits in non flex mode");
	} catch (RuntimeException e) {
	    assertTrue(true);
	}

	try {
	    assertNull(this.admDao.getAdm("FR", "A1", "B1", "C1", "D1"));
	    fail("getAdm for level 4 should throws if more than one result suits in non flex mode");
	} catch (RuntimeException e) {
	    assertTrue(true);
	}

    }
    
    @Test
    public void testListFeatureIdByLevel(){
    	int nbChilds = 2;
    	Adm admParent = GisgraphyTestHelper.createAdm("admparent", "FR", "A1",
    		null, null, null, null, 1);
    	List<Adm> childs = GisgraphyTestHelper.createAdms("admchilds", "FR", "A1",
    		"B2", null, null, null, 2, nbChilds);
    	// double set
    	admParent.setChildren(childs);
    	this.admDao.save(admParent);
    	List<Long> listOfFeatureId = admDao.listFeatureIdByLevel(2);
    	Assert.assertEquals("The result hasn't the right size for level2",childs.size(), listOfFeatureId.size());
    	for (Adm adm : childs){
    		Assert.assertTrue("the result list for level 2 doesn't contains the featureId "+adm.getFeatureId(),listOfFeatureId.contains(adm.getFeatureId()));
    	}
    	
    	listOfFeatureId = admDao.listFeatureIdByLevel(1);
    	Assert.assertEquals("The result hasn't the right size for level 1 ",1, listOfFeatureId.size());
    	Assert.assertTrue("the result list for level 1 doesn't contains the featureId "+admParent.getFeatureId(),listOfFeatureId.contains(admParent.getFeatureId()));
    }

    public void setAdmDao(IAdmDao admDao) {
	this.admDao = admDao;
    }

    public void setGisFeatureDao(IGisFeatureDao gisFeatureDao) {
	this.gisFeatureDao = gisFeatureDao;
    }

}
