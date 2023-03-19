package com.mcquest.server.item;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BasicItem extends Item {
    private final Callable<InputStream> icon;

    public BasicItem(Builder builder) {
        super(builder.id, builder.name, builder.quality, builder.description);
        icon = builder.icon;
    }

    public Callable<InputStream> getIcon() {
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
    public int writeResources(FileTree tree, int customModelDataStart) {
        // TODO
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
        BuildStep icon(Callable<InputStream> icon);
    }

    public interface BuildStep {
        BuildStep description(String description);

        BasicItem build();
    }

    private static class Builder implements IdStep, NameStep, QualityStep, IconStep, BuildStep {
        private int id;
        private String name;
        private ItemQuality quality;
        private Callable<InputStream> icon;
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
        public BuildStep icon(Callable<InputStream> icon) {
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
