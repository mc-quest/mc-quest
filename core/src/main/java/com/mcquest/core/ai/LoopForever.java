package com.mcquest.core.ai;

public class LoopForever extends Decorator {
    public LoopForever(Behavior child) {
        super(child);
    }

    @Override
    public BehaviorStatus update(long time) {
        while (true) {
            BehaviorStatus status = child.tick(time);

            if (status != BehaviorStatus.SUCCESS) {
                return status;
            }
        }
    }
}
