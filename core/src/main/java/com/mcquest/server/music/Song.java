package com.mcquest.server.music;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.asset.AssetTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;

public class Song {
    private final int id;
    private final Asset audio;
    private final Sound sound;
    private final Duration duration;

    public Song(int id, Asset audio) {
        this.id = id;
        audio.ensureType(AssetTypes.OGG);
        this.audio = audio;
        Key key = Key.key("music", String.valueOf(id));
        this.sound = Sound.sound(key, Sound.Source.MUSIC, 1f, 1f);
        this.duration = computeDuration(audio);
    }

    private static Duration computeDuration(Asset audio) {
        try {
            InputStream stream = audio.getStream();
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

    public Sound getSound() {
        return sound;
    }

    public Duration getDuration() {
        return duration;
    }
}
