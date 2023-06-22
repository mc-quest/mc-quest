package com.mcquest.core.item;

import com.google.common.collect.ListMultimap;
import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetTypes;
import com.mcquest.core.event.EventEmitter;
import com.mcquest.core.event.ItemConsumeEvent;
import com.mcquest.core.resourcepack.Materials;
import com.mcquest.core.resourcepack.ResourcePackUtility;
import com.mcquest.core.ui.Hotbar;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.ArrayList;
import java.util.List;

/**
 * A ConsumableItem is an Item that can be consumed by a PlayerCharacter to
 * have some effect.
 */
public class ConsumableItem extends Item {
    private final int level;
    private final Asset icon;
    private final EventEmitter<ItemConsumeEvent> onConsume;
    private int customModelDataStart;

    ConsumableItem(Builder builder) {
        super(builder.id, builder.name, builder.quality, builder.description);
        level = builder.level;
        icon = builder.icon;
        onConsume = new EventEmitter<>();
    }

    public int getLevel() {
        return level;
    }

    public Asset getIcon() {
        return icon;
    }

    public EventEmitter<ItemConsumeEvent> onConsume() {
        return onConsume;
    }

    @Override
    public int getStackSize() {
        return 64;
    }

    @Override
    ItemStack getItemStack() {
        return ItemStack.builder(Materials.ITEM_DEFAULT)
                .set(ID_TAG, getId())
                .displayName(getDisplayName())
                .lore(lore())
                .meta(builder -> builder.customModelData(customModelDataStart))
                .build();
    }

    ItemStack getHotbarItemStack(int cooldownTexture) {
        int customModelData = customModelDataStart + cooldownTexture;

        return ItemStack.builder(Materials.ITEM_DEFAULT)
                .set(ID_TAG, getId())
                .displayName(getDisplayName())
                .lore(lore())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
    }

    private List<Component> lore() {
        List<Component> lore = new ArrayList<>();
        return lore;
    }

    @ApiStatus.Internal
    @Override
    public void writeResources(FileTree tree,
                               ListMultimap<Material, ItemOverride> overrides) {
        Material material = Materials.ITEM_DEFAULT;

        customModelDataStart = ResourcePackUtility.writeIcon(
                tree,
                icon,
                ItemUtility.resourcePackKey(this),
                material,
                overrides
        );

        // Cooldown textures.
        for (int i = 1; i <= Hotbar.COOLDOWN_TEXTURES; i++) {
            ResourcePackUtility.writeCooldownIcon(
                    tree,
                    icon,
                    ItemUtility.resourcePackKey(this, i),
                    i,
                    material,
                    overrides
            );
        }
    }

    public static IdStep builder() {
        return new Builder();
    }

    public interface IdStep {
        NameStep id(int id);
    }

    public interface NameStep {
        QualityStep name(String name);
    }

    public interface QualityStep {
        LevelStep quality(ItemQuality quality);
    }

    public interface LevelStep {
        IconStep level(int level);
    }

    public interface IconStep {
        BuildStep icon(Asset icon);
    }

    public interface BuildStep {
        BuildStep description(String description);

        ConsumableItem build();
    }

    private static class Builder implements IdStep, NameStep, QualityStep,
            LevelStep, IconStep, BuildStep {
        private int id;
        private String name;
        private ItemQuality quality;
        private int level;
        private Asset icon;
        private String description;

        @Override
        public NameStep id(int id) {
            this.id = id;
            return this;
        }

        @Override
        public QualityStep name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public LevelStep quality(ItemQuality quality) {
            this.quality = quality;
            return this;
        }

        @Override
        public IconStep level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public BuildStep icon(Asset icon) {
            icon.requireType(AssetTypes.PNG);
            this.icon = icon;
            return this;
        }

        @Override
        public BuildStep description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public ConsumableItem build() {
            return new ConsumableItem(this);
        }
    }
}
