package com.mcquest.server.audio;

import com.mcquest.server.asset.Asset;
import org.gagravarr.ogg.OggFile;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;

public class Song {
    private final int id;
    private AudioClip audioClip;
    private final Duration duration;

    public Song(int id, AudioClip audioClip) {
        this.id = id;
        this.audioClip = audioClip;
        this.duration = computeDuration(audioClip.getAudio());
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

    public AudioClip getAudioClip() {
        return audioClip;
    }

    public Duration getDuration() {
        return duration;
    }
}
