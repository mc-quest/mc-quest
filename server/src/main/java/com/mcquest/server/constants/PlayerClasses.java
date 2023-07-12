package com.mcquest.server.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.core.playerclass.ActiveSkill;
import com.mcquest.core.playerclass.PassiveSkill;
import com.mcquest.core.playerclass.PlayerClass;
import com.mcquest.server.Assets;

import java.time.Duration;

public class PlayerClasses {
    public static final PlayerClass FIGHTER = loadPlayerClass("fighter");
    public static final PlayerClass MAGE = loadPlayerClass("mage");

    public static PlayerClass[] all() {
        return new PlayerClass[]{
                FIGHTER,
                MAGE
        };
    }

    private static PlayerClass loadPlayerClass(String path) {
        String dirPath = "playerclasses/" + path;
        String filePath = dirPath + "/" + path + ".json";
        JsonObject object = Assets.asset(filePath).readJson().getAsJsonObject();
        int id = object.get("id").getAsInt();
        String name = object.get("name").getAsString();
        PlayerClass.Builder builder = PlayerClass.builder(id, name);
        JsonArray skills = object.get("skills").getAsJsonArray();
        for (JsonElement skill : skills) {
            JsonObject skillObject = skill.getAsJsonObject();
            String type = skillObject.get("type").getAsString();
            if (type.equals("ACTIVE")) {
                loadActiveSkill(builder, skillObject, dirPath);
            } else if (type.equals("PASSIVE")) {
                loadPassiveSkill(builder, skillObject, dirPath);
            }
        }
        return builder.build();
    }

    private static void loadActiveSkill(PlayerClass.Builder builder, JsonObject skillObject, String dirPath) {
        ActiveSkill.BuildStep skillBuilder = builder.activeSkill()
                .id(skillObject.get("id").getAsInt())
                .name(skillObject.get("name").getAsString())
                .level(skillObject.get("level").getAsInt())
                .icon(Assets.asset(dirPath + "/icons/" + skillObject.get("icon").getAsString()))
                .description(skillObject.get("description").getAsString())
                .skillTreePosition(skillObject.get("skillTreeRow").getAsInt(),
                        skillObject.get("skillTreeColumn").getAsInt())
                .manaCost(skillObject.get("manaCost").getAsInt())
                .cooldown(Duration.ofMillis((long) (skillObject.get("cooldown").getAsDouble() * 1000)));
        if (skillObject.has("prerequisiteId")) {
            skillBuilder.prerequisite(skillObject.get("prerequisiteId").getAsInt());
        }
        skillBuilder.build();
    }

    private static void loadPassiveSkill(PlayerClass.Builder builder, JsonObject skillObject, String dirPath) {
        PassiveSkill.BuildStep skillBuilder = builder.passiveSkill()
                .id(skillObject.get("id").getAsInt())
                .name(skillObject.get("name").getAsString())
                .level(skillObject.get("level").getAsInt())
                .icon(Assets.asset(dirPath + "/icons/" + skillObject.get("icon").getAsString()))
                .description(skillObject.get("description").getAsString())
                .skillTreePosition(skillObject.get("skillTreeRow").getAsInt(),
                        skillObject.get("skillTreeColumn").getAsInt());
        if (skillObject.has("prerequisiteId")) {
            skillBuilder.prerequisite(skillObject.get("prerequisiteId").getAsInt());
        }
        skillBuilder.build();
    }
}
