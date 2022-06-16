package com.mcquest.server.main.constants;

import com.mcquest.server.api.item.ConsumableItem;
import com.mcquest.server.api.item.ItemManager;

/**
 * Provides references to Items loaded by the ItemLoader.
 */
public class Items {
    public static final ConsumableItem POTION_OF_MINOR_HEALING =
            (ConsumableItem) ItemManager.getItem("Potion of Minor Healing");
}
