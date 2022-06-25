package task;

import javax.swing.plaf.FontUIResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    private ExecutorService executorService;

    //Using a simplified builder pattern to make adding multiple tasks easier.
    public <V> TaskRunner addTask(ITask<V> task){
        this.tasks.add(task);
        return this;
    }

    //moved tasks to one list and a group running as seems like it would be more useful in a real world situation as you would be able to queue up a bunch of tasks and have them run in the background.
    public HashMap<ITask<?>,Future<?>> runTasks(){
        if (tasks.size() == 0) {
            System.out.println("No tasks added to run. Please add tasks using .addTask()");
            return null;
        }
        //assumed we wanted a fixed amount of threads as opposed to single threading or cached threads. Set a maximum amount as could cause performance problems with large amount of threads.
        this.executorService = Executors.newFixedThreadPool(Math.min(tasks.size(), 20));
        HashMap<ITask<?>,Future<?>> mappedFutures = new HashMap<>();
        //This could be done with invokeAll or invokeAny but want to map the task to the future for later use and there is no performance difference between them as far as I can tell.
        tasks.forEach(task -> {
            Future<?> future = runTaskAsync(task);
            mappedFutures.put(task, future);
        });

        //call shutdown here since we dont want to accept any more tasks
        //ideally we would want to use a combination of awaitTermination and shutdownnow to handle cases where the tasks take too long to execute/hand but is not relevant in this demo
        //as each task has an exit condition
        this.executorService.shutdown();
        return mappedFutures;
    }

    private <V> Future<V> runTaskAsync(ITask<V> task) {
        System.out.println("♦♦♦♦♦ " + task.getTaskName() + " '" +  task.getTaskValue() + "' with " + task.getTimesToRun() + " attempts and " + task.getSleepMillis() + " delay" + " max time this should take: " + task.getTimesToRun() * task.getSleepMillis() + " milliseconds ♦♦♦♦♦");
        //submits the task as a callable to a new thread
        //returns a future object which will be used to retrieve the value
        return this.executorService.submit(() -> {
            V result = null;
            for(int i = 0; i < task.getTimesToRun(); i++) {
                result = task.call();
                System.out.println("↦↦↦↦↦ " + task.getTaskName() + " V:" + task.getTaskValue() + " S:" + task.getSleepMillis() + " T:" + task.getTimesToRun() + " result = " + result + " ↤↤↤↤↤");
                if (task.isComplete()) {
                    break;
                }
                Thread.sleep(task.getSleepMillis());
            }
            System.out.println("★★★★★ " + task.getTaskName() + " V:" + task.getTaskValue() + " S:" + task.getSleepMillis() + " T:" + task.getTimesToRun() + " result = " + result + " ★★★★★");
            //call the callback the task has defined
            //this would need fleshing out to be truly useful with conditions and access to the task instance
            task.methodToCallback();
            return result;
        });
    }

}