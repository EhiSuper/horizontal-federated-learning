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
    ArrayList<Integer> crashes = new ArrayList<>();
    ArrayList<String> availableClients = new ArrayList<>();
    ArrayList<String> involvedClients = new ArrayList<>();
    int numMinClients = 0;
    int numOverallCrashes = 0;
    int numRounds = 0;
    int experimentId = 0;
    int numFeatures = 0;
    long time = 0;
    int firstFeature = 0;
    int secondFeature = 1;
    String reason = "";
    List<String> logExecution = new ArrayList<>();
    if (request.getAttribute("rounds") != null) {
        System.out.println("not null");
        firstFeature = (int)request.getAttribute("firstFeature");
        secondFeature = (int)request.getAttribute("secondFeature");
        Gson gsonObj = new Gson();
        Map<Object, Object> map = null;
        List<ExperimentRound> roundList = (List<ExperimentRound>) request.getAttribute("rounds");
        List<Map<Object, Object>> normList = new ArrayList<Map<Object, Object>>();
        for(int i=0; i<(roundList.size() - 1); i++){
            List<Map<Object, Object>> pointList = new ArrayList<Map<Object, Object>>();
            if(!roundList.get(i).getLast()){
                KMeansAlgorithmRound kmRound = (KMeansAlgorithmRound) roundList.get(i).getAlgorithmRound();
                List<List<Double>> centers = kmRound.getCenters();
                List<Client> availableClientsRound = roundList.get(i).getClientsState();
                List<Client> involvedClientsRound = roundList.get(i).getInvolvedClients();
                for(Client involvedClient: involvedClientsRound)
                    involvedClients.add(involvedClient.getHostname() + " ");
                int numcrashesRound = roundList.get(i).getNumCrashes();
                for (Client client : involvedClientsRound) {
                    List<List<Double>> chunk = client.getChunk();
                    for(List<Double> point: chunk){
                        map = new HashMap<Object, Object>();
                        map.put("x", point.get(firstFeature));
                        map.put("y", point.get(secondFeature));
                        map.put("color", "grey");
                        map.put("markerSize", 3);
                        map.put("fillOpacity", ".3");
                        pointList.add(map);
                    }
                }
                for (List<Double> center : centers) {
                    map = new HashMap<Object, Object>();
                    map.put("x", center.get(firstFeature));
                    map.put("y", center.get(secondFeature));
                    map.put("color", "black");
                    map.put("markerSize", 20);
                    map.put("markerBorderColor", "red");
                    pointList.add(map);
                }
                String selectedAlgorithm = (String) request.getAttribute("algorithm");
                switch(selectedAlgorithm){
                    case "KMeans":
                        KMeansAlgorithmRound round = (KMeansAlgorithmRound) roundList.get(i).getAlgorithmRound();
                        double norm = round.getfNorm();
                        map = new HashMap<Object, Object>();
                        map.put("x", i + 1);
                        map.put("y", norm);
                        normList.add(map);
                }
                dataPoints.add(gsonObj.toJson(pointList));
                normPoints.add(gsonObj.toJson(normList));
                //availableClients.add(gsonObj.toJson(availableClientsRound));
                crashes.add(Integer.valueOf(gsonObj.toJson(numcrashesRound)));
            } else {
                reason = roundList.get(i).getReason();
            }
        }
        numOverallCrashes = crashes.get(crashes.size()-1);
        experimentId = (int)request.getAttribute("experimentId");
        logExecution = (List<String>) request.getAttribute("logExecution");
        numMinClients = (int)request.getAttribute("numMinClients");
        numFeatures = (int)request.getAttribute("numFeatures");
        time = (long)request.getAttribute("time");
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
        crashes = <%=crashes%>;
        overallCrashes = <%=numOverallCrashes%>;
        totalRounds = <%=numRounds%>;
        numMinClients = <%=numMinClients%>;
        terminationReason = '<%=reason%>';
        timeNeeded = <%=time%>;
        console.log(availableClientsJS);
        console.log(involvedClientsJS);
        console.log(crashes);
        console.log(overallCrashes);
        rounds = 0;
        logExecution = <%=logExecution%>;
        window.onload = function () {
            <% if(request.getAttribute("rounds")!=null){
            %>
            time = setInterval(delayRounds ,4000);
            <%}%>
        }

        function delayRounds() {
            if (rounds == 0) {
                numFeatures = <%= numFeatures %>;
                selectList = document.getElementById("firstFeature");
                selectList2 = document.getElementById("secondFeature");
                document.getElementById("features").style.display = "block";
                for (var i = 0; i < numFeatures; i++) {
                    var option = document.createElement("option");
                    option.value = i;
                    option.text = "Feature "+ i;
                    if(i == <%= firstFeature %>)
                        option.selected = true
                    selectList.appendChild(option);

                }
                for (var i = 0; i < numFeatures; i++) {
                    var option = document.createElement("option");
                    option.value = i;
                    option.text = "Feature "+ i;
                    if(i ==  <%= secondFeature %>)
                        option.selected = true
                    selectList2.appendChild(option);
                }
            }
            rounds++;
            if(rounds==totalRounds+1){
                document.getElementById("changeButton").disabled = false;
                document.getElementById("numroundsText").textContent = "Total number of rounds:";
                document.getElementById("clientsInvolvedText").textContent = "Overall clients involved:";
                document.getElementById("crashesText").textContent = "Overall crashes:";
                document.getElementById("crashes").textContent = overallCrashes;
                document.getElementById("end").style.display = "block";
                document.getElementById("reason").textContent = terminationReason;
                document.getElementById("time").textContent = timeNeeded + " ms";
                document.getElementById("logExecution").value = logExecution.join("\r\n");
                uniq = [...new Set(involvedClientsJS)];
                document.getElementById("clientsInvolved").textContent = uniq.join(", ");
                clearInterval(time);
            } else {
                showRound();
            }
        }

        function showRound(){
            document.getElementById("numrounds").textContent = rounds;
            document.getElementById("crashes").textContent = crashes.slice(rounds-1,rounds);
            console.log(rounds)
            console.log(totalRounds)
            document.getElementById("results").style.display = "block";
            console.log(involvedClientsJS.slice(numMinClients * rounds, numMinClients * rounds + numMinClients).join("\r\n"));
            document.getElementById("clientsInvolved").textContent = involvedClientsJS.slice(numMinClients * (rounds-1), numMinClients * (rounds-1) + numMinClients).join(", ");
            printCenters();
            <% if(request.getAttribute("algorithm") != null && request.getAttribute("algorithm").equals("KMeans")){
            %>
            printNorms();
            <%}%>
        }

        function printCenters(){
            var chart = new CanvasJS.Chart("chartContainerCenters", {
                animationEnabled: false,
                theme: "light2",
                title: {
                    text: " Scatter Plot"
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
                    text: "Norm Variation"
                },
                subtitles: [{
                    text: ""
                }],
                axisY: {
                    title: "FNorm",
                    includeZero: true
                },
                axisX: {
                    title: "Rounds"
                },
                data: [{
                    type: "line",
                    xValueFormatString: "#,##0.000",
                    yValueFormatString: "#,##0.000",
                    toolTipContent: "<b>Round:</b> {x} <br><b>FNorm:</b> {y}",
                    dataPoints: normpointsJS[rounds-1]
                }]
            });
            chart.render();
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
<div id="experimentResult" style="width:1400px">
    <form action="<%=request.getContextPath()%>/Home" method="post">
        <button type="submit" name="back">Back</button>
    </form>
    <br>
    <div id="features" style=" display: none;">
        <form action="<%=request.getContextPath()%>/Home/Features" method="post">
            <input type="hidden" id="experimentId" name="experimentId" value="<%=experimentId%>">
            <label for="firstFeature">First Feature:</label>
            <select name="firstFeature" id="firstFeature">
            </select>
            <label for="secondFeature">Second Feature:</label>
            <select name="secondFeature" id="secondFeature">
            </select>
            <%
                request.setAttribute("numClients", request.getAttribute("numClients"));
                request.setAttribute("rounds", request.getAttribute("rounds"));
                request.setAttribute("experimentId", request.getAttribute("experimentId"));
                request.setAttribute("logExperiment", request.getAttribute("LogExperiment"));
                request.setAttribute("numClients", request.getAttribute("numClients"));
                request.setAttribute("numMinClients", request.getAttribute("numMinClients"));
                request.setAttribute("algorithm", request.getAttribute("algorithm"));
                request.setAttribute("numFeatures", request.getAttribute("numFeatures"));
                request.setAttribute("time", request.getAttribute("time"));
            %>
            <button type="submit" name="change" id="changeButton" disabled>Change</button>
        </form>
    </div>
    <br>
    <div id="graphCenters">
        <div id="chartContainerCenters" style="height: 370px; width: 600px; float:left"></div>
        <script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
    </div>
    <div id="graphNorms">
        <div id="chartContainerNorms" style="height: 370px; width: 600px"></div>
    </div>
    <br>
</div>
<br>
<div>
    <div id="results" style="font-size:20px; display: none;">
        <label id="numroundsText">Round number: </label>
        <label id="numrounds">0</label>
        <br>
        <label for="clientsInvolved" id="clientsInvolvedText">Clients Involved:</label>
        <label id="clientsInvolved"></label>
        <br>
        <label for="crashes" id="crashesText">Crashes until this round:</label>
        <label id="crashes">0</label>
    </div>
    <div id="end" style="font-size:20px; display: none;">
        <label for="reason" id="reasonText">Reason of termination:</label>
        <label id="reason"></label>
        <br>
        <label for="time" id="timeText">Time for the execution:</label>
        <label id="time"></label>
        <br>
        <label for="logExecution">Logs of the Execution:</label><br>
        <textarea id="logExecution" name="Logs of the execution" rows="30" cols="100">
        </textarea>
        <form action="<%=request.getContextPath()%>/Home" method="post">
            <input type="hidden" id="id" name="id" value="<%=experimentId%>">
            <button type="submit" name="export">Export as txt File</button>
        </form>
    </div>
</div>
</body>
</html>