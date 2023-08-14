package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.entity.EntityHuman;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.ui.InteractCollider;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.PlayerSkin;

public abstract class StaticHuman extends EntityCharacter {
    private static final Vec SIZE = new Vec(1.0, 2.0, 1.0);

    private final PlayerSkin skin;
    private InteractCollider interactCollider;

    protected StaticHuman(Mmorpg mmorpg, Instance instance, Pos position, PlayerSkin skin) {
        super(mmorpg, instance, position, SIZE);
        this.skin = skin;
    }

    @Override
    protected void spawn() {
        super.spawn();

        interactCollider = new InteractCollider(getInstance(),
                interactColliderCenter(), SIZE, this::interact);
        mmorpg.getPhysicsManager().addCollider(interactCollider);
    }

    @Override
    protected void despawn() {
        super.despawn();

        interactCollider.remove();
        interactCollider = null;
    }

    @Override
    protected EntityCreature createEntity() {
        return new EntityHuman(skin);
    }

    private Pos interactColliderCenter() {
        return getPosition().withY(y -> y + SIZE.y() / 2.0);
    }

    private void interact(PlayerCharacter pc) {
        entity.lookAt(pc.getEyePosition());
        onInteract(pc);
    }

    protected abstract void onInteract(PlayerCharacter pc);
}
