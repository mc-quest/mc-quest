package com.mcquest.server.cinema;

import com.google.common.collect.Iterables;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Shot {
    private final KeyFrame[] keyFrames;

    private Shot(Builder builder) {
        keyFrames = builder.keyFrames.toArray(new KeyFrame[0]);
    }

    public List<KeyFrame> getKeyFrames() {
        return List.of(keyFrames);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<KeyFrame> keyFrames;

        private Builder() {
            this.keyFrames = new ArrayList<>();
        }

        public Builder keyFrame(@NotNull Pos position, @NotNull Duration time) {
            return keyFrame(new KeyFrame(position, time));
        }

        public Builder keyFrame(@NotNull KeyFrame keyFrame) {
            Duration time = keyFrame.getTime();
            if (keyFrames.isEmpty()) {
                if (!time.isZero()) {
                    throw new IllegalArgumentException();
                }
            } else {
                Duration prevTime = Iterables.getLast(keyFrames).getTime();
                if (time.compareTo(prevTime) < 0) {
                    throw new IllegalArgumentException();
                }
            }

            keyFrames.add(keyFrame);
            return this;
        }

        public Shot build() {
            return new Shot(this);
        }
    }
}
