package net.mcquest.core.loot;

import net.mcquest.core.commerce.Money;
import net.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;

public class MoneyLoot extends Loot {
    private final Money value;

    MoneyLoot(MoneyPoolEntry entry, Money value) {
        super(entry);
        this.value = value;
    }

    @Override
    public MoneyPoolEntry getEntry() {
        return (MoneyPoolEntry) super.getEntry();
    }

    public Money getValue() {
        return value;
    }

    @Override
    public void drop(Instance instance, Pos position) {
        value.drop(instance, position);
    }
}
