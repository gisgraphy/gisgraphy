<%@ include file="/common/taglibs.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
    <title><fmt:message key="mainMenu.title"/></title>
    <meta name="heading" content="<fmt:message key='mainMenu.heading'/>"/>
    <meta name="menu" content="MainMenu"/>
</head>


<%
com.gisgraphy.fulltext.FullTextSearchEngine fulltextSearchEngine = (com.gisgraphy.fulltext.FullTextSearchEngine) org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(session.getServletContext()).getBean("fullTextSearchEngine");

com.gisgraphy.importer.ImporterManager importerManager = (com.gisgraphy.importer.ImporterManager) org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(session.getServletContext()).getBean("importerManager");

%>
<% if (!fulltextSearchEngine.isAlive()) { %>
   <div class="tip redtip"> <fmt:message key="import.fulltextEngineNotReachable" ><fmt:param><%= fulltextSearchEngine.getURL() %></fmt:param></fmt:message></div>
<% } else {%>
<div class="tip greentip"> <fmt:message key="import.fulltextEngineReachable"/> ! </div>
<%  }%>

<% if (com.gisgraphy.domain.valueobject.GisgraphyConfig.googleMapAPIKey == null || "".equals(com.gisgraphy.domain.valueobject.GisgraphyConfig.googleMapAPIKey.trim())) { %>
   <div class="tip yellowtip"> <fmt:message key="config.googlemapapikey.empty" /></div>
<% } %>
<div class="separator"></div>
<p><fmt:message key="mainMenu.message"/></p>


<ul class="glassList">
<li><a href="http://www.gisgraphy.com/documentation/index.htm"><fmt:message key="global.read.docs" /></a></li>
	<% try { if (!importerManager.isAlreadyDone()) { %>
   <li>
        <a href="<c:url value='/admin/importconfirm.html'/>"><fmt:message key="menu.admin.import"/></a>
    </li>
<% } else {%>
 <li>
         <fmt:message key="import.already.done"/> 
    </li>
<%  } } catch (Exception e){
%>
<div class="tip yellowtip"><fmt:message key="import.metadatamissing"/></div>
<%
}
%>
	<li>
		<a href="<c:url value='/admin/editSearch.html'/>"><fmt:message key="global.crud.data.long"/></a>
	</li>
	<li>
		<a href="<c:url value='/admin/stats.html'/>"><fmt:message key="stats.title"/></a>
	</li>
	<li>
		<a href="mailto:davidmasclet@gisgraphy.com?subject=[contact_from_admin_page]"><fmt:message key="global.contact"/></a>
	</li>
  	<li>
        <fmt:message key="global.gohome"/>
    </li>
    
</ul>

<p><fmt:message key="premium.desc"/><br/></p>

<iframe src="http://www.gisgraphy.com/news/getnews.php?version=<fmt:message key="gisgraphy.version"/>" 
width="100%" height="100%" id="iframenews" frameborder="0" vspace="0" hspace="0" marginwidth="0" marginheight="0" scrolling="no" noresize
> </iframe>
