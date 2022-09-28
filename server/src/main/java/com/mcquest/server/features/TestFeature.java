package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.event.PlayerCharacterBasicAttackEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.physics.RaycastHit;
import com.mcquest.server.ui.InteractionSequence;
import com.mcquest.server.ui.PlayerCharacterInteractionCollider;
import com.mcquest.server.util.Debug;
import com.mcquest.server.util.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;

import java.time.Duration;

public class TestFeature implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Instance eladrador = mmorpg.getInstanceManager().getInstance(Instances.ELADRADOR);
        InteractionSequence interactionSequence = InteractionSequence.builder()
                .interaction(pc -> pc.sendMessage(Component.text("Message 1")))
                .interaction(pc -> pc.sendMessage(Component.text("Message 2")))
                .interaction(pc -> {
                    pc.getPlayer().setAllowFlying(true);
                    pc.getPlayer().setFlyingSpeed(0.5f);
                })
                .build();
        PlayerCharacterInteractionCollider collider = new PlayerCharacterInteractionCollider(
                eladrador, new Pos(0, 70, 0), 10, 10, 10,
                pc -> interactionSequence.advance(pc));
        mmorpg.getPhysicsManager().addCollider(collider);
        Debug.showCollider(collider);
    }

    private void basicAttack(PlayerCharacterBasicAttackEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos position = pc.getPosition();
        Vec direction = pc.getLookDirection();
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        pc.disarm(Duration.ofMillis(500));
        double distance = 5.0;
        Pos rayStart = position.withY(position.y() + 1.5);
        RaycastHit hit = physicsManager.raycast(instance, rayStart, direction, distance, collider ->
                collider instanceof CharacterHitbox hitbox && !hitbox.getCharacter().isFriendly(pc));
        if (hit != null) {
            CharacterHitbox hitbox = (CharacterHitbox) hit.getCollider();
            Character character = hitbox.getCharacter();
            character.damage(pc, 0.5);
        }
        ParticleEffects.line(instance, pc.getEyePosition(), direction, distance, Particle.CRIT, 4.0);
    }
}
