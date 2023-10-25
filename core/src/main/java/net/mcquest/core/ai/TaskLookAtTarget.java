package net.mcquest.core.ai;

import net.mcquest.core.character.Character;
import net.mcquest.core.character.NonPlayerCharacter;

public class TaskLookAtTarget extends Task {
    private TaskLookAtTarget() {}

    public static TaskLookAtTarget of() {
        return new TaskLookAtTarget();
    }

    @Override
    public BehaviorStatus update(long time) {
        NonPlayerCharacter character = getCharacter();
        Character target = character.getTarget();

        if (target == null) {
            return BehaviorStatus.FAILURE;
        }

        character.lookAt(target);

        return BehaviorStatus.SUCCESS;
    }
}
