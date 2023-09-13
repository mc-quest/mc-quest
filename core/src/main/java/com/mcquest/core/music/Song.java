package com.mcquest.core.music;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetTypes;
import com.mcquest.core.resourcepack.Namespaces;
import net.kyori.adventure.key.Key;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;

public class Song {
    private final int id;
    private final Asset audio;
    private final Duration duration;

    public Song(int id, Asset audio) {
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

    public int getId() {
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
    public void writeResources(ResourcePack resourcePack) {
        Key key = getKey();
        resourcePack.sound(key, Writable.inputStream(audio::getStream));
        resourcePack.soundEvent(SoundEvent.builder()
                .key(key)
                .sounds(SoundEntry.builder()
                        .nameSound(key)
                        .stream(true)
                        .attenuationDistance(0)
                        .build())
                .build());
    }
}
