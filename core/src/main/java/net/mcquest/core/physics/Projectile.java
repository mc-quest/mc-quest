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

import java.util.function.Consumer;

public class Projectile extends Object {
    private Collider hitbox;
    private final Entity projectileEntity;
    private Vec velocity;
    private Vec hitboxSize;
    private Consumer<Collider> onHit;
    private Runnable onStuck;

    public Projectile(Mmorpg mmorpg, ObjectSpawner spawner, EntityType entityType, Pos startPosition, double maxDistance) {
        super(mmorpg, spawner);
        this.projectileEntity = new ProjectileEntity(entityType, startPosition, maxDistance);
        projectileEntity.setNoGravity(true);
        projectileEntity.setInstance(this.getInstance(), startPosition);
        this.velocity = Vec.ZERO;
        this.hitboxSize = Vec.ONE;
    }
    /**
     * implement on collider hit behaviour
     */
    public void onHit(Consumer<Collider> onHit) {
        this.onHit = onHit;
    }

    /**
     * implement on block stuck behaviour
     */
    public void onStuck(Runnable onStuck) {
        this.onStuck = onStuck;
    }

    public void setVelocity(Vec vel) {
        this.velocity = vel;
    }

    public void setHitboxSize(Vec size) {
        this.hitboxSize = size;
    }

    @Override
    protected void spawn() {
        projectileEntity.setVelocity(velocity);

        hitbox = new Collider(this.getInstance(), projectileEntity.getPosition(), hitboxSize);
        this.getMmorpg().getPhysicsManager().addCollider(hitbox);
        hitbox.onCollisionEnter(onHit);
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

            Projectile.this.setPosition(posNow);
            hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
            if (getPosition().distanceSquared(startPosition) > maxDistance * maxDistance) {
                Projectile.this.remove();
            }

            Vec diff = Vec.fromPoint(posNow.sub(posBefore));
            PhysicsResult result = CollisionUtils.handlePhysics(instance, this.getChunk(), this.getBoundingBox(), posBefore, diff, lastPhysicsResult);
            lastPhysicsResult = result;

            if (result.collisionX() || result.collisionY() || result.collisionZ()) {
                onStuck.run();
            }
        }
    }
}
