package net.mcquest.server.constants;

import net.mcquest.core.util.JsonUtility;
import net.mcquest.server.Assets;
import net.minestom.server.entity.PlayerSkin;

public class Skins {
    public static PlayerSkin ADVENTURER_MALE = loadSkin("adventurer_male");
    public static PlayerSkin GUARD_MALE = loadSkin("guard_male");
    public static PlayerSkin RED_KNIGHT = loadSkin("red_knight");
    public static PlayerSkin VILLAGER_MALE = loadSkin("villager_male");
    public static PlayerSkin VILLAGER_FEMALE = loadSkin("villager_female");
    public static PlayerSkin ADVENTURER_MALE = loadSkin("adventurer_male");
    public static PlayerSkin ADVENTURER_FEMALE = loadSkin("adventurer_female");

    private static PlayerSkin loadSkin(String fileName) {
        String path = String.format("skins/%s.json", fileName);
        return JsonUtility.fromJson(Assets.asset(path).readJson(), PlayerSkin.class);
    }
}
