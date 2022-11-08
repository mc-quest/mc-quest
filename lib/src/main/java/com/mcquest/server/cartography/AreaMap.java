package com.mcquest.server.cartography;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.coordinate.Pos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AreaMap {
    private final int id;
    private final Pos origin;
    private final Image image;
    private final QuestMarker[] questMarkers;

    private AreaMap(Builder builder) {
        this.id = builder.id;
        origin = builder.origin;
        image = builder.image;
        questMarkers = builder.questMarkers.toArray(new QuestMarker[0]);
    }

    public int getId() {
        return id;
    }

    void render() {
        // TODO: figure out args
    }

    public static Builder builder(int id, Pos origin, Image image) {
        return new Builder(id, origin, image);
    }

    public static class Builder {
        private final int id;
        private final Pos origin;
        private final Image image;
        private final List<QuestMarker> questMarkers;

        private Builder(int id, Pos origin, Image image) {
            this.id = id;
            this.origin = origin;
            this.image = image;
            this.questMarkers = new ArrayList<>();
        }

        public Builder questMarker(Pos position, Function<PlayerCharacter, QuestMarkerIcon> iconFunc) {
            QuestMarker questMarker = new QuestMarker(position, iconFunc);
            questMarkers.add(questMarker);
            return this;
        }

        public AreaMap build() {
            return new AreaMap(this);
        }
    }
}
