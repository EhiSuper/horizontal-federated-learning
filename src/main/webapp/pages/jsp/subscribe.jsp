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

<script>
    function validate(){
        console.log("sono nella validate");
        if (document.getElementById("password").value != document.getElementById("confirmPassword").value) {
            window.alert("The confirm password is not equal to the previous password")
            return false;
        }
        else return true;
    }
</script>

<h1>Subscribe</h1>
<form action="<%=request.getContextPath()%>/Subscribe" method="post" onsubmit="return validate()">
    <label for="username">Username: </label> <input type="text" name="username" id="username" placeholder="username" required>

    <label for="firstName">First Name: </label> <input type="text" name="firstName" id="firstName" placeholder="First Name" required>

    <label for="lastName">Last Name: </label> <input type="text" name="lastName" id="lastName" placeholder="Last Name" required>

    <label for="password">Password: </label> <input type="password" name="password" id="password" placeholder="password" required>

    <label for="confirmPassword">Confirm Password: </label> <input type="password" name="confirmPassword" id="confirmPassword" placeholder="Confirm Password" required>

    <button type="submit">Login</button>
</form>
</body>
</html>
