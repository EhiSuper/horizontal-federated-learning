<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign up</title>
</head>
<body>
<h1>Sign up</h1>
<% if (request.getAttribute("error") != null) { %>
    <p><%=request.getAttribute("error")%> </p>
<% } %>
<form action="<%=request.getContextPath()%>/Signup" method="post">
    <div><label for="username">Username: </label> <input type="text" name="username" id="username"
                                                         placeholder="username" required></div>
    <div><label for="firstName">First Name: </label> <input type="text" name="firstName" id="firstName"
                                                            placeholder="First Name" required></div>
    <div><label for="lastName">Last Name: </label> <input type="text" name="lastName" id="lastName"
                                                          placeholder="Last Name" required></div>
    <div><label for="password">Password: </label> <input type="password" name="password" id="password"
                                                         placeholder="password" required></div>
    <div><label for="confirmPassword">Confirm Password: </label> <input type="password" name="confirmPassword"
                                                                        id="confirmPassword"
                                                                        placeholder="Confirm Password" required></div>
    <div>
        <button type="submit">Sign up</button>
    </div>
</form>
</body>
</html>
