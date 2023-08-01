package com.mcquest.core.character;

import net.minestom.server.coordinate.Vec;

public interface Displaceable {
    /**
     * @param impulse the impulse in kg m/s
     */
    void applyImpulse(Vec impulse);
}
