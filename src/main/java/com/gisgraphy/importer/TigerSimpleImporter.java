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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.FlushMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gisgraphy.addressparser.format.BasicAddressFormater;
import com.gisgraphy.domain.geoloc.entity.AlternateOsmName;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.domain.repository.ISolRSynchroniser;
import com.gisgraphy.domain.repository.IhouseNumberDao;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.NameValueDTO;
import com.gisgraphy.domain.valueobject.Output;
import com.gisgraphy.domain.valueobject.Output.OutputStyle;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.FullTextSearchEngine;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.GeolocHelper;
import com.gisgraphy.helper.OrthogonalProjection;
import com.gisgraphy.helper.StringHelper;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Import the addresses from an (pre-processed) tiger census data file .
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class TigerSimpleImporter extends AbstractSimpleImporterProcessor {

    

    @Autowired
    protected OpenStreetMapSimpleImporter openStreetMapImporterHelper;

    protected IOpenStreetMapDao openStreetMapDao;

    protected IhouseNumberDao houseNumberDao;

    protected ISolRSynchroniser solRSynchroniser;

    protected IFullTextSearchEngine fullTextSearchEngine;

    @Autowired
    protected IIdGenerator idGenerator;

    BasicAddressFormater formater = BasicAddressFormater.getInstance();

    LabelGenerator labelGenerator = LabelGenerator.getInstance();
    String[] currentfields = null;

   // private static final Long GID_RESTART = null;
    
  


    //the fulltext has to be greater than the db one since the fulltext use boundingbox and midle point (db use cross and can be lower)
    public static final long DEFAULT_FULLTEXT_SEARCH_DISTANCE = 2000L;

    protected static final long DEFAULT_SEARCH_DISTANCE = 1000L;

    public static final Logger logger = LoggerFactory.getLogger(TigerSimpleImporter.class);
    
    public OrthogonalProjection orthogonalProjection = new OrthogonalProjection();


    protected final static Output MEDIUM_OUTPUT = Output.withDefaultFormat().withStyle(OutputStyle.MEDIUM);

    public  final static String COUNTRY_CODE = "US";


    public static final int MAX_NAME_SIZE = 250;

    public static final Float MAX_SCORE_THRESHOLD = 40f;
    public static  int counter = 0;
    
    protected boolean notYetThere = true;


    

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#flushAndClear
     * ()
     */
    @Override
    protected void flushAndClear() {
    }

    @Override
    protected void setup() {
        //temporary disable logging when importing
        FullTextSearchEngine.disableLogging=true;
        idGenerator.sync();
        super.setup();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#getFiles()
     */
    @Override
    protected File[] getFiles() {
        return ImporterHelper.listCountryFilesToImport(importerConfig.getTigerDir());
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
     * getNumberOfColumns()
     */
    @Override
    protected int getNumberOfColumns() {
        return 10;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#processData
     * (java.lang.String)
     */
    @Override
    protected void processData(String line) throws ImporterException {
        if (line==null || "".equals(line.trim())){
            return;
        }
        //0:ARID 1:alt names 2:SHAPE 3:LOCATION 4:length 5:STREET NAME 6:CITY 7:ZIP 8:HOUSE 9:TLID
        String[] fields = line.split("\\t");
        if (fields.length != getNumberOfColumns()) {
            logger.error("wrong number of column ("+fields.length+") for "+line);
            return;
        }
        if (!isAllRequiredFieldspresent(fields)){
            logger.error("some fields are not present for line "+line);
            return;
        };

        long gid = 0;
        if (!isEmptyField(fields, 0, true)) {
            gid = new Long(fields[0]);
        } else {
            gid = idGenerator.getNextGId();
        }

        //by specifying hash restart we allow to re run this importer from the line where the hash is equals to hash restart
       /* if (GID_RESTART==null){
            notYetThere=false;
        } else {

            if (gid == GID_RESTART){
                notYetThere = false;
            }

            if (GID_RESTART!=null && notYetThere){
                //logger.error("notyetthere");
                return;
            }
        }*/

        //location
        Point locationOfTheStreet =null;;
        if (!isEmptyField(fields, 3, false)) {
            try {
                locationOfTheStreet = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[3]);

            } catch (RuntimeException e) {
                logger.warn(gid+" can not parse location for "+fields[3]+" : "+e);
                return;
            }
        }

        String streetName = null;
        if (StringHelper.isNotEmptyString(fields[5])){
            streetName = StringHelper.correctStreetName(fields[5],COUNTRY_CODE);
        }

       OpenStreetMap o =findNearestStreet(streetName, locationOfTheStreet,fields);
       if (o!=null || StringHelper.isEmptyString(fields[5])){//if name is null we can not add the street so we consider the street as managed
           counter++;
       }
       if (counter %1000 ==0){
           logger.error("nb street="+counter);
       }

       

    }


   

    protected OpenStreetMap createStreet(String[] fields) {
        //0:ARID 1:alt names 2:SHAPE 3:LOCATION 4:length 5:STREET NAME 6:CITY 7:ZIP 8:HOUSE 9:TLID
        if (fields!=null && fields.length==getNumberOfColumns() ){
            logger.debug("will create street for "+dumpFields(fields));
            OpenStreetMap street = new OpenStreetMap();
            //0 gid
            long gid = 0;
            /*if (!isEmptyField(fields, 0, true)) {
                gid = new Long(fields[0]);
            } else {*/
                gid = idGenerator.getNextGId();
            //}
            street.setGid(gid);

            //3 location
            Point location =null;;
            if (!isEmptyField(fields, 3, false)) {
                try {
                    location = (Point) GeolocHelper.convertFromHEXEWKBToGeometry(fields[3]);
                    street.setLocation(location);
                } catch (RuntimeException e) {
                    logger.warn(gid+" can not parse location for "+fields[3]+" : "+e);
                    return null;
                }
            }
            street.setCountryCode(COUNTRY_CODE);
            street.setSource(GISSource.TIGER);

            //5 streetname
            street.setName(getBestStreetName(fields[1],fields[5]));
            
            
           street.setStreetRef(getBestRef(fields[1],street.getName()));
           
            for (String alternateName :splitAlternateNames(fields[1])){
               if (!StringHelper.isEmptyString(alternateName)){
                  street.addAlternateName(new AlternateOsmName(alternateName,AlternateNameSource.TIGER,COUNTRY_CODE));  
               }
            } 
            
            if (street.getName()!=null){
                StringHelper.updateOpenStreetMapEntityForIndexation(street);
            }
            


            //2 shape
            if (!isEmptyField(fields, 2, true)) {
                try {
                    street.setShape((LineString)GeolocHelper.convertFromHEXEWKBToGeometry(fields[2]));
                } catch (RuntimeException e) {
                    logger.warn(gid+" can not parse shape for "+fields[2] +" : "+e);
                    return null;
                }
            }

            //4 length
            if (!isEmptyField(fields, 4, false)) {
                Double length;
                try {
                    length = new Double(fields[4].trim());
                    street.setLength(length);
                } catch (NumberFormatException e) {
                    logger.warn("can not convert length '"+fields[4].trim()+"' for gid "+gid);
                }
            }
           
            openStreetMapImporterHelper.setIsInFields(street);
            //fallback on fields if not filled
            if (!isEmptyField(fields,6, false) && street.getIsIn()==null){
                street.setIsIn(fields[6]);
            }
            if (!isEmptyField(fields,7, false)){
                street.addIsInZip(fields[7]);
                street.setZipCode(fields[7]);
            }
            if (street.getName() !=null){
                street.setAlternateLabels(labelGenerator.generateLabels(street));
                street.setLabel(labelGenerator.generateLabel(street));
                street.setFullyQualifiedName(labelGenerator.getFullyQualifiedName(street, false));
                street.setLabelPostal(labelGenerator.generatePostal(street));
            }
            return street;
        } else {
            return null;
        }
    }



    protected String cleanNumber(String string) {
        if (string!=null){
            String cleaned = string.trim().replaceFirst("#", "").replaceFirst("^0+", "");
            if (cleaned.trim().length()==0){
                return null;
            } else {
                return cleaned;
            }
        }
        return null;
    }



    protected String cleanupStreetName(String streetName){
        if (streetName!=null){
            if (streetName.length()>MAX_NAME_SIZE){
                streetName = streetName.substring(0, MAX_NAME_SIZE);
            }
            return streetName.trim().replaceAll("[\\s]+", " ").replaceFirst("^0+(?!$)", "").trim();
        }
        return streetName;
    }

    protected boolean isAllRequiredFieldspresent(String[] fields) {
        if (isEmptyField(fields, 2, false)//shape
                ||  isEmptyField(fields, 3, false)//location
                ){
            return false;
        }
        return true;
    }


    /**
     * 
     * Simply returns a string representation x y instead of the y x one of the toString of jts Point
     */
    protected String ts(Point point){
        if (point!=null){
            return point.getY()+" "+point.getX();
        }
        return null;
    }
    
    //todo to populate alt names
    public List<String> getSynonyms(String streetName){
        List<String> altNames = new ArrayList<String>();
        if (!StringHelper.isEmptyString(streetName)){
            
        }
        return altNames;
    }

    
    /*
     * 1 preferer le nom avec dir=>getBestName
     *      statrts with dir et pas l'autre ou vice versa et l'un fini comme l'autre
     * 
     * "Ave J-8" "W Ave J 8"
     * 2 si name= cardinal en lettre => preferé cardinal chiffre=>ajouter nom alt + generalisé
     * 3 detect ref pour ajouter ref et alt name
     * state route \d
     * state rte=>expand
     * state highway
     * state hwy=>expand
     * state rd =>expand
     * state road
     * (bus|business|business loop|Business Interstate|business spur|state|co|county|u[.]?s[.]?|I|interstate])\\b[-\\s]\\s?((?:road|route|Highway|rte|rd|hwy)\\b)?(?:no|n°|No)[.]?[-\\s]?([JN]?\\d+)\\s?(bus|business)?
     * Business Loop
     * Business Interstate Highway No. 35-M
     * Business Interstate 35-M
     * Business spur
     * I-40BUS
     * business 40
     * state route 43
     * "Forest Rte 7N09"
     * "I- 205 Bus"
     * "Co Hwy J2"
     * "Co Hwy N7"
     * 4/ si altname != et pas de rue autour qui ont le meme nom ? =>ajouter alt
     * 
     * 
     * 
     * 
     */

   protected OpenStreetMap findNearestStreet(String streetNameInFile, Point location, String[] fields) {
      
        OpenStreetMap osm =null;
        if (streetNameInFile!=null){
            List<HouseNumber> housenumbers = parseHouseNumbers(fields[8]);
            LineString shapeFromFile = null;
            if (!isEmptyField(fields, 2, true)) {
                try {
                    shapeFromFile = ((LineString)GeolocHelper.convertFromHEXEWKBToGeometry(fields[2]));
                } catch (RuntimeException e) {
                    logger.warn("find nearest street, can not parse shape for "+fields[2] +" : "+e);
                    return null;
                }
            }
            //only if streetname is nomt null
            FulltextQuery query =null;
            try {
                query = new FulltextQuery(streetNameInFile, Pagination.DEFAULT_PAGINATION, MEDIUM_OUTPUT, 
                        com.gisgraphy.fulltext.Constants.STREET_PLACETYPE, null);
            } catch (IllegalArgumentException e) {
                logger.error("can not create a fulltext query for "+streetNameInFile+", will return the nearest");
                osm = openStreetMapDao.getNearestFrom(location,DEFAULT_FULLTEXT_SEARCH_DISTANCE);
                if (osm!=null){
                    updateStreet(osm, shapeFromFile,location, fields);
                    addhouseNumbers(osm, housenumbers);
                }
                return osm;
            }
            query.withAllWordsRequired(false).withoutSpellChecking();
            query.around(location);
            query.withRadius(DEFAULT_FULLTEXT_SEARCH_DISTANCE);
            FulltextResultsDto results;
            try {
                results = fullTextSearchEngine.executeQuery(query);
            } catch (RuntimeException e) {
                logger.error("error during fulltext search : "+e.getMessage(),e);
                return null;
            }
            int resultsSize = results.getResultsSize();
            //  logger.warn(query + "returns "+resultsSize +" results");
           
            List<SolrResponseDto> resultsList = results.getResults();
            if (resultsSize == 1) {
                SolrResponseDto street = resultsList.get(0);
                if (street != null) {
                    Long openstreetmapId = street.getOpenstreetmap_id();
                    // logger.warn("findNearestStreet : find a street with osmId "+openstreetmapId);
                    if (openstreetmapId != null) {
                        osm = openStreetMapDao
                                .getByOpenStreetMapId(openstreetmapId);
                        if (osm == null) {
                            logger.warn("can not find street for id "
                                    + openstreetmapId);
                        } 
                        if (results.getMaxScore() < MAX_SCORE_THRESHOLD) {
                            osm = createStreet(fields);
                            if (osm != null) {
                                openStreetMapDao.save(osm);
                                addhouseNumbers(osm, housenumbers);
                            }
                            return osm;
                        }

                    }
                }
            }
            if (resultsSize > 1) {
                osm = getNearestByGIds(resultsList,location,streetNameInFile);
                if (osm == null) {
                    logger.warn("can not getNearestByGIds ");
                } 
                if (results.getMaxScore() < MAX_SCORE_THRESHOLD) {
                    osm = createStreet(fields);
                    if (osm != null) {
                        openStreetMapDao.save(osm);
                        addhouseNumbers(osm, housenumbers);
                    }
                    return osm;
                }
            }
            if (resultsSize==0){
                osm = createStreet(fields);
                if(osm!=null){
                    openStreetMapDao.save(osm);
                    addhouseNumbers(osm, housenumbers);
                }
                return osm;
            }
            if (osm !=null){
                updateStreet(osm, shapeFromFile,location, fields);
                addhouseNumbers(osm, housenumbers);
            }
        }//else streetname is null, we can not add a street
        return osm;
    }

   
   
   protected String getBestStreetName(String names,String streetName) {
       String best = StringHelper.correctStreetName(streetName,COUNTRY_CODE);
           for (String name:splitAlternateNames(names)){
               //best is if start with a direction not a ref if possible and expanded
               if (StringHelper.hasDirection(name) && !StringHelper.isRef(name)){
                   best= StringHelper.correctStreetName(name,COUNTRY_CODE);
               }
           }
       return best;
   }
   
   protected String[] splitAlternateNames(String anFromFile){
       if (!StringHelper.isEmptyString(anFromFile)){
          return anFromFile.split("___");
       } else {
           return new String[0];
       }
   }
   
   protected String getBestRef(String names,String streetName) {
       if (StringHelper.isRef(streetName)){
           return StringHelper.correctStreetName(streetName,COUNTRY_CODE);
       }
       if (!StringHelper.isEmptyString(names)){
           String[] namesArray = names.split("___");
           for (String name:namesArray){
               //best is if start with a direction not a ref if possible and expanded
               if (StringHelper.isRef(name)){
                   return StringHelper.correctStreetName(name,COUNTRY_CODE);
               }
           }

       }
       return null;
   }

    protected void addhouseNumbers(OpenStreetMap street,
            List<HouseNumber> housenumbers) {
        if (street !=null && housenumbers != null && housenumbers.size()>0){
            street.addHouseNumbers(housenumbers);
            for (HouseNumber hn:housenumbers){
                houseNumberDao.save(hn);
            }
            openStreetMapDao.save(street);
        }
        
    }

    protected List<HouseNumber> parseHouseNumbers(String string) {
        List<HouseNumber> result = new ArrayList<HouseNumber>();
        if (string==null || string.equals("")|| string.equals("\"\"")){
            return result;
        }
        Pattern p = Pattern.compile("(\\d+)_POINT[(]([\\-\\d\\.]+)\\s([-\\d\\.]+)[)];");
        Matcher m = p.matcher(string);
        while (  m .find()){
            Point point;
            try {
                point = GeolocHelper.createPoint(Float.valueOf(m.group(2)), Float.valueOf(m.group(3)));
            } catch (NumberFormatException e) {
                logger.error("can not create point for "+string);
             continue;
            }
            HouseNumber hn = new HouseNumber(m.group(1), point,COUNTRY_CODE);
            hn.setSource(GISSource.TIGER);
            result.add(hn);
            
        };
        return result;
    }

    protected void updateStreet(OpenStreetMap street, LineString shapeFromFile,Point location, String[] fields) {
      //0:ARID 1:alt names 2:SHAPE 3:LOCATION 4:length 5:STREET NAME 6:CITY 7:ZIP 8:HOUSE 9:TLID
        //update source
        if (street!=null){
            street.setCountryCode(COUNTRY_CODE);
        if (street.getSource()!=null){
            if (street.getSource() == GISSource.OPENADDRESSES){
                street.setSource(GISSource.TIGER_OPENADDRESSES);
            } else if (street.getSource() == GISSource.OSM){
                street.setSource(GISSource.TIGER_OSM);
            } else {
                street.setSource(GISSource.TIGER);
            }
        }
        //update shape
        if (shapeFromFile !=null && street.getShape()==null){
            street.setShape(shapeFromFile);
            //if we ovveride shape, we overide all link fields
            if (!isEmptyField(fields, 4, false)) {
                Double length;
                try {
                    length = new Double(fields[4].trim());
                    street.setLength(length);
                } catch (NumberFormatException e) {
                    logger.warn("can not convert length '"+fields[4].trim()+"' for gid "+fields[0]);
                }
            }
            if (location!=null && street.getLocation()==null){
                street.setLocation(location);
            }
        } 
        if (!isEmptyField(fields, 7, false)){
            street.setZipCode(fields[7]);
        } 
        if (!isEmptyField(fields, 6, false) ){//we prefer Tiger isin so we overide, it is more official
            street.setIsIn(fields[6]);
        } 
        if (StringHelper.isEmptyString(street.getName()) && !isEmptyField(fields, 5, false) ){//we prefer keep the osm one, it is often the expand version
            street.setName(getBestStreetName(fields[1],fields[5]));
        } 
        
        for (String alternateName :splitAlternateNames(fields[1])){
            if (!StringHelper.isEmptyString(alternateName)){
               street.addAlternateName(new AlternateOsmName(alternateName,AlternateNameSource.TIGER,COUNTRY_CODE));  
            }
         } 
        
        String bestRef = getBestRef(fields[1],fields[5]);
        if (StringHelper.isEmptyString(street.getStreetRef())){
            street.setStreetRef(bestRef);
        } else if (bestRef!=null){
            street.addAlternateName(new AlternateOsmName(bestRef, AlternateNameSource.TIGER, COUNTRY_CODE));
        }
        openStreetMapDao.save(street);
        }
    }

    
    

    protected OpenStreetMap getNearestByGIds(List<SolrResponseDto> results,Point point,String streetname) {
        List<Long> ids = new ArrayList<Long>();
        OpenStreetMap result = null;
        if (results!=null){
            for (SolrResponseDto dto:results){
                if (dto!=null && dto.getFeature_id()!=null){
                    ids.add(dto.getFeature_id());
                }
            }
            String idsAsSTring="";
            /*for (Long id:ids){
                idsAsSTring = idsAsSTring+","+id;
            }*/
            //logger.warn("getNearestByIds : "+idsAsSTring);
            result = openStreetMapDao.getNearestByGIds(point, ids);
            if (result==null){
                logger.warn("getNearestByGIds for"+streetname+" and  ids "+idsAsSTring+" and point" +point+" return  "+result);
            }
        }
        return result;
    }






    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#shouldBeSkiped
     * ()
     */
    @Override
    public boolean shouldBeSkipped() {
        //no need to check the us, if it is active, we don't skip and if it is inactive we skip anyway even if countries contains US
        return !importerConfig.isTigerImporterEnabled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
     * setCommitFlushMode()
     */
    @Override
    protected void setCommitFlushMode() {
        this.openStreetMapDao.setFlushMode(FlushMode.COMMIT);
        this.houseNumberDao.setFlushMode(FlushMode.COMMIT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
     * shouldIgnoreComments()
     */
    @Override
    protected boolean shouldIgnoreComments() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.AbstractImporterProcessor#
     * shouldIgnoreFirstLine()
     */
    @Override
    protected boolean shouldIgnoreFirstLine() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.geoloc.importer.IGeonamesProcessor#rollback()
     */
    public List<NameValueDTO<Integer>> rollback() {
        List<NameValueDTO<Integer>> deletedObjectInfo = new ArrayList<NameValueDTO<Integer>>();
        return deletedObjectInfo;
    }



    @Override
    protected void tearDown() {
        super.tearDown();
        FullTextSearchEngine.disableLogging=false;
    }


    @Required
    public void setHouseNumberDao(IhouseNumberDao houseNumberDao) {
        this.houseNumberDao = houseNumberDao;
    }

    @Required
    public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
        this.openStreetMapDao = openStreetMapDao;
    }

    @Required
    public void setFullTextSearchEngine(IFullTextSearchEngine fullTextSearchEngine) {
        this.fullTextSearchEngine = fullTextSearchEngine;
    }

    @Required
    public void setSolRSynchroniser(ISolRSynchroniser solRSynchroniser) {
        this.solRSynchroniser = solRSynchroniser;
    }

    @Required
    public void setOpenStreetMapImporterHelper(
            OpenStreetMapSimpleImporter openStreetMapImporterHelper) {
        this.openStreetMapImporterHelper = openStreetMapImporterHelper;
    }




}
