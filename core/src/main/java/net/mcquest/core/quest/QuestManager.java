package net.mcquest.core.quest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The QuestManager is used to register and retrieve Quests.
 */
public class QuestManager {
    private final Map<String, Quest> questsById;
    private final Multimap<Quest, QuestMarker> questMarkers;

    @ApiStatus.Internal
    public QuestManager(Quest[] quests) {
        questsById = new HashMap<>();
        questMarkers = ArrayListMultimap.create();
        for (Quest quest : quests) {
            registerQuest(quest);
        }
    }

    private void registerQuest(Quest quest) {
        String id = quest.getId();
        if (questsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        questsById.put(id, quest);
    }

    /**
     * Returns the registered Quest with the given ID, or null if none
     * exists.
     */
    public Quest getQuest(String id) {
        return questsById.get(id);
    }

    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(questsById.values());
    }

    public QuestMarker createQuestMarker(Instance instance, Pos position,
                                         Quest quest, QuestMarkerIcon icon,
                                         Predicate<PlayerCharacter> shouldShow) {
        QuestMarker questMarker = new QuestMarker(instance, position, quest, icon, shouldShow);
        questMarkers.put(quest, questMarker);
        return questMarker;
    }
}
