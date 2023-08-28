package com.mcquest.server.constants;

import com.mcquest.core.util.JsonUtility;
import com.mcquest.server.Assets;
import net.minestom.server.entity.PlayerSkin;

public class Skins {
    public static PlayerSkin ADVENTURER_MALE = loadSkin("adventurer_male");
    public static PlayerSkin GUARD_MALE = loadSkin("guard_male");

    private static PlayerSkin loadSkin(String fileName) {
        String path = String.format("skins/%s.json", fileName);
        return JsonUtility.fromJson(Assets.asset(path).readJson(), PlayerSkin.class);
    }
}
