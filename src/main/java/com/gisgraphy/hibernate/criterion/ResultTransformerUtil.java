/**
 * ResultTransformerUtil.java
 * 
 * Mercer Inc.
 * JBossMHR
 * Copyright 2008 All Rights Reserved
 * @since 1.0 May 14, 2008
 * =============================================================================================
 * $Id: ResultTransformerUtil.java,v 1.1 2008/05/14 14:44:23 abhishekm Exp $
 * =============================================================================================
 */
package com.gisgraphy.hibernate.criterion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;

import com.gisgraphy.domain.valueobject.GisFeatureDistance;
import com.gisgraphy.domain.valueobject.GisFeatureDistanceFactory;
import com.gisgraphy.domain.valueobject.StreetDistance;

/**
 * The Class ResultTransformerUtil.
 * 
 * @author Abhishek Mirge
 */
public class ResultTransformerUtil<T> {

    
    	protected static GisFeatureDistanceFactory gisFeatureDistanceFactory = new GisFeatureDistanceFactory();
	/**
	 * Transform to bean. See bug
	 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2463
	 * 
	 * @param aliasList
	 *                the alias list
	 * @param resultList
	 *                the result list
	 * 
	 * @return the list of GisFeatureDistance
	 */
	//TODO tests zip test
	public static List<GisFeatureDistance> transformToGisFeatureDistance(String aliasList[], List<?> resultList, Map<Long, Set<String>> featureIdToZipCodesMap,Class clazz) {
		List<GisFeatureDistance> results = new ArrayList<GisFeatureDistance>();
		if (aliasList != null && !resultList.isEmpty()) {
			ResultTransformer tr = new AliasToBeanResultTransformer(GisFeatureDistance.class);
			Iterator<?> it = resultList.iterator();
			Object[] obj;
			GisFeatureDistance gisFeatureDistance;
			while (it.hasNext()) {
				obj = (Object[]) it.next();
				gisFeatureDistance = (GisFeatureDistance) tr.transformTuple(obj, aliasList);
				int indexInList = results.indexOf(gisFeatureDistance);
				if (indexInList == -1) {
				    gisFeatureDistanceFactory.updateFields(gisFeatureDistance,clazz);
					results.add(gisFeatureDistance);
					if (featureIdToZipCodesMap != null){
					    gisFeatureDistance.setZipCodes(featureIdToZipCodesMap.get(gisFeatureDistance.getId()));
					}
				}
			}
		}

		return results;
	}

	/**
	 * Transform to bean. See bug
	 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2463
	 * 
	 * @param aliasList
	 *                the alias list
	 * @param resultList
	 *                the result list
	 * 
	 * @return the list of {@link StreetDistance}
	 */
	public static List<StreetDistance> transformToStreetDistance(String aliasList[], List<?> resultList) {
		List<StreetDistance> transformList = new ArrayList<StreetDistance>();
		if (aliasList != null && !resultList.isEmpty()) {
			AliasToBeanResultTransformer tr = new AliasToBeanResultTransformer(StreetDistance.class);
			Iterator<?> it = resultList.iterator();
			Object[] obj;
			while (it.hasNext()) {
				obj = (Object[]) it.next();
				StreetDistance streetDistance = (StreetDistance) tr.transformTuple(obj, aliasList);
				streetDistance.updateFields();
				transformList.add(streetDistance);
			}
		}
		return transformList;
	}

}
