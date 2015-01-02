Gisgraphy-JS-API
----------------

the Gisgraphy JS API is a javascript  library that allow you to build search box (aka : input text) for geocoding and reverse geocoding with autocompletion / autosuggestions

Table of Contents
-----------------

* [Features](#features)
* [Demo](#Demo)
* [Usage](#usage)
  * [API](#api)
  * [Options](#options)
  * [Datasets](#datasets)
  * [Custom Events](#custom-events)
  * [Look and Feel](#look-and-feel)
* [Bloodhound Integration](#bloodhound-integration)

Features
--------
* UI is modeled after google.com's search box
* Displays suggestions to end-users as they type
* Internationalized
* Autocompletion / autosuggestions
* Customizable
* GPS / DMS (Degree minute second detection)
* Magic sentence (restaurant near...)
* Results are location aware (nearest are promotes)
* use HTML geolocalisation
* placeholder
* Works well with RTL languages and input method editors
* Highlights query matches within the suggestion

Demo
--------
Try this [demo](http://services.gisgraphy.com/static/leaflet/jsapi.html)

Usage
-----

### API

you need to import the dependencies and CSS. Then you can build a search box in one line

```javascript
new gisgraphyAutocomplete(options)
```
Here is a very simple Demonstration

```javascript
<html>
<head>
<title>Gisgraphy-JS-API demo</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<LINK href="style/gisgraphy-leaflet.css" rel="stylesheet" type="text/css">
</head>
<body>
<script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="js/placetype.js"></script>
<script src="js/typeahead/jquery-1.11.1.js"></script>
<script src="js/typeahead/typeahead.bundle.js"></script>
<script src="js/typeahead/bloodhound.js"></script>
<script src="js/typeahead/typeahead.jquery.js"></script>
<script src="js/typeahead/handlebars-v1.3.0.js"></script>
<script src="js/gisgraphy-autocomplete.js"></script>

<div id="gisgraphy-leaflet">
</div>

<script type="text/javascript">
var gg = new gisgraphyAutocomplete({ELEMENT_ID:"gisgraphy-leaflet"})
</script>
</body>

</html>
```

### Options



* `ELEMENT_ID` (required) - name of the HTML element to receive the search box

* `currentLanguage` -  the 2 letters language. It has impact on UI and autocompletion. default to the browser (autodetected) one.

* `allowPoiSelection` - Default to true - Whether user can choose what he search : address/place or poi (display the drop down).

* `allowMagicSentence` - Default to true - Wheter if magic sentence (Restaurant near...) should be proposed and managed.
 
* `allowLanguageSelection` - Default to true - Whether the user can change language or not (Display radio button).
 
* `fulltextURL` - Default to  '/fulltext/suggest' - The URL of the fulltext webservices.
 
* `reversegeocodingUrl` - Default to '/reversegeocoding/search'- - The URL of the reverse geocoding webservices.
 
* `geocodingUrl` - Default to '/geocoding/search' - - The URL of the fulltext webservices.
 
* `enableReverseGeocoding` - Default to  true- - Whether we should analise input text to detect GPS / DMS coordinate
 
* `limit` - Default to  20 - Number of result retrieve from the server;
 
* `onItemSelect` - Default to  logOnSelect- What method should be called when an item is selected;
 
* `result` - Javascript object that contains the informations about the result of the search;
 
* `userLat` - User current latitude - It has an impact on search results. It can be set manually or detected by 
 the allowUserPositionDetection option;

* `userLng` - User current longitude - It has an impact on search results. It can be set manually or detected by 
 allowUserPositionDetection option;

* `allowUserPositionDetection` - Default to  true - Whether we should detect user position (via HTML 5);.

* `withHelp` - Default to  true - Whether we should display help box and help link.

.... A lot of other options availables, see code for details
