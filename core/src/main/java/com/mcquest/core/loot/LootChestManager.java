package com.mcquest.core.loot;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.character.PlayerCharacterManager;
import com.mcquest.core.Mmorpg;
import com.mcquest.core.instance.ChunkAddress;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Chunk;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LootChestManager {
    private final Mmorpg mmorpg;
    private final Map<Pos, LootChest> lootChestsByPosition;
    private final Map<ChunkAddress, Set<LootChest>> lootChestsByChunk;
    private final Map<PlayerCharacter, LootChest> openLootChestsByPc;

    @ApiStatus.Internal
    public LootChestManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        lootChestsByPosition = new HashMap<>();
        lootChestsByChunk = new HashMap<>();
        openLootChestsByPc = new HashMap<>();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(InstanceChunkLoadEvent.class, this::handleChunkLoad);
        // Blocks and holograms are automatically removed on chunk unload.
        eventHandler.addListener(PlayerBlockInteractEvent.class, this::handleInteract);
        eventHandler.addListener(InventoryCloseEvent.class, this::handleInventoryClose);
    }

    public void addLootChest(LootChest lootChest) {
        Instance instance = lootChest.getInstance();
        Pos position = lootChest.getPosition();
        int chunkX = position.chunkX();
        int chunkZ = position.chunkZ();

        if (lootChestsByPosition.containsKey(position)) {
            throw new IllegalArgumentException("Loot chest already at " + position);
        }
        lootChestsByPosition.put(position, lootChest);

        ChunkAddress chunkAddress = new ChunkAddress(instance, chunkX, chunkZ);
        if (!lootChestsByChunk.containsKey(chunkAddress)) {
            lootChestsByChunk.put(chunkAddress, new HashSet<>());
        }
        lootChestsByChunk.get(chunkAddress).add(lootChest);

        Chunk chunk = instance.getChunk(chunkX, chunkZ);
        if (chunk != null && chunk.isLoaded()) {
            lootChest.spawn();
        }
    }

    public void removeLootChest(LootChest lootChest) {
        Instance instance = lootChest.getInstance();
        Pos position = lootChest.getPosition();
        lootChestsByPosition.remove(position);
        ChunkAddress chunkAddress = new ChunkAddress(instance, position.chunkX(), position.chunkZ());
        Set<LootChest> lootChestsInChunk = lootChestsByChunk.get(chunkAddress);
        lootChestsInChunk.remove(lootChest);
        if (lootChestsInChunk.isEmpty()) {
            lootChestsByChunk.remove(chunkAddress);
        }
        // TODO
//        if (spawned) {
//            lootChest.remove();
//        }
    }

    private void handleChunkLoad(InstanceChunkLoadEvent event) {
        Instance instance = (Instance) event.getInstance();
        int chunkX = event.getChunkX();
        int chunkZ = event.getChunkZ();
        ChunkAddress chunkAddress = new ChunkAddress(instance, chunkX, chunkZ);
        if (lootChestsByChunk.containsKey(chunkAddress)) {
            Set<LootChest> lootChests = lootChestsByChunk.get(chunkAddress);
            for (LootChest lootChest : lootChests) {
                lootChest.spawn();
            }
        }
    }

    private void handleInteract(PlayerBlockInteractEvent event) {
        Point position = Pos.fromPoint(event.getBlockPosition());
        LootChest lootChest = lootChestsByPosition.get(position);
        if (lootChest == null) {
            return;
        }
        Player player = event.getPlayer();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(player);
        openLootChest(pc, lootChest);
    }

    private void openLootChest(PlayerCharacter pc, LootChest lootChest) {
        openLootChestsByPc.put(pc, lootChest);
        lootChest.open(pc);
        removeLootChest(lootChest);
    }

    private void closeLootChest(PlayerCharacter pc, LootChest lootChest) {
        openLootChestsByPc.remove(pc);
    }

    private void handleInventoryClose(InventoryCloseEvent event) {
        // TODO
    }
}
