package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.time.Duration;

public abstract class EntityCharacter extends NonPlayerCharacter {
    protected final Mmorpg mmorpg;
    protected final Pos spawnPosition;
    protected EntityCreature entity;

    public EntityCharacter(Mmorpg mmorpg, Instance instance, Pos spawnPosition, Vec boundingBox) {
        super(instance, spawnPosition, boundingBox);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
    }

    @Override
    protected void spawn() {
        super.spawn();

        entity = createEntity();
        mmorpg.getCharacterEntityManager().bind(entity, this);
        entity.setInstance(getInstance(), getPosition());
    }

    @Override
    protected void despawn() {
        super.despawn();

        if (isAlive()) {
            mmorpg.getCharacterEntityManager().unbind(entity);
            entity.remove();
        }
        entity = null;

        setPosition(spawnPosition);
    }

    @Override
    protected void onDeath(DamageSource source) {
        mmorpg.getCharacterEntityManager().unbind(entity);
        entity.kill();
        mmorpg.getSchedulerManager().buildTask(this::remove)
                .delay(Duration.ofMillis(entity.getRemovalAnimationDelay()))
                .schedule();
    }
    
    @MustBeInvokedByOverriders
    protected void updatePosition(Pos position) {
        if (isSpawned()) {
            super.setPosition(position);
        }
    }

    protected abstract EntityCreature createEntity();
}
