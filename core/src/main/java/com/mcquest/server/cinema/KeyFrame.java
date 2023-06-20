package com.mcquest.server.cinema;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class KeyFrame {
    private final Pos position;
    private final Duration time;

    public KeyFrame(@NotNull Pos position, @NotNull Duration time) {
        this.position = position;
        this.time = time;
    }

    public Pos getPosition() {
        return position;
    }

    public Duration getTime() {
        return time;
    }
}
