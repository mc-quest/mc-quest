package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.MageSkills;
import com.mcquest.server.event.ActiveSkillUseEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.sound.SoundEvent;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
        mmorpg.getGlobalEventHandler().addListener(PlayerChatEvent.class, evt -> {
            PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(evt.getPlayer());
            useFireball(new ActiveSkillUseEvent(pc, null));
        });
    }

    public void useFireball(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos startPosition = pc.getEyePosition().add(pc.getLookDirection().mul(1f));
        Vec hitboxSize = new Vec(1f, 1f, 1f);
        Collider hitbox = new Collider(instance, startPosition, hitboxSize);
        Entity fireballEntity = new Entity(EntityType.FIREBALL) {
            @Override
            public void tick(long time) {
                super.tick(time);
                hitbox.setCenter(this.getPosition());
            }
        };
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        hitbox.onCollisionEnter(other -> {
            if (!(other instanceof CharacterHitbox characterHitbox)) {
                return;
            }
            Character character = characterHitbox.getCharacter();
            if (character.isFriendly(pc)) {
                return;
            }
            double damageAmount = 6;
            character.damage(pc, damageAmount);
            fireballEntity.remove();
            physicsManager.removeCollider(hitbox);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound);
        });
        physicsManager.addCollider(hitbox);
        Vec fireballVelocity = pc.getLookDirection().mul(20.0);
        fireballEntity.setInstance(instance, startPosition).join();
        fireballEntity.setNoGravity(true);
        fireballEntity.setVelocity(fireballVelocity);
        Sound summonFireballSound = Sound.sound(SoundEvent.BLOCK_FIRE_EXTINGUISH, Sound.Source.PLAYER, 1f, 1f);
        instance.playSound(summonFireballSound);
    }
}
