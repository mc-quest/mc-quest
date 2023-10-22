package net.mcquest.core.cartography;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.character.PlayerCharacterManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

public class MapManager {
    private final Mmorpg mmorpg;
    private final java.util.Map<String, Map> mapsById;

    @ApiStatus.Internal
    public MapManager(Mmorpg mmorpg, Map[] maps) {
        this.mmorpg = mmorpg;

        mapsById = new HashMap<>();
        for (Map map : maps) {
            registerMap(map);
        }

        mmorpg.getSchedulerManager().buildTask(this::renderMaps)
                .repeat(TaskSchedule.nextTick()).schedule();
    }

    private void registerMap(Map map) {
        String id = map.getId();
        if (mapsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        mapsById.put(id, map);
    }

    public Map getMap(String id) {
        return mapsById.get(id);
    }

    private void renderMaps() {
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        for (PlayerCharacter pc : pcManager.getPlayerCharacters()) {
            MapViewer mapViewer = pc.getMapViewer();
            if (mapViewer.isOpen()) {
                mapViewer.render();
            }
        }
    }
}
