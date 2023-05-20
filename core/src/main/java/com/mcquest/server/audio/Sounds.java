package com.mcquest.server.audio;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.sound.SoundEvent;

public class Sounds {
    public static final Sound CLICK =
            Sound.sound(SoundEvent.BLOCK_LEVER_CLICK, Sound.Source.MASTER, 1f, 1f);
    public static final Sound CHEST_OPEN =
            Sound.sound(SoundEvent.BLOCK_CHEST_OPEN, Sound.Source.MASTER, 1f, 1f);
}
