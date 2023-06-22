package com.mcquest.core.cinema;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Cutscene {
    private final Shot[] shots;

    private Cutscene(Builder builder) {
        shots = builder.shots.toArray(new Shot[0]);
    }

    public List<Shot> getShots() {
        return List.of(shots);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Shot> shots;

        private Builder() {
            shots = new ArrayList<>();
        }

        public Builder shot(@NotNull Shot shot) {
            shots.add(shot);
            return this;
        }

        public Cutscene build() {
            return new Cutscene(this);
        }
    }
}
