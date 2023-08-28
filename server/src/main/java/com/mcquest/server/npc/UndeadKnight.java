package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.entity.CharacterEntityManager;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Triggers;
import com.mcquest.core.util.Debug;
import com.mcquest.server.constants.Models;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.goal.FollowTargetGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.time.Cooldown;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;

public class UndeadKnight extends DamageableEntityCharacter {
    private static final Vec SIZE = new Vec(2.0, 5.0, 2.0);

    private final Cooldown attackCooldown;

    public UndeadKnight(Mmorpg mmorpg, Instance instance, Pos position) {
        super(mmorpg, instance, position, SIZE);
        setName("Undead Knight");
        setLevel(15);
        setMaxHealth(100);
        setHealth(getMaxHealth());
        attackCooldown = new Cooldown(Duration.ofMillis(2000));
    }

    @Override
    public Attitude getAttitude(Character other) {
        if (other instanceof UndeadKnight) { // TODO other spiders
            return Attitude.FRIENDLY;
        }

        return Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onDeath(DamageSource killer) {
        super.onDeath(killer);
        if (isSpawned()) {
            ((UndeadKnightEntity) entity).playAnimation("death");
        }
        mmorpg.getSchedulerManager().buildTask(this::respawn)
                .delay(TaskSchedule.seconds(45)).schedule();
    }

    private void respawn() {
        UndeadKnight undeadKnight = new UndeadKnight(mmorpg, getInstance(), spawnPosition);
        mmorpg.getObjectManager().add(undeadKnight);
    }

    @Override
    protected EntityCreature createEntity() {
        return new UndeadKnightEntity();
    }

    private boolean shouldAttack(Character character) {
        return getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this);
    }

    private void attack() {
        if (!isAlive()) {
            return;
        }

        entity.getNavigator().setPathTo(null);

        Entity target = entity.getTarget();
        entity.lookAt(target);

        Pos position = getPosition();
        Vec right = position.direction().withY(0.0).rotateAroundY(-Math.PI / 2.0);
        Vec targetOffset = target.getPosition().sub(position).asVec().withY(0.0);
        if (right.dot(targetOffset) >= 0.0) {
            // Target is to the right.
            swordAttack();
        } else {
            // Target is to the left.
            shieldAttack();
        }

        attackCooldown.refreshLastUpdate(System.currentTimeMillis());
    }

    private void swordAttack() {
        ((UndeadKnightEntity) entity).playAnimation("attack_sword");
        mmorpg.getSchedulerManager().buildTask(() -> {
            if (!(isAlive() && isSpawned())) {
                return;
            }

            Pos position = getPosition();
            Vec direction = position.direction().withY(0.0).normalize();
            Pos hitboxCenter = position
                    .withY(y -> y + 1.5)
                    .add(direction.rotateAroundY(-Math.PI / 2.0).mul(1.0))
                    .add(direction.mul(2.5));
            Vec extents = new Vec(2.5, 3.0, 2.5);
            mmorpg.getPhysicsManager()
                    .overlapBox(getInstance(), hitboxCenter, extents)
                    .forEach(Triggers.character(this::swordHit));
        }).delay(Duration.ofMillis(850)).schedule();
    }

    private void swordHit(Character other) {
        if (shouldAttack(other)) {
            other.damage(this, 0.1);
            other.applyImpulse(getPosition().direction().withY(0.3).mul(500.0));
        }
    }

    private void shieldAttack() {
        ((UndeadKnightEntity) entity).playAnimation("attack_shield");
        mmorpg.getSchedulerManager().buildTask(() -> {
            if (!(isAlive() && isSpawned())) {
                return;
            }

            Pos position = getPosition();
            Vec direction = position.direction().withY(0.0).normalize();
            Pos hitboxCenter = position
                    .withY(y -> y + 1.5)
                    .add(direction.rotateAroundY(Math.PI / 2.0).mul(1.0))
                    .add(direction.mul(2.0));
            Vec extents = new Vec(2.5, 3.0, 2.5);
            mmorpg.getPhysicsManager()
                    .overlapBox(getInstance(), hitboxCenter, extents)
                    .forEach(Triggers.character(this::shieldHit));
        }).delay(Duration.ofMillis(850)).schedule();
    }

    private void shieldHit(Character other) {
        if (shouldAttack(other)) {
            other.damage(this, 0.1);
            other.applyImpulse(getPosition().direction().withY(0.3).mul(2500.0));
        }
    }

    private class UndeadKnightEntity extends ModelEntity {
        private UndeadKnightEntity() {
            super(Models.UNDEAD_KNIGHT);
            setBoundingBox(SIZE.x(), SIZE.y(), SIZE.z());
            getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3f);
            setRemovalAnimationDelay(2500);
            CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 10,
                            characterEntityManager.entityPredicate(UndeadKnight.this::shouldAttack)))
                    .addGoalSelector(new AttackGoal(this))
                    .addGoalSelector(new FollowTargetGoal(this, Duration.ofMillis(500)))
                    .addGoalSelector(new RandomStrollGoal(this, 10))
                    .build());
        }

        @Override
        public void update(long time) {
            super.update(time);
            UndeadKnight.this.updatePosition(getPosition());
        }
    }

    private class AttackGoal extends GoalSelector {
        private long startTime;

        private AttackGoal(UndeadKnightEntity entity) {
            super(entity);
            startTime = 0;
        }

        @Override
        public boolean shouldStart() {
            Entity target = entityCreature.getTarget();
            if (target == null) {
                return false;
            }

            return target.getDistance(entityCreature) <= 4.0;
        }

        @Override
        public void start() {
            startTime = System.currentTimeMillis();
            UndeadKnight.this.attack();
        }

        @Override
        public void tick(long time) {
            if (UndeadKnight.this.isAlive() && time < startTime + 500) {
                entityCreature.lookAt(entityCreature.getTarget());
            }
        }

        @Override
        public boolean shouldEnd() {
            return attackCooldown.isReady(System.currentTimeMillis());
        }

        @Override
        public void end() {
        }
    }
}
