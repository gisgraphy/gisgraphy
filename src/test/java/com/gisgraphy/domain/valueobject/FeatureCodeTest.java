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
package com.gisgraphy.domain.valueobject;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.GeolocHelper;

public class FeatureCodeTest extends AbstractIntegrationHttpSolrTestCase {

	@Resource
	IGisFeatureDao gisFeatureDao;

	private static Locale localeaa = new Locale("aa");
	private static Locale localeaacc = new Locale("aa", "dd");
	private static Locale localebb = new Locale("bb");
	private static Locale localebbdd = new Locale("bb", "dd");
	private static Locale localeWithOutBundle = new Locale("ee", "ff");

	@Test
	public void testGetLocalizedDescriptionShouldReturnLocalizedDescription() {

		assertEquals("The bundle should ignore the country if a bundle with only the language exists", ResourceBundle.getBundle(Constants.FEATURECODE_BUNDLE_KEY, localeaa).getString(FeatureCode.P_PPL.toString()), FeatureCode.P_PPL.getLocalizedDescription(localeaacc));

		assertEquals("The bundle should take the country and language if a bundle with both exists", ResourceBundle.getBundle(Constants.FEATURECODE_BUNDLE_KEY, localebbdd).getString(FeatureCode.P_PPL.toString()), FeatureCode.P_PPL.getLocalizedDescription(localebbdd));

		assertEquals("The bundle should be able to only take the language into account", ResourceBundle.getBundle(Constants.FEATURECODE_BUNDLE_KEY, localeaa).getString("P_PPL"), FeatureCode.P_PPL.getLocalizedDescription(localeaa));

		// null
		Locale savedcontext = LocaleContextHolder.getLocale();
		LocaleContextHolder.setLocale(localeaa);// force an existing
		// translation bundle
		assertEquals("if the locale is null, the LocaleContextHolder one should be used",
				FeatureCode.P_PPL.getLocalizedDescription(LocaleContextHolder.getLocale()),
				FeatureCode.P_PPL.getLocalizedDescription(null));
		LocaleContextHolder.setLocale(savedcontext);

		// no bundle for the specified Locale
		LocaleContextHolder.setLocale(localeaa);// force an existing
		// translation bundle
		assertEquals("If no bundle for the specified locale exists the thread one should be used",
				FeatureCode.P_PPL.getLocalizedDescription(LocaleContextHolder.getLocale()),
				FeatureCode.P_PPL.getLocalizedDescription(localeWithOutBundle));
		LocaleContextHolder.setLocale(savedcontext);

		// no bundle exists for the thread locale

		LocaleContextHolder.setLocale(localeWithOutBundle);
		assertEquals("If no bundle for the specified locale exists and the default"
				+" thread one does not exists, the locale.getDefault should be used : " + Locale.getDefault(),
				FeatureCode.P_PPL.getLocalizedDescription(Locale.getDefault()),
				FeatureCode.P_PPL.getLocalizedDescription(localeWithOutBundle));
		// restore
		LocaleContextHolder.setLocale(savedcontext);

		// no bundle exists for the thread locale and default one,
		Locale savedDefault = Locale.getDefault();
		LocaleContextHolder.setLocale(localeWithOutBundle);
		Locale.setDefault(localeWithOutBundle);
		assertEquals("If no bundle for the specified locale exists and no bundle for the default "
				+"thread one exists and no bundle for the default one exists,"
				+" the DEFAULT_FALLBACK_LOCALE should be used : " + FeatureCode.DEFAULT_FALLBACK_LOCALE,
				FeatureCode.P_PPL.getLocalizedDescription(localeWithOutBundle), FeatureCode.P_PPL.getLocalizedDescription(null));
		// restore
		Locale.setDefault(savedDefault);
		LocaleContextHolder.setLocale(savedcontext);

		// existing locale and non existing translation
		assertEquals("if no translation for the locale is found,"
				+" default translation should be used : " + FeatureCode.DEFAULT_TRANSLATION,
				FeatureCode.DEFAULT_TRANSLATION, FeatureCode.UNKNOW.getLocalizedDescription(localeaa));//

	}

	@Test
	public void testAllPlaceTypeShouldbeMappedWithHibernate() {
		Long count = 1L;
		for (FeatureCode featureCode : FeatureCode.values()) {
			Object feature = featureCode.getObject();
			if (feature instanceof Country) {
				Country typedFeature = (Country) feature;
				typedFeature.setIso3166Alpha2Code(RandomStringUtils.random(2));
				typedFeature.setIso3166Alpha3Code(RandomStringUtils.random(3));
				typedFeature.setIso3166NumericCode(count.intValue());
				typedFeature.setFeatureId(count++);
				typedFeature.setLocation(GeolocHelper.createPoint(1.5F, 3.2F));
				typedFeature.setName("name" + count);
				typedFeature.setSource(GISSource.PERSONAL);
				gisFeatureDao.save(typedFeature);
			} else {
				GisFeature typedFeature = (GisFeature) feature;
				typedFeature.setFeatureId(count++);
				typedFeature.setLocation(GeolocHelper.createPoint(1.5F, 3.2F));
				typedFeature.setName("name" + count);
				typedFeature.setSource(GISSource.PERSONAL);
				gisFeatureDao.save(typedFeature);
			}

		}
	}

}
