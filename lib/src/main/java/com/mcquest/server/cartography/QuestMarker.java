package com.mcquest.server.cartography;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.coordinate.Pos;

import java.util.function.Function;

public class QuestMarker {
    private final Pos position;
    private final Function<PlayerCharacter, QuestMarkerIcon> iconFunc;

    QuestMarker(Pos position, Function<PlayerCharacter, QuestMarkerIcon> iconFunc) {
        this.position = position;
        this.iconFunc = iconFunc;
    }
}
