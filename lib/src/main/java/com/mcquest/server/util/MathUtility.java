package com.mcquest.server.util;

import net.minestom.server.coordinate.Pos;

public class MathUtility {
    /**
     * Returns value clamped between min and max.
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static Pos min(Pos p1, Pos p2) {
        double x = Math.min(p1.x(), p2.x());
        double y = Math.min(p1.y(), p2.y());
        double z = Math.min(p1.z(), p2.z());
        return new Pos(x, y, z);
    }

    public static Pos max(Pos p1, Pos p2) {
        double x = Math.max(p1.x(), p2.x());
        double y = Math.max(p1.y(), p2.y());
        double z = Math.max(p1.z(), p2.z());
        return new Pos(x, y, z);
    }
}
