package net.mcquest.core.ai;

import net.mcquest.core.character.NonPlayerCharacter;

public class Behavior {
    private BehaviorTree tree;
    private BehaviorStatus status;

    Behavior() {
    }

    public final NonPlayerCharacter getCharacter() {
        return tree.getCharacter();
    }

    public final Blackboard getBlackboard() {
        return tree.getBlackboard();
    }

    void initialize(BehaviorTree tree) {
        this.tree = tree;
    }

    final BehaviorStatus getStatus() {
        return status;
    }

    final BehaviorStatus tick(long time) {
        if (status != BehaviorStatus.RUNNING) {
            start(time);
        }

        status = update(time);

        if (status != BehaviorStatus.RUNNING) {
            stop(time);
        }

        return status;
    }

    final void abort(long time) {
        status = BehaviorStatus.FAILURE;
        stop(time);
    }

    protected void start(long time) {
    }

    protected BehaviorStatus update(long time) {
        return BehaviorStatus.SUCCESS;
    }

    protected void stop(long time) {
    }
}
