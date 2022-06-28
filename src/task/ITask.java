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
     * @return the amount of times to run the task, returns the default amount if none is provided
     */
    default int getTimesToRun(){
        return this.timesToRun;
    }
    /**
     * @return the delay between runs for the task, returns the default amount if none is provided
     */
    default int getSleepMillis(){
        return this.sleepMillis;
    }
    /**
     * @return the name of the task , mainly used for printing pretty
     */
    String getTaskName();
    //this would probably be renamed or moved as other tasks may not need a value
    /**
     * @return the value provided for the task
     */
    String getTaskValue();

    /**
     * A task if complete if its objective
     * has been met through an invokation
     * of the 'call' method.
     * @return whether the task is returned or not
     */
    boolean isComplete();

    /**
     * calls the callback function if its provided
     */
    void methodToCallback();

    /**
     * Does the actual work and returns
     * a result.
     * @return the result from the task
     */
    T call();
}

