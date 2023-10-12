package net.mcquest.core.ai;

/**
 * Used to run a child behavior while repeatedly executing a background behavior.
 */
public class SimpleParallel extends Composite {
    public SimpleParallel(Behavior child, Behavior background) {
        super(child, background);
    }

    private Behavior child() {
        return children[0];
    }

    private Behavior background() {
        return children[1];
    }

    @Override
    public BehaviorStatus update(long time) {
        BehaviorStatus status = child().tick(time);
        background().tick(time);

        if (status != BehaviorStatus.RUNNING) {
            return status;
        }

        return BehaviorStatus.RUNNING;
    }
}
