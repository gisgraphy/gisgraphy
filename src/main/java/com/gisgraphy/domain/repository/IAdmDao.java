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

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.importer.ImporterConfig;

/**
 * Interface for {@link Adm} data access
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IAdmDao extends IGisDao<Adm> {

    /**
     * @param level
     *                The level of the Adms to retrieve. The Level is not
     *                checked (not necessary beetween 1 and 4)
     * @return all the Adm for the specified level or an Empty List if no Adm
     *         are found
     */
    public List<Adm> getAllbyLevel(int level);

    /**
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @return The Adm with level 1 for the specified countrycode and adm1Code
     *         <u>NOTE</u> : The countryCode will be automaticaly converted in
     *         upperCase
     * @see #getAdm(String, String, String, String, String)
     * @see #getAdmByCountryAndCodeAndLevel(String, String, int)
     * @throws IllegalArgumentException
     *                 if any of the parameters are null
     */
    public Adm getAdm1(final String countryCode, final String adm1Code);

    /**
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @param adm2Code
     *                The Adm2Code of the Adm to retrieve <u>NOTE</u> : The
     *                countryCode will be automaticaly converted in upperCase
     * @return The Adm with level 2 for the specified countrycode, adm1Code, and
     *         adm2Code. If adm1code is equals to 00 it will be ignore and more
     *         than one result could be found, in that case it will return null.
     * @see #getAdm(String, String, String, String, String)
     * @see #getAdmByCountryAndCodeAndLevel(String, String, int)
     * @throws IllegalArgumentException
     *                 if any of the parameters are null
     */
    public Adm getAdm2(final String countryCode, final String adm1Code,
	    final String adm2Code);

    /**
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @param adm2Code
     *                The Adm2Code of the Adm to retrieve
     * @param adm3Code
     *                The Adm3Code of the Adm to retrieve <u>NOTE</u> : The
     *                countryCode will be automaticaly converted in upperCase
     * @return The Adm with level 3 for the specified countrycode, adm1Code,
     *         adm2Code and adm3Code. If adm1code is equals to 00 it will be
     *         ignore and more than one result could be found, in that case it
     *         will return null.
     * @see #getAdm(String, String, String, String, String)
     * @see #getAdmByCountryAndCodeAndLevel(String, String, int)
     * @throws IllegalArgumentException
     *                 if any of the parameters are null
     */
    public Adm getAdm3(final String countryCode, final String adm1Code,
	    final String adm2Code, final String adm3Code);

    /**
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @param adm2Code
     *                The Adm2Code of the Adm to retrieve
     * @param adm3Code
     *                The Adm3Code of the Adm to retrieve
     * @param adm4Code
     *                The Adm4Code of the Adm to retrieve <u>NOTE</u> : The
     *                countryCode will be automaticaly converted in upperCase
     * @return The Adm with level 4 for the specified countrycode, adm1Code,
     *         adm2Code, adm3Code and adm4Code.If adm1code is equals to 00 it
     *         will be ignore and more than one result could be found, in that
     *         case it will return null.
     * @see #getAdm(String, String, String, String, String)
     * @throws IllegalArgumentException
     *                 if any of the parameters are null
     */
    public Adm getAdm4(final String countryCode, final String adm1Code,
	    final String adm2Code, final String adm3Code, final String adm4Code);

    /**
     * Retrieve the Adm of the highest level according to the AdmXcode. The
     * level will be determine with the highest AdmXcode which is not null (e.g :
     * if adm1 and adm2 are not null, and adm3 and adm4 are null then the Adm of
     * Level 2 will be retrieved) This method is a wrapper around
     * {@link #getAdm1(String, String)},
     * {@link #getAdm2(String, String, String)},
     * {@link #getAdm3(String, String, String, String)}, and
     * {@link #getAdm4(String, String, String, String, String)}. Use This
     * Method ONLY if you've got some AdmXcode and you don't know the Level.
     * you'll have better performance with the getAdmX() methods.<br>
     * 
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @param adm2Code
     *                The Adm2Code of the Adm to retrieve
     * @param adm3Code
     *                The Adm3Code of the Adm to retrieve
     * @param adm4Code
     *                The Adm4Code of the Adm to retrieve <u>NOTE</u> : The
     *                countryCode will be automaticaly converted in upperCase
     * @return The Adm with the specified countrycode, adm1Code, adm2Code,
     *         adm3Code and adm4Code
     * @see #getAdm1(String, String)
     * @see #getAdm2(String, String, String)
     * @see #getAdm3(String, String, String, String)
     * @see #getAdm4(String, String, String, String, String)
     * @see #getAdmByCountryAndCodeAndLevel(String, String, int)
     * @see Adm#getProcessedLevelFromCodes(String, String, String, String)
     * @throws IllegalArgumentException
     *                 if the countryCode is null
     */
    public Adm getAdm(final String countryCode, final String adm1Code,
	    final String adm2Code, final String adm3Code, final String adm4Code);

    /**
     * Returns The Adm with the specified code and the specified level for the
     * specified country code. The level determine the admXcode to search for.
     * (e.g : if level=3 and admCode="C3", the adm with level 3 and adm3Code=c3"
     * will be retrieved from the datastore <u>NOTE</u> : The countryCode will
     * be automaticaly converted in upperCase
     * 
     * @param countryCode
     *                The countryCode that the Adm must belongs to
     * @param admCode
     *                The code of the Adm for the specified level
     * @param level
     *                The level of the Adm : The Level is not checked (not
     *                necessary beetween 1 and 4)
     * @return The list of Adm with the specified code and the specified level
     *         for the specified country code, never return null but an empty
     *         list
     * @throws IllegalArgumentException
     *                 if countryCode or AdmCode is null
     */
    public List<Adm> getAdmByCountryAndCodeAndLevel(final String countryCode,
	    final String admCode, final int level);

    /**
     * Return The Adm for the specified Code in the same way of
     * {@link #getAdm(String, String, String, String, String)} or the first
     * valid parent if no Adm is found with the specified codes.<br>
     * e.g : If no Adm is found with adm1code="AA", adm2Code="BB", and
     * Adm3Code="CC" but if it exist an Adm with level 2 with the specified
     * Adm1Code or Adm2Code : the adm with level 2 will be return. If no adm2 is
     * found and there is an existing Adm with level 1 and adm1code="AA" : the
     * adm with level 1 will be return this method is to used when you want to
     * do error correcting (see also
     * {@link #suggestMostAccurateAdm(String, String, String, String, String, GisFeature)}
     * 
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @param adm2Code
     *                The Adm2Code of the Adm to retrieve
     * @param adm3Code
     *                The Adm3Code of the Adm to retrieve
     * @param adm4Code
     *                The Adm4Code of the Adm to retrieve
     * @return The Adm for the specified Code in the same way of
     *         {@link #getAdm(String, String, String, String, String)} or the
     *         first valid parent if no Adm is found with the specified codes
     * @see #getAdmByCountryAndCodeAndLevel(String, String, int)
     * @see Adm#getProcessedLevelFromCodes(String, String, String, String)
     * @throws IllegalArgumentException
     *                 if the countryCode is null
     */
    public Adm getAdmOrFirstValidParentIfNotFound(final String countryCode,
	    final String adm1Code, final String adm2Code,
	    final String adm3Code, final String adm4Code);

    /**
     * This method is used when
     * {@link ImporterConfig#isTryToDetectAdmIfNotFound()} is true or when error
     * correction is needed. the algorithm will return an Adm according the
     * specified rules:
     * <ul>
     * <li>If an Adm with the specified code is found (see
     * {@link #getAdm(String, String, String, String, String)}) : retrun it</li>
     * <li>If an Adm with the highest not null level is found for the specified
     * country (e.g : if adm1,2,3 are specified and adm4 is null and it exist an
     * adm with level 3 for the specified adm3Code then it will be return)</li>
     * <ul>
     * <li>If no parent Adm is found (see
     * {@link #getAdmOrFirstValidParentIfNotFound(String, String, String, String, String)} :
     * return Adm with the highest not null level </li>
     * <li>If a parent Adm is found (see
     * {@link #getAdmOrFirstValidParentIfNotFound(String, String, String, String, String)} :
     * <ul>
     * <li>If the difference beetween the Adm and The parent Adm is <=2 : we
     * assume that it is an error with only one code and return the Adm with the
     * highest not null level</li>
     * <li>If the difference is >1 we assume that there is too much error and
     * return the nearest parent </li>
     * </ul>
     * </li>
     * </ul>
     * <li>If No Adm with the highest not null level is found for the specified
     * country </li>
     * <ul>
     * <li>If a parent Adm is found (see
     * {@link #getAdmOrFirstValidParentIfNotFound(String, String, String, String, String)} :
     * return Adm with the highest not null level : return the Parent</li>
     * <li>If no parent is found : return null</li>
     * </ul>
     * </ul>
     * 
     * @param countryCode
     *                The country code of the Adm to retrieve
     * @param adm1Code
     *                The Adm1Code of the Adm to retrieve
     * @param adm2Code
     *                The Adm2Code of the Adm to retrieve
     * @param adm3Code
     *                The Adm3Code of the Adm to retrieve
     * @param adm4Code
     *                The Adm4Code of the Adm to retrieve
     * @param gisFeature
     *                The gisFeature is not really used in the algorithm, but it
     *                can be useful to have it for logs or for specific
     *                algorithm implementation.(It is only used for logs)
     * @return The most accurate Adm for the gisFeature
     * @see #getAdmOrFirstValidParentIfNotFound(String, String, String, String,
     *      String)
     * @throws IllegalArgumentException
     *                 if the countryCode is null
     */
    public Adm suggestMostAccurateAdm(final String countryCode,
	    final String adm1Code, final String adm2Code,
	    final String adm3Code, final String adm4Code,
	    final GisFeature gisFeature);

    /**
     * @param level
     *                The level we want the Adm to count The Level is not
     *                checked (not necessary beetween 1 and 4)
     * @return how many Adm exists for the specified level
     */
    public long countByLevel(final int level);

    /**
     * @return Adm Which are not used by any GisFeature
     */
    public List<Adm> getUnused();

    /**
     * @param level
     *                the level we want to delete Adm return the number of
     *                deleted Adm
     */
    public int deleteAllByLevel(final int level);
    
    /**
     * List all the featureId of the Adms of a specified level 
     * @param level the level
     * @return a list of all featureId
     */
    public List<Long> listFeatureIdByLevel(final int level);

}
