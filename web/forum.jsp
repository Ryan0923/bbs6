<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@include file="WEB-INF/jspf/include.jspf" %>
<%

    Forum forum = Forum.find(NumberUtils.toLong(request.getParameter("fid"), 1));

    ForumService forumService = EnvUtils.getEnv().getForumService();
    List<Forum> forumList = forumService.listChildForums(forum.getForumId());

    pageContext.setAttribute("forumList", forumList);
    pageContext.setAttribute("forum", forum);

    int pageNo = NumberUtils.toInt(request.getParameter("pageNo"), 1);
    int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), 10);

    Pager<Topic> pager;
    if (pageNo == 1) {
        pager = forumService.getForumPage1(forum, pageSize);
    } else {
        pager = forumService.listTopics(forum, null, null, null, pageNo, pageSize);
    }

    pageContext.setAttribute("pager", pager);
    pageContext.setAttribute("topicList", pager.getResultList());
%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>论坛架构测试 - 版块页</title>
    <%@include file="WEB-INF/jspf/style.jspf" %>
    <%@include file="WEB-INF/jspf/script.jspf" %>
</head>
<body>
<div class="container">
    <h1>${forum.name}</h1>
    <%@include file="WEB-INF/jspf/nav.jspf" %>

    <div style="border:1px solid gray;padding:5px;margin-top: 5px">
        <c:forEach var="forum" items="${forum.route}">
            <a href="forum.jsp?fid=${forum.forumId}">${forum.name}</a> &gt;
        </c:forEach>
    </div>
    <div style="border:1px solid gray;padding:5px;margin-top: 5px" id="child-forum">
        <c:forEach var="forum" items="${forumList}">
			<span style="width:150px;display:inline-block;">
				<a href="forum.jsp?fid=${forum.forumId}">${forum.name}</a>
			</span>
        </c:forEach>
    </div>
    <div style="text-align:right;margin: 10px 0 10px 0" class="pagebar">
        <tags:dz-pager>${pager.pageNo},${pager.pageSize},${pager.total}</tags:dz-pager>
    </div>
    <table class="table table-bordered table-striped">
        <thead>
        <tr>
            <th>topicId</th>
            <th>title</th>
            <th>author</th>
            <th>createAt</th>
            <th>lastPostAt</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="topic" items="${topicList}">
            <tr>
                <td>${topic.topicId}</td>
                <td><a href="topic.jsp?tid=${topic.topicId}">${topic.title}</a></td>
                <td>${topic.author.name}</td>
                <td>${topic.createAt}</td>
                <td>${topic.lastPostAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@include file="WEB-INF/jspf/foot.jspf" %>
</div>
<script type="text/javascript">
    function remove_blank_forum() {
        var child_forum = $("#child-forum");
        var text = child_forum.html();
        if (text.length == 1) {
            child_forum.hide();
        }
    }

    $(function () {
        remove_blank_forum();
    });
</script>
</body>
</html>
