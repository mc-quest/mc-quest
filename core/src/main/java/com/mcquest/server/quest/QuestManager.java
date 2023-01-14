package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.instance.Instance;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Predicate;

/**
 * The QuestManager is used to register and retrieve Quests.
 */
public class QuestManager {
    private final Map<Integer, Quest> questsById;
    private final Map<Quest, Collection<QuestMarker>> questMarkers;

    @ApiStatus.Internal
    public QuestManager(Quest[] quests) {
        questsById = new HashMap<>();
        questMarkers = new HashMap<>();
        for (Quest quest : quests) {
            registerQuest(quest);
        }
    }

    private void registerQuest(Quest quest) {
        int id = quest.getId();
        if (questsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        questsById.put(id, quest);
    }

    /**
     * Returns the registered Quest with the given ID, or null if none
     * exists.
     */
    public Quest getQuest(int id) {
        return questsById.get(id);
    }

    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(questsById.values());
    }

    public QuestMarker createQuestMarker(Instance instance, Pos position,
                                         Quest quest, QuestMarkerIcon icon,
                                         Predicate<PlayerCharacter> shouldShow) {
        QuestMarker questMarker = new QuestMarker(instance, position, quest, icon, shouldShow);
        if (!questMarkers.containsKey(quest)) {
            questMarkers.put(quest, new ArrayList<>());
        }
        Collection<QuestMarker> markersForQuest = questMarkers.get(quest);
        markersForQuest.add(questMarker);
        return questMarker;
    }
}
