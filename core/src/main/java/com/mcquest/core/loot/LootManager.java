package com.mcquest.core.loot;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.character.PlayerCharacterManager;
import com.mcquest.core.event.LootChestRespawnEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;

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

        LootChest lootChest = lootChestEntity.getLootChest();
        if (lootChest.isOpened()) {
            return;
        }

        lootChest.open(pc);

        Duration respawnDuration = lootChest.getRespawnDuration();
        if (respawnDuration != null) {
            MinecraftServer.getSchedulerManager()
                    .buildTask(() -> respawn(lootChest))
                    .delay(respawnDuration).schedule();
        }
    }

    private void respawn(LootChest lootChest) {
        LootChest respawned = new LootChest(lootChest);

        LootChestRespawnEvent event = new LootChestRespawnEvent(respawned);
        lootChest.onRespawn().emit(event);
        MinecraftServer.getGlobalEventHandler().call(event);

        mmorpg.getObjectManager().add(respawned);
    }
}
