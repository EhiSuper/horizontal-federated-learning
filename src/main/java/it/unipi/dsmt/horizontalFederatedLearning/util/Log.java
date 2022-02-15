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

    public Log(){
    }
    public Log(String fileName){
        logFile = fileName;
    }

    public static void startLogExperiment(Experiment experiment){
        try {
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            bw.write("---- Log experiment " + experiment.getId() + " " + dtf.format(now) + " ----");
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logExperimentLine(String line){
        try {
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getLogExperimentText(Experiment experiment) {
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
                        //result.add(line);
                        exp = true;
                    }
                    else{
                        exp = false;
                    }
                }
                if(exp) {
                    result.add(line);
                    /*String[] parsedLine = line.split("#");
                    if(parsedLine.length > 1 && parsedLine[0].equals(experiment.getId() + " ")){
                        result.add(parsedLine[1]);
                    }*/
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
