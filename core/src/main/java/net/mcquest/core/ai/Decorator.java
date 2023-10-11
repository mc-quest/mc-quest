package net.mcquest.core.ai;

public abstract class Decorator extends Behavior {
    final Behavior child;

    Decorator(Behavior child) {
        this.child = child;
    }

    @Override
    void initialize(BehaviorTree tree) {
        super.initialize(tree);

        child.initialize(tree);
    }

    @Override
    public void stop(long time) {
        if (child.getStatus() == BehaviorStatus.RUNNING) {
            child.abort(time);
        }
    }
}
