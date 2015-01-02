<%@ include file="/common/taglibs.jsp"%>
<% response.setStatus(404); %>
<page:applyDecorator name="ui">

<head>
    <title><fmt:message key="404.title"/></title>
    <meta name="heading" content="<fmt:message key='404.title'/>"/>
</head>

<p>
    <fmt:message key="404.message">
        <fmt:param><c:url value="/"/></fmt:param>
    </fmt:message>
    
    
    
    <style type="text/css">
  #goog-wm { }
  #goog-wm h3.closest-match { }
  #goog-wm h3.closest-match a { }
  #goog-wm h3.other-things { }
  #goog-wm ul li { }
  #goog-wm li.search-goog { display: block; }
</style>
<script type="text/javascript">
  var GOOG_FIXURL_LANG = 'fr';
  var GOOG_FIXURL_SITE = 'http://www.gisgraphy.com/';
</script>
<script type="text/javascript" 
    src="http://linkhelp.clients.google.com/tbproxy/lh/wm/fixurl.js"></script>
</p>
</page:applyDecorator>