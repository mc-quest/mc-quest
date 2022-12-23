package com.mcquest.server.music;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.Callable;

public class Song {
    private final int id;
    private final Callable<InputStream> audio;
    private final Sound sound;
    private final Duration duration;

    public Song(int id, Callable<InputStream> audio) {
        this.id = id;
        this.audio = audio;
        Key key = Key.key("music", String.valueOf(id));
        this.sound = Sound.sound(key, Sound.Source.MUSIC, 1f, 1f);
        this.duration = computeDuration(audio);
    }

    private static Duration computeDuration(Callable<InputStream> audio) {
        try {
            InputStream inputStream = audio.call();
            OggFile oggFile = new OggFile(inputStream);
            VorbisFile vorbisFile = new VorbisFile(oggFile);
            OggAudioStatistics statistics = new OggAudioStatistics(vorbisFile, vorbisFile);
            statistics.calculate();
            long durationMillis = (long) (statistics.getDurationSeconds() * 1000.0);
            inputStream.close();
            return Duration.ofMillis(durationMillis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public Callable<InputStream> getAudio() {
        return audio;
    }

    public Sound getSound() {
        return sound;
    }

    public Duration getDuration() {
        return duration;
    }
}
