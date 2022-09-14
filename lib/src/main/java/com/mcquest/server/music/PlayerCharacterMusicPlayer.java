package com.mcquest.server.music;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

public class PlayerCharacterMusicPlayer {
    private final PlayerCharacter pc;
    private Song song;
    private Task[] playToneTasks;

    public PlayerCharacterMusicPlayer(PlayerCharacter pc) {
        this.pc = pc;
        this.song = null;
        this.playToneTasks = null;
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
        for (Task playToneTask : playToneTasks) {
            playToneTask.cancel();
        }
        playToneTasks = null;
    }

    private void playSong() {
        int toneCount = song.getToneCount();
        playToneTasks = new Task[toneCount];
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        for (int i = 0; i < toneCount; i++) {
            Tone tone = song.getTone(i);
            playToneTasks[i] = schedulerManager
                    .buildTask(new PlayToneTask(pc, tone))
                    .delay(tone.getTime())
                    .schedule();
        }
        schedulerManager.buildTask(this::playSong).delay(song.getDuration()).schedule();
    }

    private static class PlayToneTask implements Runnable {
        private final PlayerCharacter pc;
        private final Tone tone;

        private PlayToneTask(PlayerCharacter pc, Tone tone) {
            this.pc = pc;
            this.tone = tone;
        }

        @Override
        public void run() {
            pc.playSound(tone.getSound());
        }
    }
}
