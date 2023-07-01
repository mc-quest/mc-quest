package com.mcquest.core.cartography;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

public class MapManager {
    private final java.util.Map<Integer, Map> mapsById;

    @ApiStatus.Internal
    public MapManager(Map[] maps) {
        mapsById = new HashMap<>();
        for (Map map : maps) {
            registerMap(map);
        }
    }

    private void registerMap(Map map) {
        int id = map.getId();
        if (mapsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        mapsById.put(id, map);
    }

    public Map getMap(int id) {
        return mapsById.get(id);
    }
}
