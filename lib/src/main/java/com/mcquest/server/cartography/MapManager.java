package com.mcquest.server.cartography;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class MapManager {
    private final Map<Integer, AreaMap> mapsById;

    @ApiStatus.Internal
    public MapManager(AreaMap[] maps) {
        mapsById = new HashMap<>();
        for (AreaMap map : maps) {
            registerMap(map);
        }
    }

    private void registerMap(AreaMap map) {
        int id = map.getId();
        if (mapsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
    }

    public AreaMap getMap(int id) {
        return mapsById.get(id);
    }
}
