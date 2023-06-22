package com.mcquest.core.cartography;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.PlayerCharacterMoveEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class MapManager {
    private final Map<Integer, AreaMap> mapsById;

    @ApiStatus.Internal
    public MapManager(AreaMap[] maps) {
        mapsById = new HashMap<>();
        for (AreaMap map : maps) {
            registerMap(map);
        }
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterMoveEvent.class, this::handlePlayerMove);
    }

    private void registerMap(AreaMap map) {
        int id = map.getId();
        if (mapsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        mapsById.put(id, map);
    }

    private void handlePlayerMove(PlayerCharacterMoveEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        PlayerCharacterMapManager pcMapManager = pc.getMapManager();
        if (pcMapManager.isMapOpen()) {
            pc.getMapManager().getMap().render(pc);
        }
    }

    public AreaMap getMap(int id) {
        return mapsById.get(id);
    }
}
