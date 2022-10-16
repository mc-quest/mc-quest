package com.mcquest.server.item;

import net.kyori.adventure.text.Component;

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

    ArmorItem(ArmorItemBuilder builder) {
        super(builder);
        this.level = builder.level;
        this.type = builder.type;
        this.slot = builder.slot;
        this.protections = builder.protections;
    }

    public ArmorType getType() {
        return type;
    }

    /**
     * Returns the slot this ArmorItem occupies.
     */
    public ArmorSlot getSlot() {
        return slot;
    }

    /**
     * Returns the minimum level required to equip this ArmorItem.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns how many protections this ArmorItem provides.
     */
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
}
