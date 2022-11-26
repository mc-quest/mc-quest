package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterCompleteQuestEvent;
import com.mcquest.server.event.PlayerCharacterStartQuestEvent;
import com.mcquest.server.ui.ChatColor;
import com.mcquest.server.util.MathUtility;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.*;

public class PlayerCharacterQuestTracker {
    private final PlayerCharacter pc;
    /**
     * Internally, progress of -1 indicates that objective is inaccessible.
     */
    private final Map<Quest, int[]> objectiveProgress;
    private final Set<Quest> completedQuests;
    private final List<Quest> trackedQuests;
    private final Collection<QuestObjective> recentlyCompletedObjectives;

    @ApiStatus.Internal
    public PlayerCharacterQuestTracker(PlayerCharacter pc, Map<Quest, int[]> objectiveProgress,
                                       Set<Quest> completedQuests, List<Quest> trackedQuests) {
        this.pc = pc;
        this.objectiveProgress = objectiveProgress;
        this.completedQuests = completedQuests;
        this.trackedQuests = trackedQuests;
        this.recentlyCompletedObjectives = new ArrayList<>();
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::updateSidebar).delay(TaskSchedule.nextTick()).schedule();
    }

    public Set<Quest> getInProgressQuests() {
        return Collections.unmodifiableSet(objectiveProgress.keySet());
    }

    public Set<Quest> getCompletedQuests() {
        return Collections.unmodifiableSet(completedQuests);
    }

    public List<Quest> getTrackedQuests() {
        return Collections.unmodifiableList(trackedQuests);
    }

    public QuestStatus getStatus(Quest quest) {
        if (completedQuests.contains(quest)) {
            return QuestStatus.COMPLETED;
        }
        if (objectiveProgress.containsKey(quest)) {
            return QuestStatus.IN_PROGRESS;
        }
        return QuestStatus.NOT_STARTED;
    }

    public boolean compareStatus(Quest quest, QuestStatus status) {
        return getStatus(quest) == status;
    }

    public void startQuest(Quest quest) {
        if (objectiveProgress.containsKey(quest) || completedQuests.contains(quest)) {
            return;
        }
        int objectiveCount = quest.getObjectiveCount();
        int[] initialProgress = new int[objectiveCount];
        for (int i = 0; i < objectiveCount; i++) {
            initialProgress[i] = -1;
        }
        objectiveProgress.put(quest, initialProgress);
        trackedQuests.add(quest);
        updateSidebar();
        pc.sendMessage(Component.text("Quest started: " + quest.getName(), NamedTextColor.YELLOW));
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.call(new PlayerCharacterStartQuestEvent(pc, quest));
    }

    public int getProgress(QuestObjective objective) {
        Quest quest = objective.getQuest();
        if (completedQuests.contains(quest)) {
            return objective.getGoal();
        }
        if (!objectiveProgress.containsKey(quest)) {
            return 0;
        }
        int progress = objectiveProgress.get(quest)[objective.getIndex()];
        if (progress == -1) {
            return 0;
        }
        return progress;
    }

    public void setProgress(QuestObjective objective, int progress) {
        Quest quest = objective.getQuest();
        if (!objectiveProgress.containsKey(quest)) {
            return;
        }
        int[] currentProgress = objectiveProgress.get(quest);
        int objectiveIndex = objective.getIndex();
        if (currentProgress[objectiveIndex] == -1) {
            // Inaccessible.
            return;
        }
        int goal = objective.getGoal();
        progress = MathUtility.clamp(progress, 0, goal);
        currentProgress[objectiveIndex] = progress;
        if (progress == goal) {
            pc.sendMessage(Component.empty()
                    .append(Component.text(goal + "/" + goal + " ", NamedTextColor.GREEN))
                    .append(TextUtility.deserializeText(objective.getDescription()))
                    .append(Component.text(" complete!", NamedTextColor.GREEN)));
            recentlyCompletedObjectives.add(objective);
            SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
            scheduler.buildTask(() -> {
                recentlyCompletedObjectives.remove(objective);
                updateSidebar();
            }).delay(Duration.ofSeconds(3)).schedule();
            if (!checkForCompletion(quest)) {
                // If quest is complete, the sidebar will be updated elsewhere.
                updateSidebar();
            }
        } else {
            // In case objective was completed, and immediately uncompleted.
            recentlyCompletedObjectives.remove(objective);
            updateSidebar();
        }
    }

    public void addProgress(QuestObjective objective, int progress) {
        setProgress(objective, getProgress(objective) + progress);
    }

    public boolean isComplete(QuestObjective objective) {
        return getProgress(objective) == objective.getGoal();
    }

    public void complete(QuestObjective objective) {
        setProgress(objective, objective.getGoal());
    }

    private boolean checkForCompletion(Quest quest) {
        if (allObjectivesComplete(quest)) {
            complete(quest);
            return true;
        }
        return false;
    }

    private boolean allObjectivesComplete(Quest quest) {
        int[] progress = objectiveProgress.get(quest);
        for (int i = 0; i < quest.getObjectiveCount(); i++) {
            QuestObjective objective = quest.getObjective(i);
            if (progress[i] != objective.getGoal()) {
                return false;
            }
        }
        return true;
    }

    private void complete(Quest quest) {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        objectiveProgress.remove(quest);
        completedQuests.add(quest);
        trackedQuests.remove(quest);
        pc.sendMessage(Component.text("Quest completed: " + quest.getName(), NamedTextColor.YELLOW));
        eventHandler.call(new PlayerCharacterCompleteQuestEvent(pc, quest));
        updateSidebar();
    }

    /**
     * Returns whether the objective is accessible to the player character. An
     * objective is accessible if it has been made accessible and the quest has
     * not yet been completed.
     */
    public boolean isAccessible(QuestObjective objective) {
        Quest quest = objective.getQuest();
        int index = objective.getIndex();
        int[] progress = objectiveProgress.get(quest);
        if (progress == null) {
            return false;
        }
        return progress[index] != -1;
    }

    public void setAccessible(QuestObjective objective, boolean accessible) {
        Quest quest = objective.getQuest();
        int index = objective.getIndex();
        int[] progress = objectiveProgress.get(quest);
        if (progress == null) {
            return;
        }
        if (accessible) {
            if (progress[index] == -1) {
                progress[index] = 0;
            }
        } else {
            progress[index] = -1;
            // In case objective was completed, and immediately made inaccessible.
            recentlyCompletedObjectives.remove(objective);
        }
        updateSidebar();
    }

    private void updateSidebar() {
        Sidebar sidebar = new Sidebar(Component.text("Quests", NamedTextColor.YELLOW));
        List<TextComponent> sidebarText = sidebarText();
        if (sidebarText.size() > 15) {
            sidebarText.set(14, Component.text("  \u2022\u2022\u2022", NamedTextColor.YELLOW));
        }
        int numLines = Math.min(15, sidebarText.size());
        for (int i = 0; i < numLines; i++) {
            Component text = sidebarText.get(i);
            int line = numLines - i - 1;
            sidebar.createLine(new Sidebar.ScoreboardLine(String.valueOf(i), text, line));
        }
        sidebar.addViewer(pc.getPlayer());
    }

    private List<TextComponent> sidebarText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < trackedQuests.size(); i++) {
            Quest quest = trackedQuests.get(i);
            text.append(ChatColor.YELLOW);
            text.append('(');
            text.append(ChatColor.BOLD);
            int questNum = i + 1;
            text.append(questNum);
            text.append(ChatColor.RESET);
            text.append(ChatColor.YELLOW);
            text.append(')');
            text.append(' ');
            text.append(quest.getName());
            text.append(ChatColor.RESET);
            text.append('\n');
            for (int j = 0; j < quest.getObjectiveCount(); j++) {
                QuestObjective objective = quest.getObjective(j);
                boolean recentlyCompleted = recentlyCompletedObjectives.contains(objective);
                if (recentlyCompleted || (isAccessible(objective) && !isComplete(objective))) {
                    if (recentlyCompleted) {
                        text.append(ChatColor.GREEN);
                    } else {
                        text.append(ChatColor.YELLOW);
                    }
                    text.append(ChatColor.BOLD);
                    text.append('\u2022');
                    text.append(' ');
                    text.append(getProgress(objective));
                    text.append('/');
                    text.append(objective.getGoal());
                    text.append(ChatColor.RESET);
                    text.append(ChatColor.WHITE);
                    text.append(' ');
                    text.append(objective.getDescription());
                    text.append(ChatColor.RESET);
                    text.append('\n');
                }
            }
            text.append('\n');
        }
        return TextUtility.wordWrap(text.toString());
    }
}
