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

import com.gisgraphy.domain.valueobject.ImporterStatus;
import com.gisgraphy.domain.valueobject.NameValueDTO;

/**
 * Interface for Geonames processor
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IImporterProcessor {

    /**
     * Do the stuff...
     */
    public void process();

    /**
     * The number of read line for the current processed file
     * 
     * @see #getTotalReadLine()
     */
    public long getReadFileLine();

    /**
     * The number of read line for all the processed file
     * 
     * @see #getReadFileLine()
     */
    public long getTotalReadLine();

    /**
     * @return The name of the file currently processed or null if no file is
     *         processed
     */
    public String getCurrentFileName();

    /**
     * @return The number of line the processor will process. (it is not the
     *         number of lines left!)
     */
    public long getNumberOfLinesToProcess();

    /**
     * @return The current status of the importer
     */
    public ImporterStatus getStatus();

    /**
     * @return A text Message for the importer
     */
    public String getStatusMessage();

    /**
     * /!\ USE THIS METHOD VERY CAREFULLY /!\ : If you call this function, all
     * the imported data for the specified importer will be deleted
     * 
     * @return a {@linkplain NameValueDTO} with the name of the deleted object
     *         and the number of deleted Object. No entry will be return for
     *         Object that were 0 object will be deleted except if an error occurred during the deletion.
     */
    public List<NameValueDTO<Integer>> rollback();

    /**
     * @return true if the processor should Not be executed
     */
    public boolean shouldBeSkipped();

    /**
     * Reset status fields, it should be done when the import has been canceled
     */
    public void resetStatus();

}
