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
package com.gisgraphy.domain.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.test.GisgraphyTestHelper;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class AlternateNameDaoTest extends AbstractIntegrationHttpSolrTestCase {

    @Autowired
    private IGisFeatureDao gisFeatureDao;

    @Autowired
    private IAlternateNameDao alternateNameDao;

    /**
     * Test method for
     * {@link com.gisgraphy.domain.repository.AlternateNameDao#getUsedLanguagesCodes()}.
     */
    @Test
    public void testGetUsedLanguagesCodesShouldreturnAllDistinctLanguageCodes() {
	City paris = GisgraphyTestHelper.createCity("Paris", 3.4F, 4.5F, 5L);
	List<AlternateName> alternateNames = new ArrayList<AlternateName>();
	AlternateName a1 = new AlternateName("FrenchParis",
		AlternateNameSource.PERSONAL);
	a1.setLanguage("FR");
	alternateNames.add(a1);
	AlternateName a2 = new AlternateName("SpanishParis",
		AlternateNameSource.PERSONAL);
	a2.setLanguage("ES");
	alternateNames.add(a2);
	AlternateName a3 = new AlternateName("NoLangParis",
		AlternateNameSource.PERSONAL);
	alternateNames.add(a3);
	AlternateName a4 = new AlternateName("deutchParis",
		AlternateNameSource.PERSONAL);
	a4.setLanguage("DE");
	alternateNames.add(a4);
	paris.addAlternateNames(alternateNames);
	gisFeatureDao.save(paris);

	assertEquals(4L, alternateNameDao.count());
	List<String> languages = alternateNameDao.getUsedLanguagesCodes();
	assertNotNull(languages);
	assertEquals(3, alternateNameDao.getUsedLanguagesCodes().size());

    }

}
