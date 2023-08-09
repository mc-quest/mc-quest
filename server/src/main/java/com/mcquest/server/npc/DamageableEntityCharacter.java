package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.CharacterHitbox;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class DamageableEntityCharacter extends EntityCharacter {
    private CharacterHitbox hitbox;

    public DamageableEntityCharacter(Mmorpg mmorpg, Instance instance, Pos position, Vec boundingBox) {
        super(mmorpg, instance, position, boundingBox);
    }

    @Override
    protected void spawn() {
        super.spawn();
        hitbox = new CharacterHitbox(this, getInstance(), hitboxCenter(), getBoundingBox());
        mmorpg.getPhysicsManager().addCollider(hitbox);
    }

    @Override
    protected void despawn() {
        super.despawn();
        if (isAlive()) {
            hitbox.remove();
        }
        hitbox = null;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void onDeath(DamageSource killer) {
        super.onDeath(killer);
        hitbox.remove();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void updatePosition(Pos position) {
        if (isSpawned()) {
            super.updatePosition(position);
            hitbox.setCenter(hitboxCenter());
        }
    }

    private Pos hitboxCenter() {
        return getPosition().withY(y -> y + getBoundingBox().y() / 2.0);
    }
}
