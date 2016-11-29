package com.gisgraphy.addressparser.format;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.TriggeringEventEvaluator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gisgraphy.addressparser.Address;
import com.gisgraphy.addressparser.StreetTypeOrder;

/**
 *  @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 *
 */
public class AddressFormaterTest {

    private static final Address US_CA_ADDRESS;
    private static final Address US_CA_INCOMPLETE_ADDRESS;
    private static final Address CN_ADDRESS;
    private static final Address VN_ADDRESS;
    private BasicAddressFormater formater;

    @Before
    public void setup() {
	formater = BasicAddressFormater.getInstance();

    }

    static {
	US_CA_ADDRESS = new Address();
	US_CA_ADDRESS.setCountryCode("US");
	US_CA_ADDRESS.setState("CA");
	US_CA_ADDRESS.setCity("Mt View");
	US_CA_ADDRESS.setHouseNumber("1098");
	US_CA_ADDRESS.setStreetName("Alta");
	US_CA_ADDRESS.setStreetType("Ave");
	US_CA_ADDRESS.setZipCode("94043");
	
	VN_ADDRESS = new Address();
	VN_ADDRESS.setCountryCode("VN");
	VN_ADDRESS.setState("CA");
	VN_ADDRESS.setCity("Mt View");
	VN_ADDRESS.setHouseNumber("1098");
	VN_ADDRESS.setStreetName("Alta");
	VN_ADDRESS.setStreetType("Ave");
	VN_ADDRESS.setZipCode("94043");

	US_CA_INCOMPLETE_ADDRESS = new Address();
	US_CA_INCOMPLETE_ADDRESS.setCountryCode("US");
	US_CA_INCOMPLETE_ADDRESS.setState("CA");
	US_CA_INCOMPLETE_ADDRESS.setHouseNumber("1098");
	US_CA_INCOMPLETE_ADDRESS.setStreetName("Alta");
	US_CA_INCOMPLETE_ADDRESS.setStreetType("Ave");
	US_CA_INCOMPLETE_ADDRESS.setZipCode("94043");

	CN_ADDRESS = new Address();
	CN_ADDRESS.setCountryCode("CN");
	CN_ADDRESS.setState("\u53F0\u5317\u5E02"); // Taipei city
	CN_ADDRESS.setCity("\u5927\u5B89\u5340"); // Da-an district
	CN_ADDRESS.setHouseNumber("3");
	CN_ADDRESS.setStreetName("Hsin-yi");
	CN_ADDRESS.setStreetType("Rd.");
	CN_ADDRESS.setZipCode("106");
	CN_ADDRESS.setRecipientName("Mr. Liu");
    }

    @Test
    public void constructor() {
    	BasicAddressFormater.getInstance();
    }

   
@Test
public void testCitySubdivision(){
	Address address =new Address();
	address.setCitySubdivision("citySubdivision");
	address.setCity("city");
	address.setStreetName("street");
	address.setStreetType("type");
	
	String real = formater.getEnvelopeAddress(address, ScriptType.LTR, DisplayMode.COMMA);
	Assert.assertEquals("street type, citySubdivision, city", real);
	
	real = formater.getEnvelopeAddress(address, ScriptType.LTR, DisplayMode.SINGLE_LINE);
	Assert.assertEquals("street type citySubdivision, city", real);
}
   

    @Test
    public void testLines_US() {
	List<String> expected = new ArrayList<String>();
	expected.add("1098 Alta Ave");
	expected.add("Mt View, CA 94043");

	List<String> real = formater.getLines(US_CA_ADDRESS);
	for (int i = 0; i < expected.size(); i++) {
	    Assert.assertEquals(expected.get(i), real.get(i));
	}
    }

    @Test
    public void testEnvelopeAddress_US_ALLMODE() {
	List<String> expected = new ArrayList<String>();
	expected.add("1098 Alta Ave");
	expected.add("Mt View, CA 94043");

	String real = formater.getEnvelopeAddress(US_CA_ADDRESS, DisplayMode.SINGLE_LINE);
	Assert.assertEquals(expected.get(0) + " " + expected.get(1), real);

	real = formater.getEnvelopeAddress(US_CA_ADDRESS, DisplayMode.HTML);
	Assert.assertEquals(expected.get(0) + "<br/>" + expected.get(1), real);

	real = formater.getEnvelopeAddress(US_CA_ADDRESS, DisplayMode.ENVELOPE);
	Assert.assertEquals(expected.get(0) + "\r\n" + expected.get(1), real);
    }

  
    @Test
    public void testEnvelopeAddress_CN_LTR_ALLMODE() {
	List<String> expected = new ArrayList<String>();
	expected.add("Mr. Liu");
	expected.add("3 Hsin-yi Rd.");
	expected.add("\u5927\u5B89\u5340, \u53F0\u5317\u5E02 106");
							      // Da-an district

	String real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.LTR, DisplayMode.SINGLE_LINE);
	Assert.assertEquals(BasicAddressFormater.join(expected, " "), real);

	real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.LTR, DisplayMode.HTML);
	Assert.assertEquals(BasicAddressFormater.join(expected, "<br/>"), real);

	real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.LTR, DisplayMode.ENVELOPE);
	Assert.assertEquals(BasicAddressFormater.join(expected, "\r\n"), real);
	
	real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.LTR, DisplayMode.COMMA);
	Assert.assertEquals(BasicAddressFormater.join(expected, ", "), real);
    }

    @Test
    public void testEnvelopeAddress_CN_RTL_ALLMODE() {
	List<String> expected = new ArrayList<String>();
	expected.add("106, \u53F0\u5317\u5E02");
	expected.add("\u5927\u5B89\u5340"); // Taipei city, Da-an district
	expected.add("Rd. Hsin-yi, 3");
	expected.add("Mr. Liu");

	String real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.RTL, DisplayMode.SINGLE_LINE);
	Assert.assertEquals(BasicAddressFormater.join(expected, " "), real);

	real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.RTL, DisplayMode.HTML);
	Assert.assertEquals(BasicAddressFormater.join(expected, "<br/>"), real);

	real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.RTL, DisplayMode.ENVELOPE);
	Assert.assertEquals(BasicAddressFormater.join(expected, "\r\n"), real);
	
	real = formater.getEnvelopeAddress(CN_ADDRESS, ScriptType.RTL, DisplayMode.COMMA);
	Assert.assertEquals(BasicAddressFormater.join(expected, ", "), real);
	System.out.println(real);
    }

    @Test
    public void testState_state() {
	Address address = new Address();
	address.setStreetName("california");
	address.setStreetType("street");
	address.setState("ca");
	address.setCountryCode("US");

	String real = formater.getEnvelopeAddress(address,DisplayMode.COMMA);
	assertEquals("california street, ca", real);

    }
    
    @Test
    public void testPoboxShouldFallBackOnDefaultPatternIfThereIsNoPOBoxInfo(){
    	Address address= new Address();
    	Assert.assertEquals(formater.getCountryInfo("ZZ").getFormatString(), formater.getFormatString(ScriptType.LTR, "AG", address));
    	Assert.assertEquals(formater.getCountryInfo("ZZ").getFormatString(), formater.getFormatString(ScriptType.RTL, "AG", address));
    	
    }
    
    @Test
    public void testPobox(){
    	Address address= new Address();
    	address.setPOBox("pOBox");
    	address.setPOBoxAgency("POBoxAgency");
    	//address.setPOBoxInfo("boxInfo");
    	address.setPostOfficeBox("postOfficeBox");
    	Assert.assertEquals(formater.getCountryInfo("AG").getFormatString(), formater.getFormatString(ScriptType.LTR, "AG", address));
    	Assert.assertEquals(formater.getCountryInfo("AG").getFormatString(), formater.getFormatString(ScriptType.RTL, "AG", address));
    	
    }
    
    @Test
    public void testState_statelevel() {
	Address address = new Address();
	address.setStreetName("california");
	address.setStreetType("street");
	address.setAdm3Name("ca");
	address.setCountryCode("TR");

	String real = formater.getEnvelopeAddress(address,DisplayMode.COMMA);
	assertEquals("california street, ca", real);
    }
    
    @Test
    public void testState_statelevel_fallback() {
	Address address = new Address();
	address.setStreetName("california");
	address.setStreetType("street");
	address.setAdm1Name("ca");
	address.setCountryCode("TR");

	String real = formater.getEnvelopeAddress(address,DisplayMode.COMMA);
	assertEquals("california street, ca", real);
    }

    @Test
    public void testEnvelopeAddressIncompleteAddress() {
	List<String> expected = new ArrayList<String>();
	expected.add("1098 Alta Ave");
	expected.add("CA 94043");

	List<String> real = formater.getLines(US_CA_INCOMPLETE_ADDRESS);

	assertEquals(expected, real);
    }

    @Test
    public void testEnvelopeAddressEmptyAddress() {
	List<String> expected = new ArrayList<String>();
	Address address = new Address();
	address.setCountryCode("US");

	List<String> real = formater.getLines(address);
	assertEquals(expected, real);

	address = new Address();
	real = formater.getLines(address);
	assertEquals(expected, real);
    }
    
    @Test
    public void testStreetTypeThenName() {
	List<String> expected = new ArrayList<String>();
	expected.add("california street");
	Address address = new Address();
	address.setStreetName("california");
	address.setStreetType("street");
	address.setCountryCode("US");

	List<String> real = formater.getLines(address);
	assertEquals(expected, real);

    }
    
    @Test
    public void testStreetNameThenType() {
	List<String> expected = new ArrayList<String>();
	expected.add("rue jean moulin");
	Address address = new Address();
	address.setStreetName("jean moulin");
	address.setStreetType("rue");
	address.setCountryCode("FR");

	List<String> real = formater.getLines(address);
	assertEquals(expected, real);
    }
    
    
    @Test
    public void detectStreetTypeOrderFromAddress(){
	Assert.assertEquals("wrong street type for null address",StreetTypeOrder.unknow,formater.detectStreetTypeOrderFromAddress(null));
	Address address =new Address();
	Assert.assertEquals("wrong street type for null address",StreetTypeOrder.unknow,formater.detectStreetTypeOrderFromAddress(address));
	
	 address =new Address();
	 address.setCountryCode("zz");
	 Assert.assertEquals("wrong street type for wrong countrycode",StreetTypeOrder.unknow,formater.detectStreetTypeOrderFromAddress(address));
	 
	 address =new Address();
	 address.setCountryCode("US");
	 Assert.assertEquals("wrong street type for good countrycode",StreetTypeOrder.nameThenType,formater.detectStreetTypeOrderFromAddress(address));
	 
	 address =new Address();
	 address.setCountryCode("CA");
	 Assert.assertEquals("wrong street type for ambiguous countrycode",StreetTypeOrder.unknow,formater.detectStreetTypeOrderFromAddress(address));
	 
	 
	 address =new Address();
	 address.setCountryCode("BE");
	 Assert.assertEquals("wrong street type for ambiguous countrycode",StreetTypeOrder.unknow,formater.detectStreetTypeOrderFromAddress(address));
	 
	 address =new Address();
	 address.setCountryCode("CH");
	 Assert.assertEquals("wrong street type for ambiguous countrycode",StreetTypeOrder.unknow,formater.detectStreetTypeOrderFromAddress(address));
	  
	 
    }
    
    @Test
    public void countryShouldBeCamelCase(){
    	Assert.assertEquals("France", formater.getCountryInfo("Fr").getCountryName());
    }
    
    @Test
    public void getAdmLevelByContryCode(){
    	Assert.assertEquals(0, formater.getAdmLevelByContryCode(null));
    	Assert.assertEquals(0, formater.getAdmLevelByContryCode("toto"));
    	Assert.assertEquals(1, formater.getAdmLevelByContryCode("US"));
    	Assert.assertEquals(0, formater.getAdmLevelByContryCode("FR"));
    	Assert.assertEquals(3, formater.getAdmLevelByContryCode("BS"));
    }

}
