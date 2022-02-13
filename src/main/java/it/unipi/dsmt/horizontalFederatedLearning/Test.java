package it.unipi.dsmt.horizontalFederatedLearning;

import com.ericsson.otp.erlang.*;
import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Node;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;
import it.unipi.dsmt.horizontalFederatedLearning.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args){
        LevelDB db = LevelDB.getInstance();
        UserService myUserService = new UserService(db);
        ExperimentService myExperimentService = new ExperimentService(db);
        /*List<String> list = db.iterateDB();
        for(String elem: list)
                System.out.println(elem);*/
        /*
        User user = new User(1, "Franco", "Terranova", "franchinino", "terranova");
        UserService.register(user);
        UserService.login("franchino", "terranova");*/
        User user = myUserService.findUserByUsername("franchino");
        Experiment experiment = new Experiment();
        experiment.setId(2);
        experiment.setName("Experiment5");
        experiment.setDataset("https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff");
        //experiment.setDataset("https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/cpu.arff");
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
        clients.add("node@172.18.0.18");
        clients.add("node@172.18.0.42");
        clients.add("node@172.18.0.43");
        experiment.setClientsHostnames(clients);
        KMeansAlgorithm algorithm = new KMeansAlgorithm();
        algorithm.setDistance("numba_norm");
        algorithm.setEpsilon(0.001);
        algorithm.setNormFn("norm_fro");
        algorithm.setNumClusters(4);
        algorithm.setSeedCenters(100);
        experiment.setAlgorithm(algorithm);
        Communication.startExperiment(experiment);
        ExperimentRound round = null;
        List<ExperimentRound> rounds = new ArrayList<>();
        while(true) {
            try {
                round = Communication.receiveRound();
                if(round != null)
                    rounds.add(round);
            } catch(ErlangErrorException ex){
                System.out.println("Error during erlang computations: " + ex.getMessage());
                continue;
            }
            if(round == null){
                System.out.println("Experiment completed");
                break;
            }
            //round contiene le info di quel round
        }
        for(int i = 0; i<rounds.size(); ++i){
            if(rounds.get(i) != null)
                System.out.println(rounds.get(i));
        }
        KMeansAlgorithmRound lastRound = (KMeansAlgorithmRound) rounds.get(rounds.size()-2).getAlgorithmRound();
        System.out.println(lastRound);;
        algorithm.setCenters(lastRound.getCenters());
        algorithm.setfNorm(lastRound.getfNorm());
        // altri settaggi
        myExperimentService.insert(experiment);
        List<String> list = db.iterateDB();
        for(String elem: list)
            System.out.println(elem);
        Experiment foundExperiment = myExperimentService.findExperimentById(experiment.getId());
        KMeansAlgorithm km = (KMeansAlgorithm) foundExperiment.getAlgorithm();
        /*System.out.println(km.getfNorm());
        List<List<Double>> centers = km.getCenters();
        String result = "";
        for(int i = 0; i < centers.size(); ++i){
            result += "[";
            List<Double> center = centers.get(i);
            for(int j = 0; j < center.size(); ++j) {
                result += center.get(j);
                if(j != center.size()-1)
                    result += ",";
            }
            result += "]";
            if(i != centers.size()-1)
                result += ",";
        }
        System.out.println(result);*/
        /*result = result.substring(1, result.length()-1);
        List<String> centersString = Arrays.asList(result.split("],"));
        List<List<Double>> list = new ArrayList<>();
        List<Double> elem;
        for(String centerString: centersString){
            System.out.println(centersString);
            elem = new ArrayList<>();
            centerString = centerString.substring(1,centerString.length()-1);
            for(String valueString: centerString.split(",")){
                System.out.println(valueString);
                elem.add(Double.parseDouble(valueString));
            }
            list.add(elem);
        }
        result = "";
        for(int i = 0; i < list.size(); ++i){
            result += "[";
            List<Double> x = list.get(i);
            for(int j = 0; j < x.size(); ++j) {
                result += x.get(j);
                if(j != x.size()-1)
                    result += ",";
            }
            result += "]";
            if(i != list.size()-1)
                result += ",";
        }
        System.out.println(result);
        */
        Log.exportLogExperiment(experiment);
        //creato oggetto esperimento e richiesta esecuzione va aggiornato oggetto esperimento
        // vedere come chiedere esecuzione esperimento
    }
}
