package com.mcquest.server.physics;

import net.minestom.server.instance.Instance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ColliderTest {
    @Test
    public void testBasicCollision() {
        PhysicsManager physicsManager = new PhysicsManager();
        Collider c1 = new Collider();
        physicsManager.addCollider(collider);
    }
}
