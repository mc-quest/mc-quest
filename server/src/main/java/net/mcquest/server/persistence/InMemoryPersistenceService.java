package net.mcquest.server.persistence;

import net.mcquest.core.persistence.PersistenceService;
import net.mcquest.core.persistence.PlayerCharacterData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryPersistenceService implements PersistenceService {
    private final Map<UUID, PlayerCharacterData[]> dataByUuid;

    public InMemoryPersistenceService() {
        dataByUuid = new HashMap<>();
    }

    @Override
    public PlayerCharacterData[] fetch(UUID uuid) {
        if (!dataByUuid.containsKey(uuid)) {
            dataByUuid.put(uuid, new PlayerCharacterData[4]);
        }
        return dataByUuid.get(uuid);
    }

    @Override
    public void store(UUID uuid, int characterSlot, PlayerCharacterData data) {
        dataByUuid.get(uuid)[characterSlot] = data;
    }
}
