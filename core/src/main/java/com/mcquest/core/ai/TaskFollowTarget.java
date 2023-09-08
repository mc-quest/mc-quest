package com.mcquest.core.ai;

import com.mcquest.core.character.Character;
import com.mcquest.core.character.NonPlayerCharacter;
import net.minestom.server.coordinate.Pos;

public class TaskFollowTarget extends Task {
    private static final long PATH_UPDATE_PERIOD = 500;

    private final BlackboardKey<Character> target;
    private final double acceptanceRadius;
    private final double followDistance;
    private long lastPathUpdate;

    public TaskFollowTarget(BlackboardKey<Character> target, double acceptanceRadius, double followDistance) {
        this.target = target;
        this.acceptanceRadius = acceptanceRadius;
        this.followDistance = followDistance;
        lastPathUpdate = 0;
    }

    @Override
    public BehaviorStatus update(long time) {
        NonPlayerCharacter character = getCharacter();
        Character target = getBlackboard().get(this.target);
        if (target == null) {
            // No target is set.
            return BehaviorStatus.FAILURE;
        }

        Navigator navigator = character.getNavigator();
        Pos targetPosition = target.getPosition();
        if (character.getPosition().distanceSquared(targetPosition) <= acceptanceRadius * acceptanceRadius) {
            // Character is close enough to target.
            navigator.setPathTo(null);
            character.lookAt(target);
            return BehaviorStatus.SUCCESS;
        }

        if (character.getPosition().distanceSquared(targetPosition) > followDistance * followDistance) {
            // Target is too far away.
            navigator.setPathTo(null);
            return BehaviorStatus.FAILURE;
        }

        if (time >= lastPathUpdate + PATH_UPDATE_PERIOD &&
                (navigator.getPathPosition() == null
                        || !navigator.getPathPosition().samePoint(targetPosition))) {
            // Need to update path.
            lastPathUpdate = time;
            if (!navigator.setPathTo(target.getPosition())) {
                // Path update failed.
                return BehaviorStatus.FAILURE;
            }
        }

        // Character is en route to target.
        return BehaviorStatus.RUNNING;
    }
}
