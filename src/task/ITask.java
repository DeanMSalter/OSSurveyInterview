package task;

/**
 * Represents a result bearing task
 *
 * @param <T>
 */
public interface ITask<T> {
    int timesToRun = 10;
    int sleepMillis = 1000;

    /**
     * gets the amount of times to run the task, returns the default amount if none is provided
     */
    default int getTimesToRun(){
        return this.timesToRun;
    }
    /**
     * gets the delay between runs for the task, returns the default amount if none is provided
     */
    default int getSleepMillis(){
        return this.sleepMillis;
    }
    /**
     * gets the name of the task , mainly used for printing pretty
     */
    String getTaskName();
    //this would probably be renamed or moved as other tasks may not need a value
    /**
     * gets the value provided for the task
     */
    String getTaskValue();

    /**
     * A task if complete if its objective
     * has been met through an invokation
     * of the 'call' method.
     *
     */
    boolean isComplete();

    /**
     * calls the callback function if its provided
     */
    void methodToCallback();

    /**
     * Does the actual work and returns
     * a result.
     *
     */
    T call();
}

