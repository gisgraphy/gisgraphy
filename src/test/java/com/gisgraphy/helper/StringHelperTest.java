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
        Assert.assertTrue(StringHelper.isSameStreetName("Canada DR", "Canada DR",""));
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
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("Edward Drive", "Edwards Drive","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Edward Drive", "Edwards Dr","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Edwards St", "N Edward Dr","US"));
    	Assert.assertTrue("not same street type should return true",StringHelper.isSameStreetName("Edward Drive", "Edwards st","US"));
    	
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("EAST 236st STREET", "EAST 236 STREET","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("EAST 236 STREET", "EAST 236st STREET","US"));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("EAST 236 STREET", "E 236st STREET","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("E 236 STREET", "EAst 236st STREET","US"));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("236 STREET S", "236st STREET South","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("236 STREET South", "236st STREET S","US"));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("east 235st STREET", "EAST 236 STREET",null));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("235st STREET", "236st STREET",null));
    	
    	//numbered road
    	Assert.assertTrue(StringHelper.isSameStreetName("road 32", "road 32","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("road", "road","US"));
        
        Assert.assertFalse(StringHelper.isSameStreetName("road 32 1", "road 32","US"));
        Assert.assertFalse(StringHelper.isSameStreetName("road 32", "road 32 1","US"));
        Assert.assertFalse(StringHelper.isSameStreetName("road 32 1", "1 road 32","US"));
    	
    	
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
    	Assert.assertTrue(StringHelper.isSameStreetName("State Rte 128", "route 128","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("E State Rte 128", "East route 128","US"));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("Phillip s Street", "Philips Street",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Phillip's Street", "Philips Street",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Phillips Street", "Philip's Street",null));
    	Assert.assertTrue(StringHelper.isSameStreetName("Phillips Street", "Philip s Street",null));
    	
    	//ordinal
    	Assert.assertTrue(StringHelper.isSameStreetName("East 1st route 128", "East first route 128","US"));
    	
    	//direction
    	Assert.assertTrue(StringHelper.isSameStreetName("12th Avenue Northeast", "12th Ave NE","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("12th Avenue NE", "12th Ave Northeast","US"));
    	Assert.assertTrue(StringHelper.isSameStreetName("12th Avenue North East", "12th Ave NE","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("12th Avenue NE", "12th Ave North East","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("12th Avenue Northeast", "12th Ave North East","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("12th Avenue North East", "12th Ave Northeast","US"));
        
        Assert.assertTrue(StringHelper.isSameStreetName("Tyler Parkway", "Tyler Pkwy","US"));
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("East 1st route 128", "East first route 128",null));
    	
        Assert.assertTrue(StringHelper.isSameStreetName("dr scheitzer street","dr scheitzer st","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("doctor scheitzer street","dr scheitzer st","US"));

        Assert.assertTrue(StringHelper.isSameStreetName("O'Niell Street","O'Neil St","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("Saint Thomas Trail","St Thomas Trl","US"));
        Assert.assertTrue(StringHelper.isSameStreetName("Kensington Terrace","Kensington Ter","US"));
       
    	
    	//complex one
    	   Assert.assertTrue(StringHelper.isSameStreetName("E 1st rt", "East first route","US"));
    	
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("AVENIDA ABRAO ANACHE", "Avenida Abrão Anache","BR"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Rua Arabutã", "avenida ARABUTA","BR"));
    	
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("VILA ZAMBERLAM","ERS-342 / RS-342","BR"));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("rua BRASILIA","rua 12","BR"));
    	Assert.assertFalse(StringHelper.isSameStreetName("avenida RIO BARDAUNI","BR-262","BR"));
    	
    	//Assert.assertTrue(StringHelper.isSameStreetName("Bodanyi Court", "Bodanyi Place",null));
    	//Assert.assertTrue(StringHelper.isSameStreetName("Comac Street", "Comac loop",null));
    	
    	
    }
    
    @Test
    public void cleanupStreetName(){
        Assert.assertEquals("1st avenue", StringHelper.cleanupStreetName("01st  avenue "));
        Assert.assertEquals(null, StringHelper.cleanupStreetName(null));
        
        String longString = RandomStringUtils.random(StringHelper.MAX_STREET_NAME_SIZE+10,new char[] {'e'});
        longString = StringHelper.cleanupStreetName(longString);
        Assert.assertEquals("the string to test is not of the expected size the test will fail",StringHelper.MAX_STREET_NAME_SIZE, longString.length());
    }
    
    @Test
    public void testCorrectStreetName(){
        Assert.assertEquals("County Highway J2", StringHelper.correctStreetName("Co Hwy J2","US"));
        
        Assert.assertEquals("County Highway J2", StringHelper.correctStreetName("co hwy J2","US"));
        
        Assert.assertEquals("East County Highway J2", StringHelper.correctStreetName("E co hwy J2","US"));
        
        Assert.assertEquals("State Route 79 South", StringHelper.correctStreetName("State Rte 79 S","US"));
        
        Assert.assertEquals("East State Highway 4", StringHelper.correctStreetName("E State Hwy 4","US"));
        
        Assert.assertEquals("East Foo 4", StringHelper.correctStreetName("E foo 4","US"));
        Assert.assertEquals("Foo 4 South", StringHelper.correctStreetName("foo 4 S","US"));
        
        Assert.assertEquals("East 1st Route", StringHelper.correctStreetName(" E first rt","US"));
        
        Assert.assertEquals("1st Route North West", StringHelper.correctStreetName("first rt NW","US"));
        
        Assert.assertEquals("1st Route North West", StringHelper.correctStreetName("first rt North West","US"));
        
        Assert.assertEquals("Sandy Terrace", StringHelper.correctStreetName("Sandy Ter","US"));
        Assert.assertEquals("Veterans Square", StringHelper.correctStreetName("Veterans Sq","US"));
        
       
        
        Assert.assertEquals("East Foo 4", StringHelper.correctStreetName("East foo 4","US"));
        
        Assert.assertEquals("Efoo 4", StringHelper.correctStreetName("Efoo 4","US"));
        Assert.assertEquals("Efoo Bars", StringHelper.correctStreetName("efoo bars","US"));
        Assert.assertEquals("West North 1st Street",StringHelper.correctStreetName("W North First St","US"));
        
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
    	Assert.assertEquals("west Thrift Street",StringHelper.expandStreetDirections("west Thrift Street"));
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
    	
    	//two direction
    	Assert.assertEquals("Thrift Street south west",StringHelper.expandStreetDirections("Thrift Street SW"));
    	
    	//composed
    	Assert.assertEquals("Thrift Street south west",StringHelper.expandStreetDirections("Thrift Street SouthWest"));
    	
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
    	Assert.assertEquals("4 route nationale 43 59230 serques",StringHelper.prepareQuery("4 rn 43 59 230 serques").replaceAll("\\s\\s", " "));
    	Assert.assertEquals("4 route nationale 43 12177 serques",StringHelper.prepareQuery("4 rn 43 121 77 serques"));
    	Assert.assertEquals("route nationale 43 62910",StringHelper.prepareQuery("rn43 62 910").replaceAll("\\s\\s", " "));
    	Assert.assertEquals("4 route nationale 43 59130",StringHelper.prepareQuery("4 rn 43 59 130").replaceAll("\\s\\s", " "));
    	Assert.assertEquals("",StringHelper.prepareQuery(""));
    	Assert.assertEquals("4 route nationale 43 serques",StringHelper.prepareQuery("4 rn43 serques"));
    	Assert.assertEquals("4 route nationale 43 serques",StringHelper.prepareQuery("4 rn 43 serques"));
    	Assert.assertEquals("59310 foo",StringHelper.prepareQuery("59 310 foo"));
    	
    	}
    
    @Test
    public void testIsSameRoadNumber(){
        Assert.assertTrue(StringHelper.isSameNumberRoad("road 32", "road 32"));
        Assert.assertTrue(StringHelper.isSameNumberRoad("road", "road"));
        
        Assert.assertFalse(StringHelper.isSameNumberRoad("road 32 1", "road 32"));
        Assert.assertFalse(StringHelper.isSameNumberRoad("road 32", "road 32 1"));
        Assert.assertFalse(StringHelper.isSameNumberRoad("road 32 1", "1 road 32"));
    }
    
    @Test
    public void testExpandStreetType(){
        
        
        Assert.assertEquals("CA COUNTY road 150",StringHelper.expandStreetType("CA COUNTY RD 150", "US"));
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
    	Assert.assertEquals("foo street",StringHelper.expandStreetType("foo st", "US"));
    	Assert.assertEquals("foo drive",StringHelper.expandStreetType("foo dr", "US"));
    	Assert.assertEquals("route 60",StringHelper.expandStreetType("rte 60", "US"));
    	Assert.assertEquals("route july 7th 1980",StringHelper.expandStreetType("rte july 7th 1980", "US"));
    	Assert.assertEquals("state route 60",StringHelper.expandStreetType("state rte 60", "US"));
    	Assert.assertEquals("state route 60",StringHelper.expandStreetType("sr 60", "US"));
    	Assert.assertEquals("80th street",StringHelper.expandStreetType("80th St", "US"));
    	
    	Assert.assertEquals("80th street",StringHelper.expandStreetType("80th St", "US"));
    	Assert.assertEquals("State highway 89",StringHelper.expandStreetType("State Hwy 89", "US"));
    	Assert.assertEquals("US highway 50",StringHelper.expandStreetType("US Hwy 50", "US"));
    	Assert.assertEquals("county road 50",StringHelper.expandStreetType("county rd 50", "US"));
    	Assert.assertEquals("county road 50",StringHelper.expandStreetType("co rd 50", "US"));
    	Assert.assertEquals("copernic 50",StringHelper.expandStreetType("copernic 50", "US"));
    	Assert.assertEquals("county highway 50",StringHelper.expandStreetType("county hwy 50", "US"));
    	Assert.assertEquals("Blake Mountian trail",StringHelper.expandStreetType("Blake Mountian Trl", "US"));
    	
    	Assert.assertEquals("Business Loop 50",StringHelper.expandStreetType("Business Loop 50", "US"));
    	Assert.assertEquals("Business Interstate highway 50",StringHelper.expandStreetType("Business Interstate Hwy 50", "US"));
    	Assert.assertEquals("Business Interstate highway No. 35-M",StringHelper.expandStreetType("Business Interstate Highway No. 35-M", "US"));
    	Assert.assertEquals("interstate 40",StringHelper.expandStreetType("I-40", "US"));
    	Assert.assertEquals("interstate 205 Bus",StringHelper.expandStreetType("I- 205 Bus", "US"));
    	Assert.assertEquals("interstate 40BUS",StringHelper.expandStreetType("I-40BUS", "US"));
    	Assert.assertEquals("Forest route 7N09",StringHelper.expandStreetType("Forest Rte 7N09", "US"));
    	Assert.assertEquals("Big Tee drive",StringHelper.expandStreetType("Big Tee Dr", "US"));
    	
    	Assert.assertEquals("foo route N",StringHelper.expandStreetType("foo rt N","US"));
    	Assert.assertEquals("East 2",StringHelper.expandStreetType("East 2","US"));
    	Assert.assertEquals("North 7",StringHelper.expandStreetType("North 7","US"));
    	
    	
    	
    	
    	Assert.assertEquals("State route 79 S",StringHelper.expandStreetType("State Rte 79 S", "US"));
    	Assert.assertEquals("E State highway 4",StringHelper.expandStreetType("E State Hwy 4", "US"));
    	Assert.assertEquals("rural route 4",StringHelper.expandStreetType("rr 4", "US"));
    	Assert.assertEquals("rural route 4",StringHelper.expandStreetType("rural rte 4", "US"));
    	Assert.assertEquals("East route 128",StringHelper.expandStreetType("East rt 128", "US"));
    	Assert.assertEquals("RANCH road 620",StringHelper.expandStreetType("RANCH RD 620", "US"));
    	 Assert.assertEquals("E foo 4", StringHelper.expandStreetType("E foo 4","US"));
    	 
    	 //with confusing streetname not at the end
    	 Assert.assertEquals("E st john street", StringHelper.expandStreetType("E st john st","US"));
    	 Assert.assertEquals("st john street E", StringHelper.expandStreetType("st john st E","US"));
    	
    	
    	
    
    	
    	//strange case 
    	Assert.assertEquals("W North First street",StringHelper.expandStreetType("W North First St", "US"));
    	 	
    	
    	
    	
    	Assert.assertEquals("county highway J2",StringHelper.expandStreetType("Co Hwy J2", "US"));
    	Assert.assertEquals("Cot Hwy J2",StringHelper.expandStreetType("Cot Hwy J2", "US"));
    	
    	Assert.assertEquals("county highway N7",StringHelper.expandStreetType("Co Hwy N7", "US"));
    	Assert.assertEquals("county highway 2",StringHelper.expandStreetType("Co Hwy 2", "US"));
    	Assert.assertEquals("business 40",StringHelper.expandStreetType("business 40", "US"));
    	//Forest Rte 7N09
    	
    	/*
    	 * Business Loop
     * Business Interstate Highway No. 35-M
     * Business Interstate 35-M
     * Business spur
     * I-40BUS
     * business 40
     * state route 43
    	 */
    	
    	Assert.assertEquals("US foo 50",StringHelper.expandStreetType("US foo 50", "US"));
    	
    	
    }
    
    @Test
    public void testExpandOrdinalText(){
        Assert.assertEquals("foo",StringHelper.expandOrdinalText("foo"));
        Assert.assertEquals(null,StringHelper.expandOrdinalText(null));
        Assert.assertEquals("this is the 1st st NE",StringHelper.expandOrdinalText("this is the first st NE"));
        Assert.assertEquals("this is the 2nd st NE",StringHelper.expandOrdinalText("this is the second st NE"));
        Assert.assertEquals("this is the 3rd st NE",StringHelper.expandOrdinalText("this is the third st NE"));
        Assert.assertEquals("this is the 4th st NE",StringHelper.expandOrdinalText("this is the fourth st NE"));
        Assert.assertEquals("this is the 5th st NE",StringHelper.expandOrdinalText("this is the fifth st NE"));
        Assert.assertEquals("this is the 6th st NE",StringHelper.expandOrdinalText("this is the sixth st NE"));
        Assert.assertEquals("this is the 7th st NE",StringHelper.expandOrdinalText("this is the seventh st NE"));
        Assert.assertEquals("this is the 8th st NE",StringHelper.expandOrdinalText("this is the eighth st NE"));
        Assert.assertEquals("this is the 9th st NE",StringHelper.expandOrdinalText("this is the ninth st NE"));
        Assert.assertEquals("this is the 10th st NE",StringHelper.expandOrdinalText("this is the tenth st NE"));
        Assert.assertEquals("this is the 11th st NE",StringHelper.expandOrdinalText("this is the eleventh st NE"));
        Assert.assertEquals("this is the 12th st NE",StringHelper.expandOrdinalText("this is the twelfth st NE"));
        Assert.assertEquals("this is the 13th st NE",StringHelper.expandOrdinalText("this is the thirteenth st NE"));
        Assert.assertEquals("this is the 14th st NE",StringHelper.expandOrdinalText("this is the fourteenth st NE"));
        Assert.assertEquals("this is the 15th st NE",StringHelper.expandOrdinalText("this is the fifteenth st NE"));
        Assert.assertEquals("this is the 16th st NE",StringHelper.expandOrdinalText("this is the sixteenth st NE"));
        Assert.assertEquals("this is the 17th st NE",StringHelper.expandOrdinalText("this is the seventeenth st NE"));
        Assert.assertEquals("this is the 18th st NE",StringHelper.expandOrdinalText("this is the eighteenth st NE"));
        Assert.assertEquals("this is the 19th st NE",StringHelper.expandOrdinalText("this is the nineteenth st NE"));
        Assert.assertEquals("this is the 20th st NE",StringHelper.expandOrdinalText("this is the twentieth st NE"));
    }
    
    @Test
    public void testGetExpandedStreetPrefix(){
        Assert.assertEquals("foo",StringHelper.getExpandedStreetPrefix("foo"));
        Assert.assertEquals(null,StringHelper.getExpandedStreetPrefix(null));
        Assert.assertEquals("county",StringHelper.getExpandedStreetPrefix("co"));
    }
    
    @Test
    public void testRemoveDirection(){
        Assert.assertEquals("foo",StringHelper.removeDirection("foo"));
        Assert.assertEquals(null,StringHelper.removeDirection(null));
        
        //starts
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("South Vista de la Luna"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("north Vista de la Luna"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("East Vista de la Luna"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("West Vista de la Luna"));
        
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("S Vista de la Luna "));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("n Vista de la Luna"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("e Vista de la Luna"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("W Vista de la Luna"));
        
        //two directions
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("S Vista de la Luna E"));
        Assert.assertEquals("Viseta de la Luna",StringHelper.removeDirection("S Viseta de la Luna SE"));
        
        //ends
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("Vista de la Luna S"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("Vista de la Luna N"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("Vista de la Luna E"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection("Vista de la Luna W"));
        
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection(" Vista de la Luna south"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection(" Vista de la Luna north"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection(" Vista de la Luna East"));
        Assert.assertEquals("Vista de la Luna",StringHelper.removeDirection(" Vista de la Luna west"));
        
        
        
        //starts but not a dir
        Assert.assertEquals("na Vista de la Luna",StringHelper.removeDirection("na Vista de la Luna"));
        //ends but not a dir
        Assert.assertEquals("na Vista de la Lun",StringHelper.removeDirection("na Vista de la Lun"));
    }
    
    @Test
    public void testRemoveStreetType(){
    	
       // State Hwy 89/US Hwy 50
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
    
    @Test
    public void testHasDirection(){
        //trim
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street west "));
        Assert.assertTrue(StringHelper.hasDirection(" Thrift Street west "));

        //case
        Assert.assertTrue(StringHelper.hasDirection(" Thrift Street WeSt"));
        Assert.assertTrue(StringHelper.hasDirection("WeSt Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("W Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("w Thrift Street"));
        
        Assert.assertFalse(StringHelper.hasDirection(null));
        
        Assert.assertFalse(StringHelper.hasDirection("foo"));
        
        
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street west"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street w"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street N"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street north"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street E"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street east"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street S"));
        Assert.assertTrue(StringHelper.hasDirection("Thrift Street South"));
        
        
        Assert.assertTrue(StringHelper.hasDirection("west Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("W Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("north Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("N Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("east Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("E Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("south Thrift Street"));
        Assert.assertTrue(StringHelper.hasDirection("S Thrift Street"));
        
    }
    
    @Test
    public void testIsNotRef(){
        Assert.assertFalse(StringHelper.isRef(""));
        
        
        Assert.assertTrue(StringHelper.isRef("state route 43"));
        Assert.assertTrue(StringHelper.isRef("sr 43"));
        Assert.assertTrue(StringHelper.isRef("SR 43"));
        Assert.assertTrue(StringHelper.isRef("E sr 43"));
        Assert.assertTrue(StringHelper.isRef("state hwy "));
        Assert.assertTrue(StringHelper.isRef("state road "));
        Assert.assertTrue(StringHelper.isRef("business loop"));
        Assert.assertTrue(StringHelper.isRef("I-42"));
       // Assert.assertTrue(StringHelper.isRef("I42"));
        Assert.assertTrue(StringHelper.isRef("I-40BUS"));
        Assert.assertTrue(StringHelper.isRef("Co Hwy J2"));
        Assert.assertTrue(StringHelper.isRef("Forest Rte 7N09"));
        Assert.assertFalse(StringHelper.isRef("foo highway"));
     /*   Assert.assertTrue(StringHelper.isRef(""));
        Assert.assertTrue(StringHelper.isRef(""));
        Assert.assertTrue(StringHelper.isRef(""));
        Assert.assertTrue(StringHelper.isRef(""));
        Assert.assertTrue(StringHelper.isRef(""));
        Assert.assertTrue(StringHelper.isRef(""));*/
    }


}
