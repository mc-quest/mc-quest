package com.mcquest.core.achievement;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class AchievementManager {
    private final Map<Integer, Achievement> achievementsById;

    @ApiStatus.Internal
    public AchievementManager() {
        achievementsById = new HashMap<>();
    }

    public Achievement getAchievement(int id) {
        return achievementsById.get(id);
    }
}
