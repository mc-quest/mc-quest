package com.mcquest.core.object;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.character.PlayerCharacterManager;
import com.mcquest.core.instance.Instance;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
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
    private final SetMultimap<SpatialHashCell, Object> objectsByCell;
    private final Set<Object> spawnedObjects;

    @ApiStatus.Internal
    public ObjectManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        objectsByCell = HashMultimap.create();
        spawnedObjects = new HashSet<>();

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tick).repeat(TaskSchedule.nextTick()).schedule();
    }

    public void add(Object object) {
        if (object.isRemoved()) {
            throw new IllegalArgumentException();
        }

        object.setObjectManager(this);

        Instance instance = object.getInstance();
        Pos position = object.getPosition();
        Vec boundingBox = object.getBoundingBox();

        Collection<SpatialHashCell> cells = cellsFor(instance, position, boundingBox);

        for (SpatialHashCell cell : cells) {
            objectsByCell.put(cell, object);
        }
    }

    public Collection<Object> getNearbyObjects(Instance instance, Pos position, double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException();
        }

        double diameter = 2.0 * radius;

        Collection<SpatialHashCell> cells =
                cellsFor(instance, position, new Vec(diameter, diameter, diameter));

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
        Set<Object> toSpawn = new HashSet<>();
        Set<Object> toNotDespawn = new HashSet<>();

        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        for (PlayerCharacter pc : pcManager.getPlayerCharacters()) {
            Instance instance = pc.getInstance();
            Pos pcPosition = pc.getPosition();

            Pos objectMin = pcPosition.sub(DESPAWN_RADIUS);
            Pos objectMax = pcPosition.add(DESPAWN_RADIUS);

            SpatialHashCell minCell = SpatialHashCell.cellAt(instance, objectMin, CELL_SIZE);
            SpatialHashCell maxCell = SpatialHashCell.cellAt(instance, objectMax, CELL_SIZE);

            SpatialHashCell.forAllInRange(minCell, maxCell, cell -> {
                for (Object object : objectsByCell.get(cell)) {
                    if (object.getPosition().distanceSquared(pcPosition) <= SPAWN_RADIUS * SPAWN_RADIUS
                            && !object.isSpawned()) {
                        toSpawn.add(object);
                    }

                    if (object.getPosition().distanceSquared(pcPosition) <= DESPAWN_RADIUS * DESPAWN_RADIUS
                            && object.isSpawned()) {
                        toNotDespawn.add(object);
                    }
                }
            });
        }

        for (Object object : spawnedObjects) {
            if (!toNotDespawn.contains(object)) {
                object.despawn();
            }
        }
        spawnedObjects.retainAll(toNotDespawn);

        for (Object object : toSpawn) {
            object.spawn();
        }
        spawnedObjects.addAll(toSpawn);
    }

    void updateInstance(Object object, Instance oldInstance, Instance newInstance) {
        update(object,
                oldInstance, newInstance,
                object.getPosition(), object.getPosition(),
                object.getBoundingBox(), object.getBoundingBox());
    }

    void updatePosition(Object object, Pos oldPosition, Pos newPosition) {
        update(object,
                object.getInstance(), object.getInstance(),
                oldPosition, newPosition,
                object.getBoundingBox(), object.getBoundingBox());
    }

    void updateBoundingBox(Object object, Vec oldBoundingBox, Vec newBoundingBox) {
        update(object,
                object.getInstance(), object.getInstance(),
                object.getPosition(), object.getPosition(),
                oldBoundingBox, newBoundingBox);
    }

    private void update(Object object,
                        Instance oldInstance, Instance newInstance,
                        Pos oldPosition, Pos newPosition,
                        Vec oldBoundingBox, Vec newBoundingBox) {
        Collection<SpatialHashCell> oldCells = cellsFor(oldInstance, oldPosition, oldBoundingBox);
        Collection<SpatialHashCell> newCells = cellsFor(newInstance, newPosition, newBoundingBox);

        for (SpatialHashCell cell : oldCells) {
            objectsByCell.remove(cell, object);
        }

        for (SpatialHashCell cell : newCells) {
            objectsByCell.put(cell, object);
        }
    }

    void remove(Object object) {
        Instance instance = object.getInstance();
        Pos position = object.getPosition();
        Vec boundingBox = object.getBoundingBox();

        Collection<SpatialHashCell> cells = cellsFor(instance, position, boundingBox);

        for (SpatialHashCell cell : cells) {
            objectsByCell.remove(cell, object);
        }

        spawnedObjects.remove(object);
    }

    private Collection<SpatialHashCell> cellsFor(Instance instance, Pos position,
                                                 Vec boundingBox) {
        Vec halfBoundingBox = boundingBox.mul(0.5);

        Pos min = position.sub(halfBoundingBox);
        Pos max = position.add(halfBoundingBox);

        SpatialHashCell minCell = SpatialHashCell.cellAt(instance, min, CELL_SIZE);
        SpatialHashCell maxCell = SpatialHashCell.cellAt(instance, max, CELL_SIZE);

        Collection<SpatialHashCell> cells = new ArrayList<>();
        SpatialHashCell.forAllInRange(minCell, maxCell, cells::add);

        return cells;
    }
}
