package com.mcquest.server.ui;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.inventory.PlayerInventory;

public class InteractionManager {
    public static void registerListeners() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PickupItemEvent.class, InteractionManager::handlePickupItem);
    }

    private static void handlePickupItem(PickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerInventory inventory = player.getInventory();
            if (!inventory.addItemStack(event.getItemStack())) {
                event.setCancelled(true);
            }
        }
    }
}
