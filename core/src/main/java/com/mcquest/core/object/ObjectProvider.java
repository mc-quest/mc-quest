package com.mcquest.core.object;

import com.mcquest.core.Mmorpg;

@FunctionalInterface
public interface ObjectProvider {
    Object create(Mmorpg mmorpg, ObjectSpawner spawner);
}
