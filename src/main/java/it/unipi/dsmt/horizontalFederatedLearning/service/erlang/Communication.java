package it.unipi.dsmt.horizontalFederatedLearning.service.erlang;

import com.ericsson.otp.erlang.*;
import it.unipi.dsmt.horizontalFederatedLearning.entities.AlgorithmRound;
import it.unipi.dsmt.horizontalFederatedLearning.entities.Client;
import it.unipi.dsmt.horizontalFederatedLearning.entities.ExperimentRound;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithmRound;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;

import java.util.ArrayList;
import java.util.List;

public class Communication {
    private OtpErlangPid destination;

    public void setDestination(String node, int id, int serial, int creation) {
        this.destination = new OtpErlangPid(node, id, serial, creation);
    }
    // accertarsi ricezione da chi vogliamo noi
    private OtpErlangTuple receive(Node node) throws ErlangErrorException{
        OtpErlangTuple tuple = null;
        try {
            tuple = (OtpErlangTuple) node.getOtpMbox().receive();
            if(destination == null)
                destination = (OtpErlangPid)tuple.elementAt(0);
        } catch (OtpErlangExit e) {
            e.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        return tuple;
    }

    public ExperimentRound receiveRound(Node node) throws ErlangErrorException {
        OtpErlangTuple result = receive(node);
        OtpErlangAtom msgType = (OtpErlangAtom)result.elementAt(1);
        System.out.println("msgtype: " + msgType.toString());
        if(msgType.toString().equals("error")) {
            //System.out.println("error: " + result.elementAt(2).toString());
            throw new ErlangErrorException(result.elementAt(2).toString());
        } else if(msgType.toString().equals("completed")) {
            //System.out.println("completed: " + result.elementAt(2).toString());
            return new ExperimentRound(true, result.elementAt(2).toString());
        } else { //round
            OtpErlangTuple content = (OtpErlangTuple) result.elementAt(2);
            //System.out.println("round: " + content);
            OtpErlangTuple algorithmContent = (OtpErlangTuple) content.elementAt(0);
            //System.out.println("Algorithm Content: " + content);
            // deve diventare chiamata generica
            AlgorithmRound algRound = getKMeansIterationInfo(algorithmContent);
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
    }

    // mettere in un luogo independent da logica generale
    private AlgorithmRound getKMeansIterationInfo(OtpErlangTuple algorithmContent){
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

    private void send(Node node, String content){
        OtpErlangString msg = new OtpErlangString(content);
        OtpErlangTuple responseTuple = new OtpErlangTuple(new OtpErlangObject[]{node.getOtpMbox().self(), msg});
        node.getOtpMbox().send(destination, responseTuple);
    }
}


