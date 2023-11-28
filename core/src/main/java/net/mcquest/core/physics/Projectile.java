package net.mcquest.core.physics;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.object.Object;
import net.mcquest.core.object.ObjectSpawner;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestom.ModelEntity;

public abstract class Projectile extends Object {
    protected Mmorpg mmorpg;
    protected Collider hitbox;
    protected Entity projectileEntity;
    protected EntityType entityType;
    protected Model model;
    protected PlayerCharacter shooter; // TODO: should be any entity?
    protected Vec velocity;
    protected double distance;
    protected Vec hitboxSize;

    // TODO: more elegant way of handling the extra arguments required that by design forces these values before a spawn
    // TODO: additional args? fewer args? more customizablity somewhere else?
    public Projectile(Mmorpg mmorpg, ObjectSpawner spawner, EntityType entityType, PlayerCharacter shooter, Vec velocity, double distance, Vec hitboxSize) {
        super(mmorpg, spawner);
        this.mmorpg = mmorpg;
        this.entityType = entityType;
        this.shooter = shooter;
        this.velocity = velocity;
        this.distance = distance;
        this.hitboxSize = hitboxSize;
    }

    public Projectile(Mmorpg mmorpg, ObjectSpawner spawner, Model model, PlayerCharacter shooter, Vec velocity, double distance, Vec hitboxSize) {
        super(mmorpg, spawner);
        this.mmorpg = mmorpg;
        this.model = model;
        this.shooter = shooter;
        this.velocity = velocity;
        this.distance = distance;
        this.hitboxSize = hitboxSize;
    }

    /**
     * implement on collider hit behaviour
     */
    protected abstract void onHit(Character hitCharacter);

    /**
     * implement on block stuck behaviour
     */
    protected abstract void onStuck();

    @Override
    protected void spawn() {
        Pos startPosition = shooter.getWeaponPosition().add(shooter.getLookDirection().mul(1f));
        if (entityType != null) {
            this.projectileEntity = new ProjectileEntity(entityType, startPosition, distance);
        } else {
            this.projectileEntity = new ProjectileEntityModel(model, startPosition, distance);
        }
        projectileEntity.setNoGravity(true);
        projectileEntity.setVelocity(velocity);
        projectileEntity.setInstance(shooter.getInstance(), startPosition);

        hitbox = new Collider(shooter.getInstance(), startPosition, hitboxSize);
        mmorpg.getPhysicsManager().addCollider(hitbox);
        hitbox.onCollisionEnter(Triggers.character(this::onHit));
    }

    @Override
    protected void despawn() {
        hitbox.remove();
        projectileEntity.remove();
    }

    private class ProjectileEntity extends Entity {
        Pos startPosition;
        double maxDistance;
        PhysicsResult lastPhysicsResult;

        public ProjectileEntity(@NotNull EntityType entityType, Pos startPosition, double maxDistance) {
            super(entityType);
            this.startPosition = startPosition;
            this.maxDistance = maxDistance;
            this.hasPhysics = false;
            this.lastPhysicsResult = null;
        }

        @Override
        public void tick(long time) {
            final Pos posBefore = getPosition();
            super.tick(time);
            final Pos posNow = getPosition();

            this.setVelocity(velocity);
            hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
            if (getPosition().distanceSquared(startPosition) > maxDistance * maxDistance) {
                Projectile.this.remove();
            }

            Vec diff = Vec.fromPoint(posNow.sub(posBefore));
            PhysicsResult result = CollisionUtils.handlePhysics(instance, this.getChunk(), this.getBoundingBox(), posBefore, diff, lastPhysicsResult);
            lastPhysicsResult = result;

            // TODO: give the implementer more details on the block collision
            if (result.collisionX() || result.collisionY() || result.collisionZ()) {
                onStuck();
            }
        }
    }

    // TODO: better way to do this without redundancy? (probably involving CharacterModel refactor)
    // TODO: logic problems arising from using ModelEntity
    private class ProjectileEntityModel extends ModelEntity {
        Pos startPosition;
        double maxDistance;
        PhysicsResult lastPhysicsResult;
        public ProjectileEntityModel(@NotNull Model model, Pos startPosition, double maxDistance) {
            super(model);
            this.startPosition = startPosition;
            this.maxDistance = maxDistance;
            this.hasPhysics = false;
            this.lastPhysicsResult = null;
        }

        @Override
        public void tick(long time) {
            final Pos posBefore = getPosition();
            super.tick(time);
            final Pos posNow = getPosition();

            this.setVelocity(velocity);
            hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
            if (getPosition().distanceSquared(startPosition) > maxDistance * maxDistance) {
                Projectile.this.remove();
            }

            Vec diff = Vec.fromPoint(posNow.sub(posBefore));
            PhysicsResult result = CollisionUtils.handlePhysics(instance, this.getChunk(), this.getBoundingBox(), posBefore, diff, lastPhysicsResult);
            lastPhysicsResult = result;

            // TODO: give the implementer more details on the block collision
            if (result.collisionX() || result.collisionY() || result.collisionZ()) {
                onStuck();
            }
        }
    }
}
