package it.unipi.dsmt.horizontalFederatedLearning.service.db;

import com.google.common.collect.Lists;
import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.User;
import org.iq80.leveldb.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class LevelDB {
    private static volatile LevelDB instance;
    private DB db = null;
    private String pathDatabase;

    //Private Constructor
    private LevelDB(String pathDatabase){
        this.pathDatabase = pathDatabase;
        openDB();
    }

    //Singleton Pattern
    public static LevelDB getInstance(){
        if(instance == null){
            synchronized (LevelDB.class){
                if(instance == null){
                    instance = new LevelDB("KeyValueRepository");
                }
            }
        }
        return instance;
    }

    public void openDB(){
        Options options = new Options();
        // check if we have problems with lexicographic order or we need to define a comparator
        options.createIfMissing(true);
        try {
            db = factory.open(new File(pathDatabase), options);
        } catch(IOException ioe){
            closeDB();
        }
        //addExperiment();
        //addAdmin();
    }

    public void closeDB(){
        try {
            if(db != null) {
                db.close();
                db = null;
            }
        } catch(IOException ioe) { ioe.printStackTrace();}
    }

    public boolean isDBOpen(){
        return db != null;
    }

    public void putValue(String key, String value){
        db.put(bytes(key), bytes(value));
    }

    public String getValue(String key){
        return asString(db.get(bytes(key)));
    }

    public void deleteValue(String key){
        db.delete(bytes(key));
    }

    public void putBatchValues(HashMap<String, String> entries){
        try (WriteBatch batch = db.createWriteBatch()) {
            for (int i = 0; i < entries.size(); ++i) {
                batch.put(bytes((String) entries.keySet().toArray()[i]), bytes(entries.get(entries.keySet().toArray()[i])));
            }
            db.write(batch);
        } catch(IOException ioe) { ioe.printStackTrace();}
    }

    public List<String> iterateDB(){
        List<String> results = new ArrayList<>();
        try(DBIterator iterator = db.iterator()){
            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()){
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                results.add(key + " " + value);
            }
        } catch(IOException ioe){ ioe.printStackTrace();}
        return results;
    }

    public void addExperiment(){
        UserService myUserService = new UserService(this);
        new ExperimentService(this);
        User user = myUserService.findUserByUsername("antonio");
        Experiment experiment = new Experiment();
        experiment.setName("Experiment3");
        experiment.setDataset("https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff");
        experiment.setLastUpdateDate(LocalDate.now());
        experiment.setCreationDate(LocalDate.now());
        experiment.setNumFeatures(2);
        experiment.setMode(1);
        experiment.setUser(user);
        experiment.setNumRounds(4);
        experiment.setMaxNumRounds(10);
        experiment.setNumCrashes(5);
        experiment.setNumClients(3);
        experiment.setNumMinClients(3);
        experiment.setRandomClients(false);
        experiment.setRandomClientsSeed(0);
        experiment.setTimeout(25000);
        experiment.setMaxAttemptsClientCrash(3);
        experiment.setMaxAttemptsOverallCrash(20);
        experiment.setMaxAttemptsServerCrash(2);
        List<String> clients = new ArrayList<>();
        clients.add("x@localhost");
        clients.add("y@localhost");
        clients.add("z@localhost");
        clients.add("h@localhost");
        experiment.setClientsHostnames(clients);
        KMeansAlgorithm algorithm = new KMeansAlgorithm();
        algorithm.setDistance("numba_norm");
        algorithm.setEpsilon(0.05);
        algorithm.setNormFn("norm_fro");
        algorithm.setNumClusters(3);
        algorithm.setSeedCenters(100);
        experiment.setAlgorithm(algorithm);
        ExperimentService myExperimentService = new ExperimentService(this);
        myExperimentService.insert(experiment);
        List<String> list = this.iterateDB();
        for(String elem: list)
            System.out.println(elem);
    }

    public void addAdmin(){
        User user = new User("Capo", "Capo", "admin", "admin", true);
        UserService myUserService = new UserService(this);
        myUserService.register(user);
    }

    public void printContent(){
        List<String> results = iterateDB();
        for(String elem: results)
            System.out.println(elem);
    }

    public List<String> findKeysByPrefix(String prefix) throws RuntimeException{
        try(DBIterator iterator = db.iterator()){
            List<String> keys = Lists.newArrayList();
            for(iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()){
                String key = asString(iterator.peekNext().getKey());
                if(!key.startsWith(prefix))
                        break;
                keys.add(key);
            }
            return keys;
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

    public List<String> findValuesByPrefix(String prefix) throws RuntimeException{
        try(DBIterator iterator = db.iterator()){
            List<String> values = Lists.newArrayList();
            for(iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()){
                String key = asString(iterator.peekNext().getKey());
                if(!key.startsWith(prefix))
                    break;
                values.add(getValue(key));
            }
            return values;
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> findByPrefix(String prefix) throws RuntimeException{
        try(DBIterator iterator = db.iterator()){
            HashMap<String, String> entries = new HashMap<>();
            for(iterator.seek(bytes(prefix)); iterator.hasNext(); iterator.next()){
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                if(!key.startsWith(prefix))
                    break;
                entries.put(key, value);
            }
            return entries;
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

}

