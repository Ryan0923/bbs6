<%@page contentType="text/html; charset=UTF-8" session="false"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@page import="java.util.Collection"
%><%@page import="cn.pconline.util.monitor.Monitor"
%><%
	String reset = request.getParameter("reset");
	if (reset != null) {
	    Monitor.reset();
	    response.sendRedirect("monitor.jsp");
	    return;
	}
	long startTime = Monitor.getStartTime();
	Collection cl = Monitor.getReport();
	pageContext.setAttribute("items", cl);
	pageContext.setAttribute("startTime", Monitor.dateFormat.format(new java.util.Date(startTime)));
%><html>
<head>
<style>
<!--
table {
    border-collapse:collapse;border-width:1px;border-color:silver;
}
td {
    font-size: 9pt;height: 20px;
}
th {
    font-size: 9pt;background-color:lightblue;height:20px;
}
.even {background-color:#EEEEEE;}
.odd {background-color:white;}
-->
</style>
<script src="../js/jquery-1.3.2.min.js"></script>
<script>
	$(document).ready(function() {
		$("#panel").hover(
			function(){},
			function(){ $(this).hide(); }
		);
	});
	function showInfo(obj, info) {
		var _this = $(obj);
		var pos = _this.offset();
		var panel = $("#panel");
		panel.val($(info).val());
		panel.css("top", pos.top).css("left", pos.left - 920).show();
	}
</script>
</head>
<body>
<textarea style="position:absolute;display:none;border:solid black 1px;top:0;left:0;font-size:12px;background-color:#ffffe8;"
 cols="150" rows="12" id="panel"></textarea>
<table border="1" width="100%">
<tr>
	<th>请求时间统计报告</th>
	<th colspan="2">统计开始时间</th>
	<td colspan="7">&nbsp;${startTime}</td>
	<td align="right">
		<a href="monitor.jsp?reset" onclick="return confirm('您确认要重新统计吗？重新统计会完全清除现在的统计数据，\n也许您需要保存一下页面后再重新统计。');">重新统计</a>
	</td>
</tr>
<tr>
	<th>URI</th>
	<th>次数/分</th>
	<th>总次数</th>
	<th>错次数</th>
	<th>0-1秒</th>
	<th>1-3</th>
	<th>3-5</th>
	<th>5-10</th>
	<th>10-20</th>
	<th>20+</th>
	<th>最慢10个请求</th>
</tr>
<c:forEach var="item" items="${items}" varStatus="st">
<tr class="${st.count%2==0?'even':'odd'}">
	<td>${item.uri}</td>
	<td>${item.perMinute}</td>
	<td>${item.count}</td>
	<td ${item.error==0?'':'bgcolor=red'}>${item.error}</td>
	<td>${item.count1}</td>
	<td>${item.count3}</td>
	<td ${item.count5==0?'':'bgcolor=#FFFF99'}>${item.count5}</td>
	<td ${item.count10==0?'':'bgcolor=yellow'} >${item.count10}</td>
	<td ${item.count20==0?'':'bgcolor=#FF9999'} >${item.count20}</td>
	<td ${item.count20x==0?'':'bgcolor=red'} >${item.count20x}</td>
	<td align="right" width="100">${item.maxDuration}
		<textarea style="display:none;" id="ta_${st.count}"><c:forEach var="bottom" items="${item.bottoms}"><c:if test="${!empty bottom[0]}">${bottom[0]},${bottom[1]},${bottom[2]},${bottom[3]},${bottom[4]}<%="\n"%></c:if></c:forEach></textarea>
		<input style="height: 16px;font-size: 12px;color: red" type="text" size=1 value='看' readonly="readonly"
		onmouseover="showInfo(this, '#ta_${st.count}');">
	</td>
</tr>
</c:forEach>
</table>
</body>
</html>