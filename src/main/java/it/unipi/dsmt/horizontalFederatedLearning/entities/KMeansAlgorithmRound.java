package it.unipi.dsmt.horizontalFederatedLearning.entities;

import com.ericsson.otp.erlang.*;

import java.util.ArrayList;
import java.util.List;

public class KMeansAlgorithmRound implements AlgorithmRound{
    private List<List<Double>> centers;
    private double fNorm;

    public KMeansAlgorithmRound(List<List<Double>> centers, double fNorm) {
        this.centers = centers;
        this.fNorm = fNorm;
    }

    public double getfNorm() {
        return fNorm;
    }

    public void setfNorm(double fNorm) {
        this.fNorm = fNorm;
    }

    public List<List<Double>> getCenters() {
        return centers;
    }

    public void setCenters(List<List<Double>> centers) {
        this.centers = centers;
    }

    public String toString(){
        String result = "{ fNorm: " + fNorm + ", centers: ";
        for(List<Double> center: centers){
            result += "[";
            for(Double value: center)
                result += value + ",";
            result +="]";
        }
        result += "}";
        return result;
    }
}
