package com.mcquest.core.audio;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.persistence.PlayerCharacterData;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

public class MusicPlayer {
    private final PlayerCharacter pc;
    private Song song;
    private Task repeatTask;

    public MusicPlayer(PlayerCharacter pc, PlayerCharacterData data,
                       AudioManager audioManager) {
        this.pc = pc;
        Integer songId = data.getSongId();
        if (songId == null) {
            song = null;
            repeatTask = null;
        } else {
            song = audioManager.getSong(songId);
            playSong();
        }
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
        repeatTask = scheduler.buildTask(this::playSong)
                .delay(song.getAudioClip().getDuration())
                .schedule();
    }
}
