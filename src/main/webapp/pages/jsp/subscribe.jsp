<%--
  Created by IntelliJ IDEA.
  User: antonio
  Date: 10/02/22
  Time: 12:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Subscribe</title>
</head>
<body>
<h1>Subscribe</h1>
<% if(request.getAttribute("error") != null){ %>
<p><%=request.getAttribute("error")%></p>
<% } %>
<form action="<%=request.getContextPath()%>/Subscribe" method="post" onsubmit="return validate()">
    <div><label for="username">Username: </label> <input type="text" name="username" id="username" placeholder="username" required></div>

    <div><label for="firstName">First Name: </label> <input type="text" name="firstName" id="firstName" placeholder="First Name" required></div>

    <div><label for="lastName">Last Name: </label> <input type="text" name="lastName" id="lastName" placeholder="Last Name" required></div>

    <div><label for="password">Password: </label> <input type="password" name="password" id="password" placeholder="password" required></div>

    <div><label for="confirmPassword">Confirm Password: </label> <input type="password" name="confirmPassword" id="confirmPassword" placeholder="Confirm Password" required></div>

    <div><button type="submit">Subscribe</button></div>
</form>
</body>
</html>
