package com.mcquest.server.zone;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class ZoneManager {
    private final Map<Integer, Zone> zonesById;

    @ApiStatus.Internal
    public ZoneManager(Zone[] zones) {
        zonesById = new HashMap<>();
        for (Zone zone : zones) {
            registerZone(zone);
        }
    }

    private void registerZone(Zone zone) {
        int id = zone.getId();
        if (zonesById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        zonesById.put(id, zone);
    }

    public Zone getZone(int id) {
        return zonesById.get(id);
    }
}
