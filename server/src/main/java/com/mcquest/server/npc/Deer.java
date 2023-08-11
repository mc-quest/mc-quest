package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.instance.Instance;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import team.unnamed.hephaestus.minestom.ModelEntity;

public class Deer extends DamageableEntityCharacter {
    private static final Vec SIZE = new Vec(1, 1.25, 1);

    public Deer(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(mmorpg, instance, spawnPosition, SIZE);
        setBoundingBox(SIZE);
        setName("Deer");
        setMaxHealth(10);
        setHealth(getMaxHealth());
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onDamage(DamageSource source) {
        super.onDamage(source);

        Sound sound = Sound.sound(SoundEvent.ENTITY_DONKEY_HURT, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());
    }

    @Override
    protected void onDeath(DamageSource killer) {
        super.onDeath(killer);

        Sound sound = Sound.sound(SoundEvent.ENTITY_DONKEY_DEATH, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());

        mmorpg.getSchedulerManager().buildTask(this::respawn)
                .delay(TaskSchedule.seconds(3)).schedule();
    }

    private void respawn() {
        Deer deer = new Deer(mmorpg, getInstance(), spawnPosition);
        mmorpg.getObjectManager().add(deer);
    }

    @Override
    protected EntityCreature createEntity() {
        return new DeerEntity();
    }

    private class DeerEntity extends ModelEntity {
        private DeerEntity() {
            super(Models.DEER);
            playAnimation("walk");
            setBoundingBox(SIZE.x(), SIZE.y(), SIZE.z());
            getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.05f);
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 10))
                    .build());
        }

        @Override
        public void update(long time) {
            super.update(time);
            Deer.this.updatePosition(getPosition());
        }
    }
}
