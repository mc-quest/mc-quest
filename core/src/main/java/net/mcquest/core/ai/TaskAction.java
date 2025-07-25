package net.mcquest.core.ai;

public class TaskAction extends Task {
    private final Action action;

    private TaskAction(Action action) {
        this.action = action;
    }

    public static TaskAction of(Action action) {
        return new TaskAction(action);
    }

    @Override
    public BehaviorStatus update(long time) {
        return action.execute(time) ? BehaviorStatus.SUCCESS : BehaviorStatus.FAILURE;
    }

    @FunctionalInterface
    public interface Action {
        boolean execute(long time);
    }
}
