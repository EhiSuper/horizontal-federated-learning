<%@ page isErrorPage="true" %>
<head>
    <style>
        <%@include file="../../style/css/style.css" %>
    </style>
</head>

<h2>Sorry, an exception occured!</h2>
<h3>Antonio, Fabio and Francesco are working on it!</h3>

<%= request.getAttribute("error") %>