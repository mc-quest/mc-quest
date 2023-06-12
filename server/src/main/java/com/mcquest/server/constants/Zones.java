package com.mcquest.server.constants;

import com.mcquest.server.zone.Zone;
import com.mcquest.server.zone.ZoneType;

public class Zones {
    public static final Zone OAKSHIRE = new Zone(1, "Oakshire", 1, ZoneType.SETTLEMENT);
    public static final Zone BULSKAN_RUINS = new Zone(2, "Bulskan Ruins", 5, ZoneType.DUNGEON);

    public static Zone[] all() {
        return new Zone[]{
                OAKSHIRE,
                BULSKAN_RUINS
        };
    }
}
