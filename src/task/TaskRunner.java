package task;

import javax.swing.plaf.FontUIResource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static client.TaskSubmitter.*;

/**
 * Runs a submitted task <code>times</code> number of times
 * and supports a sleep interval of <code>sleepMillis</code> between
 * each run and returns a Future result.
 *
 * This method returns immediately with a Future object
 * which can be used to obtain the result of the task
 * when the task completes.
 *
 * @param task
 * @param times
 * @param sleepMillis
 */

public class TaskRunner {
    private final List<ITask<?>> tasks = new ArrayList<>();
    private final ExecutorService executorService;
    public TaskRunner(){
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public List<ITask<?>> getTasks() {
        return tasks;
    }
    public <V> TaskRunner addTask(ITask<V> task){
        this.tasks.add(task);
        return this;
    }

    public List<Future> runTasks(){
        if (tasks.size() == 0) {
            System.out.println("No tasks added to run. Please add tasks using .addTask()");
            return null;
        }
        List<Future> futures = new ArrayList<>();
        tasks.forEach(task -> {
            futures.add(runTaskAsync(task));
        });
        this.executorService.shutdown();
        return futures;
    }
    //TODO: map the futures to the tasks?
    private <V> Future<V> runTaskAsync(ITask<V> task) {
        if (task.getDebug()) {
            System.out.println(ANSI_RESET + "-------" +  ANSI_YELLOW  + task.getTaskName() + ANSI_RESET + " " +  ANSI_BLUE + task.getTaskValue() + ANSI_RESET + " with " + ANSI_GREEN + task.getTimesToRun() +  ANSI_RESET + " attempts and " + ANSI_PURPLE  +task.getSleepMillis() + ANSI_RESET + " delay" + "-------" + ANSI_RESET );
        }
        return this.executorService.submit(() -> {
            V result = null;
            for(int i = 0; i < task.getTimesToRun(); i++) {
                if (task.getDebug()){
                    System.out.println(ANSI_YELLOW + task.getTaskName() + " " + ANSI_BLUE + task.getTaskValue() + ANSI_RESET + " on thread number " + ANSI_CYAN + Thread.currentThread().getId() + ANSI_RESET + " attempt number " + ANSI_GREEN + i + ANSI_RESET + " with delay of " + ANSI_PURPLE + task.getSleepMillis() + ANSI_RESET );
                }
                result = task.call();
                if (task.isComplete()) {
                    break;
                }
                Thread.sleep(task.getSleepMillis());
            }
            return result;
        });
    }

}
