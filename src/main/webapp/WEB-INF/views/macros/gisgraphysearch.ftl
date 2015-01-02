<#assign number_after_street_countrycode = ["DE","BE","HR","IS","LV","NL","NO","NZ","PL","RU","SI","SK","SW","TR"]>
<#import "utils.ftl" as utils>
<#import "breadcrumbs.ftl" as breadcrumbs>

<#macro fulltextsearchform ajax>
<@s.url  method="search" id="formurl"/>
<@s.form action="${formurl}" method="get" id="fulltextsearch">
		<div id="simplesearch">
			<div id="searchleftblock">
				<@s.textfield name="q" required="true" size="25" theme="simple" id="searchTerms" maxlength="200" cssClass="inputsearch"/>
				<div id="searchbuttonbar">
						<span id="searchexample">e.g. Paris, الرباط ,75000,  ... </span>
					<#if ajax>
					<@s.submit value="%{getText('global.search')}" theme="simple" onclick="return doSearch()" alt="%{getText('global.search')}"/>
					<#else>
					<@s.submit value="%{getText('global.search')}" theme="simple" alt="%{getText('global.search')}"/>
					</#if>
				</div>
			</div>
			<@breadcrumbs.fulltextSearchTooltip advancedSearchURLParam="ajaxfulltextsearch"/>
		</div>
		<br/>
		<div class="clear"></div>
		 <@s.if test="advancedSearch">
			<div id="advancedsearch" >
		</@s.if>
		<@s.else>
    		<div id="advancedsearch" style="display:none;" >
		</@s.else>
	<fieldset>
		<legend>&nbsp; <@s.text name="search.advanced"/> &nbsp; </legend>
		<span class="advancedsearchcat"><@s.text name="search.geolocSpec"/></span>
		<hr/>
		<span class="searchfield">
			<@gisgraphysearch.latlongsearchbox withSearchButton=false/>
		</span>
		<div class="clear"></div>
		<span class="advancedsearchcat"><@s.text name="search.moreCriteria"/></span>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.allwordsrequired"/></span> : <@s.radio name="allwordsrequired" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}"  />
		</span>
		<div class="clear"></div>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.placetype"/> : </span>
			<@s.select headerKey="" headerValue="--Any place--"  name="placetype" list="placetypes"  multiple="true" size="5" required="false"  labelposition="left" theme="simple"/>
			<br/><br/>
			<@s.text name="global.useshifttoselectmore"/>
			<br/>
		</span>
		<div class="clear"></div>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.country"/> : </span><@s.select label="In " listKey="iso3166Alpha2Code" listValue="name" name="country" list="countries" headerValue="--All countries--" headerKey="" multiple="false" required="false" labelposition="left" theme="simple" /> 
			<br/>
		</span>
		 <div class="clear"></div>
		<span class="advancedsearchcat"><@s.text name="search.outputSpecs"/></span>
		<hr/>
		<span class="searchfield">
			<@s.url id="fulltextSearchServiceUrl" action="fulltextsearch" includeParams="all" namespace="" >
			 <@s.param name="advancedSearch" value="true" />
			</@s.url>
			<@s.text name="search.MoreOutputSpecsFulltext"><@s.param>${fulltextSearchServiceUrl}</@s.param><@s.param><@s.text name="search.fulltext.title"/></@s.param></@s.text>
		<@s.hidden size="5" maxlength="3" name="to" required="false"  theme="simple"/>
		<@s.hidden size="5" maxlength="3" name="from" required="false"  theme="simple"/>
		</span>
	</fieldset>
	</div>
	</@s.form>

</div>
<div id="nonAjaxDisplayResults">
			<#if errorMessage!= ''>
			<div class="clear"><br/><br/></div>
				<div class="tip redtip">
					<div class="importantMessage"><@s.text name="global.error"/> : ${errorMessage}</div>
				</div>
			<#elseif displayResults>
			<div class="clear"><br/><br/></div>
		 		<@gisgraphysearch.displayFulltextResults fulltextResponseDTO=responseDTO editable=admin/>
		 	</#if>
		 </div>
<div id="popupResults"></div>
<@utils.includeJs jsName="/scripts/prototype.js"/>
<@utils.includeJs jsName="/scripts/gisgraphyapi.js"/>


<script type="text/javascript" >
		pointIsRequired=false;
		DEFAULT_NUMBER_OF_RESULTS_PER_PAGE=${defaultNumberOfResultsPerPage?c};

 	 	displayPopupResults = function(transport){
	 	 	 if (transport.responseText){
	 	 	 $('nonAjaxDisplayResults').update("");
		     	$('popupResults').update(transport.responseText);
		     	$('popupResults').show();
		     	 Event.observe('closePopupResultsPopupButton','click',closePopupResults);
		     	 Event.observe(document,'keydown',function(e){
		     	 	var code;
					if (!e) var e = window.event;
					if (e.keyCode) code = e.keyCode;
					else if (e.which) code = e.which;
					if (code=27) {
						closePopupResults();
					}
		     	 }
		     	 );
		     	 return false;
		   	 } else {
		      alert("No response from the server");
		      return true;
		     }
        }
        
        closePopupResults = function(){
        	$('popupResults').hide();
        	$('popupResults').update("");
        	$('fulltextsearch')['from'].value=1;
        	$('fulltextsearch')['to'].value=DEFAULT_NUMBER_OF_RESULTS_PER_PAGE
        	
        }
        
    updatePaginationNext= function(){
    $('fulltextsearch')['from'].value=parseInt($('fulltextsearch')['from'].value)+DEFAULT_NUMBER_OF_RESULTS_PER_PAGE;
    $('fulltextsearch')['to'].value=parseInt($('fulltextsearch')['to'].value)+DEFAULT_NUMBER_OF_RESULTS_PER_PAGE;
    return updatePopupResults();
    }

    doSearch= function(){
        $('fulltextsearch')['from'].value=1;
        $('fulltextsearch')['to'].value=10;
        return updatePopupResults();
    }
    
    executeSpellSearch= function(words){
    $('fulltextsearch')['from'].value=1;
    $('fulltextsearch')['to'].value=DEFAULT_NUMBER_OF_RESULTS_PER_PAGE;
    $('fulltextsearch')['q'].value=words;
    return updatePopupResults();
    }
    
     updatePaginationPrevious = function(){
    $('fulltextsearch')['from'].value=parseInt($('fulltextsearch')['from'].value)-DEFAULT_NUMBER_OF_RESULTS_PER_PAGE;
    $('fulltextsearch')['to'].value=parseInt($('fulltextsearch')['to'].value)-DEFAULT_NUMBER_OF_RESULTS_PER_PAGE;
    return updatePopupResults();
    }
 	
 	
    updatePopupResults = function(){
    try {
     if (!checkParameters('fulltextsearch'))
     {
 	    return false;
     }
    var savedAction = $('fulltextsearch').action;
	<@s.url  method="searchpopup" id="ajaxformurl"/>
    $('fulltextsearch').action='${ajaxformurl}';
    $('fulltextsearch').request(
    { onComplete: displayPopupResults ,
    onFailure : function(transport){
	  	alert("an error has occured");
	  } }
    );
    //restore overiden parameters
    $('fulltextsearch').action=savedAction;
    return false;
    }catch(e){
    alert("an error occured : " +e);
    return true;
    }
	}

doAjaxSearch = function(formName){
	var query = new GisgraphyQuery(formName);
	query.setParameter('format','JSON');
	query.setURL('/fulltext/fulltextsearch');
	query.execute();
	return false;
}
</script>
</#macro>
<#macro displayFulltextResults fulltextResponseDTO editable=false>
			<div id="searchResults">
			<div class="clear"><br/></div>
			<div class="bigText indented">${fulltextResponseDTO.numFound} <@s.text name="search.resultFound"/>. (<@s.text name="search.resultPaginateFromTo"><@s.param>${from}</@s.param><@s.param>${to}</@s.param></@s.text>).
			 <@s.text name="search.requestTime"/> ${fulltextResponseDTO.QTime/1000} <@s.text name="search.secondUnit"/>. <br/>
			 <#--<@s.text name="search.MaxScore"><@s.param>${fulltextResponseDTO.maxScore}</@s.param></@s.text>-->
			</div>
			<#if fulltextResponseDTO.results.size()!=0>
			<br/>
			<@s.url id="showAllOnMapsURL" value="fulltext/fulltextsearch" includeParams="all" forceAddSchemeHostAndPort="true" escapeAmp="false" />			
			&nbsp;&nbsp;<a href="http://maps.google.fr/maps?q=${showAllOnMapsURL?url('UTF-8')}%26format%3DATOM" target="_blank"><img src="/images/showonmaps.png" width="32px" alt="map" class="imgAlign"/> <@s.text name="search.viewResultsOnMap"/></a>
				<#list fulltextResponseDTO.results as result>
	 			<div class="bodyResults">
					<div class="flag" >
						<img src="/images/flags/${result.country_code}.png" alt="country flag"/>
					</div>
					<div class="resultblock">
					<div>
							<div class="resultheaderleft">
							<#if result.length??>							
								<@s.url id="featureURL" action="displaystreet" includeParams="none" namespace="/public" >
					  					<@s.param name="gid" value="${result.feature_id?c}" />
					 			</@s.url>
							<#else>
								<@s.url id="featureURL" action="displayfeature" includeParams="none" >
				  					<@s.param name="featureId" value="${result.feature_id?c}" />
				 				</@s.url>
							</#if>
							<a href="${featureURL}"><#if result.name??>${result.name}<#else><@s.text name="global.street.noname" /></#if> <#if result.country_name??>(${result.country_name})</#if></a>
							</div>
							<div class="resultheaderright"><#if result.feature_class?? && result.feature_code??><@s.text name="${result.feature_class}_${result.feature_code}"/></#if></div>
					</div>
					
					<div class="separator"><hr/></div>
					
					<div class="summary">
					<#if result.feature_class?? && result.feature_code??><@s.text name="global.typeDescription"/> : <@s.text name="${result.feature_class}_${result.feature_code}"/><br/>
					<@s.text name="global.featureClassCode"/> : ${result.feature_class}.${result.feature_code}<br/></#if>
					<#if result.fully_qualified_name??>${result.fully_qualified_name}<br/></#if>
					<@s.text name="global.latitude"/> : ${result.lat?c};<br/> <@s.text name="global.longitude"/> : ${result.lng?c}<br/>
					<#if result.is_in??><@s.text name="global.is.in"/> : ${result.is_in};<br/></#if>
					<#if result.openstreetmap_id??><@s.text name="global.openstreetmapId"/> : ${result.openstreetmap_id?c};<br/></#if>
					<#if result.population??><@s.text name="global.population"/> : ${result.population};<br/></#if>
					<#if result.elevation??><@s.text name="global.elevation"/> : ${result.elevation} m<br/></#if>
					<#if result.one_way?? && result.length??>
							<#if result.one_way>
								<@s.text name="street.oneway"/>
							<#else>
								<img src="/images/twoway.png" class="imgAlign" alt="<@s.text name="global.street.way"/>"/>
								<@s.text name="street.twoway"/>
							</#if>
						<br/><br/>
					</#if>
					<#if result.length??><@s.text name="global.length"/> : ${result.length} m(s); </#if>
					<br/>
					<#if result.street_type??><@s.text name="${result.street_type}" /><br/></#if>
					<#if result.google_map_url?? && result.openstreetmap_map_url??><img src="images/world_link.png" alt="Maps links" />&nbsp;<a href="${result.google_map_url}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnGoogleMap"/></a> | <a href="${result.openstreetmap_map_url}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnOpenStreetmapMap"/></a></#if>
						<#if result.placetype?? && result.placetype.equals('Street')>
						<@s.url id="streetURL" action="displaystreet" includeParams="none" namespace="/public" >
				  					<@s.param name="gid" value="${result.feature_id?c}" />
				 		</@s.url>
						<!--<img src="/images/world_link.png" alt="Maps links" />&nbsp;<a href="${streetURL}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewStreet"/></a>-->
					 <@s.url id="proximitySearchUrl" action="ajaxgeolocsearch" forceAddSchemeHostAndPort="true" method="search" includeParams="none" >
			  			<@s.param name="lat" value="${result.lat?c}" />
			  			<@s.param name="lng" value="${result.lng?c}" />
			 		</@s.url>
					 | <a href="${proximitySearchUrl}" class="greenlink"><@s.text name="global.findNearestCity"/></a>
					</#if>
					<#if result.placetype?? && result.placetype.equals('City')>
					<@s.url id="streetsearchurl" action="streetSearch" includeParams="none" namespace="/public" >
						<@s.param name="lat" value="${result.lat?c}" />
			  			<@s.param name="lng" value="${result.lng?c}" />
						<@s.param name="autosubmit" value="true" />
					</@s.url>
					 > <a href="${streetsearchurl}" class="greenlink"><@s.text name="search.view.street.breadcrumbs"/></a>
					</#if>
					<#if result.length?? >
						<#if editable>	
						<@s.form action="editStreet!input.html" method="get" >
							<@s.submit value="%{getText('button.edit')}" theme="simple" alt="%{getText('button.edit')}" cssClass="yellowbutton"/>
							<@s.hidden name="gid" value="${result.feature_id?c}" theme="simple"/>
						</@s.form>
						</#if>
					<#else>
						<#if editable>	
						<@s.form action="editFeature!input.html" method="get" >
							<@s.submit value="%{getText('button.edit')}" theme="simple" alt="%{getText('button.edit')}" cssClass="yellowbutton"/>
							<@s.hidden name="featureid" value="${result.feature_id?c}" theme="simple"/>
						</@s.form>
						</#if>
					</#if>
					</div>
					</div>
					<div class="clear"></div>
					<br/><br/>
					</div>
				</#list> 
				<#if (from > 1)>
				<span style="float:left;padding-left:15px;"><@s.url id="previousURL" action="ajaxfulltextsearch" method="search" includeParams="all" >
			  			<@s.param name="from" value="${from?c}-${defaultNumberOfResultsPerPage?c}" />
			  			<@s.param name="to" value="${to?c}-${defaultNumberOfResultsPerPage?c}" />
			 		</@s.url><a href="${previousURL}" class="bigText strong" onclick="$('searchTerms').focus();return updatePaginationPrevious();" alt="previous" >&lt;&lt;<@s.text name="global.previous"/></a></span>
			 	</#if>
			 	<#if ((from + fulltextResponseDTO.resultsSize) < fulltextResponseDTO.numFound)>
				<span style="float:right;padding-right:15px;"><@s.url id="nextURL" action="ajaxfulltextsearch!search" includeParams="all" >
			  			<@s.param name="from" value="${from?c}+${defaultNumberOfResultsPerPage?c}" />
			  			<@s.param name="to" value="${to?c}+${defaultNumberOfResultsPerPage?c}" />
			 		</@s.url><a href="${nextURL}" class="bigText strong" onclick="$('searchTerms').focus();return updatePaginationNext();" alt="next"><@s.text name="global.next"/>&gt;&gt;</a></span>
			 	</#if>
			 		<div class="clear"><br/></div>
			</div>
				<#else>
			<br/>
			 <div>
			 <#if fulltextResponseDTO.collatedResult??>
			 <@s.url id="spellURL" action="ajaxfulltextsearch!search" includeParams="all" >
			  			<@s.param name="q" value="" />
			  			<@s.param name="from" value="1" />
			  			<@s.param name="to" value="${defaultNumberOfResultsPerPage?c}" />
			 </@s.url>
			 <br/>
			 <span class="spell"><@s.text name="search.spellChecking.proposalSentence"/></span> : <a href="${spellURL}&q=${fulltextResponseDTO.spellCheckProposal}" onclick="return executeSpellSearch('${fulltextResponseDTO.spellCheckProposal}');" alt="search.spellChecking.proposalSentence" class="spellLink">${fulltextResponseDTO.spellCheckProposal}</a> 
			<#if !(fulltextResponseDTO.collatedResult.equals(fulltextResponseDTO.spellCheckProposal.trim()))>,
 <a href="${spellURL}&q=${fulltextResponseDTO.collatedResult}" onclick="return executeSpellSearch('${fulltextResponseDTO.collatedResult}');" alt="search.spellChecking.proposalSentence" class="spellLink">${fulltextResponseDTO.collatedResult}</a> 
			</#if>
 			<br/>
			 <br/>
			 </div>
			 </#if>
			 <ul><li><@s.text name="search.noresultMessage.part1"/> <@s.text name="search.noresultMessage.part2"/></li></ul>
<br/>
<div class="bigText indented"> <@s.text name="search.noresultMessage.part3"/>
			 
			 </div>
		</#if>
</#macro>

<#macro displayGeolocResults geolocResponseDTO>
			<div id="searchResults">
				<div class="clear"><br/></div>
				<div class="bigText indented">${geolocResponseDTO.numFound} <@s.text name="search.resultFound"/>. (<@s.text name="search.resultPaginateFromTo"><@s.param>${from}</@s.param><@s.param>${to}</@s.param></@s.text>).
				 <@s.text name="search.requestTime"/> ${geolocResponseDTO.QTime/1000}  <@s.text name="search.secondUnit"/>. </div>
				<#if geolocResponseDTO.result.size()!=0>
				<br/>
				<@s.url id="showAllOnMapsURL" value="geoloc/geolocsearch" includeParams="all" forceAddSchemeHostAndPort="true" escapeAmp="false" />			
			&nbsp;&nbsp;<a href="http://maps.google.fr/maps?q=${showAllOnMapsURL?url('UTF-8')}%26format%3DATOM" target="_blank"><img src="/images/map_go.png" alt="map"/> <@s.text name="search.viewResultsOnMap"/></a>
					<#list geolocResponseDTO.result as result>
	 				<div class="bodyResults">
						<div class="flag" >
							<img src="${result.country_flag_url}" alt="country flag"/>
						</div>
						<div class="resultblock">
							<@s.url id="featureURL" action="displayfeature" includeParams="none" >
				  					<@s.param name="featureId" value="${result.featureId?c}" />
				 				</@s.url>
								<div class="resultheaderleft"><a href="${featureURL}">${result.name} (${result.countryCode})</a> <#if result.distance??>: ${result.distance} <@s.text name="search.unit.meter"/></#if></div>
								<div class="resultheaderright"><#if result.feature_class?? && result.feature_code??><@s.text name="${result.featureClass}_${result.featureCode}"/></#if></div>
						</div>
					
						<div class="separator"><hr/></div>
					
						<div class="summary">
						<#if result.feature_class?? && result.feature_code??><@s.text name="global.typeDescription"/> : <@s.text name="${result.featureClass}_${result.featureCode}"/><br/>
						<@s.text name="global.featureClassCode"/> : ${result.featureClass}.${result.featureCode}<br/>
						</#if>
						<@s.text name="global.latitude"/> : ${result.lat?c}; 
						<br/>
						<@s.text name="global.longitude"/> : ${result.lng?c}<br/>
						<#if result.population??><@s.text name="global.population"/> : ${result.population};<br/></#if>
						<#if result.elevation??><@s.text name="global.elevation"/> : ${result.elevation} m<br/></#if>
						<img src="/images/world_link.png" alt="Maps links" />&nbsp;<a href="${result.google_map_url}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnGoogleMap"/></a> | <a href="${result.openstreetmap_map_url}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnOpenStreetmapMap"/></a>

						<#if result.placetype?? && result.placeType.equals('City')>
					<@s.url id="streetsearchurl" action="streetSearch" includeParams="none" namespace="/public" >
						<@s.param name="lat" value="${result.lat?c}" />
			  			<@s.param name="lng" value="${result.lng?c}" />
						<@s.param name="autosubmit" value="true" />
					</@s.url>
					 | <a href="${streetsearchurl}" class="greenlink"><@s.text name="search.view.street.breadcrumbs"/></a>
					</#if>
						</div>
					</div>
					<div class="clear"></div>
					<br/><br/>
				</#list>
				<#if (from > 1)>
				<span style="float:left;padding-left:15px;"><@s.url id="previousURL" action="ajaxgeolocsearch" method="search" includeParams="all" >
			  			<@s.param name="from" value="${from?c}-${defaultNumberOfResultsPerPage?c}" />
			  			<@s.param name="to" value="${to?c}-${defaultNumberOfResultsPerPage?c}" />
			 		</@s.url><a href="${previousURL}" class="bigText strong" onclick="$('lat').focus();return updatePaginationPrevious();" alt="previous">&lt;&lt;<@s.text name="global.previous"/></a></span>
			 	</#if>
			 	<#if defaultNumberOfResultsPerPage==geolocResponseDTO.numFound>
				<span style="float:right;padding-right:15px;"><@s.url id="nextURL" action="ajaxgeolocsearch" method="search" includeParams="all" >
			  			<@s.param name="from" value="${from?c}+${defaultNumberOfResultsPerPage?c}" />
			  			<@s.param name="to" value="${to?c}+${defaultNumberOfResultsPerPage?c}" />
			 		</@s.url><a href="${nextURL}" class="bigText strong" onclick="$('lat').focus();return updatePaginationNext();" alt="next"><@s.text name="global.next"/>&gt;&gt;</a></span>
			 	</#if>
			<#else>
			
			<br/><br/><br/>
			  <div class="importantMessage indented"><@s.text name="search.noResult"/>!!<br/><br/><br/><br/></div>
			 
		</#if>
		</div>
</#macro>

<#macro displayAddressForm url structured=false>
<script type="text/javascript" >
toggleAddressForm = function(structured){
	if (structured){
		$("postalcheckbox").hide();
		$("unstructuredaddressfields").hide();
		$("address").disable();
		$("structuredaddressfields").show();
		$("unstructuredbutton").setStyle({border:' 0px solid #000000'});
		$("structuredbutton").setStyle({border: '2px solid #000000'});
	} else {
		$("postalcheckbox").show();
		$("unstructuredaddressfields").show();
		$("structuredaddressfields").hide();
		$("address").enable();
		$("structuredbutton").setStyle({border: '0px solid #000000'});
		$("unstructuredbutton").setStyle({border: '2px solid #000000'});
	}
};
</script>
	<@s.form action="${url}" method="get" id="addressform" cssStyle="background-color:#ebf5fc;padding-top:25px;">
<#if url.contains('geocod')>
<div style="600px;padding-bottom:20px;margin:auto;" class="center">
<span style="padding:10px 50px;background-color:#888888;margin:10px;color:#FFFFFF;text-decoration:underline;border-radius:10px;border: 2px solid #000000;" id="unstructuredbutton" onclick="toggleAddressForm(false);"><@s.text name="search.address.unstructured.label" /></span><span style="padding:10px 50px;background-color:#888888;color:#FFFFFF;text-decoration:underline;border-radius:10px" id="structuredbutton" onclick="toggleAddressForm(true);"><@s.text name="search.address.structured.label" /></span>
</div>
</#if>
		<div id="simplesearch" style="width:600px;">
		<div id="unstructuredaddressfields" <#if structured>style="display:none;"</#if> >
		<#if !url.contains('geocod')>
				<@s.text name="user.address.address" /> : (<span id="searchexample">e.g. 650 Castro Street Mountain View, CA, 94041-2021 USA</span>)
			<#else>
				<@s.text name="search.geocoder.field" /> :
		</#if>
		<@s.textfield name="address" required="true" size="56" theme="simple" id="address" maxlength="200" cssStyle="margin:0px;" cssClass="inputsearch"/><br/>
</div>
<div id="structuredaddressfields" <#if !structured>style="display:none;"</#if>>
<div style="font-weight:bold;"><@s.text name="search.address.structured.desc" /> :</div>
	<div style="margin-left:30px;">
	<span class="searchlabel" ><@s.text name="search.address.structured.housenumber" /> : </span><@s.textfield name="houseNumber" required="false" size="40" theme="simple" id="housenumber" maxlength="10" cssStyle="margin:0px;" cssClass="inputsearchsimple"/><br/>
	<span class="searchlabel" ><@s.text name="search.address.structured.street" />  (<@s.text name="global.required" />) : </span><@s.textfield name="streetName" required="true" size="40" theme="simple" id="streetname" maxlength="255" cssStyle="margin:0px;" cssClass="inputsearchsimple"/><br/>
	<span class="searchlabel" ><@s.text name="search.address.structured.city" /> (<@s.text name="global.required" />) : </span><@s.textfield name="city" required="true" size="40" theme="simple" id="city" maxlength="255" cssStyle="margin:0px;" cssClass="inputsearchsimple"/><br/>
	<span class="searchlabel" ><@s.text name="search.address.structured.state" /> : </span><@s.textfield name="state" required="false" size="40" theme="simple" id="state" maxlength="255" cssStyle="margin:0px;" cssClass="inputsearchsimple"/><br/>
	<span class="searchlabel" ><@s.text name="search.address.structured.zipcode" /> : </span><@s.textfield name="zipCode" required="false" size="40" theme="simple" id="state" maxlength="255" cssStyle="margin:0px;" cssClass="inputsearchsimple"/><br/>
	</div>
</div>
		<br/>
		<span id="postalcheckbox"><#if url.contains('geocod') && !structured>
			<div style="padding-left:120px;"><@s.checkbox name="postal" fieldValue="true" label="search.geocoding.postal.mode" theme="simple"/><@s.text name="search.geocoding.postal.mode"/></div></span>
		</#if>
		<br/>
		<b><@s.text name="search.select.country"/> <#if url.contains('geocod')>(<@s.text name="search.optional"/>)</#if> :</b> </span>
<#if !url.contains('geocod')>
<select name="country" id="addressform_country" style="width:180px;">
    <option value=""
    >Sélectionnez</option>
		<option value="AN">Netherlands Antilles (AN)</option>
		<option value="AO">Angola (AO)</option>
		<option value="AR">Argentina (AR)</option>
		<option value="AS">American Samoa (AS)</option>
		<option value="AT">Austria (AT)</option>
		 <option value="AU">Australia (AU)</option>
		 <option value="AW">Aruba (AW)</option>
		 <option value="BE">Belgium (BE)</option>
		 <option value="BQ">BQ (BQ)</option>
		 <option value="BR">Brazil (BR)</option>
		 <option value="CA">Canada (CA)</option>
		 <option value="CD">Congo (CD)</option>
		 <option value="CH">Switzerland (CH)</option>
		 <option value="CM">Cameroon (CM)</option>
		 <option value="CN">China (CN)</option>
		 <option value="CW">CW (CW)</option>
		 <option value="DE">Germany (DE)</option>
		 <option value="DK">Denmark (DK)</option>
		 <option value="DZ">Algeria (DZ)</option>
		 <option value="ES">Spain (ES)</option>
		 <option value="FI">Finland (FI)</option>
		 <option value="FK">Falkland Islands (FK)</option>
		 <option value="FO">Faroe Islands (FO)</option>
		 <option value="FR">France (FR)</option>
		 <option value="GB">United Kingdom (GB)</option>
	 	 <option value="GF">French Guiana (GF)</option>
		 <option value="GG">Guernsey (GG)</option>
		 <option value="GI">Gibraltar (GI)</option>
		 <option value="GL">Greenland (GL)</option>
		 <option value="GP">Guadeloupe (GP)</option>
		 <option value="GS">S Georgia and the S Sandwich Islands (GS)</option>
		 <option value="HK">Hong Kong (HK)</option>
		 <option value="HU">Hungary (HU)</option>
		 <option value="ID">Indonesia (ID)</option>
		 <option value="IM">Isle of Man (IM)</option>
		 <option value="IN">India (IN)</option>
		 <option value="IR">Iran (IR)</option>
		 <option value="IT">Italy (IT)</option>
		 <option value="JE">Jersey (JE)</option>
		 <option value="KZ">Kazakhstan (KZ)</option>
		 <option value="MA">Morocco (MA)</option>
		 <option value="MF">Saint Martin (MF)</option>
		 <option value="MP">Northern Mariana Islands (MP)</option>
		 <option value="MQ">Martinique (MQ)</option>
		 <option value="NL">Netherlands the (NL)</option>
	 	 <option value="NO">Norway (NO)</option>
		 <option value="PL">Poland (PL)</option>
		 <option value="PM">Saint Pierre and Miquelon (PM)</option>
		 <option value="PR">Puerto Rico (PR)</option>
		 <option value="PT">Portugal (PT)</option>
		 <option value="RE">Reunion (RE)</option>
		 <option value="RU">Russia (RU)</option>
		 <option value="SA">Saudi Arabia (SA)</option>
		 <option value="SD">Sudan (SD)</option>
		 <option value="SE">Sweden (SE)</option>
		 <option value="SG">Singapore (SG)</option>
		 <option value="SH">Saint Helena (SH)</option>
		 <option value="SM">San Marino (SM)</option>
		 <option value="SN">Senegal (SN)</option>
		 <option value="SX">SX (SX)</option>
		 <option value="TC">Turks and Caicos Islands (TC)</option>
		 <option value="TN">Tunisia (TN)</option>
		 <option value="TR">Turkey (TR)</option>
		 <option value="UA">Ukraine (UA)</option>
		 <option value="UM">US Minor Outlying Islands (UM)</option>
		 <option value="US">USA (US)</option>
		 <option value="VA">Vatican (VA)</option>
		 <option value="VI">US Virgin Islands (VI)</option>
	</select>
<#else>
<@s.select label="In " listKey="iso3166Alpha2Code" listValue="name" name="country" list="countries" headerValue="%{getText('global.select')}" headerKey="" multiple="false" required="false" labelposition="left" theme="simple" cssStyle="width:180px;"/> 
</#if> 
		<#if url.contains('geocod')>
			<@s.submit value="%{getText('search.geocode')} !" theme="simple" onclick=" return updatePopupResults()" cssStyle="margin-left:70px;" />
			<#else>
				<@s.submit value="%{getText('search.parse')} !" theme="simple" onclick=" return updatePopupResults()" cssStyle="margin-left:70px;" />
				</#if>

	</div>
	<#if !url.contains('geocod')>
					<@s.text name="addressparser.view.implemented.country" >
						<@s.param>http://www.gisgraphy.com/documentation/addressparser.htm#implemetedcountries</@s.param>					
					</@s.text>
				
				</#if>
	<div class="clear"><br/></div>
	<div id="nonAjaxDisplayResults">
			<#if errorMessage!= ''>
			<div class="clear"><br/><br/></div>
				<div class="tip redtip">
					<div class="importantMessage">Error : ${errorMessage}</div>
				</div>
			<#elseif displayResults>
			<div class="clear"><br/><br/></div>
		 		<@gisgraphysearch.displayAddressResults addressResponseDTO=addressResultsDto/>
		 	</#if>
		 </div>
	
		
		 <div class="clear"><br/></div>
	 <@s.if test="advancedSearch">
			<div id="advancedsearch" >
		</@s.if>
		<@s.else>
    		<div id="advancedsearch" style="display:none;" >
		</@s.else>
	</div>
	</@s.form>
</div>
<div id="popupResults"></div>

<@utils.includeJs jsName="/scripts/prototype.js"/>
<@utils.includeJs jsName="/scripts/gisgraphyapi.js"/>

<script type="text/javascript" >
	pointIsRequired=false;
	DEFAULT_NUMBER_OF_RESULTS_PER_PAGE=${defaultNumberOfResultsPerPage?c};


 	 	displayPopupResults = function(transport){
	 	 	 if (transport.responseText){
	 	 	 	$('nonAjaxDisplayResults').update("");
		     	$('popupResults').update(transport.responseText);
		     	$('popupResults').show();
		     	 Event.observe('closePopupResultsPopupButton','click',closePopupResults);
		     	 Event.observe(document,'keydown',function(e){
		     	 	var code;
					if (!e) var e = window.event;
					if (e.keyCode) code = e.keyCode;
					else if (e.which) code = e.which;
					if (code=27) {
						closePopupResults();
					}
		     	 }
		     	 );
		     	 return false;
		   	 } else {
		      alert("No response from the server");
		      return true;
		     }
        }
        
        closePopupResults = function(){
        	$('popupResults').hide();
        	$('popupResults').update("");
        }
        
 	doSearch= function(){
             $('fulltextsearch')['from'].value=1;
             $('fulltextsearch')['to'].value=10;
             return updatePopupResults();
        }  
 	
    updatePopupResults = function(){
    try {
     if (!checkParameters('addressform'))
     {
 	    return false;
     }
     <#if !url.contains('geocod')>
     if ($('addressform')['country'].value == ''){
    	 alert('<@s.text name="search.country.required"/>');
    	 return false;
     }
     </#if>
	<@s.url id="addresspopupurl" includeParams="none" method="searchpopup" namespace="/public" />
    var savedAction = $('addressform').action;
    $('addressform').action='${addresspopupurl}';
    $('addressform').request(
    { onComplete: displayPopupResults ,onFailure : function(transport){
	  	alert("An error has occured");
	  } }
    );
    //restore overiden parameters
    $('addressform').action=savedAction;
    return false;
    }catch(e){
    alert("an error occured : " +e);
    return true;
    }
	}
	<#if autosubmit>
		doSearch();
	</#if>

</script>

</#macro>

<#macro displayAddressResults addressResponseDTO>
<div id="searchResults">
				<div class="clear"><br/></div>
				<div class="bigText indented">${addressResponseDTO.numFound} <@s.text name="search.resultFound"/>.
				 <@s.text name="search.requestTime"/> ${addressResponseDTO.QTime/1000}  <@s.text name="search.secondUnit"/>. 
				<#if addressResponseDTO.message??><br/><br/><font color="#FF0000"><i><u> <@s.text name="global.message"/></u></i></font> : <i>${addressResponseDTO.message}</i><br/><br/>
</#if>
				</div>
				<#if addressResponseDTO.result.size()!=0>
				<br/>
				<#list addressResponseDTO.result as result>
	 				<div class="bodyResults">
								<#if result.countryCode??><div class="flag" >
									<img src="/images/flags/${result.countryCode}.png" alt=" country flag"/>
								</div></#if>
						<div class="resultblock">
								<div class="resultheaderleft">
								<#if (result.getGeocodingLevel().toString().toLowerCase().contains("street") || result.getGeocodingLevel().toString().toLowerCase().contains("house"))>
									<#if result.id?? && result.id!=0>
										<#if result.houseNumber??>
											<@s.url id="displayURL" action="displaystreet" includeParams="none" namespace="/public" >
						  						<@s.param name="gid" value="${result.id?c}" />
												<@s.param name="lat" value="${result.lat?c}" />
												<@s.param name="lng" value="${result.lng?c}" />
						 					</@s.url>
										<#else>
											<@s.url id="displayURL" action="displaystreet" includeParams="none" namespace="/public" >
						  						<@s.param name="gid" value="${result.id?c}" />
						 					</@s.url>
										</#if>
									</#if>
									
								        <#if result.id?? && result.id!=0 ><a href="${displayURL}"></#if>
											<#if result.id?? && result.id!=0>
											<#if result.countryCode?? && number_after_street_countrycode?seq_contains(result.countryCode)>

<#if result.streetName??>${result.streetName?cap_first}<#else><@s.text name="global.street.noname" /></#if><#if result.houseNumber??> ${result.houseNumber}</#if><#if result.city??>, ${result.city}</#if><#if result.id?? && result.id!=0 ></a></#if>  
											<#else>
											<#if result.houseNumber??>${result.houseNumber} </#if><#if result.streetName??>${result.streetName?cap_first}<#else><@s.text name="global.street.noname" /></#if><#if result.city??>, ${result.city}</#if><#if result.id?? && result.id!=0 ></a></#if>  
											</#if>
											<#else>
												<@s.text name="user.address.address" />
											</#if>
									</div>
									<#if result.distance??><div class="resultheaderright">${result.distance} m</div></#if>
								<#else>
									<#if result.id?? && result.id!=0 > 
									<@s.url id="displayURL" action="displayfeature" includeParams="none" namespace="/public" >
				  						<@s.param name="featureId" value="${result.id?c}" />
				 	 				</@s.url>
									</#if>						
									 	<a <#if result.id?? && result.id!=0 >href="${displayURL}"</#if>><#if result.name??>${result.name}<#elseif result.city?? >${result.city}</#if></a>
									</div>
								</#if>
								
						</div>
						<div class="separator"><hr/></div>

						<div class="summary">
							<#if result.confidence?? ><li><@s.text name="address.confidence"/> : ${result.confidence}</li></#if>
							<#if result.lat??><li><@s.text name="global.latitude"/> : ${result.lat?c}</li></#if>
							<#if result.lng??><li><@s.text name="global.longitude"/> : ${result.lng?c}</li></#if>
							<#if result.name??><li><@s.text name="global.name"/> : ${result.name}</li></#if>
							<#if result.recipientName??><li><@s.text name="global.name"/> : ${result.recipientName}</li></#if>
							<#if result.houseNumber??><li><@s.text name="address.houseNumber"/> : ${result.houseNumber}</li></#if>
							<#if result.houseNumberInfo?? ><li><@s.text name="address.houseNumberInfo"/> : ${result.houseNumberInfo}</li></#if>
							<#if result.civicNumberSuffix?? ><li><@s.text name="address.civicNumberSuffix"/> : ${result.civicNumberSuffix}</li></#if>
							<#if result.streetName??><li><@s.text name="global.streetName"/> : ${result.streetName?cap_first}</li></#if> 
							<#if result.streetType??><li><@s.text name="search.type.of.street"/> : <@s.text name="${result.streetType}"/><br/></li></#if>
							<#if result.dependentLocality?? ><li><@s.text name="address.dependentLocality"/> : ${result.dependentLocality}</li></#if>
							<#if result.PostTown?? ><li><@s.text name="address.PostTown"/> : ${result.PostTown}</li></#if>
							<#if result.city??><li><@s.text name="global.city"/> :  ${result.city}</li></#if>
							<#if result.zipCode??><li><@s.text name="global.zipCode"/> :  ${result.zipCode}</li></#if>
							<#if result.state?? ><li><@s.text name="address.state"/> : ${result.state}</li></#if>
							<#if result.prefecture?? ><li><@s.text name="address.prefecture"/> : ${result.prefecture}</li></#if>
							<#if result.district?? ><li><@s.text name="address.district"/> : ${result.district}</li></#if>
							<#if result.quarter?? ><li><@s.text name="address.quarter"/> : ${result.quarter}</li></#if>
							<#if result.extraInfo?? ><li><@s.text name="address.extraInfo"/> : ${result.extraInfo}</li></#if>
							<#if result.suiteType?? ><li><@s.text name="address.suiteType"/> : ${result.suiteType}</li></#if>
							<#if result.suiteNumber?? ><li><@s.text name="address.suiteNumber"/> : ${result.suiteNumber}</li></#if>
							<#if result.floor?? ><li><@s.text name="address.floor"/> : ${result.floor}</li></#if>
							<#if result.POBox?? ><li><@s.text name="address.POBox"/> : ${result.POBox}</li></#if>
							<#if result.POBoxInfo?? ><li><@s.text name="address.POBoxInfo"/> : ${result.POBoxInfo}</li></#if>
							<#if result.POBoxAgency?? ><li><@s.text name="address.POBoxAgency"/> : ${result.POBoxAgency}</li></#if>
							<#if result.preDirection?? ><li><@s.text name="address.preDirection"/> : ${result.preDirection}</li></#if>
							<#if result.postDirection?? ><li><@s.text name="address.postDirection"/> : ${result.postDirection}</li></#if>
							<#if result.streetNameIntersection?? ><li><@s.text name="address.streetNameIntersection"/> : ${result.streetNameIntersection}</li></#if>
							<#if result.streetTypeIntersection?? ><li><@s.text name="address.streetTypeIntersection"/> : ${result.streetTypeIntersection}</li></#if>
							<#if result.preDirectionIntersection?? ><li><@s.text name="address.preDirectionIntersection"/> : ${result.preDirectionIntersection}</li></#if>
							<#if result.postDirectionIntersection?? ><li><@s.text name="address.postDirectionIntersection"/> : ${result.postDirectionIntersection}</li></#if>
							<#if result.sector?? ><li><@s.text name="address.sector"/> : ${result.sector}</li></#if>
							<#if result.quadrant?? ><li><@s.text name="address.quadrant"/> : ${result.quadrant}</li></#if>
							<#if result.block?? ><li><@s.text name="address.block"/> : ${result.block}</li></#if>
							<#if result.country?? ><li><@s.text name="address.country"/> : ${result.country}</li></#if>

<#if result.id??>
<@s.url id="gmapsUrl" action="displaystreet" includeParams="none" namespace="/public" >
				  					<@s.param name="gid" value="${result.id?c}" />
				 				</@s.url>
</#if>
							<#if result.lat?? && result.long??><img src="/images/world_link.png" alt="Maps links" />&nbsp;<a href="${displayURL}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewStreet"/></a> | <a href="http://maps.google.com?q=${result.lat?c},${result.lng?c}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewOnGoogleMap"/></a> </#if>
						</div>
					</div>
					<div class="clear"></div>
					<br/><br/>
				</#list>
				

	<#else>
			<br/>
			  <div class="importantMessage indented"><@s.text name="search.noResult"/>!!</div><br/><br/>
					<@s.text name="addressparser.view.implemented.country" >
						<@s.param>http://www.gisgraphy.com/documentation/addressparser.htm#implemetedcountries</@s.param>					
					</@s.text>
<br/><br/><br/><br/>
			
		</#if>
		</div>

</#macro>

<#macro displaystreetResults streetResponseDTO>
	
<div id="searchResults">
				<div class="clear"><br/></div>
				<div class="bigText indented">${streetResponseDTO.numFound} <@s.text name="search.resultFound"/>. (<@s.text name="search.resultPaginateFromTo"><@s.param>${from}</@s.param><@s.param>${to}</@s.param></@s.text>).
				 <@s.text name="search.requestTime"/> ${streetResponseDTO.QTime/1000}  <@s.text name="search.secondUnit"/>. </div>
				<#if streetResponseDTO.result.size()!=0>
				<br/>
				<@s.url id="showAllOnMapsURL" value="/street/streetsearch" includeParams="all" forceAddSchemeHostAndPort="true" escapeAmp="false" />			
			&nbsp;&nbsp;<a href="http://maps.google.fr/maps?q=${showAllOnMapsURL?url('UTF-8')}%26format%3DATOM" target="_blank"><img src="/images/map_go.png" alt="map"/> <@s.text name="search.viewResultsOnMap"/></a>
					<#list streetResponseDTO.result as result>
	 				<div class="bodyResults">
					
						<div class="flag" >
							<img src="/images/flags/${result.countryCode}.png" alt=" country flag"/>
						</div>
						<div class="resultblock">
							<@s.url id="streetURL" action="displaystreet" includeParams="none" namespace="/public" >
				  					<@s.param name="gid" value="${result.gid?c}" />
				 				</@s.url>
								<div class="resultheaderleft"><a href="${streetURL}"><#if result.name??>${result.name}<#else><@s.text name="global.street.noname" /></#if> </a> <#if result.distance??><@s.text name="global.at"/> ${result.distance} <@s.text name="search.unit.meter"/></#if></div>
								<div class="resultheaderright"><#if result.streetType??><@s.text name="${result.streetType}" /></#if></div>
						</div>
					
						<div class="separator"><hr/></div>
					
						<div class="summary">
						<@s.text name="global.latitude"/> : ${result.lat?c}; 
						<br/>
						<@s.text name="global.longitude"/> : ${result.lng?c}
						<br/>
						<@s.text name="global.length"/> : ${result.length} m(s); 
						<br/>
						<#if result.oneWay??>
						<img src="/images/twoway.png" class="imgAlign" alt="<@s.text name="global.street.way"/>"/>
							<#if result.oneWay==true>
								<@s.text name="street.oneway"/>
							<#else>
								<@s.text name="street.twoway"/>
							</#if>
						<br/>
						</#if>
					<#if result.isIn??><@s.text name="global.is.in"/> : ${result.isIn};<br/></#if>
					<#if result.isInPlace??><@s.text name="global.is.inplace"/> : ${result.isInPlace};<br/></#if>
					<#if result.isInAdm??><@s.text name="global.is.inadm"/> : ${result.isInAdm};<br/></#if>
					<#if result.isInZip??><@s.text name="global.is.inzip"/> : ${result.isInZip};<br/></#if>
					<#if result.openstreetmapId??><@s.text name="global.openstreetmapId"/> : ${result.openstreetmapId?c};<br/></#if>
						<img src="/images/world_link.png" alt="Maps links" />&nbsp;<a href="${result.openstreetmapUrl}" class="greenlink" target="gisgraphyMap"><@s.text name="global.viewStreet"/></a>
						</div>
					</div>
					<div class="clear"></div>
					<br/><br/>
				</#list>
				<#if (from > 1)>
				<span style="float:left;padding-left:15px;"><@s.url id="previousURL" action="ajaxstreetsearch" method="search" includeParams="all" >
			  			<@s.param name="from" value="${from?c}-${defaultNumberOfResultsPerPage?c}" />
			  			<@s.param name="to" value="${to?c}-${defaultNumberOfResultsPerPage?c}" />
			 		</@s.url><a href="${previousURL}" class="bigText strong" onclick="$('lat').focus();return updatePaginationPrevious();" alt="previous">&lt;&lt;<@s.text name="global.previous"/></a></span>
			 	</#if>
			 	<#if defaultNumberOfResultsPerPage==streetResponseDTO.numFound>
				<span style="float:right;padding-right:15px;"><@s.url id="nextURL" action="ajaxstreetsearch" method="search" includeParams="all" >
			  			<@s.param name="from" value="${from?c}+${defaultNumberOfResultsPerPage?c}" />
			  			<@s.param name="to" value="${to?c}+${defaultNumberOfResultsPerPage?c}" />
			 		</@s.url><a href="${nextURL}" class="bigText strong" onclick="$('lat').focus();return updatePaginationNext();" alt="next"><@s.text name="global.next"/>&gt;&gt;</a></span>
			 	</#if>
			<#else>
			
			<br/><br/><br/>
			  <div class="importantMessage indented"><@s.text name="search.noResult"/>!!<br/><br/><br/><br/></div>
			 <div class="bigText indented"> <@s.text name="search.noresultMessage.openstreetmap.part1"/> <a href="http://www.geonames.org" target="geonames">Openstreetmap page</a> <@s.text name="search.noresultMessage.openstreetmap.part2"/></div>
		</#if>
		</div>
</#macro>





<#macro latlongsearchbox withSearchButton=true>
<div id="searchleftblock">
				
				Lat (&#x2195;) : <@s.textfield name="lat" maxlength="15" required="true" size="6" theme="simple" id="lat"/>
				<span class="spacer">Long (&#x2194;) : </span><@s.textfield name="lng" maxlength="15" required="true" size="6" theme="simple" id="lng"/>
				<div id="searchbuttonbar">
					<span id="searchexample">e.g. '3.5', '45.2', ... </span>
					<#if withSearchButton>
					<@s.submit title="%{getText('global.search')}" value="%{getText('global.search')}" theme="simple"  onclick="return doSearch()" />
					</#if>	
					<br/><br/>					
					<span id="myPosition"></span><br/>
				</div>
			</div>
			<@utils.includeJs jsName="/scripts/prototype.js"/>
			<script type="text/javascript">
			function fillPosition(position)
			{ 
			  $("lat").value = position.coords.latitude;
			  $("lng").value = position.coords.longitude;
			 
			}
			function displayMyPositionLink(){
				if (navigator.geolocation){
					$("myPosition").innerHTML ="<a href=\"javascript:myPosition()\"><img src=\"/images/boussole-32.png\" style=\"vertical-align:middle;\"   alt=\"<@s.text name="use.my.gps.position"/>\" title=\"<@s.text name="use.my.gps.position"/>\"><@s.text name="use.my.gps.position"/> !</a>";
				}
			}
			 
			function myPosition()
			{
			  try{	
			  navigator.geolocation.getCurrentPosition(fillPosition);
			 } catch (e) {}
			}
			displayMyPositionLink();
			</script>
</#macro>

<#macro googleStreetPanorama width heigth googleMapAPIKey CSSClass >
<@utils.includeJs jsName="http://maps.google.com/maps?file=api&amp;v=2.x&amp;key=${googleMapAPIKey} "/>

 			<div name="streetpanorama" id="streetpanorama" class="${CSSClass}"></div>
			<script type="text/javascript">
		  
		    var pano;
		    
		    function viewStreetPanorama(lat, lng) {
			$('streetpanorama').setStyle({ 
				width: '${width}px',
				height: '${heigth}px'
			});
		      var latlong = new GLatLng(lat,lng);
		      panoramaOptions = { latlng:latlong };
		      pano = new GStreetviewPanorama(document.getElementById("streetpanorama"), panoramaOptions);
		      GEvent.addListener(pano, "error", handleStreetPanoramaError);
		    }
		    
		    function handleStreetPanoramaError(errorCode) {
		      if (errorCode == GStreetviewPanorama.ErrorValues.FLASH_UNAVAILABLE) {
			alert("Error: Flash doesn't appear to be supported by your browser");
			return;
		      }
		      if (errorCode == GStreetviewPanorama.ErrorValues.NO_NEARBY_PANO) {
			return;
		      }
		else {
			alert ('An unknow error has occured on viewStreetPanorama : '+errorCode);		
		}
		    }  
		</script>

</#macro>

<#macro googleStreetView width heigth googleMapAPIKey CSSClass >
<@utils.includeJs jsName="http://maps.google.com/maps?file=api&amp;v=2.x&amp;key=${googleMapAPIKey} "/>
<@utils.includeJs jsName="/scripts/prototype.js"/>
 			<div name="streetview" id="streetview" class="${CSSClass}"></div>
			<script type="text/javascript">
		  
		    var map;
		    
		    function viewStreet(lat, lng, htmlToDisplayParam) {
			// try {
       
     

			$('streetview').setStyle({ 
				width: '${width}px',
				height: '${heigth}px'
			});
		     var map = new GMap2(document.getElementById("streetview"));
			var latlong = new GLatLng(lat, lng);
			var latlongcenter = new GLatLng(lat+0.0005, lng);
			map.setCenter(latlongcenter, 17);
			svOverlay = new GStreetviewOverlay();
			map.addOverlay(svOverlay);
			 var baseIcone = new google.maps.Icon();
			 baseIcone.iconSize=new google.maps.Size(12,20);
			 baseIcone.shadowSize=new google.maps.Size(20,22);
			 baseIcone.iconAnchor=new google.maps.Point(6,20);
			 baseIcone.infoWindowAnchor=new google.maps.Point(5,1);			
			iconeRouge = new google.maps.Icon(baseIcone, 'http://labs.google.com/ridefinder/images/mm_20_red.png', null, 'http://labs.google.com/ridefinder/images/mm_20_shadow.png');
			var marqueur = new google.maps.Marker(latlong, {icon: iconeRouge, title: "gisgraphy geocoding"});
			
			displayInfoWindowHTML = function() {
			if (typeof htmlToDisplayParam != 'undefined'){
				marqueur.openInfoWindowHtml(htmlToDisplayParam);
			} 
			}
			
			map.addOverlay(marqueur);
			displayInfoWindowHTML();
			google.maps.Event.addListener(marqueur, 'click', displayInfoWindowHTML); 
			



		      GEvent.addListener(map, "error", handleStreetViewError);
		//	 } catch (e) {alert('error during viewStreet : ' +e }
		    }
		    
		    function handleStreetViewError(errorCode) {
		      if (errorCode == GStreetviewPanorama.ErrorValues.FLASH_UNAVAILABLE) {
			alert("Error: Flash doesn't appear to be supported by your browser");
			return;
		      }
		     if (errorCode == GStreetviewPanorama.ErrorValues.NO_NEARBY_PANO) {
			$('streetpanorama').innerHtml="<@s.text name="search.nostreetpanoramaavailable" />";
			alert ('no panoavailable');
			return;
		      }
		else {
			alert ('An unknow error has occured on google streetview : '+errorCode);		
		}
		    }  
		</script>

</#macro>

<#macro leafletMap width heigth googleMapAPIKey CSSClass zoom=16>
<@utils.includeJs jsName="/scripts/prototype.js"/>
<@utils.includeJs jsName="/scripts/leaflet.js"/>
<link href="/styles/leaflet.css" rel="stylesheet" type="text/css" />
 			<div name="leafletmap" id="leafletmap" class="${CSSClass}"></div>
			<script type="text/javascript">
		  
		       var map;
			
		       function displayMap(lat, lng, htmlToDisplayParam) {
					$('leafletmap').setStyle({ 
						width: '${width}px',
						height: '${heigth}px'
					});
					
					if (typeof map == 'undefined'){
				     		map = L.map('leafletmap').setView([lat, lng], ${zoom});
					}
					var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
					var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
					var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 20, attribution: osmAttrib});
					map.addLayer(osm);
					
		
					if (typeof htmlToDisplayParam != 'undefined'){
						var popup = L.popup()
						   .setLatLng([lat, lng-0.0005])
   						 .setContent(htmlToDisplayParam)
   						 .openOn(map);
					}
			 }

		</script>

</#macro>




<#macro citySelector onCityFound>
<a name="cityselector"></a>
<#if (ambiguousCities?? &&  ambiguousCities.size() > 1 )>
		<div class="forminstructions"><img src="/images/puce_2.gif" class="imagenumberlist" alt="puce_2"/><@s.text name="search.choose.city"/> : </div><br/>
		<span class="searchfield">
		<span class="error"><@s.text name="search.city.ambiguous"/> ! </span>
		<br/><br/>
		<@s.select listKey="Feature_id" listValue="name" name="ambiguouscity" list="ambiguousCities" headerValue="-- %{getText('search.select.city')} --" headerKey="" multiple="false" required="true" labelposition="top" theme="simple" onchange="${onCityFound}();" id="ambiguouscity" />&nbsp;
		<@s.url id="chooseOtherCityUrl" action="geocoding_worldwide" includeParams="none" />
		<a href="${chooseOtherCityUrl}"><@s.text name="search.city.chooseOther" /></a>
		<br/>
		</span>
<#else>
		
		<#if cityFound>
			<br/>
			<div class="forminstructions"><img src="/images/puce_2.gif" class="imagenumberlist" alt="puce_2"/><@s.text name="search.selectedcity" /> : </div>			<span class="searchfield">
			<@s.textfield size="40" name="city" id="city"  value="${city}" theme="simple" disabled="true"/>&nbsp
			<@s.url id="chooseOtherCityUrl" action="geocoding_worldwide" includeParams="none" />
		<a href="${chooseOtherCityUrl}"><@s.text name="search.city.chooseOther" /></a>
		<#else>
			<div class="forminstructions"><img src="/images/puce_2.gif" class="imagenumberlist" alt="puce_2"/><@s.text name="search.choose.city"/> : </div>
			<#if (city?? && countryCode??) ><span class="error"><@s.text name="search.nocityfound"/> '${city}' ! </span><br/><br/></#if>
			<script type="text/javascript">
				validateNonEmptyQuery= function(){
					if ($('city').value == ''){
						alert("<@s.text name="search.city.empty"/>");
						 return false;
					} else {
						 return true;
					}
				 }
		</script>
			<span class="searchfield">
			<@s.textfield size="40" name="city" id="city" required="false"  theme="simple"/>
			<@s.submit title="%{getText('global.search')}" value="%{getText('search.city.validate.choice')}" theme="simple" id="streetsearchsubmitbutton" onclick="return validateNonEmptyQuery();"/>
		</span>
		</#if>
</#if>


</#macro>


<#macro streetNameAutoCompleter javascriptNameObject >
<link href="/scripts/autocomplete/styles.css" rel="stylesheet" type="text/css" />
<@utils.includeJs jsName="/scripts/prototype.js"/>
<@utils.includeJs jsName="/scripts/autocomplete/autocomplete.js"/>
<@s.hidden size="1" name="lat"  id="lat"  theme="simple" /><@s.hidden size="1" name="lng" required="false" id="lng" theme="simple"/>
<span class="searchfield">
	<@s.textfield size="40" name="streetname" required="false" id="streetname"  theme="simple"/><span style="display:none;" id="loadingImg"><img src="/images/loading.gif" alt="loading" class="imgAlign" style="width:25px;"></span>
	<span class="error streetautocompleteerror" id="streetNameAutocompletererror" >&nbsp;</span>
</span>
<br/>
<script type="text/javascript">
selectedStreetInformation = null;
getHtmlFromSelectedStreet = function(selectedStreetInformation){
var html = '<div id="EmplacementStreetView" class="googlemapInfoWindowHtml"><img src="/images/logos/logo_32.png" alt="free geocoding services" class="imgAlign"/><span  class="biggertext"><@s.text name="search.geocoding.services"/></span><hr/><span  class="biggertext">'+selectedStreetInformation.name+'</span><br/><br/>';
if (selectedStreetInformation.streetType != null){html= html + "<@s.text name="search.type.of.street"/> : "+selectedStreetInformation.streetType.toLowerCase()+"<br/><br/>";}
 if (selectedStreetInformation.oneWay==true){html = html+ '<@s.text name="street.oneway" />'; } else { html = html +'<@s.text name="street.twoway" />';}
html= html +'<br/><br/> <@s.text name="global.latitude" /> : '+selectedStreetInformation.lat+'<br/><br/><@s.text name="global.longitude" /> : '+selectedStreetInformation.lng+'<br/><br/> <@s.text name="global.length" /> : '+(selectedStreetInformation.length)+' m</div>';
return html;
}

${javascriptNameObject} = new Autocomplete('streetname', { serviceUrl: '/street/streetsearch?format=json"&from=1&to=10"', width: 340, deferRequestBy:200, minChars:2, onSelect: 
function(value, data){
	${javascriptNameObject}.streetResults.each(
		function(value, i) {
			if (value.gid == data){
				selectedStreetInformation = value;
				viewStreet(value.lat,value.lng,getHtmlFromSelectedStreet(selectedStreetInformation));
				viewStreetPanorama(value.lat,value.lng);
				return false;
			}
	       }.bind(this));
      },
onSearching: function(){
		$('loadingImg').show();
		$('streetNameAutocompletererror').innerHTML="&nbsp;"
	},
onEndSearching: function(){
		$('loadingImg').hide()
	},
onNoResultsFound: function(){
		$('streetNameAutocompletererror').innerHTML="<@s.text name="search.noResult"/>&nbsp; !"
	},
onFailToRetrieve: function(){
		$('streetNameAutocompletererror').innerHTML="<@s.text name="search.error"><@s.param>( </@s.param></@s.text>"+${javascriptNameObject}.errorMessage;
	}
});
  
</script>
</#macro>

<#macro editAlternateNameForm name_alternate last=false>
<@s.url id="udpateAlternateNameUrl" action="editAlternateName" method="doPersist" includeParams="none" namespace="/admin"></@s.url>
				<@s.form id="alternateform${name_alternate.id?c}" theme="simple" action="${udpateAlternateNameUrl}" method="get"><@s.hidden name="id" value="${name_alternate.getId()?c}" theme="simple"/>
					<@s.hidden name="decorate" value="none" theme="simple"/>
					<img src="/images/required_field.png"/ alt="" style="vertical-align:middle;"/><@s.text name="global.name"/> : <@s.textfield name="alternatename.name" value="${name_alternate.getName()}" theme="simple" cssStyle="margin-bottom:10px;margin-right:10px;"/>
					<#if name_alternate.language??>
						<@s.text name="search.language"/> : <@s.textfield name="alternatename.language" value="${name_alternate.getLanguage()}" theme="simple" cssStyle="width:50px;"/>
					<#else>
						<@s.text name="search.language"/> : <@s.textfield name="alternatename.language"  theme="simple" cssStyle="width:50px;"/>
					</#if>


					<@s.url id="deleteAlternateNameUrl" action="editAlternateName!delete"  includeParams="none" namespace="/admin" >	
						<@s.param name="id" value="${name_alternate.id?c}"/>
					</@s.url>
					<a onClick="deleteAjax('${deleteAlternateNameUrl}',alternateform${name_alternate.id?c})" ><img src="/images/delete.png"/ alt="<@s.text name="button.delete"/>" style="padding-right:5px;vertical-align:middle;width:18px;"></a>
					<img src="/images/save.png" style="padding-right:5px;vertical-align:middle;width:18px;"  onClick="saveAjax($(this).up())"/>
				</@s.form>
<#if last>
<div id="addNewAlternateNamePlaceholder"></div>
</#if>

</#macro>

<#macro addAlternateNameForm gisfeatureId>
<@s.url id="addAlternateNameUrl" action="editAlternateName" method="doPersist" includeParams="none" namespace="/admin"></@s.url>
<@s.form  id="addalternatenameform" name="addalternatenameform" action="${addAlternateNameUrl}" method="get"  theme="simple">
<@s.hidden name="decorate" value="none" theme="simple"/>
<@s.hidden name="gisFeatureId" value="${gisfeatureId}" theme="simple"/>
		<img src="/images/required_field.png"/ alt="" style="vertical-align:middle;"/><@s.text name="global.name"/> : <@s.textfield name="alternatename.name" value="" theme="simple" cssStyle="margin-bottom:10px;margin-right:10px;"/>
		<@s.text name="search.language"/> : <@s.textfield name="alternatename.language" value="" theme="simple" cssStyle="width:50px;"/>
		<img src="/images/add.png"/ alt="<@s.text name="button.add"/>" style="padding-right:5px;vertical-align:middle;width:18px;" onClick="AjaxAdd($(this).up(),'addNewAlternateNamePlaceholder')">
</@s.form>
</#macro>


<#macro addZipCodeForm gisfeatureId>
<@s.url id="addZipCodeUrl" action="editZipCode" method="doPersist" includeParams="none" namespace="/admin"></@s.url>
<@s.form  id="addzipcodeform" name="addzipcodeform" action="${addZipCodeUrl}" method="get"  theme="simple">
<@s.hidden name="decorate" value="none" theme="simple"/>
<@s.hidden name="gisFeatureId" value="${gisfeatureId}" theme="simple"/>
		<img src="/images/required_field.png"/ alt="" style="vertical-align:middle;"/><@s.text name="global.code"/> : <@s.textfield name="zipCode.code" value="" theme="simple" cssStyle="margin-bottom:10px;margin-right:10px;"/>
		<img src="/images/add.png"/ alt="<@s.text name="button.add"/>" style="padding-right:5px;vertical-align:middle;width:18px;" onClick="AjaxAdd($(this).up(),'addNewZipCodePlaceholder')">
</@s.form>
</#macro>




<#macro editZipCodeForm zipCode last=false>
<@s.url id="udpateZipCodeUrl" action="editZipCode" method="doPersist" includeParams="none" namespace="/admin"></@s.url>
				<@s.form id="zipCodeform${zipCode.id?c}" theme="simple" action="${udpateZipCodeUrl}" method="get"><@s.hidden name="id" value="${zipCode.id?c}" theme="simple"/>
					<@s.hidden name="decorate" value="none" theme="simple"/>
					<img src="/images/required_field.png"/ alt="" style="vertical-align:middle;"/><@s.text name="global.code"/> : <@s.textfield name="zipCode.code" value="${zipCode.getCode()}" theme="simple" cssStyle="margin-bottom:10px;margin-right:10px;"/>
					<@s.url id="deleteZipCodeUrl" action="editZipCode!delete"  includeParams="none" namespace="/admin" >	
						<@s.param name="id" value="${zipCode.id?c}"/>
					</@s.url>
					<a onClick="deleteAjax('${deleteZipCodeUrl}',zipCodeform${zipCode.id?c})" ><img src="/images/delete.png"/ alt="<@s.text name="button.delete"/>" style="padding-right:5px;vertical-align:middle;width:18px;"></a>
					<img src="/images/save.png" style="padding-right:5px;vertical-align:middle;width:18px;"  onClick="saveAjax($(this).up())"/>
				</@s.form>
<#if last>
<div id="addNewZipCodePlaceholder"></div>
</#if>

</#macro>