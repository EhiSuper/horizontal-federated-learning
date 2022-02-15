package it.unipi.dsmt.horizontalFederatedLearning.service.erlang;

import com.ericsson.otp.erlang.*;

import java.io.IOException;

public class Node {
    private OtpNode otpNode;
    private OtpMbox otpMbox;

    public Node(String nodeId, String cookie, String mBox){
        try {
            this.otpNode = new OtpNode(nodeId, cookie);
            this.otpMbox = otpNode.createMbox(mBox);
        } catch(IOException ioe){
        }
    }

    public OtpNode getOtpNode() {
        return otpNode;
    }

    public OtpMbox getOtpMbox() {
        return otpMbox;
    }

    public void setOtpMbox(OtpMbox otpMbox) {
        this.otpMbox = otpMbox;
    }

    public void setOtpNode(OtpNode otpNode) {
        this.otpNode = otpNode;
    }
}


