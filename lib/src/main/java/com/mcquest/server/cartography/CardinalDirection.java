package com.mcquest.server.cartography;

import net.minestom.server.coordinate.Vec;

public enum CardinalDirection {
    NORTH("N"), NORTHEAST("NE"), EAST("E"), SOUTHEAST("SE"),
    SOUTH("S"), SOUTHWEST("SW"), WEST("W"), NORTHWEST("NW");

    private final String abbreviation;

    CardinalDirection(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static CardinalDirection fromDirection(double x, double z) {
        double angleFromNorth = Math.atan2(z, x) + Math.PI / 2.0;
        if (angleFromNorth < 0.0) {
            angleFromNorth = 2.0 * Math.PI + angleFromNorth;
        }
        int octant = (int) Math.round(8.0 * angleFromNorth / (2.0 * Math.PI)) % 8;
        return values()[octant];
    }

    public static CardinalDirection fromDirection(Vec direction) {
        return fromDirection(direction.x(), direction.z());
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
