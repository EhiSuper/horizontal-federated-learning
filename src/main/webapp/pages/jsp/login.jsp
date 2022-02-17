<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Login</h1>
<% if(request.getAttribute("error") != null){ %>
    <p><%=request.getAttribute("error")%></p>
<% } %>
<form action="<%=request.getContextPath()%>/Login" method="post">
    <div><label for="username">Username: </label> <input type="text" name="username" id="username" placeholder="username" required></div>
    <div><label for="password">Password: </label> <input type="password" name="password" id="password" placeholder="password" required></div>
    <div><button type="submit">Login</button></div>
</form>
</body>
</html>
