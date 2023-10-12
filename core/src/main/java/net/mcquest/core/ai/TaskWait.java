package net.mcquest.core.ai;

import java.time.Duration;

public class TaskWait extends Task {
    private final Duration duration;
    private long startTime;

    public TaskWait(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void start(long time) {
        startTime = time;
    }

    @Override
    public BehaviorStatus update(long time) {
        if (time - startTime >= duration.toMillis()) {
            return BehaviorStatus.SUCCESS;
        }

        return BehaviorStatus.RUNNING;
    }
}
