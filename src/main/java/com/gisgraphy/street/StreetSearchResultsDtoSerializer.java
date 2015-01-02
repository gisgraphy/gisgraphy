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
package com.gisgraphy.street;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gisgraphy.domain.valueobject.Constants;
import com.gisgraphy.domain.valueobject.GisgraphyServiceType;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.domain.valueobject.StreetDistance;
import com.gisgraphy.domain.valueobject.StreetSearchResultsDto;
import com.gisgraphy.geoloc.GeolocResultsDto;
import com.gisgraphy.helper.OutputFormatHelper;
import com.gisgraphy.serializer.UniversalSerializer;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.serializer.exception.UnsupportedFormatException;
import com.gisgraphy.service.ServiceException;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.gml.GMLModuleImpl;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.impl.OpenSearchModuleImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * serialize @link {@link GeolocResultsDto} into several formats
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class StreetSearchResultsDtoSerializer implements
	IStreetSearchResultsDtoSerializer {
    
    public final static String START_PAGINATION_INDEX_EXTRA_PARAMETER = "startPaginationIndex";
    
    /**
     * The logger
     */
    protected static final Logger logger = LoggerFactory
	    .getLogger(StreetSearchResultsDtoSerializer.class);

    

   

    public void serialize(OutputStream outputStream, OutputFormat outputFormat,
	    StreetSearchResultsDto streetSearchResultsDto, boolean indent,Map<String,Object> extraParameters) {
	if (!OutputFormatHelper.isFormatSupported(outputFormat,GisgraphyServiceType.STREET)) {
	    throw new UnsupportedFormatException(outputFormat
		    + " is not applicable for street search");
	} 
	   
	if (outputFormat == OutputFormat.JSON || outputFormat == OutputFormat.PHP || outputFormat == OutputFormat.PYTHON  || outputFormat == OutputFormat.RUBY || outputFormat == OutputFormat.XML || outputFormat == OutputFormat.YAML) {
		serializeWithUniveraslSerializer(outputStream, streetSearchResultsDto,  indent, outputFormat,extraParameters);
	}else 	if (outputFormat==OutputFormat.ATOM){
	    int  startPaginationIndex = getStartPaginationIndex(extraParameters);
	    serializeToFeed(outputStream,streetSearchResultsDto,OutputFormat.ATOM_VERSION, startPaginationIndex);
	}
	else if (outputFormat==OutputFormat.GEORSS) {
	    int  startPaginationIndex = getStartPaginationIndex(extraParameters);
	    serializeToFeed(outputStream,streetSearchResultsDto,OutputFormat.RSS_VERSION, startPaginationIndex);
	}
	else {
	    //default
		serializeWithUniveraslSerializer(outputStream, streetSearchResultsDto,  indent, OutputFormat.XML,extraParameters);
	}
    }
    
    private int getStartPaginationIndex(Map<String, Object> extraParameters) {
	if (extraParameters!= null){
	Object startPaginationIndexObject = extraParameters.get(START_PAGINATION_INDEX_EXTRA_PARAMETER);
	if (startPaginationIndexObject != null && startPaginationIndexObject instanceof Integer){
	    return (Integer) startPaginationIndexObject;
	}
	}
	return 1;
    }    
    private void serializeWithUniveraslSerializer(OutputStream outputStream, StreetSearchResultsDto streetSearchResultsDto,boolean indent, OutputFormat format,Map<String,Object> extraParameters) {
   	 try {
   	     UniversalSerializer.getInstance().write(outputStream, streetSearchResultsDto,  indent,extraParameters, format);
   	    } catch (Exception e) {
   		throw new ServiceException(e);
   	    }
   	
       }
    
   

    @SuppressWarnings("unchecked")
    private void serializeToFeed(OutputStream outputStream,
	    StreetSearchResultsDto streetSearchResultsDto,String feedVersion, int startPaginationIndex) {
	SyndFeed synFeed = new SyndFeedImpl();
	Writer oWriter = null;
	try {

	    synFeed.setFeedType(feedVersion);
	    

	    synFeed.setTitle(Constants.FEED_TITLE);
	    synFeed.setLink(Constants.FEED_LINK);
	    synFeed.setDescription(Constants.FEED_DESCRIPTION);
	    List<SyndEntry> entries = new ArrayList<SyndEntry>();

	    for (StreetDistance gisFeatureDistance : streetSearchResultsDto
		    .getResult()) {

		SyndEntry entry = new SyndEntryImpl();
		GeoRSSModule geoRSSModuleGML = new GMLModuleImpl();
		OpenSearchModule openSearchModule = new OpenSearchModuleImpl();

		geoRSSModuleGML.setLatitude(gisFeatureDistance.getLat());
		geoRSSModuleGML.setLongitude(gisFeatureDistance.getLng());

		openSearchModule
			.setItemsPerPage(Pagination.DEFAULT_MAX_RESULTS);
		openSearchModule
			.setTotalResults(streetSearchResultsDto.getNumFound());
		openSearchModule.setStartIndex(startPaginationIndex);

		entry.getModules().add(openSearchModule);
		entry.getModules().add(geoRSSModuleGML);
		entry.setTitle(gisFeatureDistance.getName());
		entry.setAuthor(com.gisgraphy.domain.Constants.MAIL_ADDRESS);
		entry
			.setLink(Constants.STREET_BASE_URL+
				+ gisFeatureDistance.getGid());
		SyndContent description = new SyndContentImpl();
		description.setType(OutputFormat.ATOM.getContentType());
		description.setValue(gisFeatureDistance.getName());
		entry.setDescription(description);
		entries.add(entry);
	    }
	    
	    synFeed.setEntries(entries);

	    try {
		oWriter = new OutputStreamWriter(outputStream, Constants.CHARSET);
	    } catch (UnsupportedEncodingException e) {
		throw new RuntimeException("unknow encoding "+Constants.CHARSET);
	    }

	    // Copy synfeed to output
	    SyndFeedOutput output = new SyndFeedOutput();
	    try {
		output.output(synFeed, oWriter);
		 // Flush
		    oWriter.flush();
	    } catch (Exception e) {
		throw new ServiceException(e);
	    }

	   
	} finally {
	    if (oWriter != null)
		try {
		    oWriter.close();
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	    if (outputStream != null)
		try {
		    outputStream.close();
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	}

    }

}
