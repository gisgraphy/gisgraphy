<#import "macros/utils.ftl" as utils>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title>${preferedName}</title>
<meta name="Description" content="${preferedName}"/>
<meta name="heading" content="Free Geolocalisation Services"/>
<meta name="keywords" content="${preferedName} GPS information population elevation"/>
<@utils.includeJs jsName="/scripts/prototype.js"/>
<#if shape??>
<script src='https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-omnivore/v0.2.0/leaflet-omnivore.min.js'></script>
</#if>
</head>
<body>
<style>
  .leafletmap { float:right; }
</style>
<br/>
			<div class="clear"><br/></div>
				<div class="bodyResults">
						<div>
						<span class="flag" >
							<img src="/images/flags/${result.countryCode}.png" alt="country flag"/>
						</span>
								<span class="resultheaderleft"><#if housenumber??>${housenumber}, </#if>${preferedName}</span>
						</div>
					
						<div class="separator"><hr/></div>
					<@gisgraphysearch.leafletMap width="500" heigth="500" 
						googleMapAPIKey=googleMapAPIKey CSSClass="leafletmap" zoom=18 />
						<div class="summary">
						<@s.text name="global.latitude"/> : <#if lat??>${lat}<#else>${result.location.y?c}</#if> 
						<br/>
						<@s.text name="global.longitude"/> : <#if lng??>${lng}<#else>${result.location.x?c}</#if><br/>
						<#if result.length??><@s.text name="global.length"/> : ${result.length} m(s)<br/></#if>
						<#if result.lanes??><@s.text name="global.lanes"/> : ${result.lanes}<br/></#if>
						<#if result.toll??><@s.text name="global.toll"/> : ${result.toll}<br/></#if>
						<#if result.surface??><@s.text name="global.surface"/> : ${result.surface}<br/></#if>
						<#if result.maxSpeed??><@s.text name="global.maxspeed"/> : ${result.maxSpeed}<br/></#if>
						<#if result.maxSpeedBackward??><@s.text name="global.maxspeedbackward"/> : ${result.maxSpeedBackward}<br/></#if>
						<#if result.speedMode??><@s.text name="global.speedmode"/> : ${result.speedMode}<br/></#if>
						<#if result.azimuthStart??><@s.text name="global.azimuthstart"/> : ${result.azimuthStart} °<br/></#if>
						<#if result.azimuthEnd??><@s.text name="global.azimuthend"/> : ${result.azimuthEnd} °<br/></#if>
						<#if result.oneWay??>
							<#if result.oneWay>
								<@s.text name="street.oneway"/>
							<#else>
								<img src="/images/twoway.png" class="imgAlign" alt="<@s.text name="global.street.way"/>"/>
								<@s.text name="street.twoway"/>
							</#if>
						<br/><br/>
						<#if result.fullyQualifiedName??><span style="width:400px;display:block;"><@s.text name="global.fullyQualifiedName"/> : ${result.fullyQualifiedName}</span><br/><br/></#if>
						<#if result.labelPostal??><@s.text name="global.labelpostal"/> : ${result.labelPostal}<br/></#if>
						<#if housenumber??><@s.text name="address.houseNumber"/> : ${housenumber}<br/></#if>
<#if result.name??><@s.text name="global.name"/> : ${result.name}<br/></#if>
						<#if result.streetRef??><@s.text name="global.streetref"/> : ${result.streetRef}<br/></#if>
						<#if result.isIn??><@s.text name="global.is.in"/> : ${result.isIn}<br/></#if>
						<#if result.zipCode??><@s.text name="global.zipcode"/> : ${result.zipCode}<br/></#if>
					        <#if result.isInZip??><@s.text name="global.is.inzip"/> : ${result.isInZip}<br/></#if>
					        <#if result.isInPlace??><@s.text name="global.is.inplace"/> : ${result.isInPlace}<br/></#if>
					        <#if result.isInAdm??><@s.text name="global.is.inadm"/> : ${result.isInAdm}<br/></#if>
						<br/>
						<#if result.adm1Name??><@s.text name="global.adm1name"/> : ${result.adm1Name}<br/></#if>
						<#if result.adm2Name??><@s.text name="global.adm2name"/> : ${result.adm2Name}<br/></#if>
						<#if result.adm3Name??><@s.text name="global.adm3name"/> : ${result.adm3Name}<br/></#if>
						<#if result.adm4Name??><@s.text name="global.adm4name"/> : ${result.adm4Name}<br/></#if>
						<#if result.adm5Name??><@s.text name="global.adm5name"/> : ${result.adm5Name}<br/></#if>
						<br/>
						<#if result.source??><@s.text name="source"/> : ${result.source}<br/></#if>
						<#if result.openstreetmapId??><@s.text name="global.openstreetmapId"/> : ${result.openstreetmapId?c}<br/></#if>
						<br/>
						<#if result.streetType??><@s.text name="search.type.of.street"/> : <@s.text name="${result.streetType}"/><br/></#if>
						<br/><br/>
					</#if>
						<#if housenumber??>
							
						<#else>
							<#if result.houseNumbers??>
								<@s.text name="address.houseNumber"/>s : 
							<span style="width:400px;display:block;"><@s.iterator value="result.houseNumbers" status="status" id="house">
		                                               ${house.number}<@s.if test="!#status.last"> | </@s.if>       
		                                        </@s.iterator>
							</span>
								<br/><br/>
							</#if>
							 <#if result.alternateNames?? && result.alternateNames.size()!=  0>
		                               		 <p class="quote">
		                                        <@s.iterator value="result.alternateNames" status="name_wo_lang_status" id="name_alternate">
		                                                ${name_alternate.name} <@s.if test="!#name_wo_lang_status.last"> - </@s.if>         
		                                        </@s.iterator>
		                                        </p>
		                               		 </#if>
						</#if>


						<br/>
						
						<br/><br/>
						<#--<@gisgraphysearch.googleStreetPanorama width="700" heigth="300" 
						googleMapAPIKey=googleMapAPIKey CSSClass="center" />-->
						<script type="text/javascript">
						
						function commadot(that) {
						    if (that.indexOf(",") >= 0) {
						       return that.replace(/\,/g,".");
						    }
						    return that;
						}

						displayMap(commadot('<#if lat??>${lat}<#else>${result.location.y?c}</#if>'),commadot('<#if lng??>${lng}<#else>${result.location.x?c}</#if>'),"<strong><#if housenumber??>${housenumber}, </#if>${preferedName}</strong><br/>Lat :<#if lat??>${lat}<#else>${result.location.y?c}</#if><br/>long:<#if lng??>${lng}<#else>${result.location.x?c}</#if>");
						//viewStreetPanorama(commadot('${result.location.y}'),commadot('${result.location.x}'));
						<#if shape??>
						var shapelayer = omnivore.wkt.parse('${shape}')
 						 .addTo(map);
 							map.fitBounds(shapelayer.getBounds());
						</#if>
						</script>
						</div>
					</div>
					<div class="clear"></div>
					<br/><br/>



			 <div class="clear"><br/></div>
</body>
</html>