package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterBasicAttackEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.npc.Wolf;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.physics.RaycastHit;
import com.mcquest.server.util.Debug;
import com.mcquest.server.util.ParticleEffects;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;

import java.time.Duration;
import java.util.List;

public class TestFeature implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Instance instance = mmorpg.getInstanceManager().getInstance("Eladrador");
        Pos spawnPosition = new Pos(0, 70, 0);
        Wolf wolf = new Wolf(mmorpg, instance, spawnPosition);
        NonPlayerCharacterSpawner npcSpawner = mmorpg.getNonPlayerCharacterSpawner();
        npcSpawner.add(wolf);
        mmorpg.getGlobalEventHandler().addListener(PlayerCharacterBasicAttackEvent.class, this::basicAttack);
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
        List<RaycastHit> hits = physicsManager.raycast(instance, rayStart, direction, distance);
        for (RaycastHit hit : hits) {
            Collider collider = hit.getCollider();
            if (collider instanceof CharacterHitbox hitbox) {
                Character character = hitbox.getCharacter();
                if (!character.isFriendly(pc)) {
                    character.damage(pc, 0.1);
                }
            }
        }
        ParticleEffects.line(instance, rayStart, direction, distance, Particle.CRIT, 4.0);
    }
}
