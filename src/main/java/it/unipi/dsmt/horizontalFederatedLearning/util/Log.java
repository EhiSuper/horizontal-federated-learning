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
                        exp = true;
                    } else exp = false;
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

    public static void exportLogExperimentFile(Experiment experiment) {
        int id = experiment.getId();
        boolean exp = false;
        BufferedWriter bw = null;
        FileWriter fw = null;
        File fileLog;
        try {
            fileLog = new File("experiment" + experiment.getId() + ".txt");
            fw = new FileWriter(fileLog, false);
            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File fileAllLogs = new File("experimentsLog.txt");
            FileReader fr = new FileReader(fileAllLogs);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("----")){
                    int expId = Integer.parseInt(line.split("---- Log experiment ")[1].split(" ")[0]);
                    if(id == expId){
                        exp = true;
                    } else exp = false;
                }
                if(exp) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            bw.close();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
