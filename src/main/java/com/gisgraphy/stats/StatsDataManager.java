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

package com.gisgraphy.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.Country;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.AdmDao;
import com.gisgraphy.domain.repository.CityDao;
import com.gisgraphy.domain.repository.HouseNumberDao;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.helper.CountryInfo;
import com.gisgraphy.serializer.UniversalSerializer;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.IStatsUsageService;

/**
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
@Service
public class StatsDataManager implements IStatsDataManager{





	private static final String ADMS_LABEL = "Adms";

	private static final String POIS_ALL_LABEL = "Pois:all";

	private static final String CITIES_LABEL = "Cities";

	private static final String ADDRESSES_LABEL = "Addresses";

	private static final String STREETS_LABEL = "Streets";
	
	private static final String STREETS_SHAPE_LABEL = "Streets";
	
	private static final String CITIES_SHAPE_LABEL = "Streets";
	
	private static final String ADMS_SHAPE_LABEL = "Streets";

	private static Logger logger = LoggerFactory
			.getLogger(StatsDataManager.class);

	/**
	 * Default serialId
	 */
	private static final long serialVersionUID = -9018894533914543310L;

	@Autowired
	protected IGisDao<? extends GisFeature>[] daos;

	@Autowired
	protected IOpenStreetMapDao openStreetMapDao;

	@Autowired
	protected HouseNumberDao houseNumberDao;

	@Autowired
	protected CityDao cityDao;


	@Autowired
	protected AdmDao admDao;

	public static Map<String, List<StatsDataDTO>> statsByCountry = new HashMap<String, List<StatsDataDTO>>();

	String statsAsJson = "{}";

	String EXPORT_DIRECTORY= "./data/export/stats/";


	/**
	 *  export one file per country with details stats
	 */
	@Override
	public void exportEachCountriesInJson() {
		File directory = new File(EXPORT_DIRECTORY);
		if (! directory.exists()){
			directory.mkdir();
		}


		Set<String> countries = CountryInfo.countryLookupMap.keySet();
		int counter = 0;
		for (String country: countries){
			counter++;
			logger.info("stats "+counter+"/"+countries.size());
			List<StatsDataDTO> countryStats = processCountryCode(country,true);
			String json = UniversalSerializer.getInstance().writeToString(countryStats, OutputFormat.JSON);
			File file = new File(EXPORT_DIRECTORY + "/" + country+".json");
			try{
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(json);
				bw.close();
			}
			catch (IOException e){
				logger.error("can not write stats feeds for "+country+" : "+e.getMessage());
			}
		}
	}
/**
 * export ONE file with all the details stats for all the countries
 */
	@Override
	public void exportAllInJson() {
		File directory = new File(EXPORT_DIRECTORY);
		if (! directory.exists()){
			directory.mkdir();
		}
		String json = getAllInJson(true,true);
		File file = new File(EXPORT_DIRECTORY + "/" + "all.json");
		try{
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);
			bw.close();
		}
		catch (IOException e){
			logger.error("can not write stats feeds for all countries : "+e.getMessage());
		}
	}
	
	/**
	 * export one file with all the not details stats for all the countries
	 */
	@Override
	public void exportAllInJsonNoDetails() {
		File directory = new File(EXPORT_DIRECTORY);
		if (! directory.exists()){
			directory.mkdir();
		}
		String json = getAllInJson(true,false);
		File file = new File(EXPORT_DIRECTORY + "/" + "all_nodetails.json");
		try{
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);
			bw.close();
		}
		catch (IOException e){
			logger.error("can not write stats feeds for all countries : "+e.getMessage());
		}
	}

	/**
	 * export one file with a summary of all stats per category (streets, city,..)
	 */
	@Override
	public void exportAllSummaryInJson() {

		File directory = new File(EXPORT_DIRECTORY);
		if (! directory.exists()){
			directory.mkdir();
		}
		List<StatsDataDTO> stats  = getAllSummary(true);
		String json = UniversalSerializer.getInstance().writeToString(stats, OutputFormat.JSON);
		File file = new File(EXPORT_DIRECTORY + "/" + "all_summary.json");
		try{
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);
			bw.close();
		}
		catch (IOException e){
			logger.error("can not write stats feeds for all countries : "+e.getMessage());
		}
	}


	@Override
	public void exportStats(){
		exportAllInJson();
		exportAllInJsonNoDetails();
		exportEachCountriesInJson();
		exportAllSummaryInJson();
	}

	public String getAllInJson(boolean refresh,boolean withdetails) {
		getAll(refresh,withdetails);
		statsAsJson = UniversalSerializer.getInstance().writeToString(statsByCountry, OutputFormat.JSON);
		return statsAsJson;
	}

	private Map<String, List<StatsDataDTO>> getAll(boolean refresh, boolean withdetails) {
		if (statsByCountry!=null && !statsByCountry.isEmpty() && !refresh){
			logger.info("stats are alredy processed and in cache");
			return statsByCountry;
		}
		Map<String, List<StatsDataDTO>> localStatsByCountry = new HashMap<String, List<StatsDataDTO>>();
		Set<String> countries = CountryInfo.countryLookupMap.keySet();
		int counter = 0;
		for (String country: countries){
			counter++;
			logger.info("stats "+counter+"/"+countries.size());
			List<StatsDataDTO> countryStats = processCountryCode(country,withdetails);
			localStatsByCountry.put(country, countryStats);
		}
		statsByCountry = localStatsByCountry;
		return statsByCountry;
	}

	@Override
	public List<StatsDataDTO> getAllSummary(boolean refresh) {
		statsByCountry = getAll(refresh,true);
		long streets = 0;
		long cities = 0;
		long pois = 0;
		long addresses = 0;
		long adms = 0;
		for (String country:statsByCountry.keySet()){
			List<StatsDataDTO> dtos = statsByCountry.get(country);
			if (dtos != null){
				for(StatsDataDTO stat : dtos){
					if (stat.getLabel().equals(STREETS_LABEL)){
						streets = streets+stat.getStat();
					} else if(stat.getLabel().equals(CITIES_LABEL)){
						cities =cities+stat.getStat();
					} else if(stat.getLabel().equals(ADDRESSES_LABEL)){
						addresses =addresses+stat.getStat();
					}  else if (stat.getLabel().equals(ADMS_LABEL)){
						adms =adms+stat.getStat();
					}  else if (stat.getLabel().equals(POIS_ALL_LABEL)){
						pois =pois+stat.getStat();
					} 
				}
			}
		}
		List<StatsDataDTO> stats = new ArrayList<StatsDataDTO>();
		stats.add(new StatsDataDTO(ADDRESSES_LABEL,addresses));
		stats.add(new StatsDataDTO(STREETS_LABEL,streets));
		stats.add(new StatsDataDTO(CITIES_LABEL,cities));
		stats.add(new StatsDataDTO(ADMS_LABEL,adms));
		stats.add(new StatsDataDTO(POIS_ALL_LABEL,pois));

		return stats;
	}


	public String getOneInJson(String countryCode,boolean refresh)  {
		if (countryCode==null){
			logger.info("not countrycode specified");
			statsAsJson = "{}";
			return statsAsJson;
		}
		countryCode=countryCode.toUpperCase();
		if (statsByCountry!=null && !statsByCountry.isEmpty() && !refresh){
			logger.info("stats are alredy processed and in cache");
			List<StatsDataDTO> list = statsByCountry.get(countryCode);
			if (list==null){
				statsAsJson = "{}";
			}else {
				statsAsJson = UniversalSerializer.getInstance().writeToString(list, OutputFormat.JSON);
			}
			return statsAsJson;
		}
		List<StatsDataDTO> countryStats = processCountryCode(countryCode,true);
		statsAsJson = UniversalSerializer.getInstance().writeToString(countryStats, OutputFormat.JSON);
		return statsAsJson;
	}

	@Override
	public List<StatsDataDTO> processCountryCode(String country,boolean withdetails) {
		List<StatsDataDTO> countryStats = new ArrayList<StatsDataDTO>();
		logger.info("process stats for country "+country);
		long streets = openStreetMapDao.countByCountryCode(country);
		logger.info("counting stats for streets : "+ streets);
		countryStats.add(new StatsDataDTO(STREETS_LABEL, streets));
		
		long streetsWithShape = openStreetMapDao.countByCountryCode(country);
		logger.info("counting stats for shape streets : "+ streetsWithShape);
		countryStats.add(new StatsDataDTO(STREETS_SHAPE_LABEL, streetsWithShape));
		
		long houses = houseNumberDao.countByCountryCode(country);
		logger.info("counting stats for houses : "+ houses);
		countryStats.add(new StatsDataDTO(ADDRESSES_LABEL, houses));

		long cities = cityDao.countByCountryCode(country);
		logger.info("counting stats for cities : "+ cities);
		countryStats.add(new StatsDataDTO(CITIES_LABEL, cities));
		
		long citiesWithShape = cityDao.countShapeByCountryCode(country);
		logger.info("counting stats for cities shapes: "+ citiesWithShape);
		countryStats.add(new StatsDataDTO(CITIES_SHAPE_LABEL, citiesWithShape));

		long adms = admDao.countByCountryCode(country);
		logger.info("counting stats for adms : "+ adms);
		countryStats.add(new StatsDataDTO(ADMS_LABEL, adms));
		
		long admsWithShape = admDao.countByCountryCode(country);
		logger.info("counting stats for adms shapes: "+ admsWithShape);
		countryStats.add(new StatsDataDTO(ADMS_SHAPE_LABEL, admsWithShape));

		//then poi
		long globalcount = 0;
		for (int i = 0; i < daos.length; i++) {
			IGisDao<? extends GisFeature> dao = daos[i];
			if (dao.getPersistenceClass()== City.class || dao.getPersistenceClass()== Adm.class || dao.getPersistenceClass()== Country.class){
				continue;
			}
			long count = dao.countByCountryCode(country);
			if (dao.getPersistenceClass()== GisFeature.class){
				count--;//remove the country itself
			}
			logger.info("counting stats for pois:"+dao.getPersistenceClass().getSimpleName()+" : "+ count);
			globalcount = globalcount+ count;
			if (withdetails){
			countryStats.add(new StatsDataDTO("Pois:"+dao.getPersistenceClass().getSimpleName(), count));
			}
		}
		countryStats.add(new StatsDataDTO(POIS_ALL_LABEL, globalcount));
		logger.info("counting stats for pois:all : "+ globalcount);
		return countryStats;
	}

	/**
	 * @return the statsAsJson
	 */
	public String getStatsAsJson() {
		return statsAsJson;
	}

	/**
	 * @return the daos
	 */
	public IGisDao<? extends GisFeature>[] getDaos() {
		return daos;
	}

	/**
	 * @param daos the daos to set
	 */
	public void setDaos(IGisDao<? extends GisFeature>[] daos) {
		this.daos = daos;
	}

	/**
	 * @return the openStreetMapDao
	 */
	public IOpenStreetMapDao getOpenStreetMapDao() {
		return openStreetMapDao;
	}

	/**
	 * @param openStreetMapDao the openStreetMapDao to set
	 */
	public void setOpenStreetMapDao(IOpenStreetMapDao openStreetMapDao) {
		this.openStreetMapDao = openStreetMapDao;
	}

	/**
	 * @return the houseNumberDao
	 */
	public HouseNumberDao getHouseNumberDao() {
		return houseNumberDao;
	}

	/**
	 * @param houseNumberDao the houseNumberDao to set
	 */
	public void setHouseNumberDao(HouseNumberDao houseNumberDao) {
		this.houseNumberDao = houseNumberDao;
	}

	/**
	 * @return the cityDao
	 */
	public CityDao getCityDao() {
		return cityDao;
	}

	/**
	 * @param cityDao the cityDao to set
	 */
	public void setCityDao(CityDao cityDao) {
		this.cityDao = cityDao;
	}

	/**
	 * @return the admDao
	 */
	public AdmDao getAdmDao() {
		return admDao;
	}

	/**
	 * @param admDao the admDao to set
	 */
	public void setAdmDao(AdmDao admDao) {
		this.admDao = admDao;
	}

	/**
	 * @return the statsByCountry
	 */
	public Map<String, List<StatsDataDTO>> getStatsByCountry() {
		return statsByCountry;
	}


	public int getFlushFrequency(){
		return IStatsUsageService.FLUSH_THRESHOLD;
	}





}
