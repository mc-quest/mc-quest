package com.mcquest.server.load;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.Mmorpg;
import com.mcquest.server.item.*;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.item.Material;

import java.util.List;

public class ItemLoader {
    public static void loadItems(Mmorpg mmorpg) {
        loadBaseItems(mmorpg);
        loadWeapons(mmorpg);
        loadArmor(mmorpg);
        loadConsumables(mmorpg);
    }

    private static void loadBaseItems(Mmorpg mmorpg) {
        ItemManager itemManager = mmorpg.getItemManager();
        List<String> paths = ResourceUtility.getResources("items/items");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
            Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
            ItemBuilder builder = itemManager.itemBuilder(id, name, rarity, icon);
            if (object.has("description")) {
                builder.description(object.get("description").getAsString());
            }
            builder.build();
        }
    }

    private static void loadWeapons(Mmorpg mmorpg) {
        ItemManager itemManager = mmorpg.getItemManager();
        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        List<String> paths = ResourceUtility.getResources("items/weapons");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
            Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
            int level = object.get("level").getAsInt();
            WeaponBuilder builder = itemManager.weaponBuilder(id, name, rarity, icon, level);
            if (object.has("description")) {
                builder.description(object.get("description").getAsString());
            }
            JsonArray playerClassIds = object.get("playerClassIds").getAsJsonArray();
            for (JsonElement playerClassId : playerClassIds) {
                PlayerClass playerClass = playerClassManager.getPlayerClass(playerClassId.getAsInt());
                builder.addPlayerClass(playerClass);
            }
            if (object.has("physicalDamage")) {
                double physicalDamage = object.get("physicalDamage").getAsDouble();
                builder.physicalDamage(physicalDamage);
            }
            builder.build();
        }
    }

    private static void loadArmor(Mmorpg mmorpg) {
        ItemManager itemManager = mmorpg.getItemManager();
        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        List<String> paths = ResourceUtility.getResources("items/armor");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
            Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
            int level = object.get("level").getAsInt();
            ArmorSlot slot = ArmorSlot.valueOf(object.get("slot").getAsString());
            double protections = object.get("protections").getAsDouble();
            ArmorItemBuilder builder = itemManager.armorItemBuilder(id,
                    name, rarity, icon, level, slot, protections);
            if (object.has("description")) {
                builder.description(object.get("description").getAsString());
            }
            JsonArray playerClassIds = object.get("playerClassIds").getAsJsonArray();
            for (JsonElement playerClassId : playerClassIds) {
                PlayerClass playerClass = playerClassManager.getPlayerClass(playerClassId.getAsInt());
                builder.addPlayerClass(playerClass);
            }
            builder.build();
        }
    }

    private static void loadConsumables(Mmorpg mmorpg) {
        ItemManager itemManager = mmorpg.getItemManager();
        List<String> paths = ResourceUtility.getResources("items/consumables");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
            Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
            int level = object.get("level").getAsInt();
            ConsumableItemBuilder builder = itemManager.consumableItemBuilder(id,
                    name, rarity, icon, level);
            if (object.has("description")) {
                builder.description(object.get("description").getAsString());
            }
            builder.build();
        }
    }
}
