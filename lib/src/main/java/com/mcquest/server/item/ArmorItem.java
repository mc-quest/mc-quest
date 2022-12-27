package com.mcquest.server.item;

import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.PlayerCharacterEquipArmorItemEvent;
import com.mcquest.server.event.PlayerCharacterUnequipArmorItemEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * An ArmorItem is an Item that can be equipped by a PlayerCharacter to provide
 * protection.
 */
public class ArmorItem extends Item {
    private final int level;
    private final ArmorType type;
    private final ArmorSlot slot;
    private final Callable<InputStream> model;
    private final double protections;
    private final EventEmitter<PlayerCharacterEquipArmorItemEvent> onEquip;
    private final EventEmitter<PlayerCharacterUnequipArmorItemEvent> onUnequip;

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

    public Callable<InputStream> getModel() {
        return model;
    }

    public double getProtections() {
        return protections;
    }

    public EventEmitter<PlayerCharacterEquipArmorItemEvent> onEquip() {
        return onEquip;
    }

    public EventEmitter<PlayerCharacterUnequipArmorItemEvent> onUnequip() {
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
    public void writeResources(FileTree tree) {
        // TODO
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
        BuildStep model(Callable<InputStream> model);
    }

    public interface BuildStep {
        BuildStep description(String description);

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
        private Callable<InputStream> model;
        private String description;
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
        public BuildStep model(Callable<InputStream> model) {
            this.model = model;
            return this;
        }

        @Override
        public BuildStep description(String description) {
            this.description = description;
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
