<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<% List<Experiment> listExperiment = (List<Experiment>) request.getAttribute("listExperiment"); %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Page</title>
    <style>
        <%@include file="../../style/css/style.css" %>
    </style>
</head>
<body>
<div id="menu">
    <button><a href="<%=request.getContextPath()%>/Home">Home</a></button>
    <button><a href="<%=request.getContextPath()%>/History">History</a></button>
    <button><a href="<%=request.getContextPath()%>/Settings">Settings</a></button>
    <button><a href="<%=request.getContextPath()%>/Logout">Logout</a></button>
</div>
<div id="options">
    <form action="<%=request.getContextPath()%>/History" method="post">
        <label for="filter">Choose a filter:</label>
        <div>
            <div>
                <input type="radio" id="radioAll" name="user" value="all" checked>
                <label for="radioAll">All Experiments</label>
                <input type="radio" id="radioMine" name="user" value="my">
                <label for="radioMine">My Experiments</label>
            </div>
            <select name="filter" id="filter">
                <option value="name">Name</option>
                <option value="dataset">Dataset</option>
                <option value="Algorithm:name">Algorithm</option>
            </select>
            <input type="text" name="value" id="value">
        </div>
        <button type="submit">Search</button>
        <br>
    </form>
</div>
<br>
<div id="result">
    <table>
        <tr>
            <td>Name</td>
            <td>Algorithm</td>
            <td>Dataset</td>
            <td>NumFeatures</td>
            <td>Mode</td>
            <td>Creation Date</td>
            <td>Last Update Date</td>
            <td>User</td>
            <td></td>
        </tr>
        <% for (Experiment experiment : listExperiment) { %>
        <tr>
            <td><%= experiment.getName()%>
            </td>
            <td><%= experiment.getAlgorithm().getName()%>
            </td>
            <td><%= experiment.getDataset()%>
            </td>
            <td><%= experiment.getNumFeatures()%>
            </td>
            <td><%= experiment.getMode()%>
            </td>
            <td><%= experiment.getCreationDate().toString()%>
            </td>
            <td><%= experiment.getLastUpdateDate().toString()%>
            </td>
            <% if (experiment.getUser() != null) { %>
            <td><%= experiment.getUser().getUsername()%>
            </td>
            <% } %>
            <td><a href="<%= request.getContextPath()%>/ExperimentInfo?id=<%=experiment.getId()%>">
                <button>Show Experiment</button>
            </a></td>
        </tr>
        <% } %>
    </table>
</div>
</body>
</html>
