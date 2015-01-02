<#import "macros/utils.ftl" as utils>
<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title><@s.text name="search.fulltext.title"/></title>
<meta name="Description" content="Free opensource geocoder and webservices for geonames and openstreetmap data. Results can be output in XML, json, PHP, ruby, python, Atom, RSS/GeoRSS. Pagination, indentation, several languages are supported"/>
<meta name="heading" content="<@s.text name="search.fulltext.title"/>"/>
<meta name="keywords" content="fulltext java geonames webservices lucene solr hibernate toponyms gazeteers"/>
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
<@s.url id="ajaxFulltextSearchUrl" action="ajaxfulltextsearch" includeParams="none" namespace="" />
<div class="clear"></div><div style="line-height:1.5em;">
<@s.text name="search.fulltext.desc"/>.
 <@s.text name="search.geonames.data"><@s.param>http://geonames.org</@s.param></@s.text>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://www.gisgraphy.com/documentation/user-guide.htm#fulltextservice</@s.param>
 	<@s.param>http://www.gisgraphy.com/documentation/installation/index.htm</@s.param>
 </@s.text>.
 <@s.text name="search.ws.exampleofuse">
 	<@s.param>${ajaxFulltextSearchUrl}</@s.param>
 </@s.text>.
</div><br/><br/>
<div class="clear"></div>

	<@s.form action="/fulltext/fulltextsearch" method="get" id="fulltextsearch">
		<div id="simplesearch">
			<div id="searchleftblock">
				<@s.textfield name="q" required="true" size="25" theme="simple" maxlength="200" />
				<div id="searchbuttonbar">
						<span id="searchexample">e.g. Paris, الرباط ,75000,  ... </span>
					<@s.submit title="%{getText('global.search')}" value="%{getText('global.search')}" theme="simple"/>
				</div>
			</div>
			<@breadcrumbs.fulltextSearchTooltip advancedSearchURLParam="fulltextsearch"/>
	</div>
		<br/>
		 <div class="clear"></div>
		 <@breadcrumbs.opensearchFulltext/>
		 <@s.if test="advancedSearch">
			<div id="advancedsearch" >
		</@s.if>
		<@s.else>
    		<div id="advancedsearch" style="display:none;" >
		</@s.else>
	<fieldset >
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
			<span class="searchfieldlabel"><@s.text name="global.placetype"/> : </span>
			<@s.select headerKey="" headerValue="--any place--"  name="placetype" list="placetypes"  multiple="true" size="5" required="false"  labelposition="left" theme="simple"/>
			<br/><br/>
			<@s.text name="global.useshifttoselectmore"/>
			<br/>
		</span>
		
		<div class="clear"></div>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.allwordsrequired"/></span> : <@s.radio name="allwordsrequired" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}"  />
		</span>
		<div class="clear"></div>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.country"/> : </span><@s.select label="In " listKey="iso3166Alpha2Code" listValue="name" name="country" list="countries" headerValue="--All countries--" headerKey="" multiple="false" required="false" labelposition="left" theme="simple" /> 
			<br/>
		</span>
		<div class="clear"></div>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.spellChecking"/> : </span><@s.checkbox label="spellchecking" labelposition="left" name="spellchecking" theme="simple" />
		</span>
		<div class="clear"></div>
		<span class="advancedsearchcat"><@s.text name="search.outputSpecs"/></span>
		<hr/>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.language"/> : </span><@s.select name="lang" list="languages" headerValue="--No specific--" headerKey="" multiple="false" required="false" labelposition="left" theme="simple"/>
			<@s.text name="search.language.info"/>.<@s.text name="search.language.style.info"/>.
		</span>
		<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.format"/> : </span><@s.select label="Output in " name="format" list="formats" multiple="false" required="false" labelposition="left" theme="simple" />
		</span>
		<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="search.verbosity"/> : </span><@s.select label="With a verbosity " name="style" list="verbosityModes" multiple="false" required="false" labelposition="left" theme="simple" />
		</span>
		<br/>
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
</div>
</body>
</html>