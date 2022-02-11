<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm" %><%--
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
    <form action="<%=request.getContextPath()%>/Experiment">
        <label for="name">Name: </label><input id="name" value="<%=experiment.getName()%>"><br>
        <label for="dataset">Dataset: </label><input id="dataset" type="file" value="<%=experiment.getDataset()%>"><br>
        <label for="numFeatures">Number of Features: </label><input id="numFeatures" type="number" value="<%=experiment.getNumFeatures()%>"><br>
        <label for="mode">Mode: </label><input id="mode" type="number" value="<%=experiment.getMode()%>"><br>
        <label for="creationDate">Creation Date: </label><input id="creationDate" type="date" value="<%=experiment.getCreationDate()%>" readonly><br>
        <label for="lastUpdateDate">Last Update Date: </label><input id="lastUpdateDate" type="date" value="<%=experiment.getLastUpdateDate()%>" readonly><br>
        <label for="user">User: </label><input id="user" value="<%=experiment.getUser().getUsername()%>" readonly><br>
        <label for="numRounds">Number of rounds: </label><input id="numRounds" type="number" value="<%=experiment.getNumRounds()%>" readonly><br>
        <label for="maxNumRounds">Maximum number of rounds: </label><input id="maxNumRounds" type="number"value="<%=experiment.getMaxNumRounds()%>" ><br>
        <label for="numCrashes">Number of crashes: </label><input id="numCrashes" type="number" value="<%=experiment.getNumCrashes()%>" readonly><br>
        <label for="numClients">Number of clients: </label><input id="numClients" type="number" value="<%=experiment.getNumClients()%>" readonly><br>
        <label for="numMinClients">Minimum number of clients: </label><input id="numMinClients" type="number" value="<%=experiment.getNumMinClients()%>"><br>
        <label for="clientsHostnames">Clients hostnames: </label><input id="clientsHostnames" value="<%=experiment.getClientsHostnames().toString()%>"readonly><br>
        <label for="randomClients">Random clients: </label><input id="randomClients" value="<%=experiment.getRandomClients()%>"><br>
        <label for="randomClients">Random clients Seed: </label><input id="randomClientsSeed" type="number" value="<%=experiment.getRandomClientsSeed()%>"><br>
        <label for="timeout">Timeout: </label><input id="timeout" type="number" value="<%=experiment.getTimeout()%>"><br>
        <label for="maxAttemptsClientCrash">Max Attempt Clients Crash: </label><input id="maxAttemptsClientCrash" type="number" value="<%=experiment.getMaxAttemptsClientCrash()%>" readonly><br>
        <label for="maxAttemptsServerCrash">Max Attempt Server Crash: </label><input id="maxAttemptsServerCrash" type="number" value="<%=experiment.getMaxAttemptsServerCrash()%>" readonly><br>
        <label for="maxAttemptsOverallCrash">Max Attempt Overall Crash: </label><input id="maxAttemptsOverallCrash" type="number" value="<%=experiment.getMaxAttemptsOverallCrash()%>" readonly><br>
        <% if(experiment.getAlgorithm().getName().equals("KMeans")){
            KMeansAlgorithm algorithm = (KMeansAlgorithm) experiment.getAlgorithm();
        %>
            <label for="numClusters">Number of Clusters: </label><input id="numClusters" type="number" value="<%=algorithm.getNumClusters()%>"><br>
            <label for="distance">Distance: </label><input id="distance" value="<%=algorithm.getDistance()%>"><br>
            <label for="epsilon">Epsilon: </label><input id="epsilon" type="number" value="<%=algorithm.getEpsilon()%>"><br>
            <label for="seedCenters">Seed Centers: </label><input id="seedCenters" type="number" value="<%=algorithm.getSeedCenters()%>"><br><br>
            <label for="normFn">Norm Fn: </label><input id="normFn" value="<%=algorithm.getNormFn()%>"><br><br>
        <% } %>
        <%-- oppure ? --%>
        <button type="submit" formaction="<%=request.getContextPath()%>/Experiment?action=edit">Edit Experiment</button>
        <button type="submit" formaction="<%=request.getContextPath()%>/Experiment?action=delete">Delete Experiment</button>
        <br>
    </form>
</div>
</body>
</html>
