L.Control.reversehelp = L.Control.extend({
    options: {
        position: 'topright',
    },

    onAdd: function(map) {
        this._container = L.DomUtil.create('div', 'leaflet-control-help');
        this._container.id = "reversehelp";
        this._container.innerHTML += '<span class="closable" onclick="$(\'#reversehelp\').empty().hide();"> </span><div>Tips : Right click on the map to reverse geocode</div>';
        return this._container;
    }

});

var reverseGeocoderMarker = undefined;
L.Map.mergeOptions({
    contextmenuItems: []
});

L.Control.gisgraphyreversegeocoder = L.Handler.extend({
    options: {
        help: true,
    },

    initialize: function(map) {
        //console.log('init reverse');
        L.Handler.prototype.initialize.call(this, map);

        this._items = [];
        this.result = undefined;
        this._visible = false;
        var container = this._container = L.DomUtil.create('div');

        map.on('contextmenu', this._show);
        if (this.options.help) {
            map.addControl(new L.Control.reversehelp());
        }
    },


    addHooks: function() {
        L.DomEvent
            .on(document, (L.Browser.touch ? this._touchstart : 'mousedown'), this._onMouseDown, this);

        thir._map.on({
            contextmenu: this._show
        }, this);
    },

    removeHooks: function() {
        L.DomEvent
            .off(document, (L.Browser.touch ? this._touchstart : 'mousedown'), this._onMouseDown, this)

        this._map.off({
            contextmenu: this._show
        }, this);
    },
    _show: function(e) {
       // console.log('do geocode show');
        var pos = e.latlng,
            opts = this.options;
        if (pos) {
            pos = pos.wrap();
            this._currentPos = pos;
            var lat = L.NumberFormatter.round(pos.lat, 10, ".");
            var lng = L.NumberFormatter.round(pos.lng, 10, ".");
            console.log(lat + ' ' + lng);
            $.ajax({
                type: 'GET',
                url: '/reversegeocoding/search?format=json&lat=' + lat + '&lng=' + lng,
                contentType: 'application/json; charset=utf-8',
                success: function(result) {
                    console.log('result:');
                    if (result.result && result.result[0]) {
                        var address = result.result[0];
                        map.gisgraphyreversegeocoder.result = address;
                        console.log(address);
                        var content = '';
                        var hasName = false;
                        var hasNumber = false;
                        if (address.countryCode) {
                            content += '<img src="img/' + address.countryCode + '.png" alt="' + address.countryCode + '" width="20px;"/>&nbsp;&nbsp;';
                        }
                        if (address.countryCode && $.inArray(address.countryCode, ["DE", "BE", "HR", "IS", "LV", "NL", "NO", "NZ", "PL", "RU", "SI", "SK", "SW", "TR"]) >= 0) {
                            if (address.streetName) {
                                hasName = true;
                                content += "<strong>" + address.streetName + " </strong>";
                            } else if (address.name) {
                                hasName = true;
                                content += "<strong>" + address.name + " </strong>";
                            }
                            if (address.houseNumber) {
                                hasNumber = true;
                                content += address.houseNumber;
                            }
                            if (address.city) {
                                if (hasNumber == true) {
                                    content += ', ';
                                } else {
                                    content += " ";
                                }
                                content += address.city;
                            }

                        } else {
                            if (address.houseNumber) {
                                content += address.houseNumber + " ";
                            }
                            if (address.streetName) {
                                hasName = true;
                                content += "<strong>" + address.streetName + "</strong>";
                            } else if (address.name) {
                                hasName = true;
                                content += "<strong>" + address.name + "</strong>";
                            }
                            if (address.city) {
                                if (hasName == true) {
                                    content += ', ';
                                }
                                content += address.city;
                            }
                        }
                        if (address.distance) {
                            content += "<br/>&nbsp;(" + address.distance + " m)";
                        }
                        if (typeof reverseGeocoderMarker != 'undefined') {
                            map.removeLayer(reverseGeocoderMarker);
                        }
                        reverseGeocoderMarker = L.popup().setLatLng([address.lat, address.lng]).setContent(content);
                        map.addLayer(reverseGeocoderMarker);

                    } else {
                        console.log('it seems that there is no result');
                    }
                },
                error: function() {
                    alert('error during reverse geocoding, maybe you have reached the limits of allowed requests on demo version, you can retry.');
                }
            });
        }
    },
    _reversegeocode: function() {

    },

    onRemove: function(map) {},

});

function fillPosition(position) {
    if (position) {
        var userLat = position.coords.latitude;
        var userLng = position.coords.longitude;
        if (typeof map != 'undefined') {
            map.panTo(new L.LatLng(userLat, userLng));
        }
    }
}

function detectPosition() {
    try {
        navigator.geolocation.getCurrentPosition(fillPosition);
    } catch (e) {
        console.log("can not detect position");
        console.log(e);
    }
}


L.Map.addInitHook('addHandler', 'gisgraphyreversegeocoder', L.Control.gisgraphyreversegeocoder);
detectPosition();