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

import java.util.List;

import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GenericDaoTest extends AbstractIntegrationHttpSolrTestCase {

    private IAdmDao admDao;

    @Test
    public void testCount() {
	int nbToCreate = 10;
	List<Adm> adms = GisgraphyTestHelper.createAdms("adm", "FR", "A1", "B2",
		"C3", "D4", null, 4, nbToCreate);
	// set durty/clean featureid

	// double set adm1 for adm1=>adm2s cascade
	for (Adm adm : adms) {
	    this.admDao.save(adm);
	}
	// check all are saved
	List<Adm> savedAdms = this.admDao.getAll();
	assertNotNull(adms);
	assertEquals(adms.size(), savedAdms.size());

	long count = admDao.count();
	assertEquals(nbToCreate, count);

    }

    public void setAdmDao(IAdmDao admDao) {
	this.admDao = admDao;
    }
}
