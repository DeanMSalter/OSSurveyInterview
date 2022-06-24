package task;

/**
 * Represents a result bearing task
 *
 * @param <T>
 */
public interface ITask<T> {
    /**
     * A task if complete if its objective
     * has been met through an invokation
     * of the 'call' method.
     *
     */
    public boolean isComplete();

    /**
     * Does the actual work and returns
     * a result.
     *
     */
    public T call();
}

