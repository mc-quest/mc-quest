package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.NonPlayerCharacterSpawner;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.*;
import com.mcquest.core.quest.*;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Maps;
import com.mcquest.server.constants.Quests;
import com.mcquest.core.feature.Feature;
import com.mcquest.server.npc.Deer;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.core.ui.Tutorial;
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
        createQuestMarkers();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handleLogin);
        eventHandler.addListener(MenuOpenEvent.class, this::handleOpenMenu);
        eventHandler.addListener(SkillTreeOpenEvent.class, this::handleOpenSkillTree);
        eventHandler.addListener(SkillUnlockEvent.class, this::handleSkillUpgrade);
        eventHandler.addListener(AddSkillToHotbarEvent.class, this::handleAddSkillToHotbar);
        eventHandler.addListener(ActiveSkillUseEvent.class, this::handleUseSkill);
        eventHandler.addListener(MapOpenEvent.class, this::handleOpenMap);
    }

    private void createTrainingGroundsBounds() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Collider trainingGroundsBounds = new Collider(Instances.ELADRADOR, Pos.ZERO, Pos.ZERO);
        trainingGroundsBounds.onCollisionEnter(other -> {
            if (other instanceof PlayerCharacter.Hitbox hitbox) {
                PlayerCharacter pc = hitbox.getCharacter();
                handleEnterTrainingGrounds(pc);
            }
        });
        physicsManager.addCollider(trainingGroundsBounds);
    }

    private void spawnTrainingDummies() {
        NonPlayerCharacterSpawner npcSpawner = mmorpg.getNonPlayerCharacterSpawner();
        for (int i = 0; i < 1000000; i++) {
            Pos position = new Pos(Math.random() * 100000 - 50000, 80, Math.random() * 100000 - 50000);
            Deer trainingDummy = new Deer(mmorpg, Instances.ELADRADOR, position);
            npcSpawner.add(trainingDummy);
        }
    }

    private void createQuestMarkers() {
        QuestManager questManager = mmorpg.getQuestManager();
        QuestMarker trainingGroundsMarker = questManager.createQuestMarker(Instances.ELADRADOR,
                new Pos(0, 70, 0), Quests.TUTORIAL, QuestMarkerIcon.OBJECTIVE,
                pc -> Quests.TUTORIAL.getObjective(6).isAccessible(pc));
        Maps.MELCHER.addQuestMarker(trainingGroundsMarker);
    }

    private boolean tutorialObjectiveActive(PlayerCharacter pc, int objectiveIndex) {
        QuestObjective objective = Quests.TUTORIAL.getObjective(objectiveIndex);
        return objective.isAccessible(pc) && !objective.isComplete(pc);

    }

    private void handleLogin(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!Quests.TUTORIAL.compareStatus(pc, QuestStatus.NOT_STARTED)) {
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

    private void handleOpenMenu(MenuOpenEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 0)) {
            return;
        }
        Quests.TUTORIAL.getObjective(0).complete(pc);
        Quests.TUTORIAL.getObjective(1).setAccessible(pc, true);
        Tutorial.message(pc,
                Component.text("Open your skill tree to unlock powerful abilities!"),
                Duration.ofSeconds(2));
    }

    private void handleOpenSkillTree(SkillTreeOpenEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 1)) {
            return;
        }
        Quests.TUTORIAL.getObjective(1).complete(pc);
        Quests.TUTORIAL.getObjective(2).setAccessible(pc, true);
        Tutorial.message(pc,
                Component.text("Shift click a skill to unlock it!"),
                Duration.ofSeconds(2));
    }

    private void handleSkillUpgrade(SkillUnlockEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 2)) {
            return;
        }
        Quests.TUTORIAL.getObjective(2).complete(pc);
        Quests.TUTORIAL.getObjective(3).setAccessible(pc, true);
    }

    private void handleAddSkillToHotbar(AddSkillToHotbarEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 3)) {
            return;
        }
        Quests.TUTORIAL.getObjective(3).complete(pc);
        Quests.TUTORIAL.getObjective(4).setAccessible(pc, true);
    }

    private void handleUseSkill(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 4)) {
            return;
        }
        Quests.TUTORIAL.getObjective(4).complete(pc);
        Quests.TUTORIAL.getObjective(5).setAccessible(pc, true);
    }

    private void handleOpenMap(MapOpenEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 5)) {
            return;
        }
        Quests.TUTORIAL.getObjective(5).complete(pc);
        Quests.TUTORIAL.getObjective(6).setAccessible(pc, true);
    }

    private void handleEnterTrainingGrounds(PlayerCharacter pc) {
        if (!tutorialObjectiveActive(pc, 6)) {
            return;
        }
        Quests.TUTORIAL.getObjective(6).complete(pc);
        Quests.TUTORIAL.getObjective(7).setAccessible(pc, true);
    }
}
