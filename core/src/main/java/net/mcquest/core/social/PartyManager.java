package net.mcquest.core.social;

import net.mcquest.core.Mmorpg;
import net.minestom.server.event.GlobalEventHandler;
import org.jetbrains.annotations.ApiStatus;

public class PartyManager {
    private final Mmorpg mmorpg;

    @ApiStatus.Internal
    public PartyManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        // TODO
    }
}
