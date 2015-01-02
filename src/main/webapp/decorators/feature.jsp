<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ include file="/common/taglibs.jsp"%>
<%@ page session="false" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
    <head>
        <%@ include file="/common/meta.jsp" %>
        <title><decorator:title/> | <fmt:message key="webapp.name"/> Free GIS Services</title>

        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/ui/theme.css'/>" />

        <decorator:head/>
    </head>
<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.class" writeEntireProperty="true"/>>
    <div id="page">
        <div id="header" class="clearfix">
            <div id="branding">
   				 <h1 style="color:#333;font-size: 2em;"><img src="/images/logos/logo_70.png" style="vertical-align:middle;" alt="Free GIS Services"/><decorator:title/></h1>
    			 <div id="tagline"><fmt:message key="webapp.name"/> : <fmt:message key="webapp.tagline"/></div>
    			 <br/><br/>
    			 <div><a href="/ajaxfulltextsearch.html">Go to free Gisgraphy GIS services</a> | <a href="http://www.gisgraphy.com/">Go to Gisgraphy open source project</a></div>
			</div>
        </div>
		<div id="content" class="clearfix">
		<div class="divider"><div></div></div>
            <div id="main">
                <decorator:body/>
        </div>
        </div>
	
	        <div id="footer" class="clearfix">
             <div class="divider"><div></div></div>
    <span class="left"><fmt:message key="webapp.version"/> |
        <span id="validators">
            <a href="http://validator.w3.org/check?uri=referer">XHTML Valid</a> |
            <a href="http://jigsaw.w3.org/css-validator/validator-uri.html">CSS Valid</a>
        </span>
    </span>
   <span class="right">
        <a href="http://services.gisgraphy.com/public/donate.html" target="_blank"><img src="https://www.paypal.com/en_US/i/btn/btn_donate_SM.gif" alt="donate" class="donateBtn"/></a> | <a href="http://www.gisgraphy.com/free-access.htm"><fmt:message key="termsAndConditions"/></a> | <a href="<fmt:message key="company.url"/>"><fmt:message key="company.name"/> Project</a>  | <a href="mailto:<fmt:message key="company.mail"/>">Contact</a> | <a href="http://davidmasclet.gisgraphy.com" >Blog</a> 
    </span>

        </div>
    </div>
    <script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "<fmt:message key="googleanalytics._uacctcode"/>";
urchinTracker();
</script>
</body>
</html>