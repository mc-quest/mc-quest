package com.mcquest.core.character;

import net.minestom.server.entity.EntityCreature;
import team.unnamed.hephaestus.minestomce.ModelEntity;

public interface CharacterAnimation {
    void play(EntityCreature entity);

    static CharacterAnimation swingMainHand() {
        return EntityCreature::swingMainHand;
    }

    static CharacterAnimation swingOffHand() {
        return EntityCreature::swingOffHand;
    }

    static CharacterAnimation named(String name) {
        return entity -> {
            if (!(entity instanceof ModelEntity modelEntity)) {
                return;
            }

            if (!modelEntity.model().animations().containsKey(name)) {
                return;
            }

            modelEntity.playAnimation(name);
        };
    }

    static CharacterAnimation clear() {
        return entity -> {
            if (!(entity instanceof ModelEntity modelEntity)) {
                return;
            }

            modelEntity.animationController().clearQueue();
        };
    }
}
