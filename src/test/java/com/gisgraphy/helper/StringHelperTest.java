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
import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.gisgraphy.domain.geoloc.entity.OpenStreetMap;
import com.gisgraphy.geocoding.GeocodingService;
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
    	
    	Assert.assertTrue("synonyms in expected",StringHelper.isSameName("st omer","saint omer"));
    	Assert.assertTrue("synonyms in actual",StringHelper.isSameName("saint omer","st omer"));
    	
    	Assert.assertTrue("synonyms in expected case sensitive",StringHelper.isSameName("st omer","Saint omer"));
    	Assert.assertTrue("synonyms in actual case sensitive",StringHelper.isSameName("Saint omer","st omer"));
    	
    	Assert.assertFalse(StringHelper.isSameName("Les Chézeaux","Les grand Chézeaux"));
    	Assert.assertFalse("less long word but different",StringHelper.isSameName("route pepere","pepere"));
    	
    	Assert.assertTrue(StringHelper.isSameName("Stauffenbergstraße", "Stauffenberg straße"));
    	
    	
    }
    
    @Test
    public void isSameStreetNameTest(){
    
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
    	
    	Assert.assertTrue("synonyms in expected case sensitive",StringHelper.isSameStreetName("st omer","Saint omer",null));
    	Assert.assertTrue("synonyms in actual case sensitive",StringHelper.isSameStreetName("Saint omer","st omer",null));
    	
    	Assert.assertFalse(StringHelper.isSameStreetName("Les Chézeaux","Les grand Chézeaux",null));
    	Assert.assertFalse("less long word but different",StringHelper.isSameStreetName("route pepere","pepere",null));
    	
    
    	Assert.assertTrue(StringHelper.isSameStreetName("EAST 236st STREET", "EAST 236 STREET","US"));
    	Assert.assertFalse(StringHelper.isSameStreetName("east 235st STREET", "EAST 236 STREET",null));
    	
    	
    	Assert.assertTrue(StringHelper.isSameStreetName("Stauffenbergstraße", "Stauffenberg straße","DE"));
    	Assert.assertTrue(StringHelper.isSameStreetName("Stauffenberg straße", "Stauffenbergstraße","DE"));
    	
    	
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
    


}
