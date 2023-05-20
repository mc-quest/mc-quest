package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Pool {
    private final Supplier<Integer> rolls;
    private final Predicate<PlayerCharacter> condition;
    private final Collection<PoolEntry> entries;

    private Pool(Builder builder) {
        rolls = builder.rolls;
        condition = builder.condition;
        entries = builder.entries;
    }

    public Supplier<Integer> getRolls() {
        return rolls;
    }

    public Predicate<PlayerCharacter> getCondition() {
        return condition;
    }

    public Collection<PoolEntry> getEntries() {
        return Collections.unmodifiableCollection(entries);
    }

    Collection<Loot> generate(PlayerCharacter pc) {
        if (!condition.test(pc)) {
            return Collections.emptyList();
        }

        Collection<Loot> loot = new ArrayList<>();
        int rolls = this.rolls.get();
        double totalWeight = entries.stream()
                .filter(entry -> entry.getCondition().test(pc))
                .mapToDouble(entry -> entry.getWeight())
                .sum();

        for (int i = 0; i < rolls; i++) {
            double dice = Math.random() * totalWeight;
            for (PoolEntry entry : entries) {
                if (!entry.getCondition().test(pc)) {
                    continue;
                }

                if (dice < entry.getWeight()) {
                    loot.add(entry.generate());
                    break;
                } else {
                    dice -= entry.getWeight();
                }
            }
        }

        return loot;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Supplier<Integer> rolls;
        private Predicate<PlayerCharacter> condition;
        private final Collection<PoolEntry> entries;

        public Builder() {
            rolls = () -> 1;
            condition = pc -> true;
            entries = new ArrayList<>();
        }

        public Builder rolls(Supplier<Integer> rolls) {
            this.rolls = rolls;
            return this;
        }

        public Builder rolls(int rolls) {
            return rolls(() -> rolls);
        }

        public Builder condition(Predicate<PlayerCharacter> condition) {
            this.condition = condition;
            return this;
        }

        public Builder entry(PoolEntry entry) {
            entries.add(entry);
            return this;
        }

        public Pool build() {
            return new Pool(this);
        }
    }
}
