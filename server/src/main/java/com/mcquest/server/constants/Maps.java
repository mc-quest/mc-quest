package com.mcquest.server.constants;

import com.mcquest.server.cartography.AreaMap;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.coordinate.Pos;

public class Maps {
    public static final AreaMap MELCHER = new AreaMap(1, Pos.ZERO,
            ResourceUtility.readImage("maps/Melcher.png"));
}
