package com.gisgraphy.importer;

import static com.gisgraphy.fulltext.Constants.ONLY_CITY_PLACETYPE;
import static com.gisgraphy.importer.OpenStreetMapCitiesSimpleImporter.MINIMUM_OUTPUT_STYLE;
import static com.gisgraphy.test.GisgraphyTestHelper.alternateNameContains;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.Adm;
import com.gisgraphy.domain.geoloc.entity.AlternateName;
import com.gisgraphy.domain.geoloc.entity.City;
import com.gisgraphy.domain.geoloc.entity.CitySubdivision;
import com.gisgraphy.domain.geoloc.entity.GisFeature;
import com.gisgraphy.domain.geoloc.entity.ZipCode;
import com.gisgraphy.domain.repository.CitySubdivisionDao;
import com.gisgraphy.domain.repository.IAdmDao;
import com.gisgraphy.domain.repository.ICityDao;
import com.gisgraphy.domain.repository.ICitySubdivisionDao;
import com.gisgraphy.domain.repository.IGisFeatureDao;
import com.gisgraphy.domain.repository.IIdGenerator;
import com.gisgraphy.domain.valueobject.AlternateNameSource;
import com.gisgraphy.domain.valueobject.GISSource;
import com.gisgraphy.domain.valueobject.Pagination;
import com.gisgraphy.fulltext.Constants;
import com.gisgraphy.fulltext.FulltextQuery;
import com.gisgraphy.fulltext.FulltextResultsDto;
import com.gisgraphy.fulltext.IFullTextSearchEngine;
import com.gisgraphy.fulltext.SolrResponseDto;
import com.gisgraphy.helper.GeolocHelper;
import com.vividsolutions.jts.geom.Point;

public class OpenStreetMapCitiesSimpleImporterTest {
	
	LabelGenerator generator = LabelGenerator.getInstance();

	
	@Test
	public void testNoName(){
		
	
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
		EasyMock.replay(idGenerator);
		importer.setIdGenerator(idGenerator);
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		
		String line= "R\t6530243\t3009735615\t\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
		EasyMock.verify(admDao);
		EasyMock.verify(idGenerator);
	
	}
	
	@Test
	public void parsePopulation(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		Assert.assertEquals(5000,importer.parsePopulation("5000"));
		Assert.assertEquals(5000,importer.parsePopulation("5 000"));
		Assert.assertEquals(5000,importer.parsePopulation("5,000"));
		
	}
	
	@Test
	public void populatezip(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		City city = new City();
		importer.populateZip("23456", city);
		Assert.assertEquals(1,city.getZipCodes().size());
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("23456")));
		
		city = new City();
		importer.populateZip("23456,789", city);
		Assert.assertEquals(2,city.getZipCodes().size());
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("23456")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("789")));
		
		city = new City();
		importer.populateZip("23456|789", city);
		Assert.assertEquals(2,city.getZipCodes().size());
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("23456")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("789")));
		
		city = new City();
		importer.populateZip("23456;789", city);
		Assert.assertEquals(2,city.getZipCodes().size());
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("23456")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("789")));
		
		city = new City();
		importer.populateZip("23456;789;", city);
		Assert.assertEquals(2,city.getZipCodes().size());
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("23456")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("789")));
		
		//test cumulative and deduplcate
		city = new City();
		importer.populateZip("23456;789;", city);
		importer.populateZip("1011;12;789;75009 cedex", city);
		Assert.assertEquals(4,city.getZipCodes().size());
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("23456")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("789")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("1011")));
		Assert.assertTrue(city.getZipCodes().contains(new ZipCode("12")));
	}
	
	@Test
	public void createNewCity() {
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
		importer.idGenerator= idGenerator;
		
		Point location = GeolocHelper.createPoint(3D, 2D);
		Point locationAdminCentre = GeolocHelper.createPoint(4D, 8D);
		
		City actual = importer.createNewCity("name","FR",location ,locationAdminCentre);
		Assert.assertEquals(GISSource.OSM, actual.getSource());
		Assert.assertEquals(1234L, actual.getFeatureId().longValue());
		Assert.assertEquals("name", actual.getName());
		Assert.assertEquals("FR", actual.getCountryCode());
		Assert.assertEquals(location, actual.getLocation());
		Assert.assertEquals(locationAdminCentre, actual.getAdminCentreLocation());
		
	}
	
	@Test
	public void createNewPoi() {
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
    	EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(1234L);
    	EasyMock.replay(idGenerator);
    	importer.setIdGenerator(idGenerator);
		importer.idGenerator= idGenerator;
		
		Point location = GeolocHelper.createPoint(3D, 2D);
		Point locationAdminCentre = GeolocHelper.createPoint(4D, 8D);
		
		GisFeature actual = importer.createNewPoi("name","FR",location ,locationAdminCentre);
		Assert.assertEquals(GISSource.OSM, actual.getSource());
		Assert.assertEquals(1234L, actual.getFeatureId().longValue());
		Assert.assertEquals("name", actual.getName());
		Assert.assertEquals("FR", actual.getCountryCode());
		Assert.assertEquals(location, actual.getLocation());
		Assert.assertEquals(locationAdminCentre, actual.getAdminCentreLocation());
		
	}
	
	@Test
	public void populateAlternateNames() {
		
		String RawAlternateNames="";
		GisFeature poi = new GisFeature();
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		
		RawAlternateNames ="\"{\"\"\"\",name:genitive===Pełczyczyc___short_name===pełczycki}\"";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(2,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Pełczyczyc","GENITIVE"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"pełczycki",null));
		
		
		RawAlternateNames="\"{\"\"name:af===Parys t___name:am===ፓሪስ___name:an===París___alt_name:fr===Париж\"\"}\"";
		//RawAlternateNames ="\"{\"\"name:ca===Sant Andreu de Sueda___name:fr===Saint-André___name:oc===Sant Andreu de Sueda\"\",\"\"name:ca===Sant Andreu de Sueda___name:oc===Sant Andreu de Sueda\"\"}\"";
		 poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(4, poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Parys t","AF"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"ፓሪስ","AM"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"París","AN"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Париж","FR"));
		
		Iterator<AlternateName> iterator = poi.getAlternateNames().iterator();
		while (iterator.hasNext()){
			Assert.assertEquals(AlternateNameSource.OPENSTREETMAP,iterator.next().getSource());
		}
		RawAlternateNames ="\"{\"\"\"\"}\"";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertNull(poi.getAlternateNames());
		
		RawAlternateNames ="\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(2,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Pełczyczyc","GENITIVE"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"pełczycki","ADJECTIVE"));
		
		
		
		//w comma, one duplicate w same name and one with different for the same lang
		RawAlternateNames ="{name:CA===Argelers___name:fr===Argelès-sur-Mer___name:oc===Argelersoc,name:ca===Argelers___name:oc===Argelers}";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(4,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelès-sur-Mer","FR"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelersoc","OC"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","OC"));
		
		
		RawAlternateNames ="{name:CA===Argelers___name:fr===Argelès-sur-Mer___name:oc===Argelersoc1,Argelersoc2,name:ca===Argelers___name:oc===Argelers}";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(5,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelès-sur-Mer","FR"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelersoc1","OC"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelersoc2","OC"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","OC"));
		
		//w comma and alt_name after coma
		RawAlternateNames ="{name:CA===Argelers___name:fr===Argelès-sur-Mer___name:oc===Argelersoc3,Argelersoc4,alt_name:ca===Argelers___name:oc===Argelers}";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(5,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelès-sur-Mer","FR"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelersoc3","OC"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelersoc4","OC"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","OC"));
		
			
		//w comma and no duplicates 
		RawAlternateNames ="{name:ca===Argelers___name:fr===Argelès-sur-Mer___name:oc===Argelers,name:xx===Argelers___name:yy===Argelers}";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(5,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelès-sur-Mer","FR"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","OC"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","XX"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Argelers","YY"));
		
		
		
		RawAlternateNames ="\"{\"\"name:ca===Sant Andreu de Sueda1,Sant Andreu de Sueda2___name:fr===Saint-André___name:oc===Sant Andreu de Sueda\"\",\"\"name:ca===Sant Andreu de Sueda___name:oc===Sant Andreu de Sueda\"\"}\"";
		poi = new GisFeature();
		poi = importer.populateAlternateNames(poi, RawAlternateNames);
		Assert.assertEquals(5,poi.getAlternateNames().size());
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Sant Andreu de Sueda1","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Sant Andreu de Sueda2","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Saint-André","FR"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Sant Andreu de Sueda","CA"));
		Assert.assertTrue(alternateNameContains(poi.getAlternateNames(),"Sant Andreu de Sueda","OC"));
		
	
		
	}
	
	
	

	@Test
	public void getNearestCity(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		
		List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
		SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDto.getScore()).andReturn(OpenStreetMapCitiesSimpleImporter.SCORE_LIMIT+0.2F);
		EasyMock.expect(solrResponseDto.getOpenstreetmap_id()).andReturn(null);
		EasyMock.replay(solrResponseDto);
		results.add(solrResponseDto);
		FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
		EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
		EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
		EasyMock.replay(mockResultDTO);
		
		
		String text = "toto";
		String countryCode = "FR";
		Point location = GeolocHelper.createPoint(3F, 4F);
		IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		FulltextQuery query = new FulltextQuery(text, Pagination.ONE_RESULT, OpenStreetMapCitiesSimpleImporter.MINIMUM_OUTPUT_STYLE, ONLY_CITY_PLACETYPE, countryCode);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
		EasyMock.replay(mockfullFullTextSearchEngine);
		
		importer.setFullTextSearchEngine(mockfullFullTextSearchEngine);
		
		SolrResponseDto actual = importer.getNearestByPlaceType(location, text, countryCode,ONLY_CITY_PLACETYPE);
		Assert.assertEquals(solrResponseDto, actual);
		EasyMock.verify(mockfullFullTextSearchEngine);
	}
	
	@Test
	public void getNearestCity_openstreetmapidNotNull(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		
		List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
		SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDto.getScore()).andReturn(OpenStreetMapCitiesSimpleImporter.SCORE_LIMIT+0.2F);
		EasyMock.expect(solrResponseDto.getOpenstreetmap_id()).andReturn(5L);
		EasyMock.replay(solrResponseDto);
		results.add(solrResponseDto);
		FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
		EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
		EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
		EasyMock.replay(mockResultDTO);
		
		
		String text = "toto";
		String countryCode = "FR";
		Point location = GeolocHelper.createPoint(3F, 4F);
		IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		FulltextQuery query = new FulltextQuery(text, Pagination.ONE_RESULT, OpenStreetMapCitiesSimpleImporter.MINIMUM_OUTPUT_STYLE, ONLY_CITY_PLACETYPE, countryCode);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
		EasyMock.replay(mockfullFullTextSearchEngine);
		
		importer.setFullTextSearchEngine(mockfullFullTextSearchEngine);
		
		SolrResponseDto actual = importer.getNearestByPlaceType(location, text, countryCode,ONLY_CITY_PLACETYPE);
		Assert.assertEquals(null, actual);
		EasyMock.verify(mockfullFullTextSearchEngine);
	}
	
	@Test
	public void getNearestCity_lowScore(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		
		List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
		SolrResponseDto solrResponseDto = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDto.getScore()).andReturn(OpenStreetMapCitiesSimpleImporter.SCORE_LIMIT-0.2F);
		EasyMock.expect(solrResponseDto.getOpenstreetmap_id()).andReturn(null);
		EasyMock.replay(solrResponseDto);
		results.add(solrResponseDto);
		FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
		EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
		EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
		EasyMock.replay(mockResultDTO);
		
		
		String text = "toto";
		String countryCode = "FR";
		Point location = GeolocHelper.createPoint(3F, 4F);
		IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		FulltextQuery query = new FulltextQuery(text, Pagination.ONE_RESULT, OpenStreetMapCitiesSimpleImporter.MINIMUM_OUTPUT_STYLE, ONLY_CITY_PLACETYPE, countryCode);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
		EasyMock.replay(mockfullFullTextSearchEngine);
		
		importer.setFullTextSearchEngine(mockfullFullTextSearchEngine);
		
		SolrResponseDto actual = importer.getNearestByPlaceType(location, text, countryCode,ONLY_CITY_PLACETYPE);
		Assert.assertEquals(null, actual);
		EasyMock.verify(mockfullFullTextSearchEngine);
	}
	
	@Test
	public void getNearestCityWithNullName(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		Point location = GeolocHelper.createPoint(3F, 4F);
		Assert.assertNull(importer.getNearestByPlaceType(location, "", "FR",ONLY_CITY_PLACETYPE));
		Assert.assertNull(importer.getNearestByPlaceType(location, " ", "FR",ONLY_CITY_PLACETYPE));
		Assert.assertNull(importer.getNearestByPlaceType(location, null, "FR",ONLY_CITY_PLACETYPE));
	}
	
	@Test
	public void getNearestCityWithNullLocation(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		Assert.assertNull(importer.getNearestByPlaceType(null, "paris", "FR",ONLY_CITY_PLACETYPE));
		
	}
	
	@Test
	public void getAdm(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		
		List<SolrResponseDto> results = new ArrayList<SolrResponseDto>();
		SolrResponseDto solrResponseDto = EasyMock.createNiceMock(SolrResponseDto.class);
		results.add(solrResponseDto);
		FulltextResultsDto mockResultDTO = EasyMock.createMock(FulltextResultsDto.class);
		EasyMock.expect(mockResultDTO.getResultsSize()).andReturn(1);
		EasyMock.expect(mockResultDTO.getResults()).andReturn(results);
		EasyMock.replay(mockResultDTO);
		
		
		String text = "toto";
		String countryCode = "FR";
		IFullTextSearchEngine mockfullFullTextSearchEngine = EasyMock.createMock(IFullTextSearchEngine.class);
		FulltextQuery query = new FulltextQuery(text, Pagination.ONE_RESULT, MINIMUM_OUTPUT_STYLE, Constants.ONLY_ADM_PLACETYPE, countryCode);
		query.withAllWordsRequired(false).withoutSpellChecking();
		
		EasyMock.expect(mockfullFullTextSearchEngine.executeQuery(query)).andReturn(mockResultDTO);
		EasyMock.replay(mockfullFullTextSearchEngine);
		
		importer.setFullTextSearchEngine(mockfullFullTextSearchEngine);
		
		SolrResponseDto actual = importer.getAdm(text, countryCode);
		Assert.assertEquals(solrResponseDto, actual);
		
		EasyMock.verify(mockfullFullTextSearchEngine);
		
	}
	
	@Test
	public void linkAdm(){
		
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){

			int call = 0;

			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (call == 0){
					call++;
					return null;
				} else if (call==1){
					if (!name.equals("admName4") || !countryCode.equals("PL")){
						Assert.fail("getAdm Is not call with the correct parameters : " +name+";"+countryCode);
					}
						final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
						EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
						EasyMock.expect(solrResponseDtoAdm.getName()).andReturn("admName");
						EasyMock.replay(solrResponseDtoAdm);
						return solrResponseDtoAdm;
					
				}else {
						throw new RuntimeException("getAdmShouldOnlyBeCall 2 times");
					}
				}
		};
		City city = new City();
		city.setCountryCode("PL");
		
		 List<AdmDTO> dtos = new ArrayList<AdmDTO>();
		 AdmDTO dto1 = new AdmDTO("admName1", 4, 123L);
		 AdmDTO dto2 = new AdmDTO("admName2", 5, 123L);
		 AdmDTO dto3 = new AdmDTO("admName2", 6, 123L);//should be ignre because same name as previous
		 AdmDTO dto4 = new AdmDTO("admName4", 7, 123L);
		 AdmDTO dto5 = new AdmDTO("admName5", 8, 123L);//should be ignore because 8 >= 8
		 dtos.add(dto1);
		 dtos.add(dto2);
		 dtos.add(dto3);
		 dtos.add(dto4);
		 dtos.add(dto5);
		 Collections.sort(dtos);
		 
		 IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
			Adm adm = new Adm(2);
			adm.setName("admName");
			EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
			EasyMock.replay(admDao);
			importer.setAdmDao(admDao);
		
		importer.LinkAdm(city, dtos);
		Assert.assertEquals(adm, city.getAdm());
		
		EasyMock.verify(admDao);

	}
	
	@Test
	public void processWithUnknownCityAndKnownAdm(){
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.expect(solrResponseDtoAdm.getName()).andReturn("admName");
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL")){
					throw new RuntimeException("the getNearestCity() function is not called with the correct parameter");
				}
				return null;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (!name.equals("gmina Bogoria") || !countryCode.equals("PL")){
					throw new RuntimeException("the getAdm() function is not called with the correct parameter : "+name+";"+countryCode);
				}
				return solrResponseDtoAdm;
			}
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("village", city.getAmenity());
				Assert.assertEquals("Pełczyce", city.getName());
				Assert.assertEquals("PL", city.getCountryCode());
				Assert.assertEquals(50.64543, city.getLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.379207240704833, city.getLongitude().doubleValue(),0.1);
				Assert.assertEquals(50.646, city.getAdminCentreLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.378, city.getAdminCentreLongitude().doubleValue(),0.1);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setFeatureId(9876L);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
		EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(9876L);
		EasyMock.replay(idGenerator);
		importer.setIdGenerator(idGenerator);
		
		 List<AdmDTO> dtos = new ArrayList<AdmDTO>();
		 AdmDTO dto1 = new AdmDTO("admName1", 4, 123L);
		 AdmDTO dto2 = new AdmDTO("admName2", 5, 123L);
		 AdmDTO dto3 = new AdmDTO("admName2", 6, 123L);//should be ignre because same name as previous
		 AdmDTO dto4 = new AdmDTO("admName4", 7, 123L);
		 AdmDTO dto5 = new AdmDTO("admName5", 8, 123L);//should be ignore because 8 >= 8
		 dtos.add(dto1);
		 dtos.add(dto2);
		 dtos.add(dto3);
		 dtos.add(dto4);
		 dtos.add(dto5);
		 Collections.sort(dtos);
		 
		 IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
			Adm adm = new Adm(2);
			adm.setName("admName");
			EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
			EasyMock.replay(admDao);
			importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		
		EasyMock.verify(cityDao);
		EasyMock.verify(idGenerator);
	}
	
	
	@Test
	public void processWithUnknownCityAndKnownAdm_WhenAdmlevelIsNull(){
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.expect(solrResponseDtoAdm.getName()).andReturn("admName");
		EasyMock.replay(solrResponseDtoAdm);
		final Adm adm = new Adm(2);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL")){
					throw new RuntimeException("the getNearestCity() function is not called with the correct parameter");
				}
				return null;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (!name.equals("is in foobar") || !countryCode.equals("PL")){
					throw new RuntimeException("the getAdm() function is not called with the correct parameter : "+name+";"+countryCode);
				}
				return solrResponseDtoAdm;
			}
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("village", city.getAmenity());
				Assert.assertEquals("Pełczyce", city.getName());
				Assert.assertEquals("PL", city.getCountryCode());
				Assert.assertEquals(50.64543, city.getLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.379207240704833, city.getLongitude().doubleValue(),0.1);
				Assert.assertEquals(50.646, city.getAdminCentreLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.378, city.getAdminCentreLongitude().doubleValue(),0.1);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				Assert.assertEquals("adm should be set",adm, city.getAdm());
				
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
				
				Assert.assertEquals(generator.generateLabel(city), city.getLabel());
				Assert.assertTrue("alternate labels are empty and shouldn't be", city.getAlternateLabels().size()!=0);
				Assert.assertEquals(generator.generateLabels(city).size(), city.getAlternateLabels().size());
				Assert.assertEquals(generator.getFullyQualifiedName(city), city.getFullyQualifiedName());
				
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setFeatureId(9876L);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
		EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(9876L);
		EasyMock.replay(idGenerator);
		importer.setIdGenerator(idGenerator);
		
		 List<AdmDTO> dtos = new ArrayList<AdmDTO>();
		 AdmDTO dto1 = new AdmDTO("admName1", 4, 123L);
		 AdmDTO dto2 = new AdmDTO("admName2", 5, 123L);
		 AdmDTO dto3 = new AdmDTO("admName2", 6, 123L);//should be ignre because same name as previous
		 AdmDTO dto4 = new AdmDTO("admName4", 7, 123L);
		 AdmDTO dto5 = new AdmDTO("admName5", 8, 123L);//should be ignore because 8 >= 8
		 dtos.add(dto1);
		 dtos.add(dto2);
		 dtos.add(dto3);
		 dtos.add(dto4);
		 dtos.add(dto5);
		 Collections.sort(dtos);
		 
		 IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
			adm.setName("admName");
			EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
			EasyMock.replay(admDao);
			importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\t\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		
		EasyMock.verify(cityDao);
		EasyMock.verify(idGenerator);
	}
	
	 
	
	@Test
	public void processWithknownCityAndAdm_citySubdivision(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);

		EasyMock.replay(solrResponseDtoCity);
		
		final Adm adm = new Adm(2);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce 02") || !countryCode.equals("PL") || placetype != Constants.ONLY_CITYSUBDIVISION_PLACETYPE){
					throw new RuntimeException("the function getNearestCity() is not called with the correct parameter");
				}
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (!name.equals("gmina Bogoria") || !countryCode.equals("PL")){
					throw new RuntimeException("the function getAdm() is not called with the correct parameter : "+name+";"+countryCode);
				}
				return solrResponseDtoAdm;
			}
			
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("village", city.getAmenity());
				Assert.assertEquals("When a city is already present, we overide the name","Pełczyce 02", city.getName());
				Assert.assertEquals("When a city is already present, we overide the countrycode","PL", city.getCountryCode());
				Assert.assertEquals("When a city is already present, we overide the lat",50.6454D, city.getLatitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we overide the long",21.379D, city.getLongitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we set the admin centre lat",50.646D, city.getAdminCentreLatitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we set the admin centre long",21.378D, city.getAdminCentreLongitude().doubleValue(),0.01);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
				Assert.assertEquals("adm should be set",adm, city.getAdm());
				
				Assert.assertEquals(generator.generateLabel(city), city.getLabel());
				Assert.assertTrue("alternate labels are empty and shouldn't be", city.getAlternateLabels().size()!=0);
				Assert.assertEquals(generator.generateLabels(city).size(), city.getAlternateLabels().size());
				Assert.assertEquals(generator.getFullyQualifiedName(city), city.getFullyQualifiedName());
			}
		};
		
		CitySubdivisionDao citySubdivisionDao = EasyMock.createMock(CitySubdivisionDao.class);
		CitySubdivision city=new CitySubdivision();
		city.setName("initial name");
		city.setCountryCode("DE");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(123L);
		EasyMock.expect(citySubdivisionDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(citySubdivisionDao.save(city)).andReturn(city);
		EasyMock.replay(citySubdivisionDao);
		importer.setCitySubdivisionDao(citySubdivisionDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		
		adm.setName("admName");
		EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce 02\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(citySubdivisionDao);
	}
	
	@Test
	public void processWithknownCityAndAdm_citySubdivision_placetype(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);

		EasyMock.replay(solrResponseDtoCity);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL") || placetype != Constants.ONLY_CITYSUBDIVISION_PLACETYPE){
					throw new RuntimeException("the getNearestCity() function is not called with the correct parameter");
				}
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (!name.equals("gmina Bogoria") || !countryCode.equals("PL")){
					throw new RuntimeException("the getAdm() function is not called with the correct parameter");
				}
				return solrResponseDtoAdm;
			}
			
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("neighbourhood", city.getAmenity());
				Assert.assertEquals("When a city is already present, we overide the name","Pełczyce", city.getName());
				Assert.assertEquals("When a city is already present, we overide the countrycode","PL", city.getCountryCode());
				Assert.assertEquals("When a city is already present, we overide the lat",50.6454D, city.getLatitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we overide the long",21.379D, city.getLongitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we set the admin centre lat",50.646D, city.getAdminCentreLatitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we set the admin centre long",21.378D, city.getAdminCentreLongitude().doubleValue(),0.01);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
				Assert.assertEquals(generator.generateLabel(city), city.getLabel());
				
				Assert.assertTrue("alternate labels are empty and shouldn't be", city.getAlternateLabels().size()!=0);
				Assert.assertEquals(generator.generateLabels(city).size(), city.getAlternateLabels().size());
				Assert.assertEquals(generator.getFullyQualifiedName(city), city.getFullyQualifiedName());
			}
		};
		
		CitySubdivisionDao citySubdivisionDao = EasyMock.createMock(CitySubdivisionDao.class);
		CitySubdivision city=new CitySubdivision();
		city.setName("initial name");
		city.setCountryCode("DE");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(123L);
		EasyMock.expect(citySubdivisionDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(citySubdivisionDao.save(city)).andReturn(city);
		EasyMock.replay(citySubdivisionDao);
		importer.setCitySubdivisionDao(citySubdivisionDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		Adm adm = new Adm(2);
		adm.setName("admName");
		EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tneighbourhood\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(citySubdivisionDao);
	}
	
	@Test
	public void processWithknownCityAndAdm_CityThatIsAlreadyAMunicipalityShouldAlwaysBe(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L).times(2);

		EasyMock.replay(solrResponseDtoCity);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL")|| placetype != Constants.ONLY_CITY_PLACETYPE){
					throw new RuntimeException("the function is not called with the correct parameter");
				}
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (!name.equals("gmina Bogoria") || !countryCode.equals("PL")){
					throw new RuntimeException("the function is not called with the correct parameter");
				}
				return solrResponseDtoAdm;
			}
			
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("village", city.getAmenity());
				Assert.assertEquals("When a city is already present, we overide the name","Pełczyce", city.getName());
				Assert.assertEquals("When a city is already present, we overide the countrycode","PL", city.getCountryCode());
				Assert.assertEquals("When a city is already present, we overide the lat",50.6454D, city.getLatitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we overide the long",21.379D, city.getLongitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we set the admin centre lat",50.646D, city.getAdminCentreLatitude().doubleValue(),0.01);
				Assert.assertEquals("When a city is already present, we set the admin centre long",21.378D, city.getAdminCentreLongitude().doubleValue(),0.01);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
				Assert.assertTrue("city should still be a municipality because it was before, even if it is a node, a previous condition make this city a municipality",((City)city).isMunicipality());
				
				Assert.assertEquals(generator.generateLabel(city), city.getLabel());
				Assert.assertTrue("alternate labels are empty and shouldn't be", city.getAlternateLabels().size()!=0);
				Assert.assertEquals(generator.generateLabels(city).size(), city.getAlternateLabels().size());
				Assert.assertEquals(generator.getFullyQualifiedName(city), city.getFullyQualifiedName());
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setName("initial name");
		city.setCountryCode("DE");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(123L);
		city.setMunicipality(true);
		EasyMock.expect(cityDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		Adm adm = new Adm(2);
		adm.setName("admName");
		EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
	}
	
	@Test
	public void processWithknownCityAndAdm(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);

		EasyMock.replay(solrResponseDtoCity);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL")|| placetype != Constants.ONLY_CITY_PLACETYPE){
					throw new RuntimeException("the function is not called with the correct parameter");
				}
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				if (!name.equals("gmina Bogoria") || !countryCode.equals("PL")){
					throw new RuntimeException("the function is not called with the correct parameter : "+name+";"+countryCode);
				}
				return solrResponseDtoAdm;
			}
			
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("village", city.getAmenity());
				Assert.assertEquals("Pełczyce", city.getName());
				Assert.assertEquals("PL", city.getCountryCode());
				Assert.assertEquals(50.64543, city.getLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.379207240704833, city.getLongitude().doubleValue(),0.1);
				Assert.assertEquals(50.646, city.getAdminCentreLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.378, city.getAdminCentreLongitude().doubleValue(),0.1);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
				
				Assert.assertEquals(generator.generateLabel(city), city.getLabel());
				Assert.assertTrue("alternate labels are empty and shouldn't be", city.getAlternateLabels().size()!=0);
				Assert.assertEquals(generator.generateLabels(city).size(), city.getAlternateLabels().size());
				Assert.assertEquals(generator.getFullyQualifiedName(city), city.getFullyQualifiedName());
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setName("initial name");
		city.setCountryCode("DE");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(123L);
		EasyMock.expect(cityDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		Adm adm = new Adm(2);
		adm.setName("admName");
		EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
	}
	
	
	@Test
	public void processData_subdivision(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);

		EasyMock.replay(solrResponseDtoCity);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				return solrResponseDtoAdm;
			}
			
			void savecity(GisFeature city) {
				Assert.assertTrue(city instanceof CitySubdivision);
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setName("initial name");
		city.setCountryCode("DE");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(123L);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		Adm adm = new Adm(2);
		adm.setName("admName");
		EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		ICitySubdivisionDao subdivisionDao = EasyMock.createMock(ICitySubdivisionDao.class);
		CitySubdivision citySubdivision = new CitySubdivision();
		EasyMock.expect(subdivisionDao.getByFeatureId(123L)).andReturn(citySubdivision);
		EasyMock.expect(subdivisionDao.save(citySubdivision)).andReturn(citySubdivision);
		EasyMock.replay(subdivisionDao);
		importer.setCitySubdivisionDao(subdivisionDao);
		
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tsuburb\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
	}
	
	@Test
	public void processData(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);

		EasyMock.replay(solrResponseDtoCity);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				return solrResponseDtoAdm;
			}
			
			
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setName("initial name");
		city.setCountryCode("DE");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(123L);
		EasyMock.expect(cityDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		 List<AdmDTO> dtos = new ArrayList<AdmDTO>();
		 AdmDTO dto1 = new AdmDTO("admName1", 4, 123L);
		 AdmDTO dto2 = new AdmDTO("admName2", 5, 123L);
		 AdmDTO dto3 = new AdmDTO("admName2", 6, 123L);//should be ignre because same name as previous
		 AdmDTO dto4 = new AdmDTO("admName4", 7, 123L);
		 AdmDTO dto5 = new AdmDTO("admName5", 8, 123L);//should be ignore because 8 >= 8
		 dtos.add(dto1);
		 dtos.add(dto2);
		 dtos.add(dto3);
		 dtos.add(dto4);
		 dtos.add(dto5);
		 Collections.sort(dtos);
		 
		 IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
			Adm adm = new Adm(2);
			adm.setName("admName");
			EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
			EasyMock.replay(admDao);
			importer.setAdmDao(admDao);
		
		
		
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
		//EasyMock.verify(admDao);
	}
	
	@Test
	public void processData_poi(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);

		EasyMock.replay(solrResponseDtoCity);
		
		final SolrResponseDto solrResponseDtoAdm = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoAdm.getFeature_id()).andReturn(4356L).times(2);
		EasyMock.replay(solrResponseDtoAdm);
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				return solrResponseDtoAdm;
			}
			
			
		};
		
		IGisFeatureDao gisFeatureDao = EasyMock.createMock(IGisFeatureDao.class);
		GisFeature city=new GisFeature();
		city.setName("Pełczyce");
		city.setCountryCode("PL");
		city.setLocation(GeolocHelper.createPoint(30D, 20D));
		city.setFeatureId(9876L);
		//EasyMock.expect(gisFeatureDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(gisFeatureDao.save(city)).andReturn(city);
		EasyMock.replay(gisFeatureDao);
		importer.setGisFeatureDao(gisFeatureDao);
		
		 List<AdmDTO> dtos = new ArrayList<AdmDTO>();
		 AdmDTO dto1 = new AdmDTO("admName1", 4, 123L);
		 AdmDTO dto2 = new AdmDTO("admName2", 5, 123L);
		 AdmDTO dto3 = new AdmDTO("admName2", 6, 123L);//should be ignre because same name as previous
		 AdmDTO dto4 = new AdmDTO("admName4", 7, 123L);
		 AdmDTO dto5 = new AdmDTO("admName5", 8, 123L);//should be ignore because 8 >= 8
		 dtos.add(dto1);
		 dtos.add(dto2);
		 dtos.add(dto3);
		 dtos.add(dto4);
		 dtos.add(dto5);
		 Collections.sort(dtos);
		 
		 IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
			Adm adm = new Adm(2);
			adm.setName("admName");
			EasyMock.expect(admDao.getByFeatureId(4356L)).andReturn(adm);
			EasyMock.replay(admDao);
			importer.setAdmDao(admDao);
		
		
			IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
			EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(9876L);
			EasyMock.replay(idGenerator);
			importer.setIdGenerator(idGenerator);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t10\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tlocality\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(gisFeatureDao);
		//EasyMock.verify(admDao);
	}
	
	@Test
	public void processWithunKnownCityAndUnknownAdm(){
		
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL")){
					throw new RuntimeException("the function is not called with the correct parameter");
				}
				return null;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				return null;
			}
			
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setFeatureId(9876L);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		IIdGenerator idGenerator = EasyMock.createMock(IIdGenerator.class);
		EasyMock.expect(idGenerator.getNextFeatureId()).andReturn(9876L);
		EasyMock.replay(idGenerator);
		importer.setIdGenerator(idGenerator);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
		EasyMock.verify(admDao);
		EasyMock.verify(idGenerator);
	}
	
	@Test
	public void isACitySubdivision(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		Assert.assertTrue(importer.isACitySubdivision("neighbourhood"));
		Assert.assertTrue("the placetypeshould be case insensitive",importer.isACitySubdivision("NEIghbourhood"));
		Assert.assertTrue(importer.isACitySubdivision("quarter"));
		Assert.assertTrue(importer.isACitySubdivision("isolated_dwelling"));
		Assert.assertTrue(importer.isACitySubdivision("suburb"));
		Assert.assertTrue(importer.isACitySubdivision("city_block"));
		Assert.assertTrue(importer.isACitySubdivision("borough"));
		Assert.assertFalse(importer.isACitySubdivision("city"));
	}
	
	@Test
	public void isPoi(){
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		Assert.assertFalse(importer.isPoi("locality","8"));
		Assert.assertFalse(importer.isPoi("",""));
		Assert.assertTrue(importer.isPoi("locality","10"));
		Assert.assertTrue(importer.isPoi("locality",""));
		Assert.assertFalse(importer.isPoi("citylocality","8"));
		Assert.assertFalse(importer.isPoi("citylocality","10"));
	}
	
	
	
	@Test
	public void processWithKnownCityAndUnknownAdm(){
		final SolrResponseDto solrResponseDtoCity = EasyMock.createMock(SolrResponseDto.class);
		EasyMock.expect(solrResponseDtoCity.getFeature_id()).andReturn(123L);
		EasyMock.replay(solrResponseDtoCity);
		
		OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter(){
			@Override
			protected SolrResponseDto getNearestByPlaceType(Point location, String name, String countryCode,Class[]placetype) {
				if (!name.equals("Pełczyce") || !countryCode.equals("PL")){
					throw new RuntimeException("the function is not called with the correct parameter");
				}
				return solrResponseDtoCity;
			};
			
			@Override
			protected SolrResponseDto getAdm(String name, String countryCode) {
				return null;
			}
			
			@Override
			void savecity(GisFeature city) {
				super.savecity(city);
				Assert.assertEquals("village", city.getAmenity());
				Assert.assertEquals("Pełczyce", city.getName());
				Assert.assertEquals("PL", city.getCountryCode());
				Assert.assertEquals(50.64543, city.getLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.379207240704833, city.getLongitude().doubleValue(),0.1);
				Assert.assertEquals(50.646, city.getAdminCentreLatitude().doubleValue(),0.1);
				Assert.assertEquals(21.378, city.getAdminCentreLongitude().doubleValue(),0.1);
				
				Assert.assertEquals(6530243L, city.getOpenstreetmapId().longValue());
				Assert.assertEquals(1000000L, city.getPopulation().longValue());
				
				Assert.assertEquals(2, city.getZipCodes().size());
				Assert.assertTrue(city.getZipCodes().contains(new ZipCode("28-210")));
				Assert.assertTrue( city.getZipCodes().contains(new ZipCode("28-2101")));
				
				Assert.assertEquals(generator.generateLabel(city), city.getLabel());
				Assert.assertTrue("alternate labels are empty and shouldn't be", city.getAlternateLabels().size()!=0);
				Assert.assertEquals(generator.generateLabels(city).size(), city.getAlternateLabels().size());
				Assert.assertEquals(generator.getFullyQualifiedName(city), city.getFullyQualifiedName());
			}
		};
		
		ICityDao cityDao = EasyMock.createMock(ICityDao.class);
		City city=new City();
		city.setFeatureId(123L);
		EasyMock.expect(cityDao.getByFeatureId(123L)).andReturn(city);
		EasyMock.expect(cityDao.save(city)).andReturn(city);
		EasyMock.replay(cityDao);
		importer.setCityDao(cityDao);
		
		
		IAdmDao admDao = EasyMock.createMock(IAdmDao.class);
		EasyMock.replay(admDao);
		importer.setAdmDao(admDao);
		
		importer.setMunicipalityDetector(new MunicipalityDetector());
		
		String line= "R\t6530243\t3009735615\tPełczyce\tPL\t28-210\t28-2101\t8\t1000000\t0101000020E6100000D13BC9B91361354030E055AC9D524940\t0101000020E6100000DE7C7E73DA603540ACFF18DFBC524940\t0106000020E610000001000000010300000001000000D20000008D8F60F4265C35401215AA9B8B524940D2C5A695425C35401598A9A4A9524940A367695D595C3540A06B0487BC524940A1C211A4525C35408BC3995FCD52494005543882545C35403A9270C6D55249401950148D5A5C35405551BCCADA52494088FC8E3C6B5C3540EDA17DACE05249401C2ECD08CA5C35408B1D8D43FD524940627B7775D85C35400F8DDDF41D534940ACD09BE50D5D35407961C66F6553494017F2ADB4315D3540B0027CB779534940FDA6B052415D35402569ED6C7E534940DF910C946F5D3540D998D71187534940E861B2028D5D3540A0F76B578D5349400E349F73B75D35402D4D00B49C534940BD53A63DCA5D35403B9AD99DA4534940C4DD0F2ED35D3540611514ABAB534940608033E7CF5D35406009A4C4AE534940D2D0F533AB5D3540A46F777BB553494044499231885D35400D09CECFC3534940F17096ED9E5D35409378D4F3C95349400D82D8E3CF5D354003452C62D8534940137DE310165E3540708912E3EB534940E68931FA1B5E354035B91803EB5349404A21DAD8365E3540CBF4F00AE9534940A8261CD5445E35406CD84A43E8534940ADA92C0ABB5E3540906335A7DC5349400E49D2DAD95E354073840CE4D95349408813984EEB5E354056CDBDD1D85349403DF4DDAD2C5F354077A22424D253494076995077545F3540DF1D19ABCD534940207821C2645F354099BF42E6CA534940C97553CA6B5F354094AC1E8BC85349403F3E213B6F5F354060E74B14C653494025D63D0D735F354037887BD1C35349409FCBD424785F3540CD4D8A45C253494000A13F7E805F35405C6ACE0BC1534940E6F91DD48F5F354021109EBFBF534940262B757FAB5F35406AE27899BC534940FA88F3CBBB5F35402AB7A3EEB95349409C487A62E75F3540E285634CB053494074FB9B06FB5F35405B80B6D5AC534940ADD6D3580C603540CD898741AA5349403667333D17603540754AF6BEA75349400B0BEE073C6035406759411DA0534940241D2FEE50603540B2097AB99953494051FD8348866035402E89FDAF845349402B3D2E05FF60354042E1FD028B534940853EFD0C146135403B600D068D534940BAF8DB9E20613540031544937A53494013B46E28216135405334C5D276534940B226BB3E18613540D6C7E8256B5349404BD05FE81161354019EEB7D15B534940A627D1370E613540150FDE0D58534940DFB4CF1907613540ACB24A44535349403D409C2C5A6135406DAA93D85A534940396403E9626135408EF5C3BE535349403E44A33B886135401B5C847357534940A98B14CAC261354071B77FC05E534940B92869B40B623540FD4E93196F534940BECE3C6F196235403D8ED59C7253494099C51FA056623540006201028C53494054DB977D68623540014F5AB8AC534940E9894226746235408AB3226AA2534940D00B772E8C6235403444BA449A5349400A078C3F9B623540F5A85379965349403F3A75E5B3623540A176AEDE97534940F862DE99BF623540B39F200C97534940010BAA57DB6235405000C5C892534940E72F3E16EC62354075F684DB905349404ABD022C4D6335406BCA04B28D534940F11879B4CC6335402E7590D7835349400B8EDCE4016435401AADFE637C534940CCAEC5B82C6435401F1906877253494039FC242D4B643540B6EA84E16D534940878494449B643540513640C461534940D3DA34B6D7643540D788601C5C5349403A4B7FE5F7643540F1C2312658534940B0A1E58A9C6535402B02F797385349404E6D5FF6A16535405559CAE8365349403316F2ADB4653540408E41823D534940F27794EFBE65354032A0CD60425349407843BF5BC56535402C3FBA224253494099B21D9D0466354059B1AEC046534940090FE4EA11663540E99FE06245534940A6E5513B0D66354031105F81435349408D43A275F9653540CAF500272553494054E17437F4653540C586B883225349404721C9ACDE653540D917BFDF1E5349409FFBBCF3D5653540F3F56BA11A53494011CE0248C8653540FF44C07C0D534940A4A60293C0653540CB6548BA0B53494090B81160A26535401332EB200A5349407AC7293A92653540EF6F75A50A534940100BB5A67965354085C5F2F8075349408ACFF819726535400243FBFD07534940FF20376854653540F42334380053494087FBC8AD49653540A7A091BEFF5249400F5A59EC4065354038C30DF8FC52494048A41243176535403ABB1006F95249400BFB2C85F66435401FA0A0B9F35249407C09BA62EB643540A3B83E07F1524940D42B6519E2643540166C239EEC524940607AFB73D164354022B53F06E152494044CCDBC7C0643540A244F064DC524940856A3986B6643540A2A64C20DB524940B7B585E7A56435407707C25FDD524940F5F34B69A2643540963A1279DC5249406A60F591A5643540B25879B8D352494026E9F582AA643540095B47B0CC524940DAE9077591643540543651F0B952494023FCE659A46435409728D604AC524940CA75AE83DE643540B7AC0CF49A5249408C50C7BEBF643540E3395B406852494032074147AB64354071975874465249406EC905C2A96435402CC9F08E42524940E78CCD339B6435402EC1F39C3E5249407E9D8A0A8B64354077514F7A3A52494004FE953A6D6435401CF1643733524940B8B475CB696435408D7E349C325249407CF2B0506B6435409AEA6E4331524940629E3AFB6F64354034958FEE31524940F455F2B1BB643540E133C813F7514940C830822106653540058D9944BD514940C474C69C0C6535404D3F975FBC5149402CC6AF0E366535409768DA0FC2514940BF3D192D51653540E435AFEAAC514940134A5F0839653540D85F1B96AB51494060483DFA15653540EF0802BFA15149402199695611653540D4A5DA029D5149405041D9EFE4643540A1A4C00298514940BC88A53792643540C9E0720690514940AB17320D79643540B96D84EA8B514940CDEF8F5264643540DFDF450488514940231A38FB5E6435400FAC996B875149405E4E0988496435403D6304F97E5149405EE3E94B2564354005DF347D7651494069B57691F8633540B5B002D7705149401E3B037EE86335401808A7AA7551494069554B3ACA6335403F16478A6D514940667330F6B96335408D6BD7DF6D514940D2883EC4AB633540CABD0A3A6B514940BBF083F3A9633540384D9F1D70514940381D5C959F6335405C7FA6B97051494041F273E899633540F629C764715149400FA1A5869163354066F3DD5273514940D451691F8663354007D1FF1774514940575B56067A6335404E6958E7735149408089D3A46F633540F551A11573514940C637143E5B6335401577BCC96F5149409C3F5C284F63354027E8E5666E514940F243A511336335406F6CD15D6D514940074E6CF4206335406FAE505A6E5149402D184B47EF623540BB59CD84705149409E5A22BCE26235402033ABD2715149401EE7919AD1623540596DFE5F75514940C2E2CB9FCA6235405F865A2E76514940B27AD168BC623540FBB2B453735149409EFAF664B462354007A925677251494080208A13A9623540194229B5725149402E2526039B62354013F5824F73514940EA735A5C886235406C54F190735149407AD44E8358623540A38790A971514940D204E51137623540E53AD7416F5149405D55F65D11623540BCA7BC676E5149405889C3E3EC61354028DAB0016C5149406C73AD07DD61354059AC3CDC69514940ACE23213B161354052F4C0C7605149405DEB41F79B613540185E49F25C514940027C5CC070613540B60CDD91565149409838F24064613540A04C4810535149402ADDA7F45D613540E2B19FC55251494058772CB649613540065A70F552514940122A82493B613540E9EAD8525151494071B9B0242F613540B4F7F3B74E5149409923754C2761354079831E204E514940687682A21E6135406D1D1CEC4D514940085C57CC086135408AFC44AF505149407039A80E03613540E3FF8EA8505149404BCADDE7F86035404983802150514940A9195245F160354067A263624E514940CC762A85E5603540DF4EC7084D5149406BFDE373DD603540321F10E84C514940D662A6A3D2603540A97D84AB4D514940C64263DCC36035405B6A73524E514940447BAB64B6603540AF68CE554E514940F6C07DD3AA603540FD6772D64C51494027A435069D603540B573F5BE4C514940BA1798158A603540DFF2A2C04C514940F3B803D083603540B088AB144C5149402418BD0974603540E659EE714B514940DB1C42F053603540637BD22E4B5149404F18DEF64E6035405EAA2DD049514940F76A91FEA85F3540FA08A12875514940FEB1B5638F5F354096D39E92735149400C10BBA58B5F354010536C1679514940338001CE9C5F3540E4141DC9E551494061C9552C7E5F35402588A9E3E751494011FFB0A5475F35406FC5596CEE5149404CCD7921785F35400E198F52095249403561FBC9185F3540C613E6E2145249407514D67C4B5F3540F5835F8F2E52494012C9EB1C5E5F35408E90268348524940307777AE395F3540A5B84F33495249402E4F31186D5D3540E0F8DA334B52494071F55267A45C3540991E03684A52494070795160A65C354017FE672849524940A084E3439A5C35404654F2FB48524940CD76853E585C3540C14A4FA26F5249408D8F60F4265C35401215AA9B8B524940\tvillage\tis in foobar\tpowiat staszowski___6___2635574___województwo świętokrzyskie___4___130914___gmina Bogoria___7___2963727\t\"{\"\"\"\",name:genitive===Pełczyczyc___name:adjective===pełczycki}\"";
		
		importer.processData(line);
		
		EasyMock.verify(cityDao);
		EasyMock.verify(admDao);
	}
	
	
	 
	 @Test
	 public void populateAdmNamesFromAdm(){
		 Adm adm = new Adm(5);
		 adm.setAdm1Name("adm1Name");
		 adm.setAdm2Name("adm2Name");
		 adm.setAdm3Name("adm2Name");
		 adm.setAdm4Name("adm4Name");
		 adm.setAdm5Name("adm5Name");
		 OpenStreetMapCitiesSimpleImporter importer = new OpenStreetMapCitiesSimpleImporter();
		 City city = new City();
		 importer.populateAdmNamesFromAdm(city, adm);
		 Assert.assertEquals("adm1Name", city.getAdm1Name());
		 Assert.assertEquals("adm2Name", city.getAdm2Name());
		 Assert.assertEquals("adm4Name", city.getAdm3Name());
		 Assert.assertEquals("adm5Name", city.getAdm4Name());
		 
		 
	 }
	
	
	
	
}
