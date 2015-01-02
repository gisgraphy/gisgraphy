# Free, open source, and ready to use geocoder and geolocalisation webservices

* [Webservices overview](#Webservices-overview)
 * [Geocoding](#Geocoding)
 * [Reverse geocoding ](#Reverse-geocoding)
 * [Street search](#Street-search)
 * [Find nearby](#Find-nearby)
 * [Fulltext search / Autocompletion](#Fulltext-search-/-Autocompletion)
 * [Address parser](#Address-parser)
* [Who I am](#Who-I-am?)
* [Openstreetmap data download](#Openstreetmap-data-extract-by-country)
* [Gisgraphoid](#Gisgraphoid)
* [Leaflet Plugin](#Leaflet-Plugin)
* [A little bit further...](#A-little-bit-further)

Since 2006, [Gisgraphy](http://www.gisgraphy.com) is a free, open source framework that offers the possibility to do geolocalisation and geocoding
		via Java APIs or REST webservices. Because geocoding is nothing without data, it provides an easy to use importer that
		will automatically download and import the necessary (free) data to your local database ([OpenStreetMap](http://www.openstreetmap.org), [Geonames](http://www.geonames.org/) and [Quattroshapes](http://www.quattroshapes.com) : more than 100 million
		entries). You can also add your own data with the Web interface or the importer connectors provided. Gisgraphy is
		production ready, and has been designed to be scalable(load balanced), performant and used in other languages than
		just java : results can be output in XML, JSON, PHP, Python, Ruby, YAML, GeoRSS, and Atom. One of the most popular GPS
		tracking System ([OpenGTS](http://opengts.sourceforge.net/)) also includes a Gisgraphy client...Gisgraphy
		is a framework. As a result it's flexible and powerful enough to be used in a lot of different use cases. [read more](/documentation/quick-start.htm) 
 
## Who I am ?
My name is David Masclet, I have developed Gisgraphy since 2006, My goal is to provide a realistic alternative for geocoding and geolocalisation. Feel free to contact me at davidmasclet[at]gisgraphy.com.

## Webservices overview :
-----------------

All the webservice are world wide (243 countries) and support pagination. Actually there are 6 (web)services, and all
		are availables for free at [http://services.gisgraphy.com](http://services.gisgraphy.com). It also gives
		some HTML pages that uses the webservices under the hood. Find bellow a summary for each webservices :

### Geocoding

Give an Address, structured or not, and get GPS position in 243 countries.

[Doc](/documentation/user-guide.htm#geocodingservice) | [REST API](/documentation/user-guide.htm#geocodingwebservice) | [Java API](/documentation/user-guide.htm#geocodingservicejavaapi) | [Demo](http://services.gisgraphy.com/public/geocoding.html) | [Free access](/free-access.htm)
			
###	Reverse geocoding ![]
Give a GPS position and get the corresponding address

[Doc](/documentation/user-guide.htm#streetservice) | [REST API](/documentation/user-guide.htm#streetwebservice) | [Java API](/documentation/user-guide.htm#streetservicejavaapi) | [Demo](http://services.gisgraphy.com/public/reverse_geocoding_worldwide.html) | [Free
					access](/free-access.htm)
			
### Street search
Find streets and the associated informations, for a given GPS point (and an
				optionnal name). It is very useful for tracking software like Open Gts, but a lot of other use cases.

[Doc](/documentation/user-guide.htm#streetservice) | [REST API](/documentation/user-guide.htm#streetwebservice) | [Java API](/documentation/user-guide.htm#streetservicejavaapi) | [Demo](http://services.gisgraphy.com/public/reverse_geocoding_worldwide.html) | [Free
					access](/free-access.htm)
			
### Find nearby
Find places, streets, whatever you want, around a GPS point for a given radius.
				Results can be sorted by distance.

[Doc](/documentation/user-guide.htm#geolocservice) | [REST API](/documentation/user-guide.htm#geolocwebservice) | [Java API](/documentation/user-guide.htm#geolocservicejavaapi) | [Demo](http://services.gisgraphy.com/ajaxgeolocsearch.html) | [Free access](/free-access.htm)
			
### Fulltext search / Autocompletion
For a given text and an optionnal GPS point, Find places, cities, streets, zip codes,... with informations
				(coordinates, states, population, elevation, alternate names in many languages). **Auto completion**, location
				bias, spellchecking, all words required or not...

[Doc](/documentation/user-guide.htm#fulltextservice) | [REST API](/documentation/user-guide.htm#fulltextwebservice) | [Java API](/documentation/user-guide.htm#fulltextservicejavaapi) | [Demo](http://services.gisgraphy.com/ajaxfulltextsearch.html?advancedSearch=true) | [Free access](/free-access.htm)
			
### Address parser

Divide a single address (as string) into its individual component parts : house
				number, street type (bd, street, ..), street name, unit (apt, batiment, ...), zipcode, state, country, city.

[Doc](/documentation/addressparser.htm) | [REST
					API](/documentation/addressparser.htm#webservice) | [Java API](/documentation/addressparser.htm#javaapi) | [Demo](http://services.gisgraphy.com/public/addressparser.html) | [Free
					access](/free-access.htm)
			

## Openstreetmap data Download
-----------------

Gisgraphy want to simplify the access to the Openstreetmap data. The model of the openstreetmap data is in
[XML](http://wiki.openstreetmap.org/wiki/OSM_XML)
or
[PBF](http://wiki.openstreetmap.org/wiki/PBF_Format)
and is not simple to manage. To simplify this, We have :

*   Extract all the streets(**76 millions**), cities (**4.3 millions of cities / 216 000 shapes**), Points
		of interest (**7 millions**), and house numbers(**34 Milions**) for each 240 country, and put it in a CSV/TSV
		format.
*   Split the main PBF file for each countries.
*   Extract the shape of more than 160 000 cities and localities extracted from [Quatroshapes]() with
		their associated geonames Id

All those files are
**freely available on our [download server](http://download.gisgraphy.com/).
**
. In addition to the native openstreetmap data, some pre-calculated fields (length, middle point) are added...
[Read more](http://www.gisgraphy.com/download/download_data.htm)

## Gisgraphoid
-----------------
[Gisgraphoid](/gisgraphoid.htm) is a library to do geocoding on your Android mobile or tablet with the same
		[Geocoder API](http://developer.android.com/reference/android/location/Geocoder.html) as Google but with
		Gisgraphy. Run on all Android versions, free, no Google API needed by the phone, no API key, no limit. You can display
		the result on Openstreetmap or Google maps (Google API key required)

[Learn More](/gisgraphoid.htm) | [Download library](/gisgraphoid.htm#download) | [Download Demo app](/gisgraphoid.htm#demo)

##Leaflet Plugin
You can also check a [demo](http://services.gisgraphy.com/static/leaflet/index.html) with the two plugins

**Geocoding** : It allows to
                add a geocoding to add a geocoding input with autocompletion on a leaflet map. It wraps the [Gisgraphy js API.](/leaflet.htm#jsapi)

[Learn More](/leaflet.htm) | [View demo](http://services.gisgraphy.com/static/leaflet/gisgraphy-geocoder-leaflet-demo.html) | [Documentation](#)

**Reverse geocoding** :
                It reverse geocode when you rightclick on the map. it is a sample and you can personalize it.

[Learn More](/leaflet.htm) | [View demo](http://services.gisgraphy.com/static/leaflet/gisgraphy-reverse-geocoder-leaflet-demo.html) | [Documentation](#)
             



## A little bit further...
-----------------
*   Importers from geonames CSV files. Just give the country(ies) you wish to import and / or the [placetypes](/placetype.htm), and Gisgraphy download the files and import them with all the alternate names
		(optional) and sync the database with a fulltext search engine
*   All Openstreetmap data processed in csv format (view [ data](http://www.gisgraphy.com/download/download_data.htm))
	<li>Importers for Openstreetmap data in csv (view [
			data](http://www.gisgraphy.com/download/download_data.htm))
	<li>Importers for Quattroshapes data in csv (view [
			data](http://www.gisgraphy.com/download/download_data.htm))
	<li>[Leaflet plugins](/leaflet.htm)
*   WorldWide geocoding / worldWide reverse geocoding / street search WebServices;
	<li>REST WebService
*   Several output formats supported : XML, json, PHP, ruby, python, Atom, RSS / GeoRSS
*   Full text search (based on [Lucene](http://lucene.apache.org/java/) / [Solr](http://lucene.apache.org/solr/) with default filters optimized for city search (case insensitivity,
		separator characters stripping, ..) via an Java API or a webservice
*   Findnearby function (with limits, pagination, restrict to a specific country and/or language and other useful
		options) via a Java API or a Web Service
*   An admin / back office whith statistics [ interface](http://www.gisgraphy.com/screenshots.htm)
*   Fully replicated / scalable / high performances / cached services
*   Search for zipcode name, IATA, ICAO
*   Internationalized (with support of cyrillique, arabic, chinese,... alphabet)
*   Dojo widgets / prototype / Ajax to ease search but can be use it even if javascript is not enabled on the
		client side
*   [Opensearch module](http://www.gisgraphy.com/documentation/addons.htm#opensearch)
*   Plateform / language independent
*   Provides all the countries flags in svg and png format
*   ...