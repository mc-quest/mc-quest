package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.instance.Instance;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import team.unnamed.hephaestus.minestom.ModelEntity;

public class TrainingDummy extends DamageableEntityCharacter {
    private static final Vec SIZE = new Vec(1, 2, 1);

    public TrainingDummy(Mmorpg mmorpg, Instance instance, Pos position) {
        super(mmorpg, instance, position, SIZE);
        setName("Training Dummy");
        setMaxHealth(10);
        setHealth(getMaxHealth());
    }

    @Override
    public Attitude getAttitude(Character other) {
        return Attitude.NEUTRAL;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onDamage(DamageSource source) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_ARMOR_STAND_HIT, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());
        ((ModelEntity) entity).playAnimation("hit");
    }

    @Override
    protected void onDeath(DamageSource killer) {
        super.onDeath(killer);

        Sound sound = Sound.sound(SoundEvent.ENTITY_ARMOR_STAND_BREAK, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());

        ((ModelEntity) entity).playAnimation("death");

        mmorpg.getSchedulerManager().buildTask(this::respawn)
                .delay(TaskSchedule.seconds(5)).schedule();
    }

    @Override
    protected EntityCreature createEntity() {
        ModelEntity entity = new ModelEntity(Models.TRAINING_DUMMY);
        entity.setBoundingBox(SIZE.x(), SIZE.y(), SIZE.z());
        entity.setRemovalAnimationDelay(1500);
        return entity;
    }

    private void respawn() {
        TrainingDummy trainingDummy = new TrainingDummy(mmorpg, getInstance(), getPosition());
        mmorpg.getObjectManager().add(trainingDummy);
    }
}
