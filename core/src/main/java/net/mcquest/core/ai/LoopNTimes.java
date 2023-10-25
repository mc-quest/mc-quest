package net.mcquest.core.ai;

public class LoopNTimes extends Decorator {
    private final int n;
    private int counter;

    private LoopNTimes(int n, Behavior child) {
        super(child);
        this.n = n;
    }

    public static LoopNTimes of(int n, Behavior child) {
        return new LoopNTimes(n, child);
    }

    @Override
    public void start(long time) {
        counter = 0;
    }

    @Override
    public BehaviorStatus update(long time) {
        while (counter < n) {
            BehaviorStatus status = child.tick(time);

            if (status != BehaviorStatus.SUCCESS) {
                return status;
            }

            counter++;
        }

        return BehaviorStatus.SUCCESS;
    }
}
