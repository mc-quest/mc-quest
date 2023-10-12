package net.mcquest.core.ai;

public final class Selector extends Composite {
    private int currentChild;

    public Selector(Behavior... children) {
        super(children);
    }

    @Override
    public void start(long time) {
        currentChild = 0;
    }

    @Override
    public BehaviorStatus update(long time) {
        while (currentChild < children.length) {
            BehaviorStatus status = children[currentChild].tick(time);

            if (status != BehaviorStatus.FAILURE) {
                return status;
            }

            currentChild++;
        }

        return BehaviorStatus.FAILURE;
    }
}
