package it.unipi.dsmt.horizontalFederatedLearning.service.erlang;

import com.ericsson.otp.erlang.*;
import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Communication {
    private static OtpErlangPid destination;
    private static Node node;
    private static Experiment currentExperiment;

    private static OtpErlangList prepareArguments(Experiment experiment){
        OtpErlangTuple params = experiment.prepareTuple();
        OtpErlangTuple algParams = experiment.getAlgorithm().prepareSpecificParameters();
        OtpErlangObject[] listParams = new OtpErlangObject[3];
        listParams[0] = params;
        listParams[1] = algParams;
        listParams[2] = new OtpErlangInt(experiment.getMaxAttemptsOverallCrash());
        OtpErlangList arguments = new OtpErlangList(listParams);
        return arguments;
    }

    public static void startExperiment(Experiment experiment){
        OtpErlangList arguments = prepareArguments(experiment);
        destination = null;
        currentExperiment = experiment;
        node = new Node("server@localhost", "COOKIE", "javaServer");
        try {
            //String[] cmd = {"bash","-c", "erl -sname erl@localhost -setcookie COOKIE -pa './erlangFiles'"}; // type last element your command
            //final Process p = Runtime.getRuntime().exec(cmd);
            final Process p = Runtime.getRuntime().exec("erl -sname erl@localhost -setcookie COOKIE -pa \"./erlangFiles\"");
            new Thread(new Runnable(){
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    try {
                        while ((line = input.readLine()) != null)
                            System.out.println(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1000);
            OtpSelf caller = new OtpSelf("caller", "COOKIE");
            OtpPeer supervisor = new OtpPeer("erl@localhost");
            OtpConnection conn = caller.connect(supervisor);
            conn.sendRPC("supervisorNode","start", arguments );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // accertarsi ricezione da chi vogliamo noi
    private static OtpErlangTuple receive() throws OtpErlangDecodeException, OtpErlangExit {
        OtpErlangTuple tuple = null;
        tuple = (OtpErlangTuple) node.getOtpMbox().receive();
        OtpErlangPid sender = (OtpErlangPid)tuple.elementAt(0);
        if(!sender.node().equals("erl@localhost"))
            throw new ErlangErrorException();
        if(destination == null) {
            destination = sender;
            node.getOtpMbox().link(destination);
        }
        return tuple;
    }

    public static ExperimentRound receiveRound(){
        try{
            OtpErlangTuple result = receive();
            if(result == null){
                return null;
            }
            OtpErlangAtom msgType = (OtpErlangAtom)result.elementAt(1);
            System.out.println("msg type: " + msgType.toString());
            if(msgType.toString().equals("error")) {
                throw new ErlangErrorException(result.elementAt(2).toString());
            } else if(msgType.toString().equals("completed")) {
                return new ExperimentRound(true, result.elementAt(2).toString());
            } else { //round
                ExperimentRound round = composeRound(result);
                return round;
            }
        }
        catch (OtpErlangExit e) {
            if(!e.reason().toString().equals("normal")){
                return new ExperimentRound(true, e.toString());
            }
        } catch (Exception e) {
            return new ExperimentRound(true, e.toString());
        }
        return null;
    }

    private static ExperimentRound composeRound(OtpErlangTuple result) {
        OtpErlangTuple content = (OtpErlangTuple) result.elementAt(2);
        OtpErlangTuple algorithmContent = (OtpErlangTuple) content.elementAt(0);
        // deve diventare chiamata generica
        AlgorithmRound algRound = currentExperiment.getAlgorithm().getIterationInfo(algorithmContent);
        //AlgorithmRound algRound = getKMeansIterationInfo(algorithmContent);
        List<Client> clients = new ArrayList<>();
        OtpErlangList clientsContent = (OtpErlangList)content.elementAt(2);
        for(OtpErlangObject element: clientsContent) {
            OtpErlangTuple tupleElement = (OtpErlangTuple) element;
            clients.add(new Client(tupleElement.elementAt(0).toString(),tupleElement.elementAt(1).toString(), Integer.parseInt(tupleElement.elementAt(3).toString())));
        }
        List<Client> stateClients = new ArrayList<>();
        OtpErlangList stateClientsContent = (OtpErlangList)content.elementAt(3);
        for(OtpErlangObject element: stateClientsContent) {
            OtpErlangTuple tupleElement = (OtpErlangTuple) element;
            stateClients.add(new Client(tupleElement.elementAt(0).toString(),tupleElement.elementAt(1).toString(), Integer.parseInt(tupleElement.elementAt(3).toString())));
        }
        return new ExperimentRound(Integer.parseInt(content.elementAt(4).toString()), Integer.parseInt(content.elementAt(1).toString()), algRound, clients, stateClients);
    }

    // mettere in un luogo independent da logica generale
    private static AlgorithmRound getKMeansIterationInfo(OtpErlangTuple algorithmContent){
        OtpErlangList centersContent = (OtpErlangList)algorithmContent.elementAt(0);
        //System.out.println("centersContent: " + centersContent);
        List<List<Double>> centers = new ArrayList<>();
        List<Double> center = new ArrayList<>();
        for(OtpErlangObject element: centersContent) {
            OtpErlangList centerList = (OtpErlangList) element;
            for(OtpErlangObject coordinate: centerList)
                center.add(Double.parseDouble(coordinate.toString()));
            centers.add(center);
        }
        return new KMeansAlgorithmRound(centers, Double.parseDouble(algorithmContent.elementAt(1).toString()));
    }

    /*private static void send(Node node, String content){
        OtpErlangString msg = new OtpErlangString(content);
        OtpErlangTuple responseTuple = new OtpErlangTuple(new OtpErlangObject[]{node.getOtpMbox().self(), msg});
        node.getOtpMbox().send(destination, responseTuple);
    }*/
}


