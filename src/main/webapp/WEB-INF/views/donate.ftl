<#import "macros/breadcrumbs.ftl" as breadcrumbs>
<html>
<head>
<title><@s.text name="donate.title"/></title>
</head>
<body>
<br/>
<noscript>
	<div class="tip yellowtip">
<@s.text name="global.noscript"/>
	</div>
	<br/>
</noscript>
<div>
<h2 class="header"><@s.text name="donate.title"/></h2>
<div class="biggertext"><@s.text name="donate.message"/></div>
<div class="biggertext"><@s.text name="donate.contibutors"/></div>
<br/><br/><br/>
<div class="center"><@breadcrumbs.paypalDonationBig/></div>
<br/><br/>
</div>

</body>
</html>