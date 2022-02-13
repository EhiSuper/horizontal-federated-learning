<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: fabio
  Date: 11/02/2022
  Time: 11:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Map message = (Map)request.getAttribute("messages");
    String successError = (String)message.get("success") == null ? "": (String)message.get("success");
    Map valuesGeneral = (Map)request.getAttribute("valuesGeneral");
    Map valuesKMeans = (Map)request.getAttribute("valuesKMeans");
    List<String> hostnames = (List)request.getAttribute("hostnames");
%>
<html>
<head>
    <title>Admin Page</title>
</head>
<script>
    function show_form(){
        var allOptions = document.getElementById("select_alg").options;
        for(var option of allOptions){
            var elements = document.getElementsByClassName(option.value);
            for(var element of elements){
                element.style.display = "none";
            }
        }
        var value = document.getElementById("select_alg").value;
        var elements = document.getElementsByClassName(value);
        for(var element of elements){
            element.style.display = "block";
        }
    }

    function addInput(){
        element = document.createElement("input");
        element.name = "ClientsHostnames";
        document.getElementById("hostnames").append(element);
    }
</script>
<body>
<h2 style="text-align: center">Administrator page</h2>
<div id="menu">
    <button><a href="<%=request.getContextPath()%>/Logout">Logout</a></button>
</div>
<form action="<%=request.getContextPath()%>/AdminPage" method="post">
    <h1>Modify experiment default settings</h1>
    <p>
        <%  String numberOfClients = "";
            String numberOfClientsError = "";
            if(valuesGeneral.get("NumberOfClients") != null){
                numberOfClients = (String)valuesGeneral.get("NumberOfClients");
            }
            if(message.get("NumberOfClients") != null){
                numberOfClientsError = (String)message.get("NumberOfClients");
            }
        %>
        <label for="NumberOfClients">Number of clients</label>
        <input id="NumberOfClients" name="NumberOfClients" value=<%=numberOfClients%>>
        <span class="error"><%=numberOfClientsError%></span>
    </p>
    <p id="hostnames">
        <button type="button" onclick="addInput()">add</button>
        <label >Clients hostnames</label>
        <%  if(hostnames != null){
            for(String hostname: hostnames){
        %>
        <input name="ClientsHostnames" value=<%=hostname%>>
        <%
                }
            }
            String clientHostnamesError = "";
            if(message.get("ClientsHostnames") != null){
                clientHostnamesError = (String)message.get("ClientsHostnames");
            }
        %>
    </p>
    <span class="error"><%=clientHostnamesError%></span>
    <p>
        <%  String randomClientSeed = "";
            String randomClientSeedError = "";
            if(valuesGeneral.get("RandomClientsSeed") != null){
                randomClientSeed = (String)valuesGeneral.get("RandomClientsSeed");
            }
            if(message.get("RandomClientsSeed") != null){
                randomClientSeedError = (String)message.get("RandomClientsSeed");
            }
        %>
        <label for="RandomClientsSeed">Random clients seed</label>
        <input id="RandomClientsSeed" name="RandomClientsSeed" value=<%=randomClientSeed%>>
        <span class="error"><%=randomClientSeedError%></span>
    </p>
    <p>
        <%  String maxNumberRound = "";
            String maxNumberRoundError = "";
            if(valuesGeneral.get("MaxNumberRound") != null){
                maxNumberRound = (String)valuesGeneral.get("MaxNumberRound");
            }
            if(message.get("MaxNumberRound") != null){
                maxNumberRoundError = (String)message.get("MaxNumberRound");
            }
        %>
        <label for="MaxNumberRound">Maximum number of rounds</label>
        <input id="MaxNumberRound" name="MaxNumberRound" value=<%=maxNumberRound%>>
        <span class="error"><%=maxNumberRoundError%></span>
    </p>
    <p>
        <%  String maxAttemptsClientCrash = "";
            String maxAttemptsClientCrashError = "";
            if(valuesGeneral.get("MaxAttemptsClientCrash") != null){
                maxAttemptsClientCrash = (String)valuesGeneral.get("MaxAttemptsClientCrash");
            }
            if(message.get("MaxAttemptsClientCrash") != null){
                maxAttemptsClientCrashError = (String)message.get("MaxAttemptsClientCrash");
            }
        %>
        <label for="MaxAttemptsClientCrash">Maximum number of client crashes</label>
        <input id="MaxAttemptsClientCrash" name="MaxAttemptsClientCrash" value=<%=maxAttemptsClientCrash%>>
        <span class="error"><%=maxAttemptsClientCrashError%></span>
    </p>
    <p>
        <%  String maxAttemptsServerCrash = "";
            String maxAttemptsServerCrashError = "";
            if(valuesGeneral.get("MaxAttemptsServerCrash") != null){
                maxAttemptsServerCrash = (String)valuesGeneral.get("MaxAttemptsServerCrash");
            }
            if(message.get("MaxAttemptsServerCrash") != null){
                maxAttemptsServerCrashError = (String)message.get("MaxAttemptsServerCrash");
            }
        %>
        <label for="MaxAttemptsServerCrash">Maximum number of server crashes</label>
        <input id="MaxAttemptsServerCrash" name="MaxAttemptsServerCrash" value=<%=maxAttemptsServerCrash%>>
        <span class="error"><%=maxAttemptsServerCrashError%></span>
    </p>
    <p>
        <%  String maxAttemptsOverallCrash = "";
            String maxAttemptsOverallCrashError = "";
            if(valuesGeneral.get("MaxAttemptsOverallCrash") != null){
                maxAttemptsOverallCrash = (String)valuesGeneral.get("MaxAttemptsOverallCrash");
            }
            if(message.get("MaxAttemptsOverallCrash") != null){
                maxAttemptsOverallCrashError = (String)message.get("MaxAttemptsOverallCrash");
            }
        %>
        <label for="MaxAttemptsOverallCrash">Maximum number of overall client crashes</label>
        <input id="MaxAttemptsOverallCrash" name="MaxAttemptsOverallCrash" value=<%=maxAttemptsOverallCrash%>>
        <span class="error"><%=maxAttemptsOverallCrashError%></span>
    </p>
    <select name="select algorithm" id="select_alg" onchange="show_form()">
        <option disabled selected value> -- select an algorithm -- </option>
        <option value="kmeans">kmeans</option>
    </select>
    <p class="kmeans" style="display:none;">
        <%  String mode = "";
            String modeError = "";
            if(valuesKMeans.get("Mode") != null){
                mode = (String)valuesKMeans.get("Mode");
            }
            if(message.get("Mode") != null){
                modeError = (String)message.get("Mode");
            }
        %>
        <label for="Mode">Mode</label>
        <input id="Mode" name="Mode" value=<%=mode%>>
        <span class="error"><%=modeError%></span>
    </p>
    <p>
        <input type="submit">
        <span class="success"><%=successError%></span>
    </p>
</form>
</body>
</html>
