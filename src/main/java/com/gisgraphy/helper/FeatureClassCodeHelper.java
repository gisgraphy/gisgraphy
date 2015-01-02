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
package com.gisgraphy.helper;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;

/**
 * Provides some useful methods
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public enum FeatureClassCodeHelper {
    /**
     * The city feature Code
     */
    P_PPL, P_PPLA, P_PPLA2, P_PPLA3, P_PPLA4,P_PPLA5, P_PPLC, P_PPLG, P_PPLL, P_PPLR, P_PPLS, P_STLMT, P_PPLQ, P_PPLW;

    /**
     * Whether the feature code and the feature class are for {@link City}
     * objects. This method is case sensitive : if you provide lower case
     * feature Class or feature code it will return false. <br>
     * If you provide null feature class or feature code it will return false.
     * 
     * @param featureClass
     *                The feature class of the gisFeature
     * @param featureCode
     *                The feature code of the gisFeature
     * @return true if the featureClass="P" and the featurecode belongs to the
     *         {@linkplain FeatureClassCodeHelper} Enum
     */
    public static boolean isCity(String featureClass, String featureCode) {
	if (featureClass != null && featureCode != null
		&& featureClass.equals("P")) {
	    FeatureClassCodeHelper[] featureCodes = FeatureClassCodeHelper
		    .values();
	    String classCode = "";
	    for (FeatureClassCodeHelper element : featureCodes) {
		classCode = featureClass + "_" + featureCode;
		if (classCode.equals(element.toString())) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Whether the feature code and the feature class are for {@link Country}
     * object. This method is case sensitive : if you provide lower case feature
     * Class or Feature Code it will return false. <br>
     * If you provide null featureClass or FeatureCode it will return false.
     * 
     * @param featureClass
     *                The feature class to test
     * @param featureCode
     *                The feature code to test
     * @return true if the feature class and the feature code are for
     *         {@link Country} Object
     */
    public static boolean isCountry(String featureClass, String featureCode) {
	if (featureCode != null && featureClass != null
		&& featureClass.equals("A")) {
	    return (featureCode.startsWith("PCL"))
		    && (!featureCode.equals("PCLIX"));
	}
	return false;
    }

    /**
     * Whether the feature code and the feature class are for {@link Adm} object
     * It will return true if featureClass equals 'A' and featureCode is
     * ADM1,ADM2,ADM3,ADM4. it is case sensitive and will retrun false for
     * A.ADM.ADMD are not considered as ADM
     * 
     * @param featureClass
     *                The feature class to test
     * @param featureCode
     *                The feature code to test
     * @return true if the feature class and the feature code are for
     *         {@link Adm} Object
     */
    public static boolean is_Adm(String featureClass, String featureCode) {
	// TODO V2 why if this method is named isAdm =>test fails
	if (featureCode != null
		&& featureClass != null
		&& featureClass.equals("A")
		&& featureCode.length() == 4
		&& (featureCode.equals("ADM1") || featureCode.equals("ADM2")
			|| featureCode.equals("ADM3") || featureCode
			.equals("ADM4"))) {
	    return true;
	}
	return false;
    }
}
