package com.mcquest.core.ai;

import com.mcquest.core.character.Character;

public class TaskLookAtTarget extends Task {
    private final BlackboardKey<Character> targetKey;

    public TaskLookAtTarget(BlackboardKey<Character> targetKey) {
        this.targetKey = targetKey;
    }

    @Override
    public BehaviorStatus update(long time) {
        Character target = getBlackboard().get(targetKey);

        if (target == null) {
            return BehaviorStatus.FAILURE;
        }

        getCharacter().lookAt(target);

        return BehaviorStatus.SUCCESS;
    }
}
