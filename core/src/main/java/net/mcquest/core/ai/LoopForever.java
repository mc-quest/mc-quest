package net.mcquest.core.ai;

public class LoopForever extends Decorator {
    private LoopForever(Behavior child) {
        super(child);
    }

    public static LoopForever of(Behavior child) {
        return new LoopForever(child);
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
