package com.gisgraphy.importer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.importer.MunicipalityDetector.MunicipalityDetectionStrategy;

public class MunicipalityDetectorTest {

	@Test
	public void testIsMunicipalityForGisFeature() {
		IMunicipalityDetector d = new MunicipalityDetector();
		GisFeature gisFeature = new GisFeature();
		gisFeature.setAdm1Code("a");
		gisFeature.setAdm2Code("a");
		gisFeature.setAdm3Code("a");
		gisFeature.setAdm4Code("a");
		gisFeature.setAdm5Code("a");
		gisFeature.setPopulation(1);
		assertFalse(d.isMunicipalityByGisFeature("",gisFeature));
		assertFalse(d.isMunicipalityByGisFeature(null, gisFeature));
		assertFalse(d.isMunicipalityByGisFeature("NOTEXISTING",gisFeature));
		assertFalse(d.isMunicipalityByGisFeature("IT", null));

		assertTrue(d.isMunicipalityByGisFeature("it",  gisFeature));
		assertTrue(d.isMunicipalityByGisFeature("IT", gisFeature));
		
	}
	
	@Test
	public void testIsMunicipality(){
		//test ALL
		IMunicipalityDetector d = new MunicipalityDetector();
		Assert.assertTrue(d.isMunicipality(null, null, null, null));
		
		Assert.assertTrue(d.isMunicipality("IN", null, null, GISSource.GEONAMES));
		Assert.assertTrue(d.isMunicipality("IN", null, null, GISSource.OSM));
		
		//test OSM
		Assert.assertFalse(d.isMunicipality("CN", null, null, GISSource.GEONAMES));
		Assert.assertTrue(d.isMunicipality("CN", null, null, GISSource.OSM));
		
		//test R
		Assert.assertFalse(d.isMunicipality("IT", null, null, GISSource.GEONAMES));
		Assert.assertFalse("Even if it is R, it should be osm feature",d.isMunicipality("IT", null, "R", GISSource.GEONAMES));
		
		Assert.assertFalse(d.isMunicipality("IT", null, null, GISSource.OSM));
		Assert.assertFalse(d.isMunicipality("IT", null, "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("IT", null, "r", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("IT", null, "R", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("IT", null, "w", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("IT", null, "W", GISSource.OSM));
		
		//N
		Assert.assertFalse(d.isMunicipality("FI", null, null, GISSource.GEONAMES));
		Assert.assertFalse("Even if it is N, it should be osm feature",d.isMunicipality("FI", null, "N", GISSource.GEONAMES));
		
		Assert.assertFalse(d.isMunicipality("PY", null, null, GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("PY", null, "n", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("PY", null, "N", GISSource.OSM));
		
		//R_AND_N_CITY_VILLAGE_TOWN
		//R
		Assert.assertFalse(d.isMunicipality("SK", null, null, GISSource.GEONAMES));
		Assert.assertFalse("Even if it is r, it should be osm feature",d.isMunicipality("SK", null, "R", GISSource.GEONAMES));
		Assert.assertFalse("Even if it is N, it should be osm feature",d.isMunicipality("SK", null, "N", GISSource.GEONAMES));
		Assert.assertTrue(d.isMunicipality("SK", null, "r", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", null, "R", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", null, "w", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", null, "W", GISSource.OSM));
		//N
		Assert.assertFalse("Geonames should be ignore",d.isMunicipality("SK", "CiTy", "N", GISSource.GEONAMES));
		Assert.assertFalse("null type is not city,village,town",d.isMunicipality("SK", null, "N", GISSource.OSM));
		Assert.assertFalse("foo type is not city,village,town",d.isMunicipality("SK", "foo", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", "CiTy", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", "ViLLaGe", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", "ToWn", "W", GISSource.OSM));
		
		//N_CITY_VILLAGE_TOWN
		Assert.assertFalse("Geonames should be ignore",d.isMunicipality("TR", "CiTy", "N", GISSource.GEONAMES));
		Assert.assertFalse("null type is not city,village,town",d.isMunicipality("TR", null, "N", GISSource.OSM));
		Assert.assertFalse("foo type is not city,village,town",d.isMunicipality("TR", "foo", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("TR", "CiTy", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("TR", "ViLLaGe", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("SK", "ToWn", "W", GISSource.OSM));
		Assert.assertFalse("not a N",d.isMunicipality("TR", "CiTy", "R", GISSource.OSM));
		
		//N_CITY_TOWN
		Assert.assertFalse("Geonames should be ignore",d.isMunicipality("TW", "CiTy", "N", GISSource.GEONAMES));
		Assert.assertFalse("null type is not city,village,town",d.isMunicipality("TW", null, "N", GISSource.OSM));
		Assert.assertFalse("foo type is not city,village,town",d.isMunicipality("TW", "foo", "N", GISSource.OSM));
		Assert.assertTrue(d.isMunicipality("TW", "CiTy", "N", GISSource.OSM));
		Assert.assertFalse(d.isMunicipality("TW", "ViLLaGe", "N", GISSource.OSM));
		Assert.assertFalse("not a N",d.isMunicipality("TW", "ToWn", "W", GISSource.OSM));
		Assert.assertFalse("not a N",d.isMunicipality("TW", "CiTy", "R", GISSource.OSM));
		
		
	}
	
	@Test
	public void testIsMunicipality_internal() {
		MunicipalityDetector d = new MunicipalityDetector();
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION, 0, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM1CODE, 0, "a", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM1CODE, 1, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM2CODE, 0, "", "a", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM2CODE, 0, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM3CODE, 0, "", "", "a", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM3CODE, 0, "", "", "", "", ""));

		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM4CODE, 0, "", "", "", "a", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM4CODE, 0, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM5CODE, 0, "", "", "", "", "a"));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.ADM5CODE, 0, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM1CODE, 0, "a", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM1CODE, 1, "a", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM1CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM1CODE, 0, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM2CODE, 0, "", "a", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM2CODE, 1, "", "a", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM2CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM2CODE, 0, "", "", "", "", ""));
	
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM3CODE, 0, "a", "", "a", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM3CODE, 1, "a", "", "a", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM3CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM3CODE, 0, "", "", "", "", ""));
		
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM4CODE, 0, "a", "", "", "a", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM4CODE, 1, "a", "", "", "a", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM4CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM4CODE, 0, "", "", "", "", ""));

		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM5CODE, 0, "a", "", "", "", "a"));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM5CODE, 1, "a", "", "", "", "a"));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM5CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_OR_ADM5CODE, 0, "", "", "", "", ""));
		
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM1CODE, 0, "a", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM1CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM1CODE, 0, "", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM1CODE, 1, "a", "", "", "", ""));

		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM2CODE, 0, "", "a", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM2CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM2CODE, 0, "", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM2CODE, 1, "", "a", "", "", ""));
		
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM3CODE, 0, "", "", "a", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM3CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM3CODE, 0, "", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM3CODE, 1, "", "", "a", "", ""));
		
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM4CODE, 0, "", "", "", "a", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM4CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM4CODE, 0, "", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM4CODE, 1, "", "", "", "a", ""));
		
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM5CODE, 0, "", "", "", "", "a"));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM5CODE, 1, "", "", "", "", ""));
		assertFalse(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM5CODE, 0, "", "", "", "", ""));
		assertTrue(d.isMunicipality_internal(MunicipalityDetectionStrategy.POPULATION_AND_ADM5CODE, 1, "", "", "", "", "a"));
		
	}

}
