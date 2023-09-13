package com.mcquest.core.object;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.instance.Instance;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ObjectManager {
    private static final double CELL_SIZE = 256.0;
    public static final double SPAWN_RADIUS = 75.0;
    public static final double DESPAWN_RADIUS = 85.0;

    private final Mmorpg mmorpg;
    private final SetMultimap<SpatialHashCell, ObjectSpawner> spawnersByCell;
    private final SetMultimap<SpatialHashCell, Object> objectsByCell;

    @ApiStatus.Internal
    public ObjectManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        spawnersByCell = HashMultimap.create();
        objectsByCell = HashMultimap.create();

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tick).repeat(TaskSchedule.nextTick()).schedule();
    }

    public void add(ObjectSpawner spawner) {
        if (spawner.isRemoved()) {
            throw new IllegalArgumentException();
        }

        SpatialHashCell cell = cellFor(spawner.getInstance(), spawner.getPosition());
        spawnersByCell.put(cell, spawner);
        spawner.setObjectManager(this);
    }

    public Object spawn(ObjectSpawner spawner) {
        SpatialHashCell cell = cellFor(spawner.getInstance(), spawner.getPosition());
        Object object = spawner.spawn(mmorpg);
        objectsByCell.put(cell, object);
        object.spawn();
        return object;
    }

    public Collection<Object> getNearbyObjects(Instance instance, Pos position, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException();
        }

        Collection<SpatialHashCell> cells = cellsFor(instance, position, radius);

        Set<Object> objects = new HashSet<>();

        for (SpatialHashCell cell : cells) {
            for (Object object : objectsByCell.get(cell)) {
                if (object.getPosition().distanceSquared(position) <= radius * radius) {
                    objects.add(object);
                }
            }
        }

        return objects;
    }

    private void tick() {
        SetMultimap<SpatialHashCell, ObjectSpawner> toSpawn = HashMultimap.create();
        Set<Object> toNotRemove = new HashSet<>();

        for (Instance instance : mmorpg.getInstanceManager().getInstances()) {
            for (Player player : instance.getPlayers()) {
                Pos playerPosition = player.getPosition();

                Pos objectMin = playerPosition.sub(DESPAWN_RADIUS);
                Pos objectMax = playerPosition.add(DESPAWN_RADIUS);

                SpatialHashCell minCell = cellFor(instance, objectMin);
                SpatialHashCell maxCell = cellFor(instance, objectMax);

                SpatialHashCell.forAllInRange(minCell, maxCell, cell -> {
                    for (ObjectSpawner spawner : spawnersByCell.get(cell)) {
                        if (!spawner.isSpawned() && spawner.isActive()
                                && spawner.getPosition().distanceSquared(playerPosition)
                                <= SPAWN_RADIUS * SPAWN_RADIUS) {
                            toSpawn.put(cell, spawner);
                        }
                    }

                    for (Object object : objectsByCell.get(cell)) {
                        if (object.getPosition().distanceSquared(playerPosition)
                                <= DESPAWN_RADIUS * DESPAWN_RADIUS) {
                            toNotRemove.add(object);
                        }
                    }
                });
            }
        }

        Set<Object> toRemove = new HashSet<>();
        for (Object object : objectsByCell.values()) {
            if (!toNotRemove.contains(object) && !(object instanceof PlayerCharacter)) {
                toRemove.add(object);
            }
        }

        for (Object object : toRemove) {
            object.remove();
        }

        toSpawn.forEach((cell, spawner) -> {
            Object object = spawner.spawn(mmorpg);
            objectsByCell.put(cell, object);
            object.spawn();
        });
    }

    void updateInstance(Object object, Instance oldInstance, Pos oldPosition,
                        Instance newInstance, Pos newPosition) {
        update(object,
                oldInstance, newInstance,
                oldPosition, newPosition);
    }

    void updatePosition(Object object, Pos oldPosition, Pos newPosition) {
        update(object,
                object.getInstance(), object.getInstance(),
                oldPosition, newPosition);
    }

    private void update(Object object,
                        Instance oldInstance, Instance newInstance,
                        Pos oldPosition, Pos newPosition) {
        SpatialHashCell oldCell = cellFor(oldInstance, oldPosition);
        SpatialHashCell newCell = cellFor(newInstance, newPosition);

        objectsByCell.remove(oldCell, object);
        objectsByCell.put(newCell, object);
    }

    void removeFromHash(ObjectSpawner spawner) {
        SpatialHashCell cell = cellFor(spawner.getInstance(), spawner.getPosition());
        spawnersByCell.remove(cell, spawner);
    }

    void removeFromHash(Object object) {
        SpatialHashCell cell = cellFor(object.getInstance(), object.getPosition());
        objectsByCell.remove(cell, object);
    }

    private SpatialHashCell cellFor(Instance instance, Pos position) {
        return SpatialHashCell.cellAt(instance, position, CELL_SIZE);
    }

    private Collection<SpatialHashCell> cellsFor(Instance instance, Pos position, double radius) {
        Pos min = position.sub(radius, radius, radius);
        Pos max = position.add(radius, radius, radius);

        SpatialHashCell minCell = SpatialHashCell.cellAt(instance, min, CELL_SIZE);
        SpatialHashCell maxCell = SpatialHashCell.cellAt(instance, max, CELL_SIZE);

        Collection<SpatialHashCell> cells = new ArrayList<>();
        SpatialHashCell.forAllInRange(minCell, maxCell, cells::add);

        return cells;
    }
}
