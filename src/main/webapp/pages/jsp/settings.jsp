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
    <title>Settings</title>
</head>
<body>
<% if (request.getAttribute("error") != null) { %>
<p><%=request.getAttribute("error")%>
</p>
<% } %>

<div id="menu">
    <button><a href="<%=request.getContextPath()%>/Home">Home</a></button>
    <button><a href="<%=request.getContextPath()%>/History">History</a></button>
    <button><a href="<%=request.getContextPath()%>/Settings">Settings</a></button>
    <button><a href="<%=request.getContextPath()%>/Logout">Logout</a></button>
</div>

<div>
    <form action="<%=request.getContextPath()%>/Settings" method="post">
        <div><label for="username">Username: </label> <input type="text" name="username" id="username"
                                                             placeholder="username" required
                                                             value="<%=request.getAttribute("username")%>" readonly></div>

        <div><label for="firstName">First Name: </label> <input type="text" name="firstName" id="firstName"
                                                                placeholder="First Name" required
                                                                value="<%=request.getAttribute("firstName")%>"></div>

        <div><label for="lastName">Last Name: </label> <input type="text" name="lastName" id="lastName"
                                                              placeholder="Last Name" required
                                                              value="<%=request.getAttribute("lastName")%>"></div>

        <div><label for="password">Password: </label> <input name="password" id="password"
                                                             placeholder="password" required
                                                             value="<%=request.getAttribute("password")%>"></div>

        <div><label for="confirmPassword">Confirm Password: </label> <input name="confirmPassword"
                                                                            id="confirmPassword"
                                                                            placeholder="Confirm Password" required
                                                                            value="<%=request.getAttribute("confirmPassword")%>">
        </div>

        <div>
            <button type=" submit">Change Settings</button>
        </div>
    </form>
</div>

</body>
</html>
