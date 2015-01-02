autocompleteGisgraphyCounter = 0;
autocompleteGisgraphy = [];


currentRequests = {};

defaultAjax = {
    beforeSend: function(request) {
        if (currentRequests["geocoding"]) {
            try {
                currentRequests["geocoding"].abort();
                console.log('aborting');
            } catch (e) {
                console.log(e);
            }
        }

        currentRequests["geocoding"] = request;

    },
    complete: function(xhr) {
            currentRequests["geocoding"] = null;

        }
        // ......
};

function detectLanguage() {
    var lang = (navigator.language) ? navigator.language : navigator.userLanguage;
    if (lang) {
        lang = lang.split('-')[0]
    }
    return lang ? lang.toUpperCase() : "EN";
}



$(document).ajaxStart(function() {
    console.log("Triggered ajaxStart handler.");
    $('#gisgraphy-leaflet-searchButton').css("background-image", "url(img/loading.gif)");
    $('#gisgraphy-leaflet-searchButton').addClass("searching");
});

$(document).ajaxStop(function() {
    console.log("Triggered ajaxStop handler.");
    $('#gisgraphy-leaflet-searchButton').css("background-image", 'url("img/search.png")');
    $('#gisgraphy-leaflet-searchButton').removeClass("searching");
});


function setSearchText(htmlId, text) {
    $('#' + htmlId).val(text);
}

var marker = undefined;
var markerGeoloc = undefined;

function moveCenterOfMapTo(lat, lng, placetype) {
    if (typeof map != 'undefined') {
        if (lat != undefined && lng != 'undefined') {
            map.panTo(new L.LatLng(lat, lng));
            if (typeof marker != 'undefined') {
                map.removeLayer(marker)
            }
            marker = L.marker([lat, lng]).addTo(map);
        }
        if (typeof placetype != 'undefined') {
            map.setZoom(getZoomByPlaceType(placetype));
        }
    }
}

function getZoomByPlaceType(placetype) {
    var zoom = 14; //for city and other
    console.log(placetype);
    if (typeof placetype != 'undefined') {
        if (placetype.toUpperCase() == 'STREET') {
            zoom = 18;
        } else if (placetype.toUpperCase() == 'CITY') {
            zoom = 12;
        } else if (placetype.toUpperCase() == 'ADM') {
            zoom = 9;
        }
    }
    return zoom;
}

DEFAULT_LANGUAGE = detectLanguage();



(function(root) {
    "use strict";
    if (typeof console == "undefined") {
        this.console = {
            log: function(msg) { /* do nothing since it would otherwise break IE */ }
        };
    }
    var VERSION = "1.0.0";
    var old;
    old = root.gisgraphyAutocomplete;
    root.gisgraphyAutocomplete = gisgraphyAutocomplete;

    function gisgraphyAutocomplete(o) {

        if (!o) {
            $.error("usage : gisgraphyAutocomplete({ELEMENT_ID:'foo'})");
        }
        if (!o || !o.ELEMENT_ID) {
            $.error("please specify an ELEMENT_ID option");
        }

        //user options
        this.geocoding;
	this.instanceCounter = 0;
        this.autocompleteGisgraphyCounter = autocompleteGisgraphyCounter + '';
        this.ELEMENT_ID = o.ELEMENT_ID;
        this.currentLanguage = (o.currentLanguage || DEFAULT_LANGUAGE).toUpperCase();
        this.allowPoiSelection = o.allowPoiSelection || true;
        this.allowMagicSentence = o.allowMagicSentence || true;
        this.allowLanguageSelection = o.allowLanguageSelection || true;
        this.fulltextURL = o.fulltextURL || '/fulltext/suggest';
        this.reversegeocodingUrl = o.reversegeocodingUrl || '/reversegeocoding/search';
        this.geocodingUrl = o.geocodingUrl || '/geocoding/search';
        this.geolocUrl = o.geolocUrl || '/geoloc/search'
        this.enableReverseGeocoding = o.enableReverseGeocoding || true; //todo if enable check reversegeocodingUrl is defined
        this.limit = o.limit || 20;
        this.apiKey = o.apiKey || undefined;
        this.formNodeID = o.formNodeID || this.ELEMENT_ID + '-form';
        this.toolsNodeID = o.toolsNodeID || this.ELEMENT_ID + '-tools';
        this.placetypeNodeID = o.placetypeNodeID || this.ELEMENT_ID + '-placetypes';
        this.languagesNodeID = o.languagesNodeID || this.ELEMENT_ID + '-languages';
        this.inputSearchNodeID = o.inputSearchNodeID || this.ELEMENT_ID + '-inputSearch';
        this.searchButtonNodeID = o.searchButtonNodeID || this.ELEMENT_ID + '-searchButton';
        this.resultBoxNodeID = o.resultBoxNodeID || this.ELEMENT_ID + '-resultBox';

        this._formNode = undefined;
        this._toolsNode = undefined;
        this._placetypeNode = undefined;
        this._languagesNode = undefined;
        this._inputSearchNode = undefined;
        this._searchButtonNode = undefined;
        this._resultBoxNode = undefined;

        this.buildSearchBox = buildSearchBox;
        this.buildPlaceTypeDropBox = buildPlaceTypeDropBox;
        this.BuildLanguageSelector = BuildLanguageSelector;
        this.buildPoisArray = buildPoisArray;
        this.initUI = initUI;
        this.initAutoCompletion = initAutoCompletion;
        this.pois = buildPoisArray(DEFAULT_LANGUAGE);
        this.getLocalSuggestionsArray = getLocalSuggestionsArray;
        this.changeLanguage = changeLanguage;
        this.replace = $.proxy(replace, this);
        this.doGeocoding = o.doGeocoding || doGeocoding;
	this.findAround = o.findAround || findAround;
        this.doProcessGeocodingResults = o.doProcessGeocodingResults || $.proxy(doProcessGeocodingResults, this);
	this.doProcessGeolocResults = o.doProcessGeolocResults || $.proxy(doProcessGeolocResults, this);
        //used to know what to do when enter is press whether an itme is selected or not
        this.itemSelected = false;
        this.result = undefined;
        this._detectPosition = o.detectPosition || detectPosition
        this._fillPosition = $.proxy(fillPosition, this);
        this.userLat = undefined;//50.455; //undefined;
        this.userLng = undefined;//3.204; //undefined;
        this.allowUserPositionDetection = o.allowUserPositionDetection || true;
	this.locationBias = o.locationBias || false;
        this.withHelp = o.withHelp || true;
        this.displayHelp = displayHelp;
        this.initUI();

        this.geocoding = new Bloodhound({
            datumTokenizer: Bloodhound.tokenizers.obj.nonword('name'),
            queryTokenizer: Bloodhound.tokenizers.nonword,
            limit: this.limit,
            local: this.getLocalSuggestionsArray(DEFAULT_LANGUAGE),
            remote: {
                url: this.fulltextURL + '?suggest=true&allwordsrequired=false&q=%QUERY',
                replace: this.replace,
                ajax: defaultAjax,
                filter: function(d, e) {
                    var names = [];
		    var docMap ={};	
		    var seen = {};
                    if (d && d.response && d.response['docs']) {
                        $.each(d.response['docs'], function(key, value) {
                            if (value.name) {
				var keyMap = value.feature_id;
				 if (value.is_in && value.is_in.length >=0){
					var keyMap = value.name+value.is_in;
			    	  }
				if(!docMap.hasOwnProperty(keyMap)) {
                                    docMap[keyMap] = value;
				} else {
				   if (value.house_numbers && docMap[keyMap]){
					if (!docMap[keyMap].house_numbers || docMap[keyMap].house_numbers.length ==0){
						docMap[keyMap].house_numbers=[];
					}
					docMap[keyMap].house_numbers= docMap[keyMap].house_numbers.concat(value.house_numbers);
				   }
				}	
                            }
                        });
			 $.each(d.response['docs'], function(key, value) {
				var found = false;
				 if (value.feature_id) {
					$.each(docMap, function(keyMap, valueMap) {
						if (found == true){
							return
						}
						if (valueMap.feature_id == value.feature_id){
							names.push(valueMap);
							found=true;

						}
					});
			    	 }
			});
			/* $.each(docMap, function(key, value) {
				names.push(value)
			});*/
                    } else if (d && d.result && d.result[0]) {
                        names.push(convertAddressToDatum(d.result[0]));
                    }
                    return names;

                },
                rateLimitWait: 130
            }
        });


        if (this.allowUserPositionDetection) {
            this._detectPosition();
        }

        function fillPosition(position) {
            if (position) {
                this.userLat = position.coords.latitude;
                this.userLng = position.coords.longitude;
                if (typeof map != 'undefined') {
		    // map.panTo(new L.LatLng(this.userLat, this.userLng));
		    // map.setZoom(18);
		     moveCenterOfMapTo(this.userLat,this.userLng,'STREET');
                }
            }
        }

        function detectPosition() {
            try {
                navigator.geolocation.getCurrentPosition(this._fillPosition);
            } catch (e) {
                console.log("can not detect position");
                console.log(e);
            }
        }
        gisgraphyAutocomplete.normalize = function normalize(input) {
            $.each(charMap, function(unnormalizedChar, normalizedChar) {
		var normalizeRegex = new RegExp(unnormalizedChar, 'gi');
                input = input.replace(normalizeRegex, normalizedChar);
            });
            return input.replace(/\W+/, '');
        }
        Handlebars.registerHelper('if_eq', function(a, b, opts) {
            if (a == b)
                return opts.fn(this);
            else
                return opts.inverse(this);
        });
	Handlebars.registerHelper('if_number_after', function(a, opts) {
            if ($.inArray(a, NAME_HOUSE_COUNTRYCODE) >=0 )
                return opts.fn(this);
            else
                return opts.inverse(this);
        });
	Handlebars.registerHelper('housenumber', function(house_numbers, autocompleteGisgraphyNumber) {
	var found = false;
	var idElement = autocompleteGisgraphyNumber;
	var number = extractHouseNumber($('#'+idElement).val());
  	if (house_numbers && number.length >= 0){
	 $.each(house_numbers, function(key, value) {
	      var hnArray = value.split(':');
	      if (number == hnArray[0] && !found){	      
			console.log('found house number :'+hnArray[0]+' is at '+hnArray[1]);
			found = true;
		}
	});
 	 return found == true ? number:"";
	}
	});


        Handlebars.registerHelper('l10n', $.proxy(function(keyword) {
            var target = translation[keyword][this.currentLanguage];
            // fallback to the original string if nothing found
            target = target || keyword;
            //output
            return target;
        }, this));

        function buildSearchBox() {
            var box = $('<input>').attr('type', 'text').attr('placeholder', translation['placeholder'][DEFAULT_LANGUAGE]).attr('id', this.inputSearchNodeID).attr('name', 'q').attr('autocomplete', 'off').addClass('typeahead clearable searchbox').appendTo(this._formNode);
            var searchbutton = $('<input>').attr('type', 'button').attr('value', '').addClass('searchbutton').attr('onclick', 'autocompleteGisgraphy[' + autocompleteGisgraphyCounter + '].doGeocoding();').attr('id', this.searchButtonNodeID).appendTo(this._formNode);
            this._resultBoxNode = $('<div>').attr('id', this.resultBoxNodeID).addClass('resultBox').appendTo(this._formNode);
            if (this.withHelp == true) {
                this.displayHelp();
            }
        };

        function displayHelp() {
            var el = this._resultBoxNode;
            if ($('#' + this.resultBoxNodeID).length > 0) {
                el = $('#' + this.resultBoxNodeID);
            }
            el.html('<strong>Welcome to Gisgraphy !</strong><span class="closable" onclick="$(\'#' + this.resultBoxNodeID + '\').empty().hide();" >&nbsp;</span><br/>Since 2006, Gisgraphy is a free open source framework that provides 6 webservices (geocoding, reverse geocoding, find nearby, street search, fulltext / autocompletion / autosuggestion, address parsing).<ul><li> Up to house number, worldwide, internationalized</li><li> IT DOES ALL BY ITSELF, LOCALLY, no link to Google, yahoo, etc</li><li> It use free data (OpenstreetMap, Geonames, Quattroshapes,...) in its own database. </li><li>UI is modeled after google.com\'s search box</li></ul><br/>This leaflet plugin is kind of show case that use those webservices. try : <ul><li>A place : <a href="javascript:setSearchText(\'' + this.inputSearchNodeID + '\',\'paris\')">paris</a>, <a href="javascript:setSearchText(\'' + this.inputSearchNodeID + '\',\'big Apple\')">big apple</a></li><li>An address : <a href="javascript:setSearchText(\'' + this.inputSearchNodeID + '\',\'Avenue des Champs-Élysées Paris\')">Avenue des Champs-Élysées Paris</a></li><li>A GPS : <a href="javascript:setSearchText(\'' + this.inputSearchNodeID + '\',\'48.873409271240234,2.29619002342224\')">48.873409271240234,2.29619002342224</a></li><li>A DMS : <a href="javascript:setSearchText(\'' + this.inputSearchNodeID + '\',\'40:26:46.302N 079:56:55.903W\')">40:26:46.302N 079:56:55.903W</a></li><li>A magic phrase : <a href="javascript:setSearchText(\'' + this.inputSearchNodeID + '\',\'restaurant new york\')">restaurant new york</a></li></ul><a href="http://www.gisgraphy.com/"><span style="display:block;background-color:#2D81BE;height:25px;width:150px;margin:auto auto;text-align:center;color:#FFFFFF;font-size:1.2em;line_height:1.2em;vertical-align:middle;border-radius: 8px 8px 8px 8px;" ><b>Gisgraphy project &rarr;</b></span></a>');
            if ($('#' + this.resultBoxNodeID).length > 0) {
                if ($('#' + this.resultBoxNodeID).is(":hidden")) {
                    $('#' + this.resultBoxNodeID).slideDown(200);
                } else {
                    $('#' + this.resultBoxNodeID).hide();
                }

                $('#' + this.resultBoxNodeID).slideDown(200);
            }
        }

        function replace() {
            if (this.enableReverseGeocoding) {
                var latLong = convertToLatLong($('#' + this.inputSearchNodeID).val());
                if (latLong) {
                    return this.reversegeocodingUrl + "?format=json&lat=" + latLong.lat + "&lng=" + latLong.long;
                }
            }
            var fulltextUrlWithParam = this.fulltextURL + '?format=json&suggest=true&allwordsrequired=false&style=long'
            if (this.userLat && this.userLng & this.locationBias) {
                fulltextUrlWithParam = fulltextUrlWithParam + "&lat=" + this.userLat + "&lng=" + this.userLng + "&radius=0";
            }
            if (!$('#' + this.placetypeNodeID).val()) {
                fulltextUrlWithParam = fulltextUrlWithParam + '&placetype=city&placetype=adm&placetype=street';
            }
            fulltextUrlWithParam = fulltextUrlWithParam + "&from=1&to=20";
	    fulltextUrlWithParam = fulltextUrlWithParam +"&q=" + replaceHouseNumber($('#' + this.inputSearchNodeID).val());
            if (this.apiKey != undefined) {
                fulltextUrlWithParam = fulltextUrlWithParam + '&apikey=' + this.apiKey;
            }

            return fulltextUrlWithParam;
        };

        function doGeocoding() {
            if (!$('#' + this.inputSearchNodeID).val()) {
                alert(translation['placeholder'][this.currentLanguage]);
                return;
            }
            var url = this.geocodingUrl + '?format=json&address=' + $('#' + this.inputSearchNodeID).val();
            if (this.enableReverseGeocoding) {
                var latLong = convertToLatLong($('#' + this.inputSearchNodeID).val());
                if (latLong) {
                    url = this.reversegeocodingUrl + "?format=json&lat=" + latLong.lat + "&lng=" + latLong.long;
                    if (this.apiKey != undefined) {
                        url = url + '&apikey=' + this.apikey
                    }
                }
            }
            if (this.apiKey != undefined) {
                url = url + '&apikey=' + this.apikey;
            }
            $.ajax({
                    url: url,
                })
                .done($.proxy(doProcessGeocodingResults, this));
        };

        function doProcessGeocodingResults(data) {
            if (console && console.log) {
                //	      console.log(data.result );
            }

            $('#' + this.resultBoxNodeID).empty();
		var numResult=1;
            if (data) {
                $('<div>').html('<strong></strong><span class="closable" onclick="$(\'#' + this.resultBoxNodeID + '\').empty().hide();" >&nbsp;</span><br/>').appendTo('#' + this.resultBoxNodeID);
                if (data.numFound && data.numFound > 0) {
                    $.each(data.result,
                        $.proxy(function(index, value) {
                            //console.log(value);
                            var content = '';
                            var hasName = false;
                            if (value.countryCode) {
                                content += '<img src="img/' + value.countryCode + '.png" alt="' + value.countryCode + '" class="flag-autocomplete"/>';
                            }
			if (value && value.countryCode && value.countryCode.length == 2 && $.inArray(value.countryCode, NAME_HOUSE_COUNTRYCODE) >=0){
			   if (value.streetName) {
                                hasName = true;
                                content += "<strong>" + value.streetName + "</strong>";
                            } else if (value.name) {
                                hasName = true;
         		       var zip='';
                               if (value.zipCode && value.placetype && value.placetype=='City'){
                                        zip=' ('+value.zipCode+')';
                               }

                                content += "<strong>" + value.name + zip + "</strong>";
                            }
			    if (value.houseNumber) {
                                content += " "+value.houseNumber;
                            }
                            if (value.city) {
                                if (hasName == true && value.dependentLocality) {
                                    content += ', ';
                                }
				 if (value.dependentLocality){
                                        content+='<span class="isin-autocomplete">' + value.dependentLocality + '</span>';
                               }
			       var zip =' ';
                               if (value.zipCode){
                                        zip+=value.zipCode+' ';
                               }
                                content += '<span class="isin-autocomplete">,'+zip + value.city + '</span>';
                            }
				
                            
			} else {
				if (value.houseNumber) {
                                content += value.houseNumber + " ";
                            }
                            if (value.streetName) {
                                hasName = true;
                                content += "<strong>" + value.streetName + "</strong>";
                            } else if (value.name) {
                                hasName = true;
				var zip='';
                               if (value.zipCode && value.placetype && value.placetype=='City'){
                                        zip=' ('+value.zipCode+')';
                               }

                                content += "<strong>" + value.name + zip +"</strong>";
                            }
                            if (value.city) {
                                if (hasName == true && value.dependentLocality) {
                                    content += ', ';
                                }
			       if (value.dependentLocality){
                                        content+='<span class="isin-autocomplete">' + value.dependentLocality + '</span>';
                               }
			       var zip=' ';
			       if (value.zipCode){
                                        zip+=value.zipCode+' ';
                               }
                                content += '<span class="isin-autocomplete">,'+zip + value.city + '</span>';
                            }
			}
                            if (value.lat && value.lng) {
                                content += "<br/>(" + value.lat + "," + value.lng + ")";
                            }
                            $('<div onclick="moveCenterOfMapTo(' + value.lat + ',' + value.lng + ',\'' + value.placetype + '\')">').html(content).appendTo('#' + this.resultBoxNodeID);
                           
			//TODO : add search around
			//if (data.numFound ==1){
				$('<div>').attr('id', this.ELEMENT_ID+"result"+numResult).html('<a href="#" onclick="$(\'#'+this.ELEMENT_ID+"resultSearchAroundform"+numResult+'\').css(\'display\',\'inline\')">'+translation['searcharound'][this.currentLanguage]+'</a>'+' | <a href="#" onclick="alert(\'coming soon, we will soon provide some facilities with open sources route planner\')">'+translation['routeto'][this.currentLanguage]+'</a>').appendTo('#' + this.resultBoxNodeID);
				$('<form>').attr('id', this.ELEMENT_ID+"resultSearchAroundform"+numResult).attr('action', this.geolocUrl).appendTo('#' + this.resultBoxNodeID).css("display","none");
				$('<input>').attr('id', this.ELEMENT_ID+"resultSearchAroundlat"+numResult).attr('type', "hidden").attr("name","lat").attr("value",value.lat).appendTo('#' + this.ELEMENT_ID+"resultSearchAroundform"+numResult);
				$('<input>').attr('id', this.ELEMENT_ID+"resultSearchAroundlng"+numResult).attr('type', "hidden").attr("name","lng").attr("value",value.lng).appendTo('#' + this.ELEMENT_ID+"resultSearchAroundform"+numResult);
				$('<span>').attr('id', this.ELEMENT_ID+"resultSearchAroundPlacetype"+numResult).appendTo("#"+this.ELEMENT_ID+"resultSearchAroundform"+numResult);
			        this.buildPlaceTypeDropBox(DEFAULT_LANGUAGE,this.ELEMENT_ID+"resultSearchAroundPlacetype"+numResult);
				 $('#' + this.ELEMENT_ID+"resultSearchAroundPlacetype"+numResult + ' option[value="Restaurant"]').prop('selected', true);
				$("#"+this.ELEMENT_ID+"resultSearchAroundPlacetype"+numResult);
				$('<input>').attr('id', this.ELEMENT_ID+"resultSearchAroundbtn"+numResult).attr('type', "button").attr("value",translation['search'][this.currentLanguage]).attr("onclick","autocompleteGisgraphy[" + this.instanceCounter + "].findAround(\'"+this.ELEMENT_ID+"resultSearchAroundform"+numResult+"\')").appendTo('#' + this.ELEMENT_ID+"resultSearchAroundform"+numResult);
				numResult++;
			//}
 if (index + 1 < data.result.length) {
                                $('<hr>').appendTo('#' + this.resultBoxNodeID);
                            }
                        }, this)
                    );
                } else {
                    $('<div>').text('sorry no result found').appendTo('#' + this.resultBoxNodeID);
                }
            } else {
                $('<div>').text('sorry no data recieved').appendTo('#' + this.resultBoxNodeID);
            }
            if ($('#' + this.resultBoxNodeID).is(":hidden")) {
                $('#' + this.resultBoxNodeID).slideDown(200);
            } else {
                $('#' + this.resultBoxNodeID).hide();
            }

            $('#' + this.resultBoxNodeID).slideDown(200);
        };

       function findAround(formId){
	 $('#'+formId).serialize()
 	        $.ajax({
                     url:'/geoloc/search?format=json&'+$('#'+formId).serialize(),
                })
                .done($.proxy(doProcessGeolocResults, this));
	return false;
	}

function doProcessGeolocResults(data){
 	if (console && console.log) {
                	      console.log(data.result );
         }
var RedIcon = L.Icon.Default.extend({
            options: {
            	    iconUrl: 'img/marker-icon-red.png' 
            }
         });
 var redIcon = new RedIcon();
       if (data  && typeof map != 'undefined') {
		if (data.result && data.result.length >0){
		 if (typeof markerGeoloc != 'undefined') {
			 map.removeLayer(markerGeoloc)
		}
			var markerGeolocArray = [];
                    $.each(data.result,
                       function(index, value) {
				console.log(value);
				var content='';
				 if (value.countryCode) {
                        	        content += '<img src="img/' + value.countryCode + '.png" alt="' + value.countryCode + '" class="flag-autocomplete"/>';
                       		}
				if (value.name){
                            		content+= value.name+"<br/>"
				}
				if (value.placetype){
					content+=value.placetype;
				}
				if (value.amenity){
					if (value.placetype){
						content+="|";
					}
					content+=value.amenity;
				}
			
				if (value.lat && value.lng){
					content += "<br/>("+value.lat+","+value.lng+")";	
				}
				var marker =L.marker([value.lat, value.lng], {icon: redIcon}).bindPopup(content);
				markerGeolocArray.push(marker);
			}
                    );
		   markerGeoloc = L.featureGroup(markerGeolocArray);
	           markerGeoloc.addTo(map);
		map.fitBounds(markerGeoloc.getBounds());
			
       		} else {
                    $('<div>').text('sorry no result found').appendTo('#' + this.resultBoxNodeID);
                }
      } else {
                $('<div>').text('sorry no data recieved').appendTo('#' + this.resultBoxNodeID);
      }
      
}



        function buildPlaceTypeDropBox(lang,placetypeNodeID) {
            if (!lang) {
                lang = DEFAULT_LANGUAGE;
            }
            $('#lang' + lang).attr('checked', 'true');
            lang = lang.toUpperCase();
            var sel = $('<select>').attr('id', placetypeNodeID).attr('name', 'placetype').addClass('placetypes');
            var dropBoxHtml = $("#" + placetypeNodeID);
            if (dropBoxHtml.length > 0) {
                dropBoxHtml.replaceWith(sel);
            } else {
                sel.appendTo(this._toolsNode)
            }

            sel.append($("<option>").attr('value', '').text(translation['choosepoitype'][lang]));
            $.each(placetype, function(placetype, value) {
                //	console.log( index + ": " + value );
                $.each(value, function(countrycode, translations) {
                    if (countrycode == lang) {
                        var selectOptionText = placetype;
                        if (translations.length >= 1) {
                            selectOptionText = translations[0];
                        }
                        //console.log( placetype+'['+countrycode+']' + "=" + selectOptionText );
                        sel.append($("<option>").attr('value', placetype).text(selectOptionText));
                    }
                    //var value= index;

                });
            });
	return sel;
        };

        function BuildLanguageSelector(lang) {
            var ff = function(key, value) {
                $('<input>').attr('type', 'radio').attr('name', 'lang').attr('onclick', 'autocompleteGisgraphy[' + autocompleteGisgraphyCounter + '].changeLanguage(this.value);').attr('id', 'lang' + key).attr('value', key).addClass('languages').appendTo(this._toolsNode).after(value);
            }
            $.each(SUPPORTED_LANGUAGE, $.proxy(ff, this));
            $('#lang' + lang).attr('checked', 'true');
            lang = lang.toUpperCase();

        };

        function buildPoisArray(lang) {
            if (!lang) {
                lang = DEFAULT_LANGUAGE;
            }
            var pois = [];
            var seen = {};
            $.each(placetype, function(placetype, value) {
                $.each(value, function(countrycode, translations) {
                    if (countrycode == lang) {
                        if (translations.length == 0) {
                            //no translations
                            pois.push(placetype);
                        } else {
                            var selectOptionText = placetype;
                            if (translations.length > 0) {
                                for (var i = 0, len = translations.length; i < len; i++) {
                                    if (!seen[translations[i]]) {
                                        seen[translations[i]] = true;
                                        pois.push({
                                            "text": translations[i],
                                            "poiType": placetype
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
            });
            return pois;
        }

        function initUI() {
            this._formNode = $('<form>').attr('id', this.formNodeID).attr('action', this.geocodingUrl); //.appendTo('#'+this.ELEMENT_ID);
            this._toolsNode = $('<div>').attr('id', this.toolsNodeID).addClass('tools').appendTo(this._formNode);
            if (this.withHelp) {
                $("<img>").attr('alt', 'help').attr('src', './img/help.png').attr('onclick', 'autocompleteGisgraphy[' + autocompleteGisgraphyCounter + '].displayHelp();').addClass('help').appendTo(this._toolsNode);
            }
            if (this.allowLanguageSelection) {
                this.BuildLanguageSelector(DEFAULT_LANGUAGE);
            }
            if (this.allowPoiSelection) {
                this.buildPlaceTypeDropBox(DEFAULT_LANGUAGE,this.placetypeNodeID);
            }
            this.buildSearchBox();
            if ($('#' + this.ELEMENT_ID).length > 0) {
                this._formNode.appendTo($('#' + this.ELEMENT_ID));
            }
            if (this.userLat != undefined && this.userLng != 'undefined' && typeof map != 'undefined') {
                map.panTo(new L.LatLng(this.userLat, this.userLng));

            }
        }



        function initAutoCompletion() {
            if ($('#' + this.ELEMENT_ID).length == 0) {
                return;
            }
            this.geocoding.initialize();
            $('#' + this.inputSearchNodeID).typeahead({
                hint: true,
                highlight: true,
                minLength: 1
            }, {
                name: this.ELEMENT_ID + '',
                displayKey: function(obj) {
		    if (obj && obj['country_code'] && obj['country_code'].length == 2 && $.inArray(obj['country_code'], NAME_HOUSE_COUNTRYCODE) >=0){
			var addressFormated = obj['name'];			 
			var housenumber = extractHouseNumber($('#'+this.name+'-inputSearch').val());
			if (housenumber && housenumber.length >0){
				addressFormated += ' '+housenumber;
		    	}
 			if (obj['is_in'] || obj['is_in_place']) {
				if(obj['is_in_place']){
                        	   addressFormated +=', '+obj['is_in_place'];
                        	}
				var zip='';
                                 if (obj['is_in_zip'] && obj['is_in_zip'].length ==1){
				 	addressFormated+=', '+obj['is_in_zip'][0]+' ';
				}
                       		 if (obj['is_in']) {
                        		addressFormated+= ', ' + obj['is_in'];
                       		 }
                   	 }
                        return addressFormated;
			
		    } else {
		    var housenumber = extractHouseNumber($('#'+this.name+'-inputSearch').val());
		    if (housenumber && housenumber.length >0){
			housenumber= housenumber+', ';
		    }
                    var is_in = '';
                    if (obj['is_in'] || obj['is_in_place']) {
			if(obj['is_in_place']){
                           is_in +=', '+obj['is_in_place'];
                        }
			var zip='';
                        if (obj['is_in_zip'] && obj['is_in_zip'].length ==1){
                                       is_in+=', '+obj['is_in_zip'][0];
                        }
                        if (obj['is_in']) {
                            is_in += ', ' + obj['is_in'];
                        }
                        /*else if (obj['adm1_name']){
                        					is_in=obj['adm1_name'];
                        					}*/
                        return housenumber+''+obj['name'] + is_in;
                    } else {
                        return housenumber+''+obj['name'];
                    }
		}
                },
                // `ttAdapter` wraps the suggestion engine in an adapter that
                // is compatible with the typeahead jQuery plugin
                source: this.geocoding.ttAdapter(),
                templates: {
                    empty: Handlebars.compile('<div class="empty-message">{{l10n "nosuggestion" currentLanguage}}</div>'),
                    suggestion: Handlebars.compile('{{#if name}}<p>{{#if country_code}}<img src="img/{{country_code}}.png" alt={{country_code}} class="flag-autocomplete"/>{{/if}}{{#if_number_after country_code }}<strong>{{name}}{{#if_eq zipcode.length 1}} ({{zipcode}}){{/if_eq}}</strong> {{{housenumber house_numbers "'+this.inputSearchNodeID+'"}}}{{#if houseNumber}}{{houseNumber}}</span>{{/if}}{{else}} {{{housenumber house_numbers "'+this.inputSearchNodeID+'"}}} {{#if houseNumber}}{{houseNumber}}</span>{{/if}}<strong>{{name}}{{#if_eq zipcode.length 1}} ({{zipcode}}){{/if_eq}}</strong>{{/if_number_after}}{{#if is_in}}<span class="isin-autocomplete">, {{#if_eq is_in_zip.length 1}} {{is_in_zip}}{{/if_eq}} {{is_in}}</span> {{else}}{{#if adm1_name}}<span class="isin-autocomplete">, {{adm1_name}}</span>{{/if}}{{/if}}</p>{{/if}}'),
                    footer: '<div class="footer">powered by <a href="http://www.gisgraphy.com/">Gisgraphy.com</a></div>'
                }
            });

            $('input.typeahead').keypress(
                //$('#gisgraphy-leaflet-form').on('submit',
                $.proxy(function(e) {
                    if (e.which == 13) {
                        if (!this.itemSelected) {
                            //        var selectedValue = $('input.typeahead').data().ttView.dropdownView.getFirstSuggestion().datum.id;
                            //      $("#value_id").val(selectedValue);
                            console.log('enter pressed : ' + this.itemSelected);
                            this.doGeocoding();
                            $('#' + this.inputSearchNodeID).typeahead('close');
                            //        $('#gisgraphy-leaflet-form').submit();
                            this.itemSelected = false;
                            return false;
                        } else {
                            this.itemSelected = false;
                        }
                    }
                }, this));

            $('#' + this.inputSearchNodeID).bind('typeahead:selected', doOnSelect);
            $('#' + this.inputSearchNodeID).bind('typeahead:cursorchanged', doOnChoose);
            $('#' + this.inputSearchNodeID).bind('typeahead:autocompleted', doOnAutocompleted);
            $('#' + this.inputSearchNodeID).focus();
        }

       // this.initAutoCompletion();
        window.autocompleteGisgraphy[autocompleteGisgraphyCounter] = this;
        this.instanceCounter= autocompleteGisgraphyCounter; 
        autocompleteGisgraphyCounter++;

        var doOnAutocompleted = $.proxy(function(obj, datum, name) {
            console.log('doOnAutocompleted');
            if (datum && datum.poiType && this.allowPoiSelection) {
                $('#' + this.placetypeNodeID + ' option[value="' + datum.poiType + '"]').prop('selected', true);
            } else {
                $('#' + this.placetypeNodeID + ' option[value=""]').prop('selected', true);
            }
            /*if (typeof map != 'undefined' && datum && datum.lat && datum.lng){
            		console.log('moving to '+datum.lat+','+datum.lng);
                                            map.panTo(new L.LatLng(datum.lat,datum.lng));
            }*/
	   if (datum.house_numbers && datum.house_numbers.length > 0){
		var coord = getHouseNumbercoordinate(datum.house_numbers,obj.currentTarget.id);
		if (coord && coord.hasOwnProperty("lat") && coord.hasOwnProperty("long")){
			this.result = datum;
		        this.result.house_coordinate = coord;
			moveCenterOfMapTo(coord.lat, coord.long,datum.placetype);
		} else {
			 moveCenterOfMapTo(datum.lat, datum.lng, datum.placetype);
	                 this.result = datum;
		}
            } else {	
                 moveCenterOfMapTo(datum.lat, datum.lng, datum.placetype);
		 this.result = datum;
		}
            this.itemSelected = true;
            /*console.log('selected');
            console.log(obj);
            console.log(datum);
            console.log(name);*/
           
	   
            return false;

        }, this);

	 var doOnSelect = $.proxy(function(obj, datum, name) {
            console.log('doOnSelect');
            if (datum && datum.poiType && this.allowPoiSelection) {
                $('#' + this.placetypeNodeID + ' option[value="' + datum.poiType + '"]').prop('selected', true);
            } else {
                $('#' + this.placetypeNodeID + ' option[value=""]').prop('selected', true);
            }
            /*if (typeof map != 'undefined' && datum && datum.lat && datum.lng){
            		console.log('moving to '+datum.lat+','+datum.lng);
                                            map.panTo(new L.LatLng(datum.lat,datum.lng));
            }*/
	   if (datum.house_numbers && datum.house_numbers.length > 0){
		var coord = getHouseNumbercoordinate(datum.house_numbers,obj.currentTarget.id);
		if (coord && coord.hasOwnProperty("lat") && coord.hasOwnProperty("long")){
			this.result = datum;
		        this.result.house_coordinate = coord;
			moveCenterOfMapTo(coord.lat, coord.long,datum.placetype);
		} else {
			 moveCenterOfMapTo(datum.lat, datum.lng, datum.placetype);
	                 this.result = datum;
		}
            } else {	
                 moveCenterOfMapTo(datum.lat, datum.lng, datum.placetype);
		 this.result = datum;
		}
            this.itemSelected = true;
            /*console.log('selected');
            console.log(obj);
            console.log(datum);
            console.log(name);*/
	    var data={"numFound":1,"result":[convertDatumToAddress(datum,coord)]};
            var displayBox =  $.proxy(doProcessGeocodingResults, this)
	    displayBox(data);
            return false;

        }, this);

	var doOnChoose = $.proxy(function(obj, datum, name) {
            console.log('doOnChosse');
            if (datum && datum.poiType && this.allowPoiSelection) {
                $('#' + this.placetypeNodeID + ' option[value="' + datum.poiType + '"]').prop('selected', true);
            } else {
                $('#' + this.placetypeNodeID + ' option[value=""]').prop('selected', true);
            }
            /*if (typeof map != 'undefined' && datum && datum.lat && datum.lng){
            		console.log('moving to '+datum.lat+','+datum.lng);
                                            map.panTo(new L.LatLng(datum.lat,datum.lng));
            }*/
	   if (datum.house_numbers && datum.house_numbers.length > 0){
		var coord = getHouseNumbercoordinate(datum.house_numbers,obj.currentTarget.id);
		if (coord && coord.hasOwnProperty("lat") && coord.hasOwnProperty("long")){
			this.result = datum;
		        this.result.house_coordinate = coord;
			moveCenterOfMapTo(coord.lat, coord.long,datum.placetype);
		} else {
			 moveCenterOfMapTo(datum.lat, datum.lng, datum.placetype);
	                 this.result = datum;
		}
            } else {	
                 moveCenterOfMapTo(datum.lat, datum.lng, datum.placetype);
		 this.result = datum;
		}
            this.itemSelected = true;
            /*console.log('selected');
            console.log(obj);
            console.log(datum);
            console.log(name);*/
           
	   
            return false;

        }, this);

        function tog(v) {
            return v ? 'addClass' : 'removeClass';
        }

        $(document).on('input', '.clearable', function() {
            $(this)[tog(this.value)]('x');
        }).on('mousemove', '.x', function(e) {
            $(this)[tog(this.offsetWidth - 18 < e.clientX - this.getBoundingClientRect().left)]('onX');
        }).on('click', '.onX', function() {
            $(this).removeClass('x onX').val('');
            $('geocoding').typeahead('val', '');
            $('geocoding').typeahead('close');
        });

        function getLocalSuggestionsArray(lang) {
            if (this.allowMagicSentence) {
                var poiArray = $.map(this.pois, function(poi) {
                    return {
                        name: poi.text + " " + translation['near'][lang] + " ",
                        poiType: poi.poiType,
                        country_code: "gps"
                    };
                })
                var streetArray = $.map(streettype[lang], function(st) {
                    return {
                        name: st,
                        country_code: "road"
                    };
                })
                return $.merge(poiArray, streetArray);
            } else {
                return [];
            }
        }

        function changeLanguage(lang) {
            this.currentLanguage = lang;
            if (this.allowPoiSelection) {
                this.buildPlaceTypeDropBox(lang,this.placetypeNodeID);
                this.pois = buildPoisArray(lang);
            }
            this.geocoding.clear();
            this.geocoding.local = this.getLocalSuggestionsArray(lang);
            this.geocoding.initialize(true);
            $('#' + this.inputSearchNodeID).focus();
            $('#' + this.inputSearchNodeID).attr('placeholder', translation['placeholder'][lang])

        }
//use to display the address when we select datum
 function convertDatumToAddress(datum,hnCoord) {
            var address = {};
            if (datum) {
	                     address["countryCode"] = datum.country_code;
			     address["placetype"] = datum.placetype;
			     if (datum.zipcode && datum.zipcode.length ==1){
				address['zipCode']=datum.zipcode[0];
		             } else if (datum.is_in_zip && datum.is_in_zip && datum.is_in_zip.length ==1){
				address['zipCode']= datum.is_in_zip[0];
			     }
			     if (datum.is_in){
	                     	address["streetName"] =  datum.name;
			     	address["city" ] =  datum.is_in;
                             } else {
	                      	address["name"] = datum.name;
			     }
			     if (hnCoord && hnCoord["lat"] && hnCoord["long"]){
		             	address["houseNumber"] = hnCoord.number;
			      	address["lat"] = hnCoord.lat;
			      	address["lng"] = hnCoord.long;
		       	     } else {
			        address["lat"] = datum.lat;
			        address["lng"] = datum.lng;
			    }
            }
            return address;
        }
// use when reverse geocoding for a gps/DMS
        function convertAddressToDatum(address) {
            var doc = {};
	   
           if (address) {
		var zips = [];
		if (address.zipCode){
			zips[O]=address.zipCode;
		}	
                doc = {
                    "name": address.streetName,
                    "is_in": address.city,
		    "is_in_place" : address.dependentLocality,
                    "country_code": address.countryCode,
                    "lat": address.lat,
                    "lng": address.lng,
                    "distance": address.distance,
                    "houseNumber": address.houseiNumber,
		    "zipcode":zips,
                };
            }
            return doc;
        }

    }

    gisgraphyAutocomplete.noConflict = function noConflict() {
        root.gisgraphyAutocomplete = old;
        return gisgraphyAutocomplete;
    };
    return gisgraphyAutocomplete;

    function getSorter(sortFn) {
        return sort;

        function sort(array) {
            return array.sort(sortFn);
        }

        function noSort(array) {
            return array;
        }
    }

    function ignoreDuplicates() {
        return false;
    }
})(this);


//-------------------------------------------------------------------------------------------
//DMS

/*
 Pattern 1 (ex: 40:26:46.302N 079:56:55.903W)
 Pattern 2 (ex: 40°26′47″N 079°58′36″W or 40°26'47"N 079°58'36"W)
 Pattern 3 (ex: 40d 26′ 47″ N 079d 58′ 36″ W or 40d 26' 47" N 079d 58' 36" W)
*/

function convertDMS(input) {
    var obj;
    var matches = input.match(/((\d+)\s?[:°dD]*\s?(\d+)\s?\s?[:'′]*\s?(\d+([.]\d+)?)?\s?[:\"″]*\s?([NnSs])\s?([,;]\s?)?(\d+)\s?[:°dD]*\s?(\d+)\s?[:'′]*\s?(\d+([.]\d+)?)?\s?[:\"″]*\s?([EeWw]))/);
    if (matches) {
        /*for (var i=0;i<matches.length;i++){
		console.log(matches[i]);
	}*/
        lat = convertToDecimal(matches[2], matches[3], matches[4], matches[6]);
        long = convertToDecimal(matches[8], matches[9], matches[10], matches[12]);
        obj = {
            "lat": lat,
            "long": long
        };
    }
    return obj
}


function convertToDecimal(
    degrees, minutes, seconds, hemisphere) {

    var hemi = (hemisphere.toUpperCase() === "N" || hemisphere.toUpperCase() === "E") ? 1 : -1;

    degrees = parseFloat(degrees.replace(',', '.'))
    minutes = parseFloat(minutes.replace(',', '.'))
    seconds = parseFloat(seconds.replace(',', '.'))

    return (degrees + minutes / 60.0 + seconds / 3600.0) * hemi;
}

function couldbeCoordinate(str) {
    match = str.match(/[-\d\sWwEeNnSsOo\.,:°'"]+/);
    if (match) {
        return match[0] == str;
    } else {
        return false;
    }
}



/* Pattern 1 (ex: -23.399437,-52.090904 or 40.446195, -79.948862)
 Pattern 2 (ex: 40.446195N 79.948862W)
*/
function convertDD(input) {
    var obj;
    var matches = input.match(/((-?\d+[.,]\d+)\s*([NnSs])?(\s*[,;]?\s*)?(-?\d+[,.]\d+)\s*([WwEe])?)/);
    if (matches) {
        /*for (var i=0;i<matches.length;i++){
			console.log(matches[i]);
		}*/
        lat = parseDD(matches[2], matches[3]);
        long = parseDD(matches[5], matches[6]);
        obj = {
            "lat": lat,
            "long": long
        };
    }
    return obj;
}


function parseDD(decimalDegrees, hemisphere) {
    var hemi = 1;
    if (hemisphere) {
        hemi = (hemisphere.toUpperCase() === "N" || hemisphere.toUpperCase() === "E") ? 1 : -1;
    }

    decimalDegrees = parseFloat(decimalDegrees.replace(',', '.'));

    return decimalDegrees * hemi;
}

/*convertDMS("40:26:46.302N 079:56:55.903W");
convertDMS(50°37'59.7"N 3°03'13.2"E")
convertDD("-23.399437,-52.090904");
convertDD("-23.399437 -52.090904");
convertDD("-23,399437  -52,090904");*/

function convertToLatLong(str) {
    var obj;
    if (str) {
        if (couldbeCoordinate(str)) {
            obj = convertDD(str);
            if (!obj) {
                obj = convertDMS(str)
            }
        }
    }
    return obj;
}

num_pattern = /(((?:(?:\b\d{1,3}))\b(?:[\s,;]+)(?!(?:st\b|th\b|rd\b|nd\b))(?=\w+))|\s(?:\b\d{1,3}$))/i;
var num_p = new RegExp(num_pattern);

function extractHouseNumber(str){
if (!str){
   return '';
}

res = str.match(num_p);
if (res && res.length >=0){
res = res[0].replace(/\W+/g, "").trim();
console.log('find "'+res+'" in "'+str+'"');
return res;
} else {
return '';
}
}
 function getHouseNumbercoordinate(house_numbers, autocompleteid) {
	var found = false;
	var number = extractHouseNumber($('#'+autocompleteid).val());
  	if (house_numbers && number.length >= 0){
	coord = {};
	 $.each(house_numbers, function(key, value) {
	      var hnArray = value.split(':');
	      if (number == hnArray[0]){
		if(hnArray[1] && !found){	      
			var latLongAsStr=hnArray[1].split(',');
			console.log('found house number :'+hnArray[0]+' is at '+latLongAsStr[0]+' and '+latLongAsStr[1]);
			found = true;
		 	coord = {
			    "lat": latLongAsStr[1],
			    "long": latLongAsStr[0],
			    "number":number
			};
		}
	     }
	});
 	 return coord;
	}
	}

function replaceHouseNumber(str){
if (!str){
   return str;
}
strReplaced = str.replace(num_pattern, "").trim();
console.log(str+'=>'+strReplaced);
return strReplaced;
}

NAME_HOUSE_COUNTRYCODE = ["DE","BE","HR","IS","LV","NL","NO","NZ","PL","RU","SI","SK","SW","TR"];

