/*******************************************************************************
 * Gisgraphy Project 
 *  
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *  
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *  
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 *  
 *   Copyright 2008  Gisgraphy project 
 * 
 *   David Masclet <davidmasclet@gisgraphy.com>
 ******************************************************************************/
package com.gisgraphy.helper;

import static com.gisgraphy.helper.StringHelper.splitCamelCase;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.test.GisgraphyTestHelper;

public class StringHelperTest {

    @Test
    public void testNormalize() {
	Assert.assertEquals("letter without accent should not be modified"," e e e e e je me souviens de ce zouave qui jouait du xylophone en buvant du whisky c est ok c est super",
		StringHelper.normalize("-é \u00E8 \u00E9 \u00EA \u00EB JE ME SOUVIENS de ce zouave qui jouait du-xylophone en buvant.du whisky c'est ok c\"est super "));
    }
    
    @Test
    public void normalizeForNullString() {
	Assert.assertNull(StringHelper.normalize(null));
    }
    
    @Test
    public void TransformStringForIlikeIndexationForNullString(){
    	Assert.assertNull(StringHelper.transformStringForPartialWordIndexation(null,'_'));
    }
    
    @Test
    public void TransformStringForIlikeIndexation(){
	char delimiter ='-';
	String transformedString = StringHelper.transformStringForPartialWordIndexation("it s ok;",delimiter);
	String[] splited = transformedString.split(String.valueOf(" "));
	List<String> list =Arrays.asList(splited);
	//s ok, s o, it s, t s o, t s, it s ok, ok, it s o, it, t s ok
	Assert.assertEquals("There is not the number of words expected, maybe there is duplicate, or single char are indexed but should not, or ..., here is the tansformed string :"+transformedString,10, list.size());
    	Assert.assertTrue(list.contains("it-s-ok"));
    	Assert.assertTrue(list.contains("it"));
    	Assert.assertTrue(list.contains("it-s"));
    	Assert.assertTrue(list.contains("it-s-o"));
    	Assert.assertTrue(list.contains("t-s"));
    	Assert.assertTrue(list.contains("t-s-o"));
    	Assert.assertTrue(list.contains("t-s-ok"));
    	Assert.assertTrue(list.contains("s-o"));
    	Assert.assertTrue(list.contains("s-ok"));
    	Assert.assertTrue(list.contains("ok"));
    }
    
    @Test
    public void TransformStringForIlikeIndexationWithSpecialChar(){
	char delimiter ='-';
	String transformedString = StringHelper.transformStringForPartialWordIndexation("it's ok",delimiter);
	String[] splited = transformedString.split(String.valueOf(" "));
	List<String> list =Arrays.asList(splited);
	//s ok, s o, it s, t s o, t s, it s ok, ok, it s o, it, t s ok
	Assert.assertEquals("There is not the number of words expected, maybe there is duplicate, or single char are indexed but should not, or ..., here is the tansformed string :"+transformedString,10, list.size());
    	Assert.assertTrue(list.contains("it-s-ok"));
    	Assert.assertTrue(list.contains("it"));
    	Assert.assertTrue(list.contains("it-s"));
    	Assert.assertTrue(list.contains("it-s-o"));
    	Assert.assertTrue(list.contains("t-s"));
    	Assert.assertTrue(list.contains("t-s-o"));
    	Assert.assertTrue(list.contains("t-s-ok"));
    	Assert.assertTrue(list.contains("s-o"));
    	Assert.assertTrue(list.contains("s-ok"));
    	Assert.assertTrue(list.contains("ok"));
    }
    
    @Test
    public void transformStringForPartialWordIndexationWithLongString(){
	char delimiter ='-';
	String longString = RandomStringUtils.random(StringHelper.MAX_STRING_INDEXABLE_LENGTH+1,new char[] {'e'});
	Assert.assertEquals("the string to test is not of the expected size the test will fail",StringHelper.MAX_STRING_INDEXABLE_LENGTH+1, longString.length());
	String transformedString = StringHelper.transformStringForPartialWordIndexation(longString,delimiter);
	Assert.assertNull("string that are longer than "+StringHelper.MAX_STRING_INDEXABLE_LENGTH+" should return null",transformedString);
    }
    
    
    
    
    @Test
    public void transformStringForIlikeSearch(){
	char delimiter ='-';
	String transformedString = StringHelper.transformStringForPartialWordSearch("C'est-tr\u00E9s ",delimiter);
	Assert.assertEquals("c-est-tres", transformedString);
    }
    
    
    @Test
    public void updateOpenStreetMapEntityForIndexation(){
	OpenStreetMap openStreetMap = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	//we reset textsearch name and partial search name
	openStreetMap.setPartialSearchName(null);
	openStreetMap.setTextSearchName(null);
	StringHelper.updateOpenStreetMapEntityForIndexation(openStreetMap);
	Assert.assertEquals("The value of text search name is not correct",StringHelper.normalize(openStreetMap.getName()), openStreetMap.getTextSearchName());
    }
    
    @Test
    public void updateOpenStreetMapEntityForIndexationWithANullName(){
	OpenStreetMap openStreetMap = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	openStreetMap.setName(null);
	openStreetMap.setPartialSearchName(null);
	openStreetMap.setTextSearchName(null);
	StringHelper.updateOpenStreetMapEntityForIndexation(openStreetMap);
	Assert.assertNull("The value of partial search name should be null if name is null", openStreetMap.getPartialSearchName());
	Assert.assertNull("The value of text search name should be null if name is null", openStreetMap.getTextSearchName());
    }
    
    @Test
    public void updateOpenStreetMapEntityForIndexationWithALongName(){
	OpenStreetMap openStreetMap = GisgraphyTestHelper.createOpenStreetMapForPeterMartinStreet();
	String longName = RandomStringUtils.random(StringHelper.MAX_STRING_INDEXABLE_LENGTH+1,new char[] {'e'});
	openStreetMap.setName(longName);
	openStreetMap.setPartialSearchName(null);
	openStreetMap.setTextSearchName(null);
	StringHelper.updateOpenStreetMapEntityForIndexation(openStreetMap);
	Assert.assertNull("The value of partial search should be null if name is too long", openStreetMap.getPartialSearchName());
	Assert.assertEquals("The value of text search name should not be null and correct if name is too long",StringHelper.normalize(openStreetMap.getName()), openStreetMap.getTextSearchName());
    }
    
    @Test
    public void splitCamelCaseShouldSplit(){
	assertEquals("lowercase", splitCamelCase("lowercase"));
	    assertEquals("Class", splitCamelCase("Class"));
	    assertEquals("My Class", splitCamelCase("MyClass"));
	    assertEquals("HTML", splitCamelCase("HTML"));
	    assertEquals("PDF Loader", splitCamelCase("PDFLoader"));
	    assertEquals("A String", splitCamelCase("AString"));
	    assertEquals("Simple XML Parser", splitCamelCase("SimpleXMLParser"));
	    assertEquals("GL 11 Version", splitCamelCase("GL11Version"));

    }
    
    @Test
    public void isEmptyString(){
	Assert.assertTrue(StringHelper.isEmptyString(null));
	Assert.assertTrue(StringHelper.isEmptyString(" "));
	Assert.assertTrue(StringHelper.isEmptyString(""));
	Assert.assertFalse(StringHelper.isEmptyString("f"));
    }
    
    @Test
    public void isNotEmptyString(){
	Assert.assertFalse(StringHelper.isNotEmptyString(null));
	Assert.assertFalse(StringHelper.isNotEmptyString(" "));
	Assert.assertFalse(StringHelper.isNotEmptyString(""));
	Assert.assertTrue(StringHelper.isNotEmptyString("f"));
    }
    
    @Test
    public void isSameNameTest(){
    	
    	Assert.assertTrue("synonyms in expected case sensitive",StringHelper.isSameName("st omer","Saint omer"));
    	Assert.assertTrue("synonyms in actual case sensitive",StringHelper.isSameName("Saint omer","st omer"));
    	Assert.assertTrue("synonyms in expected",StringHelper.isSameName("st omer","saint omer"));
    	Assert.assertTrue("synonyms in actual",StringHelper.isSameName("saint omer","st omer"));
    	
    	Assert.assertFalse("different",StringHelper.isSameName("Finkenhof","Bildhauerhof"));
    	Assert.assertFalse("more words",StringHelper.isSameName("Le Breuil","Le Breuil-Mingot"));
    	Assert.assertFalse("more words",StringHelper.isSameName("Morgon","Villié-Morgon"));
    	Assert.assertTrue("accent",StringHelper.isSameName("Bélair","Belair"));
    	Assert.assertTrue("case sensitive",StringHelper.isSameName("La Salce","la salce"));
    	Assert.assertTrue("same",StringHelper.isSameName("La Salce","La Salce"));
    	Assert.assertTrue("less long word but same",StringHelper.isSameName("Les Agnès","agnes"));
    	
    	Assert.assertTrue("with additionnal words size =3",StringHelper.isSameName("notre dame anges","notre dame des anges"));
    	Assert.assertTrue("with additionnal words size =3",StringHelper.isSameName("notre dame des anges","notre dame anges"));
    	
    	Assert.assertTrue("with additionnal words size =2",StringHelper.isSameName("notre dame anges","notre dame de anges"));
    	Assert.assertTrue("with additionnal words size =2",StringHelper.isSameName("notre dame de anges","notre dame anges"));
    	Assert.assertFalse(StringHelper.isSameName("Berg","Sternenberg"));
    	Assert.assertFalse(StringHelper.isSameName("normandie","avr"));
    	Assert.assertFalse(StringHelper.isSameName("paris","paris 07"));
    	
    	Assert.assertTrue("With some punct added",StringHelper.isSameName("AVENIDA DR JOAO ROSA PIRES","AVENIDA DR. JOAO ROSA PIRES"));
    	
    	Assert.assertTrue("with city of",StringHelper.isSameName("city of edinbourg","edinbourg"));
    	Assert.assertTrue("with city of",StringHelper.isSameName("edinbourg","city of edinbourg"));
    	
    	Assert.assertTrue("with city",StringHelper.isSameName("new york city","new york"));
    	Assert.assertTrue("with city",StringHelper.isSameName("new york","new york city"));
    	
    	
    	
    	Assert.assertFalse(StringHelper.isSameName("Les Chézeaux","Les grand Chézeaux"));
    	Assert.assertFalse("less long word but different",StringHelper.isSameName("route pepere","pepere"));
    	
    	Assert.assertTrue(StringHelper.isSameName("Stauffenbergstraße", "Stauffenberg straße"));
    	
    	Assert.assertFalse(StringHelper.isSameName("pas de calais", "calais",2));
    	
    	
    	
    }
    
    @Test
    public void removePunctuation(){
    	Assert.assertEquals("", StringHelper.removePunctuation(""));
    	Assert.assertEquals(null, StringHelper.removePunctuation(null));
    	Assert.assertEquals(" dr kawashima", StringHelper.removePunctuation(" dr. kawashima"));
    	
    }
 
    
    @Test
    public void isSameStreetNameTest(){
    			
    	Assert.assertFalse(StringHelper.isSameStreetName("24812", "Rájec",null));
    	Assert.assertFalse(StringHelper.isSameStreetName("toto", null,null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Untere Hauptstr.", "Untere Hauptstraße","DE"));
    	//Assert.assertTrue("synonyms of street",StringHelper.isSameStreetName("omer street","omer st",null));
    
    	Assert.assertFalse("different",StringHelper.isSameStreetName("Finkenhof","Bildhauerhof",null));
    	Assert.assertFalse("more words",StringHelper.isSameStreetName("Le Breuil","Le Breuil-Mingot",null));
    	Assert.assertFalse("more words",StringHelper.isSameStreetName("Morgon","Villié-Morgon",null));
    	Assert.assertTrue("accent",StringHelper.isSameStreetName("Bélair","Belair",null));
    	Assert.assertTrue("case sensitive",StringHelper.isSameStreetName("La Salce","la salce",null));
    	Assert.assertTrue("same",StringHelper.isSameStreetName("La Salce","La Salce",null));
    	Assert.assertTrue("less long word but same",StringHelper.isSameStreetName("Les Agnès","agnes",null));
    	
    	Assert.assertTrue("with additionnal words size =3",StringHelper.isSameStreetName("notre dame anges","notre dame des anges",null));
    	Assert.assertTrue("with additionnal words size =3",StringHelper.isSameStreetName("notre dame des anges","notre dame anges",null));
    	
    	Assert.assertTrue("with additionnal words size =2",StringHelper.isSameStreetName("notre dame anges","notre dame de anges",null));
    	Assert.assertTrue("with additionnal words size =2",StringHelper.isSameStreetName("notre dame de anges","notre dame anges",null));
    	Assert.assertFalse(StringHelper.isSameStreetName("Berg","Sternenberg",null));
    	Assert.assertFalse(StringHelper.isSameStreetName("normandie","avr",null));
    	Assert.assertFalse(StringHelper.isSameStreetName("paris","paris 07",null));
    	
    	Assert.assertTrue("synonyms in expected",StringHelper.isSameStreetName("st omer","saint omer",null));
    	Assert.assertTrue("synonyms in actual",StringHelper.isSameStreetName("saint omer","st omer",null));
    	//-
    	Assert.assertTrue("synonyms in actual",StringHelper.isSameStreetName("foo baar street","foo-baar-street",null));
    	
    	Assert.assertTrue("With some punct added",StringHelper.isSameStreetName("AVENIDA DR JOAO ROSA PIRES","AVENIDA DR. JOAO ROSA PIRES",null));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("rua COMANDANTE JOSE SOARES COUTINHO","Rua Commandante José Soares","BR"));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("BR-262","avenida RIO BARDAUNI","BR")); 
    	
    	
    	
    	
    	Assert.assertTrue("synonyms in expected case sensitive",StringHelper.isSameStreetName("st omer","Saint omer",null));
    	Assert.assertTrue("synonyms in actual case sensitive",StringHelper.isSameStreetName("Saint omer","st omer",null));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("Les Chézeaux","Les grand Chézeaux",null));
    	Assert.assertFalse("less long word but different",StringHelper.isSameStreetName("route pepere","pepere",null));
    	
    
    	Assert.assertTrue(StringHelper.isSameStreetName("EAST 236st STREET", "EAST 236 STREET","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("EAST 236 STREET", "EAST 236st STREET","US"));
    	Assert.assertFalse(StringHelper.isSameStreetName("east 235st STREET", "EAST 236 STREET",null));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("235st STREET", "236st STREET",null));
    	
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("Stauffenbergstraße", "Stauffenberg straße","DE"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Stauffenberg straße", "Stauffenbergstraße","DE"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Stauffenberg straße", "Stauffenbergstrasse","DE"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Stauffenberg strasse", "Stauffenbergstraße","DE"));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("Phillips Street", "Philips Street",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Hilands Court", "Hiland Court",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Bethel Lane", "Bethal Lane",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Marshmallow Drive", "Marshmellow Drive",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Feureisen Avenue", "Feuereisen Avenue",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Cross Bow Lane", "CrossBow Lane",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("235st av", "235st avenue","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("235st av", "235st avenue","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Thrift Street W", "Thrift Street West","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("W Wind Ct", "West Wind Court","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("St Johns Drive", "Saint John's Drive","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Expressway Drive South", "Expressway Drive S","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("State Route 104", "sr 104","US"));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("AVENIDA ABRAO ANACHE", "Avenida Abrão Anache","BR"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Rua Arabutã", "avenida ARABUTA","BR"));
    	
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("VILA ZAMBERLAM","ERS-342 / RS-342","BR"));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("rua BRASILIA","rua 12","BR"));
    	Assert.assertFalse(StringHelper.isSameStreetName("avenida RIO BARDAUNI","BR-262","BR"));
    	
    	
    	
    	
    	
    	
    	

    	//Assert.assertTrue(StringHelper.isSameStreetName("Bodanyi Court", "Bodanyi Place",null));
    	//Assert.assertTrue(StringHelper.isSameStreetName("Comac Street", "Comac loop",null));
    	
    	
    }
    
    
    
    @Test
    public void testExpandStreetDirections(){
    	//at the end
    	Assert.assertEquals("Thrift Street west",StringHelper.expandStreetDirections("Thrift Street W"));
    	Assert.assertEquals("Thrift Street north",StringHelper.expandStreetDirections("Thrift Street N"));
    	Assert.assertEquals("Thrift Street east",StringHelper.expandStreetDirections("Thrift Street E"));
    	Assert.assertEquals("Thrift Street south",StringHelper.expandStreetDirections("Thrift Street S"));
    	
    	//at the end
    	Assert.assertEquals("west Thrift Street",StringHelper.expandStreetDirections("W Thrift Street"));
    	Assert.assertEquals("north Thrift Street",StringHelper.expandStreetDirections("N Thrift Street"));
    	Assert.assertEquals("east Thrift Street",StringHelper.expandStreetDirections("E Thrift Street"));
    	Assert.assertEquals("south Thrift Street",StringHelper.expandStreetDirections("S Thrift Street"));
    	//trim
    	Assert.assertEquals("south Thrift Street",StringHelper.expandStreetDirections("S Thrift Street "));
    	Assert.assertEquals("south Thrift Street",StringHelper.expandStreetDirections(" S Thrift Street"));
    	
    	//not direction
    	Assert.assertEquals("St Thrift Street",StringHelper.expandStreetDirections("St Thrift Street"));
    	Assert.assertEquals("Thrift Street Wa",StringHelper.expandStreetDirections("Thrift Street Wa"));
    	
    	//casse
    	Assert.assertEquals("Thrift Street west",StringHelper.expandStreetDirections("Thrift Street w"));
    }
    
    @Test
    public void testExpandStreetSynonyms(){
    	Assert.assertEquals("saint Johns Drive",StringHelper.expandStreetSynonyms("St Johns Drive"));
    	Assert.assertEquals("Johns st",StringHelper.expandStreetSynonyms("Johns st"));
    }
    
    @Test
    public void testIsSameAlternateNames(){
    	ArrayList<String> names = new ArrayList<String>();
    	Assert.assertFalse(StringHelper.isSameAlternateNames("notre dame anges",names));//empty list
    	
    	names.add("notre dame des anges");
    	Assert.assertTrue("with additionnal words size =3",StringHelper.isSameAlternateNames("notre dame anges",names));//nominal case true
    	
    	names.clear();
    	names.add("toto");
    	Assert.assertFalse(StringHelper.isSameAlternateNames("notre dame anges",names));//nominal case false
    	
    	names.clear();
    	names.add("toto");
    	names.add("notre dame des anges");
    	names.add(null);
    	Assert.assertTrue("with additionnal words size =3",StringHelper.isSameAlternateNames("notre dame anges",names));//several names
    	
    }
    
    @Test
    public void testPrepareQuery(){
    	Assert.assertEquals("4 route nationale 43 59230 serques",StringHelper.prepareQuery("4 rn 43 59 230 serques"));
    	Assert.assertEquals("4 route nationale 43 12177 serques",StringHelper.prepareQuery("4 rn 43 121 77 serques"));
    	Assert.assertEquals("route nationale 43 62910",StringHelper.prepareQuery("rn43 62 910"));
    	Assert.assertEquals("4 route nationale 43 59130",StringHelper.prepareQuery("4 rn 43 59 130"));
    	Assert.assertEquals("",StringHelper.prepareQuery(""));
    	Assert.assertEquals("4 route nationale 43 serques",StringHelper.prepareQuery("4 rn43 serques"));
    	Assert.assertEquals("4 route nationale 43 serques",StringHelper.prepareQuery("4 rn 43 serques"));
    	
    	}
    
    @Test
    public void testExpandStreetType(){
    	
    	//no str
    	Assert.assertEquals("foo",StringHelper.expandStreetType("foo", null));
    	
    	//one without point
    	Assert.assertEquals("trucstraße",StringHelper.expandStreetType("trucStr", null));
    	Assert.assertEquals("trucStrasse",StringHelper.expandStreetType("trucStrasse", null));
    	Assert.assertEquals("truc Strasse",StringHelper.expandStreetType("truc Strasse", null));
    //	Assert.assertEquals("truc straße",service.replaceGermanSynonyms("truc Str"));
    	
    	//one with point
    	Assert.assertEquals("trucstraße",StringHelper.expandStreetType("trucStr.", null));
    	Assert.assertEquals("trucStrasse.",StringHelper.expandStreetType("trucStrasse.", null));
    	Assert.assertEquals("truc Strasse.",StringHelper.expandStreetType("truc Strasse.", null));
    //	Assert.assertEquals("truc straße",service.replaceGermanSynonyms("truc Str."));
    	
    	//one without point + other word
    	Assert.assertEquals("foo trucstraße",StringHelper.expandStreetType("foo trucStr", null));
    	Assert.assertEquals("foo trucStrasse",StringHelper.expandStreetType("foo trucStrasse", null));
    	Assert.assertEquals("foo truc Strasse",StringHelper.expandStreetType("foo truc Strasse", null));
    //	Assert.assertEquals("foo truc straße",service.replaceGermanSynonyms("foo truc Str"));
    	
    	//one with point + other word
    	Assert.assertEquals("foo trucstraße",StringHelper.expandStreetType("foo trucStr.", null));
    	Assert.assertEquals("foo trucStrasse.",StringHelper.expandStreetType("foo trucStrasse.", null));
    	Assert.assertEquals("foo truc Strasse.",StringHelper.expandStreetType("foo truc Strasse.", null));
    //	Assert.assertEquals("foo truc straße",service.replaceGermanSynonyms("foo truc Str."));
    	
    	
    	Assert.assertEquals("trucstraße",StringHelper.expandStreetType("trucStr", "DE"));
    	Assert.assertEquals("trucstraße",StringHelper.expandStreetType("trucStr", "AT"));
    	Assert.assertEquals("trucstrasse",StringHelper.expandStreetType("trucStr", "CH"));
    	Assert.assertEquals("trucstraat",StringHelper.expandStreetType("trucStr", "NL"));
    	Assert.assertEquals("trucstræde",StringHelper.expandStreetType("trucStr", "DK"));
    	Assert.assertEquals("trucstrada",StringHelper.expandStreetType("trucStr", "MD"));
    	
    	
    	//for FR
    	Assert.assertEquals("rue de la vallée",StringHelper.expandStreetType("r de la vallée", "FR"));
    	Assert.assertEquals("rue de la vallée",StringHelper.expandStreetType("r. de la vallée", "FR"));
    	Assert.assertEquals("rue de la vallée",StringHelper.expandStreetType("r de la vallée", "CA"));
    	Assert.assertEquals("rue de la vallée",StringHelper.expandStreetType("r. de la vallée", "FR"));
    	Assert.assertEquals("rue de la vallée",StringHelper.expandStreetType("rue de la vallée", "FR"));
    	
    	//sp
    	Assert.assertEquals("calle foo",StringHelper.expandStreetType("c foo", "ES"));
    	Assert.assertEquals("card foo",StringHelper.expandStreetType("card foo", "ES"));
    	
    	//pt
    	Assert.assertEquals("rua foo",StringHelper.expandStreetType("r foo", "PT"));
    	Assert.assertEquals("avenida foo",StringHelper.expandStreetType("ave foo", "BR"));
    	
    	//it
    	Assert.assertEquals("calle foo",StringHelper.expandStreetType("c foo", "IT"));
    	Assert.assertEquals("via foo",StringHelper.expandStreetType("v foo", "IT"));
    	Assert.assertEquals("card foo",StringHelper.expandStreetType("card foo", "IT"));
    	
    	//us
    	Assert.assertEquals("foo avenue",StringHelper.expandStreetType("foo ave", "US"));
    	Assert.assertEquals("foo avenue",StringHelper.expandStreetType("foo av.", "US"));
    	Assert.assertEquals("foo court",StringHelper.expandStreetType("foo Ct", "US"));
    	
    }
    
    
    @Test
    public void testRemoveStreetType(){
    	
    	//no str
    	Assert.assertEquals("foo",StringHelper.removeStreetType("foo", null));
    	Assert.assertEquals(null,StringHelper.removeStreetType(null, null));
    	
    	//one without point
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", null));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStrasse", null));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("truc Strasse", null));
    //	Assert.assertEquals("truc straße",service.replaceGermanSynonyms("truc Str"));
    	
    	//one with point
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr.", null));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStrasse.", null));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("truc Strasse.", null));
    //	Assert.assertEquals("truc straße",service.replaceGermanSynonyms("truc Str."));
    	
    	//one without point + other word
    	Assert.assertEquals("foo truc",StringHelper.removeStreetType("foo trucStr", null));
    	Assert.assertEquals("foo truc",StringHelper.removeStreetType("foo trucStrasse", null));
    	Assert.assertEquals("foo truc",StringHelper.removeStreetType("foo truc Strasse", null));
    //	Assert.assertEquals("foo truc straße",service.replaceGermanSynonyms("foo truc Str"));
    	
    	//one with point + other word
    	Assert.assertEquals("foo truc",StringHelper.removeStreetType("foo trucStr.", null));
    	Assert.assertEquals("foo truc",StringHelper.removeStreetType("foo trucStrasse.", null));
    	Assert.assertEquals("foo truc",StringHelper.removeStreetType("foo truc Strasse.", null));
    //	Assert.assertEquals("foo truc straße",service.replaceGermanSynonyms("foo truc Str."));
    	
    	
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", "DE"));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", "AT"));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", "CH"));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", "NL"));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", "DK"));
    	Assert.assertEquals("truc",StringHelper.removeStreetType("trucStr", "MD"));
    	
    	
    	//for FR
    	Assert.assertEquals("de la vallée",StringHelper.removeStreetType("r de la vallée", "FR"));
    	Assert.assertEquals("de la vallée",StringHelper.removeStreetType("r. de la vallée", "FR"));
    	Assert.assertEquals("de la vallée",StringHelper.removeStreetType("r de la vallée", "CA"));
    	Assert.assertEquals("de la vallée",StringHelper.removeStreetType("r. de la vallée", "FR"));
    	Assert.assertEquals("de la vallée",StringHelper.removeStreetType("rue de la vallée", "FR"));
    	
    	//sp
    	Assert.assertEquals("foo",StringHelper.removeStreetType("c foo", "ES"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("calle foo", "ES"));
    	Assert.assertEquals("card foo",StringHelper.removeStreetType("card foo", "ES"));
    	
    	Assert.assertEquals("foo",StringHelper.removeStreetType("r foo", "PT"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("ave foo", "BR"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("avenida foo", "BR"));
    	
    	//it
    	Assert.assertEquals("foo",StringHelper.removeStreetType("c foo", "IT"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("calle foo", "IT"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("v foo", "IT"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("via foo", "IT"));
    	Assert.assertEquals("card foo",StringHelper.removeStreetType("card foo", "IT"));
    	
    	//us
    	Assert.assertEquals("foo",StringHelper.removeStreetType("foo ave", "US"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("foo av.", "US"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("foo Ct", "US"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("foo avenue", "US"));
    	Assert.assertEquals("foo",StringHelper.removeStreetType("foo Court", "US"));
    	
    }
    
    @Test
    public void testMinDistance(){
        Assert.assertEquals(1,StringHelper.minDistance("grenoble","grenobl"));
        Assert.assertEquals(1,StringHelper.minDistance("grenoble","renoble"));
        Assert.assertEquals(1,StringHelper.minDistance("grenoble","grennoble"));
        Assert.assertEquals(0,StringHelper.minDistance("grenoble","grenoble"));
        Assert.assertEquals(2,StringHelper.minDistance("grenoble","grneoble"));
    }
    
    @Test
    public void countSameOrApprox(){
        Assert.assertEquals(2,StringHelper.countSameOrApprox("docteur schweitzer grenoble","docteur schweitzer 51240 paris"));
        Assert.assertEquals(3,StringHelper.countSameOrApprox("docteur schweitzer grenoble","docteur schweitzer grenoble"));
    }
    
    


}
