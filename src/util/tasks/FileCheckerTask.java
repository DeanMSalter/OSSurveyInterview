package util.tasks;

import task.ITask;
import task.ITaskCallBack;

import javax.security.auth.callback.Callback;
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
    private ITaskCallBack callback;
    private Boolean isComplete = false;

    public FileCheckerTask(String fileName) {
        this.fileName = fileName;
    }
    public FileCheckerTask(String fileName, int timesToRun, int sleepMillis) {
        this.fileName = fileName;
        this.timesToRun = timesToRun;
        this.sleepMillis = sleepMillis;
    }
    public FileCheckerTask(String fileName, int timesToRun, int sleepMillis, ITaskCallBack callback) {
        this.fileName = fileName;
        this.timesToRun = timesToRun;
        this.sleepMillis = sleepMillis;
        this.callback = callback;
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
    public void methodToCallback() {
        if (this.callback == null){
            return;
        }
        this.callback.handleAction();
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
