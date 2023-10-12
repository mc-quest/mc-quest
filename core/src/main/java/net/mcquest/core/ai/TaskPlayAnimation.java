package net.mcquest.core.ai;

import net.mcquest.core.character.CharacterAnimation;

public class TaskPlayAnimation extends Task {
    private final CharacterAnimation animation;

    public TaskPlayAnimation(CharacterAnimation animation) {
        this.animation = animation;
    }

    @Override
    public void start(long time) {
        getCharacter().playAnimation(animation);
    }
}
