package com.mcquest.core.quest;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.persistence.PersistentQuestObjectiveData;
import com.mcquest.core.persistence.PlayerCharacterData;
import com.mcquest.core.text.WordWrap;
import com.mcquest.core.event.QuestCompleteEvent;
import com.mcquest.core.event.QuestStartEvent;
import com.mcquest.core.text.ChatColor;
import com.mcquest.core.text.TextSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.*;

public class QuestTracker {
    private final PlayerCharacter pc;
    private final Map<Quest, int[]> objectiveProgress;
    private final Collection<Quest> completedQuests;
    private final List<Quest> trackedQuests;
    private final Collection<QuestObjective> recentlyCompletedObjectives;

    @ApiStatus.Internal
    public QuestTracker(PlayerCharacter pc, PlayerCharacterData data,
                        QuestManager questManager) {
        this.pc = pc;

        objectiveProgress = new HashMap<>();
        PersistentQuestObjectiveData[] objectiveData = data.getQuestObjectiveData();
        for (PersistentQuestObjectiveData questData : objectiveData) {
            Quest quest = questManager.getQuest(questData.getQuestId());
            objectiveProgress.put(quest, questData.getObjectiveProgress());
        }

        completedQuests = new HashSet<>();
        int[] completedQuestIds = data.getCompletedQuestIds();
        for (int id : completedQuestIds) {
            Quest quest = questManager.getQuest(id);
            completedQuests.add(quest);
        }

        trackedQuests = new ArrayList<>();
        int[] trackedQuestIds = data.getTrackedQuestIds();
        for (int id : trackedQuestIds) {
            Quest quest = questManager.getQuest(id);
            trackedQuests.add(quest);
        }

        recentlyCompletedObjectives = new ArrayList<>();

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::updateSidebar).delay(TaskSchedule.nextTick()).schedule();
    }

    public Collection<Quest> getInProgressQuests() {
        return Collections.unmodifiableCollection(objectiveProgress.keySet());
    }

    public Collection<Quest> getCompletedQuests() {
        return Collections.unmodifiableCollection(completedQuests);
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

    public void startQuest(Quest quest) {
        if (objectiveProgress.containsKey(quest) || completedQuests.contains(quest)) {
            return;
        }

        objectiveProgress.put(quest, new int[quest.getObjectiveCount()]);
        trackedQuests.add(quest);

        pc.sendMessage(questStartedMessage(quest));
        updateSidebar();

        QuestStartEvent event = new QuestStartEvent(pc, quest);
        quest.onStart().emit(event);
        MinecraftServer.getGlobalEventHandler().call(event);
    }

    public boolean isAvailable(QuestObjective objective) {
        Quest quest = objective.getQuest();

        if (getStatus(quest) != QuestStatus.IN_PROGRESS) {
            return false;
        }

        for (QuestObjective prerequisite : objective.getPrerequisites()) {
            if (!isComplete(prerequisite)) {
                return false;
            }
        }

        return true;
    }

    public int getProgress(QuestObjective objective) {
        Quest quest = objective.getQuest();

        if (completedQuests.contains(quest)) {
            return objective.getGoal();
        }

        if (!objectiveProgress.containsKey(quest)) {
            return 0;
        }

        return objectiveProgress.get(quest)[objective.getIndex()];
    }

    public void addProgress(QuestObjective objective, int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException();
        }

        if (progress == 0) {
            return;
        }

        Quest quest = objective.getQuest();

        if (!objectiveProgress.containsKey(quest)) {
            return;
        }

        for (QuestObjective prerequisite : objective.getPrerequisites()) {
            if (!prerequisite.isComplete(pc)) {
                return;
            }
        }

        int objectiveIndex = objective.getIndex();
        int goal = objective.getGoal();
        int[] currentProgress = objectiveProgress.get(quest);

        if (currentProgress[objectiveIndex] == goal) {
            return;
        }

        int newProgress = Math.min(
                currentProgress[objectiveIndex] + progress,
                objective.getGoal()
        );
        currentProgress[objectiveIndex] = newProgress;

        if (newProgress == goal) {
            pc.sendMessage(objectiveCompleteMessage(objective));

            recentlyCompletedObjectives.add(objective);
            MinecraftServer.getSchedulerManager().buildTask(() -> {
                recentlyCompletedObjectives.remove(objective);
                updateSidebar();
            }).delay(Duration.ofSeconds(3)).schedule();

            if (allObjectivesComplete(quest)) {
                completeQuest(quest);
            }
        } else {
            pc.sendMessage(objectiveProgressMessage(objective, newProgress));
        }

        updateSidebar();
    }

    private boolean allObjectivesComplete(Quest quest) {
        int[] progress = objectiveProgress.get(quest);

        for (int i = 0; i < quest.getObjectiveCount(); i++) {
            if (progress[i] != quest.getObjective(i).getGoal()) {
                return false;
            }
        }

        return true;
    }

    private void completeQuest(Quest quest) {
        objectiveProgress.remove(quest);
        completedQuests.add(quest);
        MinecraftServer.getSchedulerManager()
                .buildTask(() -> {
                    trackedQuests.remove(quest);
                    updateSidebar();
                })
                .delay(Duration.ofSeconds(3))
                .schedule();

        pc.sendMessage(questCompletedMessage(quest));

        QuestCompleteEvent event = new QuestCompleteEvent(pc, quest);
        quest.onComplete().emit(event);
        MinecraftServer.getGlobalEventHandler().call(event);
    }

    public boolean isComplete(QuestObjective objective) {
        return getProgress(objective) == objective.getGoal();
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
        sidebar.addViewer(pc.getEntity());
    }

    private List<TextComponent> sidebarText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < trackedQuests.size(); i++) {
            Quest quest = trackedQuests.get(i);
            ChatColor questColor;
            if (quest.getStatus(pc) == QuestStatus.COMPLETED) {
                questColor = ChatColor.GREEN;
            } else {
                questColor = ChatColor.YELLOW;
            }
            text.append(questColor);
            text.append('(');
            text.append(ChatColor.BOLD);
            int questNum = i + 1;
            text.append(questNum);
            text.append(ChatColor.RESET);
            text.append(questColor);
            text.append(')');
            text.append(' ');
            text.append(quest.getName());
            text.append(ChatColor.RESET);
            text.append('\n');
            for (int j = 0; j < quest.getObjectiveCount(); j++) {
                QuestObjective objective = quest.getObjective(j);
                boolean recentlyCompleted = recentlyCompletedObjectives.contains(objective);
                if (recentlyCompleted || (isAvailable(objective) && !isComplete(objective))) {
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

        while (!text.isEmpty() && text.charAt(text.length() - 1) == '\n') {
            text.deleteCharAt(text.length() - 1);
        }

        return WordWrap.wrap(text.toString());
    }

    private static TextComponent questStartedMessage(Quest quest) {
        String content = String.format("Quest started: %s", quest.getName());
        return Component.text(content, NamedTextColor.YELLOW);
    }

    private static TextComponent objectiveProgressMessage(QuestObjective objective, int newProgress) {
        int goal = objective.getGoal();

        return Component.empty()
                .append(Component.text(newProgress + "/" + goal + " ", NamedTextColor.YELLOW))
                .append(TextSerializer.deserialize(objective.getDescription()));
    }

    private static TextComponent objectiveCompleteMessage(QuestObjective objective) {
        int goal = objective.getGoal();

        return Component.empty()
                .append(Component.text(goal + "/" + goal + " ", NamedTextColor.GREEN))
                .append(TextSerializer.deserialize(objective.getDescription()));
    }

    private static TextComponent questCompletedMessage(Quest quest) {
        String content = String.format("Quest completed: %s", quest.getName());
        return Component.text(content, NamedTextColor.YELLOW);
    }
}
