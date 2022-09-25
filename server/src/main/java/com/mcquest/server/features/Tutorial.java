package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Quests;
import com.mcquest.server.event.*;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.npc.TrainingDummy;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.instance.Instance;

public class Tutorial implements Feature {
    private static final Pos[] TRAINING_DUMMY_POSITIONS = {};

    private Quest tutorial;

    @Override
    public void hook(Mmorpg mmorpg) {
        QuestManager questManager = mmorpg.getQuestManager();
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        NonPlayerCharacterSpawner npcSpawner = mmorpg.getNonPlayerCharacterSpawner();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        tutorial = questManager.getQuest(Quests.TUTORIAL);
        eventHandler.addListener(PlayerCharacterOpenMenuEvent.class, this::handleOpenMenu);
        eventHandler.addListener(PlayerCharacterOpenSkillTreeEvent.class, this::handleOpenSkillTree);
        eventHandler.addListener(PlayerCharacterUpgradeSkillEvent.class, this::handleSkillUpgrade);
        eventHandler.addListener(PlayerCharacterAddSkillToHotbarEvent.class, this::handleAddSkillToHotbar);
        eventHandler.addListener(PlayerCharacterUseSkillEvent.class, this::handleUseSkill);
        eventHandler.addListener(PlayerCharacterOpenMapEvent.class, this::handleOpenMap);
        Instance eladrador = instanceManager.getInstance(Instances.ELADRADOR);
        Collider trainingGroundsBounds = new Collider(eladrador, 0, 0, 0, 0, 0, 0) {
            @Override
            public void onCollisionEnter(Collider other) {
                if (other instanceof PlayerCharacter.Hitbox hitbox) {
                    PlayerCharacter pc = hitbox.getCharacter();
                    handleEnterTrainingGrounds(pc);
                }
            }
        };
        physicsManager.addCollider(trainingGroundsBounds);
        for (Pos position : TRAINING_DUMMY_POSITIONS) {
            TrainingDummy trainingDummy = new TrainingDummy(mmorpg, eladrador, position);
            npcSpawner.add(trainingDummy);
        }
    }

    private void handleOpenMenu(PlayerCharacterOpenMenuEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
    }

    private void handleOpenSkillTree(PlayerCharacterOpenSkillTreeEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
    }

    private void handleSkillUpgrade(PlayerCharacterUpgradeSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
    }

    private void handleAddSkillToHotbar(PlayerCharacterAddSkillToHotbarEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
    }

    private void handleUseSkill(PlayerCharacterUseSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
    }

    private void handleOpenMap(PlayerCharacterOpenMapEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
    }

    private void handleEnterTrainingGrounds(PlayerCharacter pc) {
    }
}
