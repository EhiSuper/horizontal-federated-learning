<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment" %><%--
  Created by IntelliJ IDEA.
  User: antonio
  Date: 07/02/22
  Time: 21:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Experiment experiment = (Experiment) request.getAttribute("experiment"); %>
<!DOCTYPE html>
<html>
<head>
    <title>Experiment Page</title>
</head>
<body>
<div id="menu">
    <button><a href="<%=request.getContextPath()%>/Home">Home</a></button>
    <button><a href="<%=request.getContextPath()%>/History">History</a></button>
    <button><a href="<%=request.getContextPath()%>/Settings">Settings</a></button>
    <button><a href="<%=request.getContextPath()%>/Logout">Logout</a></button>
</div>
<div id="options">
    <form action="<%=request.getContextPath()%>/Experiment">
        <label for="name">Name: </label><input id="name" value="<%=experiment.getName()%>"><br>
        <label for="dataset">Dataset: </label><input id="dataset" type="file" value="<%=experiment.getDataset()%>"><br>
        <label for="numFeatures">Number of Features: </label><input id="numFeatures" type="number" value="<%=experiment.getNumFeatures()%>"><br>
        <label for="mode">Mode: </label><input id="mode" type="number" value="<%=experiment.getMode()%>"><br>
        <label for="creationDate">Creation Date: </label><input id="creationDate" type="date" value="<%=experiment.getCreationDate()%>" readonly><br>
        <label for="lastUpdateDate">Last Update Date: </label><input id="lastUpdateDate" type="date" readonly><%=experiment.getLastUpdateDate()%><br>
        <label for="user">User: </label><input id="user" readonly><br>
        <label for="numRounds">Number of rounds: </label><input id="numRounds" type="number" readonly><br>
        <label for="maxNumRounds">Maximum number of rounds: </label><input id="maxNumRounds" type="number"><br>
        <label for="numCrashes">Number of crashes: </label><input id="numCrashes" type="number" readonly><br>
        <label for="numClients">Number of clients: </label><input id="numClients" type="number" readonly><br>
        <label for="numMinClients">Minimum number of clients: </label><input id="numMinClients" type="number"><br>
        <label for="clientsHostnames">Clients hostnames: </label><input id="clientsHostnames" readonly><br>
        <label for="randomClients">Random clients: </label><input id="randomClients"><br>
        <label for="randomClients">Random clients Seed: </label><input id="randomClientsSeed" type="number"><br>
        <label for="timeout">Timeout: </label><input id="timeout" type="number"><br>
        <label for="maxAttemptsClientCrash">Max Attempt Clients Crash: </label><input id="maxAttemptsClientCrash" type="number" readonly><br>
        <label for="maxAttemptsServerCrash">Max Attempt Server Crash: </label><input id="maxAttemptsServerCrash" type="number" readonly><br>
        <label for="maxAttemptsOverallCrash">Max Attempt Overall Crash: </label><input id="maxAttemptsOverallCrash" type="number" readonly><br>
        <% if(experiment.getName().equals("KMeans")){ %>
            <label for="numClusters">Number of Clusters: </label><input id="numClusters" type="number"><br>
            <label for="distance">Distance: </label><input id="distance"><br>
            <label for="epsilon">Epsilon: </label><input id="epsilon" type="number"><br>
            <label for="seedCenters">Seed Centers: </label><input id="seedCenters" type="number"><br><br>
            <label for="normFn">Norm Fn: </label><input id="normFn"><br><br>
        <% } %>
        <button type="submit">Submit</button>
        <br>
    </form>
</div>
</body>
</html>
