package it.unipi.dsmt.horizontalFederatedLearning;

import com.ericsson.otp.erlang.*;
import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;
import it.unipi.dsmt.horizontalFederatedLearning.entities.ExperimentRound;
import it.unipi.dsmt.horizontalFederatedLearning.entities.KMeansAlgorithm;
import it.unipi.dsmt.horizontalFederatedLearning.entities.User;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.ExperimentService;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.LevelDB;
import it.unipi.dsmt.horizontalFederatedLearning.service.db.UserService;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Communication;
import it.unipi.dsmt.horizontalFederatedLearning.service.erlang.Node;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.ErlangErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args){

        /*LevelDB db = LevelDB.getInstance();
        UserService myUserService = new UserService(db);
        new ExperimentService(db);
        List<String> list = db.iterateDB();
        for(String elem: list)
                System.out.println(elem);
        /*User user = new User(1, "Franco", "Terranova", "franchinino", "terranova");
        UserService.register(user);
        UserService.login("franchino", "terranova");*/
        //User user = myUserService.findUserByUsername("franchino");
        Experiment experiment = new Experiment();
        experiment.setName("Experiment3");
        experiment.setDataset("https://raw.githubusercontent.com/deric/clustering-benchmark/master/src/main/resources/datasets/artificial/xclara.arff");
        experiment.setLastUpdateDate(LocalDate.now());
        experiment.setCreationDate(LocalDate.now());
        experiment.setNumFeatures(2);
        experiment.setMode(1);
        experiment.setUser(new User());
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
        Communication.startExperiment(experiment);
        ExperimentRound round = null;
        while(true) {
            try {
                round = Communication.receiveRound();
            } catch(ErlangErrorException ex){
                System.out.println("Error during erlang computations: " + ex.getMessage());
                continue;
            }
            if(round == null){
                System.out.println("finished experiment");
                break;
            }
            //round contiene le info di quel round
            System.out.println(round);
        }
        //creato oggetto esperimento e richiesta esecuzione va aggiornato oggetto esperimento
        // vedere come chiedere esecuzione esperimento
    }
}
