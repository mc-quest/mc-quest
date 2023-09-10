package com.mcquest.core.ai;

public class Inverter extends Decorator {
    public Inverter(Behavior child) {
        super(child);
    }

    @Override
    public BehaviorStatus update(long time) {
        BehaviorStatus status = child.tick(time);

        if (status == BehaviorStatus.SUCCESS) {
            return BehaviorStatus.FAILURE;
        }

        if (status == BehaviorStatus.FAILURE) {
            return BehaviorStatus.SUCCESS;
        }

        return BehaviorStatus.RUNNING;
    }
}
