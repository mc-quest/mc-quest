package com.mcquest.core.loot;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.Item;
import com.mcquest.core.util.MathUtility;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemPoolEntry extends PoolEntry {
    private final Item item;
    private final Supplier<Integer> amount;

    private ItemPoolEntry(Builder builder) {
        super(builder.weight, builder.condition);
        item = builder.item;
        amount = builder.amount;
    }

    public Item getItem() {
        return item;
    }

    public Supplier<Integer> getAmount() {
        return amount;
    }

    @Override
    Loot generate() {
        return new ItemLoot(this, amount.get());
    }

    public static Builder builder(Item item) {
        return new Builder(item);
    }

    public static class Builder {
        private final Item item;
        private Supplier<Integer> amount;
        private double weight;
        private Predicate<PlayerCharacter> condition;

        Builder(Item item) {
            this.item = item;
            amount = () -> 1;
            weight = 1.0;
            condition = pc -> true;
        }

        public Builder amount(int amount) {
            if (amount < 0 || amount > item.getStackSize()) {
                throw new IllegalArgumentException();
            }
            return amount(() -> amount);
        }

        public Builder amount(int min, int max) {
            if (min < 0 || min > max || max > item.getStackSize()) {
                throw new IllegalArgumentException();
            }
            return amount(() -> MathUtility.randomRange(min, max));
        }

        private Builder amount(Supplier<Integer> amount) {
            this.amount = amount;
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

        public ItemPoolEntry build() {
            return new ItemPoolEntry(this);
        }
    }
}
