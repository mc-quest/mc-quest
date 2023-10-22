package net.mcquest.core.ai;

import net.mcquest.core.character.Character;
import net.mcquest.core.character.NonPlayerCharacter;
import net.minestom.server.coordinate.Pos;

public class TaskFollowTarget extends Task {
    private static final long PATH_UPDATE_PERIOD = 500;

    private final double acceptanceRadius;
    private final double followDistance;
    private long lastPathUpdate;

    public TaskFollowTarget(double acceptanceRadius, double followDistance) {
        this.acceptanceRadius = acceptanceRadius;
        this.followDistance = followDistance;
        lastPathUpdate = 0;
    }

    @Override
    public BehaviorStatus update(long time) {
        NonPlayerCharacter character = getCharacter();
        Character target = character.getTarget();
        if (target == null) {
            // No target is set.
            return BehaviorStatus.FAILURE;
        }

        if (target.isInvisible()) {
            return BehaviorStatus.FAILURE;
        }

        Navigator navigator = character.getNavigator();
        Pos targetPosition = target.getPosition();
        if (character.getPosition().distanceSquared(targetPosition) <= acceptanceRadius * acceptanceRadius) {
            // Character is close enough to target.
            character.lookAt(target);
            return BehaviorStatus.SUCCESS;
        }

        if (character.getPosition().distanceSquared(targetPosition) > followDistance * followDistance) {
            // Target is too far away.
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

    @Override
    public void stop(long time) {
        getCharacter().getNavigator().setPathTo(null);
    }
}
