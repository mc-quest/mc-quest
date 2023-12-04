package net.mcquest.server.constants;

import com.google.gson.JsonObject;
import net.mcquest.core.item.*;
import net.mcquest.server.Assets;
import net.mcquest.core.asset.Asset;

public class Items {
    public static final Weapon ADVENTURERS_SWORD = loadWeapon("adventurers_sword");
    public static final Weapon ADVENTURERS_WAND = loadWeapon("adventurers_wand");
    public static final QuestItem WOLF_FLANK = loadQuestItem("wolf_flank");
    public static final ConsumableItem LESSER_HEALING_POTION = loadConsumable("lesser_healing_potion");
    public static final ConsumableItem LESSER_MANA_POTION = loadConsumable("lesser_mana_potion");
    public static final ConsumableItem MINOR_MANA_POTION = loadConsumable("minor_mana_potion");

    public static Item[] all() {
        return new Item[]{
                ADVENTURERS_SWORD,
                ADVENTURERS_WAND,
                WOLF_FLANK,
                LESSER_HEALING_POTION,
                LESSER_MANA_POTION,
                MINOR_MANA_POTION
        };
    }

    private static BasicItem loadBasicItem(String fileName) {
        String basePath = "items/basic/" + fileName;
        String itemPath = basePath + ".json";
        String iconPath = basePath + ".png";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        ItemQuality quality = ItemQuality.valueOf(object.get("quality").getAsString());
        Asset icon = Assets.asset(iconPath);
        BasicItem.BuildStep builder = BasicItem.builder()
                .id(id)
                .name(name)
                .quality(quality)
                .icon(icon);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        return builder.build();
    }

    private static QuestItem loadQuestItem(String fileName) {
        String basePath = "items/quests/" + fileName;
        String itemPath = basePath + ".json";
        String iconPath = basePath + ".png";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        ItemQuality quality = ItemQuality.valueOf(object.get("quality").getAsString());
        Asset icon = Assets.asset(iconPath);
        QuestItem.BuildStep builder = QuestItem.builder()
                .id(id)
                .name(name)
                .quality(quality)
                .icon(icon);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        return builder.build();
    }

    private static Weapon loadWeapon(String fileName) {
        String basePath = "items/weapons/" + fileName;
        String itemPath = basePath + ".json";
        String modelPath = basePath + ".bbmodel";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        ItemQuality quality = ItemQuality.valueOf(object.get("quality").getAsString());
        int level = object.get("level").getAsInt();
        WeaponType type = WeaponType.valueOf(object.get("type").getAsString());
        Asset model = Assets.asset(modelPath);
        double attackSpeed = object.get("attackSpeed").getAsDouble();
        Weapon.BuildStep builder = Weapon.builder()
                .id(id)
                .name(name)
                .quality(quality)
                .level(level)
                .type(type)
                .model(model)
                .attackSpeed(attackSpeed);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        if (object.has("physicalDamage")) {
            double physicalDamage = object.get("physicalDamage").getAsDouble();
            builder.physicalDamage(physicalDamage);
        }
        return builder.build();
    }

    private static ArmorItem loadArmor(String fileName) {
        String basePath = "items/armor/" + fileName;
        String itemPath = basePath + ".json";
        String modelPath = basePath + ".bbmodel";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        ItemQuality quality = ItemQuality.valueOf(object.get("quality").getAsString());
        int level = object.get("level").getAsInt();
        ArmorType type = ArmorType.valueOf(object.get("type").getAsString());
        ArmorSlot slot = ArmorSlot.valueOf(object.get("slot").getAsString());
        Asset model = Assets.asset(modelPath);
        ArmorItem.BuildStep builder = ArmorItem.builder()
                .id(id)
                .name(name)
                .quality(quality)
                .level(level)
                .type(type)
                .slot(slot)
                .model(model);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        if (object.has("protections")) {
            builder.protections(object.get("protections").getAsDouble());
        }
        return builder.build();
    }

    private static ConsumableItem loadConsumable(String fileName) {
        String basePath = "items/consumables/" + fileName;
        String itemPath = basePath + ".json";
        String iconPath = basePath + ".png";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        ItemQuality quality = ItemQuality.valueOf(object.get("quality").getAsString());
        int level = object.get("level").getAsInt();
        Asset icon = Assets.asset(iconPath);
        ConsumableItem.BuildStep builder = ConsumableItem.builder()
                .id(id)
                .name(name)
                .quality(quality)
                .level(level)
                .icon(icon);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        return builder.build();
    }
}
