<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<title>IOS RESPONSE</title>
<script type='text/javascript'>
	function getResponse() {
		var paramString = document.getElementById("parameters").innerHTML;
		return paramString;
	}

	function transactionCompleted() {
		window.location = 'ios:webToNativeCall';
	}
	// Dynamically call/invoke
	window.onload = transactionCompleted;
</script>
</head>
<body style='overflow-x: hidden; overflow-y: hidden;'>
	<%
		StringBuilder requestString = new StringBuilder();
		Map<String, String> requestMap = new HashMap<String, String>();
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			String key = entry.getKey();
			requestString.append(key);
			requestString.append("=");
			requestString.append(request.getParameter(key));
			requestString.append("~");
		}
	%>
	<div id="parameters" style="display: none"><%=requestString.toString()%></div>
</body>
</html>