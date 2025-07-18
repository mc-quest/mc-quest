package net.mcquest.core.ai;

import java.util.HashSet;
import java.util.Set;

public class Parallel extends Composite {
    private final Policy successPolicy;
    private final Policy failurePolicy;
    private final Set<Behavior> completedChildren;

    private Parallel(Policy successPolicy, Policy failurePolicy, Behavior... children) {
        super(children);
        this.successPolicy = successPolicy;
        this.failurePolicy = failurePolicy;
        completedChildren = new HashSet<>();
    }

    public static Parallel of(Policy successPolicy, Policy failurePolicy, Behavior... children) {
        return new Parallel(successPolicy, failurePolicy, children);
    }

    @Override
    public void start(long time) {
        completedChildren.clear();
    }

    @Override
    public BehaviorStatus update(long time) {
        int successCount = 0;
        int failureCount = 0;

        for (Behavior child : children) {
            if (!completedChildren.contains(child)) {
                child.tick(time);
            }

            if (child.getStatus() == BehaviorStatus.SUCCESS) {
                completedChildren.add(child);
                successCount++;
                if (successPolicy == Policy.REQUIRE_ONE) {
                    return BehaviorStatus.SUCCESS;
                }
            }

            if (child.getStatus() == BehaviorStatus.FAILURE) {
                completedChildren.add(child);
                failureCount++;
                if (failurePolicy == Policy.REQUIRE_ONE) {
                    return BehaviorStatus.FAILURE;
                }
            }
        }

        if (failurePolicy == Policy.REQUIRE_ALL && failureCount == children.length) {
            return BehaviorStatus.FAILURE;
        }

        if (successPolicy == Policy.REQUIRE_ALL && successCount == children.length) {
            return BehaviorStatus.SUCCESS;
        }

        return BehaviorStatus.RUNNING;
    }

    public enum Policy {
        REQUIRE_ONE,
        REQUIRE_ALL
    }
}
