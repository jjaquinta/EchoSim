<%@page import="java.net.URLEncoder"%>
<html>
<head><title>Handle Login</title></head>
<body>
Access Token: <%= request.getParameter("access_token") %><br/>
Info: <a href="https://api.amazon.com/auth/o2/tokeninfo?access_token=<%= URLEncoder.encode(request.getParameter("access_token"), "UTF-8") %>">tokeninfo</a>
</body>
</html>