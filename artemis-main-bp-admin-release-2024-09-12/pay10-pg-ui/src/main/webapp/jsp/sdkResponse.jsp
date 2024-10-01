<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.pg.core.pageintegrator.Transaction"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>ANDROID RESPONSE</title>
</head>
<body>
	<%
		StringBuilder requestString = new StringBuilder();
		Map<String, String> requestMap = new HashMap<String, String>();
		Transaction transaction = new Transaction();
		requestString.append('{');
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			String key = entry.getKey();
			requestMap.put(key, request.getParameter(key));
			transaction.appendJsonField(key, request.getParameter(key), requestString);
		}
		requestString.append('}');
		requestString.deleteCharAt(1);
	%>
	<div id="parameters" style="display: none"><%=ESAPI.encoder().encodeForHTML(requestString.toString())%></div>
	<script>
		var map = document.getElementById("parameters").innerHTML;
		Android.process(map);
	</script>
</body>
</html>