package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Items;
import com.mcquest.server.constants.PlayerClasses;
import com.mcquest.server.constants.Quests;
import com.mcquest.server.persistence.PlayerCharacterData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;

import java.time.Duration;

public class Lobby {
    private static final Pos SPAWN_POSITION = new Pos(0.0, 70.0, 0.0);

    public static void createLobby() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, Lobby::handlePlayerLogin);
        eventHandler.addListener(PlayerDisconnectEvent.class, Lobby::handlePlayerDisconnect);
    }

    private static void handlePlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        event.setSpawningInstance(Instances.ELADRADOR);
        player.setRespawnPoint(new Pos(0, 70, 0));
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            PlayerCharacterData data = PlayerCharacterData.create(PlayerClasses.FIGHTER,
                    Instances.ELADRADOR, SPAWN_POSITION, Items.ADVENTURERS_BLADE);
            PlayerCharacter pc = PlayerCharacter.register(player, data);
            Quests.TUTORIAL.start(pc);
            // TODO: Add menu item.
            pc.giveItem(Items.POTION_OF_MINOR_HEALING);
        }).delay(Duration.ofSeconds(3)).schedule();
    }

    private static void handlePlayerDisconnect(PlayerDisconnectEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        if (pc != null) {
            // TODO: save data
            pc.remove();
        }
    }
}
