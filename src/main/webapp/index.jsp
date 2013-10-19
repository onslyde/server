<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--<c:import url="/index.html"></c:import>--%>
<%--<jsp:include page="/index.html">--%>
<%--<jsp:param name="#!" value="/analytics"/>--%>
<%--</jsp:include>--%>

<%
//    System.out.println("servlet path= " + request.getServletPath());
//    System.out.println("request URL= " + request.getRequestURL());
//    System.out.println("request URI= " + request.getRequestURI());
//    System.out.println("getQueryString= " + request.getQueryString());
    String url = "/index.html#!" + request.getRequestURI() + "?" + request.getQueryString();
%>
<c:redirect url="<%=url%>"/>