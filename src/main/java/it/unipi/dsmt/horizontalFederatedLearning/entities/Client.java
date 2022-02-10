package it.unipi.dsmt.horizontalFederatedLearning.entities;

public class Client {
    private String hostname;
    private String pid;
    private int numCrashes; //or numSpawns

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
}
