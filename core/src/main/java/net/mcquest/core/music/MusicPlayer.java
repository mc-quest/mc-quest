package net.mcquest.core.music;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

public class MusicPlayer {
    private final PlayerCharacter pc;
    private Song song;
    private Task repeatTask;

    public MusicPlayer(PlayerCharacter pc, PlayerCharacterData data,
                       MusicManager musicManager) {
        this.pc = pc;
        String songId = data.songId();
        if (songId == null) {
            song = null;
            repeatTask = null;
        } else {
            song = musicManager.getSong(songId);
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
        pc.stopSound(SoundStop.named(song.getKey()));
        repeatTask.cancel();
    }

    private void playSong() {
        Sound sound = Sound.sound(song.getKey(), Sound.Source.MUSIC, 1f, 1f);
        pc.playSound(sound);

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        repeatTask = scheduler.buildTask(this::playSong)
                .delay(song.getDuration())
                .schedule();
    }
}
