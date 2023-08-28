package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.entity.CharacterEntityManager;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Triggers;
import com.mcquest.server.constants.Models;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.ai.goal.DoNothingGoal;
import net.minestom.server.entity.ai.goal.FollowTargetGoal;
import net.minestom.server.entity.ai.goal.RandomLookAroundGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.time.Cooldown;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;

public class Spider extends DamageableEntityCharacter {
    private static final Vec SIZE = new Vec(1.5, 1.0, 1.5);

    private Cooldown attackCooldown;

    public Spider(Mmorpg mmorpg, Instance instance, Pos position) {
        super(mmorpg, instance, position, SIZE);
        setName("Spider");
        setLevel(4);
        setMaxHealth(20);
        setHealth(getMaxHealth());
        attackCooldown = new Cooldown(Duration.ofMillis(1500));
    }

    @Override
    public Attitude getAttitude(Character other) {
        if (other instanceof Spider) { // TODO other spiders
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
        mmorpg.getSchedulerManager().buildTask(this::respawn)
                .delay(TaskSchedule.seconds(45)).schedule();
    }

    private void respawn() {
        Spider spider = new Spider(mmorpg, getInstance(), spawnPosition);
        mmorpg.getObjectManager().add(spider);
    }

    @Override
    protected EntityCreature createEntity() {
        return new SpiderEntity();
    }

    private boolean shouldAttack(Character character) {
        return getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this);
    }

    private void attack() {
        attackCooldown.refreshLastUpdate(System.currentTimeMillis());
        ((SpiderEntity) entity).playAnimation("attack");
        mmorpg.getSchedulerManager().buildTask(() -> {
            if (!this.isAlive()) {
                return;
            }

            Pos position = getPosition();
            Pos hitboxCenter = position.add(position.direction().mul(1.5));
            Vec extents = new Vec(1.0, 0.5, 1.0);
            mmorpg.getPhysicsManager()
                    .overlapBox(getInstance(), hitboxCenter, extents)
                    .forEach(Triggers.character(this::attackHit));
        }).delay(Duration.ofMillis(250)).schedule();
    }

    private void attackHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 0.1);
        }
    }

    private class SpiderEntity extends ModelEntity {
        private SpiderEntity() {
            super(Models.WOLF_SPIDER);
            setBoundingBox(1.0, 1.0, 1.0);
            CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 10,
                            characterEntityManager.entityPredicate(Spider.this::shouldAttack)))
                    .addGoalSelector(new AttackGoal(this))
                    .addGoalSelector(new FollowTargetGoal(this, Duration.ofMillis(500)) {
                        @Override
                        public void start() {
                            super.start();
                            playAnimation("walk");
                        }

                        @Override
                        public boolean shouldEnd() {
                            return super.shouldEnd() || getDistance(getTarget()) > 10.0;
                        }

                        @Override
                        public void end() {
                            super.end();
                            Entity target = getTarget();
                            if (target != null && getDistance(target) > 10.0) {
                                setTarget(null);
                            }
                        }
                    })
                    .addGoalSelector(new RandomLookAroundGoal(this, 5) {
                        @Override
                        public void start() {
                            super.start();
                            playAnimation("idle");
                        }
                    })
                    .addGoalSelector(new RandomStrollGoal(this, 10) {
                        @Override
                        public void start() {
                            super.start();
                            playAnimation("walk");
                        }
                    })
                    .build());
        }

        @Override
        public void update(long time) {
            super.update(time);
            Spider.this.updatePosition(getPosition());
        }
    }

    private class AttackGoal extends GoalSelector {
        private final SpiderEntity entity;

        private AttackGoal(SpiderEntity entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public boolean shouldStart() {
            Entity target = entity.getTarget();
            if (target == null) {
                return false;
            }

            return target.getDistance(entity) <= 2.0;
        }

        @Override
        public void start() {
            entity.lookAt(entity.getTarget());
            Spider.this.attack();
        }

        @Override
        public void tick(long time) {
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
