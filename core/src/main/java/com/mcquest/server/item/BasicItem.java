package com.mcquest.server.item;

import com.mcquest.server.asset.Asset;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.texture.Texture;

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
    List<Component> getItemStackLore() {
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
    public int writeResources(FileTree tree, int customModelDataStart, List<ItemOverride> overrides) {
        customModelData = customModelDataStart;

        Key key = Key.key("item", String.valueOf(getId()));
        Texture texture = Texture.of(key, Writable.inputStream(icon::getStream));
        tree.write(texture);

        Model model = Model.builder()
                .key(key)
                .parent(Key.key("minecraft", "item/handheld"))
                .textures(ModelTexture.builder()
                        .layers(key)
                        .build())
                .build();
        tree.write(model);

        ItemPredicate itemPredicate = ItemPredicate.customModelData(customModelData);
        Key modelKey = Key.key("item", String.valueOf(getId()));
        ItemOverride itemOverride = ItemOverride.of(modelKey, itemPredicate);
        overrides.add(itemOverride);

        return 1;
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
