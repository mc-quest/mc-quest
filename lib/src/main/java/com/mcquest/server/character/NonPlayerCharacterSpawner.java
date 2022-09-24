package com.mcquest.server.character;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class NonPlayerCharacterSpawner {
    private final Set<NonPlayerCharacter> spawningNpcs;

    public NonPlayerCharacterSpawner() {
        spawningNpcs = new HashSet<>();
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        schedulerManager.buildTask(this::tick)
                .repeat(TaskSchedule.nextTick()).schedule();
    }

    private void tick() {
        Set<NonPlayerCharacter> toSpawn = new HashSet<>();
        Set<NonPlayerCharacter> toDespawn = new HashSet<>();

        for (NonPlayerCharacter npc : spawningNpcs) {
            if (npc.isSpawned()) {
                if (npc.shouldDespawn()) {
                    toDespawn.add(npc);
                }
            } else {
                if (npc.shouldSpawn() && npc.isAlive()) {
                    toSpawn.add(npc);
                }
            }
        }

        for (NonPlayerCharacter npc : toSpawn) {
            npc.spawn();
        }
        for (NonPlayerCharacter npc : toDespawn) {
            npc.despawn();
        }
    }

    public void add(@NotNull NonPlayerCharacter npc) {
        if (spawningNpcs.contains(npc)) {
            throw new IllegalArgumentException("npc already added");
        }
        spawningNpcs.add(npc);
    }

    public void remove(@NotNull NonPlayerCharacter npc) {
        if (!spawningNpcs.contains(npc)) {
            throw new IllegalArgumentException("npc not previously added");
        }
        if (npc.isSpawned()) {
            npc.despawn();
        }
        spawningNpcs.remove(npc);
    }
}
