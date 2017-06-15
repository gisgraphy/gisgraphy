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
package com.gisgraphy.webapp.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.repository.AdmDao;
import com.gisgraphy.domain.repository.CityDao;
import com.gisgraphy.domain.repository.HouseNumberDao;
import com.gisgraphy.domain.repository.IGisDao;
import com.gisgraphy.domain.repository.IOpenStreetMapDao;
import com.gisgraphy.helper.countryInfo;
import com.gisgraphy.serializer.UniversalSerializer;
import com.gisgraphy.serializer.common.OutputFormat;
import com.gisgraphy.service.IStatsUsageService;
import com.gisgraphy.stats.StatsDataDTO;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * stats Action
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 */
public class StatsDataAction extends ActionSupport {

	private static Logger logger = LoggerFactory
			.getLogger(StatsDataAction.class);

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

	public boolean refresh = false;

	public String countrycode;

	/**
	 * @return the countrycode
	 */
	public String getCountrycode() {
		return countrycode;
	}

	/**
	 * @param countrycode the countrycode to set
	 */
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	/**
	 * @return the refresh
	 */
	public boolean isRefresh() {
		return refresh;
	}

	/**
	 * @param refresh the refresh to set
	 */
	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	public String alljson() throws Exception {
		if (statsByCountry!=null && !statsByCountry.isEmpty() && !refresh){
			logger.info("stats are alredy processed and in cache");
			statsAsJson = UniversalSerializer.getInstance().writeToString(statsByCountry, OutputFormat.JSON);
			return Action.SUCCESS;
		}
		Map<String, List<StatsDataDTO>> localStatsByCountry = new HashMap<String, List<StatsDataDTO>>();
		Set<String> countries = countryInfo.countryLookupMap.keySet();
		int counter = 0;
		for (String country: countries){
			counter++;
			logger.info("stats "+counter+"/"+countries.size());
			List<StatsDataDTO> countryStats = one(country);
			localStatsByCountry.put(country, countryStats);
		}
		statsByCountry = localStatsByCountry;
		statsAsJson = UniversalSerializer.getInstance().writeToString(statsByCountry, OutputFormat.JSON);
		return Action.SUCCESS;
	}


	public String onejson() throws Exception {
		if (countrycode==null){
			logger.info("not countrycode specified");
			statsAsJson = "{}";
			return Action.SUCCESS;
		}
		countrycode=countrycode.toUpperCase();
		if (statsByCountry!=null && !statsByCountry.isEmpty() && !refresh){
			logger.info("stats are alredy processed and in cache");
			List<StatsDataDTO> list = statsByCountry.get(countrycode);
			if (list==null){
				statsAsJson = "{}";
			}else {
				statsAsJson = UniversalSerializer.getInstance().writeToString(list, OutputFormat.JSON);
			}
			return Action.SUCCESS;
		}
		List<StatsDataDTO> countryStats = one(countrycode);
		statsAsJson = UniversalSerializer.getInstance().writeToString(countryStats, OutputFormat.JSON);
		return Action.SUCCESS;
	}

	private List<StatsDataDTO> one(String country) {
		List<StatsDataDTO> countryStats = new ArrayList<StatsDataDTO>();
		logger.info("process stats for country "+country);
		long streets = openStreetMapDao.countByCountryCode(country);
		logger.info("counting stats for streets : "+ streets);
		countryStats.add(new StatsDataDTO("Streets", streets));

		long houses = houseNumberDao.countByCountryCode(country);
		logger.info("counting stats for houses : "+ houses);
		countryStats.add(new StatsDataDTO("Addresses", houses));

		long cities = cityDao.countByCountryCode(country);
		logger.info("counting stats for cities : "+ cities);
		countryStats.add(new StatsDataDTO("Cities", cities));

		long adms = admDao.countByCountryCode(country);
		logger.info("counting stats for adms : "+ adms);
		countryStats.add(new StatsDataDTO("Adms", adms));

		//then poi
		long globalcount = 0;
		for (int i = 0; i < daos.length; i++) {
			IGisDao<? extends GisFeature> dao = daos[i];
			if (dao.getPersistenceClass()== City.class || dao.getPersistenceClass()== Adm.class){
				continue;
			}
			long count = dao.countByCountryCode(country);
			logger.info("counting stats for pois:"+dao.getPersistenceClass().getSimpleName()+" : "+ count);
			globalcount = globalcount+ count;
			countryStats.add(new StatsDataDTO("Pois:"+dao.getPersistenceClass().getSimpleName(), count));
		}
		countryStats.add(new StatsDataDTO("Pois:all", globalcount));
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
