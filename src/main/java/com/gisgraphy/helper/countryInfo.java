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
package com.gisgraphy.helper;

import java.io.File;
import java.util.HashMap;

/**
 * 
 * some information on country
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class countryInfo {

	 public static HashMap<String, String> countryLookupMap = new HashMap<String, String>(){
		 {
     put("AD","Andorra");
     put("AE","United Arab Emirates");
     put("AF","Afghanistan");
     put("AG","Antigua and Barbuda");
     put("AI","Anguilla");
     put("AL","Albania");
     put("AM","Armenia");
     put("AN","Netherlands Antilles");
     put("AO","Angola");
     put("AQ","Antarctica");
     put("AR","Argentina");
     put("AS","American Samoa");
     put("AT","Austria");
     put("AU","Australia");
     put("AW","Aruba");
     put("AZ","Azerbaijan");
     put("BA","Bosnia and Herzegovina");
     put("BB","Barbados");
     put("BD","Bangladesh");
     put("BE","Belgium");
     put("BF","Burkina Faso");
     put("BG","Bulgaria");
     put("BH","Bahrain");
     put("BI","Burundi");
     put("BJ","Benin");
     put("BM","Bermuda");
     put("BN","Brunei");
     put("BO","Bolivia");
     put("BR","Brazil");
     put("BS","Bahamas");
     put("BT","Bhutan");
     put("BV","Bouvet Island");
     put("BW","Botswana");
     put("BY","Belarus");
     put("BZ","Belize");
     put("CA","Canada");
     put("CC","Cocos (Keeling) Islands");
     put("CD","Congo, The Democratic Republic of the");
     put("CF","Central African Republic");
     put("CG","Congo");
     put("CH","Switzerland");
     put("CI","Côte d?Ivoire");
     put("CK","Cook Islands");
     put("CL","Chile");
     put("CM","Cameroon");
     put("CN","China");
     put("CO","Colombia");
     put("CR","Costa Rica");
     put("CU","Cuba");
     put("CV","Cape Verde");
     put("CX","Christmas Island");
     put("CY","Cyprus");
     put("CZ","Czech Republic");
     put("DE","Germany");
     put("DJ","Djibouti");
     put("DK","Denmark");
     put("DM","Dominica");
     put("DO","Dominican Republic");
     put("DZ","Algeria");
     put("EC","Ecuador");
     put("EE","Estonia");
     put("EG","Egypt");
     put("EH","Western Sahara");
     put("ER","Eritrea");
     put("ES","Spain");
     put("ET","Ethiopia");
     put("FI","Finland");
     put("FJ","Fiji Islands");
     put("FK","Falkland Islands");
     put("FM","Micronesia, Federated States of");
     put("FO","Faroe Islands");
     put("FR","France");
     put("GA","Gabon");
     put("GB","United Kingdom");
     put("GD","Grenada");
     put("GE","Georgia");
     put("GF","French Guiana");
     put("GH","Ghana");
     put("GI","Gibraltar");
     put("GL","Greenland");
     put("GM","Gambia");
     put("GN","Guinea");
     put("GP","Guadeloupe");
     put("GQ","Equatorial Guinea");
     put("GR","Greece");
     put("GS","South Georgia and the South Sandwich Islands");
     put("GT","Guatemala");
     put("GU","Guam");
     put("GW","Guinea-Bissau");
     put("GY","Guyana");
     put("HK","Hong Kong");
     put("HM","Heard Island and McDonald Islands");
     put("HN","Honduras");
     put("HR","Croatia");
     put("HT","Haiti");
     put("HU","Hungary");
     put("ID","Indonesia");
     put("IE","Ireland");
     put("IL","Israel");
     put("IN","India");
     put("IO","British Indian Ocean Territory");
     put("IQ","Iraq");
     put("IR","Iran");
     put("IS","Iceland");
     put("IT","Italy");
     put("JM","Jamaica");
     put("JO","Jordan");
     put("JP","Japan");
     put("KE","Kenya");
     put("KG","Kyrgyzstan");
     put("KH","Cambodia");
     put("KI","Kiribati");
     put("KM","Comoros");
     put("KN","Saint Kitts and Nevis");
     put("KP","North Korea");
     put("KR","South Korea");
     put("KW","Kuwait");
     put("KY","Cayman Islands");
     put("KZ","Kazakstan");
     put("LA","Laos");
     put("LB","Lebanon");
     put("LC","Saint Lucia");
     put("LI","Liechtenstein");
     put("LK","Sri Lanka");
     put("LR","Liberia");
     put("LS","Lesotho");
     put("LT","Lithuania");
     put("LU","Luxembourg");
     put("LV","Latvia");
     put("LY","Libyan Arab Jamahiriya");
     put("MA","Morocco");
     put("MC","Monaco");
     put("MD","Moldova");
     put("MG","Madagascar");
     put("MH","Marshall Islands");
     put("MK","Macedonia");
     put("ML","Mali");
     put("MM","Myanmar");
     put("MN","Mongolia");
     put("MO","Macao");
     put("MP","Northern Mariana Islands");
     put("MQ","Martinique");
     put("MR","Mauritania");
     put("MS","Montserrat");
     put("MT","Malta");
     put("MU","Mauritius");
     put("MV","Maldives");
     put("MW","Malawi");
     put("MX","Mexico");
     put("MY","Malaysia");
     put("MZ","Mozambique");
     put("NA","Namibia");
     put("NC","New Caledonia");
     put("NE","Niger");
     put("NF","Norfolk Island");
     put("NG","Nigeria");
     put("NI","Nicaragua");
     put("NL","Netherlands");
     put("NO","Norway");
     put("NP","Nepal");
     put("NR","Nauru");
     put("NU","Niue");
     put("NZ","New Zealand");
     put("OM","Oman");
     put("PA","Panama");
     put("PE","Peru");
     put("PF","French Polynesia");
     put("PG","Papua New Guinea");
     put("PH","Philippines");
     put("PK","Pakistan");
     put("PL","Poland");
     put("PM","Saint Pierre and Miquelon");
     put("PN","Pitcairn");
     put("PR","Puerto Rico");
     put("PS","Palestine");
     put("PT","Portugal");
     put("PW","Palau");
     put("PY","Paraguay");
     put("QA","Qatar");
     put("RE","Réunion");
     put("RO","Romania");
     put("RU","Russian Federation");
     put("RW","Rwanda");
     put("SA","Saudi Arabia");
     put("SB","Solomon Islands");
     put("SC","Seychelles");
     put("SD","Sudan");
     put("SE","Sweden");
     put("SG","Singapore");
     put("SH","Saint Helena");
     put("SI","Slovenia");
     put("SJ","Svalbard and Jan Mayen");
     put("SK","Slovakia");
     put("SL","Sierra Leone");
     put("SM","San Marino");
     put("SN","Senegal");
     put("SO","Somalia");
     put("SR","Suriname");
     put("ST","Sao Tome and Principe");
     put("SV","El Salvador");
     put("SY","Syria");
     put("SZ","Swaziland");
     put("TC","Turks and Caicos Islands");
     put("TD","Chad");
     put("TF","French Southern territories");
     put("TG","Togo");
     put("TH","Thailand");
     put("TJ","Tajikistan");
     put("TK","Tokelau");
     put("TM","Turkmenistan");
     put("TN","Tunisia");
     put("TO","Tonga");
     put("TP","East Timor");
     put("TR","Turkey");
     put("TT","Trinidad and Tobago");
     put("TV","Tuvalu");
     put("TW","Taiwan");
     put("TZ","Tanzania");
     put("UA","Ukraine");
     put("UG","Uganda");
     put("UM","United States Minor Outlying Islands");
     put("US","United States");
     put("UY","Uruguay");
     put("UZ","Uzbekistan");
     put("VA","Holy See (Vatican City State)");
     put("VC","Saint Vincent and the Grenadines");
     put("VE","Venezuela");
     put("VG","Virgin Islands, British");
     put("VI","Virgin Islands, U.S.");
     put("VN","Vietnam");
     put("VU","Vanuatu");
     put("WF","Wallis and Futuna");
     put("WS","Samoa");
     put("YE","Yemen");
     put("YT","Mayotte");
     put("YU","Yugoslavia");
     put("ZA","South Africa");
     put("ZM","Zambia");
     put("ZW","Zimbabwe"); 
	 }
	 };

}
