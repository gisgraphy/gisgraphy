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

import com.gisgraphy.domain.geoloc.entity.Language;

/**
 * Interface of data access object for {@link Language}
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface ILanguageDao extends IDao<Language, Long> {
    /**
     * Find by ISO 639 Alpha 2 (2-letter) code
     * 
     * @param iso639Alpha2Code
     *                The ISO 639 Alpha 2 (2-letter) code
     * @return The language or null if not found
     */
    public Language getByIso639Alpha2Code(String iso639Alpha2Code);

    /**
     * Find by ISO 639 Alpha 3 (3-letter) code
     * 
     * @param iso639Alpha3Code
     *                ISO 639 Alpha 3 (3-letter) code
     * @return The Language or null if not found
     */
    public Language getByIso639Alpha3Code(String iso639Alpha3Code);

    /**
     * @param iso639LanguageCode
     *                The alpha-2 or 3 language code
     * @return the language according the length of the specified
     *         iso639LanguageCode
     * @throws an
     *                 illegalArgumentException if the code is null or the
     *                 length is not 2 or three
     */
    public Language getByIso639Code(String iso639LanguageCode);

}
