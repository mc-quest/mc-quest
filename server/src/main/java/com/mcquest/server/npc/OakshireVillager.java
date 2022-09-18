package com.mcquest.server.npc;

import com.mcquest.server.character.NonPlayerCharacter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;

public class OakshireVillager extends NonPlayerCharacter {
    // TODO
    public OakshireVillager(Instance instance, Pos position) {
        super(null, 0, null, null);
    }

    public static class Entity extends EntityCreature {
        private Entity() {
            super(EntityType.PLAYER);
        }
    }
}
