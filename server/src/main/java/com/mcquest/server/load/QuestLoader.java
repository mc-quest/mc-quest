package com.mcquest.server.load;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.Mmorpg;
import com.mcquest.server.quest.QuestBuilder;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.util.ResourceUtility;

import java.util.List;

public class QuestLoader {
    public static void loadQuests(Mmorpg mmorpg) {
        QuestManager questManager = mmorpg.getQuestManager();
        List<String> paths = ResourceUtility.getResources("quests");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            int level = object.get("level").getAsInt();
            QuestBuilder builder = questManager.questBuilder(id, name, level);
            JsonArray objectives = object.get("objectives").getAsJsonArray();
            for (JsonElement objective : objectives) {
                JsonObject objectiveObject = objective.getAsJsonObject();
                String description = objectiveObject.get("description").getAsString();
                int goal = objectiveObject.get("goal").getAsInt();
                builder.objective(description, goal);
            }
            builder.build();
        }
    }
}
