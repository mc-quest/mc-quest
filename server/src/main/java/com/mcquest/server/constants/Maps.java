package com.mcquest.server.constants;

import com.mcquest.server.cartography.AreaMap;
import com.mcquest.server.cartography.QuestMarkerIcon;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.coordinate.Pos;

public class Maps {
    public static final AreaMap MELCHER = AreaMap
            .builder(1, Pos.ZERO, ResourceUtility.readImage("maps/Melcher.png"))
            .questMarker(Pos.ZERO, pc -> QuestMarkerIcon.HIDDEN)
            .build();
}
