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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import net.sf.jstester.util.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.fulltext.AbstractIntegrationHttpSolrTestCase;
import com.gisgraphy.helper.IntrospectionHelper;
import com.gisgraphy.helper.URLUtils;
import com.gisgraphy.test.FeedChecker;
import com.gisgraphy.test.GisgraphyTestHelper;

public class GisFeatureDistanceFactoryTest extends AbstractIntegrationHttpSolrTestCase {

    @Autowired
    protected IGisDao<? extends GisFeature>[] daos;

    
    @Test
    public void testGisFeatureDistanceShouldBeMappedWithJAXBAndHaveCalculatedFieldsWhenConstructWithGisFeatureAndDistance() {
	GisFeatureDistance gisfeatureDistance = null;
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(GisFeatureDistance.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    gisfeatureDistance = GisgraphyTestHelper
		    .createFullFilledGisFeatureDistanceWithFactory();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(gisfeatureDistance, outputStream);
	    FeedChecker.checkGisFeatureDistanceJAXBMapping(gisfeatureDistance, outputStream.toString(Constants.CHARSET),"");
	    checkupdateFields(gisfeatureDistance);
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    fail(e.getMessage());
	}
    }
    
    private void checkupdateFields(GisFeatureDistance gisfeatureDistance){
	 String gMapsURL =  URLUtils.createGoogleMapUrl(gisfeatureDistance.getLocation());
	   String yahooMapsUrl = URLUtils.createYahooMapUrl(gisfeatureDistance.getLocation());
	   String openstreetmapMapsUrl = URLUtils.createOpenstreetmapMapUrl(gisfeatureDistance.getLocation());
	   String countryflagurl = URLUtils.createCountryFlagUrl(gisfeatureDistance.getCountryCode());
	   double lat = gisfeatureDistance.getLocation().getY();
	   double lng = gisfeatureDistance.getLocation().getX();
	   String placetype="city";
	   Assert.assertEquals(gMapsURL, gisfeatureDistance.getGoogle_map_url());
	   Assert.assertEquals(yahooMapsUrl, gisfeatureDistance.getYahoo_map_url());
	   Assert.assertEquals(openstreetmapMapsUrl, gisfeatureDistance.getOpensteetmap_map_url());
	   Assert.assertEquals(countryflagurl, gisfeatureDistance.getCountry_flag_url());
	   Assert.assertEquals(lat, gisfeatureDistance.getLat());
	   Assert.assertEquals(lng, gisfeatureDistance.getLng());
	   Assert.assertEquals(placetype, gisfeatureDistance.getPlaceType());
    }
   
    @Test
    public void testGisFeatureDistanceShouldHaveCountryInfos() {
	GisFeatureDistance gisFeatureDistance = null;
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(GisFeatureDistance.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    GisFeatureDistanceFactory factory = new GisFeatureDistanceFactory();
	    gisFeatureDistance = factory.fromCountry(GisgraphyTestHelper.createFullFilledCountry(), 3D);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(gisFeatureDistance, outputStream);
	    FeedChecker.checkGisFeatureDistanceJAXBMapping(gisFeatureDistance, outputStream.toString(Constants.CHARSET),"");
	    String streamToString = outputStream.toString(Constants.CHARSET);
	    FeedChecker.assertQ("area should be filled if The GisFeature is a Country",
		    streamToString, "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/area[.='" + gisFeatureDistance.getArea() + "']",
			    "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/tld[.='" + gisFeatureDistance.getTld() + "']",
			    "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/capitalName[.='" + gisFeatureDistance.getCapitalName() + "']",
			    "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/continent[.='" + gisFeatureDistance.getContinent() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/postalCodeRegex[.='" + gisFeatureDistance.getPostalCodeRegex() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/currencyCode[.='" + gisFeatureDistance.getCurrencyCode() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/currencyName[.='" + gisFeatureDistance.getCurrencyName() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/equivalentFipsCode[.='" + gisFeatureDistance.getEquivalentFipsCode() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/fipsCode[.='" + gisFeatureDistance.getFipsCode() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/iso3166Alpha2Code[.='" + gisFeatureDistance.getIso3166Alpha2Code() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/iso3166Alpha3Code[.='" + gisFeatureDistance.getIso3166Alpha3Code() + "']",
			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/iso3166NumericCode[.='" + gisFeatureDistance.getIso3166NumericCode() + "']",

			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/phonePrefix[.='" + gisFeatureDistance.getPhonePrefix() + "']",

			    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/postalCodeMask[.='" + gisFeatureDistance.getPostalCodeMask() + "']");
	    checkupdateFields(gisFeatureDistance);
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    fail("unsupported encoding for " + Constants.CHARSET);
	}
    }
    

    @Test
    public void testGisFeatureDistanceShouldHaveLevelIfGisFeatureIsAdm() {
	GisFeatureDistance gisFeatureDistance = null;
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(GisFeatureDistance.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    gisFeatureDistance = GisgraphyTestHelper
		    .createFullFilledGisFeatureDistanceForAdmWithFactory();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(gisFeatureDistance, outputStream);
	    FeedChecker.checkGisFeatureDistanceJAXBMapping(gisFeatureDistance, outputStream.toString(Constants.CHARSET),"");
	    FeedChecker.assertQ("level should be output if The GisFeature is an Adm",
		    outputStream.toString(Constants.CHARSET), "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/level[.='" + gisFeatureDistance.getLevel() + "']");
	    checkupdateFields(gisFeatureDistance);
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    fail("unsupported encoding for " + Constants.CHARSET);
	}
    }
    
    @Test
    public void testGisFeatureDistanceShouldBeCorrectlyFullFilledGisFeatureIsStreet() {
	GisFeatureDistance gisFeatureDistance = null;
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(GisFeatureDistance.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    gisFeatureDistance = GisgraphyTestHelper
		    .createFullFilledGisFeatureDistanceForStreetWithFactory();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(gisFeatureDistance, outputStream);
	    FeedChecker.checkGisFeatureDistanceJAXBMapping(gisFeatureDistance, outputStream.toString(Constants.CHARSET),"");
	    FeedChecker.assertQ("Zipcode should be output if The GisFeature is a city",
		    outputStream.toString(Constants.CHARSET), "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/length[.='" + gisFeatureDistance.getLength() + "']",
			    "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/streetType[.='" + gisFeatureDistance.getStreetType()+ "']", "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/oneWay[.='" + gisFeatureDistance.isOneWay() + "']", 
			    "/" + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
	    + "/openstreetmapId[.='" + gisFeatureDistance.getOpenstreetmapId() + "']",
	    "/"+ GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
	    + "/isIn[.='" + gisFeatureDistance.getIsIn() + "']");
	    checkupdateFields(gisFeatureDistance);
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    fail("unsupported encoding for " + Constants.CHARSET);
	}
    }
    


    @Test
    public void testGisFeatureDistanceShouldHaveZipCode() {
	GisFeatureDistance result = null;
	try {
	    JAXBContext context = JAXBContext
		    .newInstance(GisFeatureDistance.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    result = GisgraphyTestHelper
		    .createFullFilledGisFeatureDistanceForCityWithFactory();
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    m.marshal(result, outputStream);
	    String resultAsString = outputStream.toString(Constants.CHARSET);
	    FeedChecker.checkGisFeatureDistanceJAXBMapping(result, resultAsString,"");
	    for (String zipCode: result.getZipCodes()){
	    FeedChecker.assertQ("Zipcode should be output ",
		    resultAsString, "/"
			    + GisFeatureDistance.GISFEATUREDISTANCE_JAXB_NAME
			    + "/zipCodes/zipCode[.='" + zipCode + "']");
	    }
	} catch (PropertyException e) {
	    fail(e.getMessage());
	} catch (JAXBException e) {
	    fail(e.getMessage());
	} catch (UnsupportedEncodingException e) {
	    fail("unsupported encoding for " + Constants.CHARSET);
	}
    }
    

    @Test
    public void testGisFeatureDistanceShouldHaveAllThefieldOfClassThatExtendsGisFeature() {
	List<Class<? extends GisFeature>> classList = new ArrayList<Class<? extends GisFeature>>();
	for (int i = 0; i < daos.length; i++) {
	    classList.add(daos[i].getPersistenceClass());
	}
	List<String> GisFeatureDistanceFields = inspectGisFeatureDistance();
	for (Class<? extends GisFeature> gisClass : classList) {
	    for (String field : IntrospectionHelper.getFieldsAsList(gisClass)){
		assertTrue("GisFeatureDistance does not contain "+field+ " and should  because "+gisClass+" has this field",GisFeatureDistanceFields.contains(field));
	    }
	}
    }

    private List<String> inspectGisFeatureDistance(){
	Class<?> clazzParent = GisFeatureDistance.class;
	List<String> introspectedFields = new ArrayList<String>();
	    do {
		int searchMods = 0x0;
		searchMods |= modifierFromString("private");

		Field[] flds = clazzParent.getDeclaredFields();
		for (Field f : flds) {
		    int foundMods = f.getModifiers();
		    if ((foundMods & searchMods) == searchMods
			    && !f.isSynthetic() && f.getType() != List.class
			    && !Modifier.isFinal(foundMods)) {
			introspectedFields.add(f.getName());
		    }
		}
		clazzParent = (Class<?>) clazzParent.getSuperclass();
	    } while (clazzParent != Object.class);
	return introspectedFields;
    }
    
    private static int modifierFromString(String s) {
	int m = 0x0;
	if ("public".equals(s))
	    m |= Modifier.PUBLIC;
	else if ("protected".equals(s))
	    m |= Modifier.PROTECTED;
	else if ("private".equals(s))
	    m |= Modifier.PRIVATE;
	else if ("static".equals(s))
	    m |= Modifier.STATIC;
	else if ("final".equals(s))
	    m |= Modifier.FINAL;
	else if ("transient".equals(s))
	    m |= Modifier.TRANSIENT;
	else if ("volatile".equals(s))
	    m |= Modifier.VOLATILE;
	return m;
    }
    
   
    
}
