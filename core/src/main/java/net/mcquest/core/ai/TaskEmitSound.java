package net.mcquest.core.ai;

import net.kyori.adventure.sound.Sound;

public class TaskEmitSound extends Task {
    // TODO: also support AudioClips
    private final Sound sound;

    private TaskEmitSound(Sound sound) {
        this.sound = sound;
    }

    public static TaskEmitSound of(Sound sound) {
        return new TaskEmitSound(sound);
    }

    @Override
    public BehaviorStatus update(long time) {
        getCharacter().emitSound(sound);
        return BehaviorStatus.SUCCESS;
    }
}
