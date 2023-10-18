package net.mcquest.core.item;

import com.google.common.collect.ListMultimap;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.event.EventEmitter;
import net.mcquest.core.event.ItemConsumeEvent;
import net.mcquest.core.resourcepack.Materials;
import net.mcquest.core.resourcepack.ResourcePackUtility;
import net.mcquest.core.ui.Hotbar;
import net.mcquest.core.util.ItemStackUtility;
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
        super(builder);
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
    public ItemStack getItemStack() {
        return getItemStack(customModelDataStart);
    }

    ItemStack getHotbarItemStack(int cooldownTexture) {
        int customModelData = customModelDataStart + cooldownTexture;
        return getItemStack(customModelData);
    }

    private ItemStack getItemStack(int customModelData) {
        return ItemStackUtility
                .create(Materials.ITEM_DEFAULT, getDisplayName(), itemStackLore())
                .set(ID_TAG, getId())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
    }

    private List<Component> itemStackLore() {
        ItemQuality quality = getQuality();
        String description = getDescription();

        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.qualityText(quality, "Consumable"));
        lore.add(ItemUtility.levelText(level));

        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }

        return lore;
    }

    @Override
    @ApiStatus.Internal
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
        NameStep id(String id);
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

    private static class Builder extends Item.Builder
            implements IdStep, NameStep, QualityStep, LevelStep, IconStep, BuildStep {
        private int level;
        private Asset icon;

        @Override
        public NameStep id(String id) {
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
