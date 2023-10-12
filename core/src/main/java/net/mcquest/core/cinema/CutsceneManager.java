package net.mcquest.core.cinema;

import net.mcquest.core.character.PlayerCharacterManager;
import net.mcquest.core.Mmorpg;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

public class CutsceneManager {
    private final Mmorpg mmorpg;

    @ApiStatus.Internal
    public CutsceneManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tick)
                .repeat(TaskSchedule.nextTick())
                .schedule();
    }

    private void tick() {
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.getPlayerCharacters().forEach(pc ->
                pc.getCutscenePlayer().tick());
    }
}
