L.Control.gisgraphygeocoder = L.Control.extend({
options: {
position: 'topleft',
},

initialize: function (options) {
	L.Util.setOptions(this, options);
},
onAdd: function (map) {
this._map = map;
container = this._container = L.DomUtil.create('div');
container.id=this.options.ELEMENT_ID || "gisgraphy-leaflet";
this.autocomplete = new gisgraphyAutocomplete(this.options);
container.appendChild(this.autocomplete._formNode[0]);
var o =container
/* L.DomEvent
            .addListener(o, 'click', L.DomEvent.stopPropagation)
            .addListener(o, 'click', L.DomEvent.preventDefault)
        .addListener(o, 'click', function () { console.log('click') });*/
 L.DomEvent.disableClickPropagation(container);
return container;
},


initAutoCompletion : function(){
if (this.autocomplete){
	this.autocomplete.initAutoCompletion();
}
},
onRemove: function (map) {
/*map.off('mousemove', this._onMouseMove)*/
},

});



/*L.control.gisgraphygeocoder = function (options) {
if (options || options.ELEMENT_ID) {
                throw new Error("please specify an ELEMENT_ID option");
        }

	return new L.Control.gisgraphygeocoder(options);
};*/
