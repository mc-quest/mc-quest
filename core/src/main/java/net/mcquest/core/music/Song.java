package net.mcquest.core.music;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.resourcepack.Namespaces;
import net.kyori.adventure.key.Key;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Map;

public class Song {
    private final String id;
    private final Asset audio;
    private final Duration duration;

    public Song(String id, Asset audio) {
        audio.requireType(AssetTypes.OGG);
        this.id = id;
        this.audio = audio;
        duration = computeDuration(audio);
    }

    private static Duration computeDuration(Asset audio) {
        try (InputStream stream = audio.getStream()) {
            OggFile oggFile = new OggFile(stream);
            VorbisFile vorbisFile = new VorbisFile(oggFile);
            OggAudioStatistics statistics = new OggAudioStatistics(vorbisFile, vorbisFile);
            statistics.calculate();
            long durationMillis = (long) (statistics.getDurationSeconds() * 1000.0);
            stream.close();
            return Duration.ofMillis(durationMillis);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getId() {
        return id;
    }

    public Asset getAudio() {
        return audio;
    }

    public Duration getDuration() {
        return duration;
    }

    Key getKey() {
        return Key.key(Namespaces.MUSIC, String.valueOf(id));
    }

    @ApiStatus.Internal
    public void writeResources(FileTree tree, Map<String, SoundEvent> sounds) {
        Key key = getKey();

        Writable data = Writable.inputStream(audio::getStream);
        Sound.File soundFile = Sound.File.of(key, data);
        tree.write(soundFile);

        Sound sound = Sound
                .builder()
                .nameSound(key)
                .stream(true)
                .attenuationDistance(0)
                .build();
        SoundEvent soundEvent = SoundEvent.builder().sounds(sound).build();
        sounds.put(key.value(), soundEvent);
    }
}
