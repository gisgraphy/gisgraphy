$(function(){
	Yonder.listView = new Yonder.GeocoderListView();
	Yonder.mapView = new Yonder.MapView({collection: Yonder.listView.geocoders});
});