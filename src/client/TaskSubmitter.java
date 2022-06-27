package client;

import task.ITask;
import task.ITaskCallBack;
import task.TaskRunner;
import util.tasks.FileCheckerTask;
import util.tasks.PortAvailableTask;

import javax.swing.plaf.synth.Region;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main program for creating and submitting tasks to the Task Runner.
 */
public class TaskSubmitter {
    static BufferedReader reader;

    /**
     * Handler of all tasks that are then submitted to be exectuted.
     * Takes user input or can be hard coded.
     * @param args
     */
    public static void main(String[] args) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        //each taskRunner instance contains a list of the tasks and has its own thread pool ,
        // so to run a fresh set of tasks you would want to create a new instance of taskRunner.
        //A class could be created called TaskRunnerManager to handle multiple sets of tasks but seems unnecessary here.
        TaskRunner taskRunner = new TaskRunner();

        //The only reason for the mapping is to make the print statements nicer and to allow for callbacks if desired.
        //the mapping would be useful if expanded on for reporting on performance of tasks and if those tasks were more complicated and had further steps.
        HashMap<ITask<?>,Future<?>> mappedFutures = new HashMap<>();

        //regions are supported on intelji for handy code folding
        //most of the user input region is just fluff to make playing around with the demo easier.
        //region #userInput
        //Not sure if this will work if you run it via cmd and not in an IDE
        String pathForFiles = System.getProperty("user.dir");


        System.out.println("\n\n   --Welcome to multithreading interview exercise.--   \n");
        //wasn't sure if user input was needed or just programmatically so just added a basic interface
        String requestedMode = takeInput(reader, "To add a specific tasks press (1) , to run a demo mix of tasks please enter (2)");

        if("2".equals(requestedMode)) {
            //Moved the steps and sleep into the task itself to make print statements better mainly. Would also be useful for pausing/resuming tasks if that was needed.
            mappedFutures = taskRunner
                    .addTask(new FileCheckerTask(pathForFiles + "\\multithreading2.jpg", 9, 1, () -> System.out.println("this task has been complete, do some stuff now, maybe send an email or whatever")))
                    .addTask(new FileCheckerTask(pathForFiles + "\\multithreading2.jpg", 10, 10, () -> {
                        System.out.println("lets do some stuff here");
                    }))
                    .addTask(new FileCheckerTask(pathForFiles + "\\multithreading.jpg", 5, 25))
                    .addTask(new FileCheckerTask(pathForFiles + "\\multithreading3.jpg"))
                    .addTask(new PortAvailableTask(25570, 5, 5000))
                    .addTask(new PortAvailableTask(80, 3, 1000))
                    .addTask(new PortAvailableTask(55572, 100, 50))
                    .addTask(new PortAvailableTask(1900, 5, 4500))
                    .addTask(new PortAvailableTask(1900))
                    .runTasks();
        } else if ("1".equals(requestedMode)) {
            while(true) {
                taskRunner.addTask(addTask());
                String doneAddingInput = takeInput(reader, "Do you want to add another tasks? y, n");
                if ("n".equalsIgnoreCase(doneAddingInput)) {
                    break;
                } else if (!"y".equalsIgnoreCase(doneAddingInput)) {
                    System.out.println("Invalid input, please try again.");
                    main(null);
                    break;
                }
            }
            mappedFutures = taskRunner.runTasks();
        } else {
            System.out.println("Invalid input, please try again.");
            main(null);
        }
        Instant before = Instant.now();
        //endregion

        //region #results
        HashMap<ITask<?>, String> taskResults = new HashMap<>();
        if (mappedFutures == null) {
            System.out.println("could not find any futures, did the tasks run successfully?");
            main(null);
            return;
        }
        for ( ITask<?> task : mappedFutures.keySet() ) {
            try {
                Future<?> future = mappedFutures.get(task);
                //Calling .get here is a blocking operation which means we are waiting until all tasks are done before this for loop is done and we print the results
                //If we don't have the blocking then it will go straight to printing the results , which would be empty. Which could be achieved with future.isDone() check.

                //This will pause this main thread until the given future returns a result , which means the results are not necessarily in order as other tasks could finish
                //before this one but not be able to add their results to the list until this one is done.
                Object result = future.get();

                taskResults.put(task, String.valueOf(result));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        //You could also do various actions here based on the results instead of the callbacks but would be less dynamic
        System.out.println("\n\nresults:");
        System.out.println("Time taken: " + (Instant.now().toEpochMilli() - before.toEpochMilli()) + " milliseconds");
        for ( ITask task : taskResults.keySet() ) {
            System.out.println("【【【 " + task.getTaskName() + " V:" + task.getTaskValue() + " S:" + task.getSleepMillis() + " T:" + task.getTimesToRun() + " result = " + taskResults.get(task) + " 】】】");
        }
        String startAgain = takeInput(reader, "Do you want to start again? y,n");
        if ("n".equalsIgnoreCase(startAgain)) {
            return;
        } else if (!"y".equalsIgnoreCase(startAgain)) {
            System.out.println("Invalid input, please try again.");
            main(null);
            return;
        }
        main(null);
        //endregion
    }

    //Would probably extract this to its own addTask class, so it could be replaced with a gui or something like that.
    /**
     * Handles user input for creating a task
     */
    private static ITask<?> addTask(){
        String whichTaskInput = takeInput(reader, "which task would you like to add? (3) for file test , (4) for port check");
        if ("3".equals(whichTaskInput)) {
            String fileTestInput = takeInput(reader, "please enter the file path for the file you wish to check, then a comma, then the attempts , then the delay.");
            String[] fileTestParams = fileTestInput.split(",");
            return new FileCheckerTask(fileTestParams[0], Integer.parseInt(fileTestParams[1]), Integer.parseInt(fileTestParams[2]));
        } else if ("4".equals(whichTaskInput)) {
            String fileTestInput = takeInput(reader, "please enter the port number you wish to check, then a comma, then the attempts , then the delay.");
            String[] fileTestParams = fileTestInput.split(",");
            return new PortAvailableTask(Integer.parseInt(fileTestParams[0]), Integer.parseInt(fileTestParams[1]), Integer.parseInt(fileTestParams[2]));
        }
        System.out.println("Invalid input, please try again.");
        main(null);
        return null;
    }
    /**
     * Handler for accepting text input from console
     * @param reader - the reader used to accept the user input
     * @param message - the message to display to the user as a prompt
     */
    private static String takeInput(BufferedReader reader, String message){
        try {
            if (!message.isBlank()) {
                System.out.println(message);
            }
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
