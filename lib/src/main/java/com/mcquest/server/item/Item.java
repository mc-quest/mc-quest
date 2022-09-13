package com.mcquest.server.item;

import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An Item represents an MMORPG item.
 */
public class Item {
    private final int id;
    private final String name;
    private final ItemRarity rarity;
    private final String icon;
    private final String description;
    private final ItemStack itemStack;

    Item(int id, @NotNull String name, @NotNull ItemRarity rarity,
         @NotNull Material icon, @NotNull String description) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.icon = icon.namespace().asString();
        this.description = description;
        this.itemStack = createItemStack().withTag(ItemManager.ID_TAG, id);
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the name of this Item.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the rarity of this Item.
     */
    public final ItemRarity getRarity() {
        return rarity;
    }

    /**
     * Returns the icon of this Item.
     */
    public final Material getIcon() {
        return Material.fromNamespaceId(icon);
    }

    /**
     * Returns the description of this Item.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Returns the ItemStack of this Item.
     */
    public final ItemStack getItemStack() {
        return itemStack;
    }

    public final Component getDisplayName() {
        return Component.text(name, rarity.getColor())
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Constructs the ItemStack of this Item. This method can be overridden to
     * add custom properties to the ItemStack. The amount of the returned
     * ItemStack must be exactly 1.
     */
    @NotNull ItemStack createItemStack() {
        List<Component> lore = new ArrayList<>();
        Component rarityText = Component.text(rarity.getText() + " Item", rarity.getColor())
                .decoration(TextDecoration.ITALIC, false);
        lore.add(rarityText);
        if (description != null) {
            lore.add(Component.empty());
            List<TextComponent> descriptionText = TextUtility.wordWrap(description);
            for (int i = 0; i < descriptionText.size(); i++) {
                descriptionText.set(i, descriptionText.get(i).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)).color(NamedTextColor.WHITE);
            }
            lore.addAll(descriptionText);
        }

        return ItemStack.builder(getIcon())
                .meta(builder ->
                        builder.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES, ItemHideFlag.HIDE_POTION_EFFECTS)
                )
                .displayName(getDisplayName())
                .lore(lore)
                .build();
    }

    public void drop(Instance instance, Pos position) {
        drop(instance, position, 1);
    }

    public void drop(@NotNull Instance instance, @NotNull Pos position, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        }
        ItemEntity drop = new ItemEntity(itemStack.withAmount(amount));
        drop.setCustomName(getDisplayName());
        drop.setCustomNameVisible(true);
        drop.setInstance(instance, position);
    }
}
