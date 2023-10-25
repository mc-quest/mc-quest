package net.mcquest.core.ai;

public class Inverter extends Decorator {
    private Inverter(Behavior child) {
        super(child);
    }

    public static Inverter of(Behavior child) {
        return new Inverter(child);
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
