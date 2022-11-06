package com.mcquest.server.item;

import com.mcquest.server.event.Event;
import com.mcquest.server.event.PlayerCharacterUseConsumableItemEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * A ConsumableItem is an Item that can be consumed by a PlayerCharacter to
 * have some effect.
 */
public class ConsumableItem extends Item {
    private final int level;
    private final Event<PlayerCharacterUseConsumableItemEvent> onConsume;

    ConsumableItem(Builder builder) {
        super(builder);
        level = builder.level;
        onConsume = new Event<>();
    }

    public int getLevel() {
        return level;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, "Consumable"));
        lore.add(ItemUtility.levelText(level));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        lore.add(Component.empty());
        lore.add(ItemUtility.consumeText());
        return lore;
    }

    public Event<PlayerCharacterUseConsumableItemEvent> onConsume() {
        return onConsume;
    }

    public static Builder builder(int id, String name, ItemRarity rarity,
                                  Material icon, int level) {
        return new Builder(id, name, rarity, icon, level);
    }

    public static class Builder extends Item.Builder {
        final int level;

        Builder(int id, String name, ItemRarity rarity, Material icon, int level) {
            super(id, name, rarity, icon);
            this.level = level;
        }

        @Override
        public Builder description(String description) {
            return (Builder) super.description(description);
        }

        @Override
        public ConsumableItem build() {
            return new ConsumableItem(this);
        }
    }
}
