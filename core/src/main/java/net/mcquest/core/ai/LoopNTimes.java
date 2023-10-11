package net.mcquest.core.ai;

public class LoopNTimes extends Decorator {
    private final int n;
    private int counter;

    public LoopNTimes(int n, Behavior child) {
        super(child);
        this.n = n;
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
