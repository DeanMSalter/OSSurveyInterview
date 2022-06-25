package util.tasks;

import task.ITask;

import java.io.File;


/**
 *
 * Checks if the passed in file name exists
 *
 * @param <T>
 */
public class FileCheckerTask implements ITask<Boolean>{
    private final String fileName;
    private int timesToRun = 10;
    private int sleepMillis = 1000;
    private Boolean isComplete = false;

    //If you want to disable the print messages set this to false
    private Boolean debug = true;
    public FileCheckerTask(String fileName) {
        this.fileName = fileName;
    }
    public FileCheckerTask(String fileName, int timesToRun, int sleepMillis) {
        this.fileName = fileName;
        this.timesToRun = timesToRun;
        this.sleepMillis = sleepMillis;
    }
    @Override
    public Boolean getDebug() {
        return debug;
    }
    @Override
    public String getTaskName(){
        return "File checker";
    }

    @Override
    public String getTaskValue() {
        return this.fileName;
    }

    @Override
    public int getTimesToRun(){
        return this.timesToRun;
    }
    @Override
    public int getSleepMillis(){
        return this.sleepMillis;
    }


    @Override
    public boolean isComplete() {
        return this.isComplete;
    }

    @Override
    public Boolean call() {
        File f = new File(this.fileName);
        if (f.exists() && !f.isDirectory()){
            isComplete = true;
            return true;
        }else {
            return false;
        }
    }
}
