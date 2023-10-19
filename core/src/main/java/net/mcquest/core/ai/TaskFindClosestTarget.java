package net.mcquest.core.ai;

import com.google.common.base.Predicates;
import net.mcquest.core.character.Attitude;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.object.ObjectManager;
import net.minestom.server.coordinate.Pos;

public class TaskFindClosestTarget extends Task {
    private final double radius;

    public TaskFindClosestTarget(double radius) {
        this.radius = radius;
    }

    @Override
    public BehaviorStatus update(long time) {
        NonPlayerCharacter character = getCharacter();
        ObjectManager objectManager = character.getMmorpg().getObjectManager();
        Instance instance = character.getInstance();
        Pos position = character.getPosition();
        Character target = objectManager.getNearbyObjects(instance, position, radius)
                .stream()
                .filter(Predicates.instanceOf(Character.class))
                .map(Character.class::cast)
                .filter(this::shouldTarget)
                .min(this::compareTargetsByDistance)
                .orElse(null);
        character.setTarget(target);
        return target == null ? BehaviorStatus.FAILURE : BehaviorStatus.SUCCESS;
    }

    private boolean shouldTarget(Character target) {
        NonPlayerCharacter character = getCharacter();
        return target != character
                && target.isAlive()
                && target.isDamageable(character)
                && character.getAttitude(target) == Attitude.HOSTILE;
    }

    private int compareTargetsByDistance(Character target1, Character target2) {
        NonPlayerCharacter character = getCharacter();
        Pos position = character.getPosition();
        return Double.compare(
                position.distanceSquared(target1.getPosition()),
                position.distanceSquared(target2.getPosition())
        );
    }
}
