<#import "macros/utils.ftl" as utils>
<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title><@s.text name="search.geocoding.title"/></title>
<meta name="Description" content="Worldwide geocoding free webservices and street search for openstreetmap. Pagination, indentation, several languages are supported"/>
<meta name="heading" content="<@s.text name="search.geocoding.title"/>"/>
<meta name="keywords" content="geocoding world worldwide street search java openstreetmap webservices postgis hibernate toponyms gazeteers"/>
<@utils.includeJs jsName="/scripts/prototype.js"/>
</head>
<body onunload="GUnload()">
<br/>
<div id="gissearch">
<noscript>
	<div class="tip yellowtip">
	<@s.text name="global.noscript"/>
	</div>
	<br/>
</noscript>
	<@s.url id="geocodingFormUrl" action="geocoding" includeParams="none" method="search" namespace="/public" />
			
<@breadcrumbs.searchNavBar/>

<div  style="line-height:1.5em;">
 <@s.text name="search.geocoding.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 <@s.text name="search.geocoding.notUsinggooglemap"/>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://www.gisgraphy.com/documentation/user-guide.htm#geocodingservice</@s.param>
 	<@s.param>http://www.gisgraphy.com/documentation/installation/index.htm</@s.param>
 </@s.text>.
</div><br/>
<@s.url id="addressparserUrl" action="addressparser" includeParams="none" namespace="/public" />
<@s.url id="fulltextSearchUrl" action="ajaxfulltextsearch" includeParams="none" namespace="/public" />
<span style="line-height:1.5em;">
<@s.text name="search.geocoder.try.parser">
	<@s.param>${fulltextSearchUrl}</@s.param>
	<@s.param>${addressparserUrl}</@s.param>
</@s.text>
</span>
<br/><br/><br/>
<div class="clear"></div>
	<@gisgraphysearch.displayAddressForm url=geocodingFormUrl structured=structured />
</body></html>