package net.mcquest.core.ai;

import net.mcquest.core.character.CharacterAnimation;

public class TaskPlayAnimation extends Task {
    private final CharacterAnimation animation;

    private TaskPlayAnimation(CharacterAnimation animation) {
        this.animation = animation;
    }

    public static TaskPlayAnimation of(CharacterAnimation animation) {
        return new TaskPlayAnimation(animation);
    }

    @Override
    public BehaviorStatus update(long time) {
        getCharacter().playAnimation(animation);
        return BehaviorStatus.SUCCESS;
    }
}
