<#import "macros/utils.ftl" as utils>
<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title><@s.text name="address.parser.breadcrumbs"/></title>
<meta name="Description" content="free opensource address parser"/>
<meta name="heading" content="<@s.text name="address.parser.breadcrumbs"/>"/>
<meta name="keywords" content="address parser free"/>
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
	<@s.url id="addressparserurl" action="addressparser" includeParams="none" method="search" namespace="/public" />
			
<@breadcrumbs.searchNavBar/>

<div  style="line-height:1.5em;">
 <@s.text name="search.addressparser.desc"/>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://address-parser.net/documentation/documentation.php</@s.param>
 	<@s.param>http://address-parser.net/product-java.php</@s.param>
 </@s.text>.
</div><br/>
<@s.url id="geocodingSearchUrl" action="geocoding" includeParams="none" namespace="/public" />
<@s.url id="fulltextSearchUrl" action="ajaxfulltextsearch" includeParams="none" namespace="/public" />
<span style="line-height:1.5em;"><@s.text name="search.addressparser.try.geocoder">
	<@s.param>${geocodingSearchUrl}</@s.param>
	<@s.param>${fulltextSearchUrl}</@s.param>
</@s.text></span>
<br/><br/><br/>
<div class="clear"></div>
	<@gisgraphysearch.displayAddressForm url=addressparserurl structured=false/>
</body></html>