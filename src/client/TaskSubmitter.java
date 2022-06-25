package client;

import task.ITask;
import task.TaskRunner;
import util.tasks.FileCheckerTask;
import util.tasks.PortAvailableTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main program for creating and submitting tasks to the Task Runner.
 */
public class TaskSubmitter {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    static BufferedReader reader;

    /**
     * Creates two tasks and submits them to the TaskRunner.
     * @param args
     */
    public static void main(String[] args) {
        //TODO: general validation for user input, starting from scratch etc
        //TODO: better error handling to enable restarting on fail
        //TODO: comments and javadocs
        //TODO: do something if true?
        //TODO: deal with eclipse not liking ANSI
        // https://stackoverflow.com/questions/6286701/an-eclipse-console-view-that-respects-ansi-color-codes

        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to multithreading interview exercise.");
        // Enter data using BufferReader
        System.out.println("\n\n\n");
        System.out.println("To add a specific tasks press (1) , to run a demo mix of tasks please enter (2)");

        String requestedMode = takeInput(reader);

        TaskRunner taskRunner = new TaskRunner();
        List<Future> futures = new ArrayList<>();
        //Not sure if this will work if you run it via cmd and not in an IDE
        String pathForFiles = System.getProperty("user.dir");
        if("2".equals(requestedMode)) {
            futures = taskRunner
                    .addTask(new FileCheckerTask(pathForFiles + "multithreading2.jpg", 9, 1))
                    .addTask(new FileCheckerTask(pathForFiles + "multithreading2.jpg", 10, 10))
                    .addTask(new FileCheckerTask(pathForFiles + "multithreading.jpg", 20, 500))
                    .addTask(new FileCheckerTask(pathForFiles + "multithreading3.jpg"))
                    .addTask(new PortAvailableTask(25570, 5, 5000))
                    .addTask(new PortAvailableTask(80, 3, 1000))
                    .addTask(new PortAvailableTask(55572, 100, 50))
                    .addTask(new PortAvailableTask(1900, 5, 4500))
                    .addTask(new PortAvailableTask(1900))

                    .runTasks();
        } else if ("1".equals(requestedMode)) {
            boolean doneAdding = false;
            while(!doneAdding){
                taskRunner.addTask(addTask());
                System.out.println("Are you done adding tasks? y, n");
                String doneAddingInput = takeInput(reader);
                if ("y".equals(doneAddingInput)) {
                    doneAdding = true;
                } else {
                    doneAdding = false;
                }
            }
            futures = taskRunner.runTasks();
        }


        List<String> results = new ArrayList<>();
        if (futures == null) {
            System.out.println("could not find any futures, did the tasks run successfully?");
            return;
        }

        futures.forEach(future -> {
            try {
                Object result = future.get();
                results.add(String.valueOf(result));
                System.out.println(ANSI_RED + " value has been calculated as " + result + ANSI_RESET);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(ANSI_GREEN + "\n\nresults:");
        System.out.println(Arrays.toString(results.toArray()) + ANSI_RESET);
    }

    private static ITask<?> addTask(){
        System.out.println("which task would you like to add? (3) for file test , (4) for port check");
        String whichTaskInput = takeInput(reader);
        if ("3".equals(whichTaskInput)) {
            System.out.println("please enter the file path for the file you wish to check, then a comma, then the attempts , then the delay.");
            String fileTestInput = takeInput(reader);
            String[] fileTestParams = fileTestInput.split(",");
            return new FileCheckerTask(fileTestParams[0], Integer.parseInt(fileTestParams[1]), Integer.parseInt(fileTestParams[2]));
        } else if ("4".equals(whichTaskInput)) {
            System.out.println("please enter the port number you wish to check, then a comma, then the attempts , then the delay.");
            String fileTestInput = takeInput(reader);
            String[] fileTestParams = fileTestInput.split(",");
            return new PortAvailableTask(Integer.parseInt(fileTestParams[0]), Integer.parseInt(fileTestParams[1]), Integer.parseInt(fileTestParams[2]));
        }
        return null;
    }

    private static String takeInput(BufferedReader reader){
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
