package com.mcquest.server.item;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.asset.AssetTypes;
import com.mcquest.server.event.AutoAttackEvent;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.WeaponEquipEvent;
import com.mcquest.server.event.WeaponUnequipEvent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.ArrayList;
import java.util.List;

/**
 * A Weapon is an Item that can be equipped by a PlayerCharacter to damage
 * enemies.
 */
public class Weapon extends Item {
    public static final int HOTBAR_SLOT = 8;

    private final int level;
    private final WeaponType type;
    private final Asset model;
    private final double attackSpeed;
    private final double physicalDamage;
    private final EventEmitter<WeaponEquipEvent> onEquip;
    private final EventEmitter<WeaponUnequipEvent> onUnequip;
    private final EventEmitter<AutoAttackEvent> onAutoAttack;
    private int customModelData;

    Weapon(Builder builder) {
        super(builder.id, builder.name, builder.quality, builder.description);
        level = builder.level;
        type = builder.type;
        model = builder.model;
        this.attackSpeed = builder.attackSpeed;
        physicalDamage = builder.physicalDamage;
        onEquip = new EventEmitter<>();
        onUnequip = new EventEmitter<>();
        onAutoAttack = new EventEmitter<>();
    }

    @Override
    public int getStackSize() {
        return 1;
    }

    /**
     * Returns the minimum level required to equip this Weapon.
     */
    public int getLevel() {
        return level;
    }

    public WeaponType getType() {
        return type;
    }

    public Asset getModel() {
        return model;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getPhysicalDamage() {
        return physicalDamage;
    }

    public EventEmitter<WeaponEquipEvent> onEquip() {
        return onEquip;
    }

    public EventEmitter<WeaponUnequipEvent> onUnequip() {
        return onUnequip;
    }

    public EventEmitter<AutoAttackEvent> onAutoAttack() {
        return onAutoAttack;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemQuality quality = getQuality();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.qualityText(quality, type.getText()));
        lore.add(ItemUtility.levelText(level));
        lore.add(Component.empty());
        if (physicalDamage != 0.0) {
            lore.add(ItemUtility.statText("Physical Damage", physicalDamage));
        }
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
        customModelData = customModelDataStart;
        // ResourcePackUtility.writeItemModel(tree, model, customModelData, overrides);
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
        LevelStep quality(ItemQuality quality);
    }

    public interface LevelStep {
        TypeStep level(int level);
    }

    public interface TypeStep {
        ModelStep type(WeaponType type);
    }

    public interface ModelStep {
        AttackSpeedStep model(Asset model);
    }

    public interface AttackSpeedStep {
        BuildStep attackSpeed(double attackSpeed);
    }

    public interface BuildStep {
        BuildStep description(String description);

        BuildStep physicalDamage(double physicalDamage);

        Weapon build();
    }

    private static class Builder implements IdStep, NameStep, QualityStep,
            LevelStep, TypeStep, ModelStep, AttackSpeedStep, BuildStep {
        private int id;
        private String name;
        private ItemQuality quality;
        private int level;
        private WeaponType type;
        private Asset model;
        private double attackSpeed;
        private double physicalDamage;
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
        public TypeStep level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public ModelStep type(WeaponType type) {
            this.type = type;
            return this;
        }

        @Override
        public AttackSpeedStep model(Asset model) {
            model.requireType(AssetTypes.BBMODEL);
            this.model = model;
            return this;
        }

        @Override
        public BuildStep attackSpeed(double attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        @Override
        public BuildStep description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public BuildStep physicalDamage(double physicalDamage) {
            this.physicalDamage = physicalDamage;
            return this;
        }

        @Override
        public Weapon build() {
            return new Weapon(this);
        }
    }
}
