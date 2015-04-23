<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@include file="WEB-INF/jspf/include.jspf" %>
<%
    UserService userService = EnvUtils.getEnv().getUserService();
    Pager<User> pager = userService.pageAllUser(NumberUtils.toInt(request.getParameter("pageNo"), 1), 15);
    pageContext.setAttribute("userList", pager.getResultList());
    pageContext.setAttribute("pager", pager);
%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>论坛架构测试 - 用户列表</title>
    <%@include file="WEB-INF/jspf/style.jspf" %>
</head>

<body>
<div class="container">
    <h1>用户列表</h1>
    <table style="width: 450px" class="table table-bordered table-striped">
        <c:forEach var="user" items="${userList}" varStatus="st">
            <tr>
                <td>${st.count + pager.pageNo * pager.pageSize - pager.pageSize}</td>
                <td>${user.name}</td>
            </tr>
        </c:forEach>
    </table>


    <div><tags:dz-pager>${pager.pageNo},${pager.pageSize},${pager.total}</tags:dz-pager></div>
</div>
</body>

</html>
