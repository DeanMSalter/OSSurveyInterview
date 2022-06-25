package util.tasks;

import task.ITask;
import task.ITaskCallBack;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;


public class PortAvailableTask implements ITask<Boolean>{
    private final int port;
    private int timesToRun = 10;
    private int sleepMillis = 1000;
    private ITaskCallBack callback;

    private Boolean isComplete = false;
    //If you want to disable the print messages set this to false

    public PortAvailableTask(int port) {
        this.port = port;
    }
    public PortAvailableTask(int port, int timesToRun, int sleepMillis) {
        this.port = port;
        this.timesToRun = timesToRun;
        this.sleepMillis = sleepMillis;
    }
    public PortAvailableTask(int port, int timesToRun, int sleepMillis, ITaskCallBack callback) {
        this.port = port;
        this.timesToRun = timesToRun;
        this.sleepMillis = sleepMillis;
        this.callback = callback;
    }
    @Override
    public boolean isComplete() {
        return this.isComplete;
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
    public String getTaskName(){
        return "Port available checker";
    }

    @Override
    public String getTaskValue() {
        return String.valueOf(this.port);
    }
    @Override
    public void methodToCallback() {
        if (this.callback == null){
            return;
        }
        this.callback.handleAction();
    }
    //Source for port checking code:
    //http://svn.apache.org/viewvc/camel/trunk/components/camel-test/src/main/java/org/apache/camel/test/AvailablePortFinder.java?view=markup#l130
    @Override
    public Boolean call() {
        //Not too familiar with working with ports so would be keen to know if there's a better method for checking ports
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            this.isComplete = true;
            return true;
            //Not a huge fan of using try/catches for this type of thing
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return false;
    }

}
