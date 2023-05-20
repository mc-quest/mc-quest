package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Item;
import net.minestom.server.item.ItemStack;

public class ItemLoot extends Loot {
    private int amount;

    ItemLoot(ItemPoolEntry entry, int amount) {
        super(entry);
        this.amount = amount;
    }

    @Override
    public ItemPoolEntry getEntry() {
        return (ItemPoolEntry) super.getEntry();
    }

    @Override
    ItemStack getItemStack() {
        Item item = getEntry().getItem();
        return item.getItemStack().withAmount(amount);
    }

    @Override
    ItemStack loot(PlayerCharacter pc) {
        Item item = getEntry().getItem();
        int received = pc.giveItem(item, amount);
        amount -= received;
        return item.getItemStack().withAmount(amount);
    }
}
