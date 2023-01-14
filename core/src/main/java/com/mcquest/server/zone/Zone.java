package com.mcquest.server.zone;

public class Zone {
    private final int id;
    private final String name;
    private final int level;
    private final ZoneType type;

    public Zone(int id, String name, int level, ZoneType type) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public ZoneType getType() {
        return type;
    }
}
