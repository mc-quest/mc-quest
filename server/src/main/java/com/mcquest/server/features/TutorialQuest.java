package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.*;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.core.quest.*;
import com.mcquest.core.ui.Tutorial;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Maps;
import com.mcquest.server.constants.Quests;
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
        eventHandler.addListener(SkillAddToHotbarEvent.class, this::handleAddSkillToHotbar);
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
        // TODO
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
        if (Quests.TUTORIAL.getStatus(pc) != QuestStatus.NOT_STARTED) {
            return;
        }
        Quests.TUTORIAL.start(pc);
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
        Quests.TUTORIAL.getObjective(0).addProgress(pc);
        Tutorial.message(pc,
                Component.text("Open your skill tree to unlock powerful abilities!"),
                Duration.ofSeconds(2));
    }

    private void handleOpenSkillTree(SkillTreeOpenEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 1)) {
            return;
        }
        Quests.TUTORIAL.getObjective(1).addProgress(pc);
        Tutorial.message(pc,
                Component.text("Shift click a skill to unlock it!"),
                Duration.ofSeconds(2));
    }

    private void handleSkillUpgrade(SkillUnlockEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 2)) {
            return;
        }
        Quests.TUTORIAL.getObjective(2).addProgress(pc);
    }

    private void handleAddSkillToHotbar(SkillAddToHotbarEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 3)) {
            return;
        }
        Quests.TUTORIAL.getObjective(3).addProgress(pc);
    }

    private void handleUseSkill(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 4)) {
            return;
        }
        Quests.TUTORIAL.getObjective(4).addProgress(pc);
    }

    private void handleOpenMap(MapOpenEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 5)) {
            return;
        }
        Quests.TUTORIAL.getObjective(5).addProgress(pc);
    }

    private void handleEnterTrainingGrounds(PlayerCharacter pc) {
        if (!tutorialObjectiveActive(pc, 6)) {
            return;
        }
        Quests.TUTORIAL.getObjective(6).addProgress(pc);
    }
}
