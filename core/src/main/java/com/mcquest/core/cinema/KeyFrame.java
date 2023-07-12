package com.mcquest.core.cinema;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class KeyFrame {
    private final Pos position;
    private final Duration time;
    private final Interpolation interpolation;

    public KeyFrame(@NotNull Pos position, @NotNull Duration time,
                    @NotNull Interpolation interpolation) {
        this.position = position;
        this.time = time;
        this.interpolation = interpolation;
    }

    public Pos getPosition() {
        return position;
    }

    public Duration getTime() {
        return time;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }
}
