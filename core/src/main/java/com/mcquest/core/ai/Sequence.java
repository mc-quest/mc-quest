package com.mcquest.core.ai;

public final class Sequence extends Composite {
    private int currentChild;

    public Sequence(Behavior... children) {
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

            if (status != BehaviorStatus.SUCCESS) {
                return status;
            }

            currentChild++;
        }

        return BehaviorStatus.SUCCESS;
    }
}
