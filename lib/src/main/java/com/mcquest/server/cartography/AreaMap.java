package com.mcquest.server.cartography;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.coordinate.Pos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AreaMap {
    private final int id;
    private final Pos origin;
    private final Image image;
    private final List<QuestMarker> questMarkers;

    public AreaMap(int id, Pos origin, Image image) {
        this.id = id;
        this.origin = origin;
        this.image = image;
        this.questMarkers = new ArrayList<QuestMarker>();
    }

    public int getId() {
        return id;
    }

    public void addQuestMarker(Pos position, QuestMarkerIcon icon,
                               Predicate<PlayerCharacter> shouldShow) {
        QuestMarker questMarker = new QuestMarker(position, icon, shouldShow);
        questMarkers.add(questMarker);
    }

    void render() {
        // TODO: figure out args
    }

    private static class QuestMarker {
        private final Pos position;
        private final QuestMarkerIcon icon;
        private final Predicate<PlayerCharacter> shouldShow;

        private QuestMarker(Pos position, QuestMarkerIcon icon, Predicate<PlayerCharacter> shouldShow) {
            this.position = position;
            this.icon = icon;
            this.shouldShow = shouldShow;
        }
    }
}
