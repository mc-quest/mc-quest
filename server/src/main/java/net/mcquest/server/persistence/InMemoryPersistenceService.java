package net.mcquest.server.persistence;

import net.mcquest.core.persistence.PersistenceService;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.PlayerClasses;
import net.mcquest.server.constants.Zones;
import net.minestom.server.coordinate.Pos;

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
        dataByUuid.get(uuid)[0] = PlayerCharacterData.create(PlayerClasses.FIGHTER, Instances.ELADRADOR, new Pos(2000, 150, 2000), Zones.OAKSHIRE, Items.ADVENTURERS_SWORD);
        return dataByUuid.get(uuid);
    }

    @Override
    public void store(UUID uuid, int characterSlot, PlayerCharacterData data) {
        dataByUuid.get(uuid)[characterSlot] = data;
    }

    @Override
    public void delete(UUID uuid, int characterSlot) {
        dataByUuid.get(uuid)[characterSlot] = null;
    }
}
