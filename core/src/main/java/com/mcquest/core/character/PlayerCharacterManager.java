package com.mcquest.core.character;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.event.ClickMenuLogoutEvent;
import com.mcquest.core.event.PlayerCharacterLoginEvent;
import com.mcquest.core.event.PlayerCharacterLogoutEvent;
import com.mcquest.core.event.PlayerCharacterMoveEvent;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.ObjectManager;
import com.mcquest.core.persistence.PlayerCharacterData;
import com.mcquest.core.resourcepack.ResourcePackManager;
import com.mcquest.core.ui.PlayerCharacterLogoutType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.resourcepack.ResourcePack;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerCharacterManager {
    private final Mmorpg mmorpg;
    private final Function<Player, PlayerCharacterData> dataProvider;
    private final BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> logoutHandler;
    private final Map<Player, PlayerCharacter> pcs;

    @ApiStatus.Internal
    public PlayerCharacterManager(Mmorpg mmorpg, Function<Player, PlayerCharacterData> dataProvider,
                                  BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> logoutHandler) {
        this.mmorpg = mmorpg;
        this.dataProvider = dataProvider;
        this.logoutHandler = logoutHandler;
        pcs = new HashMap<>();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handlePlayerLogin);
        eventHandler.addListener(PlayerDisconnectEvent.class, this::handlePlayerDisconnect);
        eventHandler.addListener(ClickMenuLogoutEvent.class, this::handlePlayerCharacterMenuLogout);
        eventHandler.addListener(PlayerMoveEvent.class, this::handlePlayerMove);
        eventHandler.addListener(EntityDamageEvent.class, this::handleDamage);
        SchedulerManager scheduler = mmorpg.getSchedulerManager();
        scheduler.buildTask(this::regeneratePlayerCharacters).repeat(TaskSchedule.seconds(1)).schedule();
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

    private void handlePlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerCharacterData data = dataProvider.apply(player);
        Instance instance = mmorpg.getInstanceManager().getInstance(data.getInstanceId());
        event.setSpawningInstance(instance);
        player.setRespawnPoint(data.getPosition());
        player.setGameMode(GameMode.ADVENTURE);
        ResourcePackManager resourcePackManager = mmorpg.getResourcePackManager();
        ResourcePack resourcePack = ResourcePack.forced(resourcePackManager.getResourcePackUrl(),
                resourcePackManager.getResourcePackHash());
        player.setResourcePack(resourcePack);
        PlayerCharacter pc = new PlayerCharacter(mmorpg, player, data);
        pcs.put(player, pc);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(player, pc);
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.call(new PlayerCharacterLoginEvent(pc));
    }

    private void handlePlayerDisconnect(PlayerDisconnectEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = getPlayerCharacter(player);
        handlePlayerCharacterLogout(pc, PlayerCharacterLogoutType.DISCONNECT);
    }

    private void handlePlayerCharacterMenuLogout(ClickMenuLogoutEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        handlePlayerCharacterLogout(pc, PlayerCharacterLogoutType.MENU_LOGOUT);
    }

    private void handlePlayerCharacterLogout(PlayerCharacter pc, PlayerCharacterLogoutType logoutType) {
        logoutHandler.accept(pc, logoutType);
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        PlayerCharacterLogoutEvent event = new PlayerCharacterLogoutEvent(pc, logoutType);
        eventHandler.call(event);
        pc.remove();
        pcs.remove(pc.getPlayer());
    }

    private void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = getPlayerCharacter(player);

        if (pc.getCutscenePlayer().getPlayingCutscene() != null) {
            return;
        }

        PlayerCharacterMoveEvent pcMoveEvent = new PlayerCharacterMoveEvent(pc, event);
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

    private void regeneratePlayerCharacters() {
        for (PlayerCharacter pc : pcs.values()) {
            pc.heal(pc, pc.getHealthRegenRate());
            pc.addMana(pc.getManaRegenRate());
        }
    }
}
