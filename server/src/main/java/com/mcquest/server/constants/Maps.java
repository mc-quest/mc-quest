package com.mcquest.server.constants;

import com.mcquest.server.cartography.AreaMap;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.coordinate.Pos;

public class Maps {
    public static final AreaMap MELCHER = new AreaMap(1, new Pos(0, 0, 0),
            ResourceUtility.streamSupplier("maps/MelcherTavernBasement.png"));

    public static AreaMap[] all() {
        return new AreaMap[]{
                MELCHER
        };
    }
}
