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
package com.gisgraphy.domain;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.helper.FeatureClassCodeHelper;

public class FeatureClassCodeHelperTest  {

    @Test
    public void isCityWithNullValuesShouldNotThrow() {
	Assert.assertFalse(FeatureClassCodeHelper.isCity(null, null));
    }

    @Test
    public void isCityWithCorrectFeatureClassAndWrongFeatureCodeShouldReturnFalse() {
	Assert.assertFalse(FeatureClassCodeHelper.isCity("P", "ERR"));
    }

    @Test
    public void isCityWithIncorrectFeatureClassAndCorrectFeatureCodeShouldReturnFalse() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry("F", "PPL"));
    }

    @Test
    public void isCityIsCaseSensitiveForFeatureClass() {
	Assert.assertFalse(FeatureClassCodeHelper.isCity("p", "PPL"));
    }

    @Test
    public void isCityIsCaseSensitiveForFeatureCode() {
	Assert.assertFalse(FeatureClassCodeHelper.isCity("P", "ppl"));
    }

    @Test
    public void isCityShouldReturnTrueForCityFeatureClassCode() {
	Assert.assertTrue(FeatureClassCodeHelper.isCity("P", "PPL"));
    }

    @Test
    public void isCityShouldReturnFalseForNonCityFeatureClassCode() {
	Assert.assertFalse(FeatureClassCodeHelper.isCity("P", "PPLE"));
    }

    // iscountry
    @Test
    public void isCoutnryWithNullValuesShouldNotThrow() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry(null, null));
    }

    @Test
    public void isCountryWithCorrectFeatureClassAndWrongFeatureCodeShouldReturnFalse() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry("A", "ERR"));
    }

    @Test
    public void isCountryWithIncorrectFeatureClassAndCorrectFeatureCodeShouldReturnFalse() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry("ERR", "PCL"));
    }

    @Test
    public void isCountryIsCaseSensitiveForFeatureClass() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry("a", "PCL"));
    }

    @Test
    public void isCountryIsCaseSensitiveForFeatureCode() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry("A", "pcl"));
    }

    @Test
    public void isCountryShouldReturnTrueForCountryFeatureClassCode() {
	Assert.assertTrue(FeatureClassCodeHelper.isCountry("A", "PCL"));
	Assert.assertTrue(FeatureClassCodeHelper.isCountry("A", "PCLD"));
	Assert.assertTrue(FeatureClassCodeHelper.isCountry("A", "PCLF"));
	Assert.assertTrue(FeatureClassCodeHelper.isCountry("A", "PCLI"));
	Assert.assertTrue(FeatureClassCodeHelper.isCountry("A", "PCLS"));
    }

    @Test
    public void isCountryShouldReturnFalseForNonCountryFeatureClassCode() {
	Assert.assertFalse(FeatureClassCodeHelper.isCountry("P", "PCLIX"));
    }

    // isAdm

    @Test
    public void isAdmWithNullValuesShouldNotThrow() {
	Assert.assertFalse(FeatureClassCodeHelper.is_Adm(null, null));
    }

    @Test
    public void isAdmWithCorrectFeatureClassAndWrongFeatureCodeShouldReturnFalse() {
	Assert.assertFalse(FeatureClassCodeHelper.is_Adm("A", "ERR"));
    }

    @Test
    public void isAdmWithIncorrectFeatureClassAndCorrectFeatureCodeShouldReturnFalse() {
	Assert.assertFalse(FeatureClassCodeHelper.is_Adm("ERR", "ADM1"));
    }

    @Test
    public void isAdmIsCaseSensitiveForFeatureClass() {
	Assert.assertFalse(FeatureClassCodeHelper.is_Adm("a", "ADM1"));

    }

    @Test
    public void isAdmIsCaseSensitiveForFeatureCode() {
	Assert.assertFalse(FeatureClassCodeHelper.is_Adm("A", "adm1"));
    }

    @Test
    public void isAdmShouldReturnTrueForCountryFeatureClassCode() {
	Assert.assertTrue(FeatureClassCodeHelper.is_Adm("A", "ADM1"));
	Assert.assertTrue(FeatureClassCodeHelper.is_Adm("A", "ADM2"));
	Assert.assertTrue(FeatureClassCodeHelper.is_Adm("A", "ADM3"));
	Assert.assertTrue(FeatureClassCodeHelper.is_Adm("A", "ADM4"));
    }

    @Test
    public void isAdmShouldReturnFalseForNonCityFeatureClassCode() {
	Assert.assertFalse(FeatureClassCodeHelper.is_Adm("A", "ADM"));
    }

}
