<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.ExperimentRound" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithmRound" %>
<%@ page import="it.unipi.dsmt.horizontalFederatedLearning.entities.Client" %>

<%
    ArrayList<String> dataPoints = new ArrayList<>();
    ArrayList<String> normPoints = new ArrayList<>();
    ArrayList<Integer> numCrashes = new ArrayList<>();
    ArrayList<String> availableClients = new ArrayList<>();
    ArrayList<String> involvedClients = new ArrayList<>();
    int numRounds = 0;
    if (request.getAttribute("rounds") != null) {
        Gson gsonObj = new Gson();
        Map<Object, Object> map = null;
        List<ExperimentRound> roundList = (List<ExperimentRound>) request.getAttribute("rounds");
        List<Map<Object, Object>> normList = new ArrayList<Map<Object, Object>>();
        for(int i=0; i<(roundList.size() - 1); i++){
            List<Map<Object, Object>> centerList = new ArrayList<Map<Object, Object>>();
            if(!roundList.get(i).getLast()){
                List<List<Double>> centers = roundList.get(i).getAlgorithmRound().getCenters();
                List<Client> availableClientsRound = roundList.get(i).getClientsState();
                List<Client> involvedClientsRound = roundList.get(i).getInvolvedClients();
                int numcrashesRound = roundList.get(i).getNumCrashes();
                for (List<Double> center : centers) {
                    map = new HashMap<Object, Object>();
                    map.put("x", center.get(Integer.parseInt((String) request.getAttribute("firstFeature"))));
                    map.put("y", center.get(Integer.parseInt((String) request.getAttribute("secondFeature"))));
                    centerList.add(map);
                }
                String selectedAlgorithm = request.getParameter("algorithm");
                switch(selectedAlgorithm){
                    case "kmeans":
                        KMeansAlgorithmRound round = (KMeansAlgorithmRound) roundList.get(i).getAlgorithmRound();
                        double norm = round.getfNorm();
                        map = new HashMap<Object, Object>();
                        map.put("x", i + 1);
                        map.put("y", norm);
                        normList.add(map);
                }
                dataPoints.add(gsonObj.toJson(centerList));
                normPoints.add(gsonObj.toJson(normList));
                //availableClients.add(gsonObj.toJson(availableClientsRound));
                //involvedClients.add(gsonObj.toJson(involvedClientsRound));
                numCrashes.add(Integer.valueOf(gsonObj.toJson(numcrashesRound)));
            }
        }
        numRounds = roundList.size() - 2;
    }
%>

<!DOCTYPE HTML>
<html>
<head>
    <title>Home Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript">
        let time = null;
        datapointsJS = <%=dataPoints%>;
        normpointsJS = <%=normPoints%>;
        availableClientsJS = <%=availableClients%>;
        involvedClientsJS = <%=involvedClients%>;
        numCrashesJS = <%=numCrashes%>;
        totalRounds = <%=numRounds%>;
        console.log(availableClientsJS);
        console.log(involvedClientsJS);
        console.log(numCrashesJS);
        rounds = 0;
        window.onload = function () {
            <% if(request.getAttribute("rounds")!=null){
            %>
                time = setInterval(delayRounds ,2000);
            <%}%>
        }

        function delayRounds() {
            rounds++;
            showRound();
            if(rounds>=totalRounds){
                clearInterval(time);
            }
        }

        function showRound(){
            document.getElementById("numrounds").textContent = rounds;
            document.getElementById("roundsdiv").style.display = "block";
            printCenters();
            <% if(request.getAttribute("algorithm") != null && request.getAttribute("algorithm").equals("kmeans")){
            %>
                printNorms();
            <%}%>
        }

        function printCenters(){
            var chart = new CanvasJS.Chart("chartContainerCenters", {
                animationEnabled: false,
                theme: "light2",
                title: {
                    text: " distribution of the centers"
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
                    dataPoints: datapointsJS[rounds-1]
                }]
            });
            chart.render();
        }

        function printNorms(){
            var chart = new CanvasJS.Chart("chartContainerNorms", {
                animationEnabled: false,
                theme: "light2",
                title: {
                    text: "norms"
                },
                subtitles: [{
                    text: ""
                }],
                axisY: {
                    title: "fnorm",
                    includeZero: true
                },
                axisX: {
                    title: "rounds"
                },
                data: [{
                    type: "scatter",
                    xValueFormatString: "#,##0.000",
                    yValueFormatString: "#,##0.000",
                    toolTipContent: "<b>First Feature:</b> {x} <br><b>Second feature:</b> {y}",
                    dataPoints: normpointsJS[rounds-1]
                }]
            });
            chart.render();
        }

        function showForm() {
            var algorithmValue = document.getElementById("algorithm").value;
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
        <label for="name">Name: </label><input id="name" type="text" name="name" value="Primo Esperimento"><br>
        <label for="dataset">Dataset: </label><input id="dataset" type="text" name="dataset"
                                                     value="https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff"><br>
        <label for="numFeatures">Number of features: </label><input id="numFeatures" type="number" name="numFeatures"
                                                                    value="3"><br>
        <label for="numMinClients">Number of minimum clients: </label><input id="numMinClients" type="number"
                                                                             name="numMinClients"
                                                                             value="3"><br>
        <label for="randomClients">Random Clients: </label><input id="randomClients" type="text" name="randomClients"
                                                                  value="false"><br>
        <label for="timeout">Timeout: </label><input id="timeout" type="number" name="timeout"
                                                      value="25000"><br>
        <label for="algorithm">Select the algorithm: </label>
        <select name="algorithm" id="algorithm" onchange="showForm()">
            <option disabled selected value> -- select an algorithm --</option>
            <option value="kmeans">kmeans</option>
        </select>


        <div id="kmeans" style="display: none">
            <label for="numClusters">Clusters: </label><input id="numClusters" type="number" name="numClusters"
                                                              value="3"><br>
            <label for="distance">Distance: </label><input id="distance" type="text" name="distance" value="numba_norm"><br>
            <label for="epsilon">Epsilon: </label><input id="epsilon" type="number" step="any" name="epsilon"
                                                         value="0.05"><br>
            <label for="seedCenters">Seed centers: </label><input id="seedCenters" type="number" name="seedCenters"
                                                              value="0"><br>
            <label for="normFn">Norm Fn: </label><input id="normFn" type="text" name="normFn" value="norm_fro"><br><br>
        </div>
        <br>

        <label for="firstFeature">First feature: </label><input id="firstFeature" type="text" name="firstFeature"
                                                                value="0"><br>
        <label for="secondFeature">Second feature: </label><input id="secondFeature" type="text"
                                                                  name="secondFeature" value="1"><br>
        <button type="submit">Submit</button>
        <br>
    </form>
</div>
<div id="experimentResult" style="height:1000px; width:1400px">
    <div id="graphCenters">
        <div id="chartContainerCenters" style="height: 370px; width: 600px; float:left"></div>
        <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    </div>
    <div id="graphNorms">
        <div id="chartContainerNorms" style="height: 370px; width: 600px; float:left"></div>
    </div>
    <div id="roundsdiv" style="font-size:20px; display: none;">
        <label>number of rounds:</label>
        <label id="numrounds">0</label>
    </div>
</div>
</body>
</html>
