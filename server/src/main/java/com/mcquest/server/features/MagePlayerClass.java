package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.CharacterHitbox;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.server.constants.MageSkills;
import com.mcquest.core.event.ActiveSkillUseEvent;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.core.util.Debug;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.sound.SoundEvent;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
    }

    public void useFireball(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos startPosition = pc.getEyePosition().add(pc.getLookDirection().mul(1f));
        double maxDistance = 20.0;
        Vec hitboxSize = new Vec(1f, 1f, 1f);
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Collider hitbox = new Collider(instance, startPosition, hitboxSize);
        double fireballSpeed = 20.0;
        Vec fireballVelocity = pc.getLookDirection().mul(fireballSpeed);
        Entity fireballEntity = new Entity(EntityType.FIREBALL) {
            @Override
            public void tick(long time) {
                super.tick(time);
                hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
                setVelocity(fireballVelocity);
                if (getPosition().distanceSquared(startPosition) > maxDistance * maxDistance) {
                    remove();
                    hitbox.remove();
                }
            }
        };
        hitbox.onCollisionEnter(other -> {
            if (!(other instanceof CharacterHitbox characterHitbox)) {
                return;
            }
            Character character = characterHitbox.getCharacter();
            if (character.getAttitude(pc) == Attitude.FRIENDLY) {
                return;
            }
            double damageAmount = 6.0;
            character.damage(pc, damageAmount);
            if (!fireballEntity.isRemoved()) {
                fireballEntity.remove();
                hitbox.remove();
            }
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound);
        });
        fireballEntity.setInstance(instance, startPosition.sub(0.0, 0.5, 0.0)).join();
        physicsManager.addCollider(hitbox);
        fireballEntity.setNoGravity(true);
        Sound summonFireballSound = Sound.sound(SoundEvent.BLOCK_FIRE_EXTINGUISH, Sound.Source.PLAYER, 1f, 1f);
        instance.playSound(summonFireballSound);
        Debug.showCollider(hitbox);
    }
}
