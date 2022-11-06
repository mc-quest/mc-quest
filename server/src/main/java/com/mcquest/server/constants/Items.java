package com.mcquest.server.constants;

import com.google.gson.JsonObject;
import com.mcquest.server.item.*;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.item.Material;

public class Items {
    public static final Weapon ADVENTURERS_SWORD = loadWeapon("AdventurersSword");

    private static Item loadItem(String fileName) {
        String path = "items/items/" + fileName + ".json";
        JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
        int id = object.get("id").getAsInt();
        String name = object.get("name").getAsString();
        ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
        Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
        Item.Builder builder = Item.builder(id, name, rarity, icon);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        return builder.build();
    }

    private static Weapon loadWeapon(String fileName) {
        String path = "items/weapons/" + fileName + ".json";
        JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
        int id = object.get("id").getAsInt();
        String name = object.get("name").getAsString();
        ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
        Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
        int level = object.get("level").getAsInt();
        WeaponType type = WeaponType.valueOf(object.get("type").getAsString());
        Weapon.Builder builder = Weapon.builder(id, name, rarity, icon, level, type);
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
        String path = "items/armor/" + fileName + ".json";
        JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
        int id = object.get("id").getAsInt();
        String name = object.get("name").getAsString();
        ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
        Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
        int level = object.get("level").getAsInt();
        ArmorType type = ArmorType.valueOf(object.get("type").getAsString());
        ArmorSlot slot = ArmorSlot.valueOf(object.get("slot").getAsString());
        double protections = object.get("protections").getAsDouble();
        ArmorItem.Builder builder = ArmorItem.builder(
                id, name, rarity, icon, level, type, slot, protections);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        return builder.build();
    }

    private static ConsumableItem loadConsumable(String fileName) {
        String path = "items/consumables/" + fileName + ".json";
        JsonObject object = ResourceUtility.getResourceAsJson(path).getAsJsonObject();
        int id = object.get("id").getAsInt();
        String name = object.get("name").getAsString();
        ItemRarity rarity = ItemRarity.valueOf(object.get("rarity").getAsString());
        Material icon = Material.fromNamespaceId(object.get("icon").getAsString());
        int level = object.get("level").getAsInt();
        ConsumableItem.Builder builder = ConsumableItem.builder(
                id, name, rarity, icon, level);
        if (object.has("description")) {
            builder.description(object.get("description").getAsString());
        }
        return builder.build();
    }
}
