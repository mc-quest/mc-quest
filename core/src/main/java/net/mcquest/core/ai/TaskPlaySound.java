package net.mcquest.core.ai;

import net.kyori.adventure.sound.Sound;

public class TaskPlaySound extends Task {
    // TODO: also support AudioClips
    private final Sound sound;

    public TaskPlaySound(Sound sound) {
        this.sound = sound;
    }

    @Override
    public BehaviorStatus update(long time) {
        getCharacter().playSound(sound);
        return BehaviorStatus.SUCCESS;
    }
}
