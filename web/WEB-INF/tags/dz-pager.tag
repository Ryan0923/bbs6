<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
分页条。用法： <tags:pager showPageSizer='false|true'>当前页码,每页条数,总条数</tags:pager>
--%>
<%@attribute name="showPageSizer" %>
<%@attribute name="varPageNo" %><c:set var="varPageNo" value="${!empty varPageNo ? varPageNo : 'pageNo'}"/>
<%@attribute name="varPageSize" %><c:set var="varPageSize" value="${!empty varPageSize ? varPageSize : 'pageSize'}"/>
<style type="text/css">
    .PageInput{
        width: 50px;
    }
</style>
<c:set var="utf8_html">

<c:set var="url" value="<%=(request.getContextPath()+request.getServletPath()+"?"+(request.getQueryString()!=null?request.getQueryString():"").replaceAll("&*("+jspContext.findAttribute("varPageNo")+"|"+jspContext.findAttribute("varPageSize")+")=[^&]*",""))%>"/>

<c:set var="numbers"><jsp:doBody/></c:set>
<c:set var="numbers" value="${fn:split(numbers,',')}"/>

<c:set var="pageNo" value="${1*numbers[0]}"/>
<c:set var="pageSize" value="${1*numbers[1]}"/>
<c:set var="total" value="${1*numbers[2]}"/>
<c:set var="pageTotal">${(total-1)/pageSize+1}</c:set>
<c:set var="pageTotal" value="${fn:substring(pageTotal,0,fn:indexOf(pageTotal,'.'))}"/>

<span class=pages>共${total}条记录
<c:if test='${pageNo>1}'>
<A title=首页 href="${url}">首页</A>
<A title=上一页 href="${url}<c:if test='${pageNo>2}'>&${varPageNo}=${pageNo-1}</c:if>">上一页</A>
</c:if>
${pageNo}/${pageTotal}
<c:if test='${pageNo<pageTotal}'>
<A title=下一页 href="${url}&${varPageNo}=${pageNo+1}">下一页</A>
<A title=尾页 href="${url}&${varPageNo}=${pageTotal}">尾页</A>
</c:if>
转到：
<input class="PageInput">&nbsp;&nbsp;<input class="GoPage btn btn-primary" type="button" value="确定" onclick="location='${url}&${varPageNo}='+previousSibling.value"> </span>


</c:set><%=new String(((String)jspContext.getAttribute("utf8_html")).getBytes("iso-8859-1"),"utf8")%>