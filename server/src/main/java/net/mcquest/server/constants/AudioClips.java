package net.mcquest.server.constants;

import net.mcquest.server.Assets;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.audio.AudioClip;

public class AudioClips {
    public static final AudioClip PORTCULLIS_CLOSE = loadAudioClip("portcullis_close");

    public static AudioClip[] all() {
        return new AudioClip[]{
                PORTCULLIS_CLOSE
        };
    }

    private static AudioClip loadAudioClip(String name) {
        String path = "audioclips/" + name + ".ogg";
        Asset audio = Assets.asset(path);
        return new AudioClip(audio);
    }
}
