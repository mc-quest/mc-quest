package com.mcquest.core.audio;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetTypes;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.resourcepack.Namespaces;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;

public class AudioClip {
    private final Asset audio;
    private Key key;

    public AudioClip(Asset audio) {
        audio.requireType(AssetTypes.OGG);
        this.audio = audio;
    }

    public Asset getAudio() {
        return audio;
    }

    public void play(PlayerCharacter pc, Sound.Source source) {
        play(pc, source, 1f, 1f);
    }

    public void play(PlayerCharacter pc, Sound.Source source, float volume, float pitch) {
        pc.playSound(sound(source, volume, pitch));
    }

    public void play(Instance instance, Pos position, Sound.Source source) {
        play(instance, position, source, 1f, 1f);
    }

    public void play(Instance instance, Pos position, Sound.Source source, float volume, float pitch) {
        instance.playSound(sound(source, volume, pitch), position);
    }

    private Sound sound(Sound.Source source, float volume, float pitch) {
        return Sound.sound(key, source, volume, pitch);
    }

    @ApiStatus.Internal
    public void writeResources(ResourcePack resourcePack, int id) {
        key = Key.key(Namespaces.AUDIO, String.valueOf(id));
        resourcePack.sound(key, Writable.inputStream(audio::getStream));
        resourcePack.soundEvent(SoundEvent.builder()
                .key(key)
                .sounds(SoundEntry.builder().nameSound(key).build())
                .build());
    }
}
