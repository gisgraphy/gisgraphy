L.Control.gisgraphygeocoder = L.Control.extend({
    options: {
        position: 'topleft',
    },

    initialize: function(options) {
        L.Util.setOptions(this, options);
    },
    onAdd: function(map) {
        this._map = map;
        container = this._container = L.DomUtil.create('div');
        container.id = this.options.ELEMENT_ID || "gisgraphy-leaflet";
        this.autocomplete = new gisgraphyAutocomplete(this.options);
        container.appendChild(this.autocomplete._formNode[0]);
        var o = container
        L.DomEvent.disableClickPropagation(container);
        return container;
    },


    initAutoCompletion: function() {
        if (this.autocomplete) {
            this.autocomplete.initAutoCompletion();
        }
    },
    onRemove: function(map) {},

});