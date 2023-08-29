package com.mcquest.core.item;

import com.google.common.collect.ListMultimap;
import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetTypes;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.resourcepack.Materials;
import com.mcquest.core.resourcepack.ResourcePackUtility;
import com.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.ArrayList;
import java.util.List;

public class KeyItem extends Item {
    private final Asset icon;
    private int customModelData;

    private KeyItem(Builder builder) {
        super(builder);
        this.icon = builder.icon;
    }

    public void use(PlayerCharacter pc) {
        pc.sendMessage(ItemUtility.useItemText(this));
    }

    @Override
    public int getStackSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getItemStack() {
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
        lore.add(ItemUtility.qualityText(quality, "Key Item"));

        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }

        return lore;
    }

    @Override
    @ApiStatus.Internal
    public void writeResources(FileTree tree, ListMultimap<Material, ItemOverride> overrides) {
        Key key = ItemUtility.resourcePackKey(this);
        customModelData = ResourcePackUtility
                .writeIcon(tree, icon, key, Materials.ITEM_DEFAULT, overrides);
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
        BuildStep icon(Asset model);
    }

    public interface BuildStep {
        BuildStep description(String description);

        KeyItem build();
    }

    private static class Builder extends Item.Builder
            implements IdStep, NameStep, QualityStep, IconStep, BuildStep {
        private Asset icon;

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
        public KeyItem build() {
            return new KeyItem(this);
        }
    }
}
