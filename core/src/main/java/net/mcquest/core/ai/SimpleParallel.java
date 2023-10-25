package net.mcquest.core.ai;

/**
 * Used to run a child behavior while repeatedly executing a background behavior.
 */
public class SimpleParallel extends Composite {
    private SimpleParallel(Behavior child, Behavior background) {
        super(child, background);
    }

    public static SimpleParallel of(Behavior child, Behavior background) {
        return new SimpleParallel(child, background);
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
        return status;
    }
}
