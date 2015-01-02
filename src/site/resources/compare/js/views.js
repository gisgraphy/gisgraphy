var Yonder = Yonder || {};

(function(Y) {
  Y.MapView = Backbone.View.extend({
    initialize: function() {
      this.map = new L.Map('map');
      this.pointsUpdated = 0;
      this.layerGroup = new L.LayerGroup();
          
      // create the tile layer with correct attribution
	  var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	  var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	  var osm = new L.TileLayer(osmUrl, {minZoom: 1, maxZoom: 18, attribution: osmAttrib});	

	

      this.map.addLayer(this.layerGroup);
      this.map.setView(new L.LatLng(40.7305991, -73.9865812), 2).addLayer(osm);

      this.collection.bind('change', this.render, this);
    },

    render: function() {
      var view = this,
        bounds,
        hasResults = false;

      // Count updated models
      view.pointsUpdated++;

      // Only render if the points updated is same as the collection size
      if (this.pointsUpdated === _.size(this.collection)) {
        // Clear existing markers
        view.layerGroup.clearLayers();

        // Init bounds to contain the points
        bounds = new L.LatLngBounds();
	var counter = 1; 
        this.collection.each(function(model) {
          var pt;

          if (model.has('Latitude') && model.has('Longitude')) {
            pt = new L.LatLng(model.get('Latitude')+counter*0.00001, model.get('Longitude')-counter*0.00001);
            counter++
            // Add markers to the map
            view.layerGroup.addLayer(new L.CircleMarker(pt, {
              color: model.color,
              radius: 8,
              fillOpacity: 0.7,
              opacity: 1.0
            }));

            // Extend the bounds
            bounds.extend(pt);

            hasResults = true;
          }
        });

        if (hasResults) {
          this.map.fitBounds(bounds);  
        }

        // Reset the counter
        this.pointsUpdated = 0;
      }
    }
  });


  Y.GeocoderView = Backbone.View.extend({

    template: _.template($('#geocoder-result-template').html()),

    initialize: function() {
      this.model.bind('change', this.render, this);
    },
    
    render: function() {
      var type = this.model.type;

      $('.geocoder-result', '#'+type).empty();

      _.each(this.model.toJSON(), function(val, name) {
        $('.geocoder-result', '#'+type).append(
          _.template( $("#geocoder-result-template").html(), 
            { 'name': name, 'val': val } 
          )
        );
      });

      return this;
    }
  });

  Y.GeocoderListView = Backbone.View.extend({
    // The context of this view
    el: $('.container'),

    template: _.template($('#geocoder-list-template').html()),

    events: {
      // Bind geocode button click
      'click button#geocode-submit': 'geocode'
    },

    initialize: function() {
      this.geocoders = new Y.GeocoderCollection();

      // Init and add each geocoder model
      _.each(Y.geocoderList, function(g) {
        var geocoder = new g();

        // Init the view
        new Y.GeocoderView({ model: geocoder});

        this.geocoders.add(geocoder);

        // Render the context for each geocoder result
        this.render(geocoder);
      }, this);
    },

    // Render the context for each  geocoder result
    render: function(geocoder) {
      $('#geocoder-results', this.el).append(
        _.template( $("#geocoder-list-template").html(), 
          { 'type': geocoder.type, 'name': geocoder.name, 'color': geocoder.color} 
        )
      );
    },

    // Call geocode for the collection, triggering updates for every model
    geocode: function() {
      var addr = $('input#geocode-address').val();
	/*if($('#countrylist').val()==""){
	    alert("Please select a country");
	    return;
	}*/

      if (addr) {
        // Triggers a fetch for each model in the collection
        this.geocoders.fetch( { 'address': addr });  
	$('div.span5').css("height","780")      
	} else {
	    alert("Please enter an address");
	}
    }
  });
})(Yonder);
