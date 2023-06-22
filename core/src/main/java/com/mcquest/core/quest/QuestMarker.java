package com.mcquest.core.quest;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;

import java.util.function.Predicate;

public class QuestMarker {
    private final Instance instance;
    private final Pos position;
    private final Quest quest;
    private final QuestMarkerIcon icon;
    private final Predicate<PlayerCharacter> shouldShow;

    QuestMarker(Instance instance, Pos position, Quest quest,
                QuestMarkerIcon icon, Predicate<PlayerCharacter> shouldShow) {
        this.instance = instance;
        this.position = position;
        this.quest = quest;
        this.icon = icon;
        this.shouldShow = shouldShow;
    }

    public Instance getInstance() {
        return instance;
    }

    public Pos getPosition() {
        return position;
    }

    public Quest getQuest() {
        return quest;
    }

    public QuestMarkerIcon getIcon() {
        return icon;
    }

    public boolean shouldShow(PlayerCharacter pc) {
        return shouldShow.test(pc);
    }
}
