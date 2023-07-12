package com.mcquest.core.cinema;

import com.google.common.collect.Iterables;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Shot {
    private final Pos startPosition;
    private final KeyFrame[] keyFrames;

    private Shot(Builder builder) {
        startPosition = builder.startPosition;
        keyFrames = builder.keyFrames.toArray(new KeyFrame[0]);
    }

    public Pos getStartPosition() {
        return startPosition;
    }

    public List<KeyFrame> getKeyFrames() {
        return List.of(keyFrames);
    }

    public static Builder builder(Pos startPosition) {
        return new Builder(startPosition);
    }

    public static class Builder {
        private final Pos startPosition;
        private final List<KeyFrame> keyFrames;

        private Builder(Pos startPosition) {
            this.startPosition = startPosition;
            this.keyFrames = new ArrayList<>();
        }

        public Builder keyFrame(@NotNull Pos position, @NotNull Duration time,
                                @NotNull Interpolation interpolation) {
            return keyFrame(new KeyFrame(position, time, interpolation));
        }

        public Builder keyFrame(@NotNull KeyFrame keyFrame) {
            Duration prevTime = keyFrames.isEmpty()
                    ? Duration.ZERO
                    : Iterables.getLast(keyFrames).getTime();

            if (keyFrame.getTime().compareTo(prevTime) <= 0) {
                throw new IllegalArgumentException();
            }

            keyFrames.add(keyFrame);
            return this;
        }

        public Shot build() {
            return new Shot(this);
        }
    }
}
