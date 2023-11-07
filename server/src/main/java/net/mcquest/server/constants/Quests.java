package net.mcquest.server.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcquest.core.quest.Quest;
import net.mcquest.core.util.JsonUtility;
import net.mcquest.server.Assets;

public class Quests {
    public static final Quest TUTORIAL = loadQuest("tutorial");
    public static final Quest THWARTING_THE_THIEVES = loadQuest("thwarting_the_thieves");
    public static final Quest FLAVORFUL_PURSUIT_WOLF_FLANKS =
            loadQuest("flavorful_pursuit_wolf_flanks");



    public static Quest[] all() {
        return new Quest[]{
                TUTORIAL,
                THWARTING_THE_THIEVES,
                FLAVORFUL_PURSUIT_WOLF_FLANKS
        };
    }

    private static Quest loadQuest(String fileName) {
        String path = "quests/" + fileName + ".json";
        JsonObject object = Assets.asset(path).readJson().getAsJsonObject();
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        int level = object.get("level").getAsInt();
        Quest.Builder builder = Quest.builder(id, name, level);
        JsonArray objectives = object.get("objectives").getAsJsonArray();
        for (JsonElement objective : objectives) {
            JsonObject objectiveObject = objective.getAsJsonObject();
            String description = objectiveObject.get("description").getAsString();
            int goal = objectiveObject.get("goal").getAsInt();
            int[] prerequisites;
            if (objectiveObject.has("prerequisites")) {
                prerequisites = JsonUtility.fromJson(objectiveObject.get("prerequisites"), int[].class);
            } else {
                prerequisites = new int[0];
            }
            builder.objective(description, goal, prerequisites);
        }
        return builder.build();
    }
}
