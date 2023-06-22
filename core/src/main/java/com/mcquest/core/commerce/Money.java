package com.mcquest.core.commerce;

import com.mcquest.core.instance.Instance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class Money {
    private final int value;

    public Money(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public static Money copper(int copper) {
        return new Money(copper);
    }

    public static Money silver(int silver) {
        return new Money(silver * 100);
    }

    public static Money gold(int gold) {
        return new Money(gold * 100 * 100);
    }

    public int getValue() {
        return value;
    }

    public int getCopperPart() {
        return value % 100;
    }

    public int getSilverPart() {
        return (value / 100) % 100;
    }

    public int getGoldPart() {
        return value / (100 * 100);
    }

    public Money add(Money money) {
        return new Money(value + money.value);
    }

    public Money subtract(Money money) {
        return new Money(value - money.value);
    }

    public void drop(Instance instance, Pos position) {
        // TODO: custom model data
        ItemStack itemStack = ItemStack.builder(Material.GOLD_NUGGET).build();
        ItemEntity entity = new ItemEntity(itemStack);
        entity.setCustomName(dropText());
        entity.setCustomNameVisible(true);
        entity.setInstance(instance, position);
    }

    private Component dropText() {
        Component text = Component.empty();

        int gold = getGoldPart();
        int silver = getSilverPart();
        int copper = getCopperPart();

        if (gold > 0) {
            text = text.append(Component.text(gold + "G", NamedTextColor.GOLD));
        }
        if (silver > 0) {
            text = text.append(Component.text(silver + "S", NamedTextColor.GRAY));
        }
        if (copper > 0) {
            text = text.append(Component.text(copper + "C", NamedTextColor.DARK_RED));
        }

        return text;
    }
}
