<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    Map message = (Map)request.getAttribute("messages");
    String successError = message.get("success") == null ? "": (String)message.get("success");
    Map valuesGeneral = (Map)request.getAttribute("valuesGeneral");
    List<String> hostnames = (List<String>)request.getAttribute("hostnames");
%>
<html>
<head>
    <title>Admin Page</title>
</head>
<script>
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
        <%  String numberOfClients = "";
            if(valuesGeneral.get("NumberOfClients") != null){
                numberOfClients = (String)valuesGeneral.get("NumberOfClients");
            }
        %>
    <label for="NumberOfClients">Number of clients</label>
    <input id="NumberOfClients" type="number" name="NumberOfClients" min="1" value=<%=numberOfClients%> required>
    <div id="hostnames">
        <label for="ClientsHostnames">Clients hostnames</label>
        <%  if(hostnames != null){
            for(String hostname: hostnames){
        %>
        <input id="ClientsHostnames" name="ClientsHostnames" value=<%=hostname%>>
        <%
                }
            }
            String clientHostnamesError = "";
            if(message.get("ClientsHostnames") != null){
                clientHostnamesError = (String)message.get("ClientsHostnames");
            }
        %>
    </div>
    <button type="button" onclick="addInput()">add</button>
    <span class="error"><%=clientHostnamesError%></span>
        <%  String randomClientSeed = "";
            if(valuesGeneral.get("RandomClientsSeed") != null){
                randomClientSeed = (String)valuesGeneral.get("RandomClientsSeed");
            }
        %>
    <br>
    <label for="RandomClientsSeed">Random clients seed</label>
    <input id="RandomClientsSeed" name="RandomClientsSeed" type="number" value=<%=randomClientSeed%> required>
        <%  String maxNumberRound = "";
            if(valuesGeneral.get("MaxNumberRound") != null){
                maxNumberRound = (String)valuesGeneral.get("MaxNumberRound");
            }
        %>
    <br>
    <label for="MaxNumberRound">Maximum number of rounds</label>
    <input id="MaxNumberRound" name="MaxNumberRound" type="number" min="1" value=<%=maxNumberRound%> required>
        <%  String maxAttemptsClientCrash = "";
            if(valuesGeneral.get("MaxAttemptsClientCrash") != null){
                maxAttemptsClientCrash = (String)valuesGeneral.get("MaxAttemptsClientCrash");
            }
        %>
    <br>
    <label for="MaxAttemptsClientCrash">Maximum number of client crashes</label>
    <input id="MaxAttemptsClientCrash" name="MaxAttemptsClientCrash" type="number" min="1" value=<%=maxAttemptsClientCrash%> required>
    <%  String maxAttemptsServerCrash = "";
            if(valuesGeneral.get("MaxAttemptsServerCrash") != null){
                maxAttemptsServerCrash = (String)valuesGeneral.get("MaxAttemptsServerCrash");
            }
    %>
    <br>
    <label for="MaxAttemptsServerCrash">Maximum number of server crashes</label>
    <input id="MaxAttemptsServerCrash" name="MaxAttemptsServerCrash" type="number" min="1" value=<%=maxAttemptsServerCrash%> required>
    <%  String maxAttemptsOverallCrash = "";
        if(valuesGeneral.get("MaxAttemptsOverallCrash") != null){
            maxAttemptsOverallCrash = (String)valuesGeneral.get("MaxAttemptsOverallCrash");
        }
    %>
    <br>
    <label for="MaxAttemptsOverallCrash">Maximum number of overall client crashes</label>
    <input id="MaxAttemptsOverallCrash" name="MaxAttemptsOverallCrash" type="number" min="1"  value=<%=maxAttemptsOverallCrash%> required>
    <%  String mode = "";
            if(valuesGeneral.get("Mode") != null){
                mode = (String)valuesGeneral.get("Mode");
            }
    %>
    <br>
    <label for="Mode">Mode</label>
    <input id="Mode" name="Mode" type="number" min="1" max="3" value=<%=mode%> required>
    <br>
    <input type="submit" name="update" value="Send">
    <span class="success"><%=successError%></span>
</form>
</body>
</html>
