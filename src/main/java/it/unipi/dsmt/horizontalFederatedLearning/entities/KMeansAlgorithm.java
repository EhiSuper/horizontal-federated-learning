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

    public KMeansAlgorithm(){ super("KMeans"); }

    public KMeansAlgorithm(int numClusters, double epsilon, String distance, double seedCenters, String normFn) {
        super("KMeans");
        this.numClusters = numClusters;
        this.epsilon = epsilon;
        this.distance = distance;
        this.seedCenters = seedCenters;
        this.normFn = normFn;
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

    @Override
    public AlgorithmRound getIterationInfo(OtpErlangTuple algorithmContent) {
        OtpErlangList centersContent = (OtpErlangList)algorithmContent.elementAt(0);
        //System.out.println("centersContent: " + centersContent);
        List<List<Double>> centers = new ArrayList<>();
        List<Double> center = new ArrayList<>();
        for(OtpErlangObject element: centersContent) {
            OtpErlangList centerList = (OtpErlangList) element;
            for(OtpErlangObject coordinate: centerList)
                center.add(Double.parseDouble(coordinate.toString()));
            centers.add(center);
        }
        return new KMeansAlgorithmRound(centers, Double.parseDouble(algorithmContent.elementAt(1).toString()));
    }

    @Override
    public OtpErlangTuple prepareSpecificParameters() {
        ArrayList<OtpErlangObject> objects = new ArrayList<>();
        //start( {NumClusters, Distance, Mode, Epsilon, SeedCenters, NormFn}, MaxAttemptsServerCrash).
        objects.add((new OtpErlangInt(numClusters)));
        objects.add((new OtpErlangString(distance)));
        objects.add((new OtpErlangInt(1)));
        objects.add((new OtpErlangDouble(epsilon)));
        objects.add((new OtpErlangDouble(seedCenters)));
        objects.add((new OtpErlangString(normFn)));
        OtpErlangObject[] array2 = new OtpErlangObject[objects.size()];
        OtpErlangObject[] array = objects.toArray(array2);
        OtpErlangTuple tuple = new OtpErlangTuple(array);
        return tuple;
    }
}
