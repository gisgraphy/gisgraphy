<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/common/taglibs.jsp"%>
<%@ page session="false" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <%@ include file="/common/meta.jsp" %>
        <title><decorator:title/> | <fmt:message key="webapp.name"/> - <fmt:message key="webapp.tagline"/></title>
		<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/ui/theme.css'/>" />
		<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/ui/dropdown/dropdown.css'/>" />
		<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/ui/dropdown/themes/gisgraphy/default.advanced.css'/>" />
		
		<link rel="search" type="application/opensearchdescription+xml" title="Gisgraphy" href="/static/gisgraphy_opensearch_fulltext.xml"/>
        <decorator:head/>
    </head>
<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.class" writeEntireProperty="true"/>>
    <div id="page">
        <div id="header" class="clearfix">
            <div id="branding">
   				 <h1><a href="/"><img src="/images/logos/logo_70.png" alt="Free Geolocalisation Services" class="imgAlign"/><decorator:getProperty property="meta.heading"/></a></h1>
    			 <div id="tagline"><fmt:message key="webapp.tagline"/></div>
			</div>
        </div>
		<div id="content" class="clearfix">
		<div class="divider"><div></div></div>
            <div id="main">
      <!-- <script type="text/javascript">
		google_ad_client = "pub-7203216364107204";
		google_ad_slot = "0481202012";
		google_ad_width = 468;
		google_ad_height = 15;
	</script>-->
	<!--<span style="float:left;width:70px;">&nbsp;</span>
	<span style="margin-left:30px;">
 <script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
</span><div class="clear"><br/></div>-->
<s:if test="%{!getText('gisgraphyMessage').equals('')}">
<div class="tip yellowtip">
<s:text name="gisgraphyMessage"/>
</div>
</s:if>
                <decorator:body/>
        </div>
        </div>
	        <div id="footer" class="clearfix">
             <div class="divider"><div></div></div>
    <span class="left"><img src="http://www.gisgraphy.com/images/logos/poweredby.png" style="vertical-align:middle"/> <fmt:message key="webapp.version"/> | <a href="http://www.gisgraphy.com/feedback/" ><span class="underline red"><fmt:message key="global.give.feedback"/></span></a> | <span id="validators">
            <a href="/public/servicesdescription.html"><fmt:message key="search.webservices.overview.breadcrumbs"/></a> 
        </span>
    </span>
     <span class="right" style="padding-top:10px;">
        <a href="http://services.gisgraphy.com/public/donate.html" target="_blank"><img src="https://www.paypal.com/en_US/i/btn/btn_donate_SM.gif" alt="donate" class="donateBtn"/></a> | <a href="http://www.gisgraphy.com/free-access.htm"><fmt:message key="termsAndConditions"/></a> | <a href="<fmt:message key="company.url"/>"><fmt:message key="company.name"/> Project</a>  | <a href="mailto:<fmt:message key="company.mail"/>">Contact</a> | <a href="http://davidmasclet.gisgraphy.com">Blog</a> 
    </span>

        </div>
    </div>
    <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<div id="otherlang" class="clearfix">
<br/>
<div class="tip about" >
<fmt:message key="gisgraphy.about">
	<fmt:param>http://www.geonames.org/about.html</fmt:param>
	<fmt:param>http://openstreetmap.org/</fmt:param>
	<fmt:param>http://www.gisgraphy.com/</fmt:param>
</fmt:message>.
<fmt:message key="gisgraphy.baduse"><fmt:param>http://www.gisgraphy.com/abuse.txt</fmt:param></fmt:message>.
</div>
<fmt:message key="global.availablelang"/> : <span><a href="/?locale=en"><img src="/images/languages/EN_US.png"  alt="Gisgraphy in english"/></a></span> |
<span><a href="/?locale=fr_fr"><img src="/images/languages/FR.png"  alt="Gisgraphy en francais"/></a></span> |
<span><a href="/?locale=es"><img src="/images/languages/ES.png"  alt="Gisgraphy in spanish"/></a></span> |
<span><a href="/?locale=de"><img src="/images/languages/DE.png"  alt="Gisgraphy in German"/></a></span> |
<span><a href="/?locale=it"><img src="/images/languages/IT.png"  alt="Gisgraphy in Italian"/></a></span> |
<span><a href="/?locale=cn"><img src="/images/languages/CN.png"  alt="Gisgraphy in Chinese"/></a></span> |
<span><a href="/?locale=JP"><img src="/images/languages/JP.png"  alt="Gisgraphy in Japanese"/></a></span>
<span><a href="/?locale=nl"><img src="/images/languages/NL.png"  alt="Gisgraphy in Deutsh"/></a></span> |
<span><a href="/?locale=pt"><img src="/images/languages/PT.png"  alt="Gisgraphy in portuguese"/></a></span> |
<span><a href="/?locale=no"><img src="/images/languages/NO.png"  alt="Gisgraphy in Norwegian"/></a></span> |
<span><a href="/?locale=tr"><img src="/images/languages/TR.png"  alt="Gisgraphy in Turkish"/></a></span> |
<span><a href="/?locale=kr"><img src="/images/languages/KR.png"  alt="Gisgraphy in Korean"/></a></span> |
<br/><br/> <a href="mailto:davidmasclet@gisgraphy.com?subject=help for translation">We need help for translation</a>
</div>
<script type="text/javascript">
_uacct = "<%= com.gisgraphy.domain.valueobject.GisgraphyConfig.googleanalytics_uacctcode %>";
urchinTracker();
</script>
</body>
</html>