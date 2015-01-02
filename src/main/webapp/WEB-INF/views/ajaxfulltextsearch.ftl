<#import "macros/utils.ftl" as utils>
<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title><@s.text name="search.ajaxfulltext.title"/></title>
<meta name="Description" content="Free fulltext webservices demo for Geonames Data. Results are shown in an human readable way. Pagination, indentation, several languages are supported"/>
<meta name="heading" content="<@s.text name="search.ajaxfulltext.title"/>"/>
<meta name="keywords" content="fulltext java geonames ajax webservices lucene solr hibernate toponyms gazeteers"/>
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
	<@s.url id="simpleFulltextSearchUrl" action="fulltextsearch" includeParams="none" namespace="" />
			<@breadcrumbs.searchNavBar/>
<div class="clear"></div><div  style="line-height:1.5em;">
<@s.text name="search.fulltext.desc"/>.
 <@s.text name="search.geonames.data"><@s.param>http://geonames.org</@s.param></@s.text>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://www.gisgraphy.com/documentation/user-guide.htm#fulltextservice</@s.param>
 	<@s.param>http://www.gisgraphy.com/documentation/installation/index.htm</@s.param>
 </@s.text>.
 <@s.text name="search.ws.use">
 	<@s.param>${simpleFulltextSearchUrl}</@s.param>
 </@s.text>.
</div><br/><br/>
<div class="clear"></div>

<@gisgraphysearch.fulltextsearchform ajax=true/>
 <@breadcrumbs.opensearchFulltext />
</body>
</html>