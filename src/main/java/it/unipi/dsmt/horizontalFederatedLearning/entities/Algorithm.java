package it.unipi.dsmt.horizontalFederatedLearning.entities;


import com.ericsson.otp.erlang.OtpErlangTuple;

public abstract class Algorithm {
    private String name;

    public Algorithm(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract AlgorithmRound getIterationInfo(OtpErlangTuple algorithmContent);

    public abstract OtpErlangTuple prepareSpecificParameters();
}

