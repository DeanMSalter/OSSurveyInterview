package task;

//Using this to allow a callback when a task is done. Would probably want to add some conditions to allow different actions depending on the tasks return value.
@FunctionalInterface
public interface ITaskCallBack {
    void handleAction();
}
