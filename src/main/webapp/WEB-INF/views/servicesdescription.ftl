<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<html>
<head>
<title><@s.text name="search.pages.description"/></title>
<meta name="Description" content="Description of Gisgraphy services"/>
<meta name="heading" content="<@s.text name="search.pages.description"/>"/>
<meta name="keywords" content="description gisgraphy services geoloc fulltext"/>
</head>
<body>
<br/>
<div>
<@breadcrumbs.searchNavBar/>
<h2 class="header"><@s.text name="search.pages.description"/> : </h2>
<@s.url id="simpleFulltextSearchUrl" action="fulltextsearch" includeParams="none" namespace="" />
<@s.url id="simpleGeolocSearchUrl" action="geolocsearch" includeParams="none" namespace="" />
<@s.url id="geocodingSearchUrl" action="geocoding" includeParams="none" namespace="/public" />
<@s.url id="reverseGeocodingSearchUrl" action="reverse_geocoding_worldwide" includeParams="none" namespace="/public" />
<@s.url id="streetSearchUrl" action="streetSearch" includeParams="none" namespace="/public" />
<@s.url id="addressparserUrl" action="addressparser" includeParams="none" namespace="/public" />
<@s.url id="ajaxFulltextSearchUrlAdvanced" action="ajaxfulltextsearch" includeParams="all" namespace="" >
	 <@s.param name="advancedSearch" value="true" />
	</@s.url>
<@s.url id="ajaxGeolocSearchUrl" action="ajaxgeolocsearch" includeParams="all"  namespace=""  />

<ul>
<li><div class="biggertext listspace"><a href="${geocodingSearchUrl}"><@s.text name="search.geocoding.breadcrumbs"/></a> : 
<@s.text name="search.geocoding.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 <br/>
</li>

<li><div class="biggertext listspace" ><a href="${reverseGeocodingSearchUrl}"><@s.text name="search.geocoding.reverse.breadcrumbs"/></a> : 
<@s.text name="search.reversegeocoding.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 </div>
</li>

<li><div class="biggertext listspace" ><a href="${addressparserUrl}"><@s.text name="address.parser.breadcrumbs"/></a> : 
<@s.text name="search.addressparser.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 </div>
</li>

<li><div class="biggertext listspace"><a href="${streetSearchUrl}"><@s.text name="search.street.breadcrumbs"/></a> : 
<@s.text name="search.streetsearch.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 </div>
</li>

<li><div class="biggertext listspace"><a href="${ajaxGeolocSearchUrl}"><@s.text name="search.geolocDemo.breadcrumbs"/></a> : 
<@s.text name="search.geoloc.desc"/>.
 <@s.text name="search.geonames.data"><@s.param>http://geonames.org</@s.param></@s.text>.
 </div>
</li>

<li><div class="biggertext listspace"><a href="${ajaxFulltextSearchUrlAdvanced}"><@s.text name="search.fulltextDemo.breadcrumbs"/></a> : 
<@s.text name="search.fulltext.desc"/>.
 <@s.text name="search.geonames.data"><@s.param>http://geonames.org</@s.param></@s.text>.
 </div>
</li>


<li><div class="biggertext listspace"><a href="${simpleFulltextSearchUrl}"><@s.text name="search.fulltext.breadcrumbs"/></a> : <@s.text name="search.ws.desc"><@s.param>${ajaxFulltextSearchUrl}</@s.param></@s.text>.
</div>
</li>
<li><div class="biggertext listspace"><a href="${simpleGeolocSearchUrl}"><@s.text name="search.geoloc.breadcrumbs"/></a> : <@s.text name="search.ws.desc"><@s.param>${ajaxGeolocSearchUrl}</@s.param></@s.text>.</div>
</li>
</ul>
<br/><br/><br/>

</div>

</body>
</html>