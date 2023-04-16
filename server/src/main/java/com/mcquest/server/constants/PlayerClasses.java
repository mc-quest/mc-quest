package com.mcquest.server.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.asset.Asset;
import com.mcquest.server.playerclass.PlayerClass;

import java.time.Duration;

public class PlayerClasses {
    public static final PlayerClass FIGHTER = loadPlayerClass("fighter", "Fighter.json");
    public static final PlayerClass MAGE = loadPlayerClass("mage", "Mage.json");

    public static PlayerClass[] all() {
        return new PlayerClass[]{
                FIGHTER,
                MAGE
        };
    }

    private static PlayerClass loadPlayerClass(String dirName, String fileName) {
        ClassLoader classLoader = PlayerClasses.class.getClassLoader();
        String dirPath = "playerclasses/" + dirName;
        String filePath = dirPath + "/" + fileName;
        JsonObject object = new Asset(classLoader, filePath).readJson().getAsJsonObject();
        int id = object.get("id").getAsInt();
        String name = object.get("name").getAsString();
        PlayerClass.Builder builder = PlayerClass.builder(id, name);
        JsonArray skills = object.get("skills").getAsJsonArray();
        for (JsonElement skill : skills) {
            JsonObject skillObject = skill.getAsJsonObject();
            int skillId = skillObject.get("id").getAsInt();
            String skillName = skillObject.get("name").getAsString();
            int skillLevel = skillObject.get("level").getAsInt();
            JsonElement skillPrerequisiteIdElement = skillObject.get("prerequisiteId");
            Integer skillPrerequisiteId = skillPrerequisiteIdElement.isJsonNull() ? null
                    : skillPrerequisiteIdElement.getAsInt();
            String skillIconPath = dirPath + "/icons/" + skillObject.get("icon").getAsString();
            Asset skillIcon = new Asset(classLoader, skillIconPath);
            String skillDescription = skillObject.get("description").getAsString();
            int skillTreeRow = skillObject.get("skillTreeRow").getAsInt();
            int skillTreeColumn = skillObject.get("skillTreeColumn").getAsInt();
            int skillManaCost = skillObject.get("manaCost").getAsInt();
            double skillCooldownSeconds = skillObject.get("cooldown").getAsDouble();
            Duration skillCooldown = Duration.ofMillis((long) (skillCooldownSeconds * 1000));
            builder.activeSkill(skillId, skillName, skillLevel, skillPrerequisiteId, skillIcon,
                    skillDescription, skillTreeRow, skillTreeColumn, skillManaCost, skillCooldown);
        }
        return builder.build();
    }
}
