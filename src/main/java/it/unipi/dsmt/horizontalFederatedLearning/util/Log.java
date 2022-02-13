package it.unipi.dsmt.horizontalFederatedLearning.util;

import it.unipi.dsmt.horizontalFederatedLearning.entities.Experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private String logFile;

    public Log(String fileName){
        logFile = fileName;
    }

    public void startLogExperiment(Experiment experiment){
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

    public void logExperimentLine(String line){
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

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }
}
