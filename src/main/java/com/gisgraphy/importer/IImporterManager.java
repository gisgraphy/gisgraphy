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
package com.gisgraphy.importer;

import java.util.List;

import com.gisgraphy.domain.valueobject.ImporterStatusDto;

public interface IImporterManager {

    /**
     * synchronized method to Import all the GisFeatures according the
     * {@link ImporterConfig} and the several importers define in the
     * applicationContext-geoloc file.
     */
    public void importAll();

    /**
     * @return the importerConfig
     */
    public ImporterConfig getImporterConfig();

    /**
     * @return the importers
     */
    public List<IImporterProcessor> getImporters();

    /**
     * @return Wether an import is in Progress
     */
    public boolean isInProgress();

    /**
     * @return Wether the import has already been done (error or successful)
     * @throws ImporterMetaDataException 
     */
    public boolean isAlreadyDone() throws ImporterMetaDataException;

    /**
     * @return the time the last import took. If the import is in progress,
     *         returns the time it took from the beginning. If the import has
     *         not been started yet return 0.
     */
    public long getTimeElapsed();

    /**
     * @return The human readable elapsed time .
     * 
     */
    public String getFormatedTimeElapsed();

    /**
     * /!\ USE THIS METHOD VERY CAREFULLY /!\ : If you call this function, all
     * the imported data will be deleted clear all the tables with GisFeature
     * (and subclass, adm, languages, country,...), delete alternatenames,
     * delete all the fulltext search engine entries, in order to re-run a new
     * import from scratch
     * 
     * @return a list with the SQL Errors and warnings. note
     *         that you can have warning but the reset can be successful
     * 
     * @throws Exception
     */
    public List<String> resetImport() throws Exception;

    /**
     * @return a list of DTO for each importers
     */
    public List<ImporterStatusDto> getStatusDtoList();

}