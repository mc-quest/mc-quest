package com.mcquest.server.constants;

import com.mcquest.server.Assets;
import com.mcquest.server.asset.Asset;
import com.mcquest.server.cartography.AreaMap;
import net.minestom.server.coordinate.Pos;

public class Maps {
    public static final AreaMap MELCHER = new AreaMap(
            1, new Pos(0, 0, 0), image("MelcherTavernBasement"));

    public static AreaMap[] all() {
        return new AreaMap[]{
                MELCHER
        };
    }

    private static Asset image(String name) {
        String path = "maps/" + name + ".png";
        return Assets.asset(path);
    }
}
