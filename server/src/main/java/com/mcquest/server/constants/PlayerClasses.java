package com.mcquest.server.constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.item.Material;

import java.time.Duration;

public class PlayerClasses {
    public static final PlayerClass FIGHTER = loadPlayerClass("Fighter");
    public static final PlayerClass MAGE = loadPlayerClass("Mage");

    private static PlayerClass loadPlayerClass(String fileName) {
        String path = "playerclasses/" + fileName + ".json";
        JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
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
            Material skillIcon = Material.fromNamespaceId(skillObject.get("icon").getAsString());
            String skillDescription = skillObject.get("description").getAsString();
            int skillTreeRow = skillObject.get("skillTreeRow").getAsInt();
            int skillTreeColumn = skillObject.get("skillTreeColumn").getAsInt();
            int skillManaCost = skillObject.get("manaCost").getAsInt();
            double skillCooldownSeconds = skillObject.get("cooldown").getAsDouble();
            Duration skillCooldown = Duration.ofMillis((long) (skillCooldownSeconds * 1000));
            builder.activeSkill(skillId, skillName, skillLevel, skillPrerequisiteId, skillIcon,
                    skillDescription, skillTreeRow, skillTreeColumn, skillManaCost, skillCooldown);
        }
        JsonArray skillTreeDecorations = object.get("skillTreeDecorations").getAsJsonArray();
        for (JsonElement skillTreeDecoration : skillTreeDecorations) {
            JsonObject skillTreeDecorationObject = skillTreeDecoration.getAsJsonObject();
            Material decorationIcon = Material.fromNamespaceId(
                    skillTreeDecorationObject.get("icon").getAsString());
            int decorationRow = skillTreeDecorationObject.get("row").getAsInt();
            int decorationColumn = skillTreeDecorationObject.get("column").getAsInt();
            builder.skillTreeDecoration(decorationIcon, decorationRow, decorationColumn);
        }
        return builder.build();
    }
}
