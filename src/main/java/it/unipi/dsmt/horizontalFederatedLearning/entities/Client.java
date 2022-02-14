package it.unipi.dsmt.horizontalFederatedLearning.entities;

import java.util.List;

public class Client {
    private String hostname;
    private String pid;
    private List<List<Double>> chunk;
    private int numCrashes; //or numSpawns

    public Client(String hostname, String pid, List<List<Double>> chunk, int numCrashes) {
        this.hostname = hostname;
        this.pid = pid;
        this.chunk = chunk;
        this.numCrashes = numCrashes;
    }

    public Client(String hostname, String pid, int numCrashes) {
        this.hostname = hostname;
        this.pid = pid;
        this.numCrashes = numCrashes;
    }

    public int getNumCrashes() {
        return numCrashes;
    }

    public void setNumCrashes(int numCrashes) {
        this.numCrashes = numCrashes;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String toString(){
        return "{ Hostname: "+ hostname +", Pid:" + pid + ", NumCrashes:" + numCrashes + " }";
    }


    public List<List<Double>> getChunk() {
        return chunk;
    }

    public void setChunk(List<List<Double>> chunk) {
        this.chunk = chunk;
    }

    public String getStringChunk(){
        String result = "";
        for(int i = 0; i < chunk.size(); ++i){
            result += "[";
            List<Double> list = chunk.get(i);
            for(int j = 0; j < list.size(); ++j) {
                result += list.get(j);
                if(j != list.size()-1)
                    result += ",";
            }
            result += "]";
            if(i != chunk.size()-1)
                result += ",";
        }
        result += "}}";
        return result;
    }
}
