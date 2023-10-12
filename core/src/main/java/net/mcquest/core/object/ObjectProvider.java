package net.mcquest.core.object;

import net.mcquest.core.Mmorpg;

@FunctionalInterface
public interface ObjectProvider {
    Object create(Mmorpg mmorpg, ObjectSpawner spawner);
}
