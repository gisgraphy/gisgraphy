<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
<html>
<head>
<title>
<@s.text name="search.for.place.to.edit"/>
</title>
</head>
<body>
<br/>
			<div class="clear"><br/></div>
	 			<div>
	 			
	 			<@s.url id="addstreet" action="editStreet!input" includeParams="none" namespace="/admin" />
<img src="/images/add.png" alt="" style="padding-right:5px;vertical-align:middle;"><a href="${addstreet}"><@s.text name="global.crud.create.new.street"/></a>
|
<@s.url id="addfeature" action="editFeature!input" includeParams="none" namespace="/admin" />
<img src="/images/add.png" alt="" style="padding-right:5px;vertical-align:middle;"><a href="${addfeature}"><@s.text name="global.crud.create.new.feature"/></a>

<br/><br/>
<u><@s.text name="global.or"/></u>
<br/><br/>
<img src="/images/search.png" alt="" style="padding-right:5px;vertical-align:middle;"><@s.text name="search.for.place.to.edit"/>
<br/><br/>
<@s.text name="search.fulltext.database.id"/>
<br/>
<@gisgraphysearch.fulltextsearchform ajax=true />
</body>
</html>