<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>

<%
    ArrayList<String> dataPoints = (ArrayList<String>) session.getAttribute("dataPoints");
    ArrayList<String> normPoints = (ArrayList<String>) session.getAttribute("normPoints");
    ArrayList<Integer> crashes = (ArrayList<Integer>) session.getAttribute("crashes");
    ArrayList<String> involvedClients = (ArrayList<String>) session.getAttribute("involvedClients");
    int numFeatures = (int) session.getAttribute("numFeatures");
    int numMinClients = (int) session.getAttribute("numMinClients");
    int numRounds = (int) session.getAttribute("numRounds");
    int numOverallCrashes = (int) session.getAttribute("numOverallCrashes");
    String reason = (String) session.getAttribute("reason");
    long time = (long) session.getAttribute("time");
    List<String> logExecution = (List<String>) session.getAttribute("logExecution");
    int firstFeature = (int) session.getAttribute("firstFeature");
    int secondFeature = (int) session.getAttribute("secondFeature");
    int experimentId = (int) session.getAttribute("experimentId");

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
        let time = null;
        datapoints = <%=dataPoints%>;
        normpoints = <%=normPoints%>;
        involvedClients = <%=involvedClients%>;
        crashes = <%=crashes%>;
        overallCrashes = <%=numOverallCrashes%>;
        totalRounds = <%=numRounds%>;
        numMinClients = <%=numMinClients%>;
        terminationReason = '<%=reason%>';
        timeNeeded = <%=time%>;
        rounds = 0;
        logExecution = <%=logExecution%>;
        window.onload = function () {
            time = setInterval(delayRounds, 4000);
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
                    option.text = "Feature " + i;
                    if (i == <%= firstFeature %>)
                        option.selected = true
                    selectList.appendChild(option);

                }
                for (i = 0; i < numFeatures; i++) {
                    option = document.createElement("option");
                    option.value = i;
                    option.text = "Feature " + i;
                    if (i ==  <%= secondFeature %>)
                        option.selected = true
                    selectList2.appendChild(option);
                }
            }
            rounds++;
            if (rounds == totalRounds + 1) {
                document.getElementById("changeButton").disabled = false;
                document.getElementById("numroundsText").textContent = "Total number of rounds:";
                document.getElementById("clientsInvolvedText").textContent = "Overall clients involved:";
                document.getElementById("crashesText").textContent = "Overall crashes:";
                document.getElementById("crashes").textContent = overallCrashes;
                document.getElementById("end").style.display = "block";
                document.getElementById("reason").textContent = terminationReason;
                document.getElementById("time").textContent = timeNeeded + " ms";
                document.getElementById("logExecution").value = logExecution.join("\r\n");
                uniq = [...new Set(involvedClients)];
                document.getElementById("clientsInvolved").textContent = uniq.join(", ");
                clearInterval(time);
            } else {
                showRound();
            }
        }

        function showRound() {
            document.getElementById("numrounds").textContent = rounds;
            document.getElementById("crashes").textContent = crashes.slice(rounds - 1, rounds);
            console.log(rounds)
            console.log(totalRounds)
            document.getElementById("results").style.display = "block";
            console.log(involvedClients.slice(numMinClients * rounds, numMinClients * rounds + numMinClients).join("\r\n"));
            document.getElementById("clientsInvolved").textContent = involvedClients.slice(numMinClients * (rounds - 1), numMinClients * (rounds - 1) + numMinClients).join(", ");
            printCenters();
            <% if(session.getAttribute("algorithm") != null && session.getAttribute("algorithm").equals("KMeans")){
            %>
            printNorms();
            <%}%>
        }

        function printCenters() {
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
                    dataPoints: datapoints[rounds - 1]
                }]
            });
            chart.render();
        }

        function printNorms() {
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
                    dataPoints: normpoints[rounds - 1]
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
    <br>
    <div id="features" style=" display: none;">
        <form action="<%=request.getContextPath()%>/Home/Features" method="post">
            <input type="hidden" id="ExperimentId" name="ExperimentId" value="<%=experimentId%>">
            <label for="firstFeature">First Feature:</label>
            <select name="firstFeature" id="firstFeature">
            </select>
            <label for="secondFeature">Second Feature:</label>
            <select name="secondFeature" id="secondFeature">
            </select>
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
        <textarea id="logExecution" name="Logs of the execution" rows="30" cols="100" readonly>
        </textarea>
        <form action="<%=request.getContextPath()%>/Run" method="post">
            <input type="hidden" id="id" name="id" value="<%=experimentId%>">
            <button type="submit" name="export">Export as txt File</button>
        </form>
    </div>
</div>
</body>
</html>