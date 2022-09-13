package com.mcquest.server.music;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

public class PlayerCharacterMusicPlayer {
    private final PlayerCharacter pc;
    private Song song;
    private Task[] playNoteTasks;

    public PlayerCharacterMusicPlayer(PlayerCharacter pc) {
        this.pc = pc;
        this.song = null; // TODO
        this.playNoteTasks = null;
    }

    public Song getSong() {
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
        for (Task playNoteTask : playNoteTasks) {
            playNoteTask.cancel();
        }
    }

    private void playSong() {
        playNoteTasks = new Task[song.getNoteCount()];
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        for (int i = 0; i < song.getNoteCount(); i++) {
            Note note = song.getNote(i);
            playNoteTasks[i] = schedulerManager
                    .buildTask(new PlayNoteTask(pc, note))
                    .delay(note.getTime())
                    .schedule();
        }
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
