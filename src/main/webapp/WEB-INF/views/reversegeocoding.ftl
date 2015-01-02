<#import "macros/utils.ftl" as utils>
<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title><@s.text name="search.geocoding.reverse.title"/></title>
<meta name="Description" content="Worldwide Reverse geocoding free webservices and street search for openstreetmap. Pagination, indentation, several languages are supported"/>
<meta name="heading" content="<@s.text name="search.geocoding.reverse.title"/>"/>
<meta name="keywords" content=" reverse geocoding world worldwide street search free java openstreetmap webservices "/>
</head>
<body>
<br/>
<div id="gissearch">
<noscript>
	<div class="tip yellowtip">
	<@s.text name="global.noscript.required"/>
	</div>
	<br/>
</noscript>
	
			<@breadcrumbs.searchNavBar/>

<div class="clear"></div><div style="line-height:1.5em;">

<@s.text name="search.reversegeocoding.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 <@s.text name="search.geocoding.notUsinggooglemap"/>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://www.gisgraphy.com/documentation/user-guide.htm#reversegeocodingservice</@s.param>
 	<@s.param>http://www.gisgraphy.com/documentation/installation/index.htm</@s.param>
 </@s.text>.
</div><br/><br/>
<div class="clear"></div>


	<@s.form action="/reversegeocoding/search" method="get" id="reversegeocoding">
		<div id="simplesearch">
			<@gisgraphysearch.latlongsearchbox/>
			<@breadcrumbs.streetsearchTooltip advancedSearchURLParam="" docAnchor="reversegeocodingservice"/>
<@s.hidden size="1" name="from" id="from"  value="1" theme="simple" />
<@s.hidden size="1" name="to"  id="to" value="1" theme="simple"/>
	</div>
	<div class="clear"><br/></div>
	</@s.form>
</div>
<div id="popupResults"></div>

<@utils.includeJs jsName="/scripts/prototype.js"/>
<@utils.includeJs jsName="/scripts/gisgraphyapi.js"/>

<script type="text/javascript" >
pointIsRequired=true;
getHtmlFromSelectedStreet = function(selectedStreetInformation){
var html = '<div id="EmplacementStreetView" class="googlemapInfoWindowHtml"><img src="/images/logos/logo_32.png" alt="free geocoding services" class="imgAlign"/><span  class="biggertext"><@s.text name="search.geocoding.reverse.breadcrumbs"/></span><hr/>';
if (typeof selectedStreetInformation.houseNumber != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.housenumber"/> : '+selectedStreetInformation.houseNumber+'</span><br/>';}

html= html+'<span  class="biggertext"><@s.text name="global.name"/> : '
if (typeof selectedStreetInformation.name != "undefined"){html= html+selectedStreetInformation.name+'</span><br/>';}else{html= html+'<@s.text name="global.street.noname" /></span><br/>';}
if (typeof selectedStreetInformation.streetName != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.streetName"/> : '+selectedStreetInformation.streetName+'</span><br/>';}
if (typeof selectedStreetInformation.city != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.city"/> : '+selectedStreetInformation.city+'</span><br/>';}
if (typeof selectedStreetInformation.citySubdivision != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.place"/> : '+selectedStreetInformation.citySubdivision+'</span><br/>';}
if (typeof selectedStreetInformation.zipcode != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.zipcode"/> : '+selectedStreetInformation.zipcode+'</span><br/>';}
if (typeof selectedStreetInformation.state != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.state"/> : '+selectedStreetInformation.state+'</span><br/>';}
html= html +'<br/><br/>';
if (typeof selectedStreetInformation.distance != "undefined"){html= html+'<span  class="biggertext"><@s.text name="global.distance"/> : '+Math.round(selectedStreetInformation.distance*100)/100+' m</span><br/>';}
html= html+' <@s.text name="global.latitude" /> : '+selectedStreetInformation.lat+'<br/><@s.text name="global.longitude" /> : '+selectedStreetInformation.lng+'</div>';
return html;
}

doSearch = function(){
	if (checkParameters("reversegeocoding")== false){
		return false;
	}
	query = new GisgraphyQuery("reversegeocoding",function(response){
		var data = response.evalJSON(true);
		var results = data.result
		var resultsSize = results.length
		if (resultsSize == 0){
			alert('no result found');
		} else if (resultsSize == 1){
			selectedStreetInformation = results[0];
			displayMap(selectedStreetInformation.lat,selectedStreetInformation.lng,getHtmlFromSelectedStreet(selectedStreetInformation));
			//viewStreetPanorama(selectedStreetInformation.lat,selectedStreetInformation.lng);
		}
	}
);
query.execute();
return false;
}

</script>

<@gisgraphysearch.leafletMap width="700" heigth="400" 
	googleMapAPIKey=googleMapAPIKey CSSClass="center" />
<br/><br/>
<#--<@gisgraphysearch.googleStreetPanorama width="700" heigth="300" 
	googleMapAPIKey=googleMapAPIKey CSSClass="center" />-->

</body>
</html>