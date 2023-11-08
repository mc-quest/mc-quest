package net.mcquest.server.constants;

import net.mcquest.core.zone.Zone;
import net.mcquest.core.zone.ZoneType;

public class Zones {
    public static final Zone OAKSHIRE = new Zone("oakshire", "Oakshire", 1, ZoneType.SETTLEMENT);
    public static final Zone ELADRADOR = new Zone("eladrador", "Eladrador", 1, ZoneType.WILDERNESS);
    public static final Zone PACKLORD_DEN = new Zone("packlord_den", "Packlord's Den", 2, ZoneType.DUNGEON);
    public static final Zone BROODMOTHER_LAIR = new Zone("broodmother_lair", "Broodmother's Lair", 5, ZoneType.DUNGEON);

    public static Zone[] all() {
        return new Zone[]{
                OAKSHIRE,
                PACKLORD_DEN,
                BROODMOTHER_LAIR
        };
    }
}
