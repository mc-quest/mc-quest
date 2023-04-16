package com.mcquest.server.item;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.event.ArmorEquipEvent;
import com.mcquest.server.event.ArmorUnequipEvent;
import com.mcquest.server.event.EventEmitter;
import net.kyori.adventure.text.Component;
import net.minestom.server.color.Color;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An ArmorItem is an Item that can be equipped by a PlayerCharacter to provide
 * protection.
 */
public class ArmorItem extends Item {
    private static final Set<Material> LEATHER_ARMOR_MATS = Set.of(
            Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,
            Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET);

    private final int level;
    private final ArmorType type;
    private final ArmorSlot slot;
    private Material material;
    private Color color;
    private final Asset model;
    private final double protections;
    private final EventEmitter<ArmorEquipEvent> onEquip;
    private final EventEmitter<ArmorUnequipEvent> onUnequip;

    ArmorItem(Builder builder) {
        super(builder.id, builder.name, builder.quality, builder.description);
        level = builder.level;
        type = builder.type;
        slot = builder.slot;
        model = builder.model;
        protections = builder.protections;
        onEquip = new EventEmitter<>();
        onUnequip = new EventEmitter<>();
    }

    public int getLevel() {
        return level;
    }

    public ArmorType getType() {
        return type;
    }

    public ArmorSlot getSlot() {
        return slot;
    }

    public double getProtections() {
        return protections;
    }

    public EventEmitter<ArmorEquipEvent> onEquip() {
        return onEquip;
    }

    public EventEmitter<ArmorUnequipEvent> onUnequip() {
        return onUnequip;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemQuality quality = getQuality();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.qualityText(quality, type.getText() + " Armor"));
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

    @ApiStatus.Internal
    @Override
    public int writeResources(FileTree tree, int customModelDataStart, List<ItemOverride> overrides) {
        if (model != null) {
            // TODO: write model
        }
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
        TypeStep level(int level);
    }

    public interface TypeStep {
        SlotStep type(ArmorType type);
    }

    public interface SlotStep {
        ModelStep slot(ArmorSlot slot);
    }

    public interface ModelStep {
        BuildStep model(Asset model);

        BuildStep model(Material model);
    }

    public interface BuildStep {
        BuildStep description(String description);

        BuildStep color(Color color);

        BuildStep protections(double protections);

        ArmorItem build();
    }

    private static class Builder implements IdStep, NameStep, QualityStep,
            LevelStep, TypeStep, SlotStep, ModelStep, BuildStep {
        private int id;
        private String name;
        private ItemQuality quality;
        private int level;
        private ArmorType type;
        private ArmorSlot slot;
        private String description;
        private Material materialModel;
        private Color color;
        private Asset model;
        private double protections;

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
        public TypeStep level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public SlotStep type(ArmorType type) {
            this.type = type;
            return this;
        }

        @Override
        public ModelStep slot(ArmorSlot slot) {
            this.slot = slot;
            return this;
        }

        @Override
        public BuildStep model(Asset model) {
            if (slot != ArmorSlot.HEAD) {
                throw new IllegalStateException();
            }
            this.model = model;
            return this;
        }

        @Override
        public BuildStep model(Material model) {
            this.materialModel = model;
            return this;
        }

        @Override
        public BuildStep description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public BuildStep color(Color color) {
            if (!LEATHER_ARMOR_MATS.contains(materialModel)) {
                throw new IllegalStateException();
            }
            this.color = color;
            return this;
        }

        @Override
        public BuildStep protections(double protections) {
            this.protections = protections;
            return this;
        }

        @Override
        public ArmorItem build() {
            return new ArmorItem(this);
        }
    }
}
