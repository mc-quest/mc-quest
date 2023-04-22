package com.mcquest.server.constants;

import com.mcquest.server.Assets;
import com.mcquest.server.asset.Asset;
import com.mcquest.server.audio.AudioClip;

public class AudioClips {
    public static final AudioClip DUNGEON = loadAudioClip("Dungeon");
    public static final AudioClip PORTCULLIS_CLOSE = loadAudioClip("PortcullisClose");

    public static AudioClip[] all() {
        return new AudioClip[]{
                DUNGEON,
                PORTCULLIS_CLOSE
        };
    }

    private static AudioClip loadAudioClip(String name) {
        String path = "audio/" + name + ".ogg";
        Asset audio = Assets.asset(path);
        return new AudioClip(audio);
    }
}
