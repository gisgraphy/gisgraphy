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
package com.gisgraphy.fulltext.spell;

import java.util.Map;

/**
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public interface ISpellCheckerIndexer {

    /**
     * Re-index all the {@linkplain SpellCheckerDictionaryNames}
     * 
     * @throws a
     *                 {@link SpellCheckerException} if the spellChecker is not
     *                 alive or if an error occured
     * @return a map with dictioanry name as key and boolean as value. the
     *         boolean is equal to true if the index has succeed for the
     *         dictionary
     */
    public Map<String, Boolean> buildAllIndex();

    /**
     * re-index the dictionary for the specified spellchecker dictionary name
     * 
     * @param spellCheckerDictionaryName
     *                the spellChecker Dictionary to index / re-index
     * @throws a
     *                 {@link SpellCheckerException} if the spellChecker is not
     *                 alive or if an error occured
     */
    public boolean buildIndex(
	    SpellCheckerDictionaryNames spellCheckerDictionaryName);

}