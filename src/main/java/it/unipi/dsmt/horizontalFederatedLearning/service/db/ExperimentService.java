package it.unipi.dsmt.horizontalFederatedLearning.service.db;

import it.unipi.dsmt.horizontalFederatedLearning.entities.Algorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.User;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.RegistrationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class ExperimentService {
    private int counterID;
    private LevelDB db;

    public ExperimentService(LevelDB db) {
        if (!db.isDBOpen())
            db.openDB();
        this.db = db;
        setCounterID();
    }

    private void setCounterID() {
        List<String> keys = db.findKeysByPrefix("Experiment:");
        for (String key : keys) {
            int id = Integer.parseInt(key.split(":")[1]);
            if (id > counterID)
                counterID = id;
        }
    }

    public void insert(Experiment experiment) throws RegistrationException{
        HashMap<String, String> map = new HashMap<>();
        if(findExperimentByName(experiment.getName()) != null)
            throw new RegistrationException("Experiment name already taken!");
        experiment.setId(++counterID);
        String prefixKey = "Experiment:" + experiment.getId() + ":" + experiment.getUser().getId() + ":";
        map.put(prefixKey + "name", experiment.getName());
        map.put(prefixKey + "dataset", experiment.getDataset());
        map.put(prefixKey + "numFeatures", Integer.toString(experiment.getNumFeatures()));
        map.put(prefixKey + "mode", Integer.toString(experiment.getMode()));
        map.put(prefixKey + "creationDate", experiment.getCreationDate().toString());
        map.put(prefixKey + "lastUpdateDate", experiment.getLastUpdateDate().toString());
        map.put(prefixKey + "numRounds", Integer.toString(experiment.getNumRounds()));
        map.put(prefixKey + "maxNumRounds", Integer.toString(experiment.getMaxNumRounds()));
        map.put(prefixKey + "numClients", Integer.toString(experiment.getNumClients()));
        map.put(prefixKey + "numMinClients", Integer.toString(experiment.getNumMinClients()));
        map.put(prefixKey + "clientsHostnames", String.join(",", experiment.getClientsHostnames()));
        map.put(prefixKey + "randomClients", Boolean.toString(experiment.getRandomClients()));
        map.put(prefixKey + "randomClientsSeed", Double.toString(experiment.getRandomClientsSeed()));
        map.put(prefixKey + "timeout", Double.toString(experiment.getTimeout()));
        map.put(prefixKey + "maxAttemptsClientCrash", Integer.toString(experiment.getMaxAttemptsClientCrash()));
        map.put(prefixKey + "maxAttemptsServerCrash", Integer.toString(experiment.getMaxAttemptsServerCrash()));
        map.put(prefixKey + "maxAttemptsOverallCrash", Integer.toString(experiment.getMaxAttemptsOverallCrash()));
        insertAlgorithm(experiment, map);
        db.putBatchValues(map);
    }

    public void insertAlgorithm(Experiment experiment, HashMap<String, String> map) {
        String prefixKey = "Experiment:" + experiment.getId() + ":" + experiment.getUser().getId() + ":Algorithm:";
        map.put(prefixKey + "name", experiment.getAlgorithm().getName());
        // logica dipendente
        if(experiment.getAlgorithm().getName().equals("KMeans")){
            KMeansAlgorithm kMeansAlgorithm = (KMeansAlgorithm) experiment.getAlgorithm();
            map.put(prefixKey + "numClusters", Integer.toString(kMeansAlgorithm.getNumClusters()));
            map.put(prefixKey + "epsilon", Double.toString(kMeansAlgorithm.getEpsilon()));
            map.put(prefixKey + "distance", kMeansAlgorithm.getDistance());
            map.put(prefixKey + "seedCenters", Double.toString(kMeansAlgorithm.getSeedCenters()));
            map.put(prefixKey + "normFn", kMeansAlgorithm.getNormFn());
        }
    }

    public List<Experiment> readAllExperiments() {
        List<Experiment> experiments = new ArrayList<>();
        for(int i = 0; i < counterID; ++i){
            experiments.add(findExperimentById(i));
        }
        return experiments;
    }

    public void deleteExperimentById(int id) {
        List<String> keys = db.findValuesByPrefix("Experiment:"+id);
        for(String key: keys)
            db.deleteValue(key);
    }

    public void editExperiment(Experiment newExperiment){
        deleteExperimentById(newExperiment.getId());
        insert(newExperiment);
        newExperiment.setId(counterID);
    }

    // altri parametri, una volta definiti
    public Experiment findExperimentById(int id){
        Experiment experiment = new Experiment();
        HashMap<String, String> map = db.findByPrefix("Experiment:"+id);
        for(String key: map.keySet()){
            switch(key.split(":")[3]){
                case "name":
                    experiment.setName(map.get(key));
                    break;
                case "dataset":
                    experiment.setDataset(map.get(key));
                    break;
                case "numFeatures":
                    experiment.setNumFeatures(Integer.parseInt(map.get(key)));
                    break;
                case "mode":
                    experiment.setMode(Integer.parseInt(map.get(key)));
                    break;
                case "creationDate":
                    experiment.setCreationDate(LocalDate.parse(map.get(key)));
                    break;
                case "lastUpdateDate":
                    experiment.setLastUpdateDate(LocalDate.parse(map.get(key)));
                    break;
                case "maxNumRounds":
                    experiment.setMaxNumRounds(Integer.parseInt(map.get(key)));
                    break;
                case "numClients":
                    experiment.setNumClients(Integer.parseInt(map.get(key)));
                    break;
                case "numMinClients":
                    experiment.setNumMinClients(Integer.parseInt(map.get(key)));
                    break;
                case "clientsHostnames":
                    experiment.setClientsHostnames(Arrays.asList((map.get(key).split(","))));
                    break;
                case "randomClients":
                    experiment.setRandomClients(Boolean.parseBoolean(map.get(key)));
                    break;
                case "randomClientsSeed":
                    experiment.setRandomClientsSeed(Double.parseDouble(map.get(key)));
                    break;
                case "timeout":
                    experiment.setTimeout(Integer.parseInt(map.get(key)));
                    break;
                case "maxAttemptsClientCrash":
                    experiment.setNumClients(Integer.parseInt(map.get(key)));
                    break;
                case "maxAttemptsServerCrash":
                    experiment.setNumClients(Integer.parseInt(map.get(key)));
                    break;
                case "maxAttemptsOverallCrash":
                    experiment.setNumClients(Integer.parseInt(map.get(key)));
                    break;
                default:
                    break;
            }
        }
        LevelDB myLevelDB = LevelDB.getInstance();
        UserService myUserService = new UserService(myLevelDB);
        List<String> key = db.findKeysByPrefix("Experiment:"+id);
        experiment.setUser(myUserService.findUserById(Integer.parseInt(key.get(0).split(":")[2])));
        experiment.setAlgorithm(readAlgorithm(id,Integer.parseInt(key.get(0).split(":")[2])));
        return experiment;
    }

    public Experiment findExperimentByName(String name){
        List<String> keys = db.findKeysByPrefix("Experiment:");
        for(String key: keys){
            if(key.endsWith("name") && db.getValue(key).equals(name)){
                int id = Integer.parseInt(key.split(":")[1]);
                return findExperimentById(id);
            }
        }
        return null;
    }

    public Algorithm readAlgorithm(int id, int userId){
        HashMap<String, String> map = db.findByPrefix("Experiment:"+id+":"+userId+"Algorithm");
        String name = db.findValuesByPrefix("Experiment:"+id+":"+userId+":Algorithm:name").get(0);
        Algorithm wrapAlgorithm = null;
        // codice dipendente
        if(name.equals("KMeans")) {
            KMeansAlgorithm algorithm = new KMeansAlgorithm();
            for (String key : map.keySet()) {
                switch (key.split(":")[4]) {
                    case "name":
                        algorithm.setName(map.get(key));
                        break;
                    case "numClusters":
                        algorithm.setNumClusters(Integer.parseInt(map.get(key)));
                        break;
                    case "epsilon":
                        algorithm.setEpsilon(Double.parseDouble(map.get(key)));
                        break;
                    case "distance":
                        algorithm.setDistance(map.get(key));
                        break;
                    case "seedCenters":
                        algorithm.setSeedCenters(Double.parseDouble(map.get(key)));
                        break;
                    case "normFn":
                        algorithm.setNormFn(map.get(key));
                        break;
                }
            }
            wrapAlgorithm = algorithm;
        }
        return wrapAlgorithm;
    }

    public Experiment readExperimentsByUser(User user){
        List<String> keys = db.findKeysByPrefix("Experiment:");
        for(String key: keys){
            if(key.split(":")[2].equals(Integer.toString(user.getId()))){
                int id = Integer.parseInt(key.split(":")[1]);
                return findExperimentById(id);
            }
        }
        return null;
    }
}
