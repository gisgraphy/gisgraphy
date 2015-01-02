<#import "macros/utils.ftl" as utils>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch><html>
<head>
<title>${preferedName}</title>
<meta name="Description" content="${preferedName}"/>
<meta name="heading" content="Free Geolocalisation Services"/>
<meta name="keywords" content="${preferedName} GPS information population elevation"/>

</head>
<body>
<style>
  .leafletmap { float:right; }
</style>
<#if shape??>
<script src='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-omnivore/v0.2.0/leaflet-omnivore.min.js'></script>
</#if>


<br/>
			<!--<h1>${preferedName}</h1>-->
			<div class="clear"><br/></div>
	 			<div class="bodyResults">
					<div>
						<span class="flag"><img src="/images/flags/${result.country_code}.png" alt="<#if result.country_name??>(${result.country_name})</#if>" /></span>
						<span class="resultheaderleft">${preferedName} <#if result.country_name??>(${result.country_name})</#if></span>
					</div>
					
					<div class="separator"><hr/></div>
					<@gisgraphysearch.leafletMap width="500" heigth="500" 
						googleMapAPIKey='' CSSClass="leafletmap" zoom=11 />
					<#if result.google_map_url?? && result.openstreetmap_map_url??><img src="images/world_link.png" alt="Maps links " />&nbsp;<a href="${result.google_map_url}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnGoogleMap"/></a> | <a href="${result.openstreetmap_map_url}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnOpenStreetmapMap"/></a> | </#if>
					  <@s.url id="proximitySearchUrl" action="ajaxgeolocsearch!search" forceAddSchemeHostAndPort="true" includeParams="none" >
			  			<@s.param name="lat" value="${result.lat?c}" />
			  			<@s.param name="lng" value="${result.lng?c}" />
			 		</@s.url>
					  <a href="${proximitySearchUrl}" class="greenlink"><@s.text name="global.findNearestCity"/></a>
					<div class="summary">
					<ul>
					<li><@s.text name="global.latitude"/> : ${result.lat?c}</li>
					<li><@s.text name="global.longitude"/> : ${result.lng?c}</li>
					<li><@s.text name="global.placetype"/> : ${result.placetype}</li>
					<#if result.feature_class?? && result.feature_code??><li><@s.text name="global.typeDescription"/> : <@s.text name="${result.feature_class}_${result.feature_code}"/></li></#if>
					<li><@s.text name="global.name"/> : ${result.name}</li>
					<li><@s.text name="global.FeatureId"/> : ${result.feature_id?c}</li>
					<#if result.openstreetmap_id??><@s.text name="global.openstreetmapId"/> : ${result.openstreetmap_id?c};<br/></#if>
					<#if result.amenity??><@s.text name="global.amenity"/> : ${result.amenity};<br/></#if>
					<#if result.feature_class?? && result.feature_code??><li><@s.text name="global.FeatureClass"/> : ${result.feature_class}</li>
					<li><@s.text name="global.FeatureCode"/> : ${result.feature_code}</li></#if>
					<#if result.fully_qualified_name??><li><@s.text name="global.FullyQualifedName"/> : ${result.fully_qualified_name}</li></#if>
					<#if !result.zipcodes.empty>
						<li><@s.text name="global.zipCode"/> : 
								<@s.iterator value="result.zipcodes" status="zipcodetatus" id="zipcode">
									${zipcode}<@s.if test="!#zipcodetatus.last">, </@s.if>  
								</@s.iterator>
						</li>
					</#if>
					<#if result.name_ascii??><li><@s.text name="global.asciiName"/> : ${result.name_ascii}</li></#if>
					<#if result.population??><li><@s.text name="global.population"/> : ${result.population}</li></#if>
					<#if result.elevation??><li><@s.text name="global.elevation"/> : ${result.elevation} meter</li></#if>
					<#if result.gtopo30??><li><@s.text name="global.gtopo30"/> : ${result.gtopo30}</li></#if>
					<#if result.timezone??><li><@s.text name="global.timeZone"/> : ${result.timezone}</li></#if>
					<#if result.continent??><li><@s.text name="global.continent"/> : ${result.continent}</li></#if>
					<#if result.currency_code??><li><@s.text name="global.currency_code"/> : ${result.currency_code}</li></#if>
					<#if result.currency_name??><li><@s.text name="global.currency_name"/> : ${result.currency_name}</li></#if>
					<#if result.fips_code??><li><@s.text name="global.fips_code"/> : ${result.fips_code}</li></#if>
					<#if result.isoalpha2_country_code??><li><@s.text name="global.isoalpha2_country_code"/> : ${result.isoalpha2_country_code}</li></#if>
					<#if result.isoalpha3_country_code??><li><@s.text name="global.isoalpha3_country_code"/> : ${result.isoalpha3_country_code}</li></#if>
					<#if result.postal_code_mask??><li><@s.text name="global.postal_code_mask"/> : ${result.postal_code_mask}</li></#if>
					<#if result.postal_code_regex??><li><@s.text name="global.postal_code_regex"/> : ${result.postal_code_regex}</li></#if>
					<#if result.phone_prefix??><li><@s.text name="global.phone_prefix"/> : ${result.phone_prefix}</li></#if>
					<#if result.spoken_languages??><li><@s.text name="global.spoken_languages"/> : ${result.spoken_languages}</li></#if>
					<#if result.tld??><li><@s.text name="global.tld"/> : ${result.tld}</li></#if>
					<#if result.capital_name??><li><@s.text name="global.capital_name"/> : ${result.capital_name}</li></#if>
					<#if result.area??><li><@s.text name="global.area"/> : ${result.area} km²</li></#if>
					<#if result.level??><li><@s.text name="global.level"/> : ${result.level}</li></#if>
					<#if result.street_type??><li><@s.text name="${result.street_type}" /></li></#if>
					</ul>
					<br/>

					<@s.text name="global.alternateNames"/> :
					<br/>
					<p class="quote">
					<@s.iterator value="result.name_alternates" status="name_wo_lang_status" id="name_alternate">
							${name_alternate} <@s.if test="!#name_wo_lang_status.last"> - </@s.if> 
					</@s.iterator>
					<@s.iterator value="result.name_alternates_localized.keySet()" status="name_lang_status" id="namelang">
						<@s.iterator value="result.name_alternates_localized[#namelang]" id="name_alternate_localized">
							 <strong>${name_alternate_localized}</strong> (${namelang})<@s.if test="!#name_lang_status.last"> - </@s.if>
						</@s.iterator>
					</@s.iterator>
					</p>

						
					<br/><br/>
					<@s.text name="global.countryInfo"/>
					<div class="separator"><hr/></div>
					<ul>
					<#if result.country_code??><li><@s.text name="global.countryCode"/> : ${result.country_code}</li></#if>
					<#if result.country_name??><li><@s.text name="global.country"/> : ${result.country_name}</li></#if>
					</ul>
					<br/>
					<@s.text name="global.countryAlternateNames"/> :
					<br/><br/>
					<p class="quote">
					<@s.iterator value="result.country_names_alternate" status="country_wo_lang_status" id="countryName">
							${countryName} <@s.if test="!#country_wo_lang_status.last"> - </@s.if>  
					</@s.iterator>
					<@s.iterator value="result.country_names_alternate_localized.keySet()" id="countrylang">
						<@s.iterator value="result.country_names_alternate_localized[#countrylang]" status="country_w_lang_status" id="country_name_alternate_localized">
							 <strong>${country_name_alternate_localized}</strong> (${countrylang}) <@s.if test="!#country_w_lang_status.last"> - </@s.if>
						</@s.iterator>
					</@s.iterator>
					</p>
					<br/><br/>
					<@s.text name="global.adm1"/>
					<div class="separator"><hr/></div>
					
					<#if result.adm1_code??><ul><li><@s.text name="global.adm1Code"/> : ${result.adm1_code}</li></ul></#if>
					<#if result.adm1_name??><ul><li><@s.text name="global.adm1Name"/> : ${result.adm1_name}</li></ul></#if>
					
					<@s.text name="global.alternateNames"/> :
					<br/><br/>
					<p class="quote">
					<@s.iterator value="result.adm1_names_alternate" status="adm1_wo_lang_status" id="adm1Name">
							${adm1Name} <@s.if test="!#adm1_wo_lang_status.last"> - </@s.if> 
					</@s.iterator>
					<@s.iterator value="result.adm1_names_alternate_localized.keySet()" id="adm1lang">
						<@s.iterator value="result.adm1_names_alternate_localized[#adm1lang]" status="adm1_lang_status" id="adm1_name_alternate_localized">
							 <strong>${adm1_name_alternate_localized}</strong> (${adm1lang})<@s.if test="!#adm1_lang_status.last"> - </@s.if>
						</@s.iterator>
					</@s.iterator>
					</p>
					<br/><br/>
					
					<@s.text name="global.adm2"/>
					<div class="separator"><hr/></div>
					<#if result.adm2_code??><ul><li><@s.text name="global.adm2Code"/> : ${result.adm2_code}</li></ul></#if>
					<#if result.adm2_name??><ul><li><@s.text name="global.adm2Name"/> : ${result.adm2_name}</li></ul></#if>
					<@s.text name="global.alternateNames"/> :
					<br/><br/>
					<p class="quote">
					<@s.iterator value="result.adm2_names_alternate" status="adm2langtatus" id="adm2Name">
							${adm2Name} - 
					</@s.iterator>
					<@s.iterator value="result.adm2_names_alternate_localized.keySet()" status="adm2langstatus" id="adm2lang">
						<@s.iterator value="result.adm2_names_alternate_localized[#adm2lang]" id="adm2_name_alternate_localized">
							 <strong>${adm2_name_alternate_localized}</strong> (${adm2lang})<@s.if test="!#adm2langstatus.last"> - </@s.if>
						</@s.iterator>
					</@s.iterator>
					</p>
					<br/><br/>
					
					<@s.text name="global.adm3"/>
					<div class="separator"><hr/></div>
					<#if result.adm3_code??><ul><li><@s.text name="global.adm3Code"/> : ${result.adm3_code}</li></ul></#if>
					<#if result.adm3_name??><ul><li><@s.text name="global.adm3Name"/> : ${result.adm3_name}</li></ul></#if>
					<br/><br/>
					
					<@s.text name="global.adm4"/>
					<div class="separator"><hr/></div>
					
					<#if result.adm4_code??><ul><li><@s.text name="global.adm4Code"/> : ${result.adm4_code}</li></ul></#if>
					<#if result.adm4_name??><ul><li><@s.text name="global.adm4Name"/> : ${result.adm4_name}</li></ul></#if>
					<br/>
					
					
					</div>
					<div class="clear"></div>
					<br/><br/>
					</div>
			 <div class="clear"><br/></div>
<script>
displayMap(${result.lat?c},${result.lng?c},undefined);
<#if shape??>
var shapelayer = omnivore.wkt.parse('${shape}')
  .addTo(map);
 map.fitBounds(shapelayer.getBounds());
</#if>
</script>
</body>
</html>