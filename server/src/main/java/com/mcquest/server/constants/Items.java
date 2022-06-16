package com.mcquest.server.constants;

import com.mcquest.server.item.ConsumableItem;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.ItemManager;

/**
 * Provides references to Items loaded by the ItemLoader.
 */
public class Items {
    public static final Item ADVENTURERS_CAP = ItemManager.getItem("Adventurer's Cap");

    public static final ConsumableItem POTION_OF_MINOR_HEALING =
            (ConsumableItem) ItemManager.getItem("Minor Potion of Healing");
}
