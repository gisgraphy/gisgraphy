<#import "macros/utils.ftl" as utils>
<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title><@s.text name="search.geolocsearch.title"/></title>
<meta name="Description" content="Free opensource geocoder and webservices for geonames and openstreetmap data. Results are shown in an human readable way. Pagination, indentation, several languages are supported"/>
<meta name="heading" content="<@s.text name="search.geolocsearch.title"/>"/>
<meta name="keywords" content="geoloc find nearby GPS java geonames webservices postgis hibernate toponyms gazeteers"/>
</head>
<body>
<br/>
<div id="gissearch">
<noscript>
	<div class="tip yellowtip">
<@s.text name="global.noscript"/>
	</div>
	<br/>
</noscript>
	
			<@breadcrumbs.searchNavBar/>

<@s.url id="ajaxgeolocSearchUrl" action="ajaxgeolocsearch" includeParams="none" namespace="" />
<div class="clear"></div><div  style="line-height:1.5em;">
<@s.text name="search.fulltext.desc"/>.
 <@s.text name="search.geonames.data"><@s.param>http://geonames.org</@s.param></@s.text>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://www.gisgraphy.com/documentation/user-guide.htm#geolocservice</@s.param>
 	<@s.param>http://www.gisgraphy.com/documentation/installation/index.htm</@s.param>
 </@s.text>.
 <@s.text name="search.ws.exampleofuse">
 	<@s.param>${ajaxgeolocSearchUrl}</@s.param>
 </@s.text>.
</div><br/><br/>
<div class="clear"></div>
			
	<@s.form action="/geoloc/geolocsearch" method="get" id="geolocsearch">
		<div id="simplesearch">
			<@gisgraphysearch.latlongsearchbox/>
			<@breadcrumbs.geolocSearchTooltip advancedSearchURLParam="geolocsearch"/>
	</div>
	<div class="clear"><br/></div>
	<div class="biggertext"><@s.text name="search.geoloc.defaultPlaceType.part1"/></div>
	<div class="biggertext"><@s.text name="search.geoloc.defaultPlaceType.part2"/></div>
		 <div class="clear"></div>
	 <@s.if test="advancedSearch">
			<div id="advancedsearch" >
		</@s.if>
		<@s.else>
    		<div id="advancedsearch" style="display:none;" >
		</@s.else>
	<fieldset >
		<legend>&nbsp; <@s.text name="search.advanced"/> &nbsp; </legend>
		<span class="advancedsearchcat"><@s.text name="search.moreCriteria"/></span>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.radius"/> (<@s.text name="global.unit"/>) : </span>
			<@s.textfield name="radius" maxlength="10" required="true" size="10" theme="simple" />
			<br/>
		</span>
		<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.placetype"/> : </span>
			<@s.select headerKey="" headerValue="--Any place--"  name="placetype" list="placetypes"  multiple="false" required="false"  labelposition="left" theme="simple"/>
			<br/>
		</span>
		<br/>
		<div class="clear"></div>
		<span class="advancedsearchcat"><@s.text name="search.outputSpecs"/></span>
		<hr/>
		<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.format"/> : </span><@s.select value="geolocFormats[0]" name="format" list="formats" multiple="false" required="false" labelposition="left" theme="simple" />
		</span>
		<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.includeDistance"/></span> : <@s.radio name="distance" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}"  />
		</span>
		<div class="clear"></div><br/>
		<@s.text name="search.includeDistance.resultNotSorted"/>
		<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.indent"/> : </span><@s.checkbox label="Indent output" labelposition="left" name="indent" theme="simple" />
		</span>
		<div class="clear"></div>
		<span class="advancedsearchcat"><@s.text name="search.paginationSpecs"/></span>&nbsp;&nbsp;<@s.text name="search.pagination.info"/>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.pagination.from"/> : </span><@s.textfield size="5" maxlength="3" name="from" required="false"  theme="simple"/> 
		</span>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.pagination.to"/> : </span><@s.textfield size="5" maxlength="3" name="to" required="false"  theme="simple"/> 
		</span>
	</fieldset>
	</div>
	</@s.form>
	<@utils.includeJs jsName="/scripts/prototype.js"/>
	<script type="text/javascript" >
	doSearch = function(){
 		return true;
 	}
	</script>
</div>
</body>
</html>