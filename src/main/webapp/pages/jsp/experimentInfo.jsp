<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm" %>
<%--
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
    <title>Experiment Info Page</title>
</head>
<body>
<div id="menu">
    <button><a href="<%=request.getContextPath()%>/Home">Home</a></button>
    <button><a href="<%=request.getContextPath()%>/History">History</a></button>
    <button><a href="<%=request.getContextPath()%>/Settings">Settings</a></button>
    <button><a href="<%=request.getContextPath()%>/Logout">Logout</a></button>
</div>
<div id="options">
    <form action="<%=request.getContextPath()%>/ExperimentInfo" method="post">
        <button type="submit" name="action" value="back">Back</button><br>
        <input type="hidden" id="id" name="id" value="<%=experiment.getId()%>">
        <label for="name">Name: </label><input id="name" name="name" value="<%=experiment.getName()%>"><br>
        <label for="dataset">Dataset: </label><input id="dataset" name="dataset" type="text" value="<%=experiment.getDataset()%>"><br>
        <label for="numFeatures">Number of Features: </label><input id="numFeatures" name="numFeatures" type="number" value="<%=experiment.getNumFeatures()%>"><br>
        <label for="mode">Mode: </label><input id="mode" type="number" name="mode" value="<%=experiment.getMode()%>"><br>
        <label for="creationDate">Creation Date: </label><input id="creationDate" name="creationDate" type="text" value="<%=experiment.getCreationDate()%>" readonly><br>
        <label for="lastUpdateDate">Last Update Date: </label><input id="lastUpdateDate" name="lastUpdateDate" type="text" value="<%=experiment.getLastUpdateDate()%>" readonly><br>
        <label for="user">User: </label><input id="user" name="username" value="<%=experiment.getUser().getUsername()%>" readonly><br>
        <label for="numRounds">Number of rounds: </label><input id="numRounds" name="numRounds"  type="number" value="<%=experiment.getNumRounds()%>" readonly><br>
        <label for="maxNumRounds">Maximum number of rounds: </label><input id="maxNumRounds" name="maxNumRounds" type="number"value="<%=experiment.getMaxNumRounds()%>" ><br>
        <label for="numCrashes">Number of crashes: </label><input id="numCrashes" type="number" name="numCrashes"  value="<%=experiment.getNumCrashes()%>" readonly><br>
        <label for="numClients">Number of clients: </label><input id="numClients" type="number" name="numClients" value="<%=experiment.getNumClients()%>" readonly><br>
        <label for="numMinClients">Minimum number of clients: </label><input id="numMinClients" name="numMinClients"  type="number" value="<%=experiment.getNumMinClients()%>"><br>
        <label for="clientsHostnames">Clients hostnames: </label><input id="clientsHostnames" name="clientsHostnames" value="<%=String.join(",",experiment.getClientsHostnames())%>" readonly><br>
        <label for="randomClients">Random clients: </label><input id="randomClients" name="randomClients" value="<%=experiment.getRandomClients()%>"><br>
        <label for="randomClients">Random clients Seed: </label><input id="randomClientsSeed" name="randomClientsSeed"  type="number" value="<%=experiment.getRandomClientsSeed()%>"><br>
        <label for="timeout">Timeout: </label><input id="timeout" type="number" name="timeout" value="<%=experiment.getTimeout()%>"><br>
        <label for="maxAttemptsClientCrash">Max Attempt Clients Crash: </label><input id="maxAttemptsClientCrash" name="maxAttemptsClientCrash"  type="number" value="<%=experiment.getMaxAttemptsClientCrash()%>" readonly><br>
        <label for="maxAttemptsServerCrash">Max Attempt Server Crash: </label><input id="maxAttemptsServerCrash" name="maxAttemptsServerCrash" type="number" value="<%=experiment.getMaxAttemptsServerCrash()%>" readonly><br>
        <label for="maxAttemptsOverallCrash">Max Attempt Overall Crash: </label><input id="maxAttemptsOverallCrash" name="maxAttemptsOverallCrash"  type="number" value="<%=experiment.getMaxAttemptsOverallCrash()%>" readonly><br>
        <% if(experiment.getAlgorithm().getName().equals("KMeans")){
            KMeansAlgorithm algorithm = (KMeansAlgorithm) experiment.getAlgorithm();
        %>
            <label for="algorithmName">Algorithm: </label><input id="algorithmName" name="algorithmName" value="<%=algorithm.getName()%>"><br>
            <label for="numClusters">Number of Clusters: </label><input id="numClusters" name="numClusters" type="number" value="<%=algorithm.getNumClusters()%>"><br>
            <label for="distance">Distance: </label><input id="distance" name="distance"  value="<%=algorithm.getDistance()%>"><br>
            <label for="epsilon">Epsilon: </label><input id="epsilon" name="epsilon" value="<%=algorithm.getEpsilon()%>"><br>
            <label for="seedCenters">Seed Centers: </label><input id="seedCenters" name="seedCenters" type="number" value="<%=algorithm.getSeedCenters()%>"><br>
            <label for="normFn">Norm Fn: </label><input id="normFn" name="normFn" value="<%=algorithm.getNormFn()%>"><br><br>
        <% } %>
        <%-- oppure ? --%>
        <button type="submit" name="action" value="update">Edit Experiment</button>
        <button type="submit" name="action" value="delete">Delete Experiment</button>
        <br>
    </form>
</div>
</body>
</html>
