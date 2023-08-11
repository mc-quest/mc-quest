package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;

public abstract class EntityCharacter extends NonPlayerCharacter {
    protected final Mmorpg mmorpg;
    protected final Pos spawnPosition;
    protected EntityCreature entity;
    private Task unred;

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
    @MustBeInvokedByOverriders
    protected void onDamage(DamageSource source) {
        super.onDamage(source);

        if (isSpawned()) {
            if (entity instanceof ModelEntity modelEntity) {
                red(modelEntity);
            } else {
                entity.damage(DamageType.VOID, 0.0f);
            }
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void onDeath(DamageSource source) {
        if (isSpawned()) {
            mmorpg.getCharacterEntityManager().unbind(entity);

            if (entity instanceof ModelEntity modelEntity) {
                red(modelEntity);
            }
            entity.kill();

            mmorpg.getSchedulerManager().buildTask(this::remove)
                    .delay(Duration.ofMillis(entity.getRemovalAnimationDelay()))
                    .schedule();
        }
    }

    @MustBeInvokedByOverriders
    protected void updatePosition(Pos position) {
        if (isSpawned()) {
            super.setPosition(position);
        }
    }

    private void red(ModelEntity modelEntity) {
        modelEntity.colorize(0xff7d7d);
        if (unred != null) {
            unred.cancel();
        }
        // Don't need to check if character is despawned because modelEntity is in closure.
        unred = mmorpg.getSchedulerManager().buildTask(modelEntity::colorizeDefault)
                .delay(TaskSchedule.millis(200))
                .schedule();
    }

    protected abstract EntityCreature createEntity();
}
