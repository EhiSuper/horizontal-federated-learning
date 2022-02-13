<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.ExperimentRound" %>

<%
    String dataPoints = null;
    if (request.getAttribute("round") != null) {
        Gson gsonObj = new Gson();
        Map<Object, Object> map = null;
        List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
        ExperimentRound round = (ExperimentRound) request.getAttribute("round");
        List<List<Double>> centers = round.getAlgorithmRound().getCenters();

        for (List<Double> center : centers) {
            map = new HashMap<Object, Object>();
            map.put("x", center.get(Integer.parseInt((String) request.getAttribute("firstFeature"))));
            map.put("y", center.get(Integer.parseInt((String) request.getAttribute("secondFeature"))));
            list.add(map);
        }

        dataPoints = gsonObj.toJson(list);
    }
%>

<!DOCTYPE HTML>
<html>
<head>
    <title>Home Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript">
        window.onload = function () {

            <% if(request.getAttribute("round")!=null){%>
            var chart = new CanvasJS.Chart("chartContainer", {
                animationEnabled: false,
                theme: "light2",
                title: {
                    text: "Dinamic distribution of the centers"
                },
                subtitles: [{
                    text: ""
                }],
                axisY: {
                    title: "Second feature",
                    includeZero: true
                },
                axisX: {
                    title: "First feature"
                },
                data: [{
                    type: "scatter",
                    xValueFormatString: "#,##0.000",
                    yValueFormatString: "#,##0.000",
                    toolTipContent: "<b>First Feature:</b> {x} <br><b>Second feature:</b> {y}",
                    dataPoints: <%out.print(dataPoints);%>
                }]
            });
            chart.render();
            <%}%>
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
        <label for="name">Name: </label><input id="name" type="text" name="name" value="Primo Esperimento"><br>
        <label for="dataset">Dataset: </label><input id="dataset" type="text" name="dataset"
                                                     value="https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff"><br>
        <label for="numFeatures">Features: </label><input id="numFeatures" type="number" name="numFeatures"
                                                          value="3"><br>
        <label for="maxNumberRounds">Maximum number of rounds: </label><input id="maxNumberRounds" type="number"
                                                                              name="maxNumberRounds" value="10"><br>
        <label for="numClusters">Clusters: </label><input id="numClusters" type="number" name="numClusters"
                                                          value="3"><br>
        <label for="distance">Distance: </label><input id="distance" type="text" name="distance" value="numba_norm"><br>
        <label for="epsilon">Epsilon: </label><input id="epsilon" type="number" step="any" name="epsilon"
                                                     value="0.05"><br>
        <label for="normFn">Norm Fn: </label><input id="normFn" type="text" name="normFn" value="norm_fro"><br><br>
        <label for="firstFeature">First feature: </label><input id="firstFeature" type="text" name="firstFeature"
                                                                value="0"><br>
        <label for="secondFeature">Second feature: </label><input id="secondFeature" type="text"
                                                                  name="secondFeature" value="1"><br>
        <button type="submit">Submit</button>
        <br>
    </form>
</div>
<div id="graph">
    <div id="chartContainer" style="height: 370px; width: 100%;"></div>
    <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</div>
</body>
</html>
