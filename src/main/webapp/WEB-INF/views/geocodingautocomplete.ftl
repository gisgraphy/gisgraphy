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
<noscript>
	<div class="tip yellowtip">
		<@s.text name="global.noscript.required"/>
	</div>
	<br/>
</noscript>
		 <@breadcrumbs.searchNavBar/>
<div class="clear"></div><div style="line-height:1.5em;">
 <@s.text name="search.geocoding.desc"/>.
 <@s.text name="search.openstreetmap.data"><@s.param>http://openstreetmap.org</@s.param></@s.text>.
 <@s.text name="search.geocoding.notUsinggooglemap"/>.
 <@s.text name="search.docandinstall">
 	<@s.param>http://www.gisgraphy.com/documentation/user-guide.htm#geocodingservice</@s.param>
 	<@s.param>http://www.gisgraphy.com/documentation/installation/index.htm</@s.param>
 </@s.text>.
</div><br/><br/>
<div class="clear"></div>


		<script type="text/javascript">
		// Extension to Ajax allowing for classes of requests of which only one (the latest) is ever active at a time
		// - stops queues of now-redundant requests building up / allows you to supercede one request with another easily.

		// just pass in onlyLatestOfClass: 'classname' in the options of the request

		Ajax.currentRequests = {};

		Ajax.Responders.register({
			onCreate: function(request) {
				if (request.options.onlyLatestOfClass && Ajax.currentRequests[request.options.onlyLatestOfClass]) {
					// if a request of this class is already in progress, attempt to abort it before launching this new request
					try { Ajax.currentRequests[request.options.onlyLatestOfClass].transport.abort(); } catch(e) {}
				}
				// keep note of this request object so we can cancel it if superceded
				Ajax.currentRequests[request.options.onlyLatestOfClass] = request;
			},
			onComplete: function(request) {
				if (request.options.onlyLatestOfClass) {
					// remove the request from our cache once completed so it can be garbage collected
				Ajax.currentRequests[request.options.onlyLatestOfClass] = null;
			}
			}
		});
		
			<@s.url id="streetsearchurl" action="streetSearch" includeParams="none" namespace="/public" ></@s.url>
			streetSearchBaseUrl = "${streetsearchurl}";
			latlngArray = eval('${latLongJson}');
			updateLatLng = function(){
				if ($('ambiguouscity') != null){
					var indexDropDown = ($('ambiguouscity').selectedIndex);
						if (indexDropDown == 0){
							$('lat').value='',
							$('lng').value='';
						} else {
							$('lat').value = latlngArray[indexDropDown-1].lat;
							$('lng').value = latlngArray[indexDropDown-1].lng;
						}
				}
					setStreetNameCorrectState();
                        }

			setStreetNameCorrectState = function(){
				if ($('lat').value != '' && $('lng').value != '' ){
					streetNameAutocompleter.serviceUrl="/street/streetsearch?format=json&lat="+$('lat').value+"&lng="+$('lng').value+"&mode=contains&distance=false";
					$('streetSearchLnk').href=streetSearchBaseUrl+"?lat="+$('lat').value+"&lng="+$('lng').value+"&autosubmit=true";
					$('viewAllStreetLink').show();
					$('streetname').enable();

				} else {
					$('viewAllStreetLink').hide();
					$('streetname').disable();

				}
			}

	Event.observe(window, "load", function(){
			updateLatLng();
			setStreetNameCorrectState();
			});
			</script>
	<div class="center">
	<@s.form action="#cityselector" id="streetsearch">
<br/>
<#if errorMessage!= ''><div class="error">${errorMessage}</div><br/><br/></#if>
<#if message!= ''><span class="biggertext">${message}</span><br/></#if>
<div class="forminstructions"><img src="/images/puce_1.gif" class="imagenumberlist" alt="puce_1"/><@s.text name="search.select.country"/> : </div>
         <span class="searchfield">
			<@s.select label="country " listKey="iso3166Alpha2Code" listValue="name" name="countryCode" list="countries" headerValue="-- %{getText('search.select.country')} --" headerKey="" multiple="false" required="true" labelposition="top" theme="simple" id="country"/> 
	<br/><br/>
	</span>
	<div>
<br/><br/>

	<@gisgraphysearch.citySelector  onCityFound="updateLatLng" />
	</div>
		<div class="clear"></div>
		<br/>
		</span>
                <div class="clear"></div>


<div class="forminstructions"><img src="/images/puce_3.gif" class="imagenumberlist" alt="puce_3"/><@s.text name="search.street.search"/>&nbsp;(<@s.text name="global.autocomplete"/>) : </div>
		<@gisgraphysearch.streetNameAutoCompleter javascriptNameObject="streetNameAutocompleter"/>
<div id="viewAllStreetLink" class="forminstructions indented ">>> <a href="${streetsearchurl}" id="streetSearchLnk" ><@s.text name="search.displaycity.streets"/></a></div>
		
<br/>
</div>
</@s.form>
<div>
<div class="clear"></div>
<br/><br/>
<div class="clear"></div>
<@gisgraphysearch.leafletMap width="700" heigth="400" 
	googleMapAPIKey=googleMapAPIKey CSSClass="center" />
<br/><br/>
<#--<@gisgraphysearch.googleStreetPanorama width="700" heigth="300" 
	googleMapAPIKey=googleMapAPIKey CSSClass="center" />-->

</div>

</body>
</html>