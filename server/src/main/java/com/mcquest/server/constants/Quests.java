package com.mcquest.server.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.core.quest.Quest;
import com.mcquest.core.util.JsonUtility;
import com.mcquest.server.Assets;

public class Quests {
    public static final Quest TUTORIAL = loadQuest("Tutorial");
    public static final Quest THWARTING_THE_THIEVES = loadQuest("ThwartingTheThieves");
    public static final Quest ARACHNOPHOBIA = Quest.builder(3, "Arachnophobia", 5)
            .objective("Spiders slain", 15)
            .objective("Broodmother slain", 1)
            .build();
    public static final Quest FANGS_AND_FUMES = Quest.builder(4, "Fangs and Fumes", 5)
            .objective("Spider fangs", 5)
            .objective("Spider venom", 3)
            .build();

    public static Quest[] all() {
        return new Quest[]{
                TUTORIAL,
                THWARTING_THE_THIEVES,
                ARACHNOPHOBIA,
                FANGS_AND_FUMES
        };
    }

    private static Quest loadQuest(String fileName) {
        String path = "quests/" + fileName + ".json";
        JsonObject object = Assets.asset(path).readJson().getAsJsonObject();
        int id = object.get("id").getAsInt();
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
