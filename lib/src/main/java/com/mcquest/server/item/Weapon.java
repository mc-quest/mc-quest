package com.mcquest.server.item;

import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.PlayerCharacterBasicAttackEvent;
import com.mcquest.server.event.PlayerCharacterEquipWeaponEvent;
import com.mcquest.server.event.PlayerCharacterUnequipWeaponEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * A Weapon is an Item that can be equipped by a PlayerCharacter to damage
 * enemies.
 */
public class Weapon extends Item {
    private final int level;
    private final WeaponType type;
    private final double physicalDamage;
    private final EventEmitter<PlayerCharacterEquipWeaponEvent> onEquip;
    private final EventEmitter<PlayerCharacterUnequipWeaponEvent> onUnequip;
    private final EventEmitter<PlayerCharacterBasicAttackEvent> onBasicAttack;

    Weapon(Builder builder) {
        super(builder);
        level = builder.level;
        type = builder.type;
        physicalDamage = builder.physicalDamage;
        onEquip = new EventEmitter<>();
        onUnequip = new EventEmitter<>();
        onBasicAttack = new EventEmitter<>();
    }

    public WeaponType getType() {
        return type;
    }

    /**
     * Returns the minimum level required to equip this Weapon.
     */
    public int getLevel() {
        return level;
    }

    public double getPhysicalDamage() {
        return physicalDamage;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, type.getText()));
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

    public EventEmitter<PlayerCharacterEquipWeaponEvent> onEquip() {
        return onEquip;
    }

    public EventEmitter<PlayerCharacterUnequipWeaponEvent> onUnequip() {
        return onUnequip;
    }

    public EventEmitter<PlayerCharacterBasicAttackEvent> onBasicAttack() {
        return onBasicAttack;
    }

    public static Builder builder(int id, String name, ItemRarity rarity,
                                  Material icon, int level, WeaponType type) {
        return new Builder(id, name, rarity, icon, level, type);
    }

    public static class Builder extends Item.Builder {
        final int level;
        final WeaponType type;
        private double physicalDamage;

        Builder(int id, String name, ItemRarity rarity,
                Material icon, int level, WeaponType type) {
            super(id, name, rarity, icon);
            this.level = level;
            this.type = type;
            physicalDamage = 0.0;
        }

        @Override
        public Builder description(String description) {
            return (Builder) super.description(description);
        }

        public Builder physicalDamage(double physicalDamage) {
            if (physicalDamage < 0) {
                throw new IllegalArgumentException();
            }
            this.physicalDamage = physicalDamage;
            return this;
        }

        @Override
        public Weapon build() {
            return new Weapon(this);
        }
    }
}
