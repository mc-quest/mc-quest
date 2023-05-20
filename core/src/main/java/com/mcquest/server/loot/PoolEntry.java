package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;

import java.util.function.Predicate;

public abstract class PoolEntry {
    private final double weight;
    private final Predicate<PlayerCharacter> condition;

    PoolEntry(double weight, Predicate<PlayerCharacter> condition) {
        this.weight = weight;
        this.condition = condition;
    }

    public double getWeight() {
        return weight;
    }

    public Predicate<PlayerCharacter> getCondition() {
        return condition;
    }

    abstract Loot generate();
}
