package net.mcquest.server.constants;

import net.mcquest.server.Assets;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.cartography.Map;
import net.minestom.server.coordinate.Pos;

public class Maps {
    public static final Map ELADRADOR = Map.of(
            "eladrador",
            new Pos(1728, 0, 2448),
            image("eladrador")
    );

    public static Map[] all() {
        return new Map[]{
                ELADRADOR
        };
    }

    private static Asset image(String name) {
        String path = "maps/" + name + ".png";
        return Assets.asset(path);
    }
}
