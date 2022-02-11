<%--
  Created by IntelliJ IDEA.
  User: antonio
  Date: 07/02/22
  Time: 21:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home Page</title>
</head>
<body>
<div id="menu">
    <button><a href="<%=request.getContextPath()%>/Home">Home</a></button>
    <button><a href="<%=request.getContextPath()%>/History">History</a></button>
    <button><a href="<%=request.getContextPath()%>/Settings">Settings</a></button>
    <button><a href="<%=request.getContextPath()%>/Logout">Logout</a></button>
</div>
<div id="options">
    <form action="<%=request.getContextPath()%>/Home" method="post">
        <label for="dataset">Dataset: </label><input id="dataset" type="text" name="dataset"><br>
        <label for="numFeatures">Features: </label><input id="numFeatures" type="number" name="features"><br>
        <label for="maxNumberRounds">Maximum number of rounds: </label><input id="maxNumberRounds" type="number"
                                                                              name="maxRounds"><br>
        <label for="numClusters">Clusters: </label><input id="numClusters" type="number" name="numClusters"><br>
        <label for="distance">Distance: </label><input id="distance" type="number" name="distance"><br>
        <label for="epsilon">Epsilon: </label><input id="epsilon" type="number" name="epsilon"><br>
        <label for="normFn">Norm Fn: </label><input id="normFn" type="number" name="normFn"><br><br>
        <button type="submit">Submit</button>
        <br>
    </form>
</div>
<div id="graph">

</div>
</body>
</html>
