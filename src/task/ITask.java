package task;

/**
 * Represents a result bearing task
 *
 * @param <T>
 */
public interface ITask<T> {
    int timesToRun = 10;
    int sleepMillis = 1000;

    default int getTimesToRun(){
        return this.timesToRun;
    }
    default int getSleepMillis(){
        return this.sleepMillis;
    }
    String getTaskName();
    String getTaskValue();
    default Boolean getDebug(){
        return false;
    };

    /**
     * A task if complete if its objective
     * has been met through an invokation
     * of the 'call' method.
     *
     */
    boolean isComplete();

    /**
     * Does the actual work and returns
     * a result.
     *
     */
    T call();
}

