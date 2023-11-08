package net.mcquest.server.features;

import com.google.common.base.Predicates;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.*;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.PhysicsManager;
import net.mcquest.core.physics.Triggers;
import net.mcquest.core.quest.*;
import net.mcquest.core.ui.Tutorial;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Maps;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.npc.GuardThomas;
import net.mcquest.server.npc.TrainingDummy;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;

import java.time.Duration;

public class TutorialQuest implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        createTrainingGroundsBounds();
        trainingDummies();
        guardThomas();
        createQuestMarkers();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handleLogin);
        eventHandler.addListener(MenuOpenEvent.class, this::handleOpenMenu);
        eventHandler.addListener(SkillTreeOpenEvent.class, this::handleOpenSkillTree);
        eventHandler.addListener(SkillUnlockEvent.class, this::handleSkillUpgrade);
        eventHandler.addListener(SkillAddToHotbarEvent.class, this::handleAddSkillToHotbar);
        eventHandler.addListener(ActiveSkillUseEvent.class, this::handleUseSkill);
        eventHandler.addListener(MapOpenEvent.class, this::handleOpenMap);
        Quests.TUTORIAL.getObjective(7).onComplete().subscribe(this::handleSlayTrainingDummyObjectiveComplete);
    }

    private void createTrainingGroundsBounds() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Collider trainingGroundsBounds = new Collider(
                Instances.ELADRADOR,
                new Pos(2868, 85, 3200),
                new Pos(2876, 100, 3216)
        );
        trainingGroundsBounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnterTrainingGrounds));
        physicsManager.addCollider(trainingGroundsBounds);
    }

    private void trainingDummies() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        for (Pos position : new Pos[]{
                new Pos(2873.171556, 86.000000, 3213.282500, 116.399986f, 37.649979f),
                new Pos(2870.046252, 86.000000, 3212.691405, 120.749977f, 35.249958f),
                new Pos(2868.191163, 86.000000, 3208.879730, 176.549988f, 24.749960f),
                new Pos(2870.644940, 85.937500, 3205.491251, -121.050018f, 17.999956f),
                new Pos(2873.360996, 86.000000, 3205.378723, -29.100037f, 10.199958f)
        }) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, TrainingDummy::new));
        }
    }

    private void guardThomas() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(3198, 117, 3646, 180, 0),
                GuardThomas::new
        ));
    }

    private void createQuestMarkers() {
        QuestManager questManager = mmorpg.getQuestManager();

        QuestMarker trainingGroundsMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2871, 86, 3206),
                Quests.TUTORIAL,
                QuestMarkerIcon.OBJECTIVE,
                Predicates.or(
                        Quests.TUTORIAL.getObjective(6)::isInProgress,
                        Quests.TUTORIAL.getObjective(7)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(trainingGroundsMarker);

        QuestMarker guardThomasMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2871, 86, 3206),
                Quests.TUTORIAL,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.TUTORIAL.getObjective(8)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(guardThomasMarker);
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
        Tutorial.message(pc,
                Component.text("Left click a skill and add it to your hotbar!"),
                Duration.ofSeconds(2));
    }

    private void handleAddSkillToHotbar(SkillAddToHotbarEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 3)) {
            return;
        }
        Quests.TUTORIAL.getObjective(3).addProgress(pc);
        Tutorial.message(pc,
                Component.text("Use ")
                        .append(Component.text(event.getSkill().getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" by pressing "))
                        .append(Component.text("[", NamedTextColor.GRAY))
                        .append(Component.text(event.getSlot() + 1, NamedTextColor.YELLOW))
                        .append(Component.text("]", NamedTextColor.GRAY))
                        .append(Component.text("!")),
                Duration.ofSeconds(2));
    }

    private void handleUseSkill(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 4)) {
            return;
        }
        Quests.TUTORIAL.getObjective(4).addProgress(pc);
        Tutorial.message(pc,
                Component.text("Click the ")
                        .append(Component.text("Map", NamedTextColor.YELLOW))
                        .append(Component.text(" button in the menu to open your map!")),
                Duration.ofSeconds(2));
    }

    private void handleOpenMap(MapOpenEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (!tutorialObjectiveActive(pc, 5)) {
            return;
        }
        Quests.TUTORIAL.getObjective(5).addProgress(pc);
        Tutorial.message(pc,
                Component.text("Use your map to navigate to the training grounds!"),
                Duration.ofSeconds(2));
    }

    private void handleEnterTrainingGrounds(PlayerCharacter pc) {
        if (!tutorialObjectiveActive(pc, 6)) {
            return;
        }
        Quests.TUTORIAL.getObjective(6).addProgress(pc);
        Tutorial.message(pc,
                Component.text("Use your weapon and skills to defeat the training dummies!"),
                Duration.ofSeconds(2));
    }

    private void handleSlayTrainingDummyObjectiveComplete(QuestObjectiveCompleteEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.sendMessage(Component.text("Well done! Speak with Guard Thomas to claim your reward!"));
    }
}
