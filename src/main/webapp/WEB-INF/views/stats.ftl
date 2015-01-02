<html>
<head>
	<title><@s.text name="stats.title"/></title>
</head>

<body>
<@s.text name="stats.text"/> :
<br/>
<ul>
    <@s.iterator value="statsUsages" >
     	<li><@s.property value='statsUsageType' /> <@s.text name="stats.called"/> <@s.property value='usage' /> <@s.text name="stats.times"/></li>
	 </@s.iterator>
 <li><strong><@s.text name="stats.allservices"/> <@s.text name="stats.called"/> ${totalUsage} <@s.text name="stats.times"/></strong></li>
 </ul>
<br/>
<@s.text name="stats.flush.every"><@s.param>${FlushFrequency}</@s.param></@s.text>
<br/> 
<br/>
<@s.text name="global.others"/> :
<ul>
<li><a href="https://www.google.com/analytics/settings/?et=reset&hl=en" target="statsgis">Google analytics</a></li>
<li><a href="https://www.google.com/webmasters/tools/home" target="statsgis">Google webmaster tools</a></li>
<br/>
<li><a href="http://www.gisgraphy.com/feedback/feedback.htm" target="statsgis">feedbacks (general)</a></li>
<li><a href="http://www.gisgraphy.com/feedback-address-parser/tests.htm" target="statsgis">feedbacks (address parsing)</a></li>
<li><a href="http://www.gisgraphy.com/forum/" target="statsgis">forum</a></li>
<br/>
<li><a href="http://code.google.com/p/gisgraphy/downloads/list" target="statsgis">Google Code (Download)</a></li>
<li><a href="http://code.google.com/p/gisgraphy/issues/list" target="statsgis">Google Code (issues)</a></li>
<br/>
<li><a href="http://www.google.fr/search?as_q=gisgraphy&hl=fr&num=10&btnG=Recherche+Google&as_epq=&as_oq=&as_eq=&lr=&cr=&as_ft=i&as_filetype=&as_qdr=w&as_occt=any&as_dt=i&as_sitesearch=&as_rights=&safe=off" target="statsgis">New links to gisgraphy</a></li>
<li><a href="http://www.google.fr/search?as_q=gisgraphoid&hl=fr&num=10&btnG=Recherche+Google&as_epq=&as_oq=&as_eq=&lr=&cr=&as_ft=i&as_filetype=&as_qdr=w&as_occt=any&as_dt=i&as_sitesearch=&as_rights=&safe=off" target="statsgis">New links to gisgraphoid</a></li>
<br/>
</ul>
In the clouds :
<ul> 
	<li><a href="https://appengine.google.com/" target="statsgis">appengine</a></li>
</ul>
 <br/>
</body>
</html>
