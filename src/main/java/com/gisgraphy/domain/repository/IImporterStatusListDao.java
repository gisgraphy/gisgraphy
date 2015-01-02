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

import com.gisgraphy.domain.valueobject.ImporterStatusDto;

public interface IImporterStatusListDao {

    /**
     * @return the file path where data will be saved
     */
    String getSavedFilePath();

    /**
     * @param importerStatusDtoList
     *                the list to save or update
     * @return the saved list
     */
    List<ImporterStatusDto> saveOrUpdate(
	    List<ImporterStatusDto> importerStatusDtoList);

    /**
     * @return the list of {@link ImporterStatusDto}
     */
    List<ImporterStatusDto> get();

    /**
     * Delete the saved list of {@link ImporterStatusDto}
     * 
     * @return true if the deletion is successful or nothing was saved (no list
     *         are still saved)
     */
    boolean delete();

}