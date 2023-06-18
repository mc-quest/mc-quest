package com.mcquest.server.item;

import com.google.common.collect.ListMultimap;
import com.mcquest.server.asset.Asset;
import com.mcquest.server.asset.AssetTypes;
import com.mcquest.server.resourcepack.Materials;
import com.mcquest.server.resourcepack.ResourcePackUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.ArrayList;
import java.util.List;

public class BasicItem extends Item {
    private final Asset icon;
    private int customModelData;

    public BasicItem(Builder builder) {
        super(builder.id, builder.name, builder.quality, builder.description);
        icon = builder.icon;
    }

    public Asset getIcon() {
        return icon;
    }

    @Override
    public int getStackSize() {
        return 64;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemStack.builder(Materials.ITEM_DEFAULT)
                .set(ID_TAG, getId())
                .displayName(getDisplayName())
                .lore(lore())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
    }

    private List<Component> lore() {
        ItemQuality quality = getQuality();
        String description = getDescription();

        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.qualityText(quality, "Item"));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        return lore;
    }

    @ApiStatus.Internal
    @Override
    public void writeResources(FileTree tree,
                               ListMultimap<Material, ItemOverride> overrides) {
        Key key = ItemUtility.resourcePackKey(this);
        customModelData = ResourcePackUtility.writeIcon(tree, icon, key,
                Materials.ITEM_DEFAULT, overrides);
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
        IconStep quality(ItemQuality quality);
    }

    public interface IconStep {
        BuildStep icon(Asset icon);
    }

    public interface BuildStep {
        BuildStep description(String description);

        BasicItem build();
    }

    private static class Builder implements IdStep, NameStep, QualityStep, IconStep, BuildStep {
        private int id;
        private String name;
        private ItemQuality quality;
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
        public IconStep quality(ItemQuality quality) {
            this.quality = quality;
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
        public BasicItem build() {
            return new BasicItem(this);
        }
    }
}
