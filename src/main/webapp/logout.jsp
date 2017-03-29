<%@ include file="/common/taglibs.jsp"%>
<%@ page import="javax.servlet.http.Cookie" %>

<%
if (request.getSession(false) != null) {
    session.invalidate();
}
Cookie terminate = new Cookie("ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE", null);
String contextPath = request.getContextPath();
terminate.setPath(contextPath != null && contextPath.length() > 0 ? contextPath : "/");
terminate.setMaxAge(0);
response.addCookie(terminate);
%>

<c:redirect url="/mainMenu.html"/>