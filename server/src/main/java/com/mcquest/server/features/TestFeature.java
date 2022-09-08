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
import com.mcquest.server.util.Debug;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;

import java.time.Duration;

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
        Pos hitboxCenter = position.add(0, 1.5, 0).add(direction);
        Collider hitbox = new Collider(instance, hitboxCenter, 1.0, 1.0, 1.0) {
            @Override
            protected void onCollisionEnter(Collider other) {
                if (other instanceof CharacterHitbox characterHitbox) {
                    Character character = characterHitbox.getCharacter();
                    if (!character.isFriendly(pc)) {
                        character.damage(pc, 0.1);
                        pc.disarm(Duration.ofMillis(500));
                    }
                }
            }
        };
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.addCollider(hitbox);
        Debug.showCollider(hitbox);
        Debug.hideCollider(hitbox);
        physicsManager.removeCollider(hitbox);
    }
}
