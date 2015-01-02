<head>
<style type="text/css">
body {
color:#444444;
font-family:verdana,arial,helvetica,sans-serif;
font-size:76%;
line-height:1em;
text-align:left;
}
</style>
</head>
<body>
News from Gisgraphy<br/>
<hr/>
<div>
<?php
$version =$_GET["version"];
 ?>
Last news from gisgraphy : <br/>
<?php if ($version !="4.0-beta1"){ ?>
<ul>
<li><span style="color:#FF0000">Important news</span> : Your version '<?php echo $version ?>' is <b>not</b> up to date. The new Gisgraphy version 4.0 beta is out. New important functionnalities has been added (geocoding web service, zipcode management, address parsing,...)<a href="http://www.gisgraphy.com/gisgraphy_v_4_0.htm">Read more</a>

</li>
</ul>
<hr/>
<?php }else{ ?>
<ul>
<li>
Your version is up to date
</li>
</ul>
<?php } ?> 
</div>
</body>
