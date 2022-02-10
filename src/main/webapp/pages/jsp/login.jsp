<%--
  Created by IntelliJ IDEA.
  User: antonio
  Date: 07/02/22
  Time: 21:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Login</h1>
<form action="<%=request.getContextPath()%>/Login" method="post">
    <label for="username">Username: </label> <input type="text" name="username" id="username" placeholder="username" required>

    <label for="password">Password: </label> <input type="password" name="password" id="password" placeholder="password" required>

    <button type="submit">Login</button>
</form>
</body>
</html>
