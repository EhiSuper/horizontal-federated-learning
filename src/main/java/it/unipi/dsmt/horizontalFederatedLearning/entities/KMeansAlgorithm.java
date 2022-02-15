package it.unipi.dsmt.horizontalFederatedLearning.entities;

import com.ericsson.otp.erlang.*;

import java.util.ArrayList;
import java.util.List;

public class KMeansAlgorithm extends Algorithm{
    private int numClusters;
    private double epsilon;
    private String distance;
    private double seedCenters;
    private String normFn;
    private List<List<Double>> centers;
    private double fNorm;

    public KMeansAlgorithm(){ super("KMeans"); }

    public KMeansAlgorithm(int numClusters, double epsilon, String distance, double seedCenters, String normFn) {
        super("KMeans");
        this.numClusters = numClusters;
        this.epsilon = epsilon;
        this.distance = distance;
        this.seedCenters = seedCenters;
        this.normFn = normFn;
    }

    public KMeansAlgorithm(int numClusters, double epsilon, String distance, double seedCenters, String normFn, List<List<Double>> centers, double fNorm) {
        super("KMeans");
        this.numClusters = numClusters;
        this.epsilon = epsilon;
        this.distance = distance;
        this.seedCenters = seedCenters;
        this.normFn = normFn;
        this.fNorm = fNorm;
        this.centers = centers;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getSeedCenters() {
        return seedCenters;
    }

    public void setSeedCenters(double seedCenters) {
        this.seedCenters = seedCenters;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getNormFn() {
        return normFn;
    }

    public void setNormFn(String normFn) {
        this.normFn = normFn;
    }

    public void setfNorm(double fNorm) {
        this.fNorm = fNorm;
    }

    public double getfNorm() {
        return fNorm;
    }

    public void setCenters(List<List<Double>> centers) {
        this.centers = centers;
    }

    public List<List<Double>> getCenters() {
        return centers;
    }

    @Override
    public AlgorithmRound getIterationInfo(OtpErlangTuple algorithmContent) {
        OtpErlangList centersContent = (OtpErlangList)algorithmContent.elementAt(0);
        List<List<Double>> centers = new ArrayList<>();
        List<Double> center;
        for(OtpErlangObject element: centersContent) {
            OtpErlangList centerList = (OtpErlangList) element;
            center = new ArrayList<>();
            for(OtpErlangObject coordinate: centerList) {
                center.add(Double.parseDouble(coordinate.toString()));
            }
            centers.add(center);
        }
        return new KMeansAlgorithmRound(centers, Double.parseDouble(algorithmContent.elementAt(1).toString()));
    }

    public String toString(){
        return "{name = " + getName() + ",numClusters = " + numClusters + ",epsilon = " + epsilon + "}";
    }

    public String toStringCenters(){
        String result = "";
        if(centers == null)
            return result;
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
        return result;
    }

    public boolean different(Algorithm alg2){
        if(!alg2.getName().equals("KMeans"))
            return false;
        KMeansAlgorithm kmalg2 = (KMeansAlgorithm) alg2;
        if(!distance.equals(kmalg2.getDistance()) || epsilon != kmalg2.getEpsilon() || !normFn.equals(((KMeansAlgorithm) alg2).normFn)
            || seedCenters != kmalg2.getSeedCenters() || numClusters != kmalg2.getNumClusters())
            return true;
        else return false;
    }

    @Override
    public OtpErlangTuple prepareSpecificParameters() {
        ArrayList<OtpErlangObject> objects = new ArrayList<>();
        //start( {NumClusters, Distance, Mode, Epsilon, SeedCenters, NormFn}, MaxAttemptsServerCrash).
        objects.add((new OtpErlangInt(numClusters)));
        objects.add((new OtpErlangString(distance)));
        objects.add((new OtpErlangDouble(epsilon)));
        objects.add((new OtpErlangDouble(seedCenters)));
        objects.add((new OtpErlangString(normFn)));
        OtpErlangObject[] array2 = new OtpErlangObject[objects.size()];
        OtpErlangObject[] array = objects.toArray(array2);
        OtpErlangTuple tuple = new OtpErlangTuple(array);
        return tuple;
    }
}
