<%@page contentType="text/html; charset=UTF-8"%>
<%@page session="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.*"%>
<%@page import="cn.pconline.util.monitor.*"%>
<%!
	double linerScore(double start, double end, double score, double value) {
		if (value <= start) return score;
		return score * (end - value) / (end - start);
	}
%>
<%
	/* 系统服务状况评价公式
	 5分钟平均响应时间(秒)    1 - 4      40 - 0
	 5分钟出错比例 >         1% - 20%	  50 - 0 // 由于是比率，要注意分母不能太小
	 当前超过10秒的请求次数   5 - 20      10 - 0
	 */

	List samples = MovingAverageMonitor.getReport();

	double minScore = 100.0;
	for (int i = 0; i < samples.size(); i ++) {
		double score = 0.0;
		MovingAverageCounter counter = (MovingAverageCounter)samples.get(i);
		double avg = counter.getFiveMinuteAverage();
		double errorRate = 0.0;

		// 至少有 10 次以上的请求统计比率才公平
		if (counter.getFiveMinuteCount() > 10 && counter.getFiveMinuteError() > 0) {
			errorRate = counter.getFiveMinuteError() * 1.0 / counter.getFiveMinuteCount();
		}
		score += linerScore(1000.0, 4000.0, 50.0, avg);
		score += linerScore(0.01,   0.20,   50.0, errorRate);
		if (minScore > score) minScore = score;
	}

	if ("monitor".equals(request.getParameter("method"))) {
		if (minScore < 60) {
			response.sendError(response.SC_INTERNAL_SERVER_ERROR, "系统得分：" + minScore);
		} else {
			out.println("系统得分：" + minScore);
		}
		return;
	}

	long startTime = Monitor.getStartTime();
	pageContext.setAttribute("startTime", Monitor.dateFormat.format(new java.util.Date(startTime)));
	pageContext.setAttribute("items", samples);
	pageContext.setAttribute("minScore", new Double(minScore));

	pageContext.setAttribute("now", Monitor.dateFormat.format(new java.util.Date()));
%>
<html>
<head>
<style>
<!--
table {
    border-collapse:collapse;border-width:1;border-color:lightgray;
}
td {
    font-size: 9pt;height: 20
}
th {
    font-size: 9pt;background-color:lightblue;height:20
}
.even {background-color:#EEEEEE;}
.odd {background-color:white;}
-->
</style>
</head>
<body>
<b>系统综合评价：<font color='${minScore < 60.0?"red":"green"}'>${minScore}</font></b>
<table border="1" width="100%">
<tr>
	<th >${startTime}</th>
	<th colspan="3">错误次数</th>
	<th colspan="3">请求次数</th>
	<th colspan="3">平均响应时间(毫秒)</th>
</tr>
<tr>
	<th>URI</th>
	<th>本分钟</th>
	<th>5 分钟</th>
	<th>15分钟</th>
	<th>本分钟</th>
	<th>5 分钟</th>
	<th>15分钟</th>
	<th>本分钟</th>
	<th>5 分钟</th>
	<th>15分钟</th>
</tr>
<c:forEach var="item" items="${items}" varStatus="st">
<tr class="${st.count%2==0?'even':'odd'}">
	<td>${item.name}</td>
	<td ${item.currentMinuteError==0?'':'bgcolor=red'}>${item.currentMinuteError}</td>
	<td ${item.fiveMinuteError==0?'':'bgcolor=red'}>${item.fiveMinuteError}</td>
	<td ${item.fifteenMinuteError==0?'':'bgcolor=red'}>${item.fifteenMinuteError}</td>
	<td>${item.currentMinuteCount}</td>
	<td>${item.fiveMinuteCount}</td>
	<td>${item.fifteenMinuteCount}</td>
	<td ${item.currentMinuteAverage>5000.0?(item.currentMinuteAverage>10000.0?'bgcolor=red':'bgcolor=yellow'):''}>${item.currentMinuteAverage}</td>
	<td ${item.fiveMinuteAverage>5000.0?(item.fiveMinuteAverage>10000.0?'bgcolor=red':'bgcolor=yellow'):''}>${item.fiveMinuteAverage}</td>
	<td ${item.fifteenMinuteAverage>5000.0?(item.fifteenMinuteAverage>10000.0?'bgcolor=red':'bgcolor=yellow'):''}>${item.fifteenMinuteAverage}</td>
</tr>
</c:forEach>

</table>
<div>当前时间：${now }</div>
</body>
</html>