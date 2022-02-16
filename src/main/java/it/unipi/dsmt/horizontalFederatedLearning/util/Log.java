package it.unipi.dsmt.horizontalFederatedLearning.util;

import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private static String logFile;

    static {
        logFile = "experimentsLog.txt";
    }

    public static synchronized void logExperiment(List<String> lines, Experiment experiment){
        try {
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            bw.write("---- Log experiment " + experiment.getId() + " " + dtf.format(now) + " ----");
            bw.newLine();
            for(String line: lines) {
                line = line.replace("'", "");
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getLogExperiment(Experiment experiment) {
        int id = experiment.getId();
        boolean exp = false;
        List<String> result = new ArrayList<>();
        try {
            File file = new File("experimentsLog.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("----")){
                    int expId = Integer.parseInt(line.split("---- Log experiment ")[1].split(" ")[0]);
                    if(id == expId){
                        result.clear();
                        exp = true;
                    }
                    else{
                        exp = false;
                    }
                }
                if(exp) {
                    result.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
