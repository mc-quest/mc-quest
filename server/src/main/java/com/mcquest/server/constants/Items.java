package com.mcquest.server.constants;

import com.google.gson.JsonObject;
import com.mcquest.core.item.*;
import com.mcquest.server.Assets;
import com.mcquest.core.asset.Asset;

public class Items {
    public static final Weapon ADVENTURERS_SWORD = loadWeapon("AdventurersSword");
    public static final BasicItem TEST_ITEM = loadBasicItem("TestItem");

    public static Item[] all() {
        return new Item[]{
                ADVENTURERS_SWORD,
                TEST_ITEM
        };
    }

    private static BasicItem loadBasicItem(String fileName) {
        String basePath = "items/basic/" + fileName;
        String itemPath = basePath + ".json";
        String iconPath = basePath + ".png";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        int id = object.get("id").getAsInt();
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

    private static Weapon loadWeapon(String fileName) {
        String basePath = "items/weapons/" + fileName;
        String itemPath = basePath + ".json";
        String modelPath = basePath + ".bbmodel";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        int id = object.get("id").getAsInt();
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
        int id = object.get("id").getAsInt();
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
        String basePath = "items/weapons/" + fileName;
        String itemPath = basePath + ".json";
        String iconPath = basePath + ".png";
        JsonObject object = Assets.asset(itemPath).readJson().getAsJsonObject();
        int id = object.get("id").getAsInt();
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
