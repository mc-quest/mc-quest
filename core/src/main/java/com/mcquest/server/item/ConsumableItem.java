package com.mcquest.server.item;

import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.ItemConsumeEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A ConsumableItem is an Item that can be consumed by a PlayerCharacter to
 * have some effect.
 */
public class ConsumableItem extends Item {
    private final int level;
    private final Callable<InputStream> icon;
    private final EventEmitter<ItemConsumeEvent> onConsume;

    ConsumableItem(Builder builder) {
        super(builder.id, builder.name, builder.quality, builder.description);
        level = builder.level;
        icon = builder.icon;
        onConsume = new EventEmitter<>();
    }

    public int getLevel() {
        return level;
    }

    public Callable<InputStream> getIcon() {
        return icon;
    }

    public EventEmitter<ItemConsumeEvent> onConsume() {
        return onConsume;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemQuality quality = getQuality();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.qualityText(quality, "Consumable"));
        lore.add(ItemUtility.levelText(level));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        lore.add(Component.empty());
        lore.add(ItemUtility.consumeText());
        return lore;
    }

    @ApiStatus.Internal
    @Override
    public int writeResources(FileTree tree, int customModelDataStart) {
        // TODO
        return 0;
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
        BuildStep icon(Callable<InputStream> icon);
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
        public ConsumableItem build() {
            return new ConsumableItem(this);
        }
    }
}
