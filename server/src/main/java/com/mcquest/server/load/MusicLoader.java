package com.mcquest.server.load;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.Mmorpg;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.music.Pitch;
import com.mcquest.server.music.SongBuilder;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

public class MusicLoader {
    public static void loadMusic(Mmorpg mmorpg) {
        MusicManager musicManager = mmorpg.getMusicManager();
        List<String> paths = ResourceUtility.getResources("music");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            double duration = object.get("duration").getAsDouble();
            double beatsPerMinute = object.get("beatsPerMinute").getAsDouble();
            SongBuilder builder = musicManager.songBuilder(id, duration, beatsPerMinute);
            JsonArray notes = object.get("notes").getAsJsonArray();
            for (JsonElement note : notes) {
                JsonObject noteObject = note.getAsJsonObject();
                if (noteObject.has("instrument")) {
                    SoundEvent instrument = SoundEvent
                            .fromNamespaceId(noteObject.get("instrument").getAsString());
                    builder.instrument(instrument);
                }
                if (noteObject.has("volume")) {
                    float volume = noteObject.get("volume").getAsFloat();
                    builder.volume(volume);
                }
                double time = noteObject.get("time").getAsDouble();
                Pitch pitch = Pitch.valueOf(noteObject.get("pitch").getAsString());
                builder.note(time, pitch);
            }
            builder.build();
        }
    }
}
