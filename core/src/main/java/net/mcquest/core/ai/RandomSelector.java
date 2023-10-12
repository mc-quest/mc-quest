package net.mcquest.core.ai;

import java.util.Arrays;

/**
 * Randomly chooses a child to run by weight.
 */
public class RandomSelector extends Composite {
    private final int[] weights;
    private final int totalWeight;
    private int currentChild;

    public RandomSelector(int[] weights, Behavior... children) {
        super(children);

        if (weights.length != children.length) {
            throw new IllegalArgumentException();
        }

        this.weights = weights;
        this.totalWeight = Arrays.stream(weights).sum();
    }

    @Override
    public void start(long time) {
        double dice = Math.random() * totalWeight;
        for (int i = 0; i < children.length; i++) {
            if (dice < weights[i]) {
                currentChild = i;
                break;
            } else {
                dice -= weights[i];
            }
        }
    }

    @Override
    public BehaviorStatus update(long time) {
        BehaviorStatus status = children[currentChild].tick(time);

        if (status != BehaviorStatus.RUNNING) {
            return status;
        }

        return BehaviorStatus.RUNNING;
    }
}
