package com.mcquest.server.character;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.HashSet;
import java.util.Set;

public class NonPlayerCharacter extends Character {
    private static Set<NonPlayerCharacter> aliveNpcs = new HashSet<>();

    private boolean isSpawned;

    public NonPlayerCharacter(Component displayName, int level,
                              Instance instance, Pos position) {
        super(displayName, level, instance, position);
        isSpawned = false;
        aliveNpcs.add(this);
    }

    @ApiStatus.Internal
    public static void startSpawner() {
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        schedulerManager.submitTask(() -> {
            tickSpawner();
            return TaskSchedule.nextTick();
        });
    }

    private static void tickSpawner() {
        Set<NonPlayerCharacter> toSpawn = new HashSet<>();
        Set<NonPlayerCharacter> toDespawn = new HashSet<>();

        for (NonPlayerCharacter npc : aliveNpcs) {
            if (npc.isSpawned) {
                if (npc.shouldDespawn()) {
                    toDespawn.add(npc);
                }
            } else {
                if (npc.shouldSpawn()) {
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

    @Override
    @MustBeInvokedByOverriders
    public void setAlive(boolean alive) {
        super.setAlive(alive);
        if (alive) {
            aliveNpcs.add(this);
        } else {
            aliveNpcs.remove(this);
        }
    }

    public final boolean isSpawned() {
        return isSpawned;
    }

    @MustBeInvokedByOverriders
    protected void spawn() {
        isSpawned = true;
    }

    @MustBeInvokedByOverriders
    protected void despawn() {
        isSpawned = false;
    }

    protected boolean shouldSpawn() {
        // return PlayerCharacter.isNearby(getInstance(), getPosition(), 50);
        // TODO
        return true;
    }

    protected boolean shouldDespawn() {
        // return !PlayerCharacter.isNearby(getInstance(), getPosition(), 60);
        // TODO
        return false;
    }
}
