package com.gisgraphy.fulltext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrDocument;

import com.gisgraphy.domain.repository.exception.RepositoryException;
import com.gisgraphy.street.HouseNumberDeserializer;
import com.gisgraphy.street.HouseNumberDto;

public class solrResponseDtoBuilder {
	
	HouseNumberDeserializer houseNumberDeserializer = new HouseNumberDeserializer();
	
	  /**
     * Create a {@link SolrResponseDto} from a {@link SolrDocument}
     */
    //TODO maybe a unit test is missing here
    public SolrResponseDto build(SolrDocument solrDocument) {
	SolrResponseDto solrResponseDto = new SolrResponseDto();
	if (solrDocument != null) {
		solrResponseDto.score= getFieldAsFloat(solrDocument, "score");
	    solrResponseDto.name = getFieldAsString(solrDocument, FullTextFields.NAME
		    .getValue());
	    solrResponseDto.feature_id = getFieldAsLong(solrDocument,
		    FullTextFields.FEATUREID.getValue());
	    solrResponseDto.feature_class = getFieldAsString(solrDocument,
		    FullTextFields.FEATURECLASS.getValue());
	    solrResponseDto.feature_code = getFieldAsString(solrDocument,
		    FullTextFields.FEATURECODE.getValue());
	    solrResponseDto.name_ascii = getFieldAsString(solrDocument,
		    FullTextFields.NAMEASCII.getValue());
	    solrResponseDto.elevation = getFieldAsInteger(solrDocument,
		    FullTextFields.ELEVATION.getValue());
	    solrResponseDto.gtopo30 = getFieldAsInteger(solrDocument,
		    FullTextFields.GTOPO30.getValue());
	    solrResponseDto.timezone = getFieldAsString(solrDocument,
		    FullTextFields.TIMEZONE.getValue());
	    solrResponseDto.fully_qualified_name = getFieldAsString(solrDocument,
		    FullTextFields.FULLY_QUALIFIED_NAME.getValue());
	    solrResponseDto.placetype = getFieldAsString(solrDocument,
		    FullTextFields.PLACETYPE.getValue());
	    solrResponseDto.population = getFieldAsInteger(solrDocument,
		    FullTextFields.POPULATION.getValue());
	    solrResponseDto.lat = getFieldAsDouble(solrDocument, FullTextFields.LAT
		    .getValue());
	    solrResponseDto.lng = getFieldAsDouble(solrDocument, FullTextFields.LONG
		    .getValue());
	    solrResponseDto.adm1_code = getFieldAsString(solrDocument,
		    FullTextFields.ADM1CODE.getValue());
	    solrResponseDto.adm2_code = getFieldAsString(solrDocument,
		    FullTextFields.ADM2CODE.getValue());
	    solrResponseDto.adm3_code = getFieldAsString(solrDocument,
		    FullTextFields.ADM3CODE.getValue());
	    solrResponseDto.adm4_code = getFieldAsString(solrDocument,
		    FullTextFields.ADM4CODE.getValue());
	    solrResponseDto.adm1_name = getFieldAsString(solrDocument,
		    FullTextFields.ADM1NAME.getValue());
	    solrResponseDto.adm2_name = getFieldAsString(solrDocument,
		    FullTextFields.ADM2NAME.getValue());
	    solrResponseDto.adm3_name = getFieldAsString(solrDocument,
		    FullTextFields.ADM3NAME.getValue());
	    solrResponseDto.adm4_name = getFieldAsString(solrDocument,
		    FullTextFields.ADM4NAME.getValue());
	    solrResponseDto.zipcodes = getFieldsToSet(solrDocument,
		    FullTextFields.ZIPCODE.getValue());
	    solrResponseDto.country_name = getFieldAsString(solrDocument,
		    FullTextFields.COUNTRYNAME.getValue());
	    solrResponseDto.country_flag_url = getFieldAsString(solrDocument,
		    FullTextFields.COUNTRY_FLAG_URL.getValue());
	    solrResponseDto.google_map_url = getFieldAsString(solrDocument,
		    FullTextFields.GOOGLE_MAP_URL.getValue());
	    solrResponseDto.yahoo_map_url = getFieldAsString(solrDocument,
		    FullTextFields.YAHOO_MAP_URL.getValue());
	    solrResponseDto.openstreetmap_map_url = getFieldAsString(solrDocument,
			    FullTextFields.OPENSTREETMAP_MAP_URL.getValue());
	    solrResponseDto.name_alternates = getFieldsToList(solrDocument,
		    FullTextFields.NAME.getValue()+FullTextFields.ALTERNATE_NAME_SUFFIX.getValue());
	    solrResponseDto.adm1_names_alternate = getFieldsToList(solrDocument,
		    FullTextFields.ADM1NAME.getValue()+FullTextFields.ALTERNATE_NAME_SUFFIX.getValue());
	    solrResponseDto.adm2_names_alternate = getFieldsToList(solrDocument,
		    FullTextFields.ADM2NAME.getValue()+FullTextFields.ALTERNATE_NAME_SUFFIX.getValue());
	    solrResponseDto.country_names_alternate = getFieldsToList(solrDocument,
		    FullTextFields.COUNTRYNAME.getValue()+FullTextFields.ALTERNATE_NAME_SUFFIX.getValue());

	    solrResponseDto.name_alternates_localized = getFieldsToMap(solrDocument,
		    FullTextFields.NAME.getValue()+FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue());
	    solrResponseDto.adm1_names_alternate_localized = getFieldsToMap(solrDocument,
		    FullTextFields.ADM1NAME.getValue()+FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue());
	    solrResponseDto.adm2_names_alternate_localized = getFieldsToMap(solrDocument,
		    FullTextFields.ADM2NAME.getValue()+FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue());
	    solrResponseDto.country_names_alternate_localized = getFieldsToMap(
		    solrDocument,  FullTextFields.COUNTRYNAME.getValue()+FullTextFields.ALTERNATE_NAME_DYNA_SUFFIX.getValue());
	    //countryspecific
	    solrResponseDto.continent=getFieldAsString(solrDocument,
		    FullTextFields.CONTINENT.getValue());
	    solrResponseDto.currency_code = getFieldAsString(solrDocument,
		    FullTextFields.CURRENCY_CODE.getValue());
	    solrResponseDto.currency_name= getFieldAsString(solrDocument,
		    FullTextFields.CURRENCY_NAME.getValue());
	    solrResponseDto.fips_code= getFieldAsString(solrDocument,
		    FullTextFields.FIPS_CODE.getValue());
	    solrResponseDto.isoalpha2_country_code= getFieldAsString(solrDocument,
		    FullTextFields.ISOALPHA2_COUNTRY_CODE.getValue());
	    solrResponseDto.country_code= getFieldAsString(solrDocument,
		    FullTextFields.COUNTRYCODE.getValue());
	    solrResponseDto.isoalpha3_country_code= getFieldAsString(solrDocument,
		    FullTextFields.ISOALPHA3_COUNTRY_CODE.getValue());
	    solrResponseDto.postal_code_mask= getFieldAsString(solrDocument,
		    FullTextFields.POSTAL_CODE_MASK.getValue());
	    solrResponseDto.postal_code_regex= getFieldAsString(solrDocument,
		    FullTextFields.POSTAL_CODE_REGEX.getValue());
	    solrResponseDto.phone_prefix= getFieldAsString(solrDocument,
		    FullTextFields.PHONE_PREFIX.getValue());
	    solrResponseDto.spoken_languages=getFieldsToList(solrDocument,
		    FullTextFields.SPOKEN_LANGUAGES.getValue());
	    solrResponseDto.tld= getFieldAsString(solrDocument,
		    FullTextFields.TLD.getValue());
	    solrResponseDto.capital_name= getFieldAsString(solrDocument,
		    FullTextFields.CAPITAL_NAME.getValue());
	    solrResponseDto.area= getFieldAsDouble(solrDocument,
		    FullTextFields.AREA.getValue());
	    solrResponseDto.level= getFieldAsInteger(solrDocument,
		    FullTextFields.LEVEL.getValue());
	    solrResponseDto.amenity= getFieldAsString(solrDocument,
			    FullTextFields.AMENITY.getValue());
	    solrResponseDto.municipality= getFieldAsBoolean(solrDocument,
			    FullTextFields.MUNICIPALITY.getValue(),false);
	    
	    
	    //street specific
	    solrResponseDto.one_way = getFieldAsBoolean(solrDocument,
		    FullTextFields.ONE_WAY.getValue(),false);
	    solrResponseDto.length = getFieldAsDouble(solrDocument,
		    FullTextFields.LENGTH.getValue());
	    solrResponseDto.street_type = getFieldAsString(solrDocument,
		    FullTextFields.STREET_TYPE.getValue());
	    solrResponseDto.openstreetmap_id = getFieldAsLong(solrDocument, FullTextFields.OPENSTREETMAP_ID.getValue());
	    solrResponseDto.is_in = getFieldAsString(solrDocument, FullTextFields.IS_IN.getValue());
	    solrResponseDto.is_in_place = getFieldAsString(solrDocument, FullTextFields.IS_IN_PLACE.getValue());
	    solrResponseDto.is_in_zip = getFieldsToSet(solrDocument, FullTextFields.IS_IN_ZIP.getValue());
	    solrResponseDto.is_in_adm = getFieldAsString(solrDocument, FullTextFields.IS_IN_ADM.getValue());
	    solrResponseDto.fully_qualified_address = getFieldAsString(solrDocument, FullTextFields.FULLY_QUALIFIED_ADDRESS.getValue());
	    solrResponseDto.house_numbers=getHouseNumber(solrDocument);
	}
	return solrResponseDto;
    }
    
    
	private List<HouseNumberDto> getHouseNumber(SolrDocument solrDocument) {
		List<HouseNumberDto> housenumbers = new ArrayList<HouseNumberDto>();
		String fieldname = FullTextFields.HOUSE_NUMBERS.getValue();
		if (solrDocument.getFieldValues(fieldname) != null) {
			for (Object fieldValue : solrDocument.getFieldValues(fieldname)) {
				if (fieldValue == null) {
					continue;
				} else if (fieldValue instanceof String) {
					HouseNumberDto dto = houseNumberDeserializer
							.deserialize((String) fieldValue);
					housenumbers.add(dto);
				} else {
					throw new RepositoryException(fieldname
							+ " is not a String but a "
							+ fieldValue.getClass().getSimpleName());
				}
			}
		}
		return housenumbers;
	}
	
    private Map<String, List<String>> getFieldsToMap(SolrDocument solrDocument,
	    String fieldNamePrefix) {
	Map<String, List<String>> result = new HashMap<String, List<String>>();
	if (solrDocument.getFieldNames()!=null){
	for (String fieldName : solrDocument.getFieldNames()) {
	    if (fieldName.startsWith(fieldNamePrefix)) {
		for (Object fieldValue : solrDocument.getFieldValues(fieldName)) {
		    String fieldValueString = (String) fieldValue;
		    String languageCode = fieldName.substring(fieldName
			    .lastIndexOf("_") + 1);
		    List<String> languageList = result.get(languageCode);
		    if (languageList == null) {
			languageList = new ArrayList<String>();
			result.put(languageCode, languageList);
		    }
		    languageList.add(fieldValueString);
		}
	    }
	}
	}
	return result;
    }

    private List<String> getFieldsToList(SolrDocument solrDocument,
	    String fieldname) {
	List<String> list = new ArrayList<String>();
	if (solrDocument.getFieldValues(fieldname) != null) {
	    for (Object o : solrDocument.getFieldValues(fieldname)) {
		if (o == null) {
		    continue;
		} else if (o instanceof String) {
		    list.add(o.toString());
		} else {
		    throw new RepositoryException(fieldname
			    + " is not a String but a "
			    + o.getClass().getSimpleName());
		}
	    }
	}
	return list;
    }
    
    private Set<String> getFieldsToSet(SolrDocument solrDocument,
    	    String fieldname) {
    	Set<String> set = new HashSet<String>();
    	if (solrDocument.getFieldValues(fieldname) != null) {
    	    for (Object o : solrDocument.getFieldValues(fieldname)) {
    		if (o == null) {
    		    continue;
    		} else if (o instanceof String) {
    		    set.add(o.toString());
    		} else {
    		    throw new RepositoryException(fieldname
    			    + " is not a String but a "
    			    + o.getClass().getSimpleName());
    		}
    	    }
    	}
    	return set;
        }

    private Integer getFieldAsInteger(SolrDocument solrDocument,
	    String fieldname) {
	Object o = solrDocument.getFieldValue(fieldname);
	if (o == null) {
	    return null;
	} else if (o instanceof Integer) {
	    return (Integer) o;
	} else {
	    throw new RepositoryException(fieldname
		    + " is not an Integer but a "
		    + o.getClass().getSimpleName());
	}
    }
    

    private boolean getFieldAsBoolean(SolrDocument solrDocument,
	    String fieldname,boolean defaultValue) {
	Object o = solrDocument.getFieldValue(fieldname);
	if (o == null) {
	    return defaultValue;
	} else if (o instanceof Boolean) {
	    return (Boolean) o;
	} else {
	    throw new RepositoryException(fieldname
		    + " is not an Integer but a "
		    + o.getClass().getSimpleName());
	}
    }

    @SuppressWarnings("unused")
    private Float getFieldAsFloat(SolrDocument solrDocument, String fieldname) {
	Object o = solrDocument.getFieldValue(fieldname);
	if (o == null) {
	    return null;
	} else if (o instanceof Float) {
	    return (Float) o;
	} else {
	    throw new RepositoryException(fieldname + " is not a Float but a "
		    + o.getClass().getSimpleName());
	}
    }

    private Long getFieldAsLong(SolrDocument solrDocument, String fieldname) {
	Object o = solrDocument.getFieldValue(fieldname);
	if (o == null) {
	    return null;
	} else if (o instanceof Long) {
	    return (Long) o;
	} else {
	    throw new RepositoryException(fieldname + " is not a Long but a "
		    + o.getClass().getSimpleName());
	}
    }

    private Double getFieldAsDouble(SolrDocument solrDocument, String fieldname) {
	Object o = solrDocument.getFieldValue(fieldname);
	if (o == null) {
	    return null;
	} else if (o instanceof Double) {
	    return (Double) o;
	} else {
	    throw new RepositoryException(fieldname + " is not a Double but a "
		    + o.getClass().getSimpleName());
	}
    }

    private String getFieldAsString(SolrDocument solrDocument, String fieldname) {
	Object o = solrDocument.getFieldValue(fieldname);
	if (o == null) {
	    return null;
	} else if (o instanceof String) {
	    return (String) o;
	} else {
	    throw new RepositoryException(fieldname + " is not a String but a "
		    + o.getClass().getSimpleName());
	}
    }


}
