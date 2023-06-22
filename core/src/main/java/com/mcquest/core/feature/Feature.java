package com.mcquest.core.feature;

import com.mcquest.core.Mmorpg;

/**
 * Features are the units of interactivity in an Mmorpg. They should be used to
 * spawn NonPlayerCharacters, subscribe to EventEmitters, register listeners,
 * add Colliders, schedule tasks, etc.
 */
public interface Feature {
    void hook(Mmorpg mmorpg);
}
