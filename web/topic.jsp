<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@include file="WEB-INF/jspf/include.jspf" %>
<%

    Topic topic;
    try {
        topic = Topic.find(NumberUtils.toInt(request.getParameter("tid")));
    } catch (Exception ex) {
        out.println(ex);
        return;
    }

    TopicService topicService = EnvUtils.getEnv().getTopicService();
    Pager<Post> pager;
    int pageNo = NumberUtils.toInt(request.getParameter("pageNo"), 1);
    int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), 5);

    if (pageNo == 1) {
        pager = topicService.getTopicPage1(topic, pageSize);
    } else {
        pager = topicService.getPosts(topic, pageNo, pageSize);
    }

    pageContext.setAttribute("topic", topic);
    pageContext.setAttribute("pager", pager);
    pageContext.setAttribute("postList", pager.getResultList());

%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>论坛架构测试 - 主题页</title>

    <%@include file="WEB-INF/jspf/style.jspf" %>
    <%@include file="WEB-INF/jspf/script.jspf" %>
    <style type="text/css">

    </style>
    <script type="text/javascript">
        var ubbImgWidth = 600;
        function aw_(obj) {
            if (ubbImgWidth && obj.width > ubbImgWidth) {
                obj.width = ubbImgWidth;
            }
        }

    </script>
</head>
<body>
<div class="container">
    <h1>${topic.title}</h1>
    <%@include file="WEB-INF/jspf/nav.jspf" %>
    <div>
        <c:forEach var="forum" items="${topic.forum.route}">
            <a href="forum.jsp?fid=${forum.forumId}">${forum.name}</a> &gt;
        </c:forEach>
    </div>

    <div style="text-align:right;margin: 10px 0 10px 0" class="pagebar">
        <tags:dz-pager>${pager.pageNo},${pager.pageSize},${pager.total}</tags:dz-pager></div>

    <table class="table table-bordered table-striped">
        <thead>
        <tr>
            <th style="width:128px;">作者</th>
            <th>内容</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="post" items="${postList}">
            <tr>
                <td style="height:180px;vertical-align:top;text-align:center;"><img
                        src="<c:url value='/images/facet.gif' />"
                        alt="${post.author.nickname}"/><br/>${post.author.name}</td>
                <td style="vertical-align:top;line-height:1.8em;">
                    <div style="float:right;">${post.floor} 楼</div>
                    <div style="margin-right:88px;"> ${f:ubb2html(post.message)} </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@include file="WEB-INF/jspf/foot.jspf" %>
</div>
</body>
</html>
