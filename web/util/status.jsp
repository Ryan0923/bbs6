<%@page session="false" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<textarea cols="100" rows="25" style="font-size: 12px"><c:forEach items="${_envSet}" var="e">
${e.logString}</c:forEach></textarea>
