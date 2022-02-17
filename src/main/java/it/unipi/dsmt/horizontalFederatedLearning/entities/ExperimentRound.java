package it.unipi.dsmt.horizontalFederatedLearning.entities;

import java.util.List;

public class ExperimentRound {
    private Experiment experiment;
    private int round;
    private int numCrashes;
    private AlgorithmRound algorithmRound;
    private List<Client> involvedClients;
    private List<Client> clientsState;
    private boolean last;
    private String reason;
    private long time;

    public ExperimentRound(){}

    public ExperimentRound(int round, int numCrashes, AlgorithmRound algorithmRound, List<Client> involvedClients, List<Client> clientsState ) {
        this.round = round;
        this.numCrashes = numCrashes;
        this.algorithmRound = algorithmRound;
        this.involvedClients = involvedClients;
        this.clientsState = clientsState;
    }

    public ExperimentRound(boolean last, String reason, long time) {
        this.last = last;
        this.reason = reason;
        this.time = time;
    }

    public ExperimentRound(boolean last, String reason) {
        this.last = last;
        this.reason = reason;
    }

    public void setNumCrashes(int numCrashes) {
        this.numCrashes = numCrashes;
    }

    public int getNumCrashes() {
        return numCrashes;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public AlgorithmRound getAlgorithmRound() {
        return algorithmRound;
    }

    public void setAlgorithmRound(AlgorithmRound algorithmRound) {
        this.algorithmRound = algorithmRound;
    }

    public List<Client> getInvolvedClients() {
        return involvedClients;
    }

    public void setInvolvedClients(List<Client> involvedClients) {
        this.involvedClients = involvedClients;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean getLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Client> getClientsState() {
        return clientsState;
    }

    public void setClientsState(List<Client> clientsState) {
        this.clientsState = clientsState;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    // anche output clients
    public String toString(){
        String result = "";
        if(last)
            return "{ reason of termination: " + reason + "}";
        else {
            result += "{ round: " + round + ", numCrashes: " + numCrashes + ", algorithmRound: " + algorithmRound + ", ";
            result += "involvedClients: ";
            for(Client client: involvedClients)
                result += client;
            result += ", clientsState: ";
            for(Client client: clientsState)
                result += client;
            result += " }";
            return result;
        }
    }
}
