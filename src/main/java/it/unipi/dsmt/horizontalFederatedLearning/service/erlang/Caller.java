package it.unipi.dsmt.horizontalFederatedLearning.service.erlang;
import com.ericsson.otp.erlang.*;

public class Caller {
    private static volatile Caller instance;
    private OtpSelf callerNode;
    private OtpPeer supervisorNode;
    private OtpConnection callerConnection;

    private Caller(){
        try{
            callerNode = new OtpSelf("caller", "COOKIE");
            supervisorNode = new OtpPeer("erl@127.0.0.1");
            callerConnection = callerNode.connect(supervisorNode);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Singleton Pattern
    public static Caller getCaller() {
        if (instance == null) {
            synchronized (Caller.class) {
                if (instance == null) {
                    instance = new Caller();
                }
            }
        }
        return instance;
    }

    public OtpSelf getCallerNode() {
        return callerNode;
    }

    public OtpPeer getSupervisorNode() {
        return supervisorNode;
    }

    public OtpConnection getCallerConnection() {
        return callerConnection;
    }
}
