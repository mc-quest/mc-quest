package net.mcquest.core.persistence;

import java.util.UUID;

public interface PersistenceService {
    PlayerCharacterData[] fetch(UUID uuid);

    void store(UUID uuid, int characterSlot, PlayerCharacterData data);

    void delete(UUID uuid, int characterSlot);
}
