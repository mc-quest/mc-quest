package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.commerce.Money;
import com.mcquest.server.util.MathUtility;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class MoneyPoolEntry extends PoolEntry {
    private final Supplier<Money> value;

    private MoneyPoolEntry(Builder builder) {
        super(builder.weight, builder.condition);
        value = builder.value;
    }

    public Supplier<Money> getValue() {
        return value;
    }

    @Override
    Loot generate() {
        return new MoneyLoot(this, value.get());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Supplier<Money> value;
        private double weight;
        private Predicate<PlayerCharacter> condition;

        Builder() {
            value = () -> new Money(1);
            weight = 1.0;
            condition = pc -> true;
        }

        public Builder value(Money value) {
            return value(() -> value);
        }

        public Builder value(Money min, Money max) {
            if (min.getValue() > max.getValue()) {
                throw new IllegalArgumentException();
            }
            return value(() -> new Money(MathUtility.randomRange(min.getValue(), max.getValue())));
        }

        private Builder value(Supplier<Money> value) {
            this.value = value;
            return this;
        }

        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder condition(Predicate<PlayerCharacter> condition) {
            this.condition = condition;
            return this;
        }

        public MoneyPoolEntry build() {
            return new MoneyPoolEntry(this);
        }
    }
}