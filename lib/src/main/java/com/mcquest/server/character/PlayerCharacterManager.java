package com.mcquest.server.character;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.persistence.PlayerCharacterData;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

public class PlayerCharacterManager {
    private Mmorpg mmorpg;
    private final Map<Player, PlayerCharacter> pcs;
    private Function<Player, PlayerCharacterData> dataProvider;

    @ApiStatus.Internal
    public PlayerCharacterManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        pcs = new HashMap<>();
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handlePlayerLogin);
        eventHandler.addListener(PlayerMoveEvent.class, this::synchronizePlayerPosition);
        SchedulerManager scheduler = mmorpg.getSchedulerManager();
        scheduler.buildTask(this::regeneratePlayerCharacters).repeat(TaskSchedule.seconds(1)).schedule();
    }

    public PlayerCharacter getPlayerCharacter(Player player) {
        return pcs.get(player);
    }

    public void setDataProvider(Function<Player, PlayerCharacterData> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public Collection<PlayerCharacter> getNearbyPlayerCharacters(Instance instance, Pos position, double range) {
        List<Player> result = new ArrayList<>();
        instance.getEntityTracker().nearbyEntities(position, range,
                EntityTracker.Target.PLAYERS, result::add);
        return result.stream().map(player -> getPlayerCharacter(player)).toList();
    }

    private void handlePlayerLogin(PlayerLoginEvent event) {
        if (dataProvider == null) {
            throw new IllegalStateException("You need to specify a player character data provider");
        }
        Player player = event.getPlayer();
        PlayerCharacterData data = dataProvider.apply(player);
        Instance instance = mmorpg.getInstanceManager().getInstance(data.getInstance());
        event.setSpawningInstance(instance);
        player.setRespawnPoint(data.getPosition());
        player.setGameMode(GameMode.ADVENTURE);
        player.setResourcePack(mmorpg.getResourceManager().getResourcePack());
        PlayerCharacter pc = new PlayerCharacter(mmorpg, player, data);
        pcs.put(player, pc);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(player, pc);
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.call(new PlayerCharacterLoginEvent(pc));
    }

    @ApiStatus.Internal
    public void remove(PlayerCharacter pc) {
        Player player = pc.getPlayer();
        pcs.remove(player);
        mmorpg.getCharacterEntityManager().unbind(player);
    }

    private void synchronizePlayerPosition(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = getPlayerCharacter(player);
        if (pc != null) {
            pc.setPosition(event.getNewPosition());
        }
    }

    private void regeneratePlayerCharacters() {
        for (PlayerCharacter pc : pcs.values()) {
            pc.heal(pc, pc.getHealthRegenRate());
            pc.addMana(pc.getManaRegenRate());
        }
    }
}
