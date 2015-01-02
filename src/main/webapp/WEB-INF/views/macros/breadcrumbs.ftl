<#macro searchNavBar>
	<@s.url id="simpleFulltextSearchUrl" action="fulltextsearch" includeParams="none" namespace="" />
	<@s.url id="simpleGeolocSearchUrl" action="geolocsearch" includeParams="none" namespace="" />
	<@s.url id="geocodingAutocompleteUrl" action="geocoding_worldwide" includeParams="none" namespace="/public" />
	<@s.url id="geocodingSearchUrl" action="geocoding" includeParams="none" namespace="/public" />
	<@s.url id="geocodingStructuredSearchUrl" action="geocoding" includeParams="none" namespace="/public" >
		 <@s.param name="structured" value="true" />	
	</@s.url>

	<@s.url id="reverseGeocodingSearchUrl" action="reverse_geocoding_worldwide" includeParams="none" namespace="/public" />
	<@s.url id="addressparserurl" action="addressparser" includeParams="none" namespace="/public" />
	<@s.url id="streetSearchUrl" action="streetSearch" includeParams="none" namespace="/public" />
	<@s.url id="ajaxFulltextSearchUrl" action="ajaxfulltextsearch" includeParams="none" namespace="" />
	<@s.url id="ajaxFulltextSearchUrlAdvanced" action="ajaxfulltextsearch" includeParams="none" namespace="" >
	 <@s.param name="advancedSearch" value="true" />
	</@s.url>
	<@s.url id="ajaxGeolocSearchUrl" action="ajaxgeolocsearch" includeParams="none"  namespace=""  />
	<@s.url id="advancedStreetSearchFulltext" action="ajaxfulltextsearch?placetype=street" namespace="">
	 <@s.param name="advancedSearch" value="true" />
	 <@s.param name="placetype" value="street" />
	</@s.url>
	<@s.url id="streetSearchFulltext" action="ajaxfulltextsearch?placetype=street" namespace="">
	 <@s.param name="placetype" value="street" />
	</@s.url>
<script language="JavaScript">
	window.onload = function()
	{
		var lis = document.getElementById('cssdropdown').getElementsByTagName('li');
		for(i = 0; i < lis.length; i++)
		{
			var li = lis[i];
			if (li.className == 'headlink')
			{
				li.onmouseover = function() { this.getElementsByTagName('ul').item(0).style.display = 'inline'; }
				li.onmouseout = function() { this.getElementsByTagName('ul').item(0).style.display = 'none'; }
			}
		}
	}
</script>
<div style="margin:auto auto;width:100%;text-align:center;height:auto">
<!--<img src="/images/map-icon.png" style="width:80px;vertical-align:middle;"/>--><span style="font-size:1.2em;"><@s.text name="search.try.demo"/><br/><br/></span>
</div>
<div id="navbar">
 <ul id="cssdropdown" class="dropdown dropdown-horizontal">
		<li>
			<a href="${geocodingSearchUrl}"><@s.text name="search.geocoding.breadcrumbs"/>&nbsp;&#x25BC;&nbsp;</a>
			<ul>
				<li><a href="${geocodingSearchUrl}"><@s.text name="search.geocoding.breadcrumbs"/></a></li>
				<li><a href="${geocodingAutocompleteUrl}"><@s.text name="search.geocoding.breadcrumbs"/> <@s.text name="by.autocompletion.breadcrumbs"/></a></li>
				<li><a href="${geocodingStructuredSearchUrl}"><@s.text name="search.geocoding.structured"/></a></li>
			</ul>
		</li>
		<li >
			<a href="${reverseGeocodingSearchUrl}"><@s.text name="search.geocoding.reverse.breadcrumbs"/></a>
		</li>
		<li >
			<a href="${addressparserurl}"><@s.text name="address.parser.breadcrumbs"/></a>
		</li>
		<li >
			<a href="${ajaxFulltextSearchUrl}"><@s.text name="global.zipCode"/></a>
		</li>
		<li>
			<a href="#"><@s.text name="search.street.breadcrumbs" />&nbsp;&#x25BC;&nbsp;</a>
			<ul>
				<li><a href="${streetSearchFulltext}"><@s.text name="by.name.breadcrumbs"/></a></li>
				<li><a href="${advancedStreetSearchFulltext}"><@s.text name="by.name.and.gps.breadcrumbs"/></a></li>
				<li><a href="${streetSearchUrl}"><@s.text name="by.position.breadcrumbs"/></a></li>
				<li><a href="${geocodingAutocompleteUrl}"><@s.text name="by.autocompletion.breadcrumbs"/></a></li>
			</ul>
		</li>
		
		<li>
			<a href="#"><@s.text name="search.geolocDemo.breadcrumbs"/>&nbsp;&#x25BC;&nbsp;</a>
			<ul>
				<li><a href="${ajaxGeolocSearchUrl}"><@s.text name="geoloc.findnearby"/></a></li>
				<li><a href="${simpleGeolocSearchUrl}"><@s.text name="webservice.ui"/></a></li>
				
			</ul>
		</li>
		<li>
			<a href="#"><@s.text name="search.fulltextDemo.breadcrumbs"/>&nbsp;&#x25BC;&nbsp;</a>
		<ul>
				<li><a href="${ajaxFulltextSearchUrl}"><@s.text name="search.fulltextDemo.breadcrumbs.text"/></a></li>
				<li><a href="${ajaxFulltextSearchUrlAdvanced}"><@s.text name="search.fulltextDemo.breadcrumbs.gps"/></a></li>
				<li><a href="${simpleFulltextSearchUrl}"><@s.text name="webservice.ui"/></a></li>
			</ul>
		</li>
	</ul>
</div>
</#macro>

<#macro paypalDonation>
<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_s-xclick"/>
<input type="hidden" name="hosted_button_id" value="1694440"/>
<input type="image" src="https://www.paypal.com/en_US/i/btn/btn_donate_SM.gif"  name="submit" alt="donate"/>
<img alt="pixel" src="https://www.paypal.com/fr_FR/i/scr/pixel.gif" width="1" height="1"/>
</form>
</#macro>

<#macro paypalDonationBig>
<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_s-xclick"/>
<input type="hidden" name="hosted_button_id" value="1694727"/>
<input type="image" src="https://www.paypal.com/en_US/i/btn/btn_donate_LG.gif" name="submit" alt="donate"/>
<img alt="pixel" border="0" src="https://www.paypal.com/fr_FR/i/scr/pixel.gif" width="1" height="1"/>
</form>
</#macro>

<#macro opensearchFulltext>
<#if request.getHeader('User-Agent')??>
	<#assign userAgent = request.getHeader('User-Agent')/>
	<#if userAgent.contains('Firefox') >
		<br/>
		<div class="center"><@s.text name="search.opensearch.tip.firefox.part1"/> <img src="/images/opensearch_mozilla.gif" class="imgAlign" alt="opensearch mozilla"/> <@s.text name="search.opensearch.tip.firefox.part2"/></div>
	<#elseif userAgent.contains('MSIE 7.0')>
		<div class="center"><@s.text name="search.opensearch.tip.ie.part1"/> <img src="/images/opensearch_internet_explorer.gif" class="imgAlign" alt="opensearch internet explorer"/> <@s.text name="search.opensearch.tip.ie.part2"/></div>
	</#if>
</#if>
</#macro>




<#macro fulltextSearchTooltip advancedSearchURLParam>
<div id="tooltip">
			 <@s.url id="advancedSearchUrl" action="${advancedSearchURLParam}" includeParams="all" >
			  <@s.param name="advancedSearch" value="true" />
			 </@s.url>
			 <@s.url id="servicesDescription" action="servicesdescription" includeParams="all" namespace="/public" />
				<a href="${advancedSearchUrl}" onclick="$('advancedsearch').toggle();return false;"><@s.text name="search.advanced"/></a>
				<br/>
				<a href="http://www.gisgraphy.com/documentation/user-guide.htm#fulltextservice" ><@s.text name="global.xml.api"/></a>
				<br/>
				<a href="${servicesDescription}"><@s.text name="search.servicesdescription.title"/></a>
				<br/>
			</div>
</#macro>

<#macro geolocSearchTooltip advancedSearchURLParam>
<div id="tooltip">
			 <@s.url id="advancedSearchUrl" action="${advancedSearchURLParam}" includeParams="all" >
			  <@s.param name="advancedSearch" value="true" />
			 </@s.url>
			  <@s.url id="servicesDescription" action="servicesdescription" includeParams="all" namespace="/public" />
				<a href="${advancedSearchUrl}" onclick="$('advancedsearch').toggle();return false;"><@s.text name="search.advanced"/></a>
				<br/>
				<a href="http://www.gisgraphy.com/documentation/user-guide.htm#geolocservice" ><@s.text name="global.xml.api"/></a>
				<br/>
				<a href="${servicesDescription}"><@s.text name="search.servicesdescription.title"/></a>
				<br/>
			</div>
</#macro>


<#macro streetsearchTooltip advancedSearchURLParam="" docAnchor="streetservice">
<div id="tooltip">
			<#if advancedSearchURLParam!="">
			 	<@s.url id="advancedSearchUrl" action="${advancedSearchURLParam}" includeParams="all" >
			 		 <@s.param name="advancedSearch" value="true" />
			 	</@s.url>
					<a href="${advancedSearchUrl}" onclick="$('advancedsearch').toggle();return false;"><@s.text name="search.advanced"/></a>
					<br/>
				</#if>
				<a href="http://www.gisgraphy.com/documentation/user-guide.htm#${docAnchor}" ><@s.text name="global.xml.api"/></a>
				<br/>
			  	<@s.url id="servicesDescription" action="servicesdescription" includeParams="all" namespace="/public" />
				<a href="${servicesDescription}"><@s.text name="search.servicesdescription.title"/></a>
				<br/>
			</div>
</#macro>