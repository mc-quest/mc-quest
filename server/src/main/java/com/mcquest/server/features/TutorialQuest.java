package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Quests;
import com.mcquest.server.event.*;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.npc.TrainingDummy;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.quest.QuestObjective;
import com.mcquest.server.quest.QuestStatus;
import com.mcquest.server.ui.Tutorial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;

import java.time.Duration;

public class TutorialQuest implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        createTrainingGroundsBounds();
        spawnTrainingDummies();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handleLogin);
        eventHandler.addListener(PlayerCharacterOpenMenuEvent.class, this::handleOpenMenu);
        eventHandler.addListener(PlayerCharacterOpenSkillTreeEvent.class, this::handleOpenSkillTree);
        eventHandler.addListener(PlayerCharacterUnlockSkillEvent.class, this::handleSkillUpgrade);
        eventHandler.addListener(PlayerCharacterAddSkillToHotbarEvent.class, this::handleAddSkillToHotbar);
        eventHandler.addListener(PlayerCharacterUseActiveSkillEvent.class, this::handleUseSkill);
        eventHandler.addListener(PlayerCharacterOpenMapEvent.class, this::handleOpenMap);
    }

    private void createTrainingGroundsBounds() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Collider trainingGroundsBounds = new Collider(Instances.ELADRADOR,
                0, 0, 0, 0, 0, 0) {
            @Override
            public void onCollisionEnter(Collider other) {
                if (other instanceof PlayerCharacter.Hitbox hitbox) {
                    PlayerCharacter pc = hitbox.getCharacter();
                    handleEnterTrainingGrounds(pc);
                }
            }
        };
        physicsManager.addCollider(trainingGroundsBounds);
    }

    private void spawnTrainingDummies() {
        NonPlayerCharacterSpawner npcSpawner = mmorpg.getNonPlayerCharacterSpawner();
        Pos[] positions = new Pos[]{new Pos(0, 70, 0)};
        for (Pos position : positions) {
            TrainingDummy trainingDummy = new TrainingDummy(mmorpg, Instances.ELADRADOR, position);
            npcSpawner.add(trainingDummy);
        }
    }

    private void handleLogin(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.NOT_STARTED) {
            return;
        }
        Quests.TUTORIAL.start(pc);
        Quests.TUTORIAL.getObjective(0).setAccessible(pc, true);
        Tutorial.message(pc,
                Component.text("Open your menu by pressing ")
                        .append(Component.text("[", NamedTextColor.GRAY))
                        .append(Component.text("F", NamedTextColor.YELLOW))
                        .append(Component.text("]", NamedTextColor.GRAY))
                        .append(Component.text("!")),
                Duration.ofSeconds(5));
    }

    private void handleOpenMenu(PlayerCharacterOpenMenuEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        QuestObjective openMenuObjective = Quests.TUTORIAL.getObjective(0);
        if (!openMenuObjective.isAccessible(pc) || openMenuObjective.isComplete(pc)) {
            return;
        }
        Quests.TUTORIAL.getObjective(0).complete(pc);
        Quests.TUTORIAL.getObjective(1).setAccessible(pc, true);
        Tutorial.message(pc,
                Component.text("Open your skill tree to unlock powerful abilities!"),
                Duration.ofSeconds(2));
    }

    private void handleOpenSkillTree(PlayerCharacterOpenSkillTreeEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.IN_PROGRESS) {
            return;
        }
        Quests.TUTORIAL.getObjective(1).complete(pc);
        Quests.TUTORIAL.getObjective(2).setAccessible(pc, true);
        Tutorial.message(pc,
                Component.text("Shift click a skill to unlock it!"),
                Duration.ofSeconds(2));
    }

    private void handleSkillUpgrade(PlayerCharacterUnlockSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.IN_PROGRESS) {
            return;
        }
        Quests.TUTORIAL.getObjective(2).complete(pc);
        Quests.TUTORIAL.getObjective(3).setAccessible(pc, true);
    }

    private void handleAddSkillToHotbar(PlayerCharacterAddSkillToHotbarEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.IN_PROGRESS) {
            return;
        }
        Quests.TUTORIAL.getObjective(3).complete(pc);
        Quests.TUTORIAL.getObjective(4).setAccessible(pc, true);
    }

    private void handleUseSkill(PlayerCharacterUseActiveSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.IN_PROGRESS) {
            return;
        }
        Quests.TUTORIAL.getObjective(4).complete(pc);
        Quests.TUTORIAL.getObjective(5).setAccessible(pc, true);
    }

    private void handleOpenMap(PlayerCharacterOpenMapEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.IN_PROGRESS) {
            return;
        }
        Quests.TUTORIAL.getObjective(5).complete(pc);
        Quests.TUTORIAL.getObjective(6).setAccessible(pc, true);
    }

    private void handleEnterTrainingGrounds(PlayerCharacter pc) {
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.IN_PROGRESS) {
            return;
        }
        Quests.TUTORIAL.getObjective(6).complete(pc);
        Quests.TUTORIAL.getObjective(7).setAccessible(pc, true);
    }
}
