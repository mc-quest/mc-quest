package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.commerce.Money;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class MoneyLoot extends Loot {
    private final Money value;

    MoneyLoot(MoneyPoolEntry entry, Money value) {
        super(entry);
        this.value = value;
    }

    @Override
    public MoneyPoolEntry getEntry() {
        return (MoneyPoolEntry) super.getEntry();
    }

    public Money getValue() {
        return value;
    }

    @Override
    ItemStack getItemStack() {
        // TODO
        return ItemStack.of(Material.GOLD_NUGGET).withDisplayName(Component.text(value.getValue()));
    }

    @Override
    ItemStack loot(PlayerCharacter pc) {
        pc.setMoney(pc.getMoney().add(value));
        pc.sendMessage(Component.text("Money is now " + pc.getMoney().getValue()));
        return ItemStack.AIR;
    }
}
