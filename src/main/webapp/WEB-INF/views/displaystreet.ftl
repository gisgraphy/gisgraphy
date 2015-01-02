<#import "macros/utils.ftl" as utils>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title>${preferedName}</title>
<meta name="Description" content="${preferedName}"/>
<meta name="heading" content="Free Geolocalisation Services"/>
<meta name="keywords" content="${preferedName} GPS information population elevation"/>
<@utils.includeJs jsName="/scripts/prototype.js"/>
</head>
<body>
<br/>
			<div class="clear"><br/></div>
				<div class="bodyResults">
						<div>
						<span class="flag" >
							<img src="/images/flags/${result.countryCode}.png" alt="country flag"/>
						</span>
								<span class="resultheaderleft">${preferedName}<#if result.isIn??>, ${result.isIn}</#if></span>
						</div>
					
						<div class="separator"><hr/></div>
					
						<div class="summary">
						<@s.text name="global.latitude"/> : <#if lat??>${lat}<#else>${result.location.y?c}</#if> 
						<br/>
						<@s.text name="global.longitude"/> : <#if lng??>${lng}<#else>${result.location.x?c}</#if>
						<br/>
						<@s.text name="global.length"/> : ${result.length} m(s); 
						<br/>
						<#if result.isIn??><@s.text name="global.is.in"/> : ${result.isIn};<br/></#if>
					        <#if result.isInPlace??><@s.text name="global.is.inplace"/> : ${result.isInPlace};<br/></#if>
					        <#if result.isInAdm??><@s.text name="global.is.inadm"/> : ${result.isInAdm};<br/></#if>
					        <#if result.isInZip??><@s.text name="global.is.inzip"/> : ${result.isInZip};<br/></#if>
						<br/>
						<#if result.openstreetmapId??><@s.text name="global.openstreetmapId"/> : ${result.openstreetmapId?c};<br/></#if>
						<br/>
						<#if result.streetType??><@s.text name="search.type.of.street"/> : <@s.text name="${result.streetType}"/><br/></#if>
						<#if result.one_way?? && result.length??>
							<#if result.one_way>
								<@s.text name="street.oneway"/>
							<#else>
								<img src="/images/twoway.png" class="imgAlign" alt="<@s.text name="global.street.way"/>"/>
								<@s.text name="street.twoway"/>
							</#if>
						<br/><br/>
					</#if>
						<#if result.houseNumbers??>
							<@s.text name="address.houseNumber"/> : 
							<#list result.houseNumbers as house>
							${house.number},
							</#list>;
							<br/><br/>
						</#if>
						 <#if result.alternateNames?? && result.alternateNames.size()!=  0>
                                       		 <p class="quote">
                                                <@s.iterator value="result.alternateNames" status="name_wo_lang_status" id="name_alternate">
                                                        ${name_alternate.name} <@s.if test="!#name_wo_lang_status.last"> - </@s.if>         
                                                </@s.iterator>
                                                </p>
                                       		 </#if>


						
						<@gisgraphysearch.leafletMap width="700" heigth="400" 
						googleMapAPIKey=googleMapAPIKey CSSClass="center" zoom=18 />
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

						displayMap(commadot('<#if lat??>${lat}<#else>${result.location.y?c}</#if>'),commadot('<#if lng??>${lng}<#else>${result.location.x?c}</#if>'),"<strong>${preferedName}</strong><br/>Lat :<#if lat??>${lat}<#else><#if lng??>${lng}<#else>${result.location.x?c}</#if></#if><br/>long:<#if lng??>${lng}<#else>${result.location.x?c}</#if>");
						//viewStreetPanorama(commadot('${result.location.y}'),commadot('${result.location.x}'));
						</script>
						</div>
					</div>
					<div class="clear"></div>
					<br/><br/>



			 <div class="clear"><br/></div>
</body>
</html>