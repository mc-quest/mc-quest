package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An ArmorItem is an Item that can be equipped by a PlayerCharacter to provide
 * protection.
 */
public class ArmorItem extends Item {
    private final PlayerClass[] playerClasses;
    private final int level;
    private final ArmorSlot slot;
    private final double protections;

    ArmorItem(ArmorItemBuilder builder) {
        super(builder);
        this.playerClasses = builder.playerClasses.toArray(new PlayerClass[0]);
        this.slot = builder.slot;
        this.level = builder.level;
        this.protections = builder.protections;
    }

    public int getPlayerClassCount() {
        return playerClasses.length;
    }

    public PlayerClass getPlayerClass(int index) {
        return playerClasses[index];
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
        lore.add(ItemUtility.rarityText(rarity, "Armor"));
        lore.add(ItemUtility.playerClassText(playerClasses));
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
