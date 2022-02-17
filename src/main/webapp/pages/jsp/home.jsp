<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% int numClients = 0;
    if (request.getAttribute("numClients") != null) {
        numClients = (int) request.getAttribute("numClients");
    }
%>
<!DOCTYPE HTML>
<html>
<head>
    <title>Home Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <style>
        <%@include file="../../style/css/style.css" %>
    </style>
    <script type="text/javascript">
        function showForm() {
            let algorithmValue = document.getElementById("algorithm").value;
            document.getElementById(algorithmValue).style.display = "block";
        }
    </script>
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
        <label for="name">Name: </label><input id="name" type="text" name="name" required><br>
        <label for="dataset">Dataset: </label><input id="dataset" type="text" name="dataset"
                                                     value="https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff"
                                                     required><br>
        <label for="numFeatures">Number of features: </label><input id="numFeatures" type="number" name="numFeatures"
                                                                    value="3" required><br>
        <label for="numMinClients">Number of clients: </label>
        <input id="numMinClients" type="number" name="numMinClients" min="1" max="<%=numClients%>"
               value="<%=numClients%>" required> / <%=numClients%><br>
        <label for="randomClients">Different clients at each round: </label>
        <select name="randomClients" id="randomClients" required>
            <option value="false">false</option>
            <option value="true">true</option>
        </select>
        <br>
        <label for="timeout">Timeout: </label><input id="timeout" type="number" name="timeout" value="25000" min="5000"
                                                     required><br>
        <label for="algorithm">Select the algorithm: </label>
        <select name="algorithm" id="algorithm" onchange="showForm()" required>
            <option disabled selected value> -- select an algorithm --</option>
            <option value="KMeans">KMeans</option>
        </select>
        <div id="KMeans" style="background-color: floralwhite; border-color: aquamarine; display: none">
            <label for="numClusters">Clusters: </label><input id="numClusters" type="number" name="numClusters"
                                                              value="3" required><br>
            <label for="distance">Distance: </label>
            <select name="distance" id="distance" required>
                <option value="numba_norm">numba_norm</option>
            </select>
            <br>
            <label for="epsilon">Epsilon: </label><input id="epsilon" type="number" name="epsilon" value="0.05"
                                                         step="0.01" required><br>
            <label for="seedCenters">Seed centers: </label><input id="seedCenters" type="number" name="seedCenters"
                                                                  value="0" required><br>
            <label for="normFn">Norm Fn: </label>
            <select name="normFn" id="normFn" required>
                <option value="norm_fro">norm_fro</option>
            </select>
        </div>
        <br>
        <button type="submit" name="run">Run</button>
        <br>
    </form>
</div>
<div id="log" style="width:1400px">
    <% if (request.getAttribute("error") != null) { %>
    <p><%=request.getAttribute("error")%>
    </p>
    <% } %>
</div>
</body>
</html>