package com.mcquest.core.audio;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class AudioManager {
    private final Collection<AudioClip> audioClips;

    @ApiStatus.Internal
    public AudioManager(AudioClip[] audioClips) {
        this.audioClips = new ArrayList<>();
        this.audioClips.addAll(Arrays.asList(CoreAudio.all()));
        this.audioClips.addAll(Arrays.asList(audioClips));
    }

    public Collection<AudioClip> getAudioClips() {
        return Collections.unmodifiableCollection(audioClips);
    }
}

