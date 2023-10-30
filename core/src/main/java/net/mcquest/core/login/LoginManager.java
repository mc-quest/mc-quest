package net.mcquest.core.login;

import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.event.PlayerCharacterCreateEvent;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.playerclass.PlayerClass;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.scoreboard.Sidebar;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class LoginManager {
    private static final Pos SPAWN_POSITION = new Pos(0, 1, 0);

    private final Mmorpg mmorpg;
    private final Instance loginInstance;
    private final Set<Player> loggingInPlayers;

    public LoginManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        loginInstance = createLoginInstance();
        loggingInPlayers = new HashSet<>();

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handleConnect);
        eventHandler.addListener(PlayerSpawnEvent.class, this::handleSpawn);

        loginInstance.eventNode().addListener(InventoryCloseEvent.class, this::handleInventoryClose);
        loginInstance.eventNode().addListener(PlayerMoveEvent.class, this::handleMove);
        loginInstance.eventNode().addListener(PlayerDisconnectEvent.class, this::handleDisconnect);
    }

    @ApiStatus.Internal
    public void sendToLogin(Player player) {
        player.setInstance(loginInstance, SPAWN_POSITION);
        player.getInventory().clear();
        new Sidebar(Component.empty()).addViewer(player);
        player.setHealth(20.0f);
        player.setFood(20);
        player.stopSound(SoundStop.all());
    }

    void openCharacterSelectMenu(Player player) {
        PlayerCharacterData[] data = mmorpg.getPersistenceService().fetch(player.getUuid());
        player.openInventory(Menus.characterSelectMenu(data, mmorpg));
    }

    void createCharacter(Player player, int characterSlot, PlayerClass playerClass) {
        PlayerCharacterCreateEvent event = new PlayerCharacterCreateEvent(playerClass);
        mmorpg.getGlobalEventHandler().call(event);

        PlayerCharacterCreateEvent.Result characterCreateResult = event.getResult();
        if (characterCreateResult == null) {
            throw new RuntimeException("You need to specify a character create result");
        }

        PlayerCharacterData data = PlayerCharacterData.create(playerClass, characterCreateResult);
        mmorpg.getPersistenceService().store(
                player.getUuid(),
                characterSlot,
                data
        );

        openCharacterSelectMenu(player);
    }

    void deleteCharacter(Player player, int characterSlot) {
        mmorpg.getPersistenceService().delete(player.getUuid(), characterSlot);
        openCharacterSelectMenu(player);
    }

    void loginPlayerCharacter(Player player, int characterSlot, PlayerCharacterData data) {
        loggingInPlayers.remove(player);
        mmorpg.getPlayerCharacterManager().loginPlayerCharacter(player, characterSlot, data);
    }

    private Instance createLoginInstance() {
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer();

        instance.setBlock(new Vec(0, 0, 0), Block.BARRIER);
        instance.setTimeRate(0);
        instance.setTime(18000);

        return instance;
    }

    private void handleConnect(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        player.setRespawnPoint(SPAWN_POSITION);
        event.setSpawningInstance(loginInstance);
    }

    private void handleSpawn(PlayerSpawnEvent event) {
        if (event.getSpawnInstance() != loginInstance) {
            return;
        }

        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        loggingInPlayers.add(player);
        player.setInvisible(true);
        player.setResourcePack(mmorpg.getResourcePackManager().getPlayerResourcePack());
        openCharacterSelectMenu(player);
    }

    private void handleInventoryClose(InventoryCloseEvent event) {
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            Player player = event.getPlayer();

            if (loggingInPlayers.contains(player)) {
                openCharacterSelectMenu(player);
            }
        }).delay(Duration.ofSeconds(3)).schedule();
    }

    private void handleMove(PlayerMoveEvent event) {
        event.setCancelled(true);
    }

    private void handleDisconnect(PlayerDisconnectEvent event) {
        loggingInPlayers.remove(event.getPlayer());
    }
}
