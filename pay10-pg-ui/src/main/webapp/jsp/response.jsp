
<%@page import="java.util.Map.Entry"%>
<%@page import="com.pay10.commons.util.Fields"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.pay10.commons.api.Hasher"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="java.util.Map"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Response Format</title>
</head>
<body>
<%
Map<String, String> requestMap = new HashMap<String, String>();

for(Entry<String, String[]> entry : request.getParameterMap().entrySet()){
	String key = entry.getKey();
	requestMap.put(key,request.getParameter(key));
%>
<%-- <div> Name <%= key %> &nbsp; Value: <%=request.getParameter(key)%></div> --%>

<div id=<%=key %>> Name <%= ESAPI.encoder().encodeForHTML(key) %> &nbsp;
 Value: <%=ESAPI.encoder().encodeForHTML(request.getParameter(key))%></div>
<%}%>

<%
 Fields fields = new Fields(requestMap);
//String hashRecieved = Hasher.getHash(fields);
fields.remove("HASH");
String generatedHash = "";
try{
	generatedHash = Hasher.getHash(fields);
}catch(Exception exception){
	
}
%>
Generated hash<span style="padding-left:20px">:&nbsp;<%=ESAPI.encoder().encodeForHTML(generatedHash) %></span> 
</body>
</html>