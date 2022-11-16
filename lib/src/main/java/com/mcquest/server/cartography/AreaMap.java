package com.mcquest.server.cartography;

import com.mcquest.server.quest.QuestMarker;
import net.minestom.server.coordinate.Pos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class AreaMap {
    private final int id;
    private final Pos origin;
    private final Image image;
    private final Collection<QuestMarker> questMarkers;

    public AreaMap(int id, Pos origin, Image image) {
        this.id = id;
        this.origin = origin;
        this.image = image;
        this.questMarkers = new ArrayList<QuestMarker>();
    }

    public int getId() {
        return id;
    }

    public void addQuestMarker(QuestMarker questMarker) {
        questMarkers.add(questMarker);
    }

    void render() {
        // TODO: figure out args
    }
}
