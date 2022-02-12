package it.unipi.dsmt.horizontalFederatedLearning.entities;

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
        String result = "{ fNorm: " + fNorm + ", centers: {";
        for(int i = 0; i < centers.size(); ++i){
            result += "[";
            List<Double> list = centers.get(i);
            for(int j = 0; j < list.size(); ++j) {
                result += list.get(j);
                if(j != list.size()-1)
                    result += ",";
            }
            result += "]";
            if(i != centers.size()-1)
                result += ",";
        }
        result += "}}";
        return result;
    }
}
