package it.unipi.dsmt.horizontalFederatedLearning.entities;

import java.time.LocalDate;
import java.util.List;

public class Experiment {
    private int id;
    private String name;
    private Algorithm algorithm;
    private String dataset;
    private int numFeatures;
    private int mode;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private User user;
    private int numRounds;
    private int maxNumRounds;
    private int numCrashes;
    private int numClients;
    private int numMinClients;
    private List<String> clientsHostnames;
    private boolean randomClients;
    private double randomClientsSeed;
    private double timeout;
    private int maxAttemptsClientCrash;
    private int maxAttemptsServerCrash;
    private int maxAttemptsOverallCrash;

    public Experiment(){}

    public Experiment(String name, Algorithm algorithm, String dataset, int numFeatures, int mode, User user, LocalDate creationDate,
                      LocalDate lastUpdateDate, int numRounds, int maxNumRounds, int numCrashes, int numClients, int numMinClients,
                      List<String> clientsHostnames, boolean randomClients, double randomClientsSeed, double timeout, int maxAttemptsClientCrash,
                      int maxAttemptsServerCrash, int maxAttemptsOverallCrash) {
        this.id = id;
        this.name = name;
        this.algorithm = algorithm;
        this.dataset = dataset;
        this.numFeatures = numFeatures;
        this.mode = mode;
        this.user = user;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.numRounds = numRounds;
        this.maxNumRounds = maxNumRounds;
        this.numCrashes = numCrashes;
        this.numClients = numClients;
        this.numMinClients = numMinClients;
        this.clientsHostnames = clientsHostnames;
        this.randomClients = randomClients;
        this.randomClientsSeed = randomClientsSeed;
        this.timeout = timeout;
        this.maxAttemptsClientCrash = maxAttemptsClientCrash;
        this.maxAttemptsOverallCrash = maxAttemptsOverallCrash;
        this.maxAttemptsServerCrash = maxAttemptsServerCrash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDate lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public int getMaxNumRounds() {
        return maxNumRounds;
    }

    public void setMaxNumRounds(int maxNumRounds) {
        this.maxNumRounds = maxNumRounds;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public int getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(int numRounds) {
        this.numRounds = numRounds;
    }

    public int getNumCrashes() {
        return numCrashes;
    }

    public void setNumCrashes(int numCrashes) {
        this.numCrashes = numCrashes;
    }

    public int getNumFeatures() {
        return numFeatures;
    }

    public void setNumFeatures(int numFeatures) {
        this.numFeatures = numFeatures;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean getRandomClients() {
        return randomClients;
    }

    public void setRandomClients(boolean randomClients) {
        randomClients = randomClients;
    }

    public double getRandomClientsSeed() {
        return randomClientsSeed;
    }

    public void setRandomClientsSeed(double randomClientsSeed) {
        randomClientsSeed = randomClientsSeed;
    }

    public double getTimeout() {
        return timeout;
    }

    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    public int getMaxAttemptsClientCrash() {
        return maxAttemptsClientCrash;
    }

    public void setMaxAttemptsClientCrash(int maxAttemptsClientCrash) {
        this.maxAttemptsClientCrash = maxAttemptsClientCrash;
    }

    public int getMaxAttemptsOverallCrash() {
        return maxAttemptsOverallCrash;
    }

    public void setMaxAttemptsOverallCrash(int maxAttemptsOverallCrash) {
        this.maxAttemptsOverallCrash = maxAttemptsOverallCrash;
    }

    public int getMaxAttemptsServerCrash() {
        return maxAttemptsServerCrash;
    }

    public void setMaxAttemptsServerCrash(int maxAttemptsServerCrash) {
        this.maxAttemptsServerCrash = maxAttemptsServerCrash;
    }

    public int getNumClients() {
        return numClients;
    }

    public void setNumClients(int numClients) {
        this.numClients = numClients;
    }

    public int getNumMinClients() {
        return numMinClients;
    }

    public void setNumMinClients(int numMinClients) {
        this.numMinClients = numMinClients;
    }

    public List<String> getClientsHostnames() {
        return clientsHostnames;
    }

    public void setClientsHostnames(List<String> clientsHostnames) {
        this.clientsHostnames = clientsHostnames;
    }
}
