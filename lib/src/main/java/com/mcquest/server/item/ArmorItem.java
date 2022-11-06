package com.mcquest.server.item;

import com.mcquest.server.event.*;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * An ArmorItem is an Item that can be equipped by a PlayerCharacter to provide
 * protection.
 */
public class ArmorItem extends Item {
    private final int level;
    private final ArmorType type;
    private final ArmorSlot slot;
    private final double protections;
    private final Event<PlayerCharacterEquipArmorItemEvent> onEquip;
    private final Event<PlayerCharacterUnequipArmorItemEvent> onUnequip;

    ArmorItem(Builder builder) {
        super(builder);
        level = builder.level;
        type = builder.type;
        slot = builder.slot;
        protections = builder.protections;
        onEquip = new Event<>();
        onUnequip = new Event<>();
    }

    public ArmorType getType() {
        return type;
    }

    public ArmorSlot getSlot() {
        return slot;
    }

    public int getLevel() {
        return level;
    }

    public double getProtections() {
        return protections;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, type.getText() + " Armor"));
        lore.add(ItemUtility.levelText(level));
        lore.add(Component.empty());
        lore.add(ItemUtility.statText("Protections", protections));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        lore.add(Component.empty());
        lore.add(ItemUtility.equipText());
        return lore;
    }

    public Event<PlayerCharacterEquipArmorItemEvent> onEquip() {
        return onEquip;
    }

    public Event<PlayerCharacterUnequipArmorItemEvent> onUnequip() {
        return onUnequip;
    }

    public static Builder builder(int id, String name, ItemRarity rarity,
                                  Material icon, int level, ArmorType type,
                                  ArmorSlot slot, double protections) {
        return new Builder(id, name, rarity, icon, level, type, slot, protections);
    }

    public static class Builder extends Item.Builder {
        final int level;
        final ArmorType type;
        final ArmorSlot slot;
        final double protections;

        Builder(int id, String name, ItemRarity rarity, Material icon,
                int level, ArmorType type, ArmorSlot slot, double protections) {
            super(id, name, rarity, icon);
            this.level = level;
            this.type = type;
            this.slot = slot;
            this.protections = protections;
        }

        @Override
        public Builder description(String description) {
            return (Builder) super.description(description);
        }

        @Override
        public ArmorItem build() {
            return new ArmorItem(this);
        }
    }
}
