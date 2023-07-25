package com.mcquest.core.loot;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.character.PlayerCharacterManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.ApiStatus;

public class LootManager {
    private final Mmorpg mmorpg;

    @ApiStatus.Internal
    public LootManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerEntityInteractEvent.class, this::handleInteract);
    }

    private void handleInteract(PlayerEntityInteractEvent event) {
        if (!(event.getTarget() instanceof LootChest.Entity lootChestEntity)) {
            return;
        }

        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(event.getPlayer());

        lootChestEntity.getLootChest().open(pc);
    }
}
