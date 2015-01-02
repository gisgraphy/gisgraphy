var Yonder = Yonder || {};

(function(Y) {
  // Model colors: E41A1C, 377EB8, 4DAF4A, 984EA3, FF7F00, FFFF33, A65628, F781BF, 999999

  Y.GeocoderModel = Backbone.Model.extend({
    // Implement sync to call the geocode method
    sync: function(method, model, options) {
      if (method === 'read') {
        this.clear({silent: true});
        this.geocode(options.address); 
      } else {
        throw new Error('Method [' + method + '] is not supported. Geocoders are read-only.');
      }
    }
  });

  Y.geocoderList = [
//Gisgraphy
    Y.GeocoderModel.extend({
      //Include a unique geocoder name for display
      type: 'Gisgraphy',
      name: 'Gisgraphy',
      color: '#984EA3',
      // Geocode the address and call success or error when complete
      geocode: function(addr) {
        var model = this;

        try {
          $.ajax({
            dataType: 'jsonp',
            data: {
              address: addr,
              format: 'json',
              country: $('#countrylist').val()
            },
            url: 'http://185.13.36.133/geocoding/geocode',
            success: function (res) {
              if (res.numFound && res.numFound > 0) {
                  model.set(model.parse(res.result[0]));
              } else {
                model.set({'Error': 'No results.'});
              }
            }
          });
        } catch (e) {
          model.set({'Error': 'Error parsing results.'});
        }

      },
      // Override parse to set normalized attributes for display.
      // The res param is the raw respsone from the geocoder
      parse: function(res) {
        var spacesRe = / {2,}/g,
          normalRes = {
            'Address': [res.houseNumber, res.name, res.zipCode,res.city, res.countryCode].join(' ').replace(spacesRe, ' ').trim(),
            'Longitude': parseFloat(res.lng),
            'Latitude': parseFloat(res.lat),
            'Quality': res.geocodingLevel,
            'Raw': JSON.stringify(res, null, ' '),
	    'provider':'gisgraphy'
          };

        return normalRes;
      }
    }),
 //Nominatim
    Y.GeocoderModel.extend({
      //Include a unique geocoder name for display
      type: 'nominatim',
      name: 'Nominatim',
      color: '#FFFF33',
      // Geocode the address and call success or error when complete
      geocode: function(addr) {
        var model = this;

        try {
          $.ajax({
	    dataType: 'json',
            data: {
              q: addr,
	      format :'json'	
            },
            // Including key in the data object uri encoded the key
            url: 'http://nominatim.openstreetmap.org/search',
            crossDomain: true,
            success: function (res) {
              if (res.length && res.length >= 1) {
                model.set(model.parse(res[0]));
              } else {
                model.set({'Error': 'No results.'});
              }
            },
          });
        } catch (e) {
          model.set({'Error': 'Error parsing results.'});
        }
      },
      // Override parse to set normalized attributes for display.
      // The res param is the raw response from the geocoder
      parse: function(res) {
          normalRes = {
            'Address': res.display_name,
            'Longitude': parseFloat(res.lon),
            'Latitude': parseFloat(res.lat),
            'Quality': res.type,
            'Raw': JSON.stringify(res, null, ' '),
	    'provider':'nominatim'
          };

        return normalRes;
      }
    }), 
    //photon
   Y.GeocoderModel.extend({
      //Include a unique geocoder name for display
      type: 'Photon',
      name: 'photon',
      color: '#000000',
      // Geocode the address and call success or error when complete
      geocode: function(addr) {
        var model = this;

        try {
          $.ajax({
            dataType: 'json',
            data: {
              q: addr,
              limit: 1
            },
            url: 'http://photon.komoot.de/api/',
            success: function (res) {
              if (res && res.features && res.features && res.features.length >0) {
                  model.set(model.parse(res.features[0]));
              } else {
                model.set({'Error': 'No results.'});
              }
            }
          });
        } catch (e) {
          model.set({'Error': 'Error parsing results.'});
        }

      },
      // Override parse to set normalized attributes for display.
      // The res param is the raw respsone from the geocoder
      parse: function(res) {
        var spacesRe = / {2,}/g,
          normalRes = {
            'Address': [res.properties.name, res.properties.postcode, res.properties.city, res.properties.country].join(' ').replace(spacesRe, ' '),
            'Longitude': parseFloat(res.geometry.coordinates[0]),
            'Latitude': parseFloat(res.geometry.coordinates[1]),
            'Quality': res.type,
            'Raw': JSON.stringify(res, null, ' '),
	    'provider':'Photon'
          };

        return normalRes;
      }
    }),   
// Google Maps
    Y.GeocoderModel.extend({
      //Include a unique geocoder name for display
      type: 'google',
      name: 'Google Maps',
      color: '#E41A1C',
      // Geocode the address and call success or error when complete
      geocode: function(addr) {
        var geocoder = new google.maps.Geocoder(),
          model = this;

        try {
          geocoder.geocode( { 'address': addr}, function(results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
              model.set(model.parse(results[0]));
            } else {
              model.set({'Error': 'No results.'});
            }
          });
        } catch (e) {
          model.set({'Error': 'Error parsing results.'});
        }
      },
      // Override parse to set normalized attributes for display.
      // The res param is the raw respsone from the geocoder
      parse: function(res) {
        var normalRes = {
          'Address': res.formatted_address,
          'Longitude': res.geometry.location.lng(),
          'Latitude': res.geometry.location.lat(),
          'Quality': res.geometry.location_type,
          'Raw': JSON.stringify(res, null, ' '),
	  'provider':'Google'
        };

        return normalRes;
      }
    }),

    //Yahoo! PlaceFinder
    Y.GeocoderModel.extend({
      //Include a unique geocoder name for display
      type: 'yahoo',
      name: 'Yahoo! Placefinder',
      color: '#377EB8',
      // Geocode the address and call success or error when complete
      geocode: function(addr) {
        var model = this;

        try {
          $.ajax({
            dataType: 'jsonp',
            data: {
              q: 'select * from geo.placefinder where text="'+addr+'"',
              format: 'json',
              appid: Y.config.yahoo_id
            },
            url: 'http://query.yahooapis.com/v1/public/yql',
            success: function (res) {
              if (res.query.count || ($.isArray(res.query.results.Result) && res.query.results.Result.length > 0)) {
                // For some reason, this sometimes comes back as an array. Sad.
                if ($.isArray(res.query.results.Result)) {
                  model.set(model.parse(res.query.results.Result[0]));
                } else {
                  model.set(model.parse(res.query.results.Result));
                }
              } else {
                model.set({'Error': 'No results.'});
              }
            },
          });
        } catch (e) {
          model.set({'Error': 'Error parsing results.'});
        }

      },
      // Override parse to set normalized attributes for display.
      // The res param is the raw respsone from the geocoder
      parse: function(res) {
        var spacesRe = / {2,}/g,
          normalRes = {
            'Address': [res.line1, res.line2, res.line3, res.line4].join(' ').replace(spacesRe, ' '),
            'Longitude': parseFloat(res.longitude),
            'Latitude': parseFloat(res.latitude),
            'Quality': res.quality,
            'Raw': JSON.stringify(res, null, ' '),
	    'provider':'Yahoo'
          };

        return normalRes;
      }
    }),
    //MapQuest
    Y.GeocoderModel.extend({
      //Include a unique geocoder name for display
      type: 'mapquest',
      name: 'MapQuest',
      color: '#4DAF4A',
      // Geocode the address and call success or error when complete
      geocode: function(addr) {
        var model = this;

        try {
          $.ajax({
            dataType: 'jsonp',
            data: {
              location: addr
            },
            // Including key in the data object uri encoded the key
            url: 'http://www.mapquestapi.com/geocoding/v1/address?key=' + Y.config.mapquest_id,
            crossDomain: true,
            success: function (res) {
              if (res.results.length && res.results[0].locations.length) {
                model.set(model.parse(res.results[0].locations[0]));
              } else {
                model.set({'Error': 'No results.'});
              }
            },
          });
        } catch (e) {
          model.set({'Error': 'Error parsing results.'});
        }

      },
      // Override parse to set normalized attributes for display.
      // The res param is the raw respsone from the geocoder
      parse: function(res) {
        var spacesRe = / {2,}/g,
          normalRes = {
            'Address': [res.street, (res.adminArea5 || res.adminArea4), res.adminArea3, res.postalCode, res.adminArea1].join(' ').replace(spacesRe, ' '),
            'Longitude': parseFloat(res.displayLatLng.lng),
            'Latitude': parseFloat(res.displayLatLng.lat),
            'Quality': res.geocodeQuality,
            'Raw': JSON.stringify(res, null, ' '),
	    'provider':'Mapquest'
          };

        return normalRes;
      }
    })
  ];

  Y.GeocoderCollection = Backbone.Collection.extend({
    model: Y.GeocoderModel,
    // Override fetch to delegate to the models
    fetch: function(options) {
      this.each(function(model) {
        model.fetch(options);
      });
    }
  });
})(Yonder);