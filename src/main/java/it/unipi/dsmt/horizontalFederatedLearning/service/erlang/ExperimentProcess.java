package it.unipi.dsmt.horizontalFederatedLearning.service.erlang;

import com.ericsson.otp.erlang.*;
import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.CommunicationException;
import it.unipi.dsmt.horizontalFederatedLearning.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExperimentProcess {
    private OtpErlangPid destination;
    private OtpMbox erlangProcess;
    private Process p;
    private Experiment currentExperiment;
    private long start;

    private OtpErlangList prepareArguments(Experiment experiment, OtpErlangPid javaPid) {
        OtpErlangTuple params = experiment.prepareTuple();
        OtpErlangTuple algParams = experiment.getAlgorithm().prepareSpecificParameters();
        OtpErlangObject[] listParams = new OtpErlangObject[4];
        listParams[0] = params;
        listParams[1] = algParams;
        listParams[2] = new OtpErlangInt(experiment.getMaxAttemptsServerCrash());
        listParams[3] = javaPid;
        return new OtpErlangList(listParams);
    }

    public List<ExperimentRound> startExperiment(Experiment experiment) {
        destination = null;
        currentExperiment = experiment;
        Node node = Node.getNode();
        synchronized (Node.class){
            erlangProcess = node.getOtpNode().createMbox();
        }
        OtpErlangPid javaPid = erlangProcess.self();
        OtpErlangList arguments = prepareArguments(experiment, javaPid);
        List<ExperimentRound> rounds = null;
        try {
            start = System.nanoTime();
            String[] cmd = {"bash", "-c", "erl -name erl" + experiment.getId() + "@127.0.0.1 -setcookie COOKIE -pa './erlangFiles'"}; // type last element your command
            p = Runtime.getRuntime().exec(cmd);
            new Thread(new Runnable() {
                List<String> logs = new ArrayList<>();
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    String editedLine;
                    try {
                        while ((line = input.readLine()) != null) {
                            if (line.contains("1> "))
                                editedLine = line.split("1> ")[1];
                            else editedLine = line;
                            System.out.println(editedLine);
                            logs.add(editedLine);
                            if(editedLine.contains("Received completed message") ||
                               editedLine.contains("Received reached_max_rounds message") ||
                               editedLine.contains("Max attempts reached, returning...")){
                                break;
                            }
                        }
                        Log.logExperiment(logs, experiment);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(1000);
            OtpSelf callerNode = node.getCaller();
            OtpPeer supervisorNode = new OtpPeer("erl" + experiment.getId() + "@127.0.0.1");
            synchronized (Node.class) {
                callerNode.connect(supervisorNode).sendRPC("supervisorNode", "start", arguments);
            }
            rounds = waitForRounds();
        } catch (IOException e) {
            erlangProcess.exit("exception encountered");
            p.destroy();
            e.printStackTrace();
            startExperiment(experiment);
        } catch (Exception ee){
            erlangProcess.exit("exception encountered");
            p.destroy();
            ee.printStackTrace();
        }
        erlangProcess.exit("normal");
        p.destroy();
        return rounds;
    }

    private List<ExperimentRound> waitForRounds(){
        List<ExperimentRound> rounds = new ArrayList<>();
        ExperimentRound round;
        while (true) {
            try {
                round = receiveRound();
                if(round != null)
                    rounds.add(round);
                else{
                    System.out.println("Experiment completed! Received all rounds!");
                    break;
                }
            } catch (CommunicationException ex) {
                System.out.println("Error during erlang computations: " + ex.getMessage());
            }
        }
        return rounds;
    }

    private OtpErlangTuple receive() throws OtpErlangDecodeException, OtpErlangExit {
        OtpErlangTuple tuple = (OtpErlangTuple) erlangProcess.receive();
        OtpErlangPid sender = (OtpErlangPid) tuple.elementAt(0);
        if (!sender.node().equals("erl" + currentExperiment.getId() + "@127.0.0.1"))
            throw new CommunicationException();
        if (destination == null) {
            destination = sender;
            erlangProcess.link(destination);
        }
        return tuple;

    }

    public ExperimentRound receiveRound() {
        try {
            OtpErlangTuple result = receive();
            if (result == null) {
                return null;
            }
            OtpErlangAtom msgType = (OtpErlangAtom) result.elementAt(1);
            if (msgType.toString().equals("error")) {
                throw new CommunicationException(result.elementAt(2).toString());
            } else if (msgType.toString().equals("completed")) {
                long elapsedTime = (System.nanoTime() - start) / 1000000;
                return new ExperimentRound(true, result.elementAt(2).toString(), elapsedTime);
            } else { //round
                ExperimentRound round = composeRound(result);
                System.out.println(round);
                return round;
            }
        } catch (OtpErlangExit e) {
            if (!e.reason().toString().equals("normal")) {
                return new ExperimentRound(true, e.toString());
            }
        } catch (Exception e) {
            return new ExperimentRound(true, e.toString());
        }
        return null;
    }

    private ExperimentRound composeRound(OtpErlangTuple result) {
        OtpErlangTuple content = (OtpErlangTuple) result.elementAt(2);
        OtpErlangTuple algorithmContent = (OtpErlangTuple) content.elementAt(0);
        AlgorithmRound algRound = currentExperiment.getAlgorithm().getIterationInfo(algorithmContent);
        OtpErlangList clientsContent = (OtpErlangList) content.elementAt(2);
        List<Client> clients = convertClientsList(clientsContent);
        OtpErlangList stateClientsContent = (OtpErlangList) content.elementAt(3);
        List<Client> stateClients = convertClientsList(stateClientsContent);
        return new ExperimentRound(Integer.parseInt(content.elementAt(4).toString()), Integer.parseInt(content.elementAt(1).toString()), algRound, clients, stateClients);
    }

    private List<Client> convertClientsList(OtpErlangList clientsContent) {
        List<Client> clients = new ArrayList<>();
        for (OtpErlangObject element : clientsContent) {
            OtpErlangTuple tupleElement = (OtpErlangTuple) element;
            OtpErlangList chunkList = (OtpErlangList) tupleElement.elementAt(2);
            List<List<Double>> points = new ArrayList<>();
            List<Double> point;
            for(OtpErlangObject elemChunk: chunkList) {
                OtpErlangList pointChunk = (OtpErlangList) elemChunk;
                point = new ArrayList<>();
                for(OtpErlangObject coordinate: pointChunk) {
                    point.add(Double.parseDouble(coordinate.toString()));
                }
                points.add(point);
            }
            Client client = new Client(tupleElement.elementAt(0).toString(), tupleElement.elementAt(1).toString(), points, Integer.parseInt(tupleElement.elementAt(3).toString()));
            clients.add(client);
        }
        return clients;
    }
}


