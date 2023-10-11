package net.mcquest.core.ai;

public abstract class Composite extends Behavior {
    Behavior[] children;

    Composite(Behavior... children) {
        this.children = children;
    }

    @Override
    void initialize(BehaviorTree tree) {
        super.initialize(tree);
        for (Behavior child : children) {
            child.initialize(tree);
        }
    }
}
