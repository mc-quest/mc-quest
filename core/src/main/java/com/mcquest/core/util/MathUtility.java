package com.mcquest.core.util;

import net.minestom.server.coordinate.Pos;

import java.util.Random;

public class MathUtility {
    private static final Random random = new Random();

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
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

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static float lerpAngle(float a, float b, double t) {
        float difference = (b - a) % 180.0f;
        float shortDistance = (2.0f * difference) % 180.0f - difference;
        return (float) (a + t * shortDistance);
    }

    public static Pos lerp(Pos a, Pos b, double t) {
        double x = lerp(a.x(), b.x(), t);
        double y = lerp(a.y(), b.y(), t);
        double z = lerp(a.z(), b.z(), t);
        float yaw = lerpAngle(a.yaw(), b.yaw(), t);
        float pitch = lerpAngle(a.pitch(), b.pitch(), t);
        return new Pos(x, y, z, yaw, pitch);
    }

    public static int randomRange(int min, int max) {
        return random.nextInt(min, max + 1);
    }

    public static double randomRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
