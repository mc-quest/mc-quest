package com.mcquest.server.character;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.persistence.PlayerCharacterData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlayerCharacterManager {
    private Mmorpg mmorpg;
    private final Map<Player, PlayerCharacter> pcs;
    private Function<Player, PlayerCharacterData> dataProvider;

    public PlayerCharacterManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        pcs = new HashMap<>();
        dataProvider = null;
    }

    public void register(PlayerCharacter pc) {
        pcs.put(pc.getPlayer(), pc);
    }

    public PlayerCharacter getPlayerCharacter(Player player) {
        return pcs.get(player);
    }

    public void setDataProvider(Function<Player, PlayerCharacterData> dataProvider) {
        this.dataProvider = dataProvider;
    }

    @ApiStatus.Internal
    public void registerEvents() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handlePlayerLogin);
    }

    private void handlePlayerLogin(PlayerLoginEvent event) {
        if (dataProvider == null) {
            throw new IllegalStateException("You need to specify a player character data provider");
        }
        Player player = event.getPlayer();
        PlayerCharacterData data = dataProvider.apply(player);
        Instance instance = null; // TODO: mmorpg.getInstanceManager().getInstance(data.getInstance());
        event.setSpawningInstance(instance);
    }
}
