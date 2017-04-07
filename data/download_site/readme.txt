<span style="font-size:1em;">
Welcome to Gisgraphy download server. Since 2006, Gisgraphy is a free opensource framework based on <a href="http://www.openstreetmap.org" target="gis">Openstreetmap</a>, <a href="http://www.geonames.org" target="gis">Geonames</a> and  <a href="http://quattroshapes.com/" target="gis">Quatroshapes</a> data that offers the ability to do geolocalisation, geocoding, and reverse geooding via Java APIs or REST webservices.
<br/>
More informations on the <a href="http://www.gisgraphy.com">Gisgraphy web site</a>.<br/>
<div style="color:#FF0000">Due to people that have abused / hacked the service, only PBF are available. CSV files for import are curently unavailable. Please send a mail to davidmasclet@gisgraphy.com if you really need it urgently.</div><br/>
<strong>
<ul>
<li>Freely available</li>
<li>34 million house numbers, 76 million streets, 7 million POIs; 4.3 million cities and 216,000 shapes of cities</li>
<li>Split by country and updated every month</li>
</ul>
</strong>

More questions, needs consulting, suggestions, comments, special needs ? site : <a href="http://www.gisgraphy.com/">http://www.gisgraphy.com/</a> or Mail : <a href="mailto:davidmasclet@gisgraphy.com">davidmasclet@gisgraphy.com</a><br/>
You can <a href="http://www.gisgraphy.com/premium/index.htm#data">order custom extracts</a> (based on openstreetmap tags. e.g :maxspeed), address databases, zipcodes extract, or Gisgraphy dumps on <a href="http://www.gisgraphy.com/premium">gisgraphy premium page</a><br/>
<br/>
<div class="center"><center><a href="/images/osmfiles.jpg" target="gis"><img src="/images/osmfiles.jpg" width="600"/><br/>
Gisgraphy process pipeline (click to enlarge).
</a></div>


<br/>
On these server, you will find several directories :<br/>
\_<b><a href="/releases/">releases</a></b> : releases of Gisgraphy, Gisgraphoid, plugins (leaflet,...) and client libs.<br/>
\_<b><a href="/addresses/">addresses</a></b> : <br/>
<span style="margin:20px;"></span>\_<b><a href="/addresses/csv/">csv</a></b>  aggreagted and deduplicated from <a href="http://www.openaddresses.io" target="gis">Openaddresses</a>, <a href="http://www.openstreetmap.org" target="gis">Openstreetmap</a>, <a href="http://www.geonames.org" target="gis">geonames</a>  in a CSV/TSV format (comming soon).<br/>
\_<b><a href="/openstreetmap/">openstreetmap</a></b> : <br/>
<span style="margin:20px;">\_<b><a href="/openstreetmap/pbf/">pbf</a></b> : PBF files extracted from Openstreetmap, splited by country, they ARE NOT used by the Gisgraphy importer.<br/>
<span style="margin:20px;">\_<b><a href="/openstreetmap/csv/">csv</a></b> : CSV files (only available as a premium services) of streets, houses, cities, and Pois, extracted from Openstreetmap, splited by country. Those files are downloaded by the Gisgraphy importer but can be used for anything else.<br/>
<span style="margin:40px;"></span>\_<b><a href="/openstreetmap/csv/streets/">streets</a></b>  (only available as a premium services) extracted from <a href="http://www.openstreetmap.org" target="gis">Openstreetmap</a> in a CSV/TSV format (see description bellow).<br/>

<span style="margin:60px;"></span>allcountries.gis contains each CSV/TSV for every country(~250).<br/>

<span style="margin:40px;"></span>\_<b><a href="/openstreetmap/csv/pois/">POIs</a></b> : (only available as a premium services) Point of interest (aka: POI) extracted from Openstreetmap in a CSV/TSV format (see description bellow).<br/>
<span style="margin:40px;"></span>\_<b><a href="/openstreetmap/csv/housenumbers/">housenumbers</a></b> : (only available as a premium services) House numbers extracted, based on the Karlsruhe schema<br/>
<span style="margin:40px;"></span>\_<b><a href="/openstreetmap/csv/cities/">cities</a></b> : (only available as a premium services) Cities extracted and splited by countries<br/>
\_<b><a href="/snapshots/">snapshots</a></b> : Snapshots of Gisgraphy framework (generally uploaded on demand)<br/>
<br/>
Files are provided "as is", without warranty or any representation of accuracy, tar-bzip2-compressed,<br/>
and updated every month. 
<br/><br/>
<b>Important</b> : Filename correspond to the <a href="http://fr.wikipedia.org/wiki/ISO_3166-2" target="gis">iso-3166 code</a>. The files are split by country code, not sovereignty, for instance FR doesn't contains Dom Tom and islands that are extract in a separate file (GP,RE,...). Tips : To get all files of a sovereignity, just have a look at country flags : same flag, same sovereignty.<br/>
<br/>
More information on the several CSV / TSV and PBF format <a href="/format.txt">here</a><br/>

<!--<br/>
<div class="center">Find that work useful ? <br/><br/><form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_s-xclick"/>
<input type="hidden" name="hosted_button_id" value="1694727"/>
<input type="image" src="https://www.paypal.com/en_US/i/btn/btn_donate_LG.gif" name="submit" alt="donate"/>
<img alt="pixel" border="0" src="https://www.paypal.com/fr_FR/i/scr/pixel.gif" width="1" height="1"/>
</form>
</div>-->
</span>