package com.mcquest.core.commerce;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.Item;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Shop {
    private final List<Item> items;

    public Shop(Collection<Item> items) {
        this.items = new ArrayList<>(items);
        this.items.sort(Shop::compareItems);

    }

    private static int compareItems(Item item1, Item item2) {
        return item1.getName().compareTo(item2.getName());
    }

    public void open(PlayerCharacter pc) {
//        Inventory menu = makeMenu();
//        pc.getPlayer().openInventory(menu);
    }

    private Inventory makeMenu(PlayerCharacter pc) {
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.empty());
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
//            ItemStack itemStack = item.getShopItemStack();
//            inventory.setItemStack(i, itemStack);
        }
        inventory.addInventoryCondition((player, slot, clickType, result) -> {
            result.setCancel(true);
        });
        return inventory;
    }
}
