package net.mcquest.core.ai;

public final class ActiveSelector extends Composite {
    private int currentChild;

    public ActiveSelector(Behavior... children) {
        super(children);
    }

    @Override
    public void start(long time) {
        currentChild = 0;
    }

    @Override
    public BehaviorStatus update(long time) {
        int prevChild = currentChild;
        currentChild = 0;

        BehaviorStatus status = BehaviorStatus.FAILURE;

        while (currentChild < children.length) {
            BehaviorStatus childStatus = children[currentChild].tick(time);

            if (childStatus != BehaviorStatus.FAILURE) {
                status = childStatus;
                break;
            }

            currentChild++;
        }

        if (prevChild != children.length && currentChild != prevChild) {
            children[prevChild].abort(time);
        }

        return status;
    }
}
