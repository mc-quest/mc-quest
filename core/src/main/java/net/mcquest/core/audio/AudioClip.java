package net.mcquest.core.audio;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.resourcepack.Namespaces;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.sound.SoundEvent;

import java.util.Map;

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

    public void stop(PlayerCharacter pc) {
        pc.stopSound(SoundStop.named(key));
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
    public void writeResources(FileTree tree, int id, Map<String, SoundEvent> sounds) {
        key = Key.key(Namespaces.AUDIO, String.valueOf(id));

        Writable data = Writable.inputStream(audio::getStream);
        team.unnamed.creative.sound.Sound.File soundFile =
                team.unnamed.creative.sound.Sound.File.of(key, data);
        tree.write(soundFile);

        team.unnamed.creative.sound.Sound sound = team.unnamed.creative.sound.Sound
                .builder()
                .nameSound(key)
                .build();
        SoundEvent soundEvent = SoundEvent.builder().sounds(sound).build();
        sounds.put(key.value(), soundEvent);
    }
}
