package com.mcquest.server.api.util;

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
}
