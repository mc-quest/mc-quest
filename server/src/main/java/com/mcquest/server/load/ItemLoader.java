package com.mcquest.server.load;

import com.google.gson.JsonObject;
import com.mcquest.server.Mmorpg;
import com.mcquest.server.item.*;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.item.Material;

import java.util.List;

public class ItemLoader {
    public static void loadItems(Mmorpg mmorpg) {
        ItemManager itemManager = mmorpg.getItemManager();
        loadBaseItems(itemManager);
        loadWeapons(itemManager);
        loadArmor(itemManager);
        loadConsumables(itemManager);
    }

    private static void loadBaseItems(ItemManager itemManager) {
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

    private static void loadWeapons(ItemManager itemManager) {
        List<String> paths = ResourceUtility.getResources("items/weapons");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
            Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
            int level = object.get("level").getAsInt();
            WeaponType type = WeaponType.valueOf(object.get("type").getAsString());
            WeaponBuilder builder = itemManager.weaponBuilder(id, name, rarity, icon, level, type);
            if (object.has("description")) {
                builder.description(object.get("description").getAsString());
            }
            if (object.has("physicalDamage")) {
                double physicalDamage = object.get("physicalDamage").getAsDouble();
                builder.physicalDamage(physicalDamage);
            }
            builder.build();
        }
    }

    private static void loadArmor(ItemManager itemManager) {
        List<String> paths = ResourceUtility.getResources("items/armor");
        for (String path : paths) {
            JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
            int id = object.get("id").getAsInt();
            String name = object.get("name").getAsString();
            ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
            Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
            int level = object.get("level").getAsInt();
            ArmorType type = ArmorType.valueOf(object.get("type").getAsString());
            ArmorSlot slot = ArmorSlot.valueOf(object.get("slot").getAsString());
            double protections = object.get("protections").getAsDouble();
            ArmorItemBuilder builder = itemManager.armorItemBuilder(id,
                    name, rarity, icon, level, type, slot, protections);
            if (object.has("description")) {
                builder.description(object.get("description").getAsString());
            }
            builder.build();
        }
    }

    private static void loadConsumables(ItemManager itemManager) {
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
