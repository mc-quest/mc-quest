package net.mcquest.core.item;

import com.google.common.collect.ListMultimap;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.event.AutoAttackEvent;
import net.mcquest.core.event.EventEmitter;
import net.mcquest.core.event.WeaponEquipEvent;
import net.mcquest.core.event.WeaponUnequipEvent;
import net.mcquest.core.resourcepack.Materials;
import net.mcquest.core.resourcepack.ResourcePackUtility;
import net.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
        super(builder);
        level = builder.level;
        type = builder.type;
        model = builder.model;
        this.attackSpeed = builder.attackSpeed;
        physicalDamage = builder.physicalDamage;
        onEquip = new EventEmitter<>();
        onUnequip = new EventEmitter<>();
        onAutoAttack = new EventEmitter<>();
    }

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
    public int getStackSize() {
        return 1;
    }

    @Override
    public ItemStack getItemStack() {
        return ItemStackUtility
                .create(Materials.WEAPON, getDisplayName(), itemStackLore())
                .set(ID_TAG, getId())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
    }

    private List<Component> itemStackLore() {
        List<Component> lore = new ArrayList<>();

        lore.add(ItemUtility.qualityText(getQuality(), type.getText()));
        lore.add(ItemUtility.levelText(level));

        lore.add(Component.empty());

        if (physicalDamage != 0.0) {
            lore.add(ItemUtility.statText("Physical Damage", physicalDamage));
        }

        String description = getDescription();
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
        Key key = ItemUtility.resourcePackKey(this);
        customModelData = ResourcePackUtility
                .writeModel(tree, model, key, Materials.WEAPON, overrides);
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

    private static class Builder extends Item.Builder
            implements IdStep, NameStep, QualityStep, LevelStep, TypeStep,
            ModelStep, AttackSpeedStep, BuildStep {
        private int level;
        private WeaponType type;
        private Asset model;
        private double attackSpeed;
        private double physicalDamage;

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
