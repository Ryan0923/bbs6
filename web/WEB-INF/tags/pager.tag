<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@tag pageEncoding="UTF-8" %><%
String uri = request.getHeader("uri");
if (uri == null) {
	uri = request.getContextPath()+request.getServletPath();
}
String url = uri + "?"+(request.getQueryString()!=null?request.getQueryString():"").replaceAll("&*(pageNo|pageSize)=[^&]*","");
request.setAttribute("url", url);
%>
<c:set var="numbers"><jsp:doBody/></c:set>
<c:set var="numbers" value="${fn:split(numbers,',')}"/>
<c:set var="pageNo" value="${1*numbers[0]}"/>
<c:set var="pageSize" value="${1*numbers[1]}"/>
<c:set var="total" value="${1*numbers[2]}"/>
<c:set var="pageTotal">${(total-1)/pageSize+1}</c:set>
<c:set var="pageTotal" value="${fn:substring(pageTotal,0,fn:indexOf(pageTotal,'.'))}"/>

<c:if test="${pageNo > 3}">
<a title="首页" href="${url}&pageNo=${1}&pageSize=${pageSize }">1...</a>
</c:if>
<c:if test="${pageNo > 1}">
<a title="上一页" href="${url}&pageNo=${pageNo > 1 ? pageNo-1 : 1}&pageSize=${pageSize }">上一页</a>
</c:if>
<c:forEach var="n" begin="${pageNo > 2 ? pageNo-2 : 1}" end="${pageNo < pageTotal ? (pageTotal - pageNo > 6 ? pageNo + 6 : pageTotal) : pageNo}">
	<c:if test="${n != pageNo}"><a href="${url}&pageNo=${n}&pageSize=${pageSize }">${n}</a></c:if>
	<c:if test="${n == pageNo}"><span>${n}</span></c:if>
</c:forEach>
<c:if test="${pageNo < pageTotal - 6}">
<a title="末页" href="${url}&pageNo=${pageTotal}&pageSize=${pageSize }">...${pageTotal}</a>
</c:if>
<c:if test="${pageNo < pageTotal}">
<a title="下一页" href="${url}&pageNo=${pageNo < pageTotal ? pageNo+1 : pageTotal}&pageSize=${pageSize }">下一页</a>
</c:if>
