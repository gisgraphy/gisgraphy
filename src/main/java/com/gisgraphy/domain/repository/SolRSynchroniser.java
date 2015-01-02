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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.HouseNumber;
import com.gisgraphy.domain.geoloc.entity.Language;
import com.gisgraphy.domain.geoloc.entity.Street;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeleteAllEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureDeletedEvent;
import com.gisgraphy.domain.geoloc.entity.event.GisFeatureStoredEvent;
import com.gisgraphy.domain.geoloc.entity.event.IEvent;
import com.gisgraphy.domain.geoloc.entity.event.PlaceTypeDeleteAllEvent;
import com.gisgraphy.fulltext.FullTextFields;
import com.gisgraphy.fulltext.IsolrClient;
import com.gisgraphy.geoloc.GisgraphyCommunicationException;
import com.gisgraphy.helper.ClassNameHelper;
import com.gisgraphy.helper.EncodingHelper;
import com.gisgraphy.helper.RetryOnErrorTemplate;
import com.gisgraphy.street.HouseNumberComparator;
import com.gisgraphy.street.HouseNumberSerializer;

/**
 * Interface of data access object for {@link Language}
 * 
 * @see ISolRSynchroniser
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class SolRSynchroniser implements ISolRSynchroniser {
	
	HouseNumberSerializer houseNumberListSerializer = new HouseNumberSerializer();
	HouseNumberComparator houseNumberComparator = new HouseNumberComparator();
    
    private static int numberOfRetryOnFailure = 3;

    /**
     * Needed by cglib
     */
    @SuppressWarnings("unused")
    private SolRSynchroniser() {

    }

    protected static final Logger logger = LoggerFactory
	    .getLogger(SolRSynchroniser.class);

    private IsolrClient solClient;

    public SolRSynchroniser(IsolrClient solrClient) {
	Assert
		.notNull(solrClient,
			"can not instanciate solRsynchroniser because the solrClient is null");
	this.solClient = solrClient;
    }

    /**
     * @param gisFeatureEvent
     */
    private void handleEvent(final GisFeatureDeletedEvent gisFeatureEvent) {
	try {
	    RetryOnErrorTemplate<Object> retryOnError = new RetryOnErrorTemplate<Object>() {
		    @Override
		    public String tryThat() throws Exception {
			solClient.getServer().deleteById(
				    gisFeatureEvent.getGisFeature().getFeatureId().toString());
			    solClient.getServer().commit(true, true);
			    return null;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : deletion of feature with id="+gisFeatureEvent.getGisFeature().getFeatureId());
		retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	    throw new GisgraphyCommunicationException("Can not synchronise SolR : can not delete feature with id="+gisFeatureEvent.getGisFeature().getFeatureId(),e.getCause());
	} 
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ISolRSynchroniser#deleteAll()
     */
    public void deleteAll() {
	try {
	    RetryOnErrorTemplate<Object> retryOnError = new RetryOnErrorTemplate<Object>() {
		    @Override
		    public String tryThat() throws Exception {
			 logger.info("The entire index will be reset");
			    solClient.getServer().deleteByQuery("*:*");
			    solClient.getServer().commit(true,true);
			    solClient.getServer().optimize(true,true);
			    return null;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : deletion of all features");
		retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	    throw new GisgraphyCommunicationException("Can not synchronise SolR : can not delete all features",e.getCause());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ISolRSynchroniser#deleteAll()
     */
    public void handleEvent(PlaceTypeDeleteAllEvent placeTypeDeleteAllEvent) {
	deleteAllByPlaceType(placeTypeDeleteAllEvent.getPlaceType());
    }

    public void deleteAllByPlaceType(final Class<? extends GisFeature> placetype) {
	try {
	    RetryOnErrorTemplate<Object> retryOnError = new RetryOnErrorTemplate<Object>() {
		    @Override
		    public String tryThat() throws Exception {
			  logger.info("GisFeature of type"
				    + placetype.getClass().getSimpleName() + " will be reset");
			    solClient.getServer().deleteByQuery(
				    FullTextFields.PLACETYPE.getValue() + ":"
					    + placetype.getSimpleName());
			    solClient.getServer().commit(true,true);
			    solClient.getServer().optimize(true,true);
			    return null;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : deletion of features of type="+placetype.getClass().getSimpleName());
		retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	    throw new GisgraphyCommunicationException("Can not synchronise SolR : can not delete all "+placetype.getClass().getSimpleName(),e.getCause());
	}
    }

    private void handleEvent(final GisFeatureDeleteAllEvent gisFeatureDeleteAllEvent) {
	try {
	    RetryOnErrorTemplate<Object> retryOnError = new RetryOnErrorTemplate<Object>() {
		    @Override
		    public String tryThat() throws Exception {
			 for (GisFeature gisFeature : gisFeatureDeleteAllEvent
				    .getGisFeatures()) {
				solClient.getServer().deleteById(
					gisFeature.getFeatureId().toString());
			    }
			    solClient.getServer().commit(true, true);
			    return null;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : deletion of specific features");
		retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	    throw new GisgraphyCommunicationException("Can not synchronise SolR : Can not delete the specified features ",e.getCause());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ISolRSynchroniser#handleEvent(com.gisgraphy.domain.geoloc.entity.event.IEvent)
     */
    public void handleEvent(IEvent event) {
	logger.debug("An event has been received ");
	if (event instanceof GisFeatureStoredEvent) {
	    handleEvent((GisFeatureStoredEvent) event);
	} else if (event instanceof GisFeatureDeletedEvent) {
	    handleEvent((GisFeatureDeletedEvent) event);
	} else if (event instanceof GisFeatureDeleteAllEvent) {
	    handleEvent((GisFeatureDeleteAllEvent) event);
	} else if (event instanceof PlaceTypeDeleteAllEvent) {
	    handleEvent((PlaceTypeDeleteAllEvent) event);
	} else {
	    logger.debug("unknow event " + event.getClass().getSimpleName());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ISolRSynchroniser#commit()
     */
    public boolean commit() {
	try {
	    RetryOnErrorTemplate<Boolean> retryOnError = new RetryOnErrorTemplate<Boolean>() {
		    @Override
		    public Boolean tryThat() throws Exception {
			solClient.getServer().commit(true, true);
			return true;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : commit");
		return retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	    logger.error("Can not synchronise SolR : can not commit ",e.getCause());
	    return false;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gisgraphy.domain.repository.ISolRSynchroniser#optimize()
     */
    public void optimize() {
	try {
	    RetryOnErrorTemplate<Boolean> retryOnError = new RetryOnErrorTemplate<Boolean>() {
		    @Override
		    public Boolean tryThat() throws Exception {
			solClient.getServer().optimize(true,true);
			return true;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : optimize");
		retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	  throw new GisgraphyCommunicationException("Can not synchronise SolR : can not optimize : "+e.getMessage(),e.getCause());
	}
    }

    private void handleEvent(final GisFeatureStoredEvent gisfeatureCreatedEventEvent) {
	try {
	    RetryOnErrorTemplate<Boolean> retryOnError = new RetryOnErrorTemplate<Boolean>() {
		    @Override
		    public Boolean tryThat() throws Exception {
			SolrInputDocument ex = new SolrInputDocument();
			GisFeature gisFeature = gisfeatureCreatedEventEvent.getGisFeature();

			if (gisFeature == null) {
			    logger.info("Can not synchronize a null gisFeature");
			    return false;
			}
			if (gisFeature.getFeatureId() == null || gisFeature.getFeatureId() <= 0) {
			    logger
				    .info("Can not synchronize GisFeature with wrong featureId : "
					    + gisFeature.getFeatureId());
			    return false;
			}

			if (gisFeature.getLatitude() == 0 && gisFeature.getLongitude() == 0) {
			    logger.info("Can not synchronize GisFeature "
				    + gisFeature.getFeatureId() + " with wrong Location "
				    + gisFeature.getName() + ": [" + gisFeature.getLongitude()
				    + "," + gisFeature.getLatitude() + "]");
			    return false;
			}
			
			
			if (!gisFeature.isFullTextSearchable()) {
			    logger.debug(gisFeature.getClass().getSimpleName()
				    + " is not FullTextSearchable");
			    return false;
			}

			ex.setField(FullTextFields.FEATUREID.getValue(), gisFeature
				.getFeatureId());
			if (gisFeature.getName()!=null && !gisFeature.getName().trim().equals("")){
			ex.setField(FullTextFields.NAME.getValue(), EncodingHelper
				.toUTF8(gisFeature.getName()));
			}
			ex.setField(FullTextFields.LAT.getValue(), gisFeature.getLatitude());
			ex.setField(FullTextFields.LONG.getValue(), gisFeature.getLongitude());
			String latAsString = String.format(Locale.US, "%s", gisFeature.getLatitude().doubleValue());
			String lngAsString = String.format(Locale.US, "%s", gisFeature.getLongitude().doubleValue());
			ex.setField(FullTextFields.LOCATION.getValue(), latAsString+","+lngAsString);
			ex.setField(FullTextFields.OPENSTREETMAP_ID.getValue(), gisFeature.getOpenstreetmapId());
			
			String placetype = ClassNameHelper.stripEnhancerClass(gisFeature
					.getClass().getSimpleName());
				ex.setField(FullTextFields.PLACETYPE.getValue(), placetype);
			String countryCode = gisFeature.getCountryCode();
			if (countryCode != null) {
				    ex.setField(FullTextFields.COUNTRYCODE.getValue(), gisFeature.getCountryCode().toUpperCase());
				    //Since V4.0, we don't preprocess some field for memory storage 
				   /* ex.setField(FullTextFields.COUNTRY_FLAG_URL.getValue(), URLUtils
							.createCountryFlagUrl(gisFeature.getCountryCode()));*/
				}
			if (gisFeature.getIsIn()!=null && !gisFeature.getIsIn().trim().equals("")){
	    	    ex.setField(FullTextFields.IS_IN.getValue(), gisFeature.getIsIn());
	    	}
	    	if (gisFeature.getIsInPlace()!=null && !gisFeature.getIsInPlace().trim().equals("")){
	    	    ex.setField(FullTextFields.IS_IN_PLACE.getValue(), gisFeature.getIsInPlace());
	    	}
	    	if (gisFeature.getIsInAdm()!=null && !gisFeature.getIsInAdm().trim().equals("")){
	    	    ex.setField(FullTextFields.IS_IN_ADM.getValue(), gisFeature.getIsInAdm());
	    	}
	    	if (gisFeature.getIsInZip()!=null && gisFeature.getIsInZip().size()>0){
	    	    ex.setField(FullTextFields.IS_IN_ZIP.getValue(), gisFeature.getIsInZip() );
	    	}
			if (gisFeature instanceof City){
				ex.setField(FullTextFields.MUNICIPALITY.getValue(), ((City) gisFeature).isMunicipality());
			}
			if (gisFeature.getIsInCityAlternateNames()!=null && gisFeature.getIsInCityAlternateNames().size()>0){
				ex.setField(FullTextFields.IS_IN_CITIES.getValue(), gisFeature.getIsInCityAlternateNames() );
			}
		    if (gisFeature instanceof Street) {
		    	ex.setField(FullTextFields.LENGTH.getValue(), ((Street) gisFeature).getLength());
		    	ex.setField(FullTextFields.ONE_WAY.getValue(), ((Street) gisFeature).isOneWay());
		    	ex.setField(FullTextFields.STREET_TYPE.getValue(), ((Street) gisFeature).getStreetType());
		    	ex.setField(FullTextFields.CITY_POPULATION.getValue(), ((Street) gisFeature).getPopulation());
		    	
		    	/*if (((Street) gisFeature).getFullyQualifiedAddress()!=null && !((Street) gisFeature).getFullyQualifiedAddress().trim().equals("")){
		    	    ex.setField(FullTextFields.FULLY_QUALIFIED_ADDRESS.getValue(), ((Street) gisFeature).getFullyQualifiedAddress());
		    	}*/
		    	SortedSet<HouseNumber> houseNumbersFromEntity = ((Street) gisFeature).getHouseNumbers();
		    	if (houseNumbersFromEntity!=null && houseNumbersFromEntity.size()!=0){
			    	//SortedSet<HouseNumber> houseNumbers = new TreeSet<HouseNumber>(houseNumberComparator);
					//houseNumbers.addAll(houseNumbersFromEntity);
						 List<String> houseNumbersToAdd= new ArrayList<String>();
						// Collections.sort(houseNumbers,houseNumberComparator);
			    		for (HouseNumber houseNumber:houseNumbersFromEntity){
			    			houseNumbersToAdd.add(houseNumberListSerializer.serialize(houseNumber));
			    		}
			    		ex.setField(FullTextFields.HOUSE_NUMBERS.getValue(),houseNumbersToAdd );
		    	}
				populateAlternateNamesForStreet(gisFeature.getAlternateNames(),ex);
		    } else {
			
			ex.setField(FullTextFields.FEATURECLASS.getValue(), gisFeature
				.getFeatureClass());
			ex.setField(FullTextFields.FEATURECODE.getValue(), gisFeature
				.getFeatureCode());
			if (gisFeature.getAsciiName()!=null && !gisFeature.getAsciiName().trim().equals("")){
			ex.setField(FullTextFields.NAMEASCII.getValue(), gisFeature
				.getAsciiName());
			}
			
			ex.setField(FullTextFields.ELEVATION.getValue(), gisFeature
				.getElevation());
			ex.setField(FullTextFields.AMENITY.getValue(), gisFeature
					.getAmenity());
			ex.setField(FullTextFields.GTOPO30.getValue(), gisFeature.getGtopo30());
			ex.setField(FullTextFields.TIMEZONE.getValue(), gisFeature
				.getTimezone());
			
			ex.setField(FullTextFields.POPULATION.getValue(), gisFeature
				.getPopulation());
			//Since V 4.0 all processed field are not processed dur reduce datastorage)
			/*ex.setField(FullTextFields.FULLY_QUALIFIED_NAME.getValue(),
				EncodingHelper.toUTF8(gisFeature.getFullyQualifiedName(false)));
			ex.setField(FullTextFields.GOOGLE_MAP_URL.getValue(), URLUtils
				.createGoogleMapUrl(gisFeature.getLocation(), gisFeature
					.getName()));
			ex.setField(FullTextFields.YAHOO_MAP_URL.getValue(), URLUtils
				.createYahooMapUrl(gisFeature.getLocation()));
			ex.setField(FullTextFields.OPENSTREETMAP_MAP_URL.getValue(), URLUtils
					.createOpenstreetmapMapUrl(gisFeature.getLocation()));*/
			
			// setAdmCode from adm not from the gisfeature one because of
			// syncAdmCodesWithLinkedAdmOnes if it is false , the value may not be
			// the same
			Adm adm = null;

			if (gisFeature instanceof Adm) {
			    adm = (Adm) gisFeature;
			    ex.setField(FullTextFields.LEVEL.getValue(), adm.getLevel());
			    
			} else {
			    adm = gisFeature.getAdm();
			}
			// we set admCode once for all
			if (adm != null) {
			    ex.setField(FullTextFields.ADM1CODE.getValue(), adm.getAdm1Code());
			    ex.setField(FullTextFields.ADM2CODE.getValue(), adm.getAdm2Code());
			    ex.setField(FullTextFields.ADM3CODE.getValue(), adm.getAdm3Code());
			    ex.setField(FullTextFields.ADM4CODE.getValue(), adm.getAdm4Code());
			}
			while (adm != null) {
			    int level = adm.getLevel();
			    String admLevelName = FullTextFields
				    .valueOf("ADM" + level + "NAME").getValue();
			    ex.setField(admLevelName, EncodingHelper.toUTF8(adm.getName()));
			    if (level == 1 || level == 2) {
				populateAlternateNames(admLevelName, adm.getAlternateNames(),
					ex);
			    }
			    adm = adm.getParent();
			}
				Set<ZipCode> zipCodes =gisFeature.getZipCodes();
				if (zipCodes != null) {
					List<String> zipCodesToAdd = new ArrayList<String>();
					for (ZipCode zipCode:zipCodes){
						zipCodesToAdd.add(zipCode.getCode().trim());
					}
				    ex.setField(FullTextFields.ZIPCODE.getValue(),zipCodesToAdd);
				}
			   

			// No prefix for cities

			Collection<AlternateName> alternatenames = gisFeature.getAlternateNames();
			populateAlternateNames(FullTextFields.NAME.getValue(), alternatenames,
				ex);

			// we don't want this fields
			// populateAlternateNames("adm3_", adm2.getAlternateNames(), ex);
			// populateAlternateNames("adm4_", adm1.getAlternateNames(), ex);
			if (gisFeature instanceof Country) {
			    Country country = (Country) gisFeature;
			    ex.setField(FullTextFields.CONTINENT.getValue(), country
				    .getContinent());
			    ex.setField(FullTextFields.CURRENCY_CODE.getValue(), country
				    .getCurrencyCode());
			    ex.setField(FullTextFields.CURRENCY_NAME.getValue(), country
				    .getCurrencyName());
			    ex.setField(FullTextFields.FIPS_CODE.getValue(), country
				    .getFipsCode());
			    ex.setField(FullTextFields.ISOALPHA2_COUNTRY_CODE.getValue(),
				    country.getIso3166Alpha2Code());
			    ex.setField(FullTextFields.ISOALPHA3_COUNTRY_CODE.getValue(),
				    country.getIso3166Alpha3Code());
			    ex.setField(FullTextFields.COUNTRYCODE.getValue(), country.getCountryCode()
					.toUpperCase());
			    ex.setField(FullTextFields.POSTAL_CODE_MASK.getValue(), country
				    .getPostalCodeMask());
			    ex.setField(FullTextFields.POSTAL_CODE_REGEX.getValue(), country
				    .getPostalCodeRegex());
			    ex.setField(FullTextFields.PHONE_PREFIX.getValue(), country
				    .getPhonePrefix());
			    List<Language> spokenLanguages = country.getSpokenLanguages();
			    if (spokenLanguages.size() > 0){
				    List<String> languagesToAdd= new ArrayList<String>();
					for (Language language : spokenLanguages) {
					languagesToAdd.add(language.getIso639LanguageName());
				    }
					ex.setField(FullTextFields.SPOKEN_LANGUAGES.getValue(),
							languagesToAdd);
			    }
			    ex.setField(FullTextFields.TLD.getValue(), country.getTld());
			    ex.setField(FullTextFields.CAPITAL_NAME.getValue(), country
				    .getCapitalName());
			    ex.setField(FullTextFields.AREA.getValue(), country.getArea());
			    populateAlternateNames(FullTextFields.COUNTRYNAME
				    .getValue(), country.getAlternateNames(), ex);
			    if (country.getName()!=null){
			    ex.setField(FullTextFields.COUNTRYNAME.getValue(),
				    EncodingHelper.toUTF8(country.getName()));
			    }
			} else {
			    if (countryCode != null) {
				Country country = gisFeature.getCountry();
				if (country != null) {
				    populateAlternateNames(FullTextFields.COUNTRYNAME
					    .getValue(), country.getAlternateNames(), ex);
				    if (country.getName()!=null){
				    ex.setField(FullTextFields.COUNTRYNAME.getValue(),
					    EncodingHelper.toUTF8(country.getName()));
				    }
				} else {
				    logger.error("Can not find country with code "
					    + gisFeature.getCountryCode() + " for "
					    + gisFeature);
				}
			    }
			}
			}
			solClient.getServer().add(ex);
			return true;
		    }
		};
		retryOnError.setLoggingSentence("Synchronise SolR : Add feature with id "+gisfeatureCreatedEventEvent.getGisFeature());
		retryOnError.times(numberOfRetryOnFailure);
	    
	    
	} catch (Exception e) {
	  throw new GisgraphyCommunicationException("Can not synchronise SolR : can not synchronize  "+gisfeatureCreatedEventEvent.getGisFeature()+":" +e,e.getCause());
	}
    }
    
    //Same as gisfeature but ignore language 
    private void populateAlternateNamesForStreet(Collection<AlternateName> alternateNames, SolrInputDocument ex) {
    	if (alternateNames == null || alternateNames.size() == 0) {
    	    return;
    	}
    	List<String> alternateNamesStrings = new ArrayList<String>();
    	for (AlternateName alternateName:alternateNames){
    		if (alternateName!=null){
    			alternateNamesStrings.add(alternateName.getName().trim());
    		}
    	}
    	
    	ex.setField(FullTextFields.NAME.getValue()
    			+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue(),
    			alternateNamesStrings
    				.toArray(new String[alternateNames
    					.size()]));
    	    }

    
    
    private void populateAlternateNames(String fieldPrefix,
	    Collection<AlternateName> alternateNames, SolrInputDocument ex) {
	if (alternateNames == null || alternateNames.size() == 0) {
	    return;
	}

	Map<String, List<String>> alternateNamesByAlpha3LanguageCode = new HashMap<String, List<String>>();

	List<String> alternateNamesWithoutAnAlpha3Code = new ArrayList<String>();
	// List<String> alternateNamesAsStrings = new ArrayList<String>();
	if (alternateNames != null) {
	    for (AlternateName alternateName : alternateNames) {
		String alpha3Code = alternateName.getLanguage();
		if (alpha3Code != null){
		    alpha3Code = alpha3Code.trim();
		}
		if (alpha3Code == null || "".equals(alpha3Code)) {
		    alternateNamesWithoutAnAlpha3Code.add(EncodingHelper
			    .toUTF8(alternateName.getName()));
		    continue;
		}
		alpha3Code = alpha3Code.toLowerCase();
		List<String> alternateNamesForCurrentLanguage = alternateNamesByAlpha3LanguageCode
			.get(alpha3Code);
		if (alternateNamesForCurrentLanguage == null) {
		    alternateNamesForCurrentLanguage = new ArrayList<String>();
		    alternateNamesByAlpha3LanguageCode.put(alpha3Code,
			    alternateNamesForCurrentLanguage);
		}
		alternateNamesForCurrentLanguage.add(EncodingHelper
			.toUTF8(alternateName.getName()));
	    }
	}

	// Traverse the keys in the map, generating the fields in solr
	Set<String> keys = alternateNamesByAlpha3LanguageCode.keySet();
	for (String key : keys) {
	    List<String> alternateNamesForCurrentLanguage = alternateNamesByAlpha3LanguageCode
		    .get(key);
	    ex
		    .setField(
			    fieldPrefix
				    + FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX
					    .getValue() + key.toUpperCase(),
			    alternateNamesForCurrentLanguage
				    .toArray(new String[alternateNamesForCurrentLanguage
					    .size()]));
	}

	// Handle all the names without alpha 3 codes
	ex.setField(fieldPrefix
		+ FullTextFields.ALTERNATE_NAME_SUFFIX.getValue(),
		alternateNamesWithoutAnAlpha3Code
			.toArray(new String[alternateNamesWithoutAnAlpha3Code
				.size()]));
    }

}
