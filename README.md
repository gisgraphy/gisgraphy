# Free, open source, and ready to use geocoder, reverse geocoder and geolocalisation webservices
* [Demo](#demo)
* [About us](#about-us)
* [Install it locally](##install-it-locally)
* [Webservices overview](#webservices-overview-)
* [CSV or SQL database](#Addresses-/-pois-/-street-database-in-CSV )
* [Gisgraphoid](#gisgraphoid)
* [Leaflet Plugins](#leaflet-plugins)
* [A little bit further...](#a-little-bit-further)
* [License and attributions...](#license)


Since 2006, [Gisgraphy](https://www.gisgraphy.com) is a free, open source framework that offers the possibility to do geolocalisation and geocoding
		via Java APIs or REST webservices. Because geocoding is nothing without data, it provides an easy to use importer that
		will automatically download and import the necessary (free) data to your local database ([OpenStreetMap](http://www.openstreetmap.org), [Openaddresses](https://openaddresses.io/), [Geonames](http://www.geonames.org/) and [Quattroshapes](http://www.quattroshapes.com) : more than 100 million
		entries). You can also add your own data with the Web interface or the importer connectors provided. Gisgraphy is
		production ready, and has been designed to be scalable(load balanced), performant and used in other languages than
		just java : results can be output in XML, JSON, PHP, Python, Ruby, YAML, GeoRSS, and Atom. One of the most popular GPS
		tracking System ([OpenGTS](http://opengts.sourceforge.net/)) also includes a Gisgraphy client...Gisgraphy
		is a framework. As a result it's flexible and powerful enough to be used in a lot of different use cases. [read more](https://www.gisgraphy.com/documentation/quick-start.php) 

## Demo ##
[demo on map (leaflet plugin)](http://services.gisgraphy.com)

[play with webservices] (http://services.gisgraphy.com/public/geocoding.html)

[gisgraphy web site](https://www.gisgraphy.com)

## About us ##
Since 2006, Gisgraphy's goal is to provide a realistic alternative for geocoding and geolocalisation. Feel free to contact us at contact[at]gisgraphy.com.

## Install it locally ##
We got Docker images and installation scripts. read the [installation guide](http://www2.gisgraphy.com/documentation/installation/index.php) to set up Gisgraphy on you own server.

## Webservices overview :##
-----------------

All the webservice are world wide (243 countries) and support pagination. Actually there are 6 (web)services, and all
		are availables for free at http://services.gisgraphy.com (c). It also gives
		some HTML pages that uses the webservices under the hood. Find bellow a summary for each webservices :

### Geocoding ###

Provide an address, structured or not, and get it's GPS position. Worldwide coverage, including house numbers of more than +410 million addresses.

[Doc](https://www.gisgraphy.com/documentation/user-guide.php#geocodingservice) | [Demo](http://services.gisgraphy.com/public/geocoding.html) | [Free access](https://www.gisgraphy.com/free-access.php)
			
###	Reverse geocoding ###
Provide a GPS position and get the corresponding address. Worldwide coverage, including house numbers of more than +410 millions of addresses.

[Doc](https://www.gisgraphy.com/documentation/user-guide.php#streetservice) | [Demo](http://services.gisgraphy.com/public/reverse_geocoding_worldwide.html) | [Free
					access](https://www.gisgraphy.com/free-access.php)
			
### Street search ###
Find streets for a given GPS point (and an optional name). Speed-limit, number of lanes, toll or not, surface type, azimuth, etc. For GTS tracking software (e.g : Open GTS or Traccar), but lot of other use cases.

[Doc](https://www.gisgraphy.com/documentation/user-guide.php#streetservice) | [Demo](https://services.gisgraphy.com/public/streetSearch.html) | [Free
					access](https://www.gisgraphy.com/free-access.php)
			
### Nearby ###
Find places, POIs, cities, streets around a GPS point for a given radius. Results include and can also be sorted by the distance from the given point. 230+ POIs type availables (e.g : restaurant, station, ATM, PostOffice, Doctor, Parking, tourist information office). Very powerfull when coupled with the reverse geocoding service to find place around an address.

[Doc](https://www.gisgraphy.com/documentation/user-guide.php#geolocservice) | [Demo](http://services.gisgraphy.com/ajaxgeolocsearch.html) | [Free access](https://www.gisgraphy.com/free-access.php)
			
### Fulltext search / Autocompletion ###
For a given text and an optional GPS point, find places, POIs, cities, streets, zip codes,...with its information (coordinates, states, population, elevation, alternate names in many languages and alphabet). Many options availables : Auto completion (as you type), location bias, fuzzy, spell checking, all words required or not, place type filtering,...

[Doc](https://www.gisgraphy.com/documentation/user-guide.php#fulltextservice) | [Demo](http://services.gisgraphy.com/) | [Free access](https://www.gisgraphy.com/free-access.php)
			
### Address parser ###

Divide a single address (as a string) into its individual component parts : house number, street type (directional, street, ..), street name, unit (apt, building, ...), zip-code, state, country, city. Manage PO boxes and address format in 60+ countries. This software is not open source and can be used on-line as a web-service or a license can be purchased for an unlimited off-line use

[Doc](https://address-parser.net) |  [Demo](https://address-parser.net/try.php) | [Free
					access](https://www.gisgraphy.com/free-access.php)
			

## Addresses / pois / street database in CSV ##
-----------------

We have built a unique and worldwide address, POIs (point of interest), streets, cities, and administrative divisions databases of 500+ millions entries.

Available country per country and in CSV or SQL format to simplify things. We use open data from best open data sources : Openstreetmap, Openaddresses, Geonames, Quattroshapes.

You can order our address database (or street, cities and administrative division ones) in CSV/TSV format.[Read more](https://www.gisgraphy.com/data/index.php)


## Gisgraphoid ###
-----------------
[Gisgraphoid](https://www.gisgraphy.com/gisgraphoid.htm) is a library to do geocoding on your Android mobile or tablet with the same
		[Geocoder API](http://developer.android.com/reference/android/location/Geocoder.html) as Google but with
		Gisgraphy. Run on all Android versions, free, no Google API needed by the phone, no API key, no limit. You can display
		the result on Openstreetmap or Google maps (Google API key required)

[Learn More](https://www.gisgraphy.com/gisgraphoid.htm) | [Download library](https://www.gisgraphy.com/gisgraphoid.htm#download) | [Download Demo app](https://www.gisgraphy.com/gisgraphoid.htm#demo)

## Leaflet Plugins  ##
[Demo](http://services.gisgraphy.com/static/leaflet/index.html) with the two plugins on a map.

**Geocoding** : It allows to add a geocoding to add a geocoding input with autocompletion on a leaflet map. It wraps the [Gisgraphy js API.](https://www.gisgraphy.com/documentation/leaflet.php#jsapi)

[Learn More](https://www.gisgraphy.com/documentation/leaflet.php) | [View demo](http://services.gisgraphy.com/static/leaflet/gisgraphy-geocoder-leaflet-demo.html) | [Documentation](https://github.com/gisgraphy/gisgraphy-leaflet-plugin/blob/master/leaflet/doc.md)

**Reverse geocoding** :
                It reverse geocode when you rightclick on the map. it is a sample and you can personalize it.

[Learn More](https://www.gisgraphy.com/documentation/leaflet.php) | [View demo](http://services.gisgraphy.com/static/leaflet/gisgraphy-reverse-geocoder-leaflet-demo.html) | [Documentation](https://github.com/gisgraphy/gisgraphy-leaflet-plugin/blob/master/leaflet/doc.md)
             

## A little bit further ###
-----------------
*   Importers from geonames / openstreetmap, openaddresses, quattroshapes files. Just give the country(ies) you wish to import and / or the placetypes, and Gisgraphy download the files and import them with all the alternate names
		(optional) and sync the database with a fulltext search engine
	[Leaflet plugins](https://www.gisgraphy.com/documentation/leaflet.php)
*   WorldWide geocoding / worldWide reverse geocoding / street search WebServices;
	<li>REST WebService
*   Several output formats supported : XML, json, PHP, ruby, python, Atom, RSS / GeoRSS
*   Full text search (based on [Lucene](http://lucene.apache.org/java/) / [Solr](http://lucene.apache.org/solr/) with default filters optimized for city search (case insensitivity,
		separator characters stripping, ..) via an Java API or a webservice
*   Findnearby function (with limits, pagination, restrict to a specific country and/or language and other useful
		options) via a Java API or a Web Service
*   An admin / back office whith statistics interface
*   Fully replicated / scalable / high performances / cached services
*   Search for zipcode name, IATA, ICAO
*   Internationalized (with support of cyrillique, arabic, chinese,... alphabet)
*   Dojo widgets / prototype / Ajax to ease search but can be use it even if javascript is not enabled on the
		client side
*   Opensearch module
*   Plateform / language independent
*   Provides all the countries flags in svg and png format
*   ...

## License and attributions ###
-----------------
*   Gisgraphy is under [LGPL license V3](https://github.com/gisgraphy/gisgraphy/blob/master/LICENSE.txt).
*   See [Attribution page](https://www.gisgraphy.com/attributions.html)Attribution page for data atributions and license.
