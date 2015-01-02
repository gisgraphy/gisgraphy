<html>
<head>
<title>
<#if !openstreetmap?? >
   <@s.text name="global.crud.create.new.street"/>
<#else>
<@s.text name="button.edit"/> <@s.text name="global.street"/>
'<#if openstreetmap.name??>${openstreetmap.name}<#else><@s.text name="global.street.noname" /></#if>'
</#if>
</title>
</head>
<body>
<div class="clear"><br/></div>
<h1>
<#if !openstreetmap?? >
   <@s.text name="global.crud.create.new.street"/>
<#else>
<@s.text name="button.edit"/> <@s.text name="global.street"/>
'<#if openstreetmap.name??>${openstreetmap.name}<#else><@s.text name="global.street.noname" /></#if>'
</#if>
</h1>
			<div class="clear"><br/></div>
	 			<#if openstreetmap?? >
	 			<@s.url id="addstreet" action="editStreet!input" includeParams="none" namespace="/admin" />
<img src="/images/add.png"/ alt="" style="padding-right:5px;vertical-align:middle;"/><a href="${addstreet}"><@s.text name="global.crud.create.new.street"/></a>

<@s.url id="deletestreet" action="editStreet!delete" includeParams="none" namespace="/admin" />
<span style="float:right;margin-right:130px;"><@s.form action="${deletestreet}" method="post" theme="simple" id="deleteForm">
  <@s.hidden name="gid" value="%{openstreetmap.gid}"/>
<img src="/images/delete.png"/ alt="" style="padding-right:5px;vertical-align:middle;"/><a href="#" onCLick="if (confirmDelete('street')){${'deleteForm'}.submit();}"><@s.text name="global.crud.delete.street"/></a>
</@s.form>
</span>
<br/><br/>
</#if>
<img src="/images/required_field.png"/ alt="" style="vertical-align:middle;"/> <@s.text name="search.required.label"/>
<br/>
<@s.url id="saveurl" action="editStreet" includeParams="none" method="save" namespace="/admin" />
<@s.form action="${saveurl}" method="post">
<fieldset>
<legend>

<#if !openstreetmap?? >
   <@s.text name="global.crud.create.new.street"/>
<#else>
<@s.text name="button.edit"/> <@s.text name="global.street"/> 
'<#if openstreetmap.name??>${openstreetmap.name}<#else><@s.text name="global.street.noname" /></#if>'
</#if>
</legend>
<@s.hidden name="gid" value="%{openstreetmap.gid}"/>
  <#if openstreetmap?? && openstreetmap.gid??>
	<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.FeatureId"/> : </span>${openstreetmap.gid?c}
		</span>
		<div class="clear"></div>
</#if>

<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.name"/> : </span><@s.textfield name="openstreetmap.name" value="%{openstreetmap.name}" theme="simple" size="35"/>
		</span>
		<div class="clear"></div>
<!-- start ui edition -->
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
<script type="text/javascript" src="/scripts/v3_epoly.js"></script>
<script type="text/javascript">

function gob(e){if(typeof(e)=='object')return(e);if(document.getElementById)return(document.getElementById(e));return(eval(e))}
var map;
var polyShape;
var startMarker;
var middleMarker;
var markers = [];
var midmarkers = [];
var polyPoints = [];
var pointsArray = [];
var toolID = 1;
var mylistener;
var editing = false;
var center;

var imageNormal = new google.maps.MarkerImage(
	"/images/square.png",
	new google.maps.Size(11, 11),
	new google.maps.Point(0, 0),
	new google.maps.Point(6, 6)
);
var imageHover = new google.maps.MarkerImage(
	"/images/square_over.png",
	new google.maps.Size(11, 11),
	new google.maps.Point(0, 0),
	new google.maps.Point(6, 6)
);
var imageNormalMidpoint = new google.maps.MarkerImage(
	"/images/square_transparent.png",
	new google.maps.Size(11, 11),
	new google.maps.Point(0, 0),
	new google.maps.Point(6, 6)
);


function initmap(){
    center = new google.maps.LatLng(
	<#if openstreetmap?? && openstreetmap.location??>
	${openstreetmap.location.y?string?replace(",", ".")},${openstreetmap.location.x?string?replace(",", ".")}
	<#else>
	0,0
	</#if>
);
    var myOptions = {
        zoom: 16,
        center: center,
        draggableCursor: 'default',
        draggingCursor: 'pointer',
        mapTypeControl: true,
        mapTypeControlOptions:{style: google.maps.MapTypeControlStyle.DROPDOWN_MENU},
        mapTypeId: google.maps.MapTypeId.HYBRID};
    map = new google.maps.Map(gob('map_canvas'),myOptions);
    polyPoints = new google.maps.MVCArray();
    preparePolyline();
    mylistener = google.maps.event.addListener(map, 'click', addPoint);
    resetLine();
}

function setMiddleOfLine(){
	if(middleMarker) middleMarker.setMap(null);
	if(polyPoints.length > 0){
	var pointInHalf = polyShape.GetPointAtDistance(polyShape.getLength()*1000/2);
	 middleMarker = new google.maps.Marker({
        position: pointInHalf,
        map: map});
	middleMarker.setTitle("Middle of the street");
	gob('latitude').value = pointInHalf.lat();
	gob('longitude').value = pointInHalf.lng();
	}
}

function resetLine(){
	clearMap();
<#if openstreetmap?? && openstreetmap.shape??>
	createPolylinesFromWKT('${openstreetmap.shape}');
</#if>
	setMiddleOfLine();
}

function preparePolyline(){
    var polyOptions = {
        path: polyPoints,
        strokeColor: "#FF0000",
        strokeOpacity: 1,
        strokeWeight: 3};
    polyShape = new google.maps.Polyline(polyOptions);
    polyShape.setMap(map);
  }


function addPoint(point){
    polyPoints = polyShape.getPath();
    polyPoints.insertAt(polyPoints.length, point.latLng); 
    var stringtobesaved = point.latLng.lng().toFixed(9) + ',' + point.latLng.lat().toFixed(9);
    pointsArray.push(stringtobesaved);
    if(toolID == 1) logCode1();
    setMiddleOfLine();
}

function addLatLong(latLng){
    polyPoints = polyShape.getPath();
    polyPoints.insertAt(polyPoints.length, latLng); 
    var stringtobesaved = latLng.lng().toFixed(9) + ',' + latLng.lat().toFixed(6);
    pointsArray.push(stringtobesaved);
    if(toolID == 1) logCode1();

}


function logCode1(){
	var reg=new RegExp("(,)", "g");
    var kmltext = 'LINESTRING(';
    for(var i = 0; i < pointsArray.length; i++) {
	nodeAsText =  pointsArray[i].replace(reg,' ');
        kmltext +=nodeAsText ;
	if ((i+1) != pointsArray.length ){
		kmltext += ',';
	}
    }
    kmltext +=')';
    gob('shape').value = kmltext;
	setMiddleOfLine();
}

function setTool(){
    if(toolID == 1){
        if(polyShape) polyShape.setMap(null);
        preparePolyline();
        logCode1();
    }
}

function clearMap() {
    if(editing) stopediting();
    if(startMarker) startMarker.setMap(null);
    if(middleMarker) middleMarker.setMap(null);
    polyShape.setMap(null);
    polyPoints = [];
    pointsArray = [];
    if(toolID == 1) preparePolyline();
    gob('shape').value ='';
}

function stopediting(){
    editing = false;
    gob('EditButton').value = 'Edit lines';
    for(var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    for(var i = 0; i < midmarkers.length; i++) {
        midmarkers[i].setMap(null);
    }
    polyPoints = polyShape.getPath();
    markers = [];
    midmarkers = [];
}
function editlines(){
    if(editing == true){
        stopediting();
    }else{
        polyPoints = polyShape.getPath();
        if(polyPoints.length > 0){
            toolID = gob('toolchoice').value = 1; 
            setTool();
            for(var i = 0; i < polyPoints.length; i++) {
                var marker = setmarkers(polyPoints.getAt(i));
                markers.push(marker);
                if(i > 0) {
                    var midmarker = setmidmarkers(polyPoints.getAt(i));
                    midmarkers.push(midmarker);
                }
            }
            editing = true;
            gob('EditButton').value = 'Stop edit';
        }
    }
}
function setmarkers(point) {
    var marker = new google.maps.Marker({
    	position: point,
    	map: map,
    	icon: imageNormal,
        raiseOnDrag: false,
    	draggable: true
    });
    google.maps.event.addListener(marker, "mouseover", function() {
    	marker.setIcon(imageHover);
    });
    google.maps.event.addListener(marker, "mouseout", function() {
    	marker.setIcon(imageNormal);
    });
    google.maps.event.addListener(marker, "drag", function() {
        for (var i = 0; i < markers.length; i++) {
            if (markers[i] == marker) {
                polyShape.getPath().setAt(i, marker.getPosition());
                movemidmarker(i);
                break;
            }
        }
        polyPoints = polyShape.getPath();
        var stringtobesaved = marker.getPosition().lat().toFixed(6) + ',' + marker.getPosition().lng().toFixed(6);
        pointsArray.splice(i,1,stringtobesaved);
        logCode1();
    });
    google.maps.event.addListener(marker, "click", function() {
        for (var i = 0; i < markers.length; i++) {
            if (markers[i] == marker && markers.length != 1) {
                marker.setMap(null);
                markers.splice(i, 1);
                polyShape.getPath().removeAt(i);
                removemidmarker(i);
                break;
            }
        }
        polyPoints = polyShape.getPath();
        if(markers.length > 0) {
            pointsArray.splice(i,1);
            logCode1();
        }
    });
    return marker;
}
function setmidmarkers(point) {
    var prevpoint = markers[markers.length-2].getPosition();
    var marker = new google.maps.Marker({
    	position: new google.maps.LatLng(
    		point.lat() - (0.5 * (point.lat() - prevpoint.lat())),
    		point.lng() - (0.5 * (point.lng() - prevpoint.lng()))
    	),
    	map: map,
    	icon: imageNormalMidpoint,
        raiseOnDrag: false,
    	draggable: true
    });
    google.maps.event.addListener(marker, "mouseover", function() {
    	marker.setIcon(imageNormal);
    });
    google.maps.event.addListener(marker, "mouseout", function() {
    	marker.setIcon(imageNormalMidpoint);
    });
    /*google.maps.event.addListener(marker, "dragstart", function() {
    	for (var i = 0; i < midmarkers.length; i++) {
    		if (midmarkers[i] == marker) {
    			var tmpPath = tmpPolyLine.getPath();
    			tmpPath.push(markers[i].getPosition());
    			tmpPath.push(midmarkers[i].getPosition());
    			tmpPath.push(markers[i+1].getPosition());
    			break;
    		}
    	}
    });
    google.maps.event.addListener(marker, "drag", function() {
    	for (var i = 0; i < midmarkers.length; i++) {
    		if (midmarkers[i] == marker) {
    			tmpPolyLine.getPath().setAt(1, marker.getPosition());
    			break;
    		}
    	}
    });*/
    google.maps.event.addListener(marker, "dragend", function() {
    	for (var i = 0; i < midmarkers.length; i++) {
    		if (midmarkers[i] == marker) {
    			var newpos = marker.getPosition();
    			var startMarkerPos = markers[i].getPosition();
    			var firstVPos = new google.maps.LatLng(
    				newpos.lat() - (0.5 * (newpos.lat() - startMarkerPos.lat())),
    				newpos.lng() - (0.5 * (newpos.lng() - startMarkerPos.lng()))
    			);
    			var endMarkerPos = markers[i+1].getPosition();
    			var secondVPos = new google.maps.LatLng(
    				newpos.lat() - (0.5 * (newpos.lat() - endMarkerPos.lat())),
    				newpos.lng() - (0.5 * (newpos.lng() - endMarkerPos.lng()))
    			);
    			var newVMarker = setmidmarkers(secondVPos);
    			newVMarker.setPosition(secondVPos);
    			var newMarker = setmarkers(newpos);
    			markers.splice(i+1, 0, newMarker);
    			polyShape.getPath().insertAt(i+1, newpos);
    			marker.setPosition(firstVPos);
    			midmarkers.splice(i+1, 0, newVMarker);
    			break;
    		}
    	}
        polyPoints = polyShape.getPath();
        var stringtobesaved = newpos.lat().toFixed(6) + ',' + newpos.lng().toFixed(6);
        pointsArray.splice(i+1,0,stringtobesaved);
        logCode1();
    });
    return marker;
}
function movemidmarker(index) {
    var newpos = markers[index].getPosition();
    if (index != 0) {
    	var prevpos = markers[index-1].getPosition();
    	midmarkers[index-1].setPosition(new google.maps.LatLng(
    		newpos.lat() - (0.5 * (newpos.lat() - prevpos.lat())),
    		newpos.lng() - (0.5 * (newpos.lng() - prevpos.lng()))
    	));
    }
    if (index != markers.length - 1) {
    	var nextpos = markers[index+1].getPosition();
    	midmarkers[index].setPosition(new google.maps.LatLng(
    		newpos.lat() - (0.5 * (newpos.lat() - nextpos.lat())),
    		newpos.lng() - (0.5 * (newpos.lng() - nextpos.lng()))
    	));
    }
}
function removemidmarker(index) {
    if (markers.length > 0) {
    	if (index != markers.length) {
    		midmarkers[index].setMap(null);
    		midmarkers.splice(index, 1);
    	} else {
    		midmarkers[index-1].setMap(null);
    		midmarkers.splice(index-1, 1);
    	}
    }
    if (index != 0 && index != markers.length) {
    	var prevpos = markers[index-1].getPosition();
    	var newpos = markers[index].getPosition();
    	midmarkers[index-1].setPosition(new google.maps.LatLng(
    		newpos.lat() - (0.5 * (newpos.lat() - prevpos.lat())),
    		newpos.lng() - (0.5 * (newpos.lng() - prevpos.lng()))
    	));
    }
}

function createPolylinesFromWKT(wkt){
	var reg=new RegExp("LINESTRING|\\(|\\)", "g");
    	var base =  wkt.replace(reg,'');
	var points=base.split(',');
	 for(var i=0;i<points.length;i++){
		var point=points[i].trim();
		var lat = parseFloat(point.split(' ')[0]);
		var lng = parseFloat(point.split(' ')[1]);
		var pt = new google.maps.LatLng(lng,lat);
		addLatLong(pt);
	}
		logCode1();
}


//]]>
</script>

<style type="text/css">
#map_canvas {
    /*position: absolute;
    top: 70px;
    left: 0px;*/
    width: 800px;
    height: 600px;
    background-color: #ffffff;
}
#presenter{
	display:none;
}
.editShape {
    width:800px;
    float:left;
}

</style>


<div class="editShape">
	<div id="map_canvas"></div>
	   
		<input   type="hidden" style="display:none;visibility:hidden;width:0;" id="toolchoice" name="toolchoice" onchange="toolID=parseInt(this.options[this.selectedIndex].value);setTool();" value="1"/>
		<input type="button" onclick="editlines();" value="Edit points of the shape" id="EditButton"/>
		<input type="button" onclick="clearMap();" value="Delete shape"/>
		<input type="button" onclick="resetLine();" value="Reset the initial shape"/>
<div id="info"></div>
</div>

<div id="presenter">
    lat : <input type="text" name="latitude" id="latitude"></input>; lng : <input type="text" id="longitude"  name="longitude"></input><br/>
    <textarea id="shape" cols="62" rows="1" name="shape"></textarea>
</div>
<div class="clear"></div>
<!-- end ui edition -->
 
<#if openstreetmap?? && openstreetmap.length??>
<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.length"/> (<@s.text name="global.unit"/>) : </span> ${openstreetmap.length}
</span>
		<div class="clear"></div>
</#if>

<span class="searchfield">
			<span class="searchfieldlabel"><img src="/images/required_field.png"/ alt="" style="vertical-align:middle;"/><@s.text name="global.country"/> : </span><@s.select label="countries" listKey="iso3166Alpha2Code" listValue="name" name="openstreetmap.countryCode" list="countries" headerValue="--choose--" headerKey="" multiple="false" required="true" labelposition="left" theme="simple" /> 
		</span>
		<div class="clear"></div>
<br/>


<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="street.oneway"/> : </span><@s.select name="openstreetmap.oneWay" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}" value="%{openstreetmap.oneWay}" theme="simple"/>
		</span><br/><br/>
		<div class="clear"></div>



<div class="clear"></div>
		<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.streettype"/> : </span>
			<@s.select headerKey="" headerValue="--Choose--"  name="streettype" list="streetTypes"  multiple="false" theme="simple" value="%{openstreetmap.streetType}" />
			<br/>
		</span>
		<br/>

<div class="clear"></div>
<span class="searchfield">
			<span class="searchfieldlabel"><@s.text name="global.is.in"/> : </span><@s.textfield name="openstreetmap.isIn" value="%{openstreetmap.isIn}" theme="simple" size="35"/>
		</span>
		<div class="clear"></div>
<#if !openstreetmap?? >
<@s.text name="gid.autogenerated"/>
</#if>
<br/>
<br/>
<div style="float:right;">
<@s.url id="cancelurl" action="editSearch" includeParams="none" namespace="/admin" />
 <@s.submit value="%{getText('button.save')}"  theme="simple"/> <input type="button" value="<@s.text name="button.cancel"/>" onClick="document.location.href='${cancelurl}'" />
<br/><br/>
</div>
</fieldset>
</@s.form>
<br/><br/>
<br/>
<script type="text/javascript">
initmap();
if(polyPoints.length > 0){
map.setZoom(16);
} else {
map.setZoom(2);
}
</script>
</body>
</html>
