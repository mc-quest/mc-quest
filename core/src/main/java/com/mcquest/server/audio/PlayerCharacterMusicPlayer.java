package com.mcquest.server.audio;

import com.mcquest.server.character.PlayerCharacter;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

public class PlayerCharacterMusicPlayer {
    private final PlayerCharacter pc;
    private Song song;
    private Task repeatTask;

    public PlayerCharacterMusicPlayer(PlayerCharacter pc) {
        this.pc = pc;
        this.song = null;
        this.repeatTask = null;
    }

    public @Nullable Song getSong() {
        return song;
    }

    public void setSong(@Nullable Song song) {
        if (song == this.song) {
            return;
        }
        if (this.song != null) {
            stopSong();
        }
        this.song = song;
        if (song != null) {
            playSong();
        }
    }

    private void stopSong() {
        song.getAudioClip().stop(pc);
        repeatTask.cancel();
    }

    private void playSong() {
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        song.getAudioClip().play(pc, Sound.Source.MUSIC);
        repeatTask = scheduler.buildTask(this::playSong).delay(song.getDuration()).schedule();
    }
}
