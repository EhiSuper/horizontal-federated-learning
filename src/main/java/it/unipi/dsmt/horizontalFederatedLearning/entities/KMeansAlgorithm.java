package it.unipi.dsmt.horizontalFederatedLearning.entities;

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
}
