package it.unipi.dsmt.horizontalFederatedLearning.service.exceptions;

public class ErlangErrorException extends RuntimeException {
    public ErlangErrorException() {
        super("Generic Erlang Error");
    }
    public ErlangErrorException(String message) {
        super(message);
    }
}
