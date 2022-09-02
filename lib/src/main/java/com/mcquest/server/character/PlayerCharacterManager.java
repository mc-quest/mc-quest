package com.mcquest.server.character;

import com.mcquest.server.Mmorpg;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class PlayerCharacterManager {
    private final Mmorpg mmorpg;
    private final Map<Player, PlayerCharacter> pcs;

    @ApiStatus.Internal
    public PlayerCharacterManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        pcs = new HashMap<>();
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerMoveEvent.class, this::synchronizePlayerPosition);
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::regeneratePlayerCharacters).repeat(TaskSchedule.seconds(1)).schedule();
    }

    @ApiStatus.Internal
    public void register(Player player, PlayerCharacter pc) {
        pcs.put(player, pc);
        mmorpg.getCharacterEntityManager().bind(player, pc);
    }

    @ApiStatus.Internal
    public void remove(PlayerCharacter pc) {
        Player player = pc.getPlayer();
        pcs.remove(player);
        mmorpg.getCharacterEntityManager().unbind(player);
    }

    public PlayerCharacter getPlayerCharacter(Player player) {
        return pcs.get(player);
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
