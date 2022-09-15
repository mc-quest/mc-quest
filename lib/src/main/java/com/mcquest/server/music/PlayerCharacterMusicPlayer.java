package com.mcquest.server.music;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class PlayerCharacterMusicPlayer {
    private final PlayerCharacter pc;
    private Song song;
    private Task[] playTasks;

    public PlayerCharacterMusicPlayer(PlayerCharacter pc) {
        this.pc = pc;
        this.song = null;
        this.playTasks = null;
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
        for (Task playTask : playTasks) {
            playTask.cancel();
        }
        playTasks = null;
    }

    private void playSong() {
        int noteCount = song.getNoteCount();
        double bpm = song.getBeatsPerMinute();
        playTasks = new Task[noteCount + 1];
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        for (int i = 0; i < noteCount; i++) {
            Note note = song.getNote(i);
            playTasks[i] = schedulerManager
                    .buildTask(new PlayNoteTask(pc, note))
                    .delay(beatsToDuration(note.getTime(), bpm))
                    .schedule();
        }
        playTasks[noteCount] = schedulerManager
                .buildTask(this::playSong)
                .delay(beatsToDuration(song.getDuration(), bpm))
                .schedule();
    }

    private static Duration beatsToDuration(double beats, double beatsPerMinute) {
        long millis = (long) (beats / beatsPerMinute * 60.0 * 1000.0);
        return Duration.ofMillis(millis);
    }

    private static class PlayNoteTask implements Runnable {
        private final PlayerCharacter pc;
        private final Note note;

        private PlayNoteTask(PlayerCharacter pc, Note note) {
            this.pc = pc;
            this.note = note;
        }

        @Override
        public void run() {
            pc.playSound(note.getSound());
        }
    }
}
