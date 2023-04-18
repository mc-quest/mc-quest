package com.mcquest.server.character;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.SpatialHashCell;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NonPlayerCharacterSpawner {
    private static final double CELL_SIZE = 85.0;
    private static final double SPAWN_RADIUS = 75.0;
    private static final double DESPAWN_RADIUS = 85.0;

    private final Mmorpg mmorpg;
    private final Map<SpatialHashCell, Set<NonPlayerCharacter>> npcs;
    private final Set<NonPlayerCharacter> spawnedNpcs;

    public NonPlayerCharacterSpawner(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        npcs = new HashMap<>();
        spawnedNpcs = new HashSet<>();
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        schedulerManager.buildTask(this::tick)
                .repeat(TaskSchedule.nextTick()).schedule();
    }

    private void tick() {
        Set<NonPlayerCharacter> toSpawn = new HashSet<>();
        Set<NonPlayerCharacter> toNotDespawn = new HashSet<>();

        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        for (PlayerCharacter pc : pcManager.getPlayerCharacters()) {
            Instance instance = pc.getInstance();
            Pos pcPosition = pc.getPosition();

            Pos npcMin = pcPosition.sub(DESPAWN_RADIUS);
            Pos npcMax = pcPosition.add(DESPAWN_RADIUS);

            SpatialHashCell minCell = SpatialHashCell.cellAt(instance, npcMin, CELL_SIZE);
            SpatialHashCell maxCell = SpatialHashCell.cellAt(instance, npcMax, CELL_SIZE);

            for (int x = minCell.getX(); x <= maxCell.getX(); x++) {
                for (int y = minCell.getY(); y <= maxCell.getY(); y++) {
                    for (int z = minCell.getZ(); z <= maxCell.getZ(); z++) {
                        SpatialHashCell cell = new SpatialHashCell(instance, x, y, z);
                        Set<NonPlayerCharacter> cellNpcs = npcs.get(cell);
                        if (cellNpcs != null) {
                            for (NonPlayerCharacter npc : cellNpcs) {
                                if (npc.getPosition().distanceSquared(pcPosition) <= SPAWN_RADIUS * SPAWN_RADIUS
                                        && !npc.isSpawned()) {
                                    toSpawn.add(npc);
                                }
                                if (npc.getPosition().distanceSquared(pcPosition) <= DESPAWN_RADIUS * DESPAWN_RADIUS
                                        && npc.isSpawned()) {
                                    toNotDespawn.add(npc);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (NonPlayerCharacter npc : spawnedNpcs) {
            if (!toNotDespawn.contains(npc)) {
                npc.despawn();
            }
        }
        spawnedNpcs.retainAll(toNotDespawn);

        for (NonPlayerCharacter npc : toSpawn) {
            npc.spawn();
        }
        spawnedNpcs.addAll(toSpawn);
    }

    public void add(@NotNull NonPlayerCharacter npc) {
        if (npc.spawner != null) {
            throw new IllegalArgumentException("npc already added");
        }
        Instance instance = npc.getInstance();
        Pos position = npc.getPosition();
        SpatialHashCell cell = SpatialHashCell.cellAt(instance, position, CELL_SIZE);
        addToCell(cell, npc);
    }

    public void remove(@NotNull NonPlayerCharacter npc) {
        if (npc.spawner != this) {
            throw new IllegalArgumentException("npc already removed");
        }
        if (npc.isSpawned()) {
            npc.despawn();
        }
        npc.spawner = null;
        Instance instance = npc.getInstance();
        Pos position = npc.getPosition();
        SpatialHashCell cell = SpatialHashCell.cellAt(instance, position, CELL_SIZE);
        removeFromCell(cell, npc);
    }

    void updateCell(NonPlayerCharacter npc, Instance newInstance, Pos newPosition) {
        Instance prevInstance = npc.getInstance();
        Pos prevPosition = npc.getPosition();
        SpatialHashCell prevCell = SpatialHashCell.cellAt(prevInstance, prevPosition, CELL_SIZE);
        SpatialHashCell newCell = SpatialHashCell.cellAt(newInstance, newPosition, CELL_SIZE);
        if (!newCell.equals(prevCell)) {
            removeFromCell(prevCell, npc);
            addToCell(prevCell, npc);
        }
    }

    private void addToCell(SpatialHashCell cell, NonPlayerCharacter npc) {
        if (!npcs.containsKey(cell)) {
            npcs.put(cell, new HashSet<>());
        }
        Set<NonPlayerCharacter> cellNpcs = npcs.get(cell);
        cellNpcs.add(npc);
    }

    private void removeFromCell(SpatialHashCell cell, NonPlayerCharacter npc) {
        Set<NonPlayerCharacter> npcCells = npcs.get(cell);
        npcCells.remove(npc);
        if (npcCells.isEmpty()) {
            npcs.remove(cell);
        }
    }
}
