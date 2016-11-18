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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author <a href="mailto:david.masclet@gisgraphy.com">David Masclet</a>
 * 
 */
public class StateAbbreviator {

	/**
	 * The logger
	 */
	public static final Logger logger = LoggerFactory
			.getLogger(StateAbbreviator.class);

	private static Map<String, Map<String, String>> states = new HashMap<String, Map<String, String>>();

	static {
		states.put("US", new HashMap<String, String>());
		states.put("CA", new HashMap<String, String>());
		states.put("AU", new HashMap<String, String>());
		states.put("MX", new HashMap<String, String>());
		states.put("IT", new HashMap<String, String>());
		states.put("CH", new HashMap<String, String>());
		states.put("BR", new HashMap<String, String>());
		states.put("MY", new HashMap<String, String>());
		// US
		String countrycode = "US";
		put(countrycode, "ALABAMA", "AL");
		put(countrycode, "ALASKA", "AK");
		put(countrycode, "AMERICAN SAMOA", "AS");
		put(countrycode, "ARIZONA", "AZ");
		put(countrycode, "ARKANSAS", "AR");
		put(countrycode, "CALIFORNIA", "CA");
		put(countrycode, "COLORADO", "CO");
		put(countrycode, "CONNECTICUT", "CT");
		put(countrycode, "DELAWARE", "DE");
		put(countrycode, "DISTRICT OF COLUMBIA", "DC");
		put(countrycode, "FEDERATED STATES OF MICRONESIA", "FM");
		put(countrycode, "FLORIDA", "FL");
		put(countrycode, "GEORGIA", "GA");
		put(countrycode, "GUAM", "GU");
		put(countrycode, "HAWAII", "HI");
		put(countrycode, "IDAHO", "ID");
		put(countrycode, "ILLINOIS", "IL");
		put(countrycode, "INDIANA", "IN");
		put(countrycode, "IOWA", "IA");
		put(countrycode, "KANSAS", "KS");
		put(countrycode, "KENTUCKY", "KY");
		put(countrycode, "LOUISIANA", "LA");
		put(countrycode, "MAINE", "ME");
		put(countrycode, "MARSHALL IS", "MH");
		put(countrycode, "MARSHALL ISLANDS", "MH");
		put(countrycode, "MARYLAND", "MD");
		put(countrycode, "MASSACHUSETTS", "MA");
		put(countrycode, "MICHIGAN", "MI");
		put(countrycode, "MINNESOTA", "MN");
		put(countrycode, "MISSISSIPPI", "MS");
		put(countrycode, "MISSOURI", "MO");
		put(countrycode, "MONTANA", "MT");
		put(countrycode, "NEBRASKA", "NE");
		put(countrycode, "NEVADA", "NV");
		put(countrycode, "NEW HAMPSHIRE", "NH");
		put(countrycode, "NEW JERSEY", "NJ");
		put(countrycode, "NEW MEXICO", "NM");
		put(countrycode, "NEWJERSEY", "NJ");
		put(countrycode, "NEWMEXICO", "NM");
		put(countrycode, "NEWYORK", "NY");
		put(countrycode, "NEW YORK", "NY");
		put(countrycode, "N CAROLINA", "NC");
		put(countrycode, "NORTH CAROLINA", "NC");
		put(countrycode, "N DAKOTA", "ND");
		put(countrycode, "NORTHERN MARIANA ISLANDS", "MP");
		put(countrycode, "N CAROLINA", "NC");
		put(countrycode, "NORTH DAKOTA", "ND");
		put(countrycode, "NORTHERN MARIANA ISLANDS", "MP");
		put(countrycode, "OHIO", "OH");
		put(countrycode, "OKLAHOMA", "OK");
		put(countrycode, "OREGON", "OR");
		put(countrycode, "PALAU", "PW");
		put(countrycode, "PENNSYLVANIA", "PA");
		put(countrycode, "PUERTO RICO", "PR");
		put(countrycode, "PUERTORICO", "PR");
		put(countrycode, "RHODE ISLAND", "RI");
		put(countrycode, "SOUTH CAROLINA", "SC");
		put(countrycode, "SOUTH DAKOTA", "SD");
		put(countrycode, "S CAROLINA", "SC");
		put(countrycode, "S DAKOTA", "SD");
		put(countrycode, "TENNESSEE", "TN");
		put(countrycode, "TEXAS", "TX");
		put(countrycode, "MINOR OUTLYING ISLANDS", "UM");
		put(countrycode, "UTAH", "UT");
		put(countrycode, "VERMONT", "VT");
		put(countrycode, "VIRGIN IS", "VI");
		put(countrycode, "VIRGIN ISLANDS", "VI");
		put(countrycode, "VIRGINIA", "VA");
		put(countrycode, "WASHINGTON", "WA");
		put(countrycode, "WASHINGTON", "WA");
		put(countrycode, "WEST VIRGINIA", "WV");
		put(countrycode, "W VIRGINIA", "WV");
		put(countrycode, "WISCONSIN", "WI");
		put(countrycode, "WYOMING", "WY");
		// CA
		countrycode = "CA";
		put(countrycode, "ALBERTA", "AB");
		put(countrycode, "BRITISH COLUMBIA", "BC");
		put(countrycode, "COLOMBIE BRITANNIQUE", "BC");
		put(countrycode, "MANITOBA", "MB");
		put(countrycode, "NEW BRUNSWICK", "NB");
		put(countrycode, "NOUVEAU BRUNNSWICK", "NB");
		put(countrycode, "NEWFOUNDLAND AND LABRADOR", "NL");
		put(countrycode, "TERRE NEUVE ET LABRADOR", "NL");
		put(countrycode, "NORTHWEST TERRITORIES", "NT");
		put(countrycode, "TERRITOIRES DU NORD OUEST", "NT");
		put(countrycode, "NOVA SCOTIA", "NS");
		put(countrycode, "NOUVELLE ECOSSE", "NS");
		put(countrycode, "NUNAVUT", "NU");
		put(countrycode, "ONTARIO", "ON");
		put(countrycode, "PRINCE EDWARD ISLAND", "PE");
		put(countrycode, "ÎLE DU PRINCE ÉDOUARD", "PE");
		put(countrycode, "QUÉBEC", "QC");
		put(countrycode, "SASKATCHEWAN", "SK");
		put(countrycode, "YUKON", "YT");
		// AU
		countrycode = "AU";
		put(countrycode, "Australian Capital Territory","ACT");
		put(countrycode,  "New South Wales","NSW");
		put(countrycode,  "Northern Territory","NT");
		put(countrycode,  "Queensland","QLD");
		put(countrycode, "South Australia", "SA");
		put(countrycode,  "Tasmania","TAS");
		put(countrycode,  "Victoria","VIC");
		put(countrycode,  "Western Australia","WA");
		put(countrycode,  "Jervis Bay Territory","JBT");

		// sometimes appears as two letters
		/*put(countrycode, "VI", "Victoria");
		put(countrycode, "QL", "Queensland");
		put(countrycode, "JB", "Jervis Bay Territory");
		put(countrycode, "TA", "Tasmania");
		put(countrycode, "NS", "New South Wales");*/
		// MX
		countrycode = "MX";
		put(countrycode, "Aguascalientes", "AGS");
		put(countrycode, "Baja California Norte", "BCN");
		put(countrycode, "Baja California Sur", "BCS");
		put(countrycode, "Campeche", "CAM");
		put(countrycode, "Chiapas", "CHIS");
		put(countrycode, "Chihuahua", "CHIH");
		put(countrycode, "Coahuila", "COAH");
		put(countrycode, "Colima", "COL");
		put(countrycode, "Distrito Federal", "DF");
		put(countrycode, "Durango", "DGO");
		put(countrycode, "Guanajuato", "GTO");
		put(countrycode, "Guerrero", "GRO");
		put(countrycode, "Hidalgo", "HGO");
		put(countrycode, "Jalisco", "JAL");
		put(countrycode, "México", "MEX");
		put(countrycode, "México", "MX");
		put(countrycode, "Michoacán", "MICH");
		put(countrycode, "Morelos", "MOR");
		put(countrycode, "Nayarit", "NAY");
		put(countrycode, "Nuevo León", "NL");
		put(countrycode, "Oaxaca", "OAX");
		put(countrycode, "Puebla", "PUE");
		put(countrycode, "Querétaro", "QRO");
		put(countrycode, "Quintana Roo", "QROO");
		put(countrycode, "San Luis Potosí", "SLP");
		put(countrycode, "Sinaloa", "SIN");
		put(countrycode, "Sonora", "SON");
		put(countrycode, "Tabasco", "TAB");
		put(countrycode, "Tamaulipas", "TAMPS");
		put(countrycode, "Tlaxcala", "TLAX");
		put(countrycode, "Veracruz", "VER");
		put(countrycode, "Yucatán", "YUC");
		put(countrycode, "Zacatecas", "ZAC");
		put(countrycode, "Baja California", "BCN");
		put(countrycode, "Nuevo León", "NLE");
		put(countrycode, "Nuevo León", "NL");
		put(countrycode, "Guerrero", "GRO");
		put(countrycode, "Baja California", "BC");
		put(countrycode, "Chiapas", "Chis");
		put(countrycode, "Chiapas", "CHP");
		put(countrycode, "Zacatecas", "ZAC");
		put(countrycode, "Querétaro de Arteaga", "Qro");
		put(countrycode, "Tamaulipas", "TAM");
		put(countrycode, "Nayarit", "NAY");
		put(countrycode, "Hidalgo", "Hgo");
		put(countrycode, "Hidalgo", "HID");
		put(countrycode, "Campeche", "CAM");
		put(countrycode, "Chihuahua", "CHH");
		put(countrycode, "Chihuahua", "CH");
		put(countrycode, "Aguascalientes", "AGU");
		put(countrycode, "Aguascalientes", "Ags");
		put(countrycode, "Quintana Roo", "ROO");
		put(countrycode, "México", "MEX");
		put(countrycode, "Durango", "Dgo");
		put(countrycode, "Durango", "DUR");
		put(countrycode, "Tlaxcala", "TLA");
		put(countrycode, "Morelos", "MOR");
		put(countrycode, "Baja California Sur", "BCS");
		put(countrycode, "Jalisco", "JAL");
		put(countrycode, "Guanajuato", "Gto");
		put(countrycode, "Guanajuato", "GUA");
		put(countrycode, "Tlaxcala", "Tlax");
		put(countrycode, "Tamaulipas", "Tamps");
		put(countrycode, "Coahuila de Zaragoza", "Coah");
		put(countrycode, "Coahuila de Zaragoza", "COA");
		put(countrycode, "Yucatán", "YUC");
		put(countrycode, "Michoacán de Ocampo", "MIC");
		put(countrycode, "Michoacán de Ocampo", "Mich");
		put(countrycode, "Sonora", "SON");
		put(countrycode, "Puebla", "PUE");
		put(countrycode, "Colima", "COL");
		put(countrycode, "Querétaro de Arteaga", "QUE");
		put(countrycode, "Sinaloa", "SIN");
		put(countrycode, "Oaxaca", "OAX");
		put(countrycode, "Tabasco", "TAB");
		put(countrycode, "San Luis Potosí", "SLP");
		// italy
		countrycode = "IT";
		put(countrycode, "Potenza", "PZ");
		put(countrycode, "Ravenna", "RA");
		put(countrycode, "Reggio Calabria", "RC");
		put(countrycode, "Reggio Emilia", "RE");
		put(countrycode, "Ragusa", "RG");
		put(countrycode, "Rieti", "RI");
		put(countrycode, "Roma", "RM");
		put(countrycode, "Rimini", "RN");
		put(countrycode, "Rovigo", "RO");
		put(countrycode, "Salerno", "SA");
		put(countrycode, "Siena", "SI");
		put(countrycode, "Sondrio", "SO");
		put(countrycode, "La Spezia", "SP");
		put(countrycode, "Siracusa", "SR");
		put(countrycode, "Sassari", "SS");
		put(countrycode, "Savona", "SV");
		put(countrycode, "Taranto", "TA");
		put(countrycode, "Teramo", "TE");
		put(countrycode, "Trento", "TN");
		put(countrycode, "Torino", "TO");
		put(countrycode, "Trapani", "TP");
		put(countrycode, "Terni", "TR");
		put(countrycode, "Trieste", "TS");
		put(countrycode, "Treviso", "TV");
		put(countrycode, "Udine", "UD");
		put(countrycode, "Varese", "VA");
		put(countrycode, "Vercelli", "VC");
		put(countrycode, "Venezia", "VE");
		put(countrycode, "Vicenza", "VI");
		put(countrycode, "Verbania", "VB");
		put(countrycode, "Verona", "VR");
		put(countrycode, "Viterbo", "VT");
		put(countrycode, "Vibo Valentia", "VV");
		put(countrycode, "Genova", "GE");
		put(countrycode, "Gorizia", "GO");
		put(countrycode, "Grosseto", "GR");
		put(countrycode, "Imperia", "IM");
		put(countrycode, "Isernia", "IS");
		put(countrycode, "Crotone", "KR");
		put(countrycode, "Lecco", "LC");
		put(countrycode, "Lecce", "LE");
		put(countrycode, "Livorno", "LI");
		put(countrycode, "Lodi", "LO");
		put(countrycode, "Latina", "LT");
		put(countrycode, "Lucca", "LU");
		put(countrycode, "Macerata", "MC");
		put(countrycode, "Messina", "ME");
		put(countrycode, "Milano", "MI");
		put(countrycode, "Mantova", "MN");
		put(countrycode, "Modena", "MO");
		put(countrycode, "Massa Carrara", "MS");
		put(countrycode, "Matera", "MT");
		put(countrycode, "Napoli", "NA");
		put(countrycode, "Novara", "NO");
		put(countrycode, "Nuoro", "NU");
		put(countrycode, "Oristano", "OR");
		put(countrycode, "Palermo", "PA");
		put(countrycode, "Piacenza", "PC");
		put(countrycode, "Padova", "PD");
		put(countrycode, "Pescara", "PE");
		put(countrycode, "Perugia", "PG");
		put(countrycode, "Pisa", "PI");
		put(countrycode, "Pordenone", "PN");
		put(countrycode, "Parma", "PR");
		put(countrycode, "Pesaro", "PS");
		put(countrycode, "Pistoia", "PT");
		put(countrycode, "Pavia", "PV");
		put(countrycode, "Prato", "PO");
		put(countrycode, "Agrigento", "AG");
		put(countrycode, "Alessandria", "AL");
		put(countrycode, "Ancona", "AN");
		put(countrycode, "Aosta", "AO");
		put(countrycode, "Ascoli Piceno", "AP");
		put(countrycode, "L Aquila", "AQ");
		put(countrycode, "Aquila", "AQ");
		put(countrycode, "Arezzo", "AR");
		put(countrycode, "Asti", "AT");
		put(countrycode, "Avellino", "AV");
		put(countrycode, "Bari", "BA");
		put(countrycode, "Bergamo", "BG");
		put(countrycode, "Biella", "BI");
		put(countrycode, "Belluno", "BL");
		put(countrycode, "Benevento", "BN");
		put(countrycode, "Bologna", "BO");
		put(countrycode, "Brindisi", "BR");
		put(countrycode, "Brescia", "BS");
		put(countrycode, "Bolzano", "BZ");
		put(countrycode, "Cagliari", "CA");
		put(countrycode, "Campobasso", "CB");
		put(countrycode, "Caserta", "CE");
		put(countrycode, "Chieti", "CH");
		put(countrycode, "Caltanissetta", "CL");
		put(countrycode, "Cuneo", "CN");
		put(countrycode, "Como", "CO");
		put(countrycode, "Cremona", "CR");
		put(countrycode, "Cosenza", "CS");
		put(countrycode, "Catania", "CT");
		put(countrycode, "Catanzaro", "CZ");
		put(countrycode, "Enna", "EN");
		put(countrycode, "Ferrara", "FE");
		put(countrycode, "Foggia", "FG");
		put(countrycode, "Firenze", "FI");
		put(countrycode, "Forli", "FO");
		put(countrycode, "Frosinone", "FR");
		// swiss
		countrycode = "CH";
		put(countrycode, "Neuchâtel", "NE");
		put(countrycode, "Zürich", "ZH");
		put(countrycode, "Luzern", "LU");
		put(countrycode, "Zug", "ZG");
		put(countrycode, "Jura", "JU");
		put(countrycode, "Vaud", "VD");
		put(countrycode, "Graubunden", "GR");
		put(countrycode, "Valais", "VS");
		put(countrycode, "Glarus", "GL");
		put(countrycode, "Uri", "UR");
		put(countrycode, "Geneva", "GE");
		put(countrycode, "Ticino", "TI");
		put(countrycode, "Fribourg", "FR");
		put(countrycode, "Thurgau", "TG");
		put(countrycode, "Bern", "BE");
		put(countrycode, "Solothurn", "SO");
		put(countrycode, "Basel Stadt", "BS");
		put(countrycode, "Schwyz", "SZ");
		put(countrycode, "Basel Landschaft", "BL");
		put(countrycode, "Schaffhausen", "SH");
		put(countrycode, "Appenzell Inner Rhoden", "AI");
		put(countrycode, "Sankt Gallen", "SG");
		put(countrycode, "Appenzell AusserRhoden", "AR");
		put(countrycode, "Obwalden", "OW");
		put(countrycode, "Aargau", "AG");
		put(countrycode, "Nidwalden", "NW");
		// BR
		countrycode = "BR";
		put(countrycode, "Acre", "AC");
		put(countrycode, "Amapá", "AP");
		put(countrycode, "Distrito Federal", "DF");
		put(countrycode, "Maranhão", "MA");
		put(countrycode, "Mato Grosso", "MT");
		put(countrycode, "Pernambuco", "PE");
		put(countrycode, "Rio de Janeiro", "RJ");
		put(countrycode, "Roraima", "RR");
		put(countrycode, "Sergipe", "SE");
		put(countrycode, "Alagoas", "AL");
		put(countrycode, "Bahia", "BA");
		put(countrycode, "Espírito Santo", "ES");
		put(countrycode, "Minas Gerais", "MG");
		put(countrycode, "Pará", "PA");
		put(countrycode, "Piauí", "PI");
		put(countrycode, "Rio Grande do Norte", "RN");
		put(countrycode, "Rio Grande do Sul", "RS");
		put(countrycode, "São Paulo", "SP");
		put(countrycode, "Amazonas", "AM");
		put(countrycode, "Ceará", "CE");
		put(countrycode, "Goiás", "GO");
		put(countrycode, "Mato Grosso do Sul", "MS");
		put(countrycode, "Paraíba", "PB");
		put(countrycode, "Paraná", "PR");
		put(countrycode, "Rondônia", "RO");
		put(countrycode, "Santa Catarina", "SC");
		put(countrycode, "Tocantins", "TO");

		// MY
		countrycode = "MY";
		put(countrycode, "Putrajaya", "PJY");
		put(countrycode, "Negeri Sembilan", "NSN");
		put(countrycode, "Terengganu", "TRG");
		put(countrycode, "Kedah", "KDH");
		put(countrycode, "Kelantan", "KTN");
		put(countrycode, "Melaka", "MLK");
		put(countrycode, "Pahang", "PHG");
		put(countrycode, "Sarawak", "SRW");
		put(countrycode, "Labuan", "LBN");
		put(countrycode, "Selangor", "SGR");
		put(countrycode, "Perak", "PRK");
		put(countrycode, "Pulau Pinang", "PNG");
		put(countrycode, "Johor", "JHR");
		put(countrycode, "Kuala Lumpur", "KUL");
		put(countrycode, "Perlis", "PLS");
		put(countrycode, "Sabah", "SBH");

	}

	private static void put(String countryCode, String name, String abbreviation) {
		if (countryCode != null && name != null && abbreviation != null) {
			Map<String, String> map = states.get(countryCode);
			map.put(StringHelper.normalize(name), abbreviation.toUpperCase());
		}
	}

	public static String getAbbreviation(String countryCode, String name) {
		if (countryCode == null || name == null) {
			return null;
		}
		Map<String, String> map = states.get(countryCode.toUpperCase().trim());
		if (map!=null){
		return map.get(StringHelper.normalize(name.trim()));
		}
		return null;

	}
	
	 public static int getNumberOfCountries(){
	    	return states.size();
	    }
}
