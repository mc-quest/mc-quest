package net.mcquest.core.ai;

import net.kyori.adventure.sound.Sound;

public class TaskEmitSound extends Task {
    // TODO: also support AudioClips
    private final Sound sound;

    public TaskEmitSound(Sound sound) {
        this.sound = sound;
    }

    @Override
    public BehaviorStatus update(long time) {
        getCharacter().emitSound(sound);
        return BehaviorStatus.SUCCESS;
    }
}
