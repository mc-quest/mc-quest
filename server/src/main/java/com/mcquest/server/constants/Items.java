package com.mcquest.server.constants;

import com.mcquest.server.item.ConsumableItem;
import com.mcquest.server.item.ItemManager;

/**
 * Provides references to Items loaded by the ItemLoader.
 */
public class Items {
    public static final ConsumableItem POTION_OF_MINOR_HEALING =
            (ConsumableItem) ItemManager.getItem("Potion of Minor Healing");
}
