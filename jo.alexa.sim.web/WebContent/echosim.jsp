<!DOCTYPE html>
<html>
<head>
	<title>Echo Simulator</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="style.css" />
</head>
<body>
<script type="text/javascript" src="echosim.js"></script>
<script type="text/javascript">
endpoint = <%= request.getParameterMap().containsKey("endpoint") ? "'"+request.getParameter("endpoint")+"'" : "null" %>;
intents = <%= request.getParameterMap().containsKey("intents") ? "'"+request.getParameter("intents")+"'" : "null" %>;
utterances = <%= request.getParameterMap().containsKey("utterances") ? "'"+request.getParameter("utterances")+"'" : "null" %>;
userid = <%= request.getParameterMap().containsKey("userid") ? "'"+request.getParameter("userid")+"'" : "null" %>;
appid = <%= request.getParameterMap().containsKey("appid") ? "'"+request.getParameter("appid")+"'" : "null" %>;
</script>
<table>
<tr>
	<td valign="top"><img src='images/echo.png'/></td>
	<td valign="top">
		<h1>Amazon Echo Simulator</h1>
		<% if (request.getParameterMap().containsKey("title")) { %>
		<h2><%= request.getParameter("title") %></h2>
		<% } %>
		Alexa, &nbsp;<input type="text" id="text" onkeydown="if (event.keyCode == 13) doTalk();"/>&nbsp;<input type="button" value="&#x25b6;" onClick="doTalk();"/>
		<hr/>
		<span id="message"></span>
	</td>
</tr>
</table>
<!-- getLocalName = <%= request.getLocalAddr() %><br/>
getRemoteAddr = <%= request.getLocalName() %><br/>
getRemoteHost = <%= request.getRemoteAddr() %><br/>
getRemoteUser = <%= request.getRemoteHost() %><br/>
getRemoteUser = <%= request.getRemoteUser() %><br/>
getServerName = <%= request.getServerName() %><br/> -->
</body>
</html>
