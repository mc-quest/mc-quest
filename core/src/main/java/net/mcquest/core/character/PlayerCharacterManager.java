package net.mcquest.core.character;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.event.*;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.object.Object;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectProvider;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.ui.PlayerCharacterLogoutType;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class PlayerCharacterManager {
    private final Mmorpg mmorpg;
    private final Map<Player, PlayerCharacter> pcs;

    @ApiStatus.Internal
    public PlayerCharacterManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        pcs = new HashMap<>();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handlePlayerLogin);
        eventHandler.addListener(PlayerDisconnectEvent.class, this::handlePlayerDisconnect);
        eventHandler.addListener(ClickMenuLogoutEvent.class, this::handlePlayerCharacterMenuLogout);
        eventHandler.addListener(PlayerMoveEvent.class, this::handlePlayerMove);
        eventHandler.addListener(EntityDamageEvent.class, this::handleDamage);
        eventHandler.addListener(PlayerChatEvent.class, this::handleChat);
        SchedulerManager scheduler = mmorpg.getSchedulerManager();
        scheduler.buildTask(this::regeneratePlayerCharacters).repeat(TaskSchedule.seconds(1)).schedule();
        scheduler.buildTask(this::updateNameplates).repeat(TaskSchedule.nextTick()).schedule();
    }

    public Collection<PlayerCharacter> getPlayerCharacters() {
        return Collections.unmodifiableCollection(pcs.values());
    }

    public PlayerCharacter getPlayerCharacter(Player player) {
        return pcs.get(player);
    }

    public Collection<PlayerCharacter> getNearbyPlayerCharacters(Instance instance, Pos position, double radius) {
        ObjectManager objectManager = mmorpg.getObjectManager();

        Collection<PlayerCharacter> pcs = new ArrayList<>();

        for (Object object : objectManager.getNearbyObjects(instance, position, radius)) {
            if (object instanceof PlayerCharacter pc) {
                pcs.add(pc);
            }
        }

        return pcs;
    }

    @ApiStatus.Internal
    public void loginPlayerCharacter(Player player, int slot, PlayerCharacterData data) {
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();

        Instance instance;
        Pos position;

        if (data.instanceId() == null) {
            PlayerCharacterCreateEvent event = new PlayerCharacterCreateEvent();
            eventHandler.call(event);
            if (event.getInstance() == null) {
                throw new IllegalStateException("Must specify a spawning instance");
            }
            instance = event.getInstance();

        } else {
            instance = mmorpg.getInstanceManager().getInstance(data.instanceId())
        }

        Pos position = data.position();
        event.setSpawningInstance(instance);
        player.setRespawnPoint(position);
        player.setGameMode(GameMode.ADVENTURE);
        ObjectSpawner spawner = pcSpawner(instance, position, player, data);
        PlayerCharacter pc = (PlayerCharacter) mmorpg.getObjectManager().spawn(spawner);
        pcs.put(player, pc);
        eventHandler.call(new PlayerCharacterLoginEvent(pc));
    }

    private ObjectSpawner pcSpawner(Instance instance, Pos position,
                                    Player player, PlayerCharacterData data) {
        return ObjectSpawner.of(instance, position, pcProvider(player, data));
    }

    private ObjectProvider pcProvider(Player player, PlayerCharacterData data) {
        return (mmorpg, spawner) -> new PlayerCharacter(mmorpg, spawner, player, data);
    }

    private void handlePlayerDisconnect(PlayerDisconnectEvent event) {
        TODO
        Player player = event.getPlayer();
        PlayerCharacter pc = getPlayerCharacter(player);
        handlePlayerCharacterLogout(pc, PlayerCharacterLogoutType.DISCONNECT);
    }

    private void handlePlayerCharacterMenuLogout(ClickMenuLogoutEvent event) {
        TODO
        PlayerCharacter pc = event.getPlayerCharacter();
        handlePlayerCharacterLogout(pc, PlayerCharacterLogoutType.MENU_LOGOUT);
    }

    private void handlePlayerCharacterLogout(PlayerCharacter pc, PlayerCharacterLogoutType logoutType) {
        TODO
        logoutHandler.accept(pc, logoutType);
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        PlayerCharacterLogoutEvent event = new PlayerCharacterLogoutEvent(pc, logoutType);
        eventHandler.call(event);
        pc.remove();
        pcs.remove(pc.getEntity());
    }

    private void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = getPlayerCharacter(player);

        if (pc.isTeleporting()) {
            event.setCancelled(true);
            return;
        }

        if (pc.getCutscenePlayer().getPlayingCutscene() != null) {
            return;
        }

        PlayerCharacterMoveEvent pcMoveEvent = new PlayerCharacterMoveEvent(pc, event);
        pc.onMove().emit(pcMoveEvent);
        MinecraftServer.getGlobalEventHandler().call(pcMoveEvent);

        if (!pcMoveEvent.isCancelled()) {
            pc.updatePosition(pcMoveEvent.getNewPosition());
        }
    }

    private void handleDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setDamage(0.0f);
        }
    }

    private void handleChat(PlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerCharacter pc = getPlayerCharacter(player);

        Instance instance = pc.getInstance();
        Pos position = pc.getPosition();
        Collection<PlayerCharacter> nearbyPcs = getNearbyPlayerCharacters(instance, position, 50.0);

        String message = event.getMessage();

        for (PlayerCharacter nearbyPc : nearbyPcs) {
            pc.speak(nearbyPc, Component.text(message));
        }
    }

    private void regeneratePlayerCharacters() {
        for (PlayerCharacter pc : pcs.values()) {
            pc.heal(pc, pc.getHealthRegenRate());
            pc.addMana(pc.getManaRegenRate());
        }
    }

    private void updateNameplates() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        for (PlayerCharacter pc : pcs.values()) {
            Collection<Object> nearbyObjects = objectManager.getNearbyObjects(
                    pc.getInstance(),
                    pc.getPosition(),
                    ObjectManager.SPAWN_RADIUS
            );

            for (Object object : nearbyObjects) {
                if (object instanceof Character character) {
                    character.getNameplate().updateViewer(pc);
                }
            }
        }
    }
}
