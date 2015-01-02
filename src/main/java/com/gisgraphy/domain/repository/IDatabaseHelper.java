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

import java.io.File;
import java.util.List;

/**
 * Interface that describe useful function to manage the database
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public interface IDatabaseHelper {

    /**
     * Drop the Normalize_text function in postgres
     */
    public void dropNormalize_textFunction();
    
    /**
     * Determines if the normalize_text function is created in postgres
     * 
     * @return true if the call to that function does not throw exception and
     *         return a the correct result
     */
    public boolean isNormalize_textFunctionCreated();

    /**
     * create a postgres function in postgres to remove accent, lower cased, trim, and
     * remove unwanted char (, . ; ")
     */
    public void createNormalize_textFunction();

    /**
     * @param file
     *            the file to execute, it will be read as an UTF-8 file
     * @param continueOnError
     *            if an error occured, the process will go on if this value is
     *            true, if not it will throw an exception
     * @throws Exception
     *             in case of error during execution, or if the file is null or
     *             does not exist
     * @return A list of String with errorMessage
     */
    public List<String> execute(final File file, boolean continueOnError) throws Exception;

    /**
     * Generate the sql file to create all the Gisgraphy tables
     * 
     * @param outputFile
     *            The File that we want to write the SQL
     */
    public void generateSqlCreationSchemaFile(File outputFile);

    /**
     * Generate the sql file to drop all the Gisgraphy tables
     * 
     * @param outputFile
     *            The File that we want to write the SQL
     */
    public void generateSQLDropSchemaFile(File outputFile);

    /**
     * Generate the SQL file to create all the Gisgraphy tables that have to be
     * reset to rerun the import, the user and role tables won't be deleted
     * 
     * @param outputFile
     *            The File that we want to write the SQL
     */
    public void generateSQLCreationSchemaFileToRerunImport(File outputFile);

    /**
     * Generate the SQL file to drop all the Gisgraphy tables that have to be
     * reset to rerun the import, the user and role tables won't be deleted
     * 
     * @param outputFile
     *            The File that we want to write the SQL
     */
    public void generateSqlDropSchemaFileToRerunImport(File outputFile);

}
