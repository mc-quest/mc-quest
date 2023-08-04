package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.*;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Collider;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import team.unnamed.hephaestus.minestom.ModelEntity;

public class TrainingDummy extends NonPlayerCharacter {
    private static final Vec SIZE = new Vec(1, 2, 1);

    private final Mmorpg mmorpg;
    private final Collider hitbox;
    private ModelEntity entity;

    public TrainingDummy(Mmorpg mmorpg, Instance instance, Pos position) {
        super(instance, position);
        this.mmorpg = mmorpg;
        this.hitbox = new CharacterHitbox(this, instance, hitboxCenter(), SIZE);
        setName("Training Dummy");
        setMaxHealth(10);
        setHealth(getMaxHealth());
    }

    @Override
    protected void spawn() {
        super.spawn();

        entity = new ModelEntity(Models.TRAINING_DUMMY);
        mmorpg.getCharacterEntityManager().bind(entity, this);
        entity.setInstance(getInstance(), getPosition());

        mmorpg.getPhysicsManager().addCollider(hitbox);
    }

    @Override
    protected void despawn() {
        super.despawn();

        if (isAlive()) {
            mmorpg.getCharacterEntityManager().unbind(entity);
            hitbox.remove();
        }

        entity.remove();
    }

    @Override
    public Attitude getAttitude(Character other) {
        return Attitude.NEUTRAL;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    private Pos hitboxCenter() {
        return getPosition().withY(y -> y + SIZE.y() / 2.0);
    }

    @Override
    protected void onDamage(DamageSource source) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_ARMOR_STAND_HIT, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());

        entity.playAnimation("hit");
    }

    @Override
    protected void onDeath(DamageSource killer) {
        mmorpg.getCharacterEntityManager().unbind(entity);
        hitbox.remove();

        Sound sound = Sound.sound(SoundEvent.ENTITY_ARMOR_STAND_BREAK, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());

        entity.playAnimation("death");

        SchedulerManager scheduler = mmorpg.getSchedulerManager();

        scheduler.buildTask(this::remove).delay(TaskSchedule.millis(2000)).schedule();
        scheduler.buildTask(this::respawn).delay(TaskSchedule.seconds(30)).schedule();
    }

    private void respawn() {
        TrainingDummy trainingDummy = new TrainingDummy(mmorpg, getInstance(), getPosition());
        mmorpg.getObjectManager().add(trainingDummy);
    }
}
