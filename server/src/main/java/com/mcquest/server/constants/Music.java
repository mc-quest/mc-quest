package com.mcquest.server.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.music.Pitch;
import com.mcquest.server.music.Song;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.sound.SoundEvent;

public class Music {
    public static final Song DUNGEON = loadSong("Dungeon");

    public static Song loadSong(String fileName) {
        String path = "music/" + fileName + ".json";
        JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
        int id = object.get("id").getAsInt();
        double duration = object.get("duration").getAsDouble();
        double beatsPerMinute = object.get("beatsPerMinute").getAsDouble();
        Song.Builder builder = Song.builder(id, duration, beatsPerMinute);
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
        return builder.build();
    }
}
